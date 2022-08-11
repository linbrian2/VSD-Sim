<template>
  <div>
    <!-- Left map panel -->
    <SelectionPanel name="zoneBarWidth">
      <v-combobox
        class="mx-2"
        dense
        hide-details
        single-line
        item-text="text"
        item-value="value"
        :items="zoneItems"
        :value="valueSelected"
        @input="valueSelectHandler"
        label="CHOOSE A ZONE TO SHOW"
      />
      <MapSelect ref="mapSelect" :markers="markers" :icons="icons" @click="onMapClick" />
    </SelectionPanel>

    <TitleBar :title="title" :loading="loading" :refresh="refreshData" :showMap="false" />

    <v-container fluid>
      <v-card tile class="mb-8" elevation="24" v-if="delayComplete">
        <TrafficResponsiveChart :data="chartData" :height="defaultHeight" />
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import { RouterNames } from '@/utils/constants/router';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapSelect from '@/components/modules/traffic/map/MapSelect';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import TrafficResponsiveChart from '@/components/modules/traffic/chart/TrafficResponsiveChart';

export default {
  components: {
    SelectionPanel,
    MapSelect,
    TitleBar,
    TrafficResponsiveChart
  },

  data: () => ({
    delayComplete: false,
    loading: false,
    defaultHeight: 650,
    title: RouterNames.TRAFFIC_RESPONSIVE_DATA,
    chartData: {},
    currentZoneId: null,
    valueSelected: null,
    zoneList: null,
    icons: [
      {
        path: 0,
        scale: 10.0,
        fillColor: '#05FF00',
        fillOpacity: 0.8,
        strokeWeight: 0.4
      },
      {
        path: 0,
        scale: 10.0,
        fillColor: '#FF7F00',
        fillOpacity: 0.8,
        strokeWeight: 0.4
      }
    ]
  }),

  computed: {
    markers() {
      if (!this.zoneList) return [];

      const devices = [];
      for (const zone of Object.values(this.zoneList)) {
        devices.push(...zone.devices);
      }
      return devices;
    },

    zoneItems() {
      return !this.zoneList
        ? []
        : Object.entries(this.zoneList).map(entry => ({
            text: entry[0] + ': ' + (entry[1].title ? entry[1].title : 'No Description'),
            value: entry[0]
          }));
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['activeMarker'])
  },

  created() {
    this.$store.commit('SET_CURRENT_DATE', Utils.yesterday());
  },

  mounted() {
    if (!this.zoneList) {
      this.fetchZoneList();
    }
    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.delayComplete = true;
      this.showDataIfEmpty();
    }, 500);
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  methods: {
    showDataIfEmpty() {
      this.$bus.$emit('CENTER_MAP');
      this.$bus.$emit('SELECT_FIRST');
    },

    onMapClick(marker) {
      const zoneId = marker.zoneId;
      this.currentZoneId = zoneId;
      this.valueSelected = this.zoneItems.find(item => item.value == zoneId).text;
      this.selectDetectorsByZoneId(zoneId);
      this.fetchData(zoneId);
    },

    valueSelectHandler(value) {
      const zoneId = value.value;
      this.selectDetectorsByZoneId(zoneId);
      this.fetchData(zoneId);
    },

    selectDetectorsByZoneId(zoneId) {
      const ids = this.zoneList[zoneId].devices.map(d => d.id);
      this.$refs.mapSelect.selectByIds(ids);
    },

    refreshData() {
      if (this.currentZoneId) {
        this.fetchData(this.currentZoneId);
      }
    },

    fetchData(zoneId) {
      let start = this.currentDate.getTime();
      this.fetchTrafficResponsiveData(zoneId, start);
    },

    async fetchZoneList() {
      try {
        const response = await Api.fetchZoneList();
        this.zoneList = response.data;
      } catch (error) {
        this.showWarningMessage(error);
      }
    },

    async fetchTrafficResponsiveData(zoneId, startDate) {
      this.loading = true;
      try {
        const response = await Api.fetchTrafficResponsiveData(zoneId, startDate);
        const data = this.parseResponse(response, false);
        if (data != null) {
          this.chartData = this.composeData(zoneId, data);
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    parseResponse(response, showWarning) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        if (showWarning) {
          this.showWarningMessage(response.data.message);
        }
      }
      return result;
    },

    showWarningMessage(message) {
      this.$store.dispatch('setSystemStatus', { text: message, color: 'warning', timeout: 1500 });
    },

    composeData(zoneId, resData) {
      const title = `Traffic Response Plot (${zoneId})`;
      const xAxis = 'Time of day';
      const yAxis = 'V + O (%)';

      const patterns = resData.patterns.map(item => [item[0], item[1]]);

      let data = [];
      data.push({ name: 'NB V+O', color: '#ED561B', data: resData.NBVO, tracking: true });
      data.push({ name: 'SB V+O', color: '#50B432', data: resData.SBVO, tracking: true });
      data.push({ name: 'Cycle Length', color: '#fed976', data: patterns, tracking: false });

      // Compose a level to comdate the auto min-max scaling of highcharts
      if (resData.levels && resData.levels.length > 0 && resData.patterns.length > 0) {
        const t0 = resData.patterns[0][0];
        const t1 = resData.patterns[resData.patterns.length - 1][0];
        const ll = resData.levels[resData.levels.length - 1][1];
        const levels = [
          [t0, ll],
          [t1, ll]
        ];
        data.push({ name: 'Max Level', color: '#DEDEDE', data: levels, tracking: false });
      }

      const colors = ['#023858', '#045a8d', '#2b8cbe', '#74a9cf', '#a6bddb', '#d0d1e6', '#f1eef6'];

      let bands = null;
      if (resData.levels) {
        bands = resData.levels.map((b, idx) => ({
          from: b[0],
          to: b[1],
          color: colors[0 + idx]
        }));
      }

      return { data, xAxis, yAxis, title, ymin: 0, ymax: 100, bands: bands, patterns: resData.patterns };
    }
  }
};
</script>

<style lang="scss" scoped>
.basic-chart {
  height: 500px;
}
</style>

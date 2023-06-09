<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
    <MapSelectionPanel
      ref="mapSelectPanel"
      :markers="markers"
      :items="items"
      name="anomalySideBarWidth"
      :onMarkerClick="markerClicked"
    />
    <TitleBar title="Traffic Anomaly Map" :loading="loading" :refresh="refreshData" />

    <v-container>
      <v-card class="mb-4" v-if="isValid(anomaly1)" @click="cardClicked(1)">
        <DrilldownHeatMapChart
          :data="anomaly1"
          :height="mapHeight(anomaly1)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-4" v-if="isValid(anomaly2)" @click="cardClicked(2)">
        <DrilldownHeatMapChart
          :data="anomaly2"
          :height="mapHeight(anomaly2)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-4" v-if="isValid(anomaly3)" @click="cardClicked(3)">
        <DrilldownHeatMapChart
          :data="anomaly3"
          :height="mapHeight(anomaly3)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-4" v-if="isValid(anomaly4)" @click="cardClicked(4)">
        <DrilldownHeatMapChart
          :data="anomaly4"
          :height="mapHeight(anomaly4)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-4" v-if="isValid(anomaly5)" @click="cardClicked(5)">
        <DrilldownHeatMapChart
          :data="anomaly5"
          :height="mapHeight(anomaly5)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-4" v-if="isValid(anomaly6)" @click="cardClicked(6)">
        <DrilldownHeatMapChart
          :data="anomaly6"
          :height="mapHeight(anomaly6)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>
    </v-container>

    <!-- Popup Dialogs -->
    <AnomalyBaselineDialog v-model="showDialog" ref="baselineDialog" />
  </div>

  <div class="mobile" v-else>
    <!-- Container -->
    <v-container>
      <v-card class="mb-2 px-7">
        <v-row>
          <v-col cols="12" class="pb-1 grid-center">Status</v-col>
          <v-col v-for="i in status" :key="i.id" cols="2" class="pt-0 px-1">
            <v-card :color="i.color" class="grid-center">
              <h5>{{ i.name }}</h5>
            </v-card>
          </v-col>
        </v-row>
      </v-card>

      <v-card class="mb-2" v-if="isValid(anomaly1)">
        <DrilldownHeatMapChart
          :data="anomaly1"
          :height="mapHeight(anomaly1)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-2" v-if="isValid(anomaly2)">
        <DrilldownHeatMapChart
          :data="anomaly2"
          :height="mapHeight(anomaly2)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-2" v-if="isValid(anomaly3)">
        <DrilldownHeatMapChart
          :data="anomaly3"
          :height="mapHeight(anomaly3)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-2" v-if="isValid(anomaly4)">
        <DrilldownHeatMapChart
          :data="anomaly4"
          :height="mapHeight(anomaly4)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-2" v-if="isValid(anomaly5)">
        <DrilldownHeatMapChart
          :data="anomaly5"
          :height="mapHeight(anomaly5)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>

      <v-card class="mb-2" v-if="isValid(anomaly6)">
        <DrilldownHeatMapChart
          :data="anomaly6"
          :height="mapHeight(anomaly6)"
          :deviceClicked="deviceClicked"
          @baseline="showBaselineDialog"
        />
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import { mapState, mapActions } from 'vuex';
import MapSelectionPanel from '@/components/modules/traffic/map/MapSelectionPanel';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import DrilldownHeatMapChart from '@/components/modules/traffic/chart/DrilldownHeatMapChart';
import AnomalyBaselineDialog from '@/components/modules/traffic/common/AnomalyBaselineDialog';

export default {
  components: {
    MapSelectionPanel,
    TitleBar,
    DrilldownHeatMapChart,
    AnomalyBaselineDialog
  },
  data: () => ({
    loading: false,
    showDialog: false,
    height: 650,
    anomalyDeviceNames: null,
    anomaly1: {},
    anomaly2: {},
    anomaly3: {},
    anomaly4: {},
    anomaly5: {},
    anomaly6: {},
    status: [
      { from: 0, to: 0, color: '#196f3d', name: '0' },
      { from: 1, to: 1, color: '#B71C1C', name: '1' },
      { from: 3, to: 3, color: '#E57373', name: '3' },
      { from: 4, to: 4, color: '#FF9800', name: '4' },
      { from: 5, to: 5, color: '#FFCC00', name: '5' },
      { from: 6, to: 6, color: '#FDD835', name: '6' }
    ]
  }),
  computed: {
    markers() {
      return this.anomalyDevices;
    },

    items() {
      let names = [];
      this.anomalyDevices.forEach(location => {
        names.push(location.name.trimRight());
      });
      return names;
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['showPanel', 'activeMarker', 'anomalyDevices'])
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', false);
  },

  async mounted() {
    if (this.anomalyDevices.length === 0) {
      await this.fetchAnomalyDevices();
    }
    this.createAnomalyDeviceNames();
    this.fetchData();

    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.showDataIfEmpty();
    }, 500);
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  methods: {
    markerClicked() {},

    showDataIfEmpty() {
      this.$bus.$emit('CENTER_MAP');
    },

    isValid(obj) {
      return Object.keys(obj).length > 0;
    },

    refreshData() {
      this.fetchData();
    },

    fetchData() {
      let start = this.currentDate;
      this.fetchAnomalyData(start);
    },

    mapHeight(data) {
      if (data.ycategories) {
        const size = data.ycategories.length;
        return Math.round(120 + 35 * size);
      } else {
        return this.height;
      }
    },

    showBaselineDialog(params) {
      const { deviceId, direction, start, hour } = params;
      this.$refs.baselineDialog.init(deviceId, direction, start, hour);
      this.showDialog = true;
    },

    cellClicked(e) {
      console.log(e);
    },

    createAnomalyDeviceNames() {
      if (!this.anomalyDeviceNames) {
        this.anomalyDeviceNames = new Map();
        this.anomalyDevices.forEach(d => {
          this.anomalyDeviceNames.set(d.id, d.name.trimRight());
        });
      }
    },

    async fetchAnomalyData(start) {
      const timestamp = start.getTime();
      this.loading = true;
      try {
        // Now we await for both results, whose async processes have already been started
        const [set1, set2, set3, set4, set5, set6] = await Promise.all([
          Api.fetchAnomalyResults(timestamp, 'I-95', 'NB'),
          Api.fetchAnomalyResults(timestamp, 'I-95', 'SB'),
          Api.fetchAnomalyResults(timestamp, 'SR-1', 'NB'),
          Api.fetchAnomalyResults(timestamp, 'SR-1', 'SB'),
          Api.fetchAnomalyResults(timestamp, 'DE-1', 'NB'),
          Api.fetchAnomalyResults(timestamp, 'DE-1', 'SB')
        ]);

        this.clearDataSet(this.anomaly1);
        this.anomaly1 = this.parseResponse(set1, start, 'I-95', 'NB', 0);

        this.clearDataSet(this.anomaly2);
        this.anomaly2 = this.parseResponse(set2, start, 'I-95', 'SB', 1);

        this.clearDataSet(this.anomaly3);
        this.anomaly3 = this.parseResponse(set3, start, 'SR-1', 'NB', 2);

        this.clearDataSet(this.anomaly4);
        this.anomaly4 = this.parseResponse(set4, start, 'SR-1', 'SB', 3);

        this.clearDataSet(this.anomaly5);
        this.anomaly5 = this.parseResponse(set5, start, 'DE-1', 'NB', 4);

        this.clearDataSet(this.anomaly6);
        this.anomaly6 = this.parseResponse(set6, start, 'DE-1', 'SB', 5);
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    clearDataSet(data) {
      for (let key in data) {
        delete data[key];
      }
      data = {};
    },

    parseResponse(response, start, route, direction, index) {
      let result = {};
      if (response.data.status === 'OK') {
        if (response.data.data) {
          let data = response.data.data;

          // Create names
          const names = data.ycategories.map(deviceId => this.anomalyDeviceNames.get(deviceId));

          // Setup values
          result.start = start.getTime();
          result.route = route;
          result.direction = direction;
          result.title = `${route} ${direction}`;
          result.xAxis = 'Time of day';
          result.yAxis = '';
          result.xcategories = data.xcategories;
          result.ycategories = data.ycategories;
          result.permitNumbers = data.permitNumbers;
          result.names = names;
          result.data = data.series;
          result.index = index;
          result.startTime = Utils.getStartOfDay(start);
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'info' });
      }
      return result;
    },

    async fetchAnomalyHourlyResults(deviceId, direction, start, hour) {
      this.loading = true;
      try {
        const response = await Api.fetchAnomalyHourlyResults(deviceId, direction, start, hour);
        this.loading = false;
        if (response.data.status === 'OK') {
          if (response.data.data) {
            let data = response.data.data;

            let result = {};
            result.title = `${deviceId} - ${direction}`;
            result.xAxis = 'Time of Hour';
            result.yAxis = '';
            result.xcategories = data.xcategories;
            result.ycategories = data.ycategories;
            result.data = data.series;

            return result;
          }
        } else {
          this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;

      return {};
    },

    linkDevices(type) {
      switch (type) {
        case 1:
          this.selectGroup(this.anomaly1.ycategories);
          break;
        case 2:
          this.selectGroup(this.anomaly2.ycategories);
          break;
        case 3:
          this.selectGroup(this.anomaly3.ycategories);
          break;
        case 4:
          this.selectGroup(this.anomaly4.ycategories);
          break;
        case 5:
          this.selectGroup(this.anomaly5.ycategories);
          break;
        case 6:
          this.selectGroup(this.anomaly6.ycategories);
          break;
      }
    },

    cardClicked(type) {
      if (this.showPanel) {
        this.linkDevices(type);
      }
    },

    deviceClicked(deviceId) {
      this.$refs.mapSelectPanel.selectDeviceById(deviceId);
      this.$bus.$emit('ZOOM_SELECTED');
    },

    selectGroup(deviceIds) {
      this.$refs.mapSelectPanel.selectDevicesByIds(deviceIds);
    },

    ...mapActions('traffic', ['fetchAnomalyDevices'])
  }
};
</script>

<style lang="scss" scoped>
.basic-chart {
  height: 500px;
}
.link-btn {
  margin-top: -30px;
  margin-right: 20px;
  z-index: 10;
}
.col1 {
  width: 150px;
  margin-left: 40px;
}
.col1 {
  flex: 0 0 auto;
}

.flex-container {
  width: 100%;
  min-height: 400px;
  margin: 0 auto;
  display: flex;
}

.flex-container .column {
  padding: 10px;
  background: #dbdfe5;
  flex: 1;
}
</style>

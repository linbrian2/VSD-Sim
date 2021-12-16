<template>
  <div>
    <!-- Left map panel -->
    <SelectionPanel name="trafficFlowSideBarWidth" @navigation-width-changed="sideBarWidthChanged">
      <div class="d-flex justify-space-between">
        <v-combobox
          class="mx-2"
          dense
          hide-details
          single-line
          :items="items"
          :value="valueSelected"
          @input="valueSelectHandler"
          label="CHOOSE A DETECTOR TO SHOW"
        />
        <!-- Region selection menu -->
        <v-menu bottom right offset-y min-width="250" :close-on-content-click="true">
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <v-btn icon class="mx-1" v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-icon dark>mdi-dots-vertical</v-icon>
                </v-btn>
              </template>
              <span>Region Selection</span>
            </v-tooltip>
          </template>

          <v-list>
            <v-list-item v-for="item in region_menu_items" :key="item.value" @click="regionMenuItemClicked(item.value)">
              <v-list-item-title :class="{ 'font-weight-bold': item.value === selectedRegionId }">
                <v-icon class="mr-1" v-if="item.value === selectedRegionId">mdi-check</v-icon>
                <span :class="{ 'ml-8': item.value !== selectedRegionId }"> {{ item.title }} </span>
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
      <MapSelect ref="mapSelect" :markers="markers" :icons="icons" @click="markerClicked" />
    </SelectionPanel>

    <!-- Title bar on the top -->
    <TitleBar :title="title" showId showUid :loading="loading" :refresh="refreshData">
      <div class="d-flex align-items justify-space-between align-center">
        <div class="d-flex justify-space-between">
          <div class="mt-1 mr-15" style="width:140px;">
            <v-select
              dark
              dense
              v-model="interval"
              :items="intervalItems"
              item-text="text"
              item-value="value"
              @input="intervalSelected"
              hide-details
              prepend-icon="mdi-clock-outline"
              single-line
            />
          </div>

          <div class="mt-1 mr-10" style="width:100px;">
            <v-select
              dark
              dense
              v-model="direction"
              :items="directions"
              item-text="text"
              item-value="value"
              @input="directionSelected"
              hide-details
              prepend-icon="mdi-arrow-decision-outline"
              single-line
            />
          </div>
        </div>
      </div>
    </TitleBar>

    <!-- Charts -->
    <v-container>
      <v-tabs color="teal accent-4" v-model="tab" @change="tabChanged">
        <v-tab v-for="item in tabItems" :key="item.key" :href="`#${item.key}`">
          {{ item.value }}
        </v-tab>
      </v-tabs>
      <div>
        <v-tabs-items v-model="tab">
          <v-tab-item value="bound" v-if="isTabVisible('bound')">
            <TrafficFlowCombinedCharts :data="boundData" :name="name" :direction="direction" />
          </v-tab-item>

          <v-tab-item value="lane" v-if="isTabVisible('lane')">
            <TrafficFlowCombinedCharts :data="laneData" :name="name" :direction="direction" />
          </v-tab-item>

          <v-tab-item value="minute" v-if="isTabVisible('minute')">
            <TrafficFlowCombinedCharts :data="minuteData" :name="name" :direction="direction" />
          </v-tab-item>
        </v-tabs-items>
      </div>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapSelect from '@/components/modules/traffic/map/MapSelect';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import TrafficFlowCombinedCharts from '@/components/modules/traffic/common/TrafficFlowCombinedCharts';

export default {
  components: {
    SelectionPanel,
    MapSelect,
    TitleBar,
    TrafficFlowCombinedCharts
  },
  data: () => ({
    loading: false,
    showBaseline: true,
    interval: 300000,
    title: 'Traffic Flow Data',
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
    ],

    defaultTabItems: [
      { key: 'bound', value: 'Per Bound' },
      { key: 'lane', value: 'Per Lane' },
      { key: 'minute', value: 'Per Minute' }
    ],
    tabItems: [],
    tab: null,

    valueSelected: '',
    intervalItems: [
      { text: '1 Hour', value: 3600000 },
      { text: '30 mins', value: 1800000 },
      { text: '15 mins', value: 900000 },
      { text: '5 mins', value: 300000 },
      { text: '1 min', value: 60000 }
    ],
    direction: '',

    region_menu_items: [
      { title: 'All Detectors', value: -1 },
      { title: 'Urban Freeway Detectors', value: 1 },
      { title: 'Urban System Detectors', value: 2 },
      { title: 'CAV Area Freeway Detectors', value: 3 },
      { title: 'CAV Area System Detectors', value: 4 },
      { title: ' Rural Freeway Detectors', value: 5 },
      { title: 'Rural System Detectors', value: 6 },
      { title: 'Outside study area', value: 7 }
    ],

    selectedRegionId: -1,

    devices: [],
    boundData: {},
    laneData: {},
    minuteData: {}
  }),

  computed: {
    markers() {
      if (this.selectedRegionId < 0) {
        return this.devices;
      } else {
        return this.devices.filter(location => location.flags === this.selectedRegionId);
      }
    },

    items() {
      return this.markers.map(location => location.name);
    },

    name() {
      const marker = this.activeMarker;
      return marker && marker.uid;
    },

    directions() {
      const marker = this.activeMarker;
      if (!marker || !marker.directions) {
        return [];
      }

      const result = [];
      if (marker.directions.length > 1) {
        result.push({ text: 'All', value: '' });
      }
      marker.directions.forEach(direction => {
        result.push({ text: direction, value: direction });
      });
      return result;
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['activeMarker'])
  },

  mounted() {
    this.fetchDevices();

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

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  methods: {
    sideBarWidthChanged() {
      this.$bus.$emit('CHART_RELOAD');
    },

    showDataIfEmpty() {
      this.$bus.$emit('CENTER_MAP');
      this.$bus.$emit('SELECT_FIRST');
    },

    valueSelectHandler(value) {
      this.$bus.$emit('NAME_SELECTED', value);
    },

    regionMenuItemClicked(value) {
      setTimeout(() => {
        this.selectedRegionId = value;
        this.valueSelected = '';
      }, 100);
    },

    isTabVisible(name) {
      return this.tabItems.find(i => i.key === name) !== undefined;
    },

    markerClicked(marker) {
      this.valueSelected = marker.name;
      const time = this.currentDate.getTime();
      this.fetchTrafficFlowData(marker.id, marker.uid, this.interval, time);
    },

    refreshData() {
      this.fetchData();
    },

    tabChanged() {},
    directionSelected() {},

    intervalSelected() {
      this.fetchData();
    },

    fetchData() {
      let marker = this.activeMarker;
      let time = this.currentDate.getTime();
      if (marker != null) {
        this.fetchTrafficFlowData(marker.id, marker.uid, this.interval, time);
      }
    },

    async fetchDevices() {
      try {
        const response = await Api.fetchDevices();
        this.devices = response.data;
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async fetchTrafficFlowData(id, uid, interval, time) {
      this.loading = true;
      try {
        // Now we await for all results, whose async processes have already been started
        const [baselineRes, perBoundRes, perLaneRes, perMinRes] = await Promise.all([
          Api.fetchTrafficFlowBaselineData(id, null, interval, time, null),
          Api.fetchTrafficFlowData(id, null, interval, time, null),
          Api.fetchTrafficFlowPerLaneData(id, uid, null, interval, time, null),
          Api.fetchTrafficFlowPerMinuteData(id, null, interval, time, null)
        ]);

        const dataList = [];
        dataList.push(this.getResponseData(perBoundRes, false));
        dataList.push(this.getResponseData(perLaneRes, false));
        dataList.push(this.getResponseData(perMinRes, false));

        this.boundData = dataList[0] ? dataList[0] : {};
        this.laneData = dataList[1] ? dataList[1] : {};
        this.minuteData = dataList[2] ? dataList[2] : {};

        const baselineData = this.getResponseData(baselineRes, false);
        if (baselineData && !Utils.isEmpty(baselineData)) {
          // modify the color and name
          baselineData.speed.forEach((speed, index) => {
            baselineData.speed[index].color = '#50B432'; //Utils.shadeColor(speed.color, 90);
            baselineData.speed[index].name = 'Baseline'; // + speed.name;
          });

          baselineData.volume.forEach((volume, index) => {
            baselineData.volume[index].color = '#50B432'; // Utils.shadeColor(volume.color, 90);
            baselineData.volume[index].name = 'Baseline'; //+ volume.name;
          });

          baselineData.occupancy.forEach((occupancy, index) => {
            baselineData.occupancy[index].color = '#50B432'; //Utils.shadeColor(occupancy.color, 90);
            baselineData.occupancy[index].name = 'Baseline'; // + occupancy.name;
          });

          // Merge with bound data
          this.boundData.speed.push(...baselineData.speed);
          this.boundData.volume.push(...baselineData.volume);
          this.boundData.occupancy.push(...baselineData.occupancy);
        }

        this.rangeTabItems(dataList);

        // Reset direction selection if not present in the new direction list
        // if (this.directions.find(item => item.value === this.direction) === undefined) {
        //   this.direction = '';
        // }

        // Select the first one
        this.direction = this.directions.length > 0 ? this.directions[0].value : '';

        if (dataList.every(item => item === null)) {
          this.showWarningMessage('No data available');
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    getResponseData(response, showWarning) {
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
      this.$store.dispatch('setSystemStatus', { text: message, color: 'info', timeout: 1000 });
    },

    rangeTabItems(dataList) {
      const items = Object.assign([], this.defaultTabItems);
      for (let i = 0; i < dataList.length; i++) {
        if (dataList[i] === null) {
          this.removeTabByName(items, this.defaultTabItems[i].key);
        }
      }

      if (
        !Utils.arraysEqual(
          items.map(item => item.key),
          this.tabItems.map(item => item.key)
        )
      ) {
        if (items.length > 0) {
          this.tab = items[0].key;
        }
        this.tabItems = items;
      }
    },

    removeTabByName(items, name) {
      const e = items.findIndex(item => item.key === name);
      if (e >= 0) {
        items.splice(e, 1);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.active-select {
  background-color: red;
  color: white;
  border-style: solid;
  border-width: 5px;
}
</style>

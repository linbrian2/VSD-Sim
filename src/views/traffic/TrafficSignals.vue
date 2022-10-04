<template>
  <div>
    <!-- Left map panel -->
    <SelectionPanel name="signalsBarWidth">
      <div class="d-flex justify-space-between">
        <v-combobox
          class="mx-2"
          dense
          hide-details
          single-line
          item-text="text"
          item-value="value"
          :items="deviceItems"
          :value="valueSelected"
          @input="groupSelectionHandler"
          label="Traffic Signals"
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
              <span>Filter</span>
            </v-tooltip>
          </template>

          <v-list>
            <template v-for="(item, index) in region_menu_items">
              <v-divider v-if="item.divider" :key="index + 100"></v-divider>
              <v-list-item v-else :key="item.value" @click="regionMenuItemClicked(item.value)">
                <v-list-item-title :class="{ 'font-weight-bold': item.value === selectedFilterId }">
                  <v-icon class="mr-1" v-if="item.value === selectedFilterId">mdi-check</v-icon>
                  <span :class="{ 'ml-8': item.value !== selectedFilterId }"> {{ item.title }} </span>
                </v-list-item-title>
              </v-list-item>
            </template>
          </v-list>
        </v-menu>
      </div>
      <MapSelect ref="mapSelect" :markers="markers" :icons="icons" @click="onMapClick" />
    </SelectionPanel>

    <TitleBar :title="title" :loading="loading" :refresh="refreshData" :showMap="true"> </TitleBar>

    <v-container>
      <div class="mx-4">
        <div v-if="infoAvailable">
          <v-row>
            <v-col cols="12">
              <div class="d-flex justify-space-between">
                <v-subheader class="pl-0 text-overline blue--text">
                  <h3>Intersection Info</h3>
                </v-subheader>

                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn icon @click.stop="showInfo = !showInfo" v-bind="attrs" v-on="on">
                      <v-icon small dark>mdi-chevron-down</v-icon>
                    </v-btn>
                  </template>
                  <span>Hide Info</span>
                </v-tooltip>
              </div>

              <v-divider />
            </v-col>
          </v-row>

          <v-row v-show="showInfo">
            <v-col cols="4">
              <DataCard title="Basic" :items="detectorInfo['basic']" />
            </v-col>
            <v-col cols="4">
              <DataCard title="ID" :items="detectorInfo['ids']" />
            </v-col>
            <v-col cols="4">
              <DataCard title="Misc" :items="detectorInfo['misc']" />
            </v-col>
          </v-row>
        </div>

        <div v-if="showResponsivePlot">
          <v-row>
            <v-col cols="12">
              <div class="d-flex justify-space-between">
                <v-subheader class="pl-0 text-overline blue--text">
                  <h3>Traffic Responsive Zone</h3>
                </v-subheader>
              </div>

              <v-divider />
            </v-col>

            <v-col cols="12">
              <v-card tile elevation="12" v-if="responsiveDataAvailable">
                <TrafficResponsiveChart :data="responsiveChartData" :height="responsiveChartHeight" />
              </v-card>
            </v-col>
          </v-row>
        </div>

        <div v-if="dailyPatternAvailable">
          <v-row>
            <v-col cols="12">
              <div class="d-flex justify-space-between">
                <v-subheader class="pl-0 text-overline blue--text">
                  <h3>Daily Pattern Transition</h3>
                </v-subheader>
              </div>

              <v-divider />
            </v-col>

            <v-col cols="12">
              <v-card tile elevation="12" v-if="delayComplete">
                <SignalPatternChart :data="signalChartData" :height="defaultHeight" />
              </v-card>
            </v-col>
          </v-row>
        </div>

        <v-row>
          <v-col cols="12">
            <div class="d-flex justify-space-between">
              <v-subheader class="pl-0 text-overline blue--text">
                <h3>Signal Cycle Patterns</h3>
              </v-subheader>

              <div v-show="currentPatternAvailable" class="mt-3">
                <span class="overline">Current pattern started at {{ currentPatternTime }}</span>
                <v-chip color="orange" small class="ml-3">
                  <span class="font-weight-bold black--text"> {{ currentPatternNumber }}</span>
                </v-chip>
              </div>
            </div>

            <v-divider />
          </v-col>

          <v-col cols="12">
            <v-card tile class="mt-n2" elevation="12" v-if="currentSignal">
              <SignalTable :items="currentSignal" :current="currentPatternNumber" />
            </v-card>
          </v-col>
        </v-row>
      </div>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState } from 'vuex';
import { TrafficLightIcons } from '@/mixins/TrafficLightIcons.js';
import { RouterNames } from '@/utils/constants/router';
import SignalTable from '@/components/modules/traffic/tables/SignalTable';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapSelect from '@/components/modules/traffic/map/MapSelect';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import Utils from '@/utils/Utils';
import TrafficConstants from '@/utils/constants/traffic';
import DataCard from '@/components/modules/traffic/common/DataCard';
import SignalPatternChart from '@/components/modules/traffic/chart/SignalPatternChart';
import TrafficResponsiveChart from '@/components/modules/traffic/chart/TrafficResponsiveChart';

export default {
  mixins: [TrafficLightIcons],
  components: {
    SelectionPanel,
    MapSelect,
    TitleBar,
    SignalTable,
    DataCard,
    SignalPatternChart,
    TrafficResponsiveChart
  },

  data: () => ({
    delayComplete: false,
    loading: false,
    defaultHeight: 350,
    responsiveChartHeight: 500,
    title: RouterNames.TRAFFIC_SIGNALS,
    zoneChanged: true,
    showInfo: true,
    showResponsivePlot: true,
    responsiveChartData: {},
    signalChartData: {},
    currentPermit: null,
    currentGroupId: null,
    currentSignal: null,
    valueSelected: null,
    currentZoneDetectors: [],
    deviceList: [],
    deviceGroups: null,
    currentPattern: null,
    detectorInfo: {},
    region_menu_items: TrafficConstants.SIGNAL_DEVICE_CATEGORIES,
    selectedFilterId: -1,
    icons: [
      {
        url: require('@/assets/traffic_light_xsmall.png'),
        size: { width: 10, height: 24, f: 'px', b: 'px' },
        anchor: { x: 5, y: 12 }
      },
      {
        url: require('@/assets/traffic_light_active.png'),
        size: { width: 15, height: 36, f: 'px', b: 'px' },
        anchor: { x: 8, y: 18 }
      },
      {
        url: require('@/assets/traffic_light_selected.png'),
        size: { width: 15, height: 36, f: 'px', b: 'px' },
        anchor: { x: 8, y: 18 }
      }
      // {
      //   path: 0,
      //   scale: 10.0,
      //   fillColor: '#05FF00',
      //   fillOpacity: 0.8,
      //   strokeWeight: 0.4
      // }
    ]
  }),

  computed: {
    //https://codepen.io/akolesnikov/pen/JjEeKLQ
    //https://www.npmjs.com/package/vuetify-toolkit
    markers() {
      if (this.selectedFilterId < 0) {
        return this.deviceList;
      } else {
        if (this.selectedFilterId < 10) {
          return this.deviceList.filter(d => d.region === this.selectedFilterId);
        } else {
          if (this.selectedFilterId === 11) {
            return this.deviceList.filter(d => d.enabledTr > 0);
          } else {
            return this.deviceList;
          }
        }
      }
    },

    infoAvailable() {
      return !Utils.isEmpty(this.detectorInfo);
    },

    currentPatternAvailable() {
      return !Utils.isEmpty(this.currentPattern);
    },

    dailyPatternAvailable() {
      return !Utils.isEmpty(this.signalChartData) && Utils.isEmpty(this.responsiveChartData);
    },

    responsiveDataAvailable() {
      return !Utils.isEmpty(this.responsiveChartData);
    },

    currentPatternTime() {
      if (!this.currentPattern) {
        return '';
      }
      const d = new Date(this.currentPattern.timestamp);
      if (Utils.isToday(d)) {
        return Utils.formatAMPMTime(d);
      } else {
        return Utils.formatDateTime(d);
      }
    },

    currentPatternNumber() {
      return this.currentPattern ? this.currentPattern.pattern + '' : '';
    },

    deviceItems() {
      return this.deviceList.map(d => ({ text: d.name, value: d.uid, groupId: d.groupId }));
    },

    groupItems() {
      if (!this.deviceGroups) {
        return [];
      }

      const items = [];
      Object.entries(this.deviceGroups).forEach(entry => {
        if (entry[0]) {
          const desc = entry[1].length > 0 ? entry[1][0].groupName : 'No Description';
          items.push({
            text: desc,
            value: entry[0]
          });
        }
      });
      return items;
    },
    ...mapState(['currentDate'])
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  mounted() {
    if (this.deviceList.length == 0) {
      this.fetchTrafficSignalDeviceList();
    }

    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.delayComplete = true;
      this.showDataIfEmpty();
    }, 500);
  },

  methods: {
    showDataIfEmpty() {
      this.$bus.$emit('CENTER_MAP');
      this.$bus.$emit('SELECT_FIRST');
    },

    onMapClick(marker) {
      const groupId = marker.groupId;
      this.zoneChanged = groupId != this.currentGroupId;
      this.currentGroupId = groupId;
      const selected = this.deviceItems.find(item => item.value == marker.uid);
      if (selected) {
        this.valueSelected = selected.text;
      }
      this.selectDetectorsByGroupId(groupId, null);
      this.fetchData(marker.uid);
    },

    groupSelectionHandler(value) {
      const groupId = value.groupId;
      this.zoneChanged = groupId != this.currentGroupId;
      this.currentGroupId = groupId;

      const detector = this.findFirstDetectorByUid(value.value);
      if (detector) {
        this.selectDetectorsByGroupId(groupId, detector.id);
        this.fetchData(detector.uid);
      }
    },

    selectDetectorsByGroupId(groupId, id) {
      if (groupId) {
        const ids = this.deviceList.filter(d => d.groupId === groupId).map(d => d.id);
        this.$refs.mapSelect.selectByIds(ids, false);
        if (id != null) {
          this.$refs.mapSelect.selectById(id);
        }
      }
    },

    findFirstDetectorByGroupId(groupId) {
      const detector = this.deviceList.find(d => d.groupId == groupId);
      return detector;
    },

    findFirstDetectorByUid(uid) {
      const detector = this.deviceList.find(d => d.uid == uid);
      return detector;
    },

    regionMenuItemClicked(value) {
      setTimeout(() => {
        this.selectedFilterId = value;
        this.valueSelected = '';
      }, 100);
    },

    showResponsiveChart() {
      this.showResponsivePlot = !this.showResponsivePlot;
      if (this.showResponsivePlot) {
        if (Utils.isEmpty(this.responsiveChartData)) {
          const start = this.currentDate.getTime();
          const zoneId = this.currentGroupId;
          this.fetchTrafficResponsiveData(zoneId, start);
        }
      }
    },

    refreshData() {
      if (this.currentPermit) {
        this.fetchData(this.currentPermit);
      }
    },

    fetchData(permit) {
      this.fetchSignalCycleSplit(permit);
    },

    fetchResponsiveZoneData(trEnabledSignal) {
      if (trEnabledSignal) {
        if (this.zoneChanged || Utils.isEmpty(this.responsiveChartData)) {
          const start = this.currentDate.getTime();
          const zoneId = this.currentGroupId;
          this.fetchTrafficResponsiveData(zoneId, start);
        }
      } else {
        this.responsiveChartData = {};
        this.showResponsivePlot = false;
      }
    },

    async fetchTrafficSignalDeviceList() {
      try {
        const response = await Api.trafficSignalDevices();
        this.deviceList = response.data;
        this.deviceGroups = this.groupSignalDevices(this.deviceList);
      } catch (error) {
        this.showWarningMessage(error);
      }
    },

    async fetchSignalCycleSplit(permit) {
      this.loading = true;

      try {
        const response = await Api.getSignalCycleSplit(permit);
        const data = this.parseResponse(response, false);
        if (data != null) {
          this.detectorInfo = this.composeDetectorInfo(data.device);
          this.currentSignal = data.signal;
          this.currentPattern = data.current;
          this.signalChartData = this.composeSignalData(data.pattern);
          this.fetchResponsiveZoneData(data.device.enabledTr);
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    async fetchTrafficResponsiveData(zoneId, startDate) {
      this.loading = true;
      try {
        const response = await Api.fetchTrafficResponsiveData(zoneId, startDate, null, true);
        const data = this.parseResponse(response, false);
        if (data != null) {
          this.responsiveChartData = this.composeResponsiveChartData(zoneId, data);
          this.currentZoneDetectors = data.detectors ? data.detectors.devices : [];
          this.showResponsivePlot = true;
        } else {
          this.responsiveChartData = {};
          this.showResponsivePlot = false;
        }
      } catch (error) {
        console.log(error);
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

    groupSignalDevices(deviceList) {
      // Group the data by group
      const groups = deviceList.reduce((r, item) => {
        r[item.groupId] = r[item.groupId] || [];
        r[item.groupId].push(item);
        return r;
      }, {});

      return groups;

      // Generate the tree-view items
      // const items = [];
      // Object.entries(groups).forEach(entry => {
      //   const groupId = entry[0];
      //   const values = entry[1];
      //   const children = values.map(child => ({ id: child.uid, name: child.name }));
      //   items.push({ id: groupId, name: values[0].groupName + ` (${values.length})`, children: children });
      // });
      // return items;
    },

    composeDetectorInfo(info) {
      const basic = [
        { name: 'Permit:', value: info.permit },
        { name: 'Location:', value: info.name },
        { name: 'IP Addr:', value: info.ip }
      ];

      const ids = [
        { name: 'Group Id:', value: info.groupId },
        { name: 'Intersection Id:', value: info.intersectionId },
        { name: 'Parent Zone Id', value: info.parentZoneId }
      ];

      // const button = {
      //   color: 'green',
      //   icon: 'mdi-traffic-light-outline',
      //   tooltip: 'Click to show traffic responsive chart',
      //   handler: this.showResponsiveChart
      // };

      // const buttonVal = info.enabledTr ? button : null;

      const misc = [
        { name: 'Number of Lanes:', value: info.numLanes },
        { name: 'TR Enabled:', value: info.enabledTr ? 'YES' : 'NO' },
        { name: 'Firmware Version', value: info.controllerVersion }
      ];

      return { basic, ids, misc };
    },

    composeSignalData(patterns) {
      if (!patterns) return {};

      const title = '';
      const xAxis = 'Time of day';
      const yAxis = 'Cycle Length (seconds)';

      let data = [];
      const fp = patterns.map(item => [item[0], item[1]]);
      data.push({ name: 'Cycle Length', color: '#fed976', data: fp, tracking: false });

      return { data, xAxis, yAxis, title, ymin: 0, ymax: 100, patterns: patterns };
    },

    composeResponsiveChartData(zoneId, resData) {
      const title = `Traffic Responsive Plot (${zoneId})`;
      const xAxis = 'Time of day';
      const yAxis = 'V + O (%)';

      let data = [];
      data.push({ name: 'NB V+O', color: '#ED561B', data: resData.NBVO, tracking: true });
      data.push({ name: 'SB V+O', color: '#50B432', data: resData.SBVO, tracking: true });

      if (resData.patterns) {
        const patterns = resData.patterns.map(item => [item[0], item[1]]);
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

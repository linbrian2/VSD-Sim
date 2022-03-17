<template>
  <div class="status">
    <v-main class="pa-0">
      <v-row>
        <!-- Resizable Map -->
        <template v-if="pref && pref.resizableMap">
          <div class="map">
            <SelectionPanel :width="420">
              <Map
                :apiInfo="apiInfo"
                :markers="markers"
                :polylines="polylines"
                :icons="icons"
                :disableDefaultUI="true"
                :height="'calc(100vh - 48px)'"
              />
            </SelectionPanel>
          </div>
        </template>
        <!-- Overview -->
        <v-col id="overview" :cols="rsz ? 4 : 3" :xl="rsz ? 3 : 2" class="pa-0 px-3 pl-3">
          <v-container style="max-height: calc(100vh - 48px); overflow-y: auto;" class="py-0">
            <v-row>
              <v-col cols="12" v-for="(x, i) in cardData" :key="x.id" class="pa-1">
                <v-system-bar :color="getColor(x)" height="10" />
                <v-hover v-slot="{ hover }">
                  <v-card
                    height="calc(18.9vh - 48px)"
                    class="d-flex align-center justify-center"
                    @click.native="cardClicked(i)"
                    :color="selectedIdx != i ? null : $store.state.darkMode ? 'grey darken-1' : 'grey lighten-1'"
                    :elevation="hover ? 12 : 2"
                    :class="{ 'on-hover': hover }"
                  >
                    <v-col class="grid-center pa-0">
                      <v-card-title v-show="$vuetify.breakpoint.lgAndUp" class="py-0" style="font-size:19px">
                        {{ x.title }}
                      </v-card-title>
                      <h1 style="font-size:42px" class="pr-2">
                        <v-icon class="pb-2" :color="colors[i]" x-large>{{ x.icon }}</v-icon>
                        {{ x.val }}
                      </h1>
                    </v-col>
                    <div v-show="$vuetify.breakpoint.lgAndUp" class="card-container">
                      <v-btn icon @click="showSelectionDialog(i)" class="detail-button" v-if="i != 3">
                        <v-icon :class="{ 'show-btns': hover }" :color="transparent">
                          mdi-information-outline
                        </v-icon>
                      </v-btn>
                      <v-btn icon @click="goToPage(x.link)" class="link-button">
                        <v-icon :class="{ 'show-btns': hover }" :color="transparent">
                          mdi-open-in-new
                        </v-icon>
                      </v-btn>
                    </div>
                  </v-card>
                </v-hover>
              </v-col>
            </v-row>
          </v-container>
        </v-col>
        <!-- Data & Map -->
        <v-col class="pa-0" :cols="rsz ? 8 : 9" :xl="rsz ? 9 : 10">
          <v-row>
            <!-- Data -->
            <v-col id="data" :cols="rsz ? 12 : 6" class="pa-0" v-if="pref.layout == '1: Card, 2: Info, 3: Map'">
              <InfoColumn ref="infoColumn" :apiInfo="apiInfo" :selectedIdx="selectedIdx" :cardData="cardData" />
            </v-col>
            <!-- Map -->
            <v-col id="map" cols="6" class="pa-0" v-if="!pref || !pref.resizableMap">
              <div class="map">
                <Map
                  :apiInfo="apiInfo"
                  :markers="markers"
                  :polylines="polylines"
                  :icons="icons"
                  :height="'calc(100vh - 48px)'"
                />
              </div>
            </v-col>
            <v-col id="data" :cols="rsz ? 12 : 6" class="pa-0" v-if="pref.layout == '1: Card, 2: Map, 3: Info'">
              <InfoColumn :apiInfo="apiInfo" :selectedIdx="selectedIdx" :cardData="cardData" />
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <SelectionDialog v-model="showSelection" ref="selectionDialog" />
    </v-main>
  </div>
</template>

<script>
import Devices from '@/utils/Devices.js';
import Map from '@/components/modules/dashboard/Map.vue';
import InfoColumn from '@/components/modules/dashboard/InfoColumn.vue';
import SelectionPanel from '@/components/modules/dashboard/SelectionPanel.vue';
import SelectionDialog from '@/components/modules/dashboard/SelectionDialog.vue';
import Constants from '@/utils/constants/dashboard.js';
import StatusApi from '@/utils/api/status';
import { mapActions, mapState } from 'vuex';

export default {
  name: 'App',
  components: {
    Map,
    SelectionPanel,
    InfoColumn,
    SelectionDialog
  },
  data() {
    return {
      deviceLocations: [],
      incidents: [],
      flowAnomData: [],
      icons: undefined,
      showSelection: false,
      markers: [],
      polylines: null,
      loading: false,
      manualMode: false,
      apiInfo: null,
      updateInterval: null,
      cardSwapInterval: null,
      elapsedTime: 0,
      cardElapsedTime: 0,
      cardData: [
        {
          title: Constants.TRAFFIC_INCIDENTS,
          icon: Constants.TRAFFIC_INCIDENTS_ICON,
          link: 'http://http://aitoms.net/flow/incidents',
          val: '-',
          thresholds: [
            { val: 0, color: 'green' },
            { val: 3, color: 'yellow' },
            { val: 6, color: 'orange' },
            { val: 9, color: 'red' }
          ]
        },
        {
          title: Constants.DEVICE_TRAFFIC,
          icon: Constants.DEVICE_TRAFFIC_ICON,
          link: 'http://aitoms.net/',
          val: '-',
          thresholds: [
            { val: 0, color: 'green' },
            { val: 2, color: 'yellow' },
            { val: 4, color: 'orange' },
            { val: 6, color: 'red' }
          ]
        },
        {
          title: Constants.SIGNAL_PERFORMANCE_ISSUES,
          icon: Constants.SIGNAL_PERFORMANCE_ISSUES_ICON,
          link: 'http://aitoms.net/hr/summary',
          val: '-',
          thresholds: [
            { val: 0, color: 'green' },
            { val: 2, color: 'yellow' },
            { val: 4, color: 'orange' },
            { val: 6, color: 'red' }
          ]
        },
        {
          title: Constants.TRAFFIC_FLOW_ANOMALIES,
          icon: Constants.TRAFFIC_FLOW_ANOMALIES_ICON,
          link: 'http://aitoms.net/flow/bt-dash',
          val: '-',
          thresholds: [
            { val: 0, color: 'green' },
            { val: 8000, color: 'yellow' },
            { val: 12000, color: 'orange' },
            { val: 15000, color: 'red' }
          ]
        },
        {
          title: Constants.HIGH_CONGESTION_ROUTES,
          icon: Constants.HIGH_CONGESTION_ROUTES_ICON,
          link: 'http://aitoms.net/flow/bt-dash',
          val: '-',
          thresholds: [
            { val: 0, color: 'green' },
            { val: 5, color: 'yellow' },
            { val: 10, color: 'orange' },
            { val: 15, color: 'red' }
          ]
        },
        {
          title: Constants.REPORTED_WAZE_ALERTS,
          icon: Constants.REPORTED_WAZE_ALERTS_ICON,
          link: 'http://aitoms.net/flow/bt-dash',
          val: '-',
          thresholds: [
            { val: 0, color: 'green' },
            { val: 150, color: 'yellow' },
            { val: 225, color: 'orange' },
            { val: 300, color: 'red' }
          ]
        }
      ],
      colors: ['red', 'orange', 'yellow', 'green', 'blue', 'teal'],
      selectedIdx: 0,
      transparent: 'rgba(255, 255, 255, 0)'
    };
  },
  computed: {
    rsz() {
      return this.pref.resizableMap;
    },
    ...mapState('dashboard', [
      'pref',
      'weatherStations',
      'trafficIncidents',
      'trafficDevices',
      'signalPerformanceIssues',
      'hrSummary',
      'detectors',
      'segments',
      'waze'
    ])
  },
  created() {
    window.addEventListener('click', this.keydownListener);
  },

  destroyed() {
    window.removeEventListener('click', this.keydownListener);
  },
  mounted() {
    this.startUpdateInterval();
    this.startCardSwapInterval();
    this.fetchApiData();

    this.$bus.$on('SHOW_SELECTION_POPUP', id => {
      this.showSelectionDialog(id);
    });
    this.$bus.$on('DISPLAY_MARKER_DETAILS_DASHBOARD', ({ id, type }) => {
      this.handleMarkerClick(id, type);
    });
  },
  beforeDestroy() {
    this.stopUpdateInterval();
    this.stopCardSwapInterval();
  },
  methods: {
    handleMarkerClick(id, type) {
      // let marker = null;
      console.log(id);
      console.log(type);
      /* switch (type) {
        case 0:
          marker = this.markers.find(m => m.id === id);
          this.markerClicked(marker);
          break;
        case 1:
          marker = this.bluetoothLocations.find(m => m.id === id);
          this.segmentClicked(marker);
          break;
        case 2:
          marker = this.weatherStations.find(m => m.id === id);
          this.weatherMarkerClicked(marker);
          break;
        case 3:
          marker = this.restrictions.find(m => m.id === id);
          this.restrictionClicked(marker);
          break;
        case 4:
          marker = this.currentAnomalySegments.find(m => m.id === id);
          this.anomalySegmentClicked(marker);
          break;
      } */
    },
    startUpdateInterval() {
      this.updateInterval = setInterval(this.updateData, 1000);
    },
    stopUpdateInterval() {
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.updateInterval = null;
      }
    },
    fetchApiData() {
      this.fetchWeatherStations();
      this.fetchTrafficIncidents();
      this.fetchTrafficDevices();
      this.fetchSignalPerformanceIssues();
      this.fetchStatus();
      this.fetchStatusOfDevices();
      this.fetchDetectors();
      this.fetchSegments();
      this.fetchWaze();
    },
    updateData() {
      this.elapsedTime++;
      if (this.pref && this.pref.dataUpdateEnabled && this.elapsedTime >= 300) {
        this.elapsedTime = 0;
        this.fetchApiData();
      }
    },
    startCardSwapInterval() {
      this.cardSwapInterval = setInterval(this.updateCardSwap, 1000);
    },
    stopCardSwapInterval() {
      if (this.cardSwapInterval) {
        clearInterval(this.cardSwapInterval);
        this.cardSwapInterval = null;
      }
    },
    updateCardSwap() {
      this.cardElapsedTime++;
      if (!this.manualMode && this.pref && this.pref.swapEnabled && this.cardElapsedTime >= 45) {
        this.cardElapsedTime = 0;
        if (this.selectedIdx == 5) {
          this.cardClicked(0);
        } else {
          this.cardClicked(this.selectedIdx + 1);
        }
      }
    },
    keydownListener() {
      this.cardElapsedTime = -75;
    },
    async fetchStatus() {
      let date = new Date();
      try {
        this.updatedTime = new Date();
        const response = await StatusApi.fetchErrors(date.getTime());
        const data = this.parseResponseData(response);
        if (data) {
          // this.flowAnomData = data;
          let errCount5m = data.totalErrorCounts.filter(x => x != 0);
          let count = 0;
          for (let i = 0; i < 12; i++) {
            if (i < errCount5m.length) {
              count += errCount5m[errCount5m.length - 1 - i];
            }
          }
          this.flowAnomData = { ...data, count };
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },
    createTotalSegments() {
      const linkIds = new Set();
      this.incidents.forEach(item => {
        linkIds.add(item.linkId);
      });

      const segments = [];
      linkIds.forEach(linkId => {
        const segment = this.bluetoothSegments.find(item => item.id === linkId);
        if (segment) {
          segments.push(segment);
        }
      });

      return segments;
    },
    parseResponseData(response) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },
    showSelectionDialog(type) {
      if (this.$refs.selectionDialog) {
        if (type === 0) {
          this.$refs.selectionDialog.init(
            Constants.TRAFFIC_INCIDENTS_ICON,
            Constants.TRAFFIC_INCIDENTS,
            0,
            this.incidents
          );
        } else if (type === 1) {
          this.$refs.selectionDialog.init(
            Constants.DEVICE_TRAFFIC_ICON,
            Constants.DEVICE_TRAFFIC,
            1,
            this.trafficDevices
          );
        } else if (type === 2) {
          this.$refs.selectionDialog.init(
            Constants.SIGNAL_PERFORMANCE_ISSUES_ICON,
            Constants.SIGNAL_PERFORMANCE_ISSUES,
            2,
            this.hrSummary
          );
        } else if (type === 3) {
          this.$refs.selectionDialog.init(
            Constants.TRAFFIC_FLOW_ANOMALIES_ICON,
            Constants.TRAFFIC_FLOW_ANOMALIES,
            3,
            this.detectors
          );
        } else if (type === 4) {
          this.$refs.selectionDialog.init(
            Constants.HIGH_CONGESTION_ROUTES_ICON,
            Constants.HIGH_CONGESTION_ROUTES,
            4,
            this.segments
          );
        } else if (type === 5) {
          this.$refs.selectionDialog.init(
            Constants.REPORTED_WAZE_ALERTS_ICON,
            Constants.REPORTED_WAZE_ALERTS,
            5,
            this.waze
          );
        }
        this.showSelection = true;
      }
    },

    getColor(param) {
      if (param.val == '-') {
        return 'black';
      }
      if (param && param.thresholds) {
        for (let i = 0; i < param.thresholds.length; i++) {
          if (i == param.thresholds.length - 1) {
            return param.thresholds[i].color;
          } else {
            if (param.val >= param.thresholds[i].val && param.val < param.thresholds[i + 1].val) {
              return param.thresholds[i].color;
            }
          }
        }
      } else {
        return 'black';
      }
    },
    openDialog() {
      this.showDialog = true;
    },
    cardClicked(idx) {
      this.selectedIdx = idx;
      switch (idx) {
        case 0:
          this.markers = [];
          this.icons = undefined;
          break;
        case 1:
          this.markers = this.trafficDevices;
          this.icons = undefined;
          break;
        case 2:
          this.markers = this.signalPerformanceIssues;
          this.icons = this.getHRIcons();
          break;
        case 3:
          this.markers = Devices;
          this.icons = undefined;
          break;
        case 4:
          this.markers = this.segments;
          // this.polylines = this.segments;
          this.icons = this.getTravelTimeIcons();
          break;
        case 5:
          this.markers = this.waze;
          this.icons = this.getWazeIcons();
          break;
        default:
          alert('Unhandled Case');
          break;
      }
    },
    goToPage(link) {
      window.open(link, '_blank');
    },
    getTravelTimeIcons() {
      return [
        {
          path: 0,
          scale: 10.0,
          fillColor: '#0580FF',
          fillOpacity: 0.8,
          strokeWeight: 1.0,
          strokeColor: 'white'
        },
        {
          path: 0,
          scale: 10.0,
          fillColor: '#FF7F00',
          fillOpacity: 0.8,
          strokeWeight: 1.0,
          strokeColor: 'white'
        }
      ];
    },
    getWazeIcons() {
      return [
        {
          url: require('@/assets/waze.png'),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        },
        {
          url: require('@/assets/waze-select.png'),
          size: { width: 36, height: 36, f: 'px', b: 'px' },
          anchor: { x: 18, y: 18 }
        }
      ];
    },
    getHRIcons() {
      return [
        {
          url: require('@/assets/traffic_light_small.png'),
          size: { width: 15, height: 36, f: 'px', b: 'px' },
          anchor: { x: 8, y: 18 }
        },
        {
          url: require('@/assets/traffic_light_active.png'),
          size: { width: 15, height: 36, f: 'px', b: 'px' },
          anchor: { x: 8, y: 18 }
        }
      ];
    },
    ...mapActions('dashboard', [
      'fetchWeatherStations',
      'fetchTrafficIncidents',
      'fetchTrafficDevices',
      'fetchSignalPerformanceIssues',
      'fetchStatusOfDevices',
      'fetchDetectors',
      'fetchSegments',
      'fetchWaze'
    ])
  },
  watch: {
    trafficIncidents(incidents) {
      if (incidents) {
        this.cardData[0].val = incidents.length;
      } else {
        return 'N/A';
      }
    },
    trafficDevices(trafficDevices) {
      if (trafficDevices) {
        this.cardData[1].val = trafficDevices.filter(x => x.status !== 0).length;
      } else {
        return 'N/A';
      }
    },
    hrSummary(hrSummary) {
      if (hrSummary) {
        this.cardData[2].val = 2; /* hrSummary.length; */
      } else {
        return 'N/A';
      }
    },
    flowAnomData(flowAnomData) {
      if (flowAnomData) {
        this.cardData[3].val = flowAnomData.count;
      } else {
        return 'N/A';
      }
    },
    segments(segments) {
      if (segments) {
        this.cardData[4].val = segments.filter(x => x.travelTime.level >= 5).length;
      } else {
        return 'N/A';
      }
    },
    waze(waze) {
      if (waze) {
        this.cardData[5].val = waze.length;
      } else {
        return 'N/A';
      }
    }
  }
};
</script>

<style>
.card-container {
  position: relative;
}
.link-button {
  position: absolute;
  bottom: -6vh;
  right: 8px;
}
.detail-button {
  position: absolute;
  bottom: calc(-6vh + 4px);
  top: 22px;
  right: 40px;
}
.show-btns {
  color: rgba(255, 255, 255, 1) !important;
}
.show-btns-light {
  color: rgba(0, 0, 0, 1) !important;
}
</style>

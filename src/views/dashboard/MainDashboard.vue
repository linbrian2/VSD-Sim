<template>
  <div class="status">
    <v-main class="pa-0">
      <v-row>
        <!-- Resizable Map -->
        <template v-if="rzMap">
          <div class="map">
            <SelectionPanel :width="420">
              <Map
                :apiInfo="apiInfo"
                :markers="markers"
                :segments="polylines"
                :icons="icons"
                :disableDefaultUI="true"
                :height="'calc(100vh - 48px)'"
                :selectedIdx="selectedIdx"
              />
            </SelectionPanel>
          </div>
        </template>
        <!-- Overview -->
        <v-col id="overview" :cols="rzMap ? 4 : 3" :xl="rzMap ? 3 : 2" class="pa-0 px-3 pl-3">
          <v-container style="max-height: calc(100vh - 48px); overflow-y: auto;" class="py-0">
            <v-row>
              <!-- TODO -->
              <!-- <v-card class="d-flex align-center justify-center">
                <v-progress-linear :value="cardProgress" />
              </v-card> -->
              <v-col cols="12" v-for="(x, i) in cardData" :key="x.id" class="pa-1">
                <v-hover v-slot="{ hover }">
                  <v-sheet outlined :color="i == selectedIdx ? 'blue' : 'transparent'" rounded>
                    <v-sheet outlined :color="i == selectedIdx ? 'blue' : 'transparent'" rounded>
                      <v-sheet outlined :color="i == selectedIdx ? 'blue' : 'transparent'" rounded>
                        <v-sheet outlined :color="i == selectedIdx ? 'blue' : 'transparent'" rounded>
                          <v-card
                            :disabled="!x.val || (x.val && (x.val == 0 || x.val == '-'))"
                            height="calc(19vh - 48px)"
                            class="d-flex align-center justify-center"
                            @click.native="cardClicked(i)"
                            :color="getColor(x)"
                            :elevation="hover ? 12 : 2"
                            :class="{ 'on-hover': hover }"
                          >
                            <v-col class="grid-center pa-0">
                              <v-card-title v-show="$vuetify.breakpoint.lgAndUp" class="py-0" style="font-size:26px">
                                {{ x.title }}
                              </v-card-title>
                              <h1 style="font-size:48px" class="pr-2">
                                <v-icon class="pb-2 pr-3" :color="colors[i]" x-large>{{ x.icon }}</v-icon>
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
                        </v-sheet>
                      </v-sheet>
                    </v-sheet>
                  </v-sheet>
                </v-hover>
              </v-col>
            </v-row>
          </v-container>
        </v-col>
        <!-- Data & Map -->
        <v-col class="pa-0" :cols="rzMap ? 8 : 9" :xl="rzMap ? 9 : 10">
          <v-row>
            <!-- Data -->
            <v-col id="data" :cols="rzMap ? 12 : 6" class="pa-0" v-if="layout == '1: Card, 2: Info, 3: Map'">
              <InfoColumn ref="infoColumn" :apiInfo="apiInfo" :selectedIdx="selectedIdx" :cardData="cardData" />
            </v-col>
            <!-- Map -->
            <v-col id="map" cols="6" class="pa-0" v-if="!rzMap">
              <div class="map">
                <Map
                  :apiInfo="apiInfo"
                  :markers="markers"
                  :segments="polylines"
                  :icons="icons"
                  :height="'calc(100vh - 48px)'"
                  :selectedIdx="selectedIdx"
                />
              </div>
            </v-col>
            <v-col id="data" :cols="rzMap ? 12 : 6" class="pa-0" v-if="layout == '1: Card, 2: Map, 3: Info'">
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
import SelectionPanel from '@/components/modules/dashboard/app/SelectionPanel.vue';
import SelectionDialog from '@/components/modules/dashboard/SelectionDialog.vue';
import Constants from '@/utils/constants/dashboard.js';
import { mapActions, mapGetters, mapState } from 'vuex';

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
      cardElapsedTime: 38,
      cardData: [
        {
          title: Constants.TRAFFIC_INCIDENTS,
          icon: Constants.TRAFFIC_INCIDENTS_ICON,
          link: 'http://http://aitoms.net/flow/incidents',
          val: '-',
          thresholds: [
            { val: 0, color: 'rgba(42, 215, 40, 0.35)' },
            { val: 1, color: '#339900' },
            { val: 3, color: '#D7DF01' },
            { val: 5, color: '#FFCC55' },
            { val: 7, color: '#FF6600' },
            { val: 9, color: '#FF0000' }
          ]
        },
        {
          title: Constants.DEVICE_TRAFFIC,
          icon: Constants.DEVICE_TRAFFIC_ICON,
          link: 'http://aitoms.net/',
          val: '-',
          thresholds: [
            { val: 0, color: 'rgba(42, 215, 40, 0.35)' },
            { val: 1, color: '#339900' },
            { val: 2, color: '#D7DF01' },
            { val: 4, color: '#FFCC55' },
            { val: 6, color: '#FF6600' },
            { val: 8, color: '#FF0000' }
          ]
        },
        {
          title: Constants.SIGNAL_PERFORMANCE_ISSUES,
          icon: Constants.SIGNAL_PERFORMANCE_ISSUES_ICON,
          link: 'http://aitoms.net/hr/summary',
          val: '-',
          thresholds: [
            { val: 0, color: 'rgba(42, 215, 40, 0.35)' },
            { val: 1, color: '#339900' },
            { val: 4, color: '#D7DF01' },
            { val: 6, color: '#FFCC55' },
            { val: 8, color: '#FF6600' },
            { val: 10, color: '#FF0000' }
          ]
        },
        {
          title: Constants.TRAFFIC_FLOW_ANOMALIES,
          icon: Constants.TRAFFIC_FLOW_ANOMALIES_ICON,
          link: 'http://aitoms.net/flow/bt-dash',
          val: '-',
          thresholds: [
            { val: 0, color: 'rgba(42, 215, 40, 0.35)' },
            { val: 1, color: '#339900' },
            { val: 4, color: '#D7DF01' },
            { val: 6, color: '#FFCC55' },
            { val: 8, color: '#FF6600' },
            { val: 10, color: '#FF0000' }
          ]
        },
        {
          title: Constants.HIGH_CONGESTION_ROUTES,
          icon: Constants.HIGH_CONGESTION_ROUTES_ICON,
          link: 'http://aitoms.net/flow/bt-dash',
          val: '-',
          thresholds: [
            { val: 0, color: 'rgba(42, 215, 40, 0.35)' },
            { val: 1, color: '#339900' },
            { val: 3, color: '#D7DF01' },
            { val: 6, color: '#FFCC55' },
            { val: 9, color: '#FF6600' },
            { val: 12, color: '#FF0000' }
          ]
        },
        {
          title: Constants.REPORTED_WAZE_ALERTS,
          icon: Constants.REPORTED_WAZE_ALERTS_ICON,
          link: 'http://aitoms.net/flow/bt-dash',
          val: '-',
          thresholds: [
            { val: 0, color: 'rgba(42, 215, 40, 0.35)' },
            { val: 1, color: '#339900' },
            { val: 20, color: '#D7DF01' },
            { val: 35, color: '#FFCC55' },
            { val: 50, color: '#FF6600' },
            { val: 65, color: '#FF0000' }
          ]
        }
      ],
      colors: ['white', 'white', 'white', 'white', 'white', 'white'],
      selectedIdx: -1,
      transparent: 'rgba(255, 255, 255, 0)'
    };
  },
  computed: {
    incidentData() {
      return this.segments != null && this.trafficIncidents != null;
    },
    cardProgress() {
      return this.cardElapsedTime < 0 ? 0 : (this.cardElapsedTime / 45) * 100;
    },
    rzMap() {
      return this.getSetting('mainDashboard', 'resizableMap');
    },
    layout() {
      return this.getSetting('mainDashboard', 'pageLayout');
    },
    ...mapState('dashboard', [
      'pref',
      'weatherStations',
      'trafficIncidents',
      'trafficDevices',
      'signalPerformanceIssues',
      'flowAnomData',
      'hrSummary',
      'detectors',
      'segments',
      'waze'
    ]),
    ...mapGetters(['getSetting'])
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
    this.$bus.$on('SET_DASHBOARD_MARKERS', markers => {
      this.setMarkers(markers);
    });
    this.$bus.$on('SET_DASHBOARD_POLYLINES', polylines => {
      this.setPolylines(polylines);
    });
  },
  beforeDestroy() {
    this.stopUpdateInterval();
    this.stopCardSwapInterval();
  },
  methods: {
    dataAvailable(data) {
      return data.val && data.val != 0 && data.val != '-' && data.val != 'N/A';
    },
    setMarkers(markers) {
      if (markers) {
        this.markers = markers;
      }
    },
    setPolylines(polylines) {
      if (polylines) {
        this.polylines = polylines;
      }
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
      this.fetchBluetoothSegments();
      this.fetchWeatherStations();
      this.fetchTrafficIncidents();
      this.fetchTrafficDevices();
      this.fetchSignalPerformanceIssues();
      this.fetchStatus();
      this.fetchStatusOfDevices();
      this.fetchSegments();
      this.fetchWaze();
    },
    updateData() {
      this.elapsedTime++;
      if (
        this.getSetting('mainDashboard', 'autoDataUpdate') &&
        this.elapsedTime >= this.getSetting('mainDashboard', 'dataUpdateInterval') * 60
      ) {
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
      let pageSwapEnabled = this.getSetting('mainDashboard', 'autoPageSwaps');
      let swapInterval = this.getSetting('mainDashboard', 'swapInterval');
      if (!this.manualMode && pageSwapEnabled && this.cardElapsedTime >= swapInterval) {
        this.cardElapsedTime = 0;
        if (this.selectedIdx >= 0 && this.selectedIdx <= 4) {
          for (let i = this.selectedIdx + 1; i < this.cardData.length; i++) {
            if (this.dataAvailable(this.cardData[i])) {
              this.selectedIdx = i;
              this.cardClicked(this.selectedIdx);
              return;
            }
          }
        }
        for (let i = 0; i < this.cardData.length; i++) {
          if (this.dataAvailable(this.cardData[i])) {
            this.selectedIdx = i;
            this.cardClicked(this.selectedIdx);
            return;
          }
        }
      }
    },
    keydownListener() {
      this.cardElapsedTime = -75;
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
          this.polylines = undefined;
          this.icons = undefined;
          break;
        case 1:
          this.markers = this.trafficDevices;
          this.polylines = undefined;
          this.icons = undefined;
          break;
        case 2:
          this.markers = this.signalPerformanceIssues;
          this.polylines = undefined;
          this.icons = this.getHRIcons();
          break;
        case 3:
          this.markers = Devices;
          this.polylines = undefined;
          this.icons = undefined;
          break;
        case 4:
          this.markers = undefined;
          this.polylines = this.segments;
          this.icons = undefined;
          break;
        case 5:
          this.markers = this.waze;
          this.polylines = undefined;
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
      'fetchStatus',
      'fetchStatusOfDevices',
      'fetchSegments',
      'fetchWaze'
    ]),
    ...mapActions('traffic', ['fetchBluetoothSegments'])
  },
  watch: {
    incidentData(incidents) {
      if (incidents) {
        this.cardData[0].val = this.trafficIncidents.length;
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
        this.cardData[2].val = this.hrSummary.filter(x => x.score > 60).length;
      } else {
        return 'N/A';
      }
    },
    flowAnomData(flowAnomData) {
      if (flowAnomData) {
        let maxCount = flowAnomData.sensorErrorCounts[0].score;
        let hours = new Date().getHours()
        this.cardData[3].val = flowAnomData.sensorErrorCounts.filter(
          x => x.score / hours > 50 && x.score > maxCount - maxCount * 0.15
        ).length;
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
        this.cardData[5].val = waze.filter(x => x.confidence >= 5).length;
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

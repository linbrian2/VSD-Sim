<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
    <v-card class="mx-auto" elevation="0" style="position: absolute; top: 60px; left:20px; " color="rgb(0, 0, 0, 0.0)">
      <v-col v-for="x in sortedCardData" :key="x.id" class="pa-0">
        <v-row cols="12" v-if="!!((x && x.val) || showAllOverlayCards)">
          <v-hover v-slot="{ hover }">
            <v-sheet outlined :color="x.id == selectedIdx ? 'white' : 'transparent'">
              <v-sheet outlined :color="x.id == selectedIdx ? 'white' : 'transparent'">
                <v-sheet outlined :color="x.id == selectedIdx ? 'white' : 'transparent'">
                  <v-sheet outlined :color="x.id == selectedIdx ? 'white' : 'transparent'">
                    <v-tooltip right>
                      <template v-slot:activator="{ on: tooltip }">
                        <v-card
                          tile
                          class="d-flex align-center justify-center"
                          :height="$vuetify.breakpoint.mobile ? '40px' : '60px'"
                          :width="$vuetify.breakpoint.mobile ? '80px' : '110px'"
                          @click.native="cardClicked(x.id)"
                          :color="getColor(x)"
                          :elevation="hover ? 12 : 2"
                          :class="{ 'on-hover': hover }"
                          v-on="{ ...tooltip }"
                        >
                          <v-col class="grid-center pa-0">
                            <v-card-title
                              class="pa-0"
                              :style="`font-size:${$vuetify.breakpoint.mobile ? '26px' : '38px'}`"
                            >
                              <v-icon class="pr-2" color="white" :large="!$vuetify.breakpoint.mobile">
                                {{ x.icon }}
                              </v-icon>
                              <template v-if="x.val == null || (x.val && x.val == '-')">
                                <div class="pt-3"></div>
                                <Spinner />
                              </template>
                              <template v-else>
                                <h4 :style="'color: white;'">{{ x.val }}</h4>
                              </template>
                            </v-card-title>
                          </v-col>
                          <div v-show="hover" class="card-container">
                            <v-btn icon @click="goToPage(x.link)" class="link-button">
                              <v-icon small :class="{ 'show-btns': hover }" color="transparent">
                                mdi-open-in-new
                              </v-icon>
                            </v-btn>
                          </div>
                        </v-card>
                      </template>
                      <span>{{ x.title }}</span>
                    </v-tooltip>
                  </v-sheet>
                </v-sheet>
              </v-sheet>
            </v-sheet>
          </v-hover>
        </v-row>
      </v-col>
    </v-card>
  </div>
  <div v-else>
    <v-card class="mx-auto" elevation="0" style="position: absolute; top: 55px; left:10px; " color="rgb(0, 0, 0, 0.0)">
      <v-chip-group>
        <v-chip
          v-for="x in sortedCardData"
          :key="x.id"
          filter
          small
          class="px-2 mr-1"
          :color="getColor(x)"
          @click.native="cardClicked(x.id)"
          :active="!!((x && x.val) || showAllOverlayCards)"
        >
          <template v-if="!!((x && x.val) || showAllOverlayCards)">
            <v-icon class="pr-1" color="white" small>
              {{ x.icon }}
            </v-icon>
            <template v-if="x && x.val != '-'">
              {{ x.val }}
            </template>
          </template>
          <template v-if="x.val == null || (x.val && x.val == '-')">
            <div class="pt-3"></div>
            <Spinner :size="12" />
          </template>
        </v-chip>
      </v-chip-group>
    </v-card>
  </div>
</template>

<script>
import Constants from '@/utils/constants/dashboard.js';
import Spinner from '@/components/common/Spinner.vue';
import { RouterPaths } from '@/utils/constants/router';
import { mapActions, mapGetters, mapState } from 'vuex';

export default {
  components: {
    Spinner
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
      updateInterval: null,
      cardSwapInterval: null,
      elapsedTime: 0,
      cardElapsedTime: 38,
      cardData: [
        {
          id: Constants.CARD_DATA_INCIDENTS_ID,
          title: Constants.TRAFFIC_INCIDENTS,
          icon: Constants.TRAFFIC_INCIDENTS_ICON,
          link: RouterPaths.TRAFFIC_INCIDENT_LIST,
          val: '-',
          displayOrder: 0,
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
          id: Constants.CARD_DATA_FLOW_ANOMALIES_ID,
          title: Constants.TRAFFIC_FLOW_ISSUES,
          icon: Constants.TRAFFIC_FLOW_ISSUES_ICON,
          link: RouterPaths.TRAFFIC_MULTIGRAPH,
          val: '-',
          displayOrder: 2,
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
          id: Constants.CARD_DATA_SIGNAL_ISSUES_ID,
          title: Constants.SIGNAL_PERFORMANCE_ISSUES,
          icon: Constants.SIGNAL_PERFORMANCE_ISSUES_ICON,
          link: RouterPaths.HR_DASHBOARD,
          val: '-',
          displayOrder: 3,
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
          id: Constants.CARD_DATA_DEDVICE_ANOMALIES_ID,
          title: Constants.DEVICE_ANOMALIES,
          icon: Constants.DEVICE_CABINET_ICON,
          link: RouterPaths.STATUS_QUALITY_MAP,
          val: '-',
          displayOrder: 5,
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
          id: Constants.CARD_DATA_CONGESTED_ROUTES_ID,
          title: Constants.HIGH_CONGESTION_ROUTES,
          icon: Constants.HIGH_CONGESTION_ROUTES_ICON,
          link: RouterPaths.TRAFFIC_DATA_PLAYBACK,
          val: '-',
          displayOrder: 1,
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
          id: Constants.CARD_DATA_WAZE_ALERTS_ID,
          title: Constants.REPORTED_WAZE_ALERTS,
          icon: Constants.REPORTED_WAZE_ALERTS_ICON,
          link: RouterPaths.TRAFFIC_DATA_PLAYBACK,
          val: '-',
          displayOrder: 4,
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
      selectedIdx: -1,
      transparent: 'rgba(255, 255, 255, 0)'
    };
  },
  computed: {
    showAllOverlayCards() {
      return this.getSetting('dashboard', 'showAllOverlayCards');
    },
    incidentData() {
      return this.congestedSegments != null && this.trafficIncidents != null;
    },

    sortedCardData() {
      const sorted = [...this.cardData].sort((a, b) => a.displayOrder - b.displayOrder);
      return sorted;
    },

    ...mapState('dashboard', [
      'weatherStations',
      'trafficIncidents',
      'trafficDevices',
      'signalPerformanceIssues',
      'flowAnomData',
      'hrSummary',
      'detectors',
      'congestedSegments',
      'waze'
    ]),
    ...mapGetters(['getSetting'])
  },

  created() {
    window.addEventListener('click', this.keydownListener);
  },

  destroyed() {
    window.removeEventListener('click', this.keydownListener);
    this.stopCardSwapInterval();
  },

  mounted() {
    this.startUpdateInterval();
    this.startCardSwapInterval();
    this.initExistingData();
    this.fetchApiData();

    this.$bus.$on('SHOW_SELECTION_POPUP', id => {
      this.showSelectionDialog(id);
    });

    this.$bus.$on('DISPLAY_MARKER_DETAILS_DASHBOARD', ({ id, type }) => {
      this.handleMarkerClick(id, type);
    });

    this.$bus.$on('DISPLAY_MARKER_DETAILS', () => {
      this.selectedIdx = -1;
    });

    this.$bus.$on('SET_DASHBOARD_MARKERS', markers => {
      this.setMarkers(markers);
    });

    this.$bus.$on('SET_DASHBOARD_POLYLINES', polylines => {
      this.setPolylines(polylines);
    });
  },
  methods: {
    keydownListener() {
      this.cardElapsedTime = -75;
    },

    cardClicked(i) {
      let param = this.cardData[i];
      if (!param.val || (param.val && (param.val == 0 || param.val == '-'))) {
        return;
      } else {
        this.selectedIdx = this.selectedIdx == i ? -1 : i;
        this.$emit('cardClicked', { idx: this.selectedIdx, cardData: this.cardData });
      }
    },

    goToPage(link) {
      const route = window.location.origin + link;
      //console.log(route);
      window.open(route, '_blank');
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

    initExistingData() {
      if (this.incidentData) {
        this.cardData[Constants.CARD_DATA_INCIDENTS_ID].val = this.trafficIncidents.length;
      }

      if (this.flowAnomData) {
        let maxCount = this.flowAnomData.sensorErrorCounts[0].score;
        let hours = new Date().getHours();
        this.cardData[Constants.CARD_DATA_FLOW_ANOMALIES_ID].val = this.flowAnomData.sensorErrorCounts.filter(
          x => x.score / hours > 50 && x.score > maxCount - maxCount * 0.15
        ).length;
      }

      if (this.hrSummary) {
        this.cardData[Constants.CARD_DATA_SIGNAL_ISSUES_ID].val = this.hrSummary.filter(x => x.score > 60).length;
      }

      if (this.trafficDevices) {
        this.cardData[Constants.CARD_DATA_DEDVICE_ANOMALIES_ID].val = this.trafficDevices.filter(
          x => x.status !== 0
        ).length;
      }

      if (this.congestedSegments) {
        this.cardData[Constants.CARD_DATA_CONGESTED_ROUTES_ID].val = this.congestedSegments.filter(
          x => x.travelTime.level >= 5
        ).length;
      }

      if (this.waze) {
        this.cardData[Constants.CARD_DATA_WAZE_ALERTS_ID].val = this.waze.filter(x => x.confidence >= 5).length;
      }
    },

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
      this.fetchCongestedSegments(5);
      this.fetchWaze();
    },

    updateData() {
      this.elapsedTime++;
      if (
        this.getSetting('dashboard', 'autoDataUpdate') &&
        this.elapsedTime >= this.getSetting('dashboard', 'dataUpdateInterval') * 60
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
      let pageSwapEnabled = this.getSetting('dashboard', 'autoPageSwaps');
      let swapInterval = this.getSetting('dashboard', 'swapInterval');

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
            this.congestedSegments
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
    ...mapActions('dashboard', [
      'fetchWeatherStations',
      'fetchTrafficIncidents',
      'fetchTrafficDevices',
      'fetchSignalPerformanceIssues',
      'fetchStatus',
      'fetchStatusOfDevices',
      'fetchCongestedSegments',
      'fetchWaze'
    ]),
    ...mapActions('traffic', ['fetchBluetoothSegments'])
  },

  watch: {
    incidentData(incidents) {
      if (incidents) {
        this.cardData[Constants.CARD_DATA_INCIDENTS_ID].val = this.trafficIncidents.length;
      } else {
        return 'N/A';
      }
    },

    flowAnomData(flowAnomData) {
      if (flowAnomData) {
        let maxCount = flowAnomData.sensorErrorCounts[0].score;
        let hours = new Date().getHours();
        this.cardData[Constants.CARD_DATA_FLOW_ANOMALIES_ID].val = flowAnomData.sensorErrorCounts.filter(
          x => x.score / hours > 50 && x.score > maxCount - maxCount * 0.15
        ).length;
      } else {
        return 'N/A';
      }
    },

    hrSummary(hrSummary) {
      if (hrSummary) {
        this.cardData[Constants.CARD_DATA_SIGNAL_ISSUES_ID].val = this.hrSummary.filter(x => x.score > 60).length;
      } else {
        return 'N/A';
      }
    },

    trafficDevices(trafficDevices) {
      if (trafficDevices) {
        this.cardData[Constants.CARD_DATA_DEDVICE_ANOMALIES_ID].val = trafficDevices.filter(x => x.status !== 0).length;
      } else {
        return 'N/A';
      }
    },

    congestedSegments(segments) {
      if (segments) {
        this.cardData[Constants.CARD_DATA_CONGESTED_ROUTES_ID].val = segments.filter(
          x => x.travelTime.level >= 5
        ).length;
      } else {
        return 'N/A';
      }
    },

    waze(waze) {
      if (waze) {
        this.cardData[Constants.CARD_DATA_WAZE_ALERTS_ID].val = waze.filter(x => x.confidence >= 5).length;
      } else {
        return 'N/A';
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.link-button {
  position: absolute;
  bottom: 4px;
  right: -8px;
}
.card-container {
  position: relative;
}

.show-btns {
  color: rgba(255, 255, 255, 1) !important;
}
</style>

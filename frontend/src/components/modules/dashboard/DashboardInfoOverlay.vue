<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
    <v-card class="mx-auto" elevation="0" style="position: absolute; top: 60px; left:20px; " color="rgb(0, 0, 0, 0.0)">
      <v-col v-for="x in sortedCardData" :key="x.id" class="pa-0">
        <v-row cols="12" v-if="!!((x && x.val) || showAllOverlayCards)">
          <v-hover v-slot="{ hover }">
            <v-sheet outlined class="mb-1" color="black">
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
                    :style="cardBorder(x.id)"
                    v-on="{ ...tooltip }"
                  >
                    <v-col
                      class="grid-center pa-0 mx-3 d-flex justify-space-between align-center"
                      :style="`font-size:${$vuetify.breakpoint.mobile ? '26px' : '32px'}`"
                    >
                      <v-icon class="pr-2" color="white" :large="!$vuetify.breakpoint.mobile">
                        {{ x.icon }}
                      </v-icon>

                      <div>
                        <template v-if="x.val == null || (x.val && x.val == '-')">
                          <div class="pt-3"></div>
                          <Spinner />
                        </template>
                        <template v-else>
                          <h4 :style="'color: white;'">
                            <v-fade-transition leave-absolute>
                              <span :key="`counts-${x.val}`">
                                {{ x.val }}
                              </span>
                            </v-fade-transition>
                          </h4>
                        </template>
                      </div>
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
import Utils from '@/utils/Utils.js';
import Constants from '@/utils/constants/dashboard.js';
import Spinner from '@/components/common/Spinner.vue';
import CardData from '@/components/modules/dashboard/CardData.js';
import { mapActions, mapGetters, mapState } from 'vuex';

export default {
  components: {
    Spinner
  },
  data() {
    return {
      manualMode: false,
      updateInterval: null,
      cardSwapInterval: null,
      dataUpdateInterval: 60000, // 60 seconds
      elapsedTime: 0,
      cardColorAlpha: 0.9,
      cardElapsedTime: 38,
      cardData: CardData,
      selectedIdx: -1,
      transparent: 'rgba(255, 255, 255, 0)'
    };
  },

  computed: {
    showAllOverlayCards() {
      return this.getSetting('dashboard', 'showAllOverlayCards');
    },

    sortedCardData() {
      const sorted = [...this.cardData].sort((a, b) => a.displayOrder - b.displayOrder);
      return sorted;
    },

    ...mapState('dashboard', [
      'trafficIncidents',
      'trafficRestrictions',
      'signalIssues',
      'deviceAnomalies',
      'congestedSegments',
      'wazeAlerts'
    ]),
    ...mapGetters(['getSetting'])
  },

  created() {
    window.addEventListener('click', this.keydownListener);
  },

  destroyed() {
    window.removeEventListener('click', this.keydownListener);
    this.stopCardSwapInterval();
    this.stopUpdateInterval();
  },

  mounted() {
    this.$bus.$on('FETCH_DASHBOARD_DATA', () => {
      this.fetchApiData();
    });

    this.startUpdateInterval();
    this.startCardSwapInterval();
    this.initExistingData();
    this.fetchApiData();
  },

  methods: {
    keydownListener() {
      this.cardElapsedTime = -75;
    },

    cardBorder(id) {
      return id == this.selectedIdx ? 'border: 3px solid red;' : 'border: 0px solid transparent;';
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
      window.open(route, '_blank');
    },

    getColor(param) {
      if (!param || !param.thresholds || param.val == '-') {
        return 'black';
      }

      let idx = param.thresholds.length - 1;
      for (let i = 0; i < param.thresholds.length - 1; i++) {
        if (param.thresholds[i].val <= param.val && param.val < param.thresholds[i + 1].val) {
          idx = i;
        }
      }

      return Utils.hexToRGBA(param.thresholds[idx].color, this.cardColorAlpha);
    },

    initExistingData() {
      for (let i = 0; i < this.cardData.length; i++) {
        this.cardData[i].val = 0;
      }
    },

    dataAvailable(data) {
      return data.val && data.val != 0 && data.val != '-' && data.val != 'N/A';
    },

    startUpdateInterval() {
      this.updateInterval = setInterval(this.fetchApiData, this.dataUpdateInterval);
    },

    stopUpdateInterval() {
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.updateInterval = null;
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

    async fetchApiData() {
      // Start loading data
      this.$store.commit('SET_REFRESH_LOADING', true);

      await Promise.all([
        this.fetchTrafficIncidents(),
        this.fetchTrafficDevices(),
        this.fetchTrafficRestrictions(),
        this.fetchSignalIssues(),
        this.fetchAnomalyDevices(80),
        this.fetchCongestedSegments(5),
        this.fetchWazeAlerts()
      ]);

      // Update last updated time
      this.$store.commit('SET_LAST_UPDATED_TIME', new Date());
      this.$store.commit('SET_REFRESH_LOADING', false);
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

    ...mapActions('dashboard', [
      'fetchTrafficIncidents',
      'fetchTrafficDevices',
      'fetchTrafficRestrictions',
      'fetchSignalIssues',
      'fetchAnomalyDevices',
      'fetchCongestedSegments',
      'fetchWazeAlerts'
    ])
  },

  watch: {
    trafficIncidents(trafficIncidents) {
      if (trafficIncidents) {
        this.cardData[Constants.CARD_DATA_INCIDENTS_ID].val = trafficIncidents.length;
      }
    },

    deviceAnomalies(deviceAnomalies) {
      if (deviceAnomalies) {
        this.cardData[Constants.CARD_DATA_DEDVICE_ANOMALIES_ID].val = deviceAnomalies.length;
      }
    },

    signalIssues(signalIssues) {
      if (signalIssues) {
        this.cardData[Constants.CARD_DATA_SIGNAL_ISSUES_ID].val = this.signalIssues.length;
      }
    },

    trafficRestrictions(trafficRestrictions) {
      if (trafficRestrictions) {
        this.cardData[Constants.CARD_DATA_RESTRICTIONS_ID].val = trafficRestrictions.length;
      }
    },

    congestedSegments(segments) {
      if (segments) {
        this.cardData[Constants.CARD_DATA_CONGESTED_ROUTES_ID].val = segments.length;
      }
    },

    wazeAlerts(wazeAlerts) {
      if (wazeAlerts) {
        this.cardData[Constants.CARD_DATA_WAZE_ALERTS_ID].val = wazeAlerts.length;
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

<template>
  <div>
    <TitleBar title="Performance Measures" :loading="loading" :refresh="fetchData">
      <div style="width:280px" class="mt-2">
        <v-combobox flat dense small-chips hide-details multiple single-line :items="phases" v-model="select">
          <template v-slot:prepend>
            <v-icon class="mt-n1">mdi-road</v-icon>
          </template>
        </v-combobox>
      </div>
    </TitleBar>
    <v-container fluid :style="{ 'max-width': maxWidth }">
      <v-card class="mt-1">
        <v-card-title :class="panelStyle">
          <v-chip color="cyan" small>
            <span class="font-weight-bold"> {{ activeMarker.id }}</span>
          </v-chip>
          <span class="ml-3 title font-weight-light">{{ activeMarker.name }}</span>
        </v-card-title>
        <v-img :src="imageSrc" aspect-ratio="2.5" style="height:280px"> </v-img>
      </v-card>

      <v-card class="mt-6" v-show="ready">
        <v-card-title :class="panelStyle">
          <v-icon class="mr-2">mdi-format-list-bulleted</v-icon>
          <span class="title font-weight-light">Timing Plans</span>
          <v-btn x-small icon class="refresh-btn" @click.stop="fetchSingleData(0)" :loading="loadingPlan">
            <v-icon>mdi-refresh</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <TimingPlans :phases="phases" :plans="plans" />
        </v-card-text>
      </v-card>

      <v-card class="mt-6">
        <v-card-title :class="panelStyle">
          <v-icon class="mr-2">mdi-traffic-light</v-icon>
          <span class="title font-weight-light">Arrival on Red</span>
          <v-btn x-small icon class="refresh-btn" @click.stop="fetchSingleData(1)" :loading="loadingAoR">
            <v-icon>mdi-refresh</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-subtitle class="mt-3 font-italic">
          Arrivals on Red provide the proportion of vehicles that arrive to a red light versus the proportion that
          arrive to a green light. This provides an estimate of total stops, and identifies the quality of the offsets
          between two coordinated intersections.
        </v-card-subtitle>
        <v-card-text>
          <div class="mb-2" v-for="(aor, index) in aors" :key="index">
            <AorChart :data="aor.data" :title="aor.title" :height="chartHeight(aors.length)" v-if="!reload" />
          </div>
        </v-card-text>
      </v-card>

      <v-card class="mt-6">
        <v-card-title :class="panelStyle">
          <v-icon class="mr-2">mdi-dice-d20-outline</v-icon>
          <span class="title font-weight-light">Approach Volume</span>
          <v-btn x-small icon class="refresh-btn" @click.stop="fetchSingleData(2)" :loading="loadingVolume">
            <v-icon>mdi-refresh</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-subtitle class="mt-3 font-italic">
          Approach volumes provide the number of vehicles arriving at an intersection on each approach per 5 minutes
          over a 24-hour period.
        </v-card-subtitle>
        <v-card-text>
          <div class="mb-2" v-for="(volume, index) in volumes" :key="index">
            <BasicChart :data="volume" :height="chartHeight(volumes.length)" v-if="!reload" />
          </div>
        </v-card-text>
      </v-card>

      <v-card class="mt-6">
        <v-card-title :class="panelStyle">
          <v-icon class="mr-2">mdi-chart-timeline</v-icon>
          <span class="title font-weight-light">Phase Interval</span>
          <v-btn x-small icon class="refresh-btn" @click.stop="fetchSingleData(3)" :loading="loadingInterval">
            <v-icon>mdi-refresh</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-subtitle class="mt-3 font-italic">
          Phase Interval provides cycle by cycle Red / Green / Yellow durations over a 24-hour period.
        </v-card-subtitle>
        <v-card-text>
          <div class="mb-2" v-for="(interval, index) in intervals" :key="index">
            <IntervalChart
              :data="interval.data"
              :title="interval.title"
              :phase="interval.phase"
              :height="chartHeight(intervals.length)"
              v-if="!reload"
            />
          </div>
        </v-card-text>
      </v-card>

      <v-card class="mt-6">
        <v-card-title :class="panelStyle">
          <v-icon class="mr-2">mdi-car</v-icon>
          <span class="title font-weight-light">Simple Approach Delay</span>
          <v-btn x-small icon class="refresh-btn" @click.stop="fetchSingleData(4)" :loading="loadingDelay">
            <v-icon>mdi-refresh</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-subtitle class="mt-3 font-italic">
          Simple Approach Delay provides the time between detector actuation during the red phase and when the phase
          turns green.
        </v-card-subtitle>
        <v-card-text>
          <div class="mb-2" v-for="(delay, index) in delays" :key="index">
            <BasicChart :data="delay" :height="chartHeight(delays.length)" v-if="!reload" />
          </div>
        </v-card-text>
      </v-card>

      <v-card class="mt-6">
        <v-card-title class="d-flex justify-space-between" :class="panelStyle">
          <div>
            <v-icon class="mr-2">mdi-percent-outline</v-icon>
            <span class="title font-weight-light">Averaged Duration and Occupancy</span>
          </div>
          <v-spacer></v-spacer>

          <div style="width:140px; margin-right:100px">
            <v-select
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

          <div style="width: 220px; margin-right: 30px">
            <v-select
              flat
              dense
              hide-details
              single-line
              item-text="text"
              item-value="value"
              :items="typeItems"
              v-model="occType"
              @input="typeSelected"
              prepend-icon="mdi-format-list-bulleted"
            >
            </v-select>
          </div>

          <div style="margin-right: 30px" v-if="occType === 1">
            <v-checkbox
              class="mt-n1"
              v-model="fixedYAxisRange"
              label="Fixed Y Range"
              hide-details
              @change="setFixedYAxisRange"
            ></v-checkbox>
          </div>

          <v-btn x-small icon class="refresh-btn" @click.stop="fetchSingleData(5)" :loading="loadingOccupancy">
            <v-icon>mdi-refresh</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-subtitle class="mt-3 font-italic">
          Averaged Occupancy calculates the average vehicle occupancy percentage over five minutes interval based on the
          duration between ON and OFF status of loop detectors.
        </v-card-subtitle>
        <v-card-text>
          <BasicChart
            class="mb-2"
            v-for="(occupancy, index) in occupancies"
            :key="index"
            ref="occCharts"
            :data="occupancy"
            :height="chartHeight(occupancies.length)"
          />
        </v-card-text>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import Api from '@/utils/api/hr';
import Constants from '@/utils/constants/hr';
import TitleBar from '@/components/hr/TitleBar';
import TimingPlans from '@/components/hr/TimingPlans';
import AorChart from '@/components/hr/AorChart';
import BasicChart from '@/components/hr/BasicChart';
import IntervalChart from '@/components/hr/IntervalChart';

export default {
  components: {
    TitleBar,
    TimingPlans,
    AorChart,
    BasicChart,
    IntervalChart
  },
  data: () => ({
    reload: false,
    loading: false,
    loadingPlan: false,
    loadingAoR: false,
    loadingVolume: false,
    loadingInterval: false,
    loadingDelay: false,
    loadingOccupancy: false,

    fixedYAxisRange: false,

    colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4'],

    interval: 300,
    intervalItems: [
      { text: '1 min', value: 60 },
      { text: '2 mins', value: 120 },
      { text: '5 mins', value: 300 },
      { text: '10 mins', value: 600 }
    ],

    occType: 0,
    typeItems: [
      { text: 'Average duration', value: 0 },
      { text: 'Average occupancy', value: 1 }
    ],

    select: [],
    aors: [],
    volumes: [],
    intervals: [],
    delays: [],
    occupancies: []
  }),
  computed: {
    ready() {
      return this.activeSignal && 'plans' in this.activeSignal && Object.keys(this.activeSignal.plans).length > 0;
    },

    imageSrc() {
      let id = this.activeMarker ? this.activeMarker.id : 0;
      return require(`@/assets/images/${id}.jpg`);
    },

    activeSignal() {
      return (
        this.currentSignal || {
          id: 6,
          name: 'No Signal',
          phases: [],
          detectors: [],
          plans: []
        }
      );
    },

    phases() {
      let signal = this.activeMarker || { phases: [] };
      return signal.phases;
    },

    plans() {
      let plans = [];
      if ('plans' in this.activeSignal) {
        plans = Object.values(this.activeSignal.plans);
      }
      return plans;
    },

    panelStyle() {
      return this.darkMode ? 'pa-1 pl-3 grey darken-3' : 'pa-1 pl-3 primary';
    },

    maxWidth() {
      return this.showPanel ? '100%' : '85%';
    },
    ...mapState(['darkMode']),
    ...mapState('hr', ['showPanel', 'activeMarker', 'currentSignal', 'currentDate'])
  },

  watch: {
    currentDate() {
      this.fetchData();
    },

    phases() {
      this.presetPhase();
    },

    darkMode() {
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, 1);
    }
  },

  mounted() {
    this.$bus.$on('GET_PREF', ({ marker, time }) => {
      this.fetchPerformanceData(marker.id, time);
    });
    this.presetPhase();
    this.fetchData();
  },

  methods: {
    chartHeight(length) {
      return length == 1 ? 560 : 360;
    },

    setFixedYAxisRange() {
      if (this.fixedYAxisRange) {
        this.$refs.occCharts.forEach(chart => {
          chart.setYAxisRange(0, 100);
        });
      } else {
        this.clearChartYRange();
      }
    },

    clearChartYRange() {
      this.$refs.occCharts.forEach(chart => {
        chart.setYAxisRange(null, null);
      });
    },

    presetPhase() {
      this.select = [];
      if (this.phases.indexOf(2) >= 0) {
        this.select.push(2);
      } else if (this.phases.length > 0) {
        this.select.push(this.phases[0]);
      }

      if (this.phases.indexOf(6) >= 0) {
        this.select.push(6);
      } else if (this.phases.length > 1) {
        this.select.push(this.phases[1]);
      }
    },

    regulatePhaseSelection() {
      if (this.select.length == 0) {
        if (this.phases.indexOf(2) >= 0) {
          this.select.push(2);
        } else if (this.phases.length > 0) {
          this.select.push(this.phases[0]);
        }
      }
      // Sort the selections
      this.select.sort();
    },

    intervalSelected() {
      this.fetchSingleData(5);
    },

    typeSelected() {
      this.clearChartYRange();
      this.fetchSingleData(5);
    },

    fetchData() {
      let marker = this.activeMarker;
      let time = this.currentDate.getTime();
      if (marker != null) {
        this.fetchPerformanceData(marker.id, time);
      }
    },

    fetchSingleData(type) {
      let marker = this.activeMarker;
      let time = this.currentDate.getTime();
      if (marker != null) {
        let id = marker.id;
        this.fetchIndividualPerformanceData(type, id, time);
      }
    },

    fetchPerformanceData(id, time) {
      this.regulatePhaseSelection();
      this.fetchSignal(id, time);
      this.fetchAORs(id, this.select, time);
      this.fetchVolumes(id, this.select, time);
      this.fetchPhaseIterval(id, this.select, time);
      this.fetchSimpleDelay(id, this.select, time);
      this.fetchDetectorOccupancy(id, time, this.interval, this.occType);
    },

    fetchIndividualPerformanceData(type, id, time) {
      this.regulatePhaseSelection();
      switch (type) {
        case Constants.PREF_SIGNAL:
          this.fetchSignal(id, time);
          break;
        case Constants.PREF_AOR:
          this.fetchAORs(id, this.select, time);
          break;
        case Constants.PREF_VOLUME:
          this.fetchVolumes(id, this.select, time);
          break;
        case Constants.PREF_INTERVAL:
          this.fetchPhaseIterval(id, this.select, time);
          break;
        case Constants.PREF_DELAY:
          this.fetchSimpleDelay(id, this.select, time);
          break;
        case Constants.PREF_OCCUPANCY:
          this.fetchDetectorOccupancy(id, time, this.interval, this.occType);
          break;
      }
    },

    createSignals(deviceId, phases) {
      let series = [];
      phases.forEach(phaseId => {
        series.push([deviceId, phaseId]);
      });
      return series;
    },

    async fetchSignal(id, time) {
      this.loadingPlan = true;
      await this.fetchPlans({ id, time });
      this.loadingPlan = false;
    },

    fetchAORs(id, phases, time) {
      let series = this.createSignals(id, phases);
      try {
        this.loadingAoR = true;
        Api.fetchAORSet(series, time).then(response => {
          if (response.data.status === 'OK') {
            let aors = response.data.data;
            this.aors = [];
            Object.keys(aors).forEach(key => {
              let aor = aors[key];
              let item = {
                data: aor.cycles,
                title: `Arrival on Red (P${aor.phaseId})`
              };
              this.aors.push(item);
            });
          } else {
            this.$store.dispatch('hr/setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loadingAoR = false;
        });
      } catch (error) {
        this.$store.dispatch('hr/setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchVolumes(id, phases, time) {
      let series = this.createSignals(id, phases);
      try {
        this.loadingVolume = true;
        Api.fetchVolumeSet(series, time).then(response => {
          if (response.data.status === 'OK') {
            let volumes = response.data.data;
            this.volumes = [];
            Object.keys(volumes).forEach((key, idx) => {
              let v = volumes[key];
              let item = {
                data: v.volume,
                title: `Approach Volume (P${v.phaseId})`,
                ylabel: 'Volume (vehicles/5 minutes)',
                color: this.colors[idx]
              };
              this.volumes.push(item);
            });
          } else {
            this.$store.dispatch('hr/setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loadingVolume = false;
        });
      } catch (error) {
        this.$store.dispatch('hr/setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchPhaseIterval(id, phases, time) {
      try {
        this.loadingInterval = true;
        Api.fetchIntervals(id, phases, time).then(response => {
          if (response.data.status === 'OK') {
            let intervals = response.data.data;
            this.intervals = [];
            Object.keys(intervals).forEach(phaseId => {
              let interval = intervals[phaseId];
              let item = {
                data: interval,
                date: this.currentDate,
                phase: phaseId,
                title: `Phase Interval (P${phaseId})`
              };
              this.intervals.push(item);
            });
          } else {
            this.$store.dispatch('hr/setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loadingInterval = false;
        });
      } catch (error) {
        this.$store.dispatch('hr/setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchSimpleDelay(id, phases, time) {
      try {
        this.loadingDelay = true;
        Api.fetchDelays(id, phases, time).then(response => {
          if (response.data.status === 'OK') {
            this.delays = [];

            let delays = response.data.data;
            Object.keys(delays).forEach((phaseId, idx) => {
              let delay = delays[phaseId];
              let item = {
                data: delay,
                title: `Simple Approach Delay (${phaseId})`,
                ylabel: 'Cycle Delay (seconds)',
                color: this.colors[idx]
              };
              this.delays.push(item);
            });
          } else {
            this.$store.dispatch('hr/setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loadingDelay = false;
        });
      } catch (error) {
        this.$store.dispatch('hr/setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchDetectorOccupancy(id, time, interval, type) {
      const ylabel = type === Constants.TYPE_DURATION ? 'Average Duration (ms)' : 'Average Occupancy (%)';

      try {
        this.loadingOccupancy = true;
        Api.fetchDetectorOccupancy(id, time, interval, type).then(response => {
          if (response.data.status === 'OK') {
            this.occupancies = [];

            let occupancies = response.data.data;
            const total = Object.keys(occupancies).length;
            Object.keys(occupancies).forEach((detectorId, idx) => {
              let occupancy = occupancies[detectorId];
              let item = {
                data: occupancy,
                title: `Detector (${detectorId}) - ${this.getDetectorLaneDesc(detectorId, total)}`,
                ylabel,
                color: this.colors[idx]
              };
              this.occupancies.push(item);
            });
          } else {
            this.$store.dispatch('hr/setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loadingOccupancy = false;

          // Set fixed range
          if (type !== Constants.TYPE_DURATION && this.fixedYAxisRange) {
            setTimeout(() => {
              this.setFixedYAxisRange();
            }, 500);
          }
        });
      } catch (error) {
        this.$store.dispatch('hr/setSystemStatus', { text: error, color: 'error' });
      }
    },

    getDetectorLaneDesc(detectorId, total) {
      const LANE_DESC = {
        2: 'NB Left Lane',
        6: 'SB Left Lane',
        9: 'NB Right Lane',
        10: 'SB right Lane'
      };

      const LANE_DESC_1 = {
        2: 'NB',
        6: 'SB',
        9: 'NB',
        10: 'SB'
      };
      return total > 2 ? LANE_DESC[detectorId] : LANE_DESC_1[detectorId];
    },

    showInfo() {
      this.$store.commit('hr/SET_CURRENT_ACTION', Constants.PAGE_INFO);
      this.$router.push({ path: '/hr/info' }).catch(() => {});
    },
    arrivalOnRed() {
      this.$store.commit('hr/SET_CURRENT_ACTION', Constants.PAGE_AOR);
      this.$router.push({ path: '/hr/aor' }).catch(() => {});
    },
    approachVolume() {
      this.$store.commit('hr/SET_CURRENT_ACTION', Constants.PAGE_VOLUME);
      this.$router.push({ path: '/hr/volume' }).catch(() => {});
    },
    phaseInterval() {
      this.$store.commit('hr/SET_CURRENT_ACTION', Constants.PAGE_INTERVAL);
      this.$router.push({ path: '/hr/interval' }).catch(() => {});
    },
    simpleDelay() {
      this.$store.commit('hr/SET_CURRENT_ACTION', Constants.PAGE_DELAY);
      this.$router.push({ path: '/hr/delay' }).catch(() => {});
    },

    ...mapActions('hr', ['fetchPlans'])
  }
};
</script>

<style lang="scss" scoped>
.refresh-btn {
  margin-left: auto;
  margin-right: 5px;
}
</style>

<template>
  <div>
    <LoadingProgress :loading="loading" color="green accent-4" />
    <v-card>
      <v-toolbar dark color="#607C8A" dense flat fixed overflow extension-height="0" class="mt-n2">
        <v-toolbar-title>
          <v-btn dark icon @click="goBack">
            <v-icon>mdi-format-columns </v-icon>
          </v-btn>
          Corridor Average Occupancy Charts
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn small outlined class="mt-1" color="white" @click.stop="selectSignals">
          <v-icon left color="green accent-3">mdi-source-branch </v-icon>
          <span> Select Intersections </span>
        </v-btn>
        <v-spacer></v-spacer>
        <v-toolbar-items>
          <v-btn icon @click="goBack"><v-icon>mdi-close</v-icon></v-btn>
        </v-toolbar-items>
      </v-toolbar>

      <v-row>
        <template v-for="(occupancy, index) in occupancies">
          <v-col cols="6" :key="index">
            <BasicChart class="my-4" :key="index" :data="occupancy" :height="occupancies.length == 1 ? 520 : 360" />
          </v-col>
        </template>
      </v-row>
    </v-card>
    <SignalSelectionDialog v-model="showSelectionDlg" ref="signalDialog" :callback="fetchData" />
  </div>
</template>

<script>
import Api from '@/utils/api/hr';
import Constants from '@/utils/constants/hr';
import BasicChart from '@/components/modules/hr/BasicChart';
import LoadingProgress from '@/components/modules/hr/LoadingProgress';
import SignalSelectionDialog from '@/components/modules/hr/SignalSelectionDialog';
export default {
  components: {
    LoadingProgress,
    BasicChart,
    SignalSelectionDialog
  },
  data: () => ({
    loading: false,
    occupancies: [],
    baseIndex: 0,
    initPanelState: false,
    showSelectionDlg: false
  }),

  computed: {
    date() {
      return this.$store.state.currentDate;
    }
  },

  mounted() {
    setTimeout(() => {
      this.initPanelState = this.$store.state.hr.showPanel;
      this.$store.commit('hr/SHOW_PANEL', false);
    }, 100);

    setTimeout(() => {
      this.selectSignals();
    }, 1500);
  },

  beforeDestroy() {
    if (this.initPanelState) {
      this.$store.commit('hr/SHOW_PANEL', true);
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },

    selectSignals() {
      this.$refs.signalDialog.init();
      this.showSelectionDlg = true;
    },

    fetchData(signals) {
      const series = signals.map(signal => ({ id: signal[0] }));
      const time = this.date.getTime();
      this.getDetectorOccupancySet(series, time, 300, Constants.TYPE_OCCUPANCY);
    },

    getDetectorOccupancySet(series, time, interval, type) {
      const ylabel = type === Constants.TYPE_DURATION ? 'Average Duration (ms)' : 'Average Occupancy (%)';

      this.loading = true;
      Api.fetchDetectorOccupancySet(series, time, interval, type).then(
        response => {
          if (response.data.status === 'OK') {
            let plotLines = [
              {
                color: '#AAAAAA',
                width: 2,
                value: 50,
                dashStyle: 'Dot'
              }
            ];

            this.occupancies = [];
            let occupancies = response.data.data;
            const total = Object.keys(occupancies).length;
            occupancies.forEach((item, idx) => {
              const { deviceId, detectorId, data } = item;
              let entry = {
                data,
                title: `Signal ${deviceId} - Detector ${detectorId} - ${this.getDetectorLaneDesc(detectorId, total)}`,
                ylabel,
                color: Constants.COLORS[idx % 2],
                min: 0,
                max: 100,
                plotLines
              };
              this.occupancies.push(entry);
            });
          } else {
            this.$store.dispatch('setSystemStatus', { text: response.data.message, timeout: 0, color: 'error' });
          }
          this.loading = false;
        },
        error => {
          this.$store.dispatch('setSystemStatus', { text: error, timeout: 0, color: 'error' });
        }
      );
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
    }
  }
};
</script>

<style scoped>
.v-toolbar__extension {
  padding: 0px !important;
}
input[type='color']::-webkit-color-swatch-wrapper {
  padding: 0;
}
</style>

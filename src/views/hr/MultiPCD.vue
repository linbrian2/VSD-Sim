<template>
  <div>
    <LoadingProgress :loading="loading" color="green accent-4" />
    <v-card>
      <v-toolbar dark color="#607C8A" dense flat fixed overflow extension-height="0">
        <v-toolbar-title>
          <v-btn icon @click="goBack">
            <v-icon dark>mdi-format-columns</v-icon>
          </v-btn>
          Corridor PCD Charts
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn small outlined class="mt-1" color="white" @click.stop="selectSignals">
          <v-icon left color="green accent-3">mdi-source-branch </v-icon>
          <span> Select Intersections </span>
        </v-btn>
        <v-spacer></v-spacer>
        <v-toolbar-items>
          <v-btn icon dark @click="goBack"><v-icon>mdi-close</v-icon></v-btn>
        </v-toolbar-items>
      </v-toolbar>

      <v-row>
        <template v-for="(pcd, index) in pcds">
          <v-col cols="6" :key="index">
            <PcdChart
              class="my-4"
              :key="baseIndex + index"
              :info="pcd.info"
              :title="pcd.title"
              :date="pcd.date"
              :phase="pcd.phase"
              :height="pcds.length == 1 ? 520 : 360"
            />
          </v-col>
        </template>
      </v-row>
    </v-card>
    <PCDSignalSelectionDialog v-model="showSelectionDlg" ref="signalDialog" :callback="fetchData" />
  </div>
</template>

<script>
import Api from '@/utils/api/hr';
import PcdChart from '@/components/modules/hr/PcdChart';
import LoadingProgress from '@/components/modules/hr/LoadingProgress';
import PCDSignalSelectionDialog from '@/components/modules/hr/PCDSignalSelectionDialog';

export default {
  components: {
    LoadingProgress,
    PcdChart,
    PCDSignalSelectionDialog
  },

  data: () => ({
    loading: false,
    pcds: [],
    baseIndex: 0,
    initPanelState: false,
    showSelectionDlg: false
  }),

  created() {
    // this.showAll();
  },

  computed: {
    date() {
      return this.$store.state.currentDate;
    },
    markers() {
      return this.$store.state.hr.locations;
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
    selectSignals() {
      this.$refs.signalDialog.init();
      this.showSelectionDlg = true;
    },

    fetchData(signals) {
      // const series = signals.map(signal => ({ id: signal[0] }));
      // const time = this.date.getTime();
      // this.getDetectorOccupancySet(series, time, 300, Constants.TYPE_OCCUPANCY);

      //      let series = this.$store.state.hr.multiSignals;
      const time = this.date.getTime();
      this.getPCDData(signals, time);
    },

    goBack() {
      this.$router.go(-1);
    },

    showAll() {
      let series = this.$store.state.hr.multiSignals;
      const time = this.date.getTime();
      this.getPCDData(series, time);
    },

    getPCDData(series, time) {
      this.loading = true;
      Api.fetchPCDSet(series, time).then(
        response => {
          if (response.data.status === 'OK') {
            this.preparePCDData(response.data.data);
          } else {
            this.$store.dispatch('setSystemStatus', { text: response.data.message, timeout: 0, color: 'error' });
          }
          this.loading = false;
        },
        error => {
          //console.log(error);
          this.$store.dispatch('setSystemStatus', { text: error, timeout: 0, color: 'error' });
        }
      );
    },

    preparePCDData(pcdData) {
      this.pcds = [];
      Object.keys(pcdData).forEach(key => {
        let pcd = pcdData[key];
        let items = key.split('-');
        let deviceId = parseInt(items[0]);
        let phaseId = parseInt(items[1]);
        let marker = this.markers.find(m => m.id == deviceId) || { name: '' };

        let item = {
          info: pcd,
          title: marker.name,
          date: this.date,
          phase: phaseId
        };
        this.pcds.push(item);
      });
      this.baseIndex += 10;
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

<template>
  <div>
    <TitleBar title="Combined Traffic Anomaly Heatmap" :loading="loading" :refresh="refreshData" />

    <v-container>
      <v-container v-if="isData0Available">
        <BasicHeatMapChart :data="heatMapData0" :height="height" />
      </v-container>

      <v-container v-if="isData0Available">
        <BasicHeatMapChart :data="heatMapData1" :height="height" />
      </v-container>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState } from 'vuex';
import Utils from '@/utils/Utils';
import TitleBar from '@/components/traffic/common/TitleBar';
import BasicHeatMapChart from '@/components/traffic/chart/BasicHeatMapChart';

export default {
  components: {
    TitleBar,
    BasicHeatMapChart
  },

  data: () => ({
    loading: false,
    height: 650,
    heatMapData0: {},
    heatMapData1: {}
  }),

  computed: {
    isData0Available() {
      return !Utils.isEmpty(this.heatMapData0);
    },

    isData1Available() {
      return !Utils.isEmpty(this.heatMapData1);
    },
    ...mapState('traffic', ['currentDate'])
  },

  mounted() {
    this.fetchData();
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  methods: {
    refreshData() {
      this.fetchData();
    },

    fetchData() {
      this.fetchCombinedAnomalyData(this.currentDate);
    },

    async fetchCombinedAnomalyData(startTime) {
      this.loading = true;

      const start = startTime.getTime();

      try {
        // Now we await for both results, whose async processes have already been started
        const [set1, set2] = await Promise.all([
          Api.fetchCombinedAnomalyResults(19997, 'NB', start),
          Api.fetchCombinedAnomalyResults(19951, 'NB', start)
        ]);

        this.clearDataSet(this.heatMapData0);
        this.heatMapData0 = this.parseResponse(set1, startTime, '19997', 'NB');

        this.clearDataSet(this.heatMapData1);
        this.heatMapData1 = this.parseResponse(set2, startTime, '19951', 'SB');
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

    parseResponse(response, startTime, deviceId, direction) {
      let result = {};
      if (response.data.status === 'OK') {
        if (response.data.data) {
          let data = response.data.data;
          result.deviceId = deviceId;
          result.direction = direction;
          result.title = `${deviceId} ${direction}`;
          result.xAxis = 'Time of day';
          result.yAxis = '';
          result.xcategories = data.xcategories;
          result.ycategories = data.ycategories;
          result.colorAxis = this.createColorAxis();
          result.data = data.series;
          result.startTime = Utils.getStartOfDay(startTime);
          result.interval = 300;
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },

    createColorAxis() {
      return [
        { from: 0, to: 0, color: '#1B5E20', name: '0' },
        { from: 1, to: 1, color: '#FF8A80', name: '1' },
        { from: 2, to: 2, color: '#FF5252', name: '2' },
        { from: 3, to: 3, color: '#FF1744', name: '3' },
        { from: 4, to: 4, color: '#D50000', name: '4' },
        { from: 5, to: 5, color: '#B71C1C', name: '5' }
      ];
    }
  }
};
</script>

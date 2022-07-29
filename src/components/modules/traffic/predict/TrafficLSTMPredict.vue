<template>
  <div>
    <MapSelectionPanel
      ref="mapSelectPanel"
      :markers="markers"
      :items="items"
      name="predictSideBarWidth"
      :onMarkerClick="markerClicked"
    />

    <TitleBar title="Traffic Flow Prediction" :loading="loading" :refresh="refreshData">
      <div class="d-flex align-center justify-end mt-3">
        <div class="text-caption" v-if="timeUsedDisplay">Time used: {{ timeUsedDisplay }} ms</div>
      </div>
    </TitleBar>

    <div class="mt-3">
      <FlowChartDisplay
        :name="selectedSensor"
        :speed="speed0"
        :volume="volume0"
        :occupancy="occupancy0"
        :vosList="vosList0"
      />
    </div>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import MapSelectionPanel from '@/components/modules/traffic/map/MapSelectionPanel';
import TitleBar from '@/components/modules/traffic/predict/TitleBar';
import FlowChartDisplay from '@/components/modules/traffic/predict/FlowChartDisplay';

export default {
  components: {
    MapSelectionPanel,
    TitleBar,
    FlowChartDisplay
  },

  data: () => ({
    loading: false,
    baseURL: process.env.VUE_APP_PREDICT_API_URL,

    tabItems: [],
    tab: null,

    vosList0: [],
    speed0: {},
    volume0: {},
    occupancy0: {},
    timeUsed0: 0,

    vosList1: [],
    speed1: {},
    volume1: {},
    occupancy1: {},
    timeUsed1: 0,

    colors: ['#ED561B', '#058DC7', '#50B432', '#DD8800', '#BDBDBD'],

    currentDir: 'NB',
    dirItems: [
      { text: 'NB', value: 'NB' },
      { text: 'SB', value: 'SB' }
    ],
    locations: []
  }),

  computed: {
    markers() {
      return this.locations;
    },

    selectedSensor() {
      if (!this.$refs.mapSelectPanel) {
        return null;
      }
      const sensor = this.$refs.mapSelectPanel.getSelectedMarker();
      return `${sensor.sensorId}-${sensor.dir}`;
    },

    items() {
      let names = [];
      this.locations.forEach(location => {
        names.push(location.name.trimRight());
      });
      return names;
    },

    timeUsedDisplay() {
      return this.timeUsed0;
    },

    ...mapState(['currentDate'])
  },

  mounted() {
    this.fetchPredictionLocations();

    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.showDataIfEmpty();
    }, 500);
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  methods: {
    markerClicked(marker) {
      this.fetchPredictionData(marker);
    },

    showDataIfEmpty() {
      if (Utils.isEmpty(this.speed0) && Utils.isEmpty(this.speed1)) {
        this.$refs.mapSelectPanel.triggerFirstMarkerClick();
      }
    },

    dirSelected() {
      this.fetchData();
    },

    isTabVisible(name) {
      return this.tabItems.find(i => i.key === name) !== undefined;
    },

    refreshData() {
      this.fetchData();
    },

    fetchData() {
      const marker = this.$refs.mapSelectPanel.getSelectedMarker();
      this.fetchPredictionData(marker);
    },

    fetchPredictionData(marker) {
      const time = this.currentDate;
      if (marker && marker.dir) {
        this.tabItems = marker.methods.map(item => ({ key: item, value: item }));
        this.tab = this.tabItems.length > 0 ? this.tabItems[0].key : null;
        this.fetchPredictionResults(['LSTM'], marker.sensorId, marker.dir, time);
      }
    },

    async fetchPredictionResults(methods, id, dir, time) {
      this.loading = true;
      this.clearData();

      try {
        // Convert time to start and end of the day
        let startTime = Utils.formatDateTime(Utils.getStartOfDay(time));
        let endTime = Utils.formatDateTime(Utils.getEndOfDay(time));

        const requests = [];
        //requests.push(Api.fetchFlowData(id, dir, time.getTime()));
        requests.push(Api.fetchTrafficFlowAndBaselineData(id, dir, 300000, time.getTime()));
        requests.push(Api.fetchAnomalyTimeList(id, dir, time.getTime()));
        methods.forEach(method => {
          requests.push(Api.fetchPredictions(this.baseURL, id, dir, startTime, endTime, method));
        });

        // Now we await for both results, whose async processes have already been started
        const [flow, timeList, ...predicts] = await Promise.all(requests);

        // Verify the flow data is avaialble
        let flowList = this.getResponseData(flow);
        if (!flowList) {
          this.$store.dispatch('setSystemStatus', { text: 'No traffic flow data available', color: 'info' });
          this.loading = false;
          return;
        }

        const anomalyTimeList = this.getResponseData(timeList);
        const excludedList = this.createExcludedTimeList(anomalyTimeList);

        if (methods.includes('LSTM')) {
          this.composeLSTMResult(flowList, predicts[0], time, excludedList);
        }

        if (methods.includes('XGBM')) {
          this.composeXGBMResult(flowList, methods.length === 1 ? predicts[0] : predicts[1], time, excludedList);
        }
      } catch (error) {
        console.log(error);
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    async fetchPredictionLocations() {
      try {
        const response = await Api.fetchPredictionSensors(process.env.VUE_APP_PREDICT_API_URL);
        if (response.data.status === 'OK') {
          this.locations = response.data.sensors.map(sensor => ({
            id: `${sensor.id}${sensor.dir}`,
            sensorId: sensor.id,
            name: sensor.name,
            uid: sensor.uid,
            dir: sensor.dir,
            type: sensor.type,
            position: sensor.position,
            methods: sensor.methods
          }));
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    clearData() {
      this.vosList0 = [];
      this.speed0 = {};
      this.volume0 = {};
      this.occupancy0 = {};
      this.timeUsed0 = 0;
      this.vosList1 = [];
      this.speed1 = {};
      this.volume1 = {};
      this.occupancy1 = {};
      this.timeUsed1 = 0;
    },

    createExcludedTimeList(anomalyTimeList) {
      const result = new Set();
      if (anomalyTimeList) {
        anomalyTimeList.forEach(time => {
          result.add(time);
        });
        console.log('excluded list=', result.size);
      }
      return result;
    },

    composeLSTMResult(flowList, predict, time, excludedList) {
      const data = this.getResponseData(predict);
      if (data) {
        const { result: predictList, time: timeUsed } = data;
        if (predictList) {
          this.speed0 = this.formSpeedData(predictList, flowList, excludedList);
          this.volume0 = this.formVolumeData(predictList, flowList, excludedList);
          this.occupancy0 = this.formOccupancyData(predictList, flowList, excludedList);

          if (Utils.isToday(time)) {
            this.vosList0 = this.fillLastPredictions(flowList, predictList);
          } else {
            this.vosList0 = [];
          }

          this.timeUsed0 = Math.round(timeUsed);
        }
      }
    },

    composeXGBMResult(flowList, predict, time, excludedList) {
      const data = this.getResponseData(predict);
      if (data) {
        const { result: predictList, time: timeUsed } = data;
        if (predictList) {
          this.speed1 = this.formSpeedData(predictList, flowList, excludedList);
          this.volume1 = this.formVolumeData(predictList, flowList, excludedList);
          this.occupancy1 = this.formOccupancyData(predictList, flowList, excludedList);

          if (Utils.isToday(time)) {
            this.vosList1 = this.fillLastPredictions(flowList, predictList);
          } else {
            this.vosList1 = [];
          }

          this.timeUsed1 = Math.round(timeUsed);
        }
      }
    },

    fillLastPredictions(flowList, predictions) {
      let tsp0 = flowList.speed[flowList.speed.length - 1][0];
      let spd0 = flowList.speed[flowList.speed.length - 1][1];
      let vol0 = flowList.volume[flowList.volume.length - 1][1];
      let occ0 = flowList.occupancy[flowList.occupancy.length - 1][1];

      let tsp1 = Math.round(predictions.pred5.speed[predictions.pred5.speed.length - 1][0]);
      let spd1 = Math.round(predictions.pred5.speed[predictions.pred5.speed.length - 1][1]);
      let vol1 = Math.round(predictions.pred5.volume[predictions.pred5.volume.length - 1][1]);
      let occ1 = Math.round(predictions.pred5.occupancy[predictions.pred5.occupancy.length - 1][1]);

      let tsp2 = Math.round(predictions.pred10.speed[predictions.pred10.speed.length - 1][0]);
      let spd2 = Math.round(predictions.pred10.speed[predictions.pred10.speed.length - 1][1]);
      let vol2 = Math.round(predictions.pred10.volume[predictions.pred10.volume.length - 1][1]);
      let occ2 = Math.round(predictions.pred10.occupancy[predictions.pred10.occupancy.length - 1][1]);

      let tsp3 = Math.round(predictions.pred15.speed[predictions.pred15.speed.length - 1][0]);
      let spd3 = Math.round(predictions.pred15.speed[predictions.pred15.speed.length - 1][1]);
      let vol3 = Math.round(predictions.pred15.volume[predictions.pred15.volume.length - 1][1]);
      let occ3 = Math.round(predictions.pred15.occupancy[predictions.pred15.occupancy.length - 1][1]);

      const vosList = [];
      vosList.push(this.composeVosItem('Actual', tsp0, spd0, vol0, occ0));
      vosList.push(this.composeVosItem('Pred-5', tsp1, spd1, vol1, occ1));
      vosList.push(this.composeVosItem('Pred-10', tsp2, spd2, vol2, occ2));
      vosList.push(this.composeVosItem('Pred-15', tsp3, spd3, vol3, occ3));

      return vosList;
    },

    composeVosItem(type, timestamp, speed, volume, occupancy) {
      return {
        type,
        time: Utils.formatTimeAsMinute(new Date(timestamp)),
        speed,
        volume,
        occupancy
      };
    },

    getResponseData(response) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      }
      return result;
    },

    formSpeedData(predictList, flowList, excludedList) {
      let title = 'Speed';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';

      let data = [];
      if (flowList.speed) {
        data.push({ name: 'Actual', color: this.colors[0], data: flowList.speed });
      }

      if (flowList.baselineSpeed) {
        let error = this.calcMAPE(flowList.speed, flowList.baselineSpeed, excludedList);
        data.push({ name: this.formatText('Baseline', error), color: this.colors[4], data: flowList.baselineSpeed });
      }

      if (predictList && flowList.speed) {
        let error5 = this.calcMAPE(flowList.speed, predictList.pred5.speed, excludedList);
        let error10 = this.calcMAPE(flowList.speed, predictList.pred10.speed, excludedList);
        let error15 = this.calcMAPE(flowList.speed, predictList.pred15.speed, excludedList);

        data.push({ name: this.formatText('Pred-5', error5), color: this.colors[1], data: predictList.pred5.speed });
        data.push({ name: this.formatText('Pred-10', error10), color: this.colors[2], data: predictList.pred10.speed });
        data.push({ name: this.formatText('Pred-15', error15), color: this.colors[3], data: predictList.pred15.speed });
      }

      return { data, xAxis, yAxis, title, ymin: 0, ymax: 100 };
    },

    formVolumeData(predictList, flowList, excludedList) {
      let title = 'Volume';
      let xAxis = 'Time of day';
      let yAxis = 'Volume (vehicles/min)';

      let data = [];
      if (flowList !== null) {
        data.push({ name: 'Actual', color: this.colors[0], data: flowList.volume });
      }

      if (flowList && flowList.baselineVolume) {
        let error = this.calcMAPE(flowList.volume, flowList.baselineVolume, excludedList);
        data.push({ name: this.formatText('Baseline', error), color: this.colors[4], data: flowList.baselineVolume });
      }

      if (predictList !== null) {
        let error5 = this.calcMAPE(flowList.volume, predictList.pred5.volume, excludedList);
        let error10 = this.calcMAPE(flowList.volume, predictList.pred10.volume, excludedList);
        let error15 = this.calcMAPE(flowList.volume, predictList.pred15.volume, excludedList);

        data.push({ name: this.formatText('Pred-5', error5), color: this.colors[1], data: predictList.pred5.volume });
        data.push({
          name: this.formatText('Pred-10', error10),
          color: this.colors[2],
          data: predictList.pred10.volume
        });
        data.push({
          name: this.formatText('Pred-15', error15),
          color: this.colors[3],
          data: predictList.pred15.volume
        });
      }

      return { data, xAxis, yAxis, title };
    },

    formOccupancyData(predictList, flowList, excludedList) {
      let title = 'Occupancy';
      let xAxis = 'Time of day';
      let yAxis = 'Occupancy (%)';

      let data = [];
      if (flowList !== null) {
        data.push({ name: 'Actual', color: this.colors[0], data: flowList.occupancy });
      }

      if (flowList && flowList.baselineOccupancy) {
        let error = this.calcMAPE(flowList.occupancy, flowList.baselineOccupancy, excludedList);
        data.push({
          name: this.formatText('Baseline', error),
          color: this.colors[4],
          data: flowList.baselineOccupancy
        });
      }

      if (predictList !== null) {
        let error5 = this.calcMAPE(flowList.occupancy, predictList.pred5.occupancy, excludedList);
        let error10 = this.calcMAPE(flowList.occupancy, predictList.pred10.occupancy, excludedList);
        let error15 = this.calcMAPE(flowList.occupancy, predictList.pred15.occupancy, excludedList);

        data.push({
          name: this.formatText('Pred-5', error5),
          color: this.colors[1],
          data: predictList.pred5.occupancy
        });
        data.push({
          name: this.formatText('Pred-10', error10),
          color: this.colors[2],
          data: predictList.pred10.occupancy
        });
        data.push({
          name: this.formatText('Pred-15', error15),
          color: this.colors[3],
          data: predictList.pred15.occupancy
        });
      }

      return { data, xAxis, yAxis, title };
    },

    formatText(title, percent) {
      return `${title} (${percent.toFixed(2)}%)`;
    },

    // MAPE -- Mean Average Percentage Error
    calcMAPE(items0, items1, excludedList) {
      let data = {};
      for (let item of items0) {
        const time = item[0];
        if (item[1] > 0 && !excludedList.has(time)) {
          data[time] = { item1: item[1] };
        }
      }

      for (let item of items1) {
        let value = data[item[0]];
        if (value) {
          value['item2'] = item[1];
        }
      }

      let totalCount = 0;
      let totalValue = 0;
      for (let key of Object.keys(data)) {
        let item = data[key];
        let v1 = item['item1'];
        let v2 = item['item2'];
        if (v1 && v2) {
          totalValue += Math.abs(v1 - v2) / v1;
          totalCount++;
        }
      }

      let mre = totalCount > 0 ? (totalValue * 100) / totalCount : -1;

      return mre;
    }
  }
};
</script>

<style lang="scss" scoped>
.time-display {
  margin-top: 20px;
  margin-right: 20px;
  font-size: 14px;
  font-weight: bold;
}
</style>

<template>
  <!-- <div class="desktop" v-if="!$vuetify.breakpoint.mobile"> -->
  <div>
    <MapSelectionPanel
      ref="mapSelectPanel"
      :markers="markers"
      :items="items"
      name="predictSideBarWidth"
      :onMarkerClick="markerClicked"
    />

    <TitleBar title="Traffic Flow Prediction" :showId="false" :loading="loading" :refresh="refreshData">
      <div class="d-flex align-items justify-space-between align-center">
        <div class="mt-1 mr-10" style="width:100px;">
          <v-select
            dark
            dense
            v-model="direction"
            :items="dirItems"
            item-text="text"
            item-value="value"
            @input="dirSelected"
            hide-details
            prepend-icon="mdi-arrow-decision-outline"
            single-line
          />
        </div>
        <div class="d-flex align-center justify-end mt-3">
          <div class="text-caption" v-if="timeUsed">Time used: {{ timeUsed }} ms</div>
        </div>
      </div>
    </TitleBar>

    <v-container>
      <v-row>
        <template v-for="(volumeData, deviceId, index) in volumeList">
          <v-col cols="12" :key="index">
            <div :id="`D${deviceId}`">
              <div class="my-3" v-if="predictionAvail">
                <v-row wrap no-gutters>
                  <template v-for="(data, idx) in predictionItems[deviceId]">
                    <v-col :class="idx > 0 ? 'ml-2' : ''" :key="100 + idx">
                      <VolumeCard :item="data" color="green" />
                    </v-col>
                  </template>
                </v-row>
              </div>

              <v-card tile elevation="24">
                <BasicChart :data="volumeData" :height="height" />
              </v-card>
            </div>
          </v-col>
        </template>
      </v-row>
    </v-container>
  </div>

  <!-- <div class="mobile" v-else>
    <MapSelectionPanel
      ref="mapSelectPanel"
      :markers="markers"
      :items="items"
      name="predictSideBarWidth"
      :onMarkerClick="markerClicked"
    />

    <v-container ref="myDiv">
      <v-card>
        <v-col class="pt-0">
          <v-row class="grid-center">
            <MenuSelector :items="predictionModes" :selectedItem="predictionMode" @click="setPredictionMode" />
          </v-row>
          <v-row>
            <template v-for="(volumeData, deviceId, index) in volumeList">
              <v-col cols="12" :key="index">
                <div :id="`D${deviceId}`">
                  <div class="my-3" v-if="predictionAvail">
                    <v-row wrap no-gutters>
                      <template v-for="(data, idx) in predictionItems[deviceId]">
                        <v-col :class="idx > 0 ? 'ml-2' : ''" :key="100 + idx">
                          <VolumeCard :item="data" color="green" />
                        </v-col>
                      </template>
                    </v-row>
                  </div>

                  <v-card tile elevation="24">
                    <BasicChart :data="volumeData" :height="height" />
                  </v-card>
                </div>
              </v-col>
            </template>
          </v-row>
        </v-col>
      </v-card>
    </v-container>
  </div> -->
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import MapSelectionPanel from '@/components/modules/traffic/map/MapSelectionPanel';
import TitleBar from '@/components/modules/traffic/predict/TitleBar';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import VolumeCard from '@/components/modules/traffic/common/VolumeCard';

export default {
  components: {
    MapSelectionPanel,
    TitleBar,
    BasicChart,
    VolumeCard
  },

  data: () => ({
    loading: false,
    baseURL: process.env.VUE_APP_GTS_API_URL,
    height: 500,
    timeUsed: null,
    loss: 0,
    volumeList: {},
    predictionItems: {},
    colors: [
      '#ED561B',
      '#BDBDBD',
      '#058DC7',
      '#50B432',
      '#DDDF00',
      '#f28f43',
      '#77a1e5',
      '#c42525',
      '#a6c96a',
      '#492970'
    ],
    direction: 'NB',
    dirItems: [
      { text: 'NB', value: 'NB' },
      { text: 'SB', value: 'SB' }
    ],
    locations: []
  }),

  computed: {
    markers() {
      return this.getUniqueSensors();
    },

    items() {
      let names = [];
      this.getUniqueSensors().forEach(location => {
        names.push(location.name.trimRight());
      });
      return names;
    },

    predictionAvail() {
      return !Utils.isEmpty(this.predictionItems);
    },

    ...mapState(['currentDate'])
  },

  mounted() {
    this.fetchPredictionLocations();

    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.fetchPredictionData();
    }, 1000);
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
      this.gotoSection(`#D${marker.sensorId}`);
    },

    dirSelected() {
      this.fetchData();
    },

    refreshData() {
      this.fetchData();
    },

    fetchData() {
      this.fetchPredictionData();
    },

    gotoSection(target) {
      this.$vuetify.goTo(target);
    },

    getUniqueSensors() {
      return Utils.uniqueBy(this.locations, item => item.sensorId);
    },

    async fetchPredictionLocations() {
      try {
        const response = await Api.fetchPredictionSensors(process.env.VUE_APP_GTS_API_URL);
        this.locations = response.data.map(sensor => ({
          id: `${sensor.id}-${sensor.dir}`,
          sensorId: sensor.id,
          name: sensor.name,
          uid: sensor.uid,
          dir: sensor.dir,
          type: sensor.type,
          position: sensor.position,
          methods: sensor.methods
        }));
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchPredictionData() {
      const time = this.currentDate;
      const dir = this.direction;
      this.fetchPredictionResults(dir, time);
    },

    async fetchPredictionResults(dir, time) {
      this.loading = true;
      this.timeUsed = null;

      try {
        // Convert time to start and end of the day
        let startTime = Utils.formatDateTime(Utils.getStartOfDay(time));
        let endTime = Utils.formatDateTime(Utils.getEndOfDay(time));

        const ids = this.locations.filter(item => item.dir === dir).map(item => item.id);

        const requests = [];
        requests.push(Api.fetchTrafficVolumeAndBaselineDataList(ids, 300000, time.getTime()));
        requests.push(Api.fetchAnomalyTimeLists(ids, time.getTime()));
        requests.push(Api.fetchGTSPredictions(this.baseURL, -1, dir, startTime, endTime, -1));

        // Now we await for both results, whose async processes have already been started
        const [flow, timeList, predicts] = await Promise.all(requests);

        // Verify the flow data is avaialble
        let flowList = this.getResponseData(flow);
        if (!flowList) {
          this.$store.dispatch('setSystemStatus', { text: 'No traffic flow data available', color: 'info' });
          this.loading = false;
          return;
        }

        const anomalyTimeList = this.getResponseData(timeList);
        const excludedList = this.createExcludedTimeList(anomalyTimeList);
        this.composeResult(time, dir, flowList, predicts, excludedList);
      } catch (error) {
        console.log(error);
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    createExcludedTimeList(anomalyTimeList) {
      const result = {};
      if (anomalyTimeList) {
        Object.keys(anomalyTimeList).forEach(deviceId => {
          const timeList = new Set();
          anomalyTimeList[deviceId].forEach(time => {
            timeList.add(time);
          });
          result[deviceId] = timeList;
        });

        console.log('excluded list=', Object.keys(result).length);
      }
      return result;
    },

    composeResult(time, direction, flowList, predict, excludedList) {
      const data = this.getResponseData(predict);

      if (data) {
        const { result: predictList, time: timeUsed, loss: loss } = data;
        if (predictList) {
          this.volumeList = this.formVolumeDataList(direction, predictList, flowList, excludedList);
          this.timeUsed = Math.round(timeUsed);
          this.loss = loss;

          if (Utils.isToday(time)) {
            this.predictionItems = this.fillLastPredictions(direction, flowList, predictList);
          } else {
            this.predictionItems = [];
          }
        }
      }
    },

    formVolumeDataList(direction, predictList, flowLists, excludedLists) {
      const preds = Object.keys(predictList);
      const deviceIds = Object.keys(predictList[preds[0]]);

      const result = {};
      deviceIds.forEach(deviceId => {
        const device = `${deviceId}-${direction}`;
        const actualVolume = this.removeZeroVolumes(flowLists[device].volume);
        const baselineVolume = flowLists[device].baselineVolume;
        const excludedList = deviceId in excludedLists ? excludedLists[deviceId] : new Set();
        const data = this.formVolumeData(deviceId, predictList, actualVolume, baselineVolume, excludedList);
        result[deviceId] = data;
      });

      return result;
    },

    fillLastPredictions(direction, flowLists, predictList) {
      const preds = Object.keys(predictList);
      const deviceIds = Object.keys(predictList[preds[0]]);

      const result = {};
      deviceIds.forEach(deviceId => {
        const device = `${deviceId}-${direction}`;
        const actualVolume = this.removeZeroVolumes(flowLists[device].volume);
        const data = this.formLastPredictions(deviceId, actualVolume, predictList);
        result[deviceId] = data;
      });

      return result;
    },

    removeZeroVolumes(flowList) {
      if (flowList != null && flowList.length > 0) {
        return flowList.filter(item => item[1] > 0);
      } else {
        return flowList;
      }
    },

    formVolumeData(deviceId, predictList, actualVolume, baselineVolume, excludedList) {
      const device = this.locations.find(item => item.sensorId == deviceId);
      let title = device ? device.name : deviceId;
      let xAxis = 'Time of day';
      let yAxis = 'Volume (vehicles/min)';

      let data = [];

      // Add actual and baseline volume data
      if (actualVolume) {
        data.push({ name: 'Actual', color: this.colors[0], data: actualVolume });

        if (baselineVolume) {
          let error = this.calcMAPE(actualVolume, baselineVolume, excludedList);
          data.push({ name: this.formatText('Baseline', error), color: this.colors[1], data: baselineVolume });
        }
      }

      // For each types of predictions
      Object.keys(predictList).forEach((pred, idx) => {
        const predictions = predictList[pred][deviceId];
        // console.log('predictions=' + predictions);
        const validPredictions = predictions.filter(item => item[1] > 0);
        const error = this.calcMAPE(actualVolume, validPredictions, excludedList);
        data.push({ name: this.formatText(pred, error), color: this.colors[2 + idx], data: validPredictions });
      });

      return { data, xAxis, yAxis, title };
    },

    formLastPredictions(deviceId, actualVolume, predictList) {
      const t0 = actualVolume[actualVolume.length - 1][0];
      const v0 = actualVolume[actualVolume.length - 1][1];

      const volumeList = [];
      volumeList.push(this.composeVolumeItem('Actual', t0, v0, this.colors[0]));

      // For each types of predictions
      Object.keys(predictList).forEach((pred, idx) => {
        const predictions = predictList[pred][deviceId];
        let t = Math.round(predictions[predictions.length - 1][0]);
        let v = Math.round(predictions[predictions.length - 1][1]);
        volumeList.push(this.composeVolumeItem(Utils.toTitleCase(pred), t, v, this.colors[2 + idx]));
      });

      return volumeList;
    },

    composeVolumeItem(type, timestamp, volume, color) {
      return {
        type,
        time: Utils.formatTimeAsMinute(new Date(timestamp)),
        volume,
        color
      };
    },

    // MAPE -- Mean Average Percentage Error
    calcMAPE(items0, items1, excludes) {
      if (!items0 || !items1) {
        return 0;
      }

      let data = {};
      items0.forEach(item => {
        const time = item[0];
        if (item[1] > 0 && !excludes.has(time)) {
          data[time] = { item1: item[1] };
        }
      });

      items1.forEach(item => {
        let value = data[item[0]];
        if (value) {
          value['item2'] = item[1];
        }
      });

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

    formatText(title, percent) {
      const name = Utils.toTitleCase(title);
      return `${name} (${percent.toFixed(2)}%)`;
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

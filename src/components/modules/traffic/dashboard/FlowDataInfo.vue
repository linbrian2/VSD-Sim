<template>
  <div>
    <v-container>
      <v-row id="info">
        <v-col cols="12">
          <div class="d-flex justify-space-between mt-n6">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Current Info</h3></v-subheader>
            <div class="mt-4 mr-3">
              <v-chip class="ml-2 mt-n1" outlined small>
                <span>{{ marker.id }} / {{ marker.uid }}</span>
              </v-chip>
            </div>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4 mt-n3">
            <div class="d-flex justify-space-between">
              <div>
                <v-tabs color="teal accent-4" v-model="tab">
                  <v-tab v-for="(item, direction, i) in info" :key="10 + i">
                    <v-chip small>{{ direction }}</v-chip>
                  </v-tab>
                </v-tabs>
              </div>
              <div class="mt-2 mr-1 overline gray--text">{{ infoTime }}</div>
            </div>

            <div>
              <v-tabs-items v-model="tab" class="custom-tab-items">
                <v-tab-item v-for="(infoItem, direction, i) in info" :key="10 + i">
                  <v-row>
                    <v-col cols="6" v-for="(item, j) in infoItem.basicInfo" :key="j">
                      <ListInfoCard :info="item" :extra="direction" class="mt-0" />
                    </v-col>
                  </v-row>

                  <v-row v-if="infoItem.predictions">
                    <v-col cols="12">
                      <div class="d-flex justify-space-between">
                        <v-subheader class="pl-0 mx-4 mt-n4 font-weight-bold text-overline blue--text">
                          <h3>Predictions</h3>
                        </v-subheader>
                      </div>
                      <v-divider />
                    </v-col>
                    <v-col cols="12">
                      <div class="mx-1 mt-n4">
                        <PredictionInfoList :header="infoItem.predictHeader" :data="infoItem.predictions" />
                      </div>
                    </v-col>
                  </v-row>
                </v-tab-item>
              </v-tabs-items>
            </div>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="isSpeed">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Speed</h3></v-subheader>

            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showSpeedChart" class="mr-4 mt-2" :loading="loading">
                  <v-icon small>mdi-arrow-expand</v-icon>
                </v-btn>
              </template>
              <span>Expand</span>
            </v-tooltip>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="4">
              <BasicChart :data="speed" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Volume</h3></v-subheader>
            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showVolumeChart" class="mr-4 mt-2" :loading="loading">
                  <v-icon small>mdi-arrow-expand</v-icon>
                </v-btn>
              </template>
              <span>Expand</span>
            </v-tooltip>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="4">
              <BasicChart :data="volume" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Occupancy</h3></v-subheader>

            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showOccupancyChart" class="mr-4 mt-2" :loading="loading">
                  <v-icon small>mdi-arrow-expand</v-icon>
                </v-btn>
              </template>
              <span>Expand</span>
            </v-tooltip>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="4">
              <BasicChart :data="occupancy" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="camerasAvaliable">
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Cameras</h3></v-subheader>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-btn outlined v-for="id in cameraIds" :key="id" @click.stop="playVideo(id)" class="mr-5">
              <v-icon left color="warning">
                mdi-camera
              </v-icon>
              {{ id }}
            </v-btn>
          </div>
        </v-col>
      </v-row>
    </v-container>
    <ChartDialog ref="chartDialog" v-model="showChartDialog" />
    <VideoPlayerDialog ref="vpRef" videoType="application/x-mpegURL" v-model="showVideoPlayer" />
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import ListInfoCard from '@/components/modules/traffic/common/ListInfoCard';
import PredictionInfoList from '@/components/modules/traffic/dashboard/PredictionInfoList';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import ChartDialog from '@/components/modules/traffic/common/ChartDialog';
import VideoPlayerDialog from '@/components/modules/traffic/common/VideoPlayerDialog';
import { getVideoUrl } from '@/utils/DeldotVideoUrl';

export default {
  props: {
    marker: Object
  },

  components: {
    BasicChart,
    ListInfoCard,
    ChartDialog,
    PredictionInfoList,
    VideoPlayerDialog
  },

  data: () => ({
    loading: false,
    showChartDialog: false,
    showVideoPlayer: false,
    height: 300,
    legendY: 5,
    marginLeft: 80,
    tab: null,
    speed: {},
    volume: {},
    occupancy: {},
    deviceInfo: {},
    info: {},
    cameraIds: []
  }),

  computed: {
    camerasAvaliable() {
      return !Utils.isEmpty(this.cameraIds);
    },

    isSpeed() {
      return !Utils.isEmpty(this.speed);
    },

    infoTime() {
      if (Object.keys(this.deviceInfo).length > 0) {
        const key = Object.keys(this.deviceInfo)[0];
        const time = this.deviceInfo[key].flowTime;
        const result = Utils.formatAMPMTime(time);
        return result;
      } else {
        return null;
      }
    }
  },

  mounted() {
    this.init(this.marker);
  },

  methods: {
    init(marker) {
      this.fetchData(marker.id);
    },

    showSpeedChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Speed', this.speed);
    },

    showVolumeChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Volume', this.volume);
    },

    showOccupancyChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Occupancy', this.occupancy);
    },

    playVideo(id) {
      const url = getVideoUrl(id);
      if (url) {
        if (this.$refs.vpRef) {
          this.$refs.vpRef.changeVideoSource(url);
        }
        this.showVideoPlayer = true;
      }
    },

    fetchData(deviceId) {
      let interval = 300000;
      let time = new Date().getTime();
      this.fetchTrafficFlowData(deviceId, interval, time);
    },

    async fetchTrafficFlowData(id, interval, time) {
      this.loading = true;
      try {
        // Now we await for both results, whose async processes have already been started
        const [flowListRes, deviceInfoRes] = await Promise.all([
          Api.fetchTrafficFlowData(id, null, interval, time, null),
          Api.fetchLatestDeviceInfo(id)
        ]);

        let flowList = this.parseResponseData(flowListRes);
        if (flowList) {
          this.speed = this.formSpeedData(flowList);
          this.volume = this.formVolumeData(flowList);
          this.occupancy = this.formOccupancyData(flowList);
        }

        let deviceInfo = this.parseResponseData(deviceInfoRes);
        if (deviceInfo) {
          this.deviceInfo = deviceInfo;
          this.info = this.formDeviceInfoData(deviceInfo);
          this.cameraIds = this.formCameraIds(deviceInfo);
        }
      } catch (error) {
        console.log(error);
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
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
        console.log(response);
        console.log(response.data.message);
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },

    formSpeedData(flowList) {
      if (!flowList.speed) {
        return {};
      }
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';
      let data = flowList.speed;

      return { data, xAxis, yAxis, title };
    },

    formVolumeData(flowList) {
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Volume (veh./5m)';
      let data = flowList.volume;

      return { data, xAxis, yAxis, title };
    },

    formOccupancyData(flowList) {
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Occupancy (%)';
      let data = flowList.occupancy;

      return { data, xAxis, yAxis, title };
    },

    formCameraIds(deviceInfo) {
      let result = new Set();
      for (const direction in deviceInfo) {
        let d = deviceInfo[direction];
        if (d.upCamUrl) {
          result.add(d.upCamUrl);
        }
        if (d.downCamUrl) {
          result.add(d.downCamUrl);
        }
      }
      return Array.from(result);
    },

    showFlowChart(params) {
      this.$store.commit('traffic/SET_CURRENT_FLOW_CHART_PARAMS', params);
      this.$store.commit('traffic/SHOW_FLOW_CHART', true);
    },

    formDeviceInfoData(deviceInfo) {
      let result = {};
      let directions = ['NB', 'SB', 'EB', 'WB'];
      directions.forEach(direction => {
        if (deviceInfo.hasOwnProperty(direction)) {
          const info = deviceInfo[direction];
          const basicInfo = this.composeBasicInfo(info);
          const predictInfo = this.composePredictionInfo(info.prediction, info.capacityFiveMin);
          if (predictInfo) {
            const { predictHeader, predictions } = predictInfo;
            result[direction] = { basicInfo, predictHeader, predictions };
          } else {
            result[direction] = { basicInfo };
          }
        }
      });
      return result;
    },

    formFlowChartParamInfo(i) {
      return {
        deviceId: i.deviceId,
        direction: i.direction,
        speed: i.speed,
        volume: i.volume,
        occupancy: i.occupancy,
        mdist: i.mdist,
        status: i.anomalyStatus,
        time: i.anomalyTime
      };
    },

    composeBasicInfo(i) {
      const p2 = { Speed: i.speed ? i.speed : 'N/A', Volume: i.volume, Occupancy: i.occupancy };
      const p3 = { 'avg Speed': i.avgSpd, 'avg Volume': i.avgVol, 'avg Occupancy': i.avgOcc };

      const p4 = {
        '5 Min': i.volume5MinMax,
        '1 Hr': i.volume1hrMax,
        '1 Hr Projected': i.volume1hrProjected
      };

      const p5 = {};
      if (i.mdist != null && i.severity != null) {
        p5['mdist'] = i.mdist;
        p5['severity'] = i.severity;
        let statusName = 'Status';
        if (i.anomalyStatus > 0) {
          statusName = `Status ${i.anomalyStatus}`;
        }
        const text = i.anomalyStatus > 0 ? 'Anomaly' : 'Normal';
        p5[statusName] = i.anomalyStatus > 0 ? { text } : text;
      }

      const p6 = { lanes: i.lanes, stations: i.stations, 'sample Size': i.sampleSize };

      // Flow status
      const p7 = {};
      const name = `Status ${i.flowLevel}`;
      p7[name] = i.flowStatus;

      p7['vol + occ'] = i.vo + '%';
      if (i.breakdown) {
        p7['Breakdown Probability'] = Math.round(i.breakdown) + '%';
      } else {
        p7['Flow Level'] = i.flowLevel;
      }

      const p8 = {};
      if (i.capacityFiveMin) {
        p8['Percentage'] = i.capacityPercent + '%';
        p8['5 Min'] = i.capacityFiveMin;
        p8['Per Hour'] = i.capacityHour;
        //p8['Per Lane per hour'] = i.capacityHourPerLane;
      }

      const color = Constants.INFO_BAR_DEFAULT_COLOR;

      const result = [];
      if (!Utils.isValueAllEmpty(p2)) {
        result.push({ title: 'Current Flow', color: color, items: Utils.obj2Arr(p2) });
      }
      if (!Utils.isValueAllEmpty(p7)) {
        let currentColor = `level-${i.flowLevel}`;
        result.push({ title: 'Flow Status', color: currentColor, items: Utils.obj2Arr(p7) });
      }

      if (!Utils.isValueAllEmpty(p5)) {
        let button = null;
        if (i.anomalyStatus > 0) {
          const params = this.formFlowChartParamInfo(i);
          button = { color: '#ff8080', icon: 'mdi-help-circle-outline', handler: this.showFlowChart, params: params };
        }

        let currentColor = i.anomalyStatus > 0 ? 'red lighten-2' : color;
        result.push({ title: 'Anomaly Detection', button: button, color: currentColor, items: Utils.obj2Arr(p5) });
      }

      if (!Utils.isValueAllEmpty(p3)) {
        result.push({ title: 'Average Flow', color: color, items: Utils.obj2Arr(p3) });
      }

      if (!Utils.isValueAllEmpty(p8)) {
        result.push({ title: 'Throughput', color: color, items: Utils.obj2Arr(p8) });
      }

      if (!Utils.isValueAllEmpty(p4)) {
        result.push({ title: 'Max Volume', color: color, items: Utils.obj2Arr(p4) });
      } else if (!Utils.isValueAllEmpty(p6)) {
        result.push({ title: 'Device Info', color: color, items: Utils.obj2Arr(p6) });
      }

      return result;
    },

    composePredictionInfo(prediction, capacityFiveMin) {
      if (!prediction) {
        return null;
      }

      const predictHeader = [{ text: 'Flow', value: 'item' }];
      const predictValues = {};

      Object.keys(prediction).forEach(pred => {
        const value = prediction[pred];

        Object.keys(value).forEach(type => {
          const itemValue = value[type][0][1];
          let values = predictValues[type];
          if (!values) {
            values = {};
            predictValues[type] = values;
          }
          values[pred] = itemValue;
        });

        const time = value[Object.keys(value)[0]][0][0];
        const headerItem = { text: Utils.formatAMPMTime(time), value: pred };
        predictHeader.push(headerItem);
      });

      const predictions = [];
      Object.keys(predictValues).forEach(item => {
        const name = Utils.toTitleCase(item);
        const row = { item: name, ...predictValues[item] };
        predictions.push(row);
      });

      //Add throughput (percentage)
      if (capacityFiveMin) {
        const throughputValues = {};
        const volumes = predictValues['volume'];
        Object.keys(volumes).forEach(key => {
          throughputValues[key] = Math.floor((volumes[key] * 100) / capacityFiveMin) + '%';
        });
        const row = { item: 'Throughput', ...throughputValues };
        predictions.push(row);
      }

      return { predictHeader, predictions };
    },

    getStatusTextByLevel(level) {
      const statuses = ['No Delay', 'No Delay', 'No Delay', 'Minor Delay', 'Major Delay', 'Major Delay', 'Stop and Go'];
      return statuses[level];
    },

    getStatusColorByLevel(level) {
      const colors = ['#339900', '#00ff33', '#ffff00', '#ffc031', '#fa7a28', '#ff0000', '#c00000'];
      return colors[level];
    }
  }
};
</script>

<style lang="scss" scoped>
.time-display {
  margin: 18px 10px;
  padding: 5px 10px;
  font-size: 11pt;
}
.custom-tab-items {
  background-color: transparent !important;
}
</style>

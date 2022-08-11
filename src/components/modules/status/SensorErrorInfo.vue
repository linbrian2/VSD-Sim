<template>
  <div>
    <v-container>
      <v-row id="info">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Basic Info</h3></v-subheader>
            <div class="mt-4 mr-3">
              <v-chip class="ml-2 mt-n1" outlined small>
                <span>{{ marker.id }} / {{ marker.uid }}</span>
              </v-chip>
            </div>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
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
                </v-tab-item>
              </v-tabs-items>
            </div>
          </div>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Error Types</h3></v-subheader>
          <v-divider />
        </v-col>

        <v-col cols="12">
          <div class="mx-4">
            <v-card tile elevation="4">
              <v-chip-group active-class="primary--text" column class="mx-4">
                <v-chip small label v-for="{ color, name, from } in dataClasses" :key="from" :color="color">
                  {{ from }}: {{ name }}
                </v-chip>
              </v-chip-group>
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row v-for="({ errorTypes }, i) in sensorErrorTypes" :key="i">
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="4">
              <SensorHeatmapChart :data="errorTypes" :legend="false" :height="350" />
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import StatusApi from '@/utils/api/status';
import { mapState } from 'vuex';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/status';
import ListInfoCard from '@/components/modules/traffic/common/ListInfoCard';
import SensorHeatmapChart from '@/components/modules/status/SensorHeatmapChart';
export default {
  props: {
    marker: Object
  },

  components: {
    ListInfoCard,
    SensorHeatmapChart
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
    cameraIds: [],
    sensorErrorTypes: [],
    dataClasses: []
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
    },

    ...mapState(['currentDate'])
  },

  mounted() {
    this.dataClasses = this.createDataClasses();
    this.init(this.marker);
  },

  methods: {
    init(marker) {
      this.fetchData(marker.id);
    },

    getVideoUrl(cameraId) {
      return `http://167.21.72.35:1935/live/${cameraId}.stream/playlist.m3u8`;
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
      const url = this.getVideoUrl(id);
      if (url) {
        if (this.$refs.vpRef) {
          this.$refs.vpRef.changeVideoSource(url);
        }
        this.showVideoPlayer = true;
      }
    },

    fetchData(deviceId) {
      this.fetchSensorStatus(deviceId, this.currentDate);
    },

    async fetchSensorStatus(deviceId, date) {
      this.loading = true;
      try {
        const response = await StatusApi.fetchSensorErrors(deviceId, date.getTime());
        const allErrorTypes = this.parseResponseData(response);
        if (allErrorTypes) {
          const sensorErrorTypes = [];
          allErrorTypes.forEach(({ dir, types }) => {
            const errorTypes = this.composeSensorErrorTypesHeatMapData(deviceId, dir, types);
            sensorErrorTypes.push({ dir, errorTypes });
          });
          this.sensorErrorTypes = sensorErrorTypes;
        }
      } catch (error) {
        console.log(error);
      }
      this.loading = false;
    },

    composeSensorErrorTypesHeatMapData(deviceId, dir, errorTypes) {
      let xcategories = [];
      let ycategories = [];

      let startTime = Utils.getStartOfDay(this.currentDate).getTime();

      const rowCount = 12;
      const colCount = 24;

      for (let i = 0; i < rowCount; i++) {
        ycategories.push(i * 5);
      }
      for (let i = 0; i < colCount; i++) {
        xcategories.push(startTime + 3600000 * i);
      }

      // Initialize a map to represent 2D arrays
      let matrix = {};
      let errorCount = 0;
      for (let row = 0; row < rowCount; row++) {
        for (let col = 0; col < colCount; col++) {
          matrix[row * colCount + col] = errorTypes[col * rowCount + row];
          if (errorTypes[col * rowCount + row] > 0) {
            errorCount++;
          }
        }
      }

      // Create a series
      let series = [];
      for (let x = 0; x < colCount; x++) {
        for (let y = 0; y < rowCount; y++) {
          series.push([x, y, matrix[y * colCount + x]]);
        }
      }

      const dataClasses = this.createDataClasses();
      const colorAxis = {
        dataClasses
      };

      let errorPercentage = (errorCount * 100) / (rowCount * colCount);
      if (errorPercentage < 1) {
        errorPercentage = errorPercentage.toFixed(1);
      } else {
        errorPercentage = Math.round(errorPercentage);
      }

      let result = {};
      result.title = `${deviceId} ${dir} Error Types (${errorPercentage}%)`;
      result.xAxis = 'Time of Day (hour)';
      result.yAxis = 'Time of Hour (min)';
      result.xcategories = xcategories;
      result.ycategories = ycategories;
      result.data = series;
      result.colorAxis = colorAxis;

      return result;
    },

    createDataClasses() {
      const dataClasses = [];
      for (let i = 0; i < 8; i++) {
        dataClasses.push({
          from: i,
          to: i,
          color: Constants.DISTINCT_COLORS[i],
          name: Constants.QUALITY_ERROR_NAMES[i]
        });
      }
      return dataClasses;
    },

    addSpace(n) {
      return n <= 0
        ? ''
        : Array(n)
            .fill(' ')
            .join('');
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

    showFlowChart(params) {
      this.$store.commit('traffic/SET_CURRENT_FLOW_CHART_PARAMS', params);
      this.$store.commit('traffic/SHOW_FLOW_CHART', true);
    },

    formDeviceInfoData(deviceInfo) {
      let result = {};
      let directions = ['NB', 'SB', 'EB', 'WB'];
      directions.forEach(direction => {
        if (deviceInfo.hasOwnProperty(direction)) {
          const basicInfo = this.composeBasicInfo(deviceInfo[direction]);
          result[direction] = { basicInfo };
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

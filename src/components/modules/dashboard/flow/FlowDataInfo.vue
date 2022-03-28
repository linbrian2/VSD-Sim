<template>
  <div>
    <v-container>
      <v-tabs v-model="selectedTab" color="teal accent-4" centered>
        <v-tab>Volume</v-tab>
        <v-tab>Speed</v-tab>
        <v-tab>Occupancy</v-tab>
        <v-tab-item>
          <v-row>
            <v-col cols="12">
              <div class="mx-4">
                <v-card tile class="basic-chart" elevation="4">
                  <BasicChart :data="volume" :height="height" :left="marginLeft" :legendy="legendY" />
                </v-card>
              </div>
            </v-col>
          </v-row>
        </v-tab-item>
        <v-tab-item>
          <v-row>
            <v-col cols="12">
              <div class="mx-4">
                <v-card tile class="basic-chart" elevation="4">
                  <BasicChart :data="speed" :height="height" :left="marginLeft" :legendy="legendY" />
                </v-card>
              </div>
            </v-col>
          </v-row>
        </v-tab-item>
        <v-tab-item>
          <v-row>
            <v-col cols="12">
              <div class="mx-4">
                <v-card tile class="basic-chart" elevation="4">
                  <BasicChart :data="occupancy" :height="height" :left="marginLeft" :legendy="legendY" />
                </v-card>
              </div>
            </v-col>
          </v-row>
        </v-tab-item>
      </v-tabs>
      <v-row>
        <v-col class="px-10 py-0">
          <v-card tile class="basic-chart" elevation="4" v-if="url">
            <VideoPlayer ref="videoPlayer" :options="playerOptions" :caption="caption" />
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import VideoPlayer from '@/components/modules/traffic/common/VideoPlayer';

export default {
  props: {
    marker: Object
  },

  components: {
    BasicChart,
    VideoPlayer
  },

  data: () => ({
    selectedTab: 0,
    loading: false,
    showChartDialog: false,
    showVideoPlayer: false,
    height: 300,
    legendY: 5,
    marginLeft: 80,
    speed: {},
    volume: {},
    occupancy: {},
    info: {},
    cameraIds: [],
    interval: null,
    // Camera
    url: '',
    title: 'Video Player',
    caption: '',
    videoType: 'application/x-mpegURL'
  }),

  computed: {
    playerOptions() {
      return {
        // videojs options
        muted: true,
        language: 'en',
        playbackRates: [0.5, 1.0, 2.0, 3.0],
        controlBar: {
          fullscreenToggle: true,
          playbackRateMenuButton: true,
          pictureInPictureToggle: false,
          volumePanel: false
        },
        sources: [
          {
            withCredentials: false,
            type: this.videoType,
            src: this.url
          }
        ],
        poster: require('@/assets/loading.gif')
      };
    }
  },

  mounted() {
    if (this.marker) {
      this.init(this.marker);
      if (this.cameraIds && this.cameraIds.length > 0) {
        this.playVideo(this.cameraIds[0]);
      }
    }
    this.interval = setInterval(() => {
      if (this.$refs.weather) {
        this.$refs.weather.changeDay();
      }
      this.selectedTab = this.selectedTab + 1 > 2 ? 0 : this.selectedTab + 1;
    }, 20000);
  },

  beforeDestroy() {
    clearInterval(this.interval);
  },

  methods: {
    changeVideoSource(url) {
      this.url = url;
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.setUrl(this.url);
      }
    },
    changeTitle(title) {
      this.title = title;
    },
    changeCaption(caption) {
      this.caption = caption;
    },

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
      console.log(id);
      this.url = this.getVideoUrl(id);
      if (this.url) {
        this.changeVideoSource(this.url);
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
          result[direction] = this.composeInfo(deviceInfo[direction]);
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

    composeInfo(i) {
      const p2 = { speed: i.speed, volume: i.volume, occupancy: i.occupancy };
      const p3 = { 'avg Speed': i.avgSpd, 'avg Volume': i.avgVol, 'avg Occupancy': i.avgOcc };

      const p4 = {
        '5 Min': i.volume5MinMax,
        '1 Hr': i.volume1hrMax,
        '1 Hr Projected': i.volume1hrProjected
      };

      const p5 = { mdist: i.mdist, severity: i.severity };
      let statusName = 'Status';
      if (i.anomalyStatus > 0) {
        statusName = `Status ${i.anomalyStatus}`;
      }
      const text = i.anomalyStatus > 0 ? 'Anomaly' : 'Normal';
      p5[statusName] = i.anomalyStatus > 0 ? { text } : text;

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
    }
  },
  watch: {
    marker() {
      this.init(this.marker);
      if (this.cameraIds && this.cameraIds.length > 0) {
        this.playVideo(this.cameraIds[0]);
      }
    },
    cameraIds() {
      if (this.cameraIds && this.cameraIds.length > 0) {
        this.playVideo(this.cameraIds[0]);
      }
    }
  }
};
</script>

<style scoped>
.v-toolbar__extension {
  padding: 0px !important;
}
</style>

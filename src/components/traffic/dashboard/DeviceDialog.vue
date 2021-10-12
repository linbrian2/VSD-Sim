<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="900px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-map-marker</v-icon>
          <v-toolbar-title>{{ title }} </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container style="height:570px">
          <v-tabs color="teal accent-4" v-model="tab" @change="tabChanged">
            <v-tab key="0">Info</v-tab>
            <v-tab key="1">Speed</v-tab>
            <v-tab key="2">Volume</v-tab>
            <v-tab key="3">Occupancy</v-tab>
            <v-tab key="4" v-if="camerasAvaliable">Cameras</v-tab>
          </v-tabs>
          <div>
            <v-tabs-items v-model="tab">
              <v-tab-item key="0">
                <v-container>
                  <v-tabs vertical>
                    <v-tab v-for="(item, direction, i) in info" :key="10 + i">
                      <v-chip small>{{ direction }}</v-chip>
                    </v-tab>
                    <v-tab-item v-for="(infoItem, direction, i) in info" :key="10 + i">
                      <v-row>
                        <v-col cols="4" v-for="(item, j) in infoItem" :key="j">
                          <InfoCard :info="item" :extra="direction" />
                        </v-col>
                      </v-row>
                    </v-tab-item>
                  </v-tabs>
                </v-container>
              </v-tab-item>
              <v-tab-item key="1">
                <BasicChart :data="speed" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="2">
                <BasicChart :data="volume" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="3">
                <BasicChart :data="occupancy" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="4" v-if="camerasAvaliable">
                <div class="mx-16 mb-n2 mt-n1">
                  <v-radio-group v-model="cameraSelect" dense row @change="videoUrlChanged">
                    <v-radio v-for="(id, i) in cameraIds" :label="id" :value="id" :key="i"></v-radio>
                  </v-radio-group>
                </div>
                <div class="mx-16">
                  <VideoPlayer ref="videoPlayer" :options="playerOptions" v-if="tab == 4" />
                </div>
              </v-tab-item>
            </v-tabs-items>
          </div>
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import InfoCard from '@/components/traffic/common/InfoCard';
import VideoPlayer from '@/components/traffic/common/VideoPlayer';
import BasicChart from '@/components/traffic/common/BasicChart';
export default {
  props: {
    value: Boolean
  },
  components: {
    BasicChart,
    VideoPlayer,
    InfoCard
  },
  data: () => ({
    height: 470,
    legendY: 15,
    title: '',
    tab: null,
    cameraSelect: null,
    url: '',
    speed: {},
    volume: {},
    occupancy: {},
    info: {},
    cameraIds: []
  }),
  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },

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
            type: 'application/x-mpegURL',
            src: this.url
          }
        ],
        poster: require('@/assets/loading.gif')
      };
    },

    camerasAvaliable() {
      return !Utils.isEmpty(this.cameraIds);
    }
  },

  methods: {
    init(marker) {
      this.tab = 0;
      if (marker) {
        this.title = marker.name;
        this.fetchData(marker.id);
      }
    },

    cleanUp() {
      this.tab = 0;
    },

    hideDialog() {
      this.show = false;
      this.cleanUp();
    },

    videoUrlChanged() {
      this.changeVideoSource(this.getVideoUrl(this.cameraSelect));
    },

    tabChanged() {},

    changeVideoSource(url) {
      this.url = url;
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.setUrl(url);
      }
    },

    getVideoUrl(cameraId) {
      return `http://167.21.72.35:1935/live/${cameraId}.stream/playlist.m3u8`;
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

        if (this.cameraIds.length > 0) {
          this.cameraSelect = this.cameraIds[0];
          this.changeVideoSource(this.getVideoUrl(this.cameraSelect));
        }
      } catch (error) {
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
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },

    formSpeedData(flowList) {
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';
      let data = flowList.speed;

      return { data, xAxis, yAxis, title, ymin: 0, ymax: 100 };
    },

    formVolumeData(flowList) {
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Volume (vehicles/5min)';
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

    composeCameraUrl(i) {
      let result = [];
      let url0 = i.upCamUrl;
      let url1 = i.downCamUrl;
      if (url0) {
        result.push(url0);
      }
      if (url1) {
        result.push(url1);
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
        p7['Breakdown Probability'] = i.breakdown + '%';
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

<style lang="scss" scoped></style>

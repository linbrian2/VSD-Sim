<template>
  <div>
    <TitleBar :loading="loading" :refresh="refreshData">
      <v-btn-toggle class="mt-1" dense dark color="teal" v-model="selectedDirection" mandatory v-if="isJetsonCamera">
        <v-btn flat>NB</v-btn>
        <v-btn flat>SB</v-btn>
        <v-btn flat>EB</v-btn>
        <v-btn flat>WB</v-btn>
      </v-btn-toggle>
    </TitleBar>

    <div>
      <div class="d-flex justify-space-between">
        <div>
          <v-tabs color="teal accent-4" v-model="tab" @change="tabChanged">
            <v-tab key="0">Live Feed</v-tab>
            <v-tab key="1">Historical Video</v-tab>
            <v-tab key="2">Traffic Flow</v-tab>
          </v-tabs>
        </div>
        <div class="time-display">
          <v-row v-if="tab == 1">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <div v-on="on">
                  <v-btn outlined small dark icon class="ml-16 mr-10 mt-n2" @click="showTrafficHeatmap">
                    <v-icon>mdi-dots-grid</v-icon>
                  </v-btn>
                </div>
              </template>
              <span>Traffic Volumes Heatmap</span>
            </v-tooltip>
            <MenuTimePicker ref="timePicker" class="pa-0 ma-0 mt-n7 mr-10" />
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-btn icon @click="downloadClip" v-on="on" class="ml-1 mt-n2">
                  <v-icon color="gray">mdi-download</v-icon>
                </v-btn>
              </template>
              <span>Download video clip</span>
            </v-tooltip>
          </v-row>
        </div>
      </div>

      <div>
        <v-tabs-items v-model="tab">
          <v-tab-item key="0">
            <v-card class="my-3 mx-5">
              <VideoPlayer ref="liveVideoPlayer" :options="livePlayerOptions" v-if="tab == 0" />
            </v-card>
          </v-tab-item>
          <v-tab-item key="1">
            <v-card class="mt-3 mx-5" v-if="showHeatmap">
              <v-card-title class="d-flex justify-space-between">
                <h4 class="overline">Click the heatmap cell to select video</h4>
                <v-btn icon @click="showHeatmap = false"><v-icon>mdi-close</v-icon></v-btn>
              </v-card-title>
              <HeatMapChart :data="timeSeries" :height="heatMapHeight" @cell-click="cellClicked" />
            </v-card>
            <v-card class="mt-3 mx-5">
              <VideoPlayer ref="mp4VideoPlayer" :options="mp4PlayerOptions" v-if="tab == 1" />
            </v-card>
          </v-tab-item>
          <v-tab-item key="2">
            <div class="mt-3 mx-5">
              <TrafficFlowCharts />
            </div>
          </v-tab-item>
        </v-tabs-items>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Api from '@/utils/api/vision';
import { mapState } from 'vuex';
import Utils from '@/utils/Utils';
import TitleBar from '@/components/modules/vision/TitleBar';
import MenuTimePicker from '@/components/modules/vision/MenuTimePicker';
import VideoPlayer from '@/components/modules/vision/VideoPlayer';
import HeatMapChart from '@/components/modules/vision/HeatMapChart';
import TrafficFlowCharts from '@/components/modules/vision/TrafficFlowCharts';

export default {
  components: {
    TitleBar,
    MenuTimePicker,
    HeatMapChart,
    VideoPlayer,
    TrafficFlowCharts
  },
  data: () => ({
    tab: null,
    loading: false,
    showHeatmap: false,
    heatMapHeight: 500,
    info: null,
    visionResult: [],
    timeSeries: [],
    liveUrl: null,
    mp4Url: null,
    videoServer: process.env.VUE_APP_VIDEO_URL,
    selectedDirection: 0,
    cameraType: 'normal',
    Constants: {
      TAB_LIVE_FEED: 0,
      TAB_HISTORICAL: 1,
      TAB_TRAFFIC_FLOW: 2
    }
  }),

  computed: {
    livePlayerOptions() {
      // videojs options
      return {
        muted: true,
        language: 'en',
        playbackRates: [1.0],
        controlBar: {
          playToggle: false,
          remainingTimeDisplay: false,
          fullscreenToggle: true,
          playbackRateMenuButton: false,
          pictureInPictureToggle: false,
          volumePanel: false
        },
        sources: [
          {
            withCredentials: false,
            type: 'application/x-mpegURL',
            src: this.liveUrl
          }
        ],
        poster: require('@/assets/loading.gif')
      };
    },

    mp4PlayerOptions() {
      // videojs options
      return {
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
            type: 'video/mp4',
            src: this.mp4Url
          }
        ],
        poster: require('@/assets/loading.gif')
      };
    },

    isJetsonCamera() {
      return this.cameraType === 'Jetson';
    },

    video() {
      return this.info.uri;
    },
    ...mapState(['currentDate']),
    ...mapState('vision', ['activeMarker', 'currentTime'])
  },

  mounted() {
    this.fetchData();
  },

  watch: {
    activeMarker(marker) {
      if (marker != null) {
        this.fetchData();
      }
    },

    currentDate() {
      this.fetchData();
    },

    currentTime() {
      if (this.tab == this.Constants.TAB_HISTORICAL) {
        this.selectVideoByTime();
      }
    }
  },

  methods: {
    tabChanged() {
      if (this.tab == this.Constants.TAB_LIVE_FEED) {
        if (this.info) {
          this.liveUrl = this.info.uri;
          // Wait a monment to allow VideoPlayer created
          setTimeout(() => {
            this.changeLiveVideoSource(this.info.uri);
          }, 300);
        }
      } else if (this.tab == this.Constants.TAB_HISTORICAL) {
        let url = this.getVideoByTime();
        if (url) {
          this.mp4Url = url;
          // Wait a monment to allow VideoPlayer created
          setTimeout(() => {
            this.changeMp4VideoSource(url);
          }, 300);
        }
      }
    },

    showTrafficHeatmap() {
      this.showHeatmap = !this.showHeatmap;
      if (this.showHeatmap) {
        this.timeSeries = this.composeHeatMapData(this.visionResult);
      }
    },

    cellClicked(e) {
      let hour = e.x;
      let minute = e.y;
      let time = `${hour}:${Utils.pad(minute, 2)}`;
      this.$refs.timePicker.setTime(time);
      this.$store.commit('vision/SET_CURRENT_TIME', time);
    },

    downloadClip() {
      let video = this.getVideoByTime();
      if (video) {
        let url = video;
        let items = video.split('/');
        let label = items[items.length - 1];
        let baseURL = this.info.server ? this.info.server : this.videoServer;
        this.downloadItem({ url, baseURL, label });
      }
    },

    refreshData() {
      this.fetchData();
    },

    fetchData() {
      let camera = this.activeMarker;
      let time = this.currentDate.getTime();
      if (camera != null) {
        const { id, type } = camera;
        this.fetchDeviceInfo(id, type);
        this.loadVideoResultData(id, type, time);
      }
    },

    async fetchDeviceInfo(id, type) {
      if (this.info && this.info.id === id) {
        return;
      }

      this.loading = true;
      try {
        const response = await Api.fetchInfo(id, type);
        if (response.data.status === 'OK') {
          this.info = response.data.data;
          if (this.tab == this.Constants.TAB_LIVE_FEED) {
            this.changeLiveVideoSource(this.info.uri);
          }
        } else {
          this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    async loadVideoResultData(id, type, time) {
      this.loading = true;
      try {
        const response = await Api.fetchVideoResult(id, type, time);
        if (response.data.status === 'OK') {
          this.visionResult = response.data.data;

          if (this.tab == this.Constants.TAB_HISTORICAL) {
            this.timeSeries = this.composeHeatMapData(this.visionResult);
            this.selectVideoByTime(this.currentTime);
          }
        } else {
          this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'info' });
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    composeHeatMapData(visionResult) {
      let xcategories = [];
      let ycategories = [];

      let startTime = Utils.getStartOfDay(this.currentDate).getTime();

      const rowCount = 60;
      const colCount = 24;

      for (let i = 0; i < rowCount; i++) {
        ycategories.push(i);
      }
      for (let i = 0; i < colCount; i++) {
        xcategories.push(startTime + 3600000 * i);
      }

      // Initialize a map to represent 2D arrays
      let matrix = {};
      for (let row = 0; row < rowCount; row++) {
        for (let col = 0; col < colCount; col++) {
          matrix[row * colCount + col] = -1;
        }
      }

      // Add data to matrix
      for (let v of visionResult) {
        let time = new Date(v.time);
        const h = time.getHours();
        const m = time.getMinutes();
        matrix[m * colCount + h] = v.volume;
      }

      // Create a series
      let series = [];
      for (let x = 0; x < colCount; x++) {
        for (let y = 0; y < rowCount; y++) {
          series.push([x, y, matrix[y * colCount + x]]);
        }
      }

      let result = {};
      result.title = `Traffic Volumes on (${this.info.name})`;
      result.xAxis = 'Time of Day (hour)';
      result.yAxis = 'Time of Hour (min)';
      result.xcategories = xcategories;
      result.ycategories = ycategories;
      result.data = series;
      return result;
    },

    changeLiveVideoSource(url) {
      if (this.$refs.liveVideoPlayer) {
        this.$refs.liveVideoPlayer.setUrl(url);
      }
    },

    changeMp4VideoSource(url) {
      this.mp4Url = url;
      if (this.$refs.mp4VideoPlayer) {
        this.$refs.mp4VideoPlayer.setUrl(url);
      }
    },

    getVideoByTime() {
      let videoUrl = null;
      if (this.visionResult.length > 0) {
        let time = Utils.dateFromTimeString(this.currentDate, this.currentTime).getTime();
        let arr = this.visionResult.map(v => v.time);
        let index = Utils.getInsertionIndex(arr, time, (a, b) => a - b);
        if (index < 0 || index > this.visionResult.length - 1) {
          index = this.visionResult.length - 1;
        }

        let video = this.visionResult[index].video;
        if (!video) {
          // Loop backward to find another clip
          for (let i = index - 1; i >= 0; i--) {
            if (this.visionResult[i].video) {
              video = this.visionResult[i].video;
              break;
            }
          }
        }

        if (video) {
          const server = this.info.server ? this.info.server : this.videoServer;
          videoUrl = server + video.split('\\').join('/');
        }
      }
      return videoUrl;
    },

    selectVideoByTime() {
      let video = this.getVideoByTime();
      if (video) {
        this.changeMp4VideoSource(video);
      }
    },

    downloadItem({ url, baseURL, label }) {
      const api = axios.create({ baseURL });
      api
        .get(url, { responseType: 'blob' })
        .then(response => {
          const blob = new Blob([response.data], { type: 'application/octet-stream' });
          const link = document.createElement('a');
          link.href = URL.createObjectURL(blob);
          link.download = label;
          link.click();
          URL.revokeObjectURL(link.href);
        })
        .catch(console.error);
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

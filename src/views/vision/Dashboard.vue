<template>
  <div>
    <TitleBar :loading="loading" :refresh="refreshData">
      <v-row v-if="tab == 1">
        <MenuTimePicker ref="timePicker" class="pa-0 ma-0 mt-n3" />
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <div v-on="on">
              <v-btn outlined small dark icon class="ml-16 mt-2" @click="showTrafficHeatmap">
                <v-icon>mdi-dots-grid</v-icon>
              </v-btn>
            </div>
          </template>
          <span>Show/Hide Traffic Volumes Heatmap</span>
        </v-tooltip>
      </v-row>
    </TitleBar>
    <v-container>
      <v-tabs color="teal accent-4" v-model="tab" @change="tabChanged">
        <v-tab key="0">Live Feed</v-tab>
        <v-tab key="1">Historical Video</v-tab>
        <v-tab key="2">Traffic Flow</v-tab>
      </v-tabs>
      <div>
        <v-tabs-items v-model="tab">
          <v-tab-item key="0">
            <v-card class="my-5 mx-5">
              <VideoPlayer ref="liveVideoPlayer" :options="livePlayerOptions" v-if="tab == 0" />
            </v-card>
          </v-tab-item>
          <v-tab-item key="1">
            <v-card class="mt-3 mx-5" v-if="showHeatmap">
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
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/vision';
import { mapState } from 'vuex';
import Constants from '@/utils/constants/vision';
import Utils from '@/utils/Utils';
import TitleBar from '@/components/vision/TitleBar';
import MenuTimePicker from '@/components/vision/MenuTimePicker';
import VideoPlayer from '@/components/vision/VideoPlayer';
import HeatMapChart from '@/components/vision/HeatMapChart';
import TrafficFlowCharts from '@/components/vision/TrafficFlowCharts';

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

    videoServer: process.env.VUE_APP_VIDEO_URL
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

    video() {
      return this.info.uri;
    },
    ...mapState('vision', ['activeMarker', 'currentDate', 'currentTime'])
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
      if (this.tab == 2) {
        this.selectVideoByTime();
      }
    }
  },

  methods: {
    tabChanged() {
      switch (this.tab) {
        case Constants.TAB_LIVE_FEED:
          if (this.info) {
            this.liveUrl = this.info.uri;
            // Wait a monment to allow VideoPlayer created
            setTimeout(() => {
              this.changeLiveVideoSource(this.info.uri);
            }, 300);
          }
          break;
        case Constants.TAB_LIVE_PROCESSED:
          break;
        case Constants.TAB_HISTORICAL:
          {
            let url = this.getVideoByTime();
            if (url) {
              this.mp4Url = url;
              // Wait a monment to allow VideoPlayer created
              setTimeout(() => {
                this.changeMp4VideoSource(url);
              }, 300);
            }
          }
          break;
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

    refreshData() {
      this.fetchData();
    },

    fetchData() {
      let marker = this.activeMarker;
      let time = this.currentDate.getTime();
      if (marker != null) {
        const id = marker.id;
        this.fetchDeviceInfo(id);
        this.loadVideoResultData(id, time);
      }
    },

    async fetchDeviceInfo(id) {
      if (this.info && this.info.id === id) {
        return;
      }

      this.loading = true;
      try {
        const response = await Api.fetchInfo(id);
        if (response.data.status === 'OK') {
          this.info = response.data.data;
          if (this.tab == Constants.TAB_LIVE_FEED) {
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

    async loadVideoResultData(id, time) {
      this.loading = true;
      try {
        const response = await Api.fetchVideoResult(id, time);
        if (response.data.status === 'OK') {
          this.visionResult = response.data.data;

          if (this.tab == Constants.TAB_HISTORICAL) {
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
      result.title = `Traffic Volumes on ${this.info.desc} (${this.info.name})`;
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
          videoUrl = this.videoServer + video.split('\\').join('/');
        }
      }
      return videoUrl;
    },

    selectVideoByTime() {
      let video = this.getVideoByTime();
      if (video) {
        //console.log(video);
        this.changeMp4VideoSource(video);
      }
    }
  }
};
</script>

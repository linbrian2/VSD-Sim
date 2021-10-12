<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="900px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-car</v-icon>
          <v-toolbar-title
            >{{ title }}<small>{{ subtitle }} </small></v-toolbar-title
          >
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container style="height:570px">
          <v-tabs color="teal accent-4" v-model="tab">
            <v-tab key="0">Current Info</v-tab>
            <v-tab key="1">Travel Time</v-tab>
            <v-tab key="2">Traffic Speed</v-tab>
            <v-tab key="3">Nearby Cameras</v-tab>
          </v-tabs>
          <div>
            <v-tabs-items v-model="tab">
              <v-tab-item key="0">
                <v-container>
                  <v-row>
                    <v-col cols="4" v-for="(item, i) in info" :key="i">
                      <InfoCard :info="item" />
                    </v-col>
                  </v-row>
                </v-container>
              </v-tab-item>
              <v-tab-item key="1">
                <BasicChart :data="travelTime" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="2">
                <BasicChart :data="speed" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="3">
                <div v-if="cameras">
                  <div class="mx-16 mb-n2 mt-n1">
                    <v-radio-group v-model="cameraSelect" dense row @change="videoUrlChanged">
                      <v-radio v-for="(id, i) in cameraIds" :label="id" :value="id" :key="i"></v-radio>
                    </v-radio-group>
                  </div>
                  <div class="mx-16">
                    <VideoPlayer ref="videoPlayer" :options="playerOptions" v-if="tab == 3" />
                  </div>
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
import BasicChart from '@/components/traffic/common/BasicChart';
import VideoPlayer from '@/components/traffic/common/VideoPlayer';
export default {
  props: {
    value: Boolean
  },
  components: {
    BasicChart,
    InfoCard,
    VideoPlayer
  },
  data: () => ({
    height: 470,
    legendY: 15,
    title: '',
    subtitle: '',
    tab: null,
    segment: null,
    cameraSelect: null,
    cameraIds: [],
    speed: {},
    travelTime: {},
    info: {}
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

    cameras() {
      return this.cameraIds.length > 0;
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
    }
  },

  methods: {
    init(segment) {
      this.tab = 0;
      if (segment) {
        this.segment = segment;
        this.title = `${segment.id} - ${segment.name}`;
        let time = new Date().getTime();
        this.fetchTravelTimeData(segment.id, 300000, time);

        if (segment.cameras) {
          this.cameraIds = segment.cameras;
          if (this.cameraIds.length > 0) {
            this.cameraSelect = this.cameraIds[0];
            this.changeVideoSource(this.getVideoUrl(this.cameraSelect));
          }
        }

        if (this.$refs.mapSegmentRef) {
          this.$refs.mapSegmentRef.centerMapAndZoom();
        }
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

    changeVideoSource(url) {
      this.url = url;
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.setUrl(url);
      }
    },

    getVideoUrl(cameraId) {
      return `http://167.21.72.35:1935/live/${cameraId}.stream/playlist.m3u8`;
    },

    async fetchTravelTimeData(linkId, interval, time) {
      this.loading = true;
      try {
        // Now we await for both results, whose async processes have already been started
        const [travelTimeRes, deviceInfoRes] = await Promise.all([
          Api.fetchTravelTimeData(linkId, interval, time),
          Api.fetchLatestTravelTimeInfo(linkId)
        ]);

        let travelTimeList = this.parseResponseData(travelTimeRes);
        if (travelTimeList) {
          this.travelTime = this.formTravelTimeData(travelTimeList);
          this.speed = this.formSpeedData(travelTimeList);
        }

        let deviceInfo = this.parseResponseData(deviceInfoRes);
        if (deviceInfo) {
          this.info = this.formDeviceInfoData(deviceInfo);
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

    formSpeedData(travelTimeList) {
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.speed });
      data.push({ name: 'Baseline', color: '#058DC7', data: travelTimeList.baselineSpeed });

      return { data, xAxis, yAxis, title };
    },

    formTravelTimeData(travelTimeList) {
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Travel Time (s)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.travelTime });
      data.push({ name: 'Baseline', color: '#058DC7', data: travelTimeList.baselineTravelTime });

      return { data, xAxis, yAxis, title };
    },

    formDeviceInfoData(info) {
      let current = {
        'Travel Time': this.formatDuration(info.current.mean),
        'Average Speed': info.current.speed + ' mph'
      };

      let baseline = {
        'Travel Time': this.formatDuration(info.baseline.meanTravelTime),
        'Average Speed': info.baseline.avgLinkSpeed + ' mph'
      };

      let status = {
        'Max Travel Time': this.formatDuration(info.current.max),
        'Actual Wait Time': this.formatDuration(info.current.waitTime)
      };

      let result = [];
      let color = Constants.INFO_BAR_DEFAULT_COLOR;
      if (!Utils.isValueAllEmpty(current)) {
        result.push({ title: 'Current', color: color, items: Utils.obj2Arr(current) });
      }

      if (!Utils.isValueAllEmpty(baseline)) {
        result.push({ title: 'Baseline', color: color, items: Utils.obj2Arr(baseline) });
      }

      if (!Utils.isValueAllEmpty(status)) {
        result.push({ title: 'Status', color: color, items: Utils.obj2Arr(status) });
      }

      if (info.anomaly) {
        const text = info.anomaly.status === 7 ? 'Anomaly' : 'Normal';
        const status = {
          status: info.anomaly.status === 7 ? { text } : text,
          updated: Utils.formatTime(new Date(info.anomaly.updated))
        };
        let currentColor = info.anomaly.status == 7 ? 'red lighten-2' : color;
        result.push({ title: 'Detection', color: currentColor, items: Utils.obj2Arr(status) });
      }

      return result;
    },

    formatDuration(seconds) {
      let minutes = seconds / 60;
      return `${minutes.toFixed(1)} min`;
    }
  }
};
</script>

<style lang="scss" scoped>
.map-segment {
  height: 200px;
  margin-bottom: 20px;
  border-bottom: 2px solid black;
}
</style>

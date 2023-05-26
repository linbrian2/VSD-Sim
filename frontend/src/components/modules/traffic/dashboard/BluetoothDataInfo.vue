<template>
  <div>
    <v-container>
      <v-row id="info">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Current Info</h3></v-subheader>
            <div class="mt-4 mr-3">
              <v-chip class="ml-2 mt-n2" outlined small>
                <span>{{ segment.id }}</span>
              </v-chip>
            </div>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-row>
              <v-col cols="6" v-for="(item, j) in info" :key="j">
                <ListInfoCard :info="item" />
              </v-col>
            </v-row>
          </div>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Travel Time</h3></v-subheader>

            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showTravelTimeChart" class="mr-4 mt-2" :loading="loading">
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
              <BasicChart :data="travelTime" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
              ><h3>Traffic Speed</h3></v-subheader
            >
            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showTrafficSpeedChart" class="mr-4 mt-2" :loading="loading">
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

      <v-row v-if="camerasAvaliable">
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Nearby Cameras</h3></v-subheader>
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
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import ListInfoCard from '@/components/modules/traffic/common/ListInfoCard';
import BasicChart from '@/components/modules/traffic/common/BasicChart';

export default {
  props: {
    segment: Object
  },

  components: {
    BasicChart,
    ListInfoCard
  },

  data: () => ({
    loading: false,
    height: 300,
    legendY: 5,
    marginLeft: 80,
    cameraIds: [],
    speed: {},
    travelTime: {},
    info: {}
  }),

  computed: {
    cameras() {
      return this.cameraIds.length > 0;
    },

    camerasAvaliable() {
      return !Utils.isEmpty(this.cameraIds);
    }
  },

  mounted() {
    this.init(this.segment);
  },

  methods: {
    init(segment) {
      if (segment) {
        let time = new Date().getTime();
        this.fetchTravelTimeData(segment.id, 300000, time);

        if (segment.cameras) {
          this.cameraIds = segment.cameras;
        }

        if (this.$refs.mapSegmentRef) {
          this.$refs.mapSegmentRef.centerMapAndZoom();
        }
      }
    },

    showTrafficSpeedChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Traffic Speed', data: this.speed });
    },

    showTravelTimeChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Travel Time', data: this.travelTime });
    },

    playVideo(id) {
      this.$bus.$emit('PLAY_POPUP_VIDEO', id);
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
      }
      //  else {
      //   this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      // }
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
      let current = info.current
        ? {
            'Travel Time': this.formatDuration(info.current.mean),
            'Average Speed': info.current.speed + ' mph'
          }
        : {};

      let baseline = {
        'Travel Time': this.formatDuration(info.baseline.meanTravelTime),
        'Average Speed': info.baseline.avgLinkSpeed + ' mph'
      };

      let status = info.current
        ? {
            'Max Travel Time': this.formatDuration(info.current.max),
            'Actual Wait Time': this.formatDuration(info.current.waitTime)
          }
        : {};

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

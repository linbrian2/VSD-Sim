<template>
  <v-container>
    <v-row v-if="segment">
      <v-col cols="12">
        <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Basic Info</h3></v-subheader>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <div class="mx-4">
          <RouteCard :route="segment" />
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
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Traffic Speed</h3></v-subheader>
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

    <NearbyCameras :cameraIds="cameraIds" v-if="camerasAvaliable" />
  </v-container>
</template>

<script>
import Utils from '@/utils/Utils';
import RouteCard from '@/components/modules/traffic/dashboard/RouteCard';
import Api from '@/utils/api/traffic';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import NearbyCameras from '@/components/modules/traffic/common/NearbyCameras';

export default {
  props: {
    segment: Object
  },

  components: {
    RouteCard,
    BasicChart,
    NearbyCameras
  },

  data: () => ({
    loading: false,
    height: 300,
    legendY: 5,
    marginLeft: 80,
    speed: {},
    travelTime: {}
  }),

  computed: {
    cameraIds() {
      return this.segment ? this.segment.cameras : [];
    },

    camerasAvaliable() {
      return !Utils.isEmpty(this.cameraIds);
    }
  },

  watch: {
    segment() {
      if (this.segment) {
        this.init(this.segment);
      }
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
      }
    },

    showTrafficSpeedChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Traffic Speed', data: this.speed });
    },

    showTravelTimeChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Travel Time', data: this.travelTime });
    },

    async fetchTravelTimeData(linkId, interval, time) {
      this.loading = true;
      try {
        const travelTimeRes = await Api.fetchTravelTimeData(linkId, interval, time);
        let travelTimeList = this.parseResponseData(travelTimeRes);
        if (travelTimeList) {
          this.travelTime = this.formTravelTimeData(travelTimeList);
          this.speed = this.formSpeedData(travelTimeList);
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
    }
  }
};
</script>

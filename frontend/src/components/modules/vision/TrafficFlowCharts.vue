<template>
  <div>
    <v-card tile class="mb-4" v-if="availability.speed">
      <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
        <div class="ml-4 overline font-weight-bold">Traffic Speed</div>
        <v-btn small icon @click.stop="showChartInDialog" class="mr-2">
          <v-icon small>mdi-arrow-expand</v-icon>
        </v-btn>
      </v-card-title>
      <BasicChart :data="speed" :height="height" />
    </v-card>

    <v-card tile class="mb-4" v-if="availability.volume">
      <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
        <div class="ml-4 overline font-weight-bold">Traffic Volume</div>
        <v-btn small icon @click.stop="showChartInDialog" class="mr-2">
          <v-icon small>mdi-arrow-expand</v-icon>
        </v-btn>
      </v-card-title>
      <BasicChart :data="volume" :height="height" />
    </v-card>

    <v-card tile class="mb-4" v-if="availability.occupancy">
      <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
        <div class="ml-4 overline font-weight-bold">Traffic Occupancy</div>
        <v-btn small icon @click.stop="showChartInDialog" class="mr-2">
          <v-icon small>mdi-arrow-expand</v-icon>
        </v-btn>
      </v-card-title>
      <BasicChart :data="occupancy" :height="height" />
    </v-card>

    <v-card tile class="mb-4">
      <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
        <div class="ml-4 overline font-weight-bold">Traffic Counts</div>
        <v-spacer></v-spacer>

        <div style="width:150px; margin-right:120px">
          <v-select
            style="width:150px"
            dense
            v-model="interval"
            :items="items"
            item-text="text"
            item-value="value"
            hide-details
            prepend-icon="mdi-clock-outline"
            single-line
            @input="intervalSelected"
            color="black"
          ></v-select>
        </div>
        <v-btn small icon @click.stop="showChartInDialog" class="mr-2">
          <v-icon small>mdi-arrow-expand</v-icon>
        </v-btn>
      </v-card-title>
      <StackBarChart :data="counts" :interval="interval" :height="height" />
    </v-card>
  </div>
</template>

<script>
import Api from '@/utils/api/vision';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import BasicChart from '@/components/modules/vision/BasicChart';
import StackBarChart from '@/components/modules/vision/StackBarChart';

export default {
  components: {
    BasicChart,
    StackBarChart
  },

  data: () => ({
    loading: false,
    height: 500,
    interval: 1800000,
    countsLoading: false,
    items: [
      { text: '1 Hour', value: 3600000 },
      { text: '30 mins', value: 1800000 },
      { text: '10 mins', value: 600000 },
      { text: '5 mins', value: 300000 }
    ],
    counts: {},
    speed: {},
    volume: {},
    occupancy: {},

    availability: {
      speed: false,
      volume: false,
      occupancy: false
    }
  }),

  computed: {
    ...mapState(['currentDate']),
    ...mapState('vision', ['activeMarker'])
  },

  watch: {
    currentDate() {
      this.refreshData();
    },

    activeMarker() {
      this.refreshData();
    }
  },

  mounted() {
    this.$bus.$on('GET_FLOW', ({ marker }) => {
      this.fetchAllData(marker);
    });
    this.fetchData();
  },

  methods: {
    refreshData() {
      this.fetchData();
    },

    fetchData() {
      let marker = this.activeMarker;
      this.fetchAllData(marker);
    },

    fetchAllData(item) {
      if (item != null) {
        let time = this.currentDate.getTime();
        this.fetchVisionData(item.id, item.type, item.detector, time);
        this.fetchCounts(item.id, item.type, time, this.interval);
      }
    },

    intervalSelected() {
      let marker = this.activeMarker;
      let time = this.currentDate.getTime();
      if (marker != null) {
        this.fetchCounts(marker.id, marker.type, time, this.interval);
      }
    },

    async fetchVisionData(id, type, detectorId, time) {
      this.loading = true;
      try {
        let visionList = null;
        let flowList = null;

        if (detectorId) {
          // Now we await for both results, whose async processes have already been started
          const [vision, flow] = await Promise.all([
            Api.fetchVisionData(id, type, time),
            Api.fetchFlowData(detectorId, time)
          ]);
          visionList = this.getResponseData(vision);
          flowList = this.getResponseData(flow);
        } else {
          const vision = await Api.fetchVisionData(id, type, time);
          visionList = this.getResponseData(vision);
        }

        if (visionList || flowList) {
          this.speed = this.formSpeedData(visionList, flowList);
          this.volume = this.formVolumeData(visionList, flowList);
          this.occupancy = this.formOccupancyData(visionList, flowList);
          this.availability.speed = true;
          this.availability.volume = true;
          this.availability.occupancy = true;
        } else {
          this.speed = {};
          this.volume = {};
          this.occupancy = {};
          this.availability.speed = false;
          this.availability.volume = false;
          this.availability.occupancy = false;
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    async fetchCounts(id, type, time, interval) {
      this.loading = true;
      try {
        const response = await Api.fetchCounts(id, type, time, interval);
        if (response.data.status === 'OK') {
          if (response.data.data !== undefined) {
            let data = response.data.data;
            if (Object.keys(data).length > 0) {
              this.counts = this.formCountsData(data);
            }
          }
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    getResponseData(response) {
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

    formSpeedData(visionList, flowList) {
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';

      let data = [];
      if (visionList !== null) {
        data.push({ name: 'Speed', color: '#ED561B', data: visionList.speed });
      }
      if (flowList && flowList.speed) {
        data.push({ name: 'Baseline', color: '#058DC7', data: flowList.speed });
      }

      return { data, xAxis, yAxis };
    },

    formVolumeData(visionList, flowList) {
      let xAxis = 'Time of day';
      let yAxis = 'Volume (vehicles/5min)';

      let data = [];
      if (visionList !== null) {
        data.push({ name: 'Volume', color: '#ED561B', data: visionList.volume });
      }
      if (flowList && flowList.volume) {
        data.push({ name: 'Baseline', color: '#058DC7', data: flowList.volume });
      }
      return { data, xAxis, yAxis };
    },

    formOccupancyData(visionList, flowList) {
      let xAxis = 'Time of day';
      let yAxis = 'Occupancy (%)';

      let data = [];
      if (visionList !== null) {
        data.push({ name: 'Occupancy', color: '#ED561B', data: visionList.occupancy });
      }
      if (flowList && flowList.occupancy) {
        data.push({ name: 'Baseline', color: '#058DC7', data: flowList.occupancy });
      }
      return { data, xAxis, yAxis };
    },

    formCountsData(input) {
      let xAxis = 'Time of day';
      let yAxis = 'Vehicle Counts';

      let data = [];
      Object.keys(input).forEach(name => {
        let counts = input[name];
        data.push({ name: Utils.capitalize(name), data: counts });
      });

      return { data, xAxis, yAxis };
    },

    showChartInDialog() {}
  }
};
</script>

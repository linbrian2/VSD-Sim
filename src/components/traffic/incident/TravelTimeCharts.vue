<template>
  <div>
    <v-card tile class="mb-4" v-if="availability.travelTime">
      <BasicChart :data="travelTimeData" :height="height" />
      <div v-if="name" class="text-name">{{ name }}</div>
    </v-card>

    <v-card tile class="mb-4" v-if="availability.travelSpeed">
      <BasicChart :data="travelSpeedData" :height="height" />
      <div v-if="name" class="text-name">{{ name }}</div>
    </v-card>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import BasicChart from '@/components/traffic/common/BasicChart';
export default {
  props: {
    height: {
      type: Number,
      default: 470
    },
    linkId: String,
    name: {
      type: String,
      default: ''
    },
    interval: Number,
    time: Date,
    timeSlots: {
      type: Array,
      defualt: []
    }
  },

  components: {
    BasicChart
  },

  data: () => ({
    travelTimeData: {},
    travelSpeedData: {},
    availability: {
      travelTime: false,
      travelSpeed: false
    }
  }),

  mounted() {
    this.init(this.linkId, this.time, this.timeSlots);
  },

  methods: {
    init(linkId, time, timeSlots) {
      this.fetchTravelTimeData(linkId, this.interval, time.getTime(), timeSlots);
    },

    async fetchTravelTimeData(id, interval, time, timeSlots) {
      this.loading = true;
      try {
        // Now we await for both results, whose async processes have already been started
        const response = await Api.fetchTravelTimeData(id, interval, time);

        let travelTimeList = this.getResponseData(response);
        if (travelTimeList) {
          this.travelTimeData = this.formTravelTimeData(travelTimeList);
          this.travelSpeedData = this.formSpeedData(travelTimeList);
          if (timeSlots) {
            this.travelTimeData.timeSlots = timeSlots;
            this.travelSpeedData.timeSlots = timeSlots;
          }
          this.availability.travelTime = travelTimeList.speed ? true : false;
          this.availability.travelSpeed = travelTimeList.travelTime ? true : false;
        } else {
          this.availability.travelTime = false;
          this.availability.travelSpeed = false;
        }
      } catch (error) {
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
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
        this.$store.dispatch('traffic/setSystemStatus', { text: response.data.message, color: 'warning' });
      }
      return result;
    },

    formTravelTimeData(travelTimeList) {
      let title = 'Travel Time';
      let xAxis = 'Time of day';
      let yAxis = 'Travel Time (s)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.travelTime });
      data.push({ name: 'Baseline', color: '#50B432', data: travelTimeList.baselineTravelTime });

      return { data, xAxis, yAxis, title };
    },

    formSpeedData(travelTimeList) {
      let title = 'Travel Speed';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.speed });
      data.push({ name: 'Baseline', color: '#50B432', data: travelTimeList.baselineSpeed });

      return { data, xAxis, yAxis, title };
    }
  }
};
</script>

<style lang="scss">
.text-name {
  color: rgb(215, 215, 215);
  font-size: 8px;
  margin-left: 10px;
  padding-bottom: 5px;
}
</style>

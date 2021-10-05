<template>
  <div>
    <v-card class="mb-8" ref="spdCard" v-if="isSpeed">
      <BasicChart :data="speed" :height="height" />
      <div v-if="name" class="text-name">{{ name }}</div>
    </v-card>

    <v-card class="mb-8" v-if="isVolume">
      <BasicChart :data="volume" :height="height" />
      <div v-if="name" class="text-name">{{ name }}</div>
    </v-card>

    <v-card class="mb-8" v-if="isOccupancy">
      <BasicChart :data="occupancy" :height="height" />
      <div v-if="name" class="text-name">{{ name }}</div>
    </v-card>
  </div>
</template>

<script>
import BasicChart from '@/components/traffic/common/BasicChart';
export default {
  props: {
    height: {
      type: Number,
      default: 480
    },

    name: {
      type: String,
      default: ''
    },

    direction: {
      type: String,
      default: ''
    },

    data: Object,

    timeSlots: {
      type: Array,
      defualt: []
    }
  },

  components: {
    BasicChart
  },

  computed: {
    speed() {
      return this.formSpeedData(this.data, this.direction);
    },

    volume() {
      return this.formVolumeData(this.data, this.direction);
    },

    occupancy() {
      return this.formOccupancyData(this.data, this.direction);
    },

    isSpeed() {
      return !!this.data.speed;
    },

    isVolume() {
      return !!this.data.volume;
    },

    isOccupancy() {
      return !!this.data.occupancy;
    }
  },

  methods: {
    formSpeedData(flowList, direction) {
      const title = 'Speed';
      const xAxis = 'Time of day';
      const yAxis = 'Speed (mph)';
      const data = this.filterByDirection(flowList.speed, direction);
      const result = { data, xAxis, yAxis, title, min: 0, max: 100 };
      this.addTimeSlots(result);
      return result;
    },

    formVolumeData(flowList, direction) {
      const title = 'Volume';
      const xAxis = 'Time of day';
      const yAxis = 'Volume (vehicles/interval)';
      const data = this.filterByDirection(flowList.volume, direction);
      const result = { data, xAxis, yAxis, title };
      this.addTimeSlots(result);
      return result;
    },

    formOccupancyData(flowList, direction) {
      const title = 'Occupancy';
      const xAxis = 'Time of day';
      const yAxis = 'Occupancy (%)';
      const data = this.filterByDirection(flowList.occupancy, direction);
      const result = { data, xAxis, yAxis, title };
      this.addTimeSlots(result);
      return result;
    },

    addTimeSlots(data) {
      if (this.timeSlots && this.timeSlots.length > 0) {
        data.timeSlots = this.timeSlots;
      }
    },

    filterByDirection(data, direction) {
      if (!direction) {
        return data.filter(item => !item.name.startsWith('Baseline'));
      }
      return data.filter(item => item.bound === direction);
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

<template>
  <div>
    <v-card class="mb-8" ref="spdCard" v-if="isSpeed && (!limitGraph || limitGraph == 'Speed')">
      <BasicChart :data="speed" :height="height" />
    </v-card>

    <v-card class="mb-8" v-if="isVolume && (!limitGraph || limitGraph == 'Volume')">
      <BasicChart :data="volume" :height="height" />
    </v-card>

    <v-card class="mb-8" v-if="isOccupancy && (!limitGraph || limitGraph == 'Occupancy')">
      <BasicChart :data="occupancy" :height="height" />
    </v-card>
  </div>
</template>

<script>
import BasicChart from '@/components/modules/traffic/common/BasicChart';
export default {
  props: {
    limitGraph: {
      type: String,
      default: ''
    },

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
      const title = 'SBT0039-SBT0004: EB DE16 from DE5 to DE1';
      const subtitle = 'Speed';
      const xAxis = 'Time of day';
      const yAxis = 'Speed (mph)';
      const data = this.filterByDirection(flowList.speed, direction);
      const result = { data, xAxis, yAxis, title, subtitle, ymin: 0, ymax: 100 };
      this.addTimeSlots(result);
      this.addXAxisStart(result, flowList, direction);
      return result;
    },

    formVolumeData(flowList, direction) {
      const title = 'SBT0039-SBT0004: EB DE16 from DE5 to DE1';
      const subtitle = 'Volume';
      const xAxis = 'Time of day';
      const yAxis = 'Volume (vehicles/interval)';
      const data = this.filterByDirection(flowList.volume, direction);
      const result = { data, xAxis, yAxis, title, subtitle };
      this.addTimeSlots(result);
      this.addXAxisStart(result, flowList, direction);
      return result;
    },

    formOccupancyData(flowList, direction) {
      const title = 'SBT0039-SBT0004: EB DE16 from DE5 to DE1';
      const subtitle = 'Occupancy';
      const xAxis = 'Time of day';
      const yAxis = 'Occupancy (%)';
      const data = this.filterByDirection(flowList.occupancy, direction);
      const result = { data, xAxis, yAxis, title, subtitle };
      this.addTimeSlots(result);
      this.addXAxisStart(result, flowList, direction);
      return result;
    },

    addTimeSlots(data) {
      if (this.timeSlots && this.timeSlots.length > 0) {
        data.timeSlots = this.timeSlots;
      }
    },

    addXAxisStart(data, flowList, direction) {
      data.xmin = null;

      if (flowList.speed) {
        const list = this.filterByDirection(flowList.speed, direction);
        list.forEach(item => {
          data.xmin = data.xmin ? Math.min(data.xmin, item.data[0][0]) : item.data[0][0];
        });
      }

      if (flowList.volume) {
        const list = this.filterByDirection(flowList.volume, direction);
        list.forEach(item => {
          data.xmin = data.xmin ? Math.min(data.xmin, item.data[0][0]) : item.data[0][0];
        });
      }

      if (flowList.occupancy) {
        const list = this.filterByDirection(flowList.occupancy, direction);
        list.forEach(item => {
          data.xmin = data.xmin ? Math.min(data.xmin, item.data[0][0]) : item.data[0][0];
        });
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

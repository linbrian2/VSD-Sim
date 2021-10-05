<template>
  <div>
    <v-container grid-list-sm class="mt-2 mb-n4" v-if="vosList.length">
      <v-row wrap no-gutters>
        <v-col class="mr-3"><VosCard :vos="vosList[0]" color="red lighten-1"/></v-col>
        <v-col class="mr-3"><VosCard :vos="vosList[1]" color="blue lighten-1"/></v-col>
        <v-col class="mr-3"><VosCard :vos="vosList[2]" color="green"/></v-col>
        <v-col><VosCard :vos="vosList[3]" color="orange"/></v-col>
      </v-row>
    </v-container>

    <v-container class="mt-2">
      <v-card tile class="basic-chart mb-8" elevation="24" v-if="isSpeedAvailable">
        <BasicChart :data="speed" :height="height" />
        <div v-if="name" class="text-name">{{ name }}</div>
      </v-card>
      <v-card tile class="basic-chart mb-8" elevation="24" v-if="isVolumeAvailable">
        <BasicChart :data="volume" :height="height" />
        <div v-if="name" class="text-name">{{ name }}</div>
      </v-card>
      <v-card tile class="basic-chart mb-8" elevation="24" v-if="isOccupancyAvailable">
        <BasicChart :data="occupancy" :height="height" />
        <div v-if="name" class="text-name">{{ name }}</div>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import BasicChart from '@/components/traffic/common/BasicChart';
import VosCard from '@/components/traffic/common/VosCard';
export default {
  props: {
    speed: Object,
    volume: Object,
    occupancy: Object,
    vosList: Array,
    name: {
      type: String,
      default: ''
    }
  },

  components: {
    BasicChart,
    VosCard
  },

  data: () => ({
    height: 480
  }),

  computed: {
    isSpeedAvailable() {
      return !Utils.isEmpty(this.speed);
    },

    isVolumeAvailable() {
      return !Utils.isEmpty(this.volume);
    },

    isOccupancyAvailable() {
      return !Utils.isEmpty(this.occupancy);
    }
  }
};
</script>

<style lang="scss" scoped>
.basic-chart {
  height: 500px;
}
</style>

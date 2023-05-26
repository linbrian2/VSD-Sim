<template>
  <div>
    <div class="mx-3">
      <h4 class="overline ml-4">{{ name }}</h4>
      <DataQualityIssues :quality="quality" />
    </div>

    <v-container grid-list-sm class="mt-2 mb-n4" v-if="vosList.length">
      <v-row wrap no-gutters>
        <v-col class="mr-2"><VosCard :vos="vosList[0]" color="grey darken-1"/></v-col>
        <v-col class="mr-2"><VosCard :vos="vosList[1]" color="blue"/></v-col>
        <v-col class="mr-2"><VosCard :vos="vosList[2]" color="green"/></v-col>
        <v-col class="mr-2"><VosCard :vos="vosList[3]" color="teal"/></v-col>
        <v-col class="mr-2" v-if="vosList.length > 4"><VosCard :vos="vosList[4]" color="orange"/></v-col>
        <v-col v-if="vosList.length > 5"><VosCard :vos="vosList[5]" color="red"/></v-col>
      </v-row>
    </v-container>

    <v-container class="mt-2">
      <v-card tile class="pa-2 mb-8" elevation="24" v-if="isSpeedAvailable">
        <BasicChart :data="speed" :height="height" class="pb-2" />
        <!-- <div v-if="name" class="text-name">{{ name }}</div> -->
        <BasicChart :data="volume" :height="height" class="pb-2" />
        <!-- <div v-if="name" class="text-name">{{ name }}</div> -->
        <BasicChart :data="occupancy" :height="height" />
        <!-- <div v-if="name" class="text-name">{{ name }}</div> -->
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import VosCard from '@/components/modules/traffic/common/VosCard';
import DataQualityIssues from '@/components/modules/traffic/common/DataQualityIssues';
export default {
  props: {
    speed: Object,
    volume: Object,
    occupancy: Object,
    vosList: Array,
    quality: Object,
    name: {
      type: String,
      default: ''
    }
  },

  components: {
    BasicChart,
    VosCard,
    DataQualityIssues
  },

  computed: {
    height() {
      return this.$vuetify.breakpoint.mobile ? 320 : 480;
    },

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

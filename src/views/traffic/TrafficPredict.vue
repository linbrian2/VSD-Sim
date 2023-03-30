<template>
  <div>
    <div v-if="isFreeway">
      <TrafficLSTMPredict />
    </div>
    <div v-else-if="isArterial1">
      <TrafficGTSPredict :baseURL="baseURLs[0]" :key="baseURLs[0]" />
    </div>
    <div v-else-if="isArterial2">
      <TrafficGTSPredict :baseURL="baseURLs[1]" :key="baseURLs[1]" />
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import TrafficLSTMPredict from '@/components/modules/traffic/predict/TrafficLSTMPredict.vue';
import TrafficGTSPredict from '@/components/modules/traffic/predict/TrafficGTSPredict.vue';

export default {
  components: {
    TrafficLSTMPredict,
    TrafficGTSPredict
  },

  data: () => ({
    baseURLs: [process.env.VUE_APP_GTS_API_URL, process.env.VUE_APP_GTS_BEACH_API_URL]
  }),

  computed: {
    isFreeway() {
      return this.predictionMode === this.predictionModes[0];
    },

    isArterial1() {
      return this.predictionMode === this.predictionModes[1];
    },

    isArterial2() {
      return this.predictionMode === this.predictionModes[2];
    },

    ...mapState('traffic', ['predictionModes', 'predictionMode'])
  },

  mounted() {
    console.log(this.predictionMode);
  }
};
</script>

<style></style>

<template>
  <div>
    <div v-if="predictionMode == 'Freeway'">
      <TrafficLSTMPredict />
    </div>
    <div v-else>
      <TrafficGTSPredict />
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

  computed: {
    ...mapState('traffic', ['predictionMode'])
  },
  mounted() {
    console.log(this.predictionMode);
  }
};
</script>

<style></style>

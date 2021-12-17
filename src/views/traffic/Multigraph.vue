<template>
  <div class="multigraph">
    <div v-if="multigraphModeSelect == 'Traffic Flow Data'">
      <MultigraphFlow />
    </div>
    <div v-if="multigraphModeSelect == 'Travel Time Data'">
      <MultigraphTravelTime />
    </div>
    <div v-if="multigraphModeSelect == 'Weather Data'">
      <MultigraphWeather />
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import MultigraphFlow from '../../components/modules/traffic/multigraph/MultigraphFlow.vue';
import MultigraphTravelTime from '../../components/modules/traffic/multigraph/MultigraphTravelTime.vue';
import MultigraphWeather from '../../components/modules/traffic/multigraph/MultigraphWeather.vue';

export default {
  components: { MultigraphFlow, MultigraphWeather, MultigraphTravelTime },
  computed: {
    ...mapState('traffic', ['activeMarker', 'multigraphModeSelect'])
  }
};
</script>

<style></style>

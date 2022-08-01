<template>
  <div v-if="singleSelect == 'Single Select'">
    <div v-if="multigraphModeSelect == 'Traffic Flow Data'">
      <TrafficFlow />
    </div>
    <div v-if="multigraphModeSelect == 'Travel Time Data'">
      <TravelTimeData />
    </div>
    <div v-if="multigraphModeSelect == 'Weather Data'">
      <WeatherData />
    </div>
  </div>
  <div class="multigraph" v-else>
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
import TrafficFlow from '../../components/modules/traffic/multigraph/TrafficFlow.vue';
import TravelTimeData from '../../components/modules/traffic/multigraph/TravelTimeData.vue';
import WeatherData from '../../components/modules/traffic/multigraph/WeatherData.vue';

export default {
  components: {
    MultigraphFlow,
    MultigraphWeather,
    MultigraphTravelTime,
    TrafficFlow,
    TravelTimeData,
    WeatherData
  },

  computed: {
    ...mapState('traffic', ['activeMarker', 'multigraphModeSelect', 'singleSelect'])
  }
};
</script>

<style></style>

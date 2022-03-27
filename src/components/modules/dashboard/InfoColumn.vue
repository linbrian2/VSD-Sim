<template>
  <v-container class="pa-0 px-2" style="max-height: calc(100vh - 48px); overflow-y: auto;">
    <div id="traffic-incidents" v-if="selectedIdx == 0">
      <TrafficIncidents :handleRowClick="handleRowClick" />
    </div>
    <div id="traffic-flow-issues" v-else-if="selectedIdx == 1">
      <TrafficFlowIssues />
    </div>
    <div id="signal-performance-issues" v-else-if="selectedIdx == 2">
      <SignalPerformanceIssues />
    </div>
    <div id="device-anomalies" v-else-if="selectedIdx == 3">
      <DeviceAnomalies />
    </div>
    <div id="congested-routes" v-else-if="selectedIdx == 4">
      <CongestedRoutes :apiInfo="apiInfo" />
    </div>
    <div id="waze-alerts" v-else-if="selectedIdx == 5">
      <WazeAlerts />
    </div>
  </v-container>
</template>

<script>
import TrafficIncidents from '@/components/modules/dashboard/incidents/TrafficIncidents.vue';
import TrafficFlowIssues from '@/components/modules/dashboard/flow/TrafficFlowIssues.vue';
import DeviceAnomalies from '@/components/modules/dashboard/anomalies/DeviceAnomalies.vue';
import SignalPerformanceIssues from '@/components/modules/dashboard/issues/SignalPerformanceIssues.vue';
import CongestedRoutes from '@/components/modules/dashboard/routes/CongestedRoutes.vue';
import WazeAlerts from '@/components/modules/dashboard/waze/WazeAlerts.vue';

import Incidents from '@/utils/samples/Incidents.js';

export default {
  props: {
    apiInfo: Object,
    selectedIdx: Number,
    cardData: Array
  },
  components: {
    TrafficIncidents,
    TrafficFlowIssues,
    SignalPerformanceIssues,
    DeviceAnomalies,
    CongestedRoutes,
    WazeAlerts
  },
  data() {
    return {
      incidents: Incidents
    };
  },
  methods: {
    handleRowClick(data) {
      console.log('handleRowClick: %o', data);
    }
  }
};
</script>

<style></style>

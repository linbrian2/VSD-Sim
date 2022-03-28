<template>
  <v-container class="pa-0 px-2" style="max-height: calc(100vh - 48px); overflow-y: auto;">
    <div id="traffic-incidents" v-if="dataAvailable(cardData[0])" v-show="selectedIdx == 0">
      <TrafficIncidents :handleRowClick="handleRowClick" />
    </div>
    <div id="traffic-flow-issues" v-if="dataAvailable(cardData[1])" v-show="selectedIdx == 1">
      <TrafficFlowIssues :itemsPerPage="cardData[1].val" />
    </div>
    <div id="signal-performance-issues" v-if="dataAvailable(cardData[2])" v-show="selectedIdx == 2">
      <SignalPerformanceIssues :itemsPerPage="cardData[2].val" />
    </div>
    <div id="device-anomalies" v-if="dataAvailable(cardData[3])" v-show="selectedIdx == 3">
      <DeviceAnomalies :itemsPerPage="cardData[3].val" />
    </div>
    <div id="congested-routes" v-if="dataAvailable(cardData[4])" v-show="selectedIdx == 4">
      <CongestedRoutes :itemsPerPage="cardData[4].val" />
    </div>
    <div id="waze-alerts" v-if="dataAvailable(cardData[5])" v-show="selectedIdx == 5">
      <WazeAlerts :itemsPerPage="cardData[5].val" />
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
    },
    dataAvailable(data) {
      return data.val && data.val != 0 && data.val != '-' && data.val != 'N/A';
    }
  }
};
</script>

<style></style>

<template>
  <v-container class="pa-0 px-2">
    <div id="traffic-incidents" v-if="dataAvailable(cardData[0]) && selectedIdx == 0">
      <TrafficIncidents class="mt-1" :infoColumnCount="infoColumnCount" :handleRowClick="handleRowClick" />
    </div>
    <div id="traffic-flow-issues" v-if="dataAvailable(cardData[1]) && selectedIdx == 1">
      <TrafficFlowIssues class="mt-1" :maxItems="cardData[1].val" :infoColumnCount="infoColumnCount" />
    </div>
    <div id="signal-performance-issues" v-if="dataAvailable(cardData[2]) && selectedIdx == 2">
      <SignalPerformanceIssues class="mt-1" :maxItems="cardData[2].val" :infoColumnCount="infoColumnCount" />
    </div>
    <div id="device-anomalies" v-if="dataAvailable(cardData[3]) && selectedIdx == 3">
      <DeviceAnomalies class="mt-1" :maxItems="cardData[3].val" :infoColumnCount="infoColumnCount" />
    </div>
    <div id="congested-routes" v-if="dataAvailable(cardData[4]) && selectedIdx == 4">
      <CongestedRoutes class="mt-1" :maxItems="cardData[4].val" :infoColumnCount="infoColumnCount" />
    </div>
    <div id="waze-alerts" v-if="dataAvailable(cardData[5]) && selectedIdx == 5">
      <WazeAlerts class="mt-1" :maxItems="cardData[5].val" :infoColumnCount="infoColumnCount" />
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
import { mapGetters } from 'vuex';

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
  computed: {
    infoColumnCount() {
      return this.getSetting('dashboard', 'infoColumnCount');
    },
    listLimit() {
      if (this.getSetting) {
        return this.getSetting('dashboard', 'limitListings');
      } else {
        return 0;
      }
    },
    ...mapGetters(['getSetting'])
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

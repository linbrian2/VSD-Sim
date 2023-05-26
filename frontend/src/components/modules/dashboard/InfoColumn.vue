<template>
  <v-container class="pa-0 px-2">
    <div id="traffic-incidents" v-if="avialable(CARD_IDS.INCIDENTS_ID)">
      <TrafficIncidents class="mt-1" :maxItems="cardData[CARD_IDS.INCIDENTS_ID].val" />
    </div>
    <div id="traffic-flow-issues" v-if="avialable(CARD_IDS.FLOW_ANOMALIES_ID)">
      <TrafficFlowIssues class="mt-1" :maxItems="cardData[CARD_IDS.FLOW_ANOMALIES_ID].val" />
    </div>
    <div id="traffic-restrictions" v-if="avialable(CARD_IDS.RESTRICTIONS_ID)">
      <TrafficRestrictions class="mt-1" :maxItems="cardData[CARD_IDS.RESTRICTIONS_ID].val" />
    </div>
    <div id="signal-performance-issues" v-if="avialable(CARD_IDS.SIGNAL_ISSUES_ID)">
      <SignalIssues class="mt-1" :maxItems="cardData[CARD_IDS.SIGNAL_ISSUES_ID].val" />
    </div>
    <div id="device-anomalies" v-if="avialable(CARD_IDS.DEDVICE_ANOMALIES_ID)">
      <DeviceAnomalies class="mt-1" :maxItems="cardData[CARD_IDS.DEDVICE_ANOMALIES_ID].val" />
    </div>
    <div id="congested-routes" v-if="avialable(CARD_IDS.CONGESTED_ROUTES_ID)">
      <CongestedRoutes class="mt-1" :maxItems="cardData[CARD_IDS.CONGESTED_ROUTES_ID].val" />
    </div>
    <div id="waze-alerts" v-if="avialable(CARD_IDS.WAZE_ALERTS_ID)">
      <WazeAlerts class="mt-1" :maxItems="cardData[CARD_IDS.WAZE_ALERTS_ID].val" />
    </div>
  </v-container>
</template>

<script>
import TrafficIncidents from '@/components/modules/dashboard/views/TrafficIncidents.vue';
import TrafficFlowIssues from '@/components/modules/dashboard/views/TrafficFlowIssues.vue';
import DeviceAnomalies from '@/components/modules/dashboard/views/DeviceAnomalies.vue';
import SignalIssues from '@/components/modules/dashboard/views/SignalIssues.vue';
import CongestedRoutes from '@/components/modules/dashboard/views/CongestedRoutes.vue';
import WazeAlerts from '@/components/modules/dashboard/views/WazeAlerts.vue';
import TrafficRestrictions from '@/components/modules/dashboard/views/TrafficRestrictions.vue';
import Constants from '@/utils/constants/dashboard.js';
import { mapGetters } from 'vuex';

export default {
  props: {
    selectedIdx: Number,
    cardData: Array
  },
  components: {
    TrafficIncidents,
    TrafficFlowIssues,
    SignalIssues,
    DeviceAnomalies,
    CongestedRoutes,
    WazeAlerts,
    TrafficRestrictions
  },
  data() {
    return {
      incidents: null
    };
  },
  computed: {
    ...mapGetters(['getSetting'])
  },
  created() {
    this.CARD_IDS = {
      INCIDENTS_ID: Constants.CARD_DATA_INCIDENTS_ID,
      RESTRICTIONS_ID: Constants.CARD_DATA_RESTRICTIONS_ID,
      SIGNAL_ISSUES_ID: Constants.CARD_DATA_SIGNAL_ISSUES_ID,
      DEDVICE_ANOMALIES_ID: Constants.CARD_DATA_DEDVICE_ANOMALIES_ID,
      CONGESTED_ROUTES_ID: Constants.CARD_DATA_CONGESTED_ROUTES_ID,
      WAZE_ALERTS_ID: Constants.CARD_DATA_WAZE_ALERTS_ID
    };
  },
  methods: {
    dataAvailable(data) {
      return data && data.val && data.val != 0 && data.val != '-' && data.val != 'N/A';
    },
    avialable(idx) {
      return this.cardData && this.dataAvailable(this.cardData[idx]) && this.selectedIdx == idx;
    }
  }
};
</script>

<style></style>

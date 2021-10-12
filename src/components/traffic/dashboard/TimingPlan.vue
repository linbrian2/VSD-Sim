<template>
  <v-data-table
    :height="height"
    fixed-header
    :headers="headers"
    :items="timingPlans"
    disable-sort
    hide-default-footer
    class="elevation-1"
  >
  </v-data-table>
</template>

<script>
import Api from '@/utils/api/traffic';
export default {
  props: {
    height: { type: Number, default: 250 },
    planId: String
  },

  data: () => ({
    headers: [
      { text: 'INT', value: 'intId' },
      { text: 'S1', value: 's1' },
      { text: 'S2', value: 's2' },
      { text: 'S3', value: 's3' },
      { text: 'S4', value: 's4' },
      { text: 'S5', value: 's5' },
      { text: 'S6', value: 's6' },
      { text: 'S7', value: 's7' },
      { text: 'S8', value: 's8' },
      { text: 'CL', value: 'cl' },
      { text: 'OFF', value: 'off' }
    ],
    timingPlans: []
  }),

  mounted() {
    this.init(this.planId);
  },

  methods: {
    init(planId) {
      if (planId) {
        this.fetchTimingPlan(planId);
      }
    },

    async fetchTimingPlan(planId) {
      try {
        const response = await Api.fetchTimingPlan(planId);
        if (response.data.status === 'OK') {
          if (response.data.data !== undefined) {
            this.timingPlans = response.data.data;
          }
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    }
  }
};
</script>

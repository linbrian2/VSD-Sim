<template>
  <div>
    <div class="d-flex justify-space-between">
      <div>
        <v-tabs color="teal accent-4" v-model="activeTab" show-arrows>
          <v-tab v-for="item in simulationScenarios" :key="item.planId" :href="`#${item.planId}`">
            {{ item.planId }}
          </v-tab>
        </v-tabs>
      </div>
      <div>{{ activeTab }} s</div>
    </div>

    <div>
      <v-tabs-items>
        <v-tab-item v-for="item in simulationScenarios" :key="`${item.planId}`" :value="item.planId">
          <v-data-table
            :height="height"
            fixed-header
            :headers="headers"
            :items="item.timingPlan"
            disable-sort
            hide-default-footer
            class="elevation-1"
          >
          </v-data-table>
        </v-tab-item>
      </v-tabs-items>
    </div>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
export default {
  props: {
    height: { type: Number, default: 250 },
    type: String
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
    simulationScenarios: [],
    activeTab: null
  }),

  computed: {
    activeMOE() {
      console.log(this.activeTab);
      return this.activeTab ? this.simulationScenarios[this.activeTab].moe : null;
    }
  },

  mounted() {
    this.init(this.type);
  },

  methods: {
    init(type) {
      if (type) {
        this.fetchfetchSimulationScenarios(type);
      }
    },

    async fetchfetchSimulationScenarios(type) {
      try {
        const response = await Api.fetchSimulationScenarios(type);
        if (response.data.status === 'OK') {
          if (response.data.data !== undefined) {
            this.simulationScenarios = response.data.data;
          }
        }
      } catch (error) {
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
      }
    }
  }
};
</script>

<template>
  <div>
    <v-row justify="center">
      <v-dialog v-model="show" hide-overlay max-width="900px">
        <v-card>
          <v-app-bar color="#009688" dark dense>
            <v-icon class="mt-n1 mr-2">mdi-sitemap </v-icon>
            <v-toolbar-title>M-dist Detection Flow Chart</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon @click="show = false">
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </v-app-bar>
          <v-container style="height:570px">
            <FlowChart ref="flowChart" />
          </v-container>
        </v-card>
      </v-dialog>
    </v-row>
  </div>
</template>

<script>
import FlowChart from '@/components/modules/traffic/app/FlowChart';

export default {
  components: {
    FlowChart
  },

  props: {
    value: Boolean
  },

  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$store.commit('traffic/SHOW_FLOW_CHART', value);
      }
    }
  },

  methods: {
    init(params, status) {
      if (this.$refs.flowChart) {
        this.$refs.flowChart.update(params, status);
      }
    }
  }
};
</script>

<style scoped>
.v-toolbar__extension {
  padding: 0px !important;
}
</style>

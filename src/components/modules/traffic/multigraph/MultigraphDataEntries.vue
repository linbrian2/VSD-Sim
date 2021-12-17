<template>
  <v-row>
    <v-col cols="12" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
      <template v-if="i.data && i.data[param] && !flowParams.isFlow">
        <div class="graph-container">
          <v-btn icon @click="removeItem(i)" class="graph-close-button"><v-icon>mdi-close</v-icon></v-btn>
        </div>
        <BasicChart :data="i.data[param]" :height="height" />
      </template>
      <template v-else-if="i.data && i.data[param] && flowParams.isFlow && !isEmpty(i.data[param]) &&
          objContainsKey(i.data[param])"
      >
        <!-- {{ i.data[param] }} -->
        <div class="graph-container">
          <v-btn icon @click="removeItem(i)" class="graph-close-button"><v-icon>mdi-close</v-icon></v-btn>
        </div>
        <TrafficFlowMultigraphCombinedCharts
          :data="i.data[param]"
          :name="i.name"
          :direction="flowParams.direction"
          :limitGraph="flowParams.limitGraph"
        />
      </template>
      <template
        v-else-if="i.data == -1 || (i.data && i.data[param] 
        && (i.data[param].length == 0 || isEmpty(i.data[param]) || !objContainsKey(i.data[param])))"
      >
        <div class="graph-container">
          <v-btn icon @click="removeItem(i)" class="graph-close-button"><v-icon>mdi-close</v-icon></v-btn>
        </div>
        <v-card max-width :height="height" color="#333333" class="pa-6">
          <v-row class="grid-center">
            <h3>{{ i.name }}</h3>
            <h1 style="color: #ff6666; margin-top: 160px">Data is unavailable</h1>
          </v-row>
        </v-card>
      </template>
      <template v-else>
        <div class="graph-container">
          <v-btn icon @click="removeItem(i)" class="graph-close-button"><v-icon>mdi-close</v-icon></v-btn>
        </div>
        <v-card max-width :height="height" color="#333333" class="pa-6">
          <v-row class="grid-center">
            <h3>{{ i.name }}</h3>
            <h1 style="margin-top: 160px">Loading data...</h1>
          </v-row>
        </v-card>
      </template>
    </v-col>
  </v-row>
</template>

<script>
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import TrafficFlowMultigraphCombinedCharts from '@/components/modules/traffic/multigraph/TrafficFlowMultigraphCombinedCharts';

export default {
  props: {
    valuesSelected: { type: Array },
    param: { type: String },
    flowParams: {
      type: Object,
      default() {
        return {
          isFlow: false,
        };
      },
    },
  },
  components: {
    BasicChart,
    TrafficFlowMultigraphCombinedCharts,
  },
  data() {
    return {
      height: 480,
    };
  },
  methods: {
    removeItem(item) {
      this.$emit('removeItem', item);
    },
    isEmpty(obj) {
      if (obj) {
        return Object.keys(obj).length === 0;
      } else {
        return true;
      }
    },
    objContainsKey(obj) {
      let key = this.flowParams.limitGraph;
      return key.toLowerCase() in obj;
    },
  },
};
</script>

<style>
.graph-container {
  position: relative;
}
.graph-close-button {
  position: absolute;
  right: 0px;
  z-index: 99;
}
</style>
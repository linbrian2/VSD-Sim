<template>
  <v-row>
    <v-col :cols="cols" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
      <template v-if="i.data && i.data[param] && !flowParams.isFlow">
        <div class="graph-container">
          <v-btn icon @click="removeItem(i)" class="graph-close-button"><v-icon>mdi-close</v-icon></v-btn>
        </div>
        <BasicChart :data="i.data[param]" :height="height" />
      </template>
      <template
        v-else-if="
          i.data && i.data[param] && flowParams.isFlow && !isEmpty(i.data[param]) && objContainsKey(i.data[param])
        "
      >
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
        v-else-if="
          i.data == -1 ||
            (i.data &&
              i.data[param] &&
              (i.data[param].length == 0 || isEmpty(i.data[param]) || !objContainsKey(i.data[param])))
        "
      >
        <div class="graph-container">
          <v-btn icon @click="removeItem(i)" class="graph-close-button"><v-icon>mdi-close</v-icon></v-btn>
        </div>
        <v-card v-if="!$vuetify.breakpoint.mobile" max-width :height="height" color="#333333" class="pa-6">
          <v-row class="grid-center">
            <h3>{{ i.name }}</h3>
            <h1 :style="`color: #aaa; margin-top: ${marginTop}`">NO DATA</h1>
          </v-row>
        </v-card>
        <v-card v-else max-width color="#333333" class="pa-6">
          <v-row class="grid-center">
            <h3>{{ i.name }} - (No Data)</h3>
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
            <h1 :style="`color: #aaa; margin-top: ${marginTop}`">Loading data...</h1>
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
    cols: { type: Number, default: 12 },
    valuesSelected: { type: Array },
    param: { type: String },
    flowParams: {
      type: Object,
      default() {
        return {
          isFlow: false
        };
      }
    }
  },
  components: {
    BasicChart,
    TrafficFlowMultigraphCombinedCharts
  },
  computed: {
    height() {
      return this.$vuetify.breakpoint.mobile ? 300 : 480;
    },
    marginTop() {
      return this.$vuetify.breakpoint.mobile ? '80px' : '160px';
    }
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
    }
  }
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

<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="1000px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-chart-line</v-icon>
          <v-toolbar-title>{{ title }} </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container style="width: 960px; height:600px">
          <BasicChart ref="chart" :data="data" :height="height" :legendy="legendY" />
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import BasicChart from '@/components/modules/traffic/common/BasicChart';
export default {
  props: {
    value: Boolean
  },

  components: {
    BasicChart
  },

  data: () => ({
    legendY: 5,
    title: '',
    data: {},
    height: 550
  }),

  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    }
  },

  methods: {
    init(title, data) {
      this.title = title;
      this.data = data;

      setTimeout(() => {
        if (this.$refs.chart) {
          this.$refs.chart.updateScaling();
        }
      }, 300);
    },

    hideDialog() {
      this.show = false;
    }
  }
};
</script>

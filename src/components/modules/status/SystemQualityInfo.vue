<template>
  <v-container>
    <v-row v-if="totalErrorCounts">
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
            <h3>Error Counts By Time</h3>
          </v-subheader>

          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showStackChart" class="mr-4 mt-2" :loading="loading">
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <HeatmapChart :data="totalErrorCounts" :height="chartHeight" class="mx-4" />
      </v-col>
    </v-row>

    <v-row v-if="errorCountsByType">
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
            <h3>Error Counts by Type</h3>
          </v-subheader>

          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showStackChart" class="mr-4 mt-2" :loading="loading">
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <PieChart :data="errorCountsByType" :height="chartHeight" class="mx-4" />
      </v-col>
    </v-row>

    <v-row v-if="errorCountsByHour">
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
            <h3>Error Counts by Hour</h3>
          </v-subheader>

          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showStackChart" class="mr-4 mt-2" :loading="loading">
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <PieChart :data="errorCountsByHour" :height="chartHeight" class="mx-4" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Api from '@/utils/api/status';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/status';
import { mapState } from 'vuex';
import HeatmapChart from '@/components/modules/status/HeatmapChart';
import PieChart from '@/components/modules/status/PieChart';

export default {
  props: {
    marker: Object
  },

  components: {
    PieChart,
    HeatmapChart
  },

  data: () => ({
    loading: false,
    updatedTime: new Date(),

    totalErrorCounts: null,
    errorCountsByType: null,
    errorCountsByHour: null
  }),

  computed: {
    chartHeight() {
      return this.$vuetify.breakpoint.mobile ? 360 : 450;
    },
    panelStyle() {
      return this.darkMode ? 'pa-1 pl-3 grey darken-3' : 'pa-1 pl-3 primary';
    },
    ...mapState(['darkMode', 'currentDate'])
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  async mounted() {
    this.refreshData();
  },

  methods: {
    refreshData() {
      this.fetchStatus(this.currentDate);
    },

    async fetchStatus(date) {
      this.loading = true;
      try {
        this.updatedTime = new Date();
        const response = await Api.fetchErrors(date.getTime());
        const data = this.parseResponseData(response);

        if (data) {
          this.totalErrorCounts = this.composeTotalErrorCountsHeatMapData(data.totalErrorCounts);
          this.errorCountsByType = this.composeErrorByTypePieChartData(data.errorCountsByType);
          this.errorCountsByHour = this.composeErrorByHourPieChartData(data.errorCountsByHour);
        }
      } catch (error) {
        console.log(error);
      }
      this.loading = false;
    },

    parseResponseData(response) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },

    composeTotalErrorCountsHeatMapData(totalErrorCounts) {
      let xcategories = [];
      let ycategories = [];

      let startTime = Utils.getStartOfDay(this.currentDate).getTime();

      const rowCount = 12;
      const colCount = 24;

      for (let i = 0; i < rowCount; i++) {
        ycategories.push(i * 5);
      }
      for (let i = 0; i < colCount; i++) {
        xcategories.push(startTime + 3600000 * i);
      }

      // Initialize a map to represent 2D arrays
      let matrix = {};
      for (let row = 0; row < rowCount; row++) {
        for (let col = 0; col < colCount; col++) {
          matrix[row * colCount + col] = totalErrorCounts[col * rowCount + row];
        }
      }

      // Create a series
      let series = [];
      for (let x = 0; x < colCount; x++) {
        for (let y = 0; y < rowCount; y++) {
          series.push([x, y, matrix[y * colCount + x]]);
        }
      }

      let result = {};
      result.title = `Total Error Counts`;
      result.xAxis = 'Time of Day (hour)';
      result.yAxis = 'Time of Hour (min)';
      result.xcategories = xcategories;
      result.ycategories = ycategories;
      result.data = series;
      return result;
    },

    composeErrorByTypePieChartData(data) {
      // Transform counts to percentages
      const total = data.reduce((sum, v) => (sum += v), 0);
      const percentages = data.map(v => (v * 100.0) / total);

      // Create pie chart series data
      const series = [];
      for (let i = 1; i < percentages.length; i++) {
        series.push({
          name: Constants.QUALITY_ERROR_NAMES[i],
          y: percentages[i]
        });
      }

      // Find the maximum index
      const maxIdx = percentages.indexOf(Math.max(...percentages));
      series[maxIdx].sliced = true;
      series[maxIdx].selected = true;

      const title = 'Error Counts Percentage By Type';

      return { series, title };
    },

    composeErrorByHourPieChartData(data) {
      // Transform counts to percentages
      const total = data.reduce((sum, v) => (sum += v), 0);
      const percentages = data.map(v => (v * 100.0) / total);

      // Create pie chart series data
      const series = percentages.map((value, index) => {
        const name = Utils.formatNumberToTime(index);
        return { name, y: value };
      });

      // Find the maximum index and set sliced
      const maxIdx = percentages.indexOf(Math.max(...percentages));
      series[maxIdx].sliced = true;
      series[maxIdx].selected = true;

      const title = 'Error Counts Percentage By Hour';

      return { series, title };
    }
  }
};
</script>

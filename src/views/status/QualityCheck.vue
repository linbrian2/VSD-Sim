<template>
  <div>
    <TitleBar
      :title="$vuetify.breakpoint.mobile ? 'Data Quality' : 'Traffic Flow Data Quality Checking'"
      :loading="loading"
      :refresh="refreshData"
    >
      <!-- <MenuDatePicker class="pa-0 ma-0 mt-n3" /> -->
      <div class="update" v-if="updatedTime">{{ updatedTime | date }}</div>
    </TitleBar>

    <v-container fluid :style="`${$vuetify.breakpoint.mobile ? 'max-width: 100%' : 'max-width: 85%'}`">
      <v-card tile class="mt-6 mb-4">
        <v-card-title :class="panelStyle">
          <span class="title white--text font-weight-light">Total Error Counts</span>
        </v-card-title>

        <div class="mt-2" v-if="totalErrorCounts">
          <HeatmapChart :data="totalErrorCounts" :height="chartHeight" />
        </div>
      </v-card>

      <v-card tile class="mt-6 mb-4">
        <v-card-title :class="panelStyle">
          <span class="title white--text font-weight-light">Error Counts by Type and Hour </span>
        </v-card-title>
        <v-row>
          <v-col :cols="$vuetify.breakpoint.mobile ? 12 : 6">
            <div class="mt-2" v-if="errorCountsByType">
              <PieChart :data="errorCountsByType" :height="chartHeight" />
            </div>
          </v-col>

          <v-col :cols="$vuetify.breakpoint.mobile ? 12 : 6">
            <div class="mt-2" v-if="errorCountsByHour">
              <PieChart :data="errorCountsByHour" :height="chartHeight" />
            </div>
          </v-col>
        </v-row>
      </v-card>

      <v-card tile class="mt-6 mb-4">
        <v-card-title :class="panelStyle">
          <span class="title white--text font-weight-light">Error Counts by Sensor </span>
          <v-spacer></v-spacer>
          <!-- Region selection menu -->
          <div style="width:280px;">
            <v-select
              dark
              dense
              v-model="selectedRegionId"
              :items="region_items"
              item-text="title"
              item-value="value"
              hide-details
              prepend-icon="mdi-check-box-outline "
              single-line
            />
          </div>

          <v-spacer></v-spacer>
          <v-text-field
            dark
            rounded
            dense
            outlined
            clearable
            v-model="search"
            prepend-inner-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
            class="shrink mr-5"
          ></v-text-field>

          <v-btn small icon class="refresh-btn mr-3" :loading="sensorLoading">
            <v-icon color="white">mdi-refresh</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-subtitle color="#26c6da" class="mt-2 ml-3 font-italic" v-if="!sensorErrorTypes">
          <div>
            <div class="ml-3 py-2">
              Click a table row to reveal more details about the selected detector
            </div>
          </div>
        </v-card-subtitle>

        <v-card-text style="height: 340px;">
          <v-data-table
            :headers="headers"
            height="300"
            fixed-header
            :items="items"
            :items-per-page="itemsPerPage"
            hide-default-footer
            @click:row="handleRowClick"
            :search="search"
            class="elevation-1"
          >
            <template v-slot:[`item.id`]="{ item }">
              <v-chip color="success" outlined small style="width:62px;">{{ item.id }}</v-chip>
            </template>
          </v-data-table>
        </v-card-text>

        <div class="ml-8 mr-2 mb-4 sensor-chart" v-if="sensorErrorTypes">
          <v-tabs color="deep-purple accent-4" v-model="activeTab" show-arrows>
            <v-tab v-for="({ dir }, i) in sensorErrorTypes" :key="i">
              <v-chip color="orange" small>{{ dir }}</v-chip>
            </v-tab>
            <v-tab-item v-for="({ errorTypes }, i) in sensorErrorTypes" :key="i">
              <v-container fluid>
                <SensorHeatmapChart :data="errorTypes" :height="450" />
              </v-container>
            </v-tab-item>
          </v-tabs>
        </div>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/status';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/status';
import TrafficConstants from '@/utils/constants/traffic';
import { mapState } from 'vuex';
import TitleBar from '@/components/modules/status/TitleBar';
import HeatmapChart from '@/components/modules/status/HeatmapChart';
import SensorHeatmapChart from '@/components/modules/status/SensorHeatmapChart';
import PieChart from '@/components/modules/status/PieChart';

export default {
  components: {
    TitleBar,
    PieChart,
    HeatmapChart,
    SensorHeatmapChart
  },

  data: () => ({
    loading: false,
    sensorLoading: false,
    updatedTime: new Date(),

    totalErrorCounts: null,
    errorCountsByType: null,
    sensorErrorCounts: null,
    errorCountsByHour: null,
    sensorErrorTypes: null,

    search: '',
    itemsPerPage: 500,
    allItems: [],
    headers: [],

    activeTab: null,

    region_items: TrafficConstants.TRAFFIC_DEVICE_CATEGORIES,
    selectedRegionId: -1
  }),

  computed: {
    chartHeight() {
      return this.$vuetify.breakpoint.mobile ? 360 : 600;
    },
    panelStyle() {
      return this.darkMode ? 'pa-1 pl-3 grey darken-3' : 'pa-1 pl-3 primary';
    },

    items() {
      if (this.selectedRegionId < 0) {
        return this.allItems;
      } else {
        return this.allItems.filter(item => item.flags === this.selectedRegionId);
      }
    },
    ...mapState(['darkMode', 'currentDate'])
  },

  filters: {
    date: function(date) {
      return 'Updated time: ' + Utils.formatDateAndTime(date);
    }
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
    handleRowClick(value) {
      this.fetchSensorStatus(value.id, this.currentDate);
      this.scrollToElement('sensor-chart');
    },

    scrollToElement(className) {
      const el = this.$el.getElementsByClassName(className)[0];
      if (el) {
        // Use el.scrollIntoView() to instantly scroll to the element
        el.scrollIntoView({ behavior: 'smooth' });
      }
    },

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
          this.prepareSensorErrorCounts(data.sensorErrorCounts);
          this.errorCountsByHour = this.composeErrorByHourPieChartData(data.errorCountsByHour);
          this.sensorErrorTypes = null;
        }
      } catch (error) {
        console.log(error);
      }
      this.loading = false;
    },

    async fetchSensorStatus(deviceId, date) {
      this.sensorLoading = true;
      try {
        const response = await Api.fetchSensorErrors(deviceId, date.getTime());
        const allErrorTypes = this.parseResponseData(response);

        if (allErrorTypes) {
          const sensorErrorTypes = [];
          allErrorTypes.forEach(({ dir, types }) => {
            const errorTypes = this.composeSensorErrorTypesHeatMapData(deviceId, types);
            sensorErrorTypes.push({ dir, errorTypes });
          });
          this.sensorErrorTypes = sensorErrorTypes;
        }
      } catch (error) {
        console.log(error);
      }
      this.sensorLoading = false;
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

    composeSensorErrorTypesHeatMapData(deviceId, errorTypes) {
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
          matrix[row * colCount + col] = errorTypes[col * rowCount + row];
        }
      }

      // Create a series
      let series = [];
      for (let x = 0; x < colCount; x++) {
        for (let y = 0; y < rowCount; y++) {
          series.push([x, y, matrix[y * colCount + x]]);
        }
      }

      const dataClasses = [];
      for (let i = 0; i < 8; i++) {
        dataClasses.push({
          from: i,
          to: i,
          color: Constants.DISTINCT_COLORS[i],
          name: `${i}: ${Constants.QUALITY_ERROR_NAMES[i]}`
        });
      }

      const colorAxis = {
        dataClasses
      };

      const spaces = Array(25)
        .fill('&nbsp')
        .join('');

      let result = {};
      result.title = `Sensor (${deviceId}) Error Types ${spaces}`;
      result.xAxis = 'Time of Day (hour)';
      result.yAxis = 'Time of Hour (min)';
      result.xcategories = xcategories;
      result.ycategories = ycategories;
      result.data = series;
      result.colorAxis = colorAxis;

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
    },

    prepareSensorErrorCounts(data) {
      const TYPE_AND_FIELD = [
        { text: 'Total Counts', value: 'total' },
        { text: 'Missing data', value: 'e1' },
        { text: 'Flow status', value: 'e2' },
        { text: 'Exceeding Threshold', value: 'e3' },
        { text: 'VOS Constant', value: 'e4' },
        { text: 'VOS Inconsistency', value: 'e5' },
        { text: 'Volume Aggregation', value: 'e6' },
        { text: 'Timezone Issue', value: 'e7' }
      ];

      this.headers = [{ text: 'Sensor Id', value: 'id' }];
      for (let i = 0; i < 8; i++) {
        this.headers.push(TYPE_AND_FIELD[i]);
      }

      this.allItems = data.map(d => {
        const counts = d.counts;
        const item = { id: d.id, flags: d.flags };
        for (let i = 0; i < counts.length; i++) {
          const value = TYPE_AND_FIELD[i].value;
          item[value] = counts[i];
        }
        return item;
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.update {
  font-size: 13px !important;
  position: absolute;
  right: 80px;
  margin-top: 12px;
}
</style>

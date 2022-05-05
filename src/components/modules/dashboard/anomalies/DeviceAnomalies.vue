<template>
  <div>
    <v-card class="device-anomalies">
      <v-data-table
        :height="height"
        fixed-header
        :headers="headers"
        :items="items"
        hide-default-footer
        :items-per-page="itemsPerPage"
        :item-class="itemRowBackground"
        @click:row="handleRowClick"
        class="elevation-1"
      >
        <template v-slot:[`item.id`]="{ item }">
          <v-chip color="pink" outlined small style="width:62px;">{{ item.id }}</v-chip>
        </template>
        <template v-slot:[`footer`]>
          <v-btn :disabled="maxItems == 1" block @click="expandTable">
            <v-icon>{{ itemsPerPage == 1 ? 'mdi-arrow-expand-down' : 'mdi-arrow-expand-up' }}</v-icon>
          </v-btn>
        </template>
      </v-data-table>
      <v-row class="mt-3 ml-1 mr-7" v-if="selectedDetector">
        <v-col cols="6" class="pa-1">
          <InfoCard :icon="'mdi-note-outline'" :name="'Total Issues'" :value="selectedDetector.counts[0]" />
        </v-col>
        <v-col cols="6" class="pa-1">
          <InfoCard :icon="'mdi-note-outline'" :name="getCount('name')" :value="getCount('val')" />
        </v-col>
      </v-row>

      <div class="ml-8 mr-2 mb-4 sensor-chart" v-if="sensorErrorTypes && !reload">
        <v-tabs color="deep-purple accent-4" v-model="activeTab" show-arrows>
          <v-tab v-for="({ dir }, i) in sensorErrorTypes" :key="i">
            <v-chip color="orange" small>{{ dir }}</v-chip>
          </v-tab>
          <v-tab-item v-for="({ errorTypes }, i) in sensorErrorTypes" :key="i">
            <v-container fluid>
              <SensorHeatmapChart :data="errorTypes" :height="350" />
            </v-container>
          </v-tab-item>
        </v-tabs>
      </div>
    </v-card>
  </div>
</template>

<script>
import Api from '@/utils/api/status';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/status';
import { mapState } from 'vuex';
import SensorHeatmapChart from '@/components/modules/status/SensorHeatmapChart';
import InfoCard from '@/components/modules/dashboard/InfoCard';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: {
    SensorHeatmapChart,
    InfoCard
  },

  data: () => ({
    height: null,
    itemsPerPage: 1,

    reload: false,
    loading: false,
    sensorLoading: false,
    updatedTime: new Date(),

    totalErrorCounts: null,
    errorCountsByType: null,
    sensorErrorCounts: null,
    errorCountsByHour: null,
    sensorErrorTypes: null,

    allItems: [],
    headers: [],

    activeTab: null,

    region_items: [
      { title: 'All Detectors', value: -1 },
      { title: 'Urban Freeway Detectors', value: 1 },
      { title: 'Urban System Detectors', value: 2 },
      { title: 'CAV Area Freeway Detectors', value: 3 },
      { title: 'CAV Area System Detectors', value: 4 },
      { title: ' Rural Freeway Detectors', value: 5 },
      { title: 'Rural System Detectors', value: 6 },
      { title: 'Outside study area', value: 7 }
    ],

    selectedRegionId: -1
  }),

  computed: {
    items() {
      if (this.selectedRegionId < 0) {
        return this.allItems;
      } else {
        return this.allItems.filter(item => item.flags === this.selectedRegionId);
      }
    },
    selectedDetector: {
      get() {
        return this.$store.state.dashboard.selectedDetector;
      },
      set(val) {
        this.$store.state.dashboard.selectedDetector = val;
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },
    ...mapState('dashboard', ['flowAnomData'])
  },

  filters: {
    date: function(date) {
      return 'Updated time: ' + Utils.formatDateAndTime(date);
    }
  },

  mounted() {
    if (this.flowAnomData && this.flowAnomData.sensorErrorCounts) {
      this.prepareSensorErrorCounts(this.flowAnomData.sensorErrorCounts);
      this.handleRowClick(this.flowAnomData.sensorErrorCounts[0]);
    }
  },

  watch: {
    allItems(data) {
      console.log(data[0]);
    },
    flowAnomData() {
      if (this.flowAnomData && this.flowAnomData.sensorErrorCounts) {
        this.selectedDetector = this.flowAnomData;
        this.prepareSensorErrorCounts(this.flowAnomData.sensorErrorCounts);
      }
    }
  },

  methods: {
    expandTable() {
      if (this.itemsPerPage == 1) {
        this.prepareSensorErrorCounts(this.flowAnomData.sensorErrorCounts);
        if (this.maxItems > 12) {
          this.height = 'calc(95vh - 48px)';
        }
        this.itemsPerPage = this.maxItems;
      } else {
        this.allItems = this.items.filter(x => x.id == this.selectedDetector.id);
        this.height = null;
        this.itemsPerPage = 1;
      }
    },

    getCount(attr) {
      if (this.selectedDetector) {
        let counts = this.selectedDetector.counts;
        for (let i = 0; i < counts.length; i++) {
          if (counts[i] != 0 && attr == 'name' && i != 0) {
            return this.getError(i);
          } else if (counts[i] != 0 && attr == 'val') {
            return counts[i];
          }
        }
      }
      return 'N/A';
    },
    getError(idx) {
      if (idx == 0) return 'Total Counts';
      else if (idx == 1) return 'Missing data';
      else if (idx == 2) return 'Flow status';
      else if (idx == 3) return 'Exceeding Threshold';
      else if (idx == 4) return 'VOS Constant';
      else if (idx == 5) return 'VOS Inconsistency';
      else if (idx == 6) return 'Volume Aggregation';
      else if (idx == 6) return 'Timezone Issue';
      else return 'N/A';
    },
    handleRowClick(value) {
      this.selectedDetector = value;
      let currDate = Utils.getStartOfDay(new Date());
      this.fetchSensorStatus(value.id, currDate);
      this.scrollToElement('sensor-chart');
    },

    itemRowBackground(item) {
      return item.id == this.selectedDetector.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    scrollToElement(className) {
      const el = this.$el.getElementsByClassName(className)[0];
      if (el) {
        // Use el.scrollIntoView() to instantly scroll to the element
        el.scrollIntoView({ behavior: 'smooth' });
      }
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

      let startTime = Utils.getStartOfDay(new Date()).getTime();

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

      let startTime = Utils.getStartOfDay(new Date()).getTime();

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
      const TYPE_AND_FIELD = [{ text: 'Total Counts', value: 'total' }];

      this.headers = [{ text: 'Sensor Id', value: 'id' }];
      for (let i = 0; i < TYPE_AND_FIELD.length; i++) {
        this.headers.push(TYPE_AND_FIELD[i]);
      }

      this.allItems = data.map(d => {
        const counts = d.counts;
        const item = { id: d.id, flags: d.flags, counts: d.counts, score: d.score };
        for (let i = 0; i < TYPE_AND_FIELD.length; i++) {
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

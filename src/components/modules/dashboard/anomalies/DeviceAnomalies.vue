<template>
  <div>
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items.slice(0, maxItems)"
      :items-per-page="showTable && maxItems > listLimit ? maxItems : listLimit"
      disable-sort
      :hide-default-header="!showTable"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1 mx-2"
    >
      <template v-slot:[`item.id`]="{ item }">
        <v-chip color="pink" outlined small style="width:62px;">{{ item.id }}</v-chip>
      </template>
    </v-data-table>
    <v-row class="mt-3 mx-1" v-if="selectedDetector">
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-alert-circle-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Total Issues'"
          :value="selectedDetector.counts[0]"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-note-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Major Error Type'"
          :value="getCount('name', selectedDetector)"
          :valueFontSize="singleCol ? undefined : 36"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-note-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="getCount('name', selectedDetector)"
          :value="getCount('val', selectedDetector)"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-for="err in sensorErrorTypes" :key="err.dir">
        <InfoCard
          :icon="'mdi-alert-circle-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="`Total Issues (${err.dir})`"
          :value="getDirErrorCount(err)"
        />
      </v-col>
    </v-row>

    <!-- <div class="ml-8 mr-2 mb-4 sensor-chart" v-if="sensorErrorTypes && !reload">
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
      </div> -->
  </div>
</template>

<script>
import Api from '@/utils/api/status';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/status';
import TrafficConstants from '@/utils/constants/traffic';
import { mapGetters, mapState } from 'vuex';
import InfoCard from '@/components/modules/dashboard/InfoCard';

export default {
  props: {
    data: Object,
    maxItems: Number,
    infoColumnCount: Number
  },
  components: {
    InfoCard
  },

  data: () => ({
    itemsPerPage: 0,

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

    region_items: TrafficConstants.TRAFFIC_DEVICE_CATEGORIES,
    selectedRegionId: -1
  }),

  computed: {
    height() {
      if (this.showTable && this.maxItems > 12) {
        return 'calc(80vh - 48px)';
      } else {
        return null;
      }
    },
    singleCol() {
      return this.infoColumnCount == 1;
    },
    cardHeight() {
      return this.singleCol ? '11vh' : undefined;
    },
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
    showTable: {
      get() {
        return this.$store.state.dashboard.showTable;
      },
      set(show) {
        this.$store.commit('dashboard/SHOW_TABLE', show);
      }
    },
    listLimit() {
      if (this.getSetting) {
        return this.getSetting('dashboard', 'limitListings');
      } else {
        return 0;
      }
    },
    ...mapGetters(['getSetting']),
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
    getDirErrorCount(data) {
      return data.errorTypes.data.reduce((x, y) => x + y[2], 0);
    },

    getCount(attr, detector) {
      if (detector) {
        let counts = detector.counts;
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

      this.headers.push({ text: 'Major Error Type', value: 'majorError' });
      this.headers.push({ text: '', value: 'actions' });
      this.allItems = data.map(d => {
        const counts = d.counts;
        const item = {
          id: d.id,
          flags: d.flags,
          counts: d.counts,
          score: d.score,
          majorError: this.getCount('name', d)
        };
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

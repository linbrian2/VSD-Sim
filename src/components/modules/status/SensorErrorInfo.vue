<template>
  <v-container>
    <v-row id="info" v-if="marker">
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Basic Info</h3></v-subheader>
          <div class="mt-4 mr-3">
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <v-chip class="ml-2 mt-n1" outlined small v-on="on" @click.stop="showTrafficFlowViewer(marker.id)">
                  <span>{{ marker.id }} / {{ marker.uid }}</span>
                  <v-icon small class="ml-2">mdi-open-in-new</v-icon>
                </v-chip>
              </template>
              <span>Open Traffic flow</span>
            </v-tooltip>
          </div>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <div class="mx-4 mt-n3">
          <v-row>
            <v-col :cols="12" v-for="(item, j) in info" :key="j">
              <ListInfoCard :info="item" class="mt-0" />
            </v-col>
          </v-row>
        </div>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
            ><h3>Historical Data</h3></v-subheader
          >

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
        <StackBarChart :data="counts" :colors="colors" :interval="interval" :height="stackHeight" class="mx-4" />
      </v-col>
    </v-row>

    <v-row v-if="sensorErrorTypes.length > 0">
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Error Types</h3></v-subheader>
          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showLegend = !showLegend" class="mr-4 mt-2">
                <v-icon small>mdi-information-outline</v-icon>
              </v-btn>
            </template>
            <span>Legend</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>

      <v-col cols="12" v-show="showLegend">
        <div class="mx-4">
          <v-card tile>
            <v-chip-group active-class="primary--text" column class="ml-6">
              <v-chip
                small
                v-for="{ color, name, from } in dataClasses"
                :key="from"
                :color="color"
                class="gray--text short"
              >
                {{ from }}: {{ name }}
              </v-chip>
            </v-chip-group>
          </v-card>
        </div>
      </v-col>
    </v-row>

    <v-row v-for="({ errorTypes }, i) in sensorErrorTypes" :key="i">
      <v-col cols="12">
        <div class="mx-4">
          <v-card tile>
            <SensorHeatmapChart :data="errorTypes" :legend="false" :height="height" />
          </v-card>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import StatusApi from '@/utils/api/status';
import { mapState } from 'vuex';
import Utils from '@/utils/Utils';
import { RouterNames } from '@/utils/constants/router';
import Constants from '@/utils/constants/status';
import ListInfoCard from '@/components/modules/traffic/common/ListInfoCard';
import SensorHeatmapChart from '@/components/modules/status/SensorHeatmapChart';
import StackBarChart from '@/components/modules/vision/StackBarChart';

export default {
  props: {
    marker: Object
  },

  components: {
    ListInfoCard,
    SensorHeatmapChart,
    StackBarChart
  },

  data: () => ({
    loading: false,
    showLegend: false,
    height: 350,
    stackHeight: 400,
    info: [],
    sensorErrorTypes: [],
    dataClasses: [],
    interval: 1800000,
    counts: {},
    colors: Constants.DISTINCT_COLORS.slice(1)
  }),

  computed: {
    ...mapState(['currentDate'])
  },

  watch: {
    marker() {
      if (this.marker) {
        this.init(this.marker);
      }
    }
  },

  mounted() {
    this.dataClasses = this.createDataClasses();
    this.init(this.marker);
  },

  methods: {
    init(marker) {
      if (marker && marker.id) {
        this.fetchSensorStatus(marker, this.currentDate);
      }
    },

    showStackChart() {},

    showTrafficFlowViewer(id) {
      const r = this.$router.resolve({ name: RouterNames.TRAFFIC_FLOW_DATA, params: { id } });
      window.open(r.href, '_blank');
    },

    async fetchSensorStatus(device, date) {
      const deviceId = device.id;

      this.loading = true;
      try {
        // Now we await for both results, whose async processes have already been started
        const [statusRes, countsRes] = await Promise.all([
          StatusApi.fetchSensorErrors(deviceId, date.getTime()),
          StatusApi.fetchSensorsErrorPercentCounts(deviceId, date.getTime(), 14)
        ]);

        const allErrorTypes = this.parseResponseData(statusRes);
        if (allErrorTypes) {
          this.info = this.composeBasicInfo(device, allErrorTypes);

          const sensorErrorTypes = [];
          allErrorTypes.forEach(({ dir, types }) => {
            const errorTypes = this.composeSensorErrorTypesHeatMapData(deviceId, dir, types);
            sensorErrorTypes.push({ dir, errorTypes });
          });
          this.sensorErrorTypes = sensorErrorTypes;
        } else {
          this.sensorErrorTypes = [];
        }

        const countsData = this.parseResponseData(countsRes);
        if (countsData) {
          this.counts = this.formCountsData(countsData);
        }
      } catch (error) {
        console.log(error);
      }
      this.loading = false;
    },

    composeSensorErrorTypesHeatMapData(deviceId, dir, errorTypes) {
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
      let errorCount = 0;
      for (let row = 0; row < rowCount; row++) {
        for (let col = 0; col < colCount; col++) {
          matrix[row * colCount + col] = errorTypes[col * rowCount + row];
          if (errorTypes[col * rowCount + row] > 0) {
            errorCount++;
          }
        }
      }

      // Create a series
      let series = [];
      for (let x = 0; x < colCount; x++) {
        for (let y = 0; y < rowCount; y++) {
          series.push([x, y, matrix[y * colCount + x]]);
        }
      }

      const dataClasses = this.createDataClasses();
      const colorAxis = {
        dataClasses
      };

      let errorPercentage = (errorCount * 100) / (rowCount * colCount);
      if (errorPercentage < 1) {
        errorPercentage = errorPercentage.toFixed(1);
      } else {
        errorPercentage = Math.round(errorPercentage);
      }

      let result = {};
      result.title = `${deviceId} ${dir} Error Types (${errorPercentage}%)`;
      result.xAxis = 'Time of Day (hour)';
      result.yAxis = 'Time of Hour (min)';
      result.xcategories = xcategories;
      result.ycategories = ycategories;
      result.data = series;
      result.colorAxis = colorAxis;

      return result;
    },

    createDataClasses() {
      const dataClasses = [];
      for (let i = 0; i < 8; i++) {
        dataClasses.push({
          from: i,
          to: i,
          color: Constants.DISTINCT_COLORS[i],
          name: Constants.QUALITY_ERROR_NAMES[i]
        });
      }
      return dataClasses;
    },

    addSpace(n) {
      return n <= 0
        ? ''
        : Array(n)
            .fill(' ')
            .join('');
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
        // console.log(response);
        // console.log(response.data.message);
        // this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },

    getDominantErrorType(counts, startIdx) {
      let max = counts[startIdx];
      let maxIndex = startIdx;

      for (var i = startIdx + 1; i < counts.length; i++) {
        if (counts[i] > max) {
          maxIndex = i;
          max = counts[i];
        }
      }

      return maxIndex;
    },

    getError(idx) {
      return idx > 0 && idx < Constants.QUALITY_ERROR_NAMES.length ? Constants.QUALITY_ERROR_NAMES[idx] : '';
    },

    composeBasicInfo(device, allErrorTypes) {
      let errorCount = 0;
      let totalCount = 0;
      let typesCount = new Array(8).fill(0);

      let totalPoints = 288;
      if (Utils.isToday(this.currentDate)) {
        const current = new Date();
        totalPoints = Math.min(288, Utils.get5MinIndexOf288(current.getTime()) + 1);
      }

      allErrorTypes.forEach(({ types }) => {
        for (let i = 0; i < totalPoints; i++) {
          totalCount++;
          if (types[i] > 0) {
            errorCount++;
            typesCount[types[i]]++;
          }
        }
      });

      const dominantErrorType = this.getDominantErrorType(typesCount, 1);
      let errorPercentage = (errorCount * 100) / totalCount;
      if (errorPercentage < 1) {
        errorPercentage = errorPercentage.toFixed(1);
      } else {
        errorPercentage = Math.round(errorPercentage);
      }

      const p = {
        Location: `${device.uid} - ${device.title}`,
        'Total Errors': errorCount,
        'Error Percentage': errorPercentage + '%',
        'Major Error': this.getError(dominantErrorType)
      };

      // const d = {
      //   'Device Id': device.id,
      //   Permit: device.uid,
      //   Location: device.title
      // };

      const result = [];
      //result.push({ title: 'Device Info', items: Utils.obj2Arr(d) });
      result.push({ title: 'Error Statistics', items: Utils.obj2Arr(p) });

      return result;
    },

    formCountsData(input) {
      let xAxis = 'Day of month';
      let yAxis = 'Error Count Percentage';

      let data = [];
      Object.keys(input).forEach(name => {
        let counts = input[name];
        data.push({ name: Utils.capitalize(name), data: counts });
      });

      return { data, xAxis, yAxis };
    }
  }
};
</script>

<style lang="scss" scoped>
.short {
  min-width: 200px;
}
.time-display {
  margin: 18px 10px;
  padding: 5px 10px;
  font-size: 11pt;
}
.custom-tab-items {
  background-color: transparent !important;
}
</style>

<template>
  <div>
    <v-container>
      <v-card class="mb-4" v-if="isInfo">
        <v-card-title class="teal  darken-3 d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Trip Info</strong></h4>
          <v-btn small icon @click.stop="showInfo = !showInfo" class="mr-2" :loading="loading">
            <v-icon>{{ showInfo ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
          </v-btn>
        </v-card-title>
        <InfoList :height="300" :data="info" v-show="showInfo" />
      </v-card>

      <v-card class="mb-4" v-if="isCSPI">
        <v-card-title class="teal  darken-3 d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>CSPI Score List</strong></h4>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn small icon class="mr-2" @click="downloadAll()" v-on="on">
                <v-icon>mdi-download</v-icon>
              </v-btn>
            </template>
            <span>Download All CSPI Data</span>
          </v-tooltip>
        </v-card-title>
        <CSPIScoreList :items="items" class="mt-4" />
      </v-card>

      <!-- Charts -->
      <v-card class="mb-4" v-if="speedAvailable">
        <v-card-title class="teal  darken-3 d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Speed</strong></h4>
          <v-btn small icon @click.stop="showSpeedChart" class="mr-2" :loading="loading">
            <v-icon small>mdi-arrow-expand</v-icon>
          </v-btn>
        </v-card-title>
        <BasicChart ref="speedChart" :data="speed" :height="height" />
      </v-card>

      <v-card class="mb-4" v-if="engineRPMAvailable">
        <v-card-title class="teal  darken-3 d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Engine RPM</strong></h4>
          <v-btn small icon @click.stop="showRPMChart" class="mr-2" :loading="loading">
            <v-icon small>mdi-arrow-expand</v-icon>
          </v-btn>
        </v-card-title>
        <BasicChart ref="rpmChart" :data="engineRPM" :height="height" />
      </v-card>

      <v-card class="mb-4" v-if="accelerationAvailable">
        <v-card-title class="teal  darken-3 d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Acceleration</strong></h4>
          <v-btn small icon @click.stop="showAccelChart" class="mr-2" :loading="loading">
            <v-icon small>mdi-arrow-expand</v-icon>
          </v-btn>
        </v-card-title>
        <BasicChart ref="accChart" :data="acceleration" :height="height" />
      </v-card>

      <v-card class="mb-4" v-if="fluidTempAvailable">
        <v-card-title class="teal  darken-3 d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Coolant Temperature</strong></h4>
          <v-btn small icon @click.stop="showTempChart" class="mr-2" :loading="loading">
            <v-icon small>mdi-arrow-expand</v-icon>
          </v-btn>
        </v-card-title>
        <BasicChart ref="tempChart" :data="fluidTemp" :height="height" />
      </v-card>
    </v-container>
    <ChartDialog ref="chartDialog" v-model="showChartDialog" />
  </div>
</template>

<script>
import Api from '@/utils/api/cav';
import Format from '@/utils/Format';
import Utils from '@/utils/Utils';
import InfoList from '@/components/modules/cav/InfoList';
import BasicChart from '@/components/modules/cav/BasicChart';
import ChartDialog from '@/components/modules/cav/ChartDialog';
import CSPIScoreList from '@/components/modules/cav/CSPIScoreList.vue';
import { mapState } from 'vuex';

export default {
  components: {
    InfoList,
    BasicChart,
    ChartDialog,
    CSPIScoreList
  },
  data: () => ({
    loading: false,
    showChartDialog: false,
    showInfo: true,
    height: 300,
    info: {},
    speed: {},
    acceleration: {},
    engineRPM: {},
    fluidTemp: {},
    speedAvailable: false,
    engineRPMAvailable: false,
    accelerationAvailable: false,
    fluidTempAvailable: false
  }),

  computed: {
    isInfo() {
      return Object.keys(this.info).length > 0;
    },
    isCSPI() {
      return this.isInfo && this.currentTrip && this.currentTrip.cspiData && this.currentTrip.cspiData.length > 0;
    },
    ...mapState('cav', ['currentEvent', 'currentTrip', 'currentState'])
  },

  watch: {
    currentTrip() {
      if (this.currentTrip.cspiData) {
        this.items = this.currentTrip.cspiData;
        this.showInfo = false;
      }
      this.refreshData();
    },

    currentState(value) {
      if (value && value.time) {
        this.updateChartLines(value.time);
      }
    },

    currentEvent: function(value) {
      if (value != null) {
        this.info = this.formatTripInfo(value.info);
        this.updateChartData(value.chart);
      }
    }
  },

  methods: {
    refreshData() {
      if (this.currentTrip) {
        this.fetchData(this.currentTrip.id);
      }
    },

    async fetchData(tripId) {
      this.loading = true;
      this.$store.commit('cav/SET_PROGRESS_LOADING', true);
      this.$store.commit('cav/SHOW_PANEL', true);

      try {
        // Now we await for all results, whose async processes have already been started
        const [tripDataRes, tripInfoRes] = await Promise.all([Api.fetchTripData(tripId), Api.fetchTripInfo(tripId)]);

        if (tripDataRes.data.status === 'OK') {
          const data = tripDataRes.data.data;
          this.speed = { data: data.speed, yAxis: 'Speed (mph)' };
          this.acceleration = { data: data.accel, yAxis: 'Accel (m/s\u00B2)' };

          if (data.rpm) {
            this.engineRPM = { data: data.rpm, yAxis: 'RPM (r/min)' };
          }

          if (data.fluid) {
            this.fluidTemp = { data: data.fluid, yAxis: 'Temperature (\u00B0C)' };
          }

          this.speedAvailable = true;
          this.accelerationAvailable = true;
          this.engineRPMAvailable = !!data.rpm;
          this.fluidTempAvailable = !!data.fluid;
        } else {
          this.speedAvailable = false;
          this.engineRPMAvailable = false;
          this.accelerationAvailable = false;
          this.fluidTempAvailable = false;
        }

        if (tripInfoRes.data.status === 'OK') {
          this.info = this.formatTripInfo(tripInfoRes.data.data);
        }
      } catch (error) {
        console.log(error);
      }
      this.loading = false;
      this.$store.commit('cav/SET_PROGRESS_LOADING', false);
    },

    formatTripInfo(trip) {
      return {
        tripId: trip.id,
        deviceId: trip.deviceId,
        status: trip.status > 0 ? 'Completed' : 'Ongoing',
        lastUpdated: Format.formatTimestamp(new Date(trip.lastUpdated)),
        startTime: Format.formatTimestamp(new Date(trip.startTime)),
        endTime: Format.formatTimestamp(new Date(trip.endTime)),
        startLocation: trip.startLoc ? Format.formatAddress(trip.startLoc) : undefined,
        endLocation: trip.endLoc ? Format.formatAddress(trip.endLoc) : undefined,
        distance: trip.distance ? Format.formatDistance(trip.distance) : undefined,
        duration: trip.duration ? Format.formatDuration(trip.duration, false) : undefined,
        topSpeed: trip.topSpeed ? trip.topSpeed + ' mph' : undefined,
        topRpm: trip.topRpm ? trip.topRpm + ' rpm' : undefined,
        topAccel: trip.topAccel ? trip.topAccel + ' m/s\u00B2' : undefined
      };
    },

    updateChartLines(time) {
      if (this.$refs.speedChart) {
        this.$refs.speedChart.updateChartVerticalLine(time);
      }
      if (this.$refs.accChart) {
        this.$refs.accChart.updateChartVerticalLine(time);
      }
      if (this.$refs.rpmChart) {
        this.$refs.rpmChart.updateChartVerticalLine(time);
      }
      if (this.$refs.tempChart) {
        this.$refs.tempChart.updateChartVerticalLine(time);
      }
    },

    updateChartData(chartData) {
      if (this.$refs.speedChart) {
        this.$refs.speedChart.updateChartData(chartData.ds);
      }
      if (this.$refs.accChart) {
        this.$refs.accChart.updateChartData(chartData.da);
      }
      if (this.$refs.rpmChart && chartData.de) {
        this.$refs.rpmChart.updateChartData(chartData.de);
      }
      if (this.$refs.tempChart && chartData.dt) {
        this.$refs.tempChart.updateChartData(chartData.dt);
      }
    },

    showSpeedChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Speed', this.speed);
    },

    showRPMChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Engine RPM', this.engineRPM);
    },

    showAccelChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Acceleration', this.acceleration);
    },

    showTempChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Coolant Temperature', this.fluidTemp);
    },

    downloadAll() {
      let newItem = this.items.map(item => {
        if (item.data) {
          return {
            id: item.id,
            group: item.group,
            score: item.score,
            speedScore: item.speedScore,
            speed: item.speed,
            grScore: item.grScore,
            gr: item.gr,
            spmScore: item.spmScore,
            stopsPerMile: item.stopsPerMile,
            stops: item.stops,
            start: item.start,
            end: item.end,
            phase: item.phase,
            redCount: item.redCount,
            devices: item.devices,
            distTotal: item.distTotal,
            data: item.data
          };
        } else {
          return item;
        }
      });
      Utils.downloadJSON(`Trip ${this.currentTrip.id} CSPI`, newItem);
    }
  }
};
</script>

<style lang="scss">
.v-data-table > .v-data-table__wrapper > table > tbody > tr > td {
  font-size: 12px;
}
.panel-default {
  text-align: center;
  background-color: #f5fafb;
  color: #555555;
}
</style>

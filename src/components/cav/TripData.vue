<template>
  <div>
    <v-container>
      <v-card class="mb-4" v-if="isInfo">
        <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Trip Info</strong></h4>
          <v-btn small icon @click.stop="refreshData" class="mr-2" :loading="loading">
            <v-icon>mdi-refresh</v-icon>
          </v-btn>
        </v-card-title>
        <InfoList :height="300" :data="info" />
      </v-card>

      <!-- Charts -->
      <v-card class="mb-4" v-if="speedAvailable">
        <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Speed</strong></h4>
          <v-btn small icon @click.stop="showSpeedChart" class="mr-2" :loading="loading">
            <v-icon small>mdi-arrow-expand</v-icon>
          </v-btn>
        </v-card-title>
        <BasicChart ref="speedChart" :data="speed" :height="height" />
      </v-card>

      <v-card class="mb-4" v-if="engineRPMAvailable">
        <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Engine RPM</strong></h4>
          <v-btn small icon @click.stop="showRPMChart" class="mr-2" :loading="loading">
            <v-icon small>mdi-arrow-expand</v-icon>
          </v-btn>
        </v-card-title>
        <BasicChart ref="rpmChart" :data="engineRPM" :height="height" />
      </v-card>

      <v-card class="mb-4" v-if="accelerationAvailable">
        <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
          <h4 class="ml-2 overline"><strong>Acceleration</strong></h4>
          <v-btn small icon @click.stop="showAccelChart" class="mr-2" :loading="loading">
            <v-icon small>mdi-arrow-expand</v-icon>
          </v-btn>
        </v-card-title>
        <BasicChart ref="accChart" :data="acceleration" :height="height" />
      </v-card>

      <v-card class="mb-4" v-if="fluidTempAvailable">
        <v-card-title class="d-flex justify-space-between mt-n1 pa-1">
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
import InfoList from '@/components/cav/InfoList';
import BasicChart from '@/components/cav/BasicChart';
import ChartDialog from '@/components/cav/ChartDialog';
import { mapState } from 'vuex';

export default {
  components: {
    InfoList,
    BasicChart,
    ChartDialog
  },
  data: () => ({
    loading: false,
    showChartDialog: false,
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
    ...mapState('cav', ['currentEvent', 'currentTrip', 'currentState'])
  },
  watch: {
    currentTrip() {
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
        lastUpdated: Format.fromatTimestamp(new Date(trip.lastUpdated)),
        startTime: Format.fromatTimestamp(new Date(trip.startTime)),
        endTime: Format.fromatTimestamp(new Date(trip.endTime)),
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

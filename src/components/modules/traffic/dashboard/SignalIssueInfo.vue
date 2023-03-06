<template>
  <v-container>
    <v-card tile color="teal">
      <v-card-title class="d-flex justify-space-between">
        <div>{{ signal.intersection }}</div>
        <div>
          <v-chip small outlined>
            <strong>{{ signal.permit }}</strong>
          </v-chip>
        </div>
      </v-card-title>

      <v-card-text class="mt-2">
        <v-row wrap no-gutters>
          <v-col class="mr-3"><PropertyCard :entry="getAoREntry('AoR', signal.AoR)" color=""/></v-col>
          <v-col class="mr-3"><PropertyCard :entry="getEntry('Approach Vol', signal.approachVolume)"/></v-col>
          <v-col class="mr-3"><PropertyCard :entry="getEntry('Simple Delay', signal.simpleDelay)"/></v-col>
        </v-row>
        <v-row class="mt-2 ml-2">
          <div class="overline">{{ formatDate(signal) }}</div>
        </v-row>
      </v-card-text>
    </v-card>

    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Arrival on Red</h3></v-subheader>

          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showAORChart" class="mr-4 mt-2" :loading="loadingAoR">
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <div class="mx-4">
          <v-card tile class="basic-chart" elevation="4">
            <BasicChart :data="aors" :height="height" :left="marginLeft" :legendy="legendY" />
          </v-card>
        </div>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
            ><h3>Approach Volume</h3></v-subheader
          >

          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn
                small
                icon
                v-on="on"
                @click.stop="showApproachVolumeChart"
                class="mr-4 mt-2"
                :loading="loadingVolume"
              >
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <div class="mx-4">
          <v-card tile class="basic-chart" elevation="4">
            <BasicChart :data="volumes" :height="height" :left="marginLeft" :legendy="legendY" />
          </v-card>
        </div>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
            ><h3>Simple Approach Delay</h3></v-subheader
          >

          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showSimpleDelayChart" class="mr-4 mt-2" :loading="loadingDelay">
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <div class="mx-4">
          <v-card tile class="basic-chart" elevation="4">
            <BasicChart :data="delays" :height="height" :left="marginLeft" :legendy="legendY" />
          </v-card>
        </div>
      </v-col>
    </v-row>

    <NearbyCameras :cameraIds="cameraIds" v-if="camerasAvaliable" />
  </v-container>
</template>

<script>
import Utils from '@/utils/Utils';
import Api from '@/utils/api/hr';
import PropertyCard from '@/components/modules/traffic/common/PropertyCard';
import NearbyCameras from '@/components/modules/traffic/common/NearbyCameras';
import BasicChart from '@/components/modules/traffic/common/BasicChart';

export default {
  props: {
    signal: Object
  },

  components: {
    PropertyCard,
    NearbyCameras,
    BasicChart
  },

  data: () => ({
    height: 300,
    legendY: 5,
    marginLeft: 80,
    loadingAoR: false,
    loadingVolume: false,
    loadingDelay: false,
    aors: {},
    volumes: {},
    delays: {},
    colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#FF0000', '#00FF00', '#FF9655', '#FFF263', '#6AF9C4']
  }),

  computed: {
    cameraIds() {
      return this.signal.cameras ? this.signal.cameras : [];
    },

    camerasAvaliable() {
      return !Utils.isEmpty(this.cameraIds);
    }
  },

  mounted() {
    this.init(this.signal);
  },

  watch: {
    signal() {
      if (this.signal) {
        this.init(this.signal);
      }
    }
  },

  methods: {
    init(signal) {
      if (signal) {
        let time = new Date().getTime();
        this.fetchPerformanceData(signal.id, time);
      }
    },

    getEntry(name, values) {
      return {
        name,
        items: [
          { name: 'NB', value: values[0] },
          { name: 'SB', value: values[1] }
        ]
      };
    },

    getAoREntry(name, values) {
      return {
        name,
        items: [
          { name: 'NB', value: values[0], color: this.getAORCOlor(values[0]) },
          { name: 'SB', value: values[1], color: this.getAORCOlor(values[1]) }
        ]
      };
    },

    getAORCOlor(value) {
      if (value < 30) {
        return '#66BB6A';
      } else if (value < 40) {
        return '#FFB74D';
      } else if (value < 50) {
        return '#EF5350';
      } else {
        return '#C62828';
      }
    },

    formatDate(signal) {
      const d1 = new Date(signal.startTime);
      const d2 = new Date(signal.endTime);

      const mm1 = Utils.formatDateWOYear(d1);
      const tt1 = Utils.formatTimeAsMinute(d1);
      const mm2 = Utils.formatDateWOYear(d2);
      const tt2 = Utils.formatTimeAsMinute(d2);

      let endTime = mm1 === mm2 ? tt2 : `${mm2} ${tt2}`;

      return `${mm1} ${tt1} - ${endTime}`;
    },

    showAORChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Arrival on Red', data: this.aors });
    },

    showApproachVolumeChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Approach Volume', data: this.volumes });
    },

    showSimpleDelayChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Simple Delay', data: this.delays });
    },

    fetchPerformanceData(id, time) {
      const phases = [2, 6];
      this.fetchAORs(id, phases, time);
      this.fetchVolumes(id, phases, time);
      this.fetchSimpleDelay(id, phases, time);
    },

    createSignals(deviceId, phases) {
      return phases.map(phaseId => [deviceId, phaseId]);
    },

    fetchAORs(id, phases, time) {
      let series = this.createSignals(id, phases);
      try {
        this.loadingAoR = true;
        Api.fetchAORSet(series, time).then(response => {
          if (response.data.status === 'OK') {
            let aors = response.data.data;

            const data = [];
            Object.keys(aors).forEach((key, idx) => {
              let aor = aors[key];
              let item = {
                data: aor.cycles,
                name: `P${aor.phaseId}`,
                color: this.colors[2 + idx]
              };
              data.push(item);
            });

            const title = '';
            const xAxis = 'Time of day';
            const yAxis = 'Percentage (%)';
            this.aors = { data, xAxis, yAxis, title };
          } else {
            this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loadingAoR = false;
        });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchVolumes(id, phases, time) {
      let series = this.createSignals(id, phases);
      try {
        this.loadingVolume = true;
        Api.fetchVolumeSet(series, time).then(response => {
          if (response.data.status === 'OK') {
            let volumes = response.data.data;

            const data = [];
            Object.keys(volumes).forEach((key, idx) => {
              let v = volumes[key];
              let item = {
                data: v.volume,
                name: `P${v.phaseId}`,
                color: this.colors[0 + idx]
              };
              data.push(item);
            });

            const title = '';
            const xAxis = 'Time of day';
            const yAxis = 'Volume (veh/5min)';
            this.volumes = { data, xAxis, yAxis, title };
          } else {
            this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loadingVolume = false;
        });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchSimpleDelay(id, phases, time) {
      try {
        this.loadingDelay = true;
        Api.fetchDelays(id, phases, time).then(response => {
          if (response.data.status === 'OK') {
            this.delays = [];

            let delays = response.data.data;

            const data = [];
            Object.keys(delays).forEach((phaseId, idx) => {
              let delay = delays[phaseId];
              let item = {
                data: delay,
                name: `${phaseId}`,
                color: this.colors[0 + idx]
              };
              data.push(item);
            });

            const title = '';
            const xAxis = 'Time of day';
            const yAxis = 'Cycle Delay (seconds)';
            this.delays = { data, xAxis, yAxis, title };
          } else {
            this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loadingDelay = false;
        });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    }
  }
};
</script>

<template>
  <v-row justify="center">
    <v-dialog v-model="show" max-width="600px">
      <v-card>
        <v-app-bar color="teal" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-chart-bell-curve</v-icon>
          <v-toolbar-title>{{ title }} </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container style="height:380px">
          <SpeedVolumeChart ref="speedVolumeChart" :data="data" :height="355" />
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Api from '@/utils/api/traffic';
import SpeedVolumeChart from '@/components/modules/traffic/chart/SpeedVolumeChart';
export default {
  props: {
    value: Boolean
  },

  components: {
    SpeedVolumeChart
  },

  data: () => ({
    title: '',
    deviceId: null,
    direction: '',
    time: null,
    data: {}
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
    init(name, deviceId, direction, time) {
      this.title = name;
      this.deviceId = deviceId;
      this.direction = direction;
      this.time = time;
      this.fetchTrafficFlowData(deviceId, direction, time, 3600);
    },

    hideDialog() {
      this.show = false;
    },

    async fetchTrafficFlowData(id, direction, time, range) {
      try {
        const res = await Api.fetchRangedTrafficFlowVolumeSpeed(id, direction, time, range);
        let data = this.parseResponseData(res);
        if (data) {
          this.data = this.formSpeedVolumeData(data, time);
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
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
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'warning' });
      }
      return result;
    },

    formSpeedVolumeData(resData, time) {
      let title = '';
      let xAxis = '';
      let yAxis = 'Speed (mph)';

      const speedList = resData.map(item => [item.time, item.y]);
      const volumeList = resData.map(item => [item.time, item.x]);
      const timeSlots = [time];

      let data = [];
      data.push({ name: 'Speed', color: '#50B432', data: speedList });
      data.push({ name: 'Volume', color: '#ED561B', data: volumeList });

      return { data, xAxis, yAxis, title, timeSlots };
    }
  }
};
</script>

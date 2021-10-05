<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="900px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-map-marker</v-icon>
          <v-toolbar-title>{{ title }} </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container style="height:540px">
          <TrafficFlowCharts
            ref="trafficFlowCharts"
            :deviceId="deviceId"
            :direction="direction"
            :interval="interval"
            :time="time"
            :showTitle="false"
          />
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import TrafficFlowCharts from '@/components/traffic/common/TrafficFlowCharts';
export default {
  props: {
    value: Boolean
  },

  components: {
    TrafficFlowCharts
  },

  data: () => ({
    title: '',
    deviceId: null,
    direction: '',
    interval: 300000,
    time: null
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
      if (this.$refs.trafficFlowCharts) {
        this.$refs.trafficFlowCharts.init(deviceId, direction, time);
      }
    },

    hideDialog() {
      this.show = false;
    }
  }
};
</script>

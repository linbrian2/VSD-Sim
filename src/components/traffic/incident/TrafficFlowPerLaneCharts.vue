<template>
  <div>
    <TrafficFlowCombinedCharts
      :data="flowData"
      :timeSlots="timeSlots"
      :name="name"
      :direction="direction"
      :height="height"
    />
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import TrafficFlowCombinedCharts from '@/components/traffic/common/TrafficFlowCombinedCharts';
export default {
  props: {
    height: {
      type: Number,
      default: 470
    },
    name: {
      type: String,
      default: ''
    },
    deviceId: Number,
    direction: String,
    interval: Number,
    time: Date,
    timeSlots: {
      type: Array,
      defualt: []
    }
  },

  components: {
    TrafficFlowCombinedCharts
  },

  data: () => ({
    flowData: {}
  }),

  mounted() {
    this.init(this.deviceId, this.direction, this.time);
  },

  methods: {
    init(deviceId, direction, time) {
      this.fetchTrafficFlowData(deviceId, null, direction, this.interval, time.getTime());
    },

    isEmpty() {
      return Object.keys(this.flowData).length === 0;
    },

    async fetchTrafficFlowData(id, uid, direction, interval, time) {
      this.loading = true;
      try {
        const response = await Api.fetchTrafficFlowPerLaneData(id, uid, direction, interval, time, null);
        const data = this.getResponseData(response, false);
        this.flowData = data ? data : {};
      } catch (error) {
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    getResponseData(response, showWarning) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        if (showWarning) {
          this.$store.dispatch('traffic/setSystemStatus', { text: response.data.message, color: 'warning', timeout: 1000 });
        }
      }
      return result;
    }
  }
};
</script>

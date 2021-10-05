<template>
  <div>
    <div v-if="isTabMode">
      <v-tabs color="teal accent-4" v-model="tab">
        <v-tab v-for="item in tabItems" :key="item.key" :href="`#${item.key}`">
          {{ item.value }}
        </v-tab>
      </v-tabs>
      <div>
        <v-tabs-items v-model="tab">
          <v-tab-item value="spd" v-if="isTabVisible('spd')">
            <BasicChart :data="speed" :height="height" :legendy="legendY" />
          </v-tab-item>
          <v-tab-item value="vol" v-if="isTabVisible('vol')">
            <BasicChart :data="volume" :height="height" :legendy="legendY" />
          </v-tab-item>
          <v-tab-item value="occ" v-if="isTabVisible('occ')">
            <BasicChart :data="occupancy" :height="height" :legendy="legendY" />
          </v-tab-item>
        </v-tabs-items>
      </div>
    </div>
    <div v-else>
      <v-card tile class="mb-4" v-if="availability.speed">
        <BasicChart :data="speed" :height="height" :legendy="legendY" />
        <div v-if="name" class="text-name">{{ name }}</div>
      </v-card>

      <v-card tile class="mb-4" v-if="availability.volume">
        <BasicChart :data="volume" :height="height" :legendy="legendY" />
        <div v-if="name" class="text-name">{{ name }}</div>
      </v-card>

      <v-card tile class="mb-4" v-if="availability.occupancy">
        <BasicChart :data="occupancy" :height="height" :legendy="legendY" />
        <div v-if="name" class="text-name">{{ name }}</div>
      </v-card>
    </div>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import BasicChart from '@/components/traffic/common/BasicChart';
export default {
  props: {
    mode: {
      type: String,
      default: 'tab'
    },
    height: {
      type: Number,
      default: 470
    },
    name: {
      type: String,
      default: ''
    },
    showTitle: {
      type: Boolean,
      default: true
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
    BasicChart
  },

  data: () => ({
    legendY: 40,
    tab: null,

    defaultTabItems: [
      { key: 'spd', value: 'Speed' },
      { key: 'vol', value: 'Volume' },
      { key: 'occ', value: 'Occupancy' }
    ],
    tabItems: [],

    speed: {},
    volume: {},
    occupancy: {},

    availability: {
      speed: false,
      volume: false,
      occupancy: false
    }
  }),

  computed: {
    isTabMode() {
      return this.mode === 'tab';
    }
  },

  mounted() {
    this.init(this.deviceId, this.direction, this.time, this.timeSlots);
  },

  methods: {
    init(deviceId, direction, time, timeSlots) {
      Object.assign(this.tabItems, this.defaultTabItems);
      this.fetchTrafficFlowData(deviceId, direction, this.interval, time.getTime(), timeSlots);
    },

    isTabVisible(name) {
      return this.tabItems.find(i => i.key === name) !== undefined;
    },

    removeTabByName(name) {
      const e = this.tabItems.findIndex(item => item.key === name);
      if (e >= 0) {
        this.tabItems.splice(e, 1);
      }
    },

    async fetchTrafficFlowData(id, direction, interval, time, timeSlots) {
      let speedAvailability = false;
      let volumeAvailability = false;
      let occupancyAvailability = false;

      try {
        const res = await Api.fetchTrafficFlowAndBaselineData(id, direction, interval, time);
        let data = this.parseResponseData(res);

        if (data) {
          if (data.speed) {
            this.speed = this.formSpeedData(data);
            if (timeSlots) {
              this.speed.timeSlots = timeSlots;
            }
            speedAvailability = true;
          }
          if (data.volume) {
            this.volume = this.formVolumeData(data);
            if (timeSlots) {
              this.volume.timeSlots = timeSlots;
            }
            volumeAvailability = true;
          }
          if (data.occupancy) {
            this.occupancy = this.formOccupancyData(data);
            if (timeSlots) {
              this.occupancy.timeSlots = timeSlots;
            }
            occupancyAvailability = true;
          }
        }

        this.availability.speed = speedAvailability;
        this.availability.volume = volumeAvailability;
        this.availability.occupancy = occupancyAvailability;
      } catch (error) {
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
      }

      if (!this.availability.speed) {
        this.removeTabByName('spd');
      }
      if (!this.availability.volume) {
        this.removeTabByName('vol');
      }
      if (!this.availability.occupancy) {
        this.removeTabByName('occ');
      }

      if (this.tabItems.length > 0) {
        this.tab = this.tabItems[0].key;
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
        this.$store.dispatch('traffic/setSystemStatus', { text: response.data.message, color: 'warning' });
      }
      return result;
    },

    formSpeedData(flowList) {
      let title = this.showTitle ? `Speed` : '';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';

      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: flowList.speed });
      data.push({ name: 'Baseline', color: '#50B432', data: flowList.baselineSpeed });

      return { data, xAxis, yAxis, title, min: 0, max: 100 };
    },

    formVolumeData(flowList) {
      let title = this.showTitle ? `Volume` : '';
      let xAxis = 'Time of day';
      let yAxis = 'Volume (vehicles/5min)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: flowList.volume });
      data.push({ name: 'Baseline', color: '#50B432', data: flowList.baselineVolume });

      return { data, xAxis, yAxis, title };
    },

    formOccupancyData(flowList) {
      let title = this.showTitle ? `Occupancy` : '';
      let xAxis = 'Time of day';
      let yAxis = 'Occupancy (%)';

      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: flowList.occupancy });
      data.push({ name: 'Baseline', color: '#50B432', data: flowList.baselineOccupancy });

      return { data, xAxis, yAxis, title };
    }
  }
};
</script>

<style lang="scss">
.text-name {
  color: rgb(215, 215, 215);
  font-size: 8px;
  margin-left: 10px;
  padding-bottom: 5px;
}
</style>

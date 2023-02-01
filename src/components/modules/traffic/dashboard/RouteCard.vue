<template>
  <v-card tile color="#444">
    <v-card-title class="d-flex justify-space-between">
      <div>{{ route.short }}</div>
      <div class="overline"><v-icon small class="mt-n1 mr-1">mdi-vector-line</v-icon>{{ route.distance }} mi</div>
      <div class="mr-2">
        <v-chip small :color="color">
          <strong class="black--text overline">{{ route.travelTime.level }}</strong>
        </v-chip>
      </div>
    </v-card-title>

    <v-card-text class="mt-2">
      <v-row wrap no-gutters>
        <v-col class="mr-3"><PropertyCard :cols="12" :entry="travelTime"/></v-col>
        <v-col class="mr-3"><PropertyCard :cols="12" :entry="travelSpeed"/></v-col>
      </v-row>
      <v-row class="mt-2 mx-2 d-flex justify-space-between">
        <div class="overline">{{ route.desc }}</div>
        <div class="overline">Updated at {{ getTimeStr(route.travelTime.calculationTimestamp.value) }}</div>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '@/utils/Utils';
import PropertyCard from '@/components/modules/traffic/common/PropertyCard';
export default {
  props: {
    route: Object
  },

  components: {
    PropertyCard
  },

  computed: {
    color() {
      const level = this.route ? this.route.travelTime.level : 0;
      return Utils.getStrokeColor(level);
    },
    travelTime() {
      if (!this.route) {
        return {};
      }

      const actual = Utils.durationToTimeStr(this.route.travelTime.data.meanTravelTimeSecs);
      const baseline = this.route.travelTime.baseline
        ? Utils.durationToTimeStr(this.route.travelTime.baseline.meanTravelTime)
        : Utils.durationToTimeStr(this.route.travelTime.data.freeFlowRunTimeSecs);
      return {
        name: 'Travel Time',
        items: [
          { name: 'Actual', value: actual, color: '#B71C1C' },
          { name: 'Average', value: baseline }
        ]
      };
    },
    travelSpeed() {
      if (!this.route) {
        return {};
      }

      const actual = this.route.travelTime.data.speedMph.toFixed(1);
      const baseline = this.route.travelTime.baseline ? this.route.travelTime.baseline.avgLinkSpeed.toFixed(1) : 'N/A';

      return {
        name: 'Travel Speed (mph)',
        items: [
          { name: 'Actual', value: actual, color: '#B71C1C' },
          { name: 'Average', value: baseline }
        ]
      };
    }
  },

  methods: {
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)}`;
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
    formatDate(signal) {
      const d1 = new Date(signal.startTime);
      const d2 = new Date(signal.endTime);

      const mm1 = Utils.formatDateWOYear(d1);
      const tt1 = Utils.formatTimeAsMinute(d1);
      const mm2 = Utils.formatDateWOYear(d2);
      const tt2 = Utils.formatTimeAsMinute(d2);

      let endTime = mm1 === mm2 ? tt2 : `${mm2} ${tt2}`;

      return `${mm1} ${tt1} - ${endTime}`;
    }
  }
};
</script>

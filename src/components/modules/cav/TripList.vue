<template>
  <v-data-table
    disable-sort
    :headers="headers"
    height="400"
    fixed-header
    :items="items"
    :items-per-page="itemsPerPage"
    hide-default-footer
    :item-class="itemRowBackground"
    @click:row="handleRowClick"
  >
    <template v-slot:[`item.startTime`]="{ item }">
      <div>{{ formatTimestamp(item.startTime) }}</div>
    </template>
    <template v-slot:[`item.lastUpdated`]="{ item }">
      <div>{{ formatTimestamp(item.lastUpdated) }}</div>
    </template>
    <template v-slot:[`item.duration`]="{ item }">
      <div>{{ formatDuration(item.duration) }}</div>
    </template>
    <template v-slot:[`item.distance`]="{ item }">
      <div>{{ formatDistance(item.distance) }}</div>
    </template>
    <template v-slot:[`item.status`]="{ item }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <span v-on="on">
            <v-icon v-if="item.status === 0" color="green">mdi-progress-check</v-icon>
            <v-icon v-else color="grey draken-3">mdi-check-circle-outline</v-icon>
          </span>
        </template>
        <span>{{ item.status === 0 ? 'ONGOING' : 'COMPLETED' }}</span>
      </v-tooltip>
    </template>
    <template v-slot:[`item.segment`]="{ item }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <span v-on="on">
            <div v-if="item.cspiData && item.cspiData.length > 0">
              <v-chip small :color="getColor(item.cspiData[item.cspiData.length - 1].score)">
                <h4 style="color: #3b3b3b;">{{ item.cspiData[item.cspiData.length - 1].score }}</h4>
              </v-chip>
            </div>
            <div v-else>-</div>
          </span>
        </template>
        <span>{{ item.segment ? 'Has CSPI' : 'No CSPI' }}</span>
      </v-tooltip>
    </template>
  </v-data-table>
</template>

<script>
import * as d3 from 'd3';
import Format from '@/utils/Format';
export default {
  props: {
    headers: Array,
    items: Array
  },

  data: () => ({
    itemsPerPage: 100
  }),

  methods: {
    getColor(score) {
      return d3.interpolateLab('#ff1900', '#33ff00')((score - 33) / 76);
      // if (score < 50) {
      //   return '#ff1900';
      // } else if (score < 60) {
      //   return '#ff9900';
      // } else if (score < 70) {
      //   return '#fff700';
      // } else if (score < 80) {
      //   return '#c8ff00';
      // } else {
      //   return '#33ff00';
      // }
    },

    formatTimestamp(timestamp) {
      return Format.formatTimestamp(timestamp);
    },

    formatDuration(seconds) {
      return Format.formatDuration(seconds);
    },

    formatDistance(meters) {
      return Format.formatDistance(meters);
    },

    itemRowBackground(item) {
      if (item.status == 0) {
        return 'highlighted';
      }
      return '';
    },

    prepareData(data) {
      this.headers = [
        { text: 'TripId', value: 'id' },
        { text: 'Start Time', value: 'startTime' },
        { text: 'Duration', value: 'duration' },
        { text: 'Status', value: 'status' },
        { text: 'CSPI', value: 'segment' }
      ];
      this.items = data;
    },

    handleRowClick(value) {
      const trip = Object.assign({}, value);
      this.$store.commit('cav/SET_CURRENT_TRIP', trip);
      this.$emit('click', value);
    }
  }
};
</script>

<style lang="scss">
.highlighted {
  background-color: rgb(215, 215, 44);
}
</style>

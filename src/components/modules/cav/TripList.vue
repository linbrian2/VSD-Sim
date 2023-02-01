<template>
  <v-data-table
    disable-sort
    :headers="headersFilt"
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
    <template v-slot:[`item.segment`]="{ item }" v-if="showCSPI">
      <div v-if="item.cspiData && item.cspiData.length > 0">
        <v-chip small :color="getColor(item.cspiData[item.cspiData.length - 1].score)">
          <h4 style="color: #3b3b3b;">{{ item.cspiData[item.cspiData.length - 1].score }}</h4>
        </v-chip>
      </div>
      <div v-else>-</div>
    </template>
  </v-data-table>
</template>

<script>
import Colors from '@/utils/Colors.js';
import Format from '@/utils/Format';
export default {
  props: {
    headers: Array,
    items: Array,
    showCSPI: {
      type: Boolean,
      default: false
    }
  },

  data: () => ({
    itemsPerPage: 100
  }),

  computed: {
    headersFilt() {
      if (!this.showCSPI) {
        return this.headers.filter(x => x.text != 'CSPI');
      } else {
        return this.headers;
      }
    }
  },

  methods: {
    getColor(score) {
      return Colors.getRedGreenColor((score - 33) / 76);
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

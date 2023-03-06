<template>
  <div class="congested-routes">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemPerPage"
      disable-sort
      :hide-default-header="!showTable"
      :item-class="itemRowBackground"
      hide-default-footer
      @click:row="handleRowClick"
      class="elevation-1 mx-2"
      v-show="showTable"
    >
      <template v-slot:[`item.level`]="{ item }">
        <v-chip small :color="getStrokeColor(item.level)">
          <strong class="black--text">{{ item.level }}</strong>
        </v-chip>
      </template>
      <template v-slot:[`item.lastUpdated`]="{ item }">
        {{ item.lastUpdated.split(' ')[1].slice(0, 5) }}
      </template>
    </v-data-table>

    <CongestedRouteInfo :segment="selectedCongestedSegment" v-if="selectedCongestedSegment" />
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
import CongestedRouteInfo from '@/components/modules/traffic/dashboard/CongestedRouteInfo';
import Constants from '@/utils/constants/traffic.js';
import Utils from '@/utils/Utils.js';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: {
    CongestedRouteInfo
  },
  data: () => ({
    loading: false,
    listLimit: 0,
    items: [],
    headers: []
  }),
  computed: {
    height() {
      return this.maxItems > 5 ? '35vh' : 'null';
    },

    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },

    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['congestedSegments', 'selectedCongestedSegment', 'showTable'])
  },

  watch: {
    congestedSegments() {
      if (this.congestedSegments) {
        this.prepareHighCongestionRoutes(this.congestedSegments);
        this.handleRowClick(this.items[0]);
      }
    }
  },

  mounted() {
    if (this.congestedSegments) {
      this.prepareHighCongestionRoutes(this.congestedSegments);
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)}`;
    },

    getStrokeColor(level) {
      return Utils.getStrokeColor(level);
    },

    prepareHighCongestionRoutes(data) {
      this.headers = [
        { text: 'Time', value: 'lastUpdated' },
        { text: 'Segment', value: 'desc' },
        { text: 'Travel Time', value: 'travelTime' },
        { text: 'Level', value: 'level' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        lastUpdated: d.travelTime.calculationTimestamp.text,
        travelTime: Utils.durationToTimeStr(d.travelTime.data.meanTravelTimeSecs),
        desc: d.desc,
        level: d.travelTime.level
      }));
    },

    itemRowBackground(item) {
      return item.id == this.selectedCongestedSegment.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id: item.id, type: Constants.LAYER_DEVICE_CONGESTED_SEGMENTS });
    }
  }
};
</script>

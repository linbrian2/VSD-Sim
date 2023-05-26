<template>
  <div class="waze">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemPerPage"
      disable-sort
      :hide-default-header="!showTable"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1 mx-2"
      v-show="showTable"
    >
    </v-data-table>

    <RestrictionDataInfo :restriction="selectedRestriction" v-if="selectedRestriction" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic.js';
import { mapGetters, mapState } from 'vuex';
import RestrictionDataInfo from '@/components/modules/traffic/dashboard/RestrictionDataInfo';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: { RestrictionDataInfo },
  data: () => ({
    reload: false,
    listLimit: 0,
    items: [],
    headers: []
  }),
  computed: {
    height() {
      return this.maxItems > 5 ? '35vh' : 'null';
    },
    currRestriction() {
      return this.getRestrictionInfo(this.selectedRestriction);
    },

    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },

    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['trafficRestrictions', 'selectedRestriction', 'showTable'])
  },
  watch: {
    trafficRestrictions() {
      if (this.trafficRestrictions) {
        this.prepareData(this.trafficRestrictions);
        this.handleRowClick(this.items[0]);
      }
    }
  },
  mounted() {
    if (this.trafficRestrictions) {
      this.prepareData(this.trafficRestrictions);
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    prepareData(data) {
      this.headers = [
        { text: 'Id', value: 'id' },
        { text: 'Time', value: 'time' },
        //{ text: 'Type', value: 'name' },
        { text: 'Location', value: 'loc' }
      ];

      this.items = data.map(res => ({
        id: res.id,
        restrictionId: res.restrictionId,
        name: res.impactType,
        time: this.formatTime(res.time),
        loc: res.location,
        desc: res.description
      }));
    },

    itemRowBackground(item) {
      return item.id == this.selectedRestriction.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id: item.id, type: Constants.LAYER_DEVICE_RESTRICTIONS });
    },

    formatTime(t) {
      const d = new Date(t);
      return Utils.formatTimeAsMinute(d);
    }
  }
};
</script>

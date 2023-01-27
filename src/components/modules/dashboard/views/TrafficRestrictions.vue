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
    >
    </v-data-table>

    <RestrictionInfo class="mx-2 mt-6" :restriction="currRestriction" v-if="currRestriction" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import { mapGetters, mapState } from 'vuex';
import RestrictionInfo from '@/components/modules/traffic/common/RestrictionInfo';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: { RestrictionInfo },
  data: () => ({
    reload: false,
    items: [],
    headers: []
  }),
  computed: {
    height() {
      if (this.showTable && this.maxItems > 12) {
        return 'calc(80vh - 48px)';
      } else {
        return null;
      }
    },
    currRestriction() {
      return this.getRestrictionInfo(this.selectedRestriction);
    },

    listLimit() {
      return this.getSetting ? this.getSetting('dashboard', 'limitListings') : 0;
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
        { text: 'ResId', value: 'restrictionId' },
        { text: 'Created', value: 'time' },
        { text: 'Type', value: 'name' },
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
      const data = this.trafficRestrictions.find(x => x.id == item.id);
      if (data) {
        console.log('handleRowClick', data);
        this.$store.commit('dashboard/SET_SELECTED_TRAFFIC_RESTRICTION', data);
      }
    },

    getRestrictionInfo(res) {
      if (!res) {
        return null;
      }

      return {
        id: res.restrictionId,
        name: res.impactType,
        time: res.time,
        dist: this.formatDistance(res.dist),
        loc: res.location,
        position: res.position,
        desc: res.description
      };
    },

    formatDistance(dist) {
      if (!dist) {
        return '';
      }
      const value = (dist / 1609).toFixed(2);
      return `${value} miles`;
    },

    formatTime(t) {
      const d = new Date(t);
      return Utils.formatDateTime(d);
    }
  }
};
</script>

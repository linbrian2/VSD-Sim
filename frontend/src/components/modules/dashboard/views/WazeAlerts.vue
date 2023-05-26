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
      <template v-slot:[`item.time`]="{ item }">
        {{ item.time.split(' ')[1].slice(0, 5) }}
      </template>
    </v-data-table>

    <WazeAlertInfo :waze="selectedWazeAlert" v-if="selectedWazeAlert" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic.js';
import { mapGetters, mapState } from 'vuex';
import WazeAlertInfo from '@/components/modules/traffic/dashboard/WazeAlertInfo';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: { WazeAlertInfo },
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
 
    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },

    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['wazeAlerts', 'selectedWazeAlert', 'showTable'])
  },
  watch: {
    wazeAlerts() {
      if (this.wazeAlerts) {
        this.prepareReportedWazeAlerts(this.wazeAlerts);
        this.handleRowClick(this.items[0]);
      }
    }
  },
  mounted() {
    if (this.wazeAlerts) {
      this.prepareReportedWazeAlerts(this.wazeAlerts);
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)}`;
    },

    prepareReportedWazeAlerts(data) {
      this.headers = [
        { text: 'Time', value: 'time' },
        { text: 'Street', value: 'street' },
        { text: 'Description', value: 'desc' },
        { text: 'Reliability', value: 'reliability' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        time: d.alertTime || d.time,
        desc: d.description || d.desc,
        street: d.address ? d.address.street : d.street,
        reliability: d.reliability
      }));
    },

    itemRowBackground(item) {
      return item.id == this.selectedWazeAlert.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id: item.id, type: Constants.LAYER_DEVICE_WAZE_ALERTS });
    }
  }
};
</script>

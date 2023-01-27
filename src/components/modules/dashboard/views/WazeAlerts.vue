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
      <template v-slot:[`item.time`]="{ item }">
        {{ item.time.split(' ')[1].slice(0, 5) }}
      </template>
    </v-data-table>

    <WazeInfo class="mx-2 mt-3" :waze="currWazeInfo" v-if="currWazeInfo" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import { mapGetters, mapState } from 'vuex';
import WazeInfo from '@/components/modules/traffic/common/WazeInfo';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: { WazeInfo },
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
    currWazeInfo() {
      return this.getWazeInfo(this.selectedWazeAlert);
    },

    listLimit() {
      return this.getSetting ? this.getSetting('dashboard', 'limitListings') : 0;
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
      const data = this.wazeAlerts.find(x => x.id == item.id);
      if (data) {
        this.$store.commit('dashboard/SET_SELECTED_WAZE_ALERT', data);
      }
    },

    getWazeInfo(waze) {
      if (!waze) {
        return null;
      }

      return {
        id: waze.id,
        time: Utils.formatAMPMTime(new Date(waze.alertTimeTS)),
        type: waze.alertType.name,
        desc: waze.description,
        position: waze.position,
        reportRating: waze.reportRating,
        confidence: waze.confidence,
        reliability: waze.reliability,
        thumbsup: waze.thumbsUp
      };
    }
  }
};
</script>

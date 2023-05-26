<template>
  <div class="signal-performance">
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
      <template v-slot:[`item.permit`]="{ item }">
        <v-chip color="green" outlined small style="width:62px;">{{ item.permit }}</v-chip>
      </template>

      <template v-slot:[`item.AoRN`]="{ item }">
        <FormatChip :value="item.AoR[0]" />
      </template>

      <template v-slot:[`item.AoRS`]="{ item }">
        <FormatChip :value="item.AoR[1]" />
      </template>
    </v-data-table>

    <SignalIssueInfo :signal="selectedSignalIssue" v-if="selectedSignalIssue" />
  </div>
</template>

<script>
import Constants from '@/utils/constants/traffic.js';
import FormatChip from '@/components/modules/hr/FormatChip';
import SignalIssueInfo from '@/components/modules/traffic/dashboard/SignalIssueInfo';
import { mapState } from 'vuex';

export default {
  props: {
    maxItems: Number,
    data: Object
  },
  components: {
    FormatChip,
    SignalIssueInfo
  },
  data: () => ({
    reload: false,
    listLimit: 0,
    headers: [
      { text: 'Permit', value: 'permit' },
      { text: 'Intersection', value: 'intersection' },
      { text: 'AoR (NB)', value: 'AoRN' },
      { text: 'AoR (SB)', value: 'AoRS' }
    ]
  }),
  computed: {
    height() {
      return this.maxItems > 5 ? '35vh' : 'null';
    },

    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },

    items() {
      return this.signalIssues;
    },

    ...mapState('dashboard', ['signalIssues', 'selectedSignalIssue', 'showTable'])
  },
  mounted() {
    this.triggerFirst();
  },
  methods: {
    itemRowBackground(item) {
      return item.id == this.selectedSignalIssue.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id: item.id, type: Constants.LAYER_DEVICE_SIGNALS });
    },

    triggerFirst() {
      if (!this.selectedSignalIssue && this.items && this.items.length > 0) {
        this.handleRowClick(this.items[0]);
      }
    }
  },
  watch: {
    signalIssues() {
      this.triggerFirst();
    }
  }
};
</script>

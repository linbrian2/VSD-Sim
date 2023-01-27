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

    <SignalInfo :signal="selectedSignalIssue" class="mt-3 mx-2" v-if="selectedSignalIssue" />

    <!-- <v-row class="mt-3 mx-1" v-if="currSignalIssue">
      <v-col :cols="12 / infoColumnCount" v-for="x in currSignalIssue" :key="x.name" class="pa-1">
        <InfoCard
          :icon="x.icon"
          :colDisplay="x.colDisplay"
          :flex="x.flex"
          :height="x.height"
          :name="x.name"
          :value="x.value"
        />
      </v-col>
    </v-row> -->
  </div>
</template>

<script>
import FormatChip from '@/components/modules/hr/FormatChip';
import SignalInfo from '@/components/modules/dashboard/views/SignalInfo';
import { mapState } from 'vuex';

export default {
  props: {
    maxItems: Number,
    data: Object
  },
  components: {
    FormatChip,
    SignalInfo
  },
  data: () => ({
    reload: false,
    headers: [
      { text: 'Permit', value: 'permit' },
      { text: 'Intersection', value: 'intersection' },
      { text: 'AoR (NB)', value: 'AoRN' },
      { text: 'AoR (SB)', value: 'AoRS' }
    ]
  }),
  computed: {
    height() {
      if (this.showTable && this.maxItems > 12) {
        return 'calc(80vh - 48px)';
      } else {
        return null;
      }
    },

    infoColumnCount() {
      return this.getSetting('dashboard', 'infoColumnCount');
    },

    listLimit() {
      return this.getSetting ? this.getSetting('dashboard', 'limitListings') : 0;
    },

    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },

    currSignalIssue() {
      return this.formatData(this.selectedSignalIssue);
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
      const data = this.signalIssues.find(x => x.id == item.id);
      if (data) {
        this.$store.commit('dashboard/SET_SELECTED_SIGNAL_ISSUE', data);
      }
    },

    triggerFirst() {
      if (!this.selectedSignalIssue && this.items && this.items.length > 0) {
        this.handleRowClick(this.items[0]);
      }
    },

    formatData(item) {
      if (!item) {
        return [];
      }
      const signalIssueItems = [
        { icon: 'mdi-note-outline', name: 'Permit' },
        { icon: 'mdi-map-marker-outline', name: 'Intersection' },
        { icon: 'mdi-note-outline', name: 'AoR Total' },
        { icon: 'mdi-arrow-up-bold-outline', name: 'Simple Delay (NB)' },
        { icon: 'mdi-arrow-down-bold-outline', name: 'Simple Delay (SB)' },
        { icon: 'mdi-arrow-up-bold-outline', name: 'Approach Volume (NB)' },
        { icon: 'mdi-arrow-down-bold-outline', name: 'Approach Volume (SB)' }
      ];

      for (let i = 0; i < signalIssueItems.length; i++) {
        signalIssueItems[i]['colDisplay'] = this.infoColumnCount == 1;
        signalIssueItems[i]['flex'] = this.infoColumnCount == 1;
        signalIssueItems[i]['height'] = this.infoColumnCount == 1 ? '11vh' : undefined;
      }

      signalIssueItems[0]['value'] = item.permit;
      signalIssueItems[1]['value'] = item.intersection;
      signalIssueItems[2]['value'] = `${item.score / 2} %`;
      signalIssueItems[3]['value'] = `${item.simpleDelay[0]} s`;
      signalIssueItems[4]['value'] = `${item.simpleDelay[1]} s`;
      signalIssueItems[5]['value'] = `${item.approachVolume[0]} veh./5m`;
      signalIssueItems[6]['value'] = `${item.approachVolume[1]} veh./5m`;

      return signalIssueItems;
    }
  },
  watch: {
    signalIssues() {
      this.triggerFirst();
    }
  }
};
</script>

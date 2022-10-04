<template>
  <div class="signal-performance">
    <SignalPerformanceIssuesTable
      class="mx-2"
      :itemsPerPage="itemsPerPage"
      :summary="summary"
      :preSelect="false"
      :hrSummary="hrSummary"
      :maxItems="maxItems"
      @click="handleRowClick"
      @prepareData="prepareSignalPerformanceIssues"
    />
    <v-row class="mt-3 mx-1" v-if="currSignalPerformanceIssue">
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-note-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Permit'"
          :value="currSignalPerformanceIssue.permit"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-map-marker-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Intersection'"
          :value="currSignalPerformanceIssue.intersection"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-note-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'AoR Total'"
          :value="`${currSignalPerformanceIssue.score / 2} %`"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-arrow-up-bold-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Simple Delay (NB)'"
          :value="`${currSignalPerformanceIssue.simpleDelay[0]} s`"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-arrow-down-bold-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Simple Delay (SB)'"
          :value="`${currSignalPerformanceIssue.simpleDelay[1]} s`"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-arrow-up-bold-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Approach Volume (NB)'"
          :value="`${currSignalPerformanceIssue.approachVolume[0]} veh/5m`"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-arrow-down-bold-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Approach Volume (SB)'"
          :value="`${currSignalPerformanceIssue.approachVolume[1]} veh/5m`"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import InfoCard from '@/components/modules/dashboard/InfoCard';
import SignalPerformanceIssuesTable from '@/components/modules/dashboard/issues/SignalPerformanceIssuesTable';

import HRApi from '@/utils/api/hr';
import { mapState } from 'vuex';

export default {
  props: {
    height: { type: Number, default: 225 },
    itemsPerPage: { type: Number, default: 3 },
    maxItems: Number,
    data: Object,
    infoColumnCount: Number
  },
  components: {
    InfoCard,
    SignalPerformanceIssuesTable
  },
  data() {
    return {
      selectedTab: 0,
      reload: false,
      summary: null,
      loading: false,
      select: 2,
      baseIndex: 0
    };
  },
  computed: {
    singleCol() {
      return this.infoColumnCount == 1;
    },
    cardHeight() {
      return this.singleCol ? '11vh' : undefined;
    },
    currSignalPerformanceIssue() {
      if (this.selectedSignalPerformanceIssue && this.hrSummary) {
        let result = this.hrSummary.filter(x => x.id == this.selectedSignalPerformanceIssue.id);
        return result.length > 0 ? result[0] : null;
      } else {
        return null;
      }
    },
    selectedSignalPerformanceIssue: {
      get() {
        return this.$store.state.dashboard.selectedSignalPerformanceIssue;
      },
      set(val) {
        this.$store.state.dashboard.selectedSignalPerformanceIssue = val;
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },
    ...mapState('dashboard', ['hrSummary', 'signalPerformanceIssues'])
  },
  mounted() {
    this.presetPhase();
    if (this.hrSummary) {
      this.prepareSignalPerformanceIssues(this.hrSummary);
    }
    if (!this.selectedSignalPerformanceIssue && this.items && this.items.length > 0) {
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    presetPhase() {
      if (this.select.length == 0 && this.phases.length > 0) {
        if (this.phases.indexOf(2) >= 0) {
          this.select.push(2);
        } else {
          this.select.push(this.phases[0]);
        }
        if (this.phases.indexOf(6) >= 0) {
          this.select.push(6);
        } else {
          if (this.phases.length > 1) {
            this.select.push(this.phases[1]);
          }
        }
      }
    },
    itemRowBackground(item) {
      return item.id == this.selectedId ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedSignalPerformanceIssue = item;
      this.$emit('click', item);
    },
    prepareSignalPerformanceIssues(data) {
      this.summary = data;
    },
    fetchPCDData(id, phases, time, title) {
      try {
        this.loading = true;
        HRApi.fetchPCDs(id, phases, time).then(response => {
          if (response.data.status === 'OK') {
            let pcds = response.data.data;
            this.pcds = [];
            Object.keys(pcds).forEach(phaseId => {
              let pcd = pcds[phaseId];
              let item = {
                info: pcd,
                title: title,
                date: this.currentDate,
                phase: phaseId,
                height: pcds.length == 1 ? 560 : 450
              };
              this.pcds.push(item);
            });
            this.baseIndex += 10;
          } else {
            this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loading = false;
        });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    }
  },
  watch: {
    items() {
      if (!this.selectedSignalPerformanceIssue && this.items && this.items.length > 0) {
        this.handleRowClick(this.items[0]);
      }
    },
    hrSummary() {
      if (this.hrSummary) {
        this.prepareSignalPerformanceIssues(this.hrSummary);
      }
      if (!this.selectedSignalPerformanceIssue && this.items && this.items.length > 0) {
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

<style></style>

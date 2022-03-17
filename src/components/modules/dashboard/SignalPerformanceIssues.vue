<template>
  <div class="signal-performance mr-4">
    {{ selectedSignalPerformanceIssue }}
    <br /><br />
    {{ signalPerformanceIssues.map(x => x.name.slice(0, 4)) }}
    <SignalPerformanceIssuesTable
      :itemsPerPage="3"
      :height="height"
      :summary="summary"
      :preSelect="false"
      @click="handleRowClick"
    />
    <v-card class="py-3">
      <v-combobox
        flat
        dense
        small-chips
        hide-details
        multiple
        single-line
        label="Phases:"
        :items="phases"
        v-model="select"
      >
        <template v-slot:prepend>
          <v-icon class="mt-n1" color="white">mdi-road</v-icon>
        </template>
      </v-combobox>
    </v-card>
    <!-- <v-card class="my-2" min-height="36.9vh" v-if="pcd">
      <PcdChart :info="pcd.info" :title="pcd.title" :date="pcd.date" :phase="pcd.phase" :height="pcd.height" />
    </v-card> -->
    <v-card class="my-2" v-for="(pcd, index) in pcds" :key="baseIndex + index" min-height="36.9vh">
      <PcdChart :info="pcd.info" :title="pcd.title" :date="pcd.date" :phase="pcd.phase" :height="pcd.height" />
    </v-card>
    <!-- <v-card min-height="36.9vh" v-if="aor">
      <AorChart :data="aor.data" :title="aor.title" :height="420" />
    </v-card> -->
  </div>
</template>

<script>
import SignalPerformanceIssuesTable from '@/components/modules/dashboard/SignalPerformanceIssuesTable';
import PcdChart from '@/components/modules/dashboard/graphs/PcdChart';
// import AorChart from '@/components/modules/dashboard/graphs/AorChart';

// import PCD from '@/utils/samples/PCD.js';
// import AOR from '@/utils/samples/AOR.js';
import HRApi from '@/utils/api/hr';
import { mapState } from 'vuex';

export default {
  props: {
    height: { type: Number, default: 225 },
    data: Object
  },
  components: {
    SignalPerformanceIssuesTable,
    PcdChart
    // AorChart
  },
  data() {
    return {
      pcds: [],
      // pcd: null,
      // aor: null,
      reload: false,
      itemsPerPage: 3,
      summary: null,
      loading: false,
      select: [],
      baseIndex: 0
    };
  },
  computed: {
    phases() {
      if (this.selectedSignalPerformanceIssue) {
        let issueInfo = null;
        for (let i = 0; i < this.signalPerformanceIssues.length; i++) {
          if (this.signalPerformanceIssues[i].name.slice(0, 4) == this.selectedSignalPerformanceIssue.permit) {
            issueInfo = this.signalPerformanceIssues[i];
          }
        }
        if (issueInfo) {
          let signal = issueInfo || { phases: [] };
          return signal.phases;
        }
      }
      return { phases: [] };
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
    if (!this.selectedSignalPerformanceIssue) {
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
    fetchInfo(device) {
      console.log('Fetch Device Info: %o', device);
      let issueInfo = null;
      for (let i = 0; i < this.signalPerformanceIssues.length; i++) {
        if (this.signalPerformanceIssues[i].name.slice(0, 4) == device.permit) {
          issueInfo = this.signalPerformanceIssues[i];
        }
      }
      if (issueInfo) {
        const id = issueInfo.id;
        const time = new Date().getTime();
        const title = issueInfo.name;
        this.fetchPCDData(id, this.select, time, title);
      }
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
    selectedSignalPerformanceIssue(device) {
      this.fetchInfo(device);
    },
    hrSummary() {
      if (this.hrSummary) {
        this.prepareSignalPerformanceIssues(this.hrSummary);
      }
      if (!this.selectedSignalPerformanceIssue) {
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

<style></style>

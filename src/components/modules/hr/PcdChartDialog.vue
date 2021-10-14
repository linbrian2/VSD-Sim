<template>
  <div>
    <LoadingProgress :loading="loading" color="deep-purple accent-4" />
    <v-row justify="center">
      <v-dialog v-model="show" fullscreen hide-overlay transition="dialog-left-transition">
        <v-card>
          <v-toolbar dark dense flat fixed overflow extension-height="0">
            <v-btn icon @click="show = false">
              <v-icon dark>mdi-arrow-left</v-icon>
            </v-btn>
            <v-toolbar-title>PCD Chart</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-toolbar-title class="phase-label">Phase: </v-toolbar-title>
            <div style="width:300px">
              <v-combobox
                flat
                dense
                small-chips
                hide-details
                multiple
                single-line
                :items="phases"
                v-model="select"
              ></v-combobox>
            </div>
            <v-btn color="success" small outlined rounded class="ml-2" @click="showAll">Load</v-btn>
            <v-spacer></v-spacer>
            <v-toolbar-items>
              <v-btn icon dark @click="show = false"><v-icon>mdi-close</v-icon></v-btn>
            </v-toolbar-items>
            <!-- <toolbar-progress-bar :loading="loading" color="white" slot="extension" /> -->
          </v-toolbar>

          <div class="my-6 mx-auto" style="width:85%" v-for="(pcd, index) in pcds" :key="baseIndex + index">
            <PcdChart
              :info="pcd.info"
              :title="pcd.title"
              :date="pcd.date"
              :phase="pcd.phase"
              :height="pcds.length == 1 ? 520 : 400"
            />
          </div>
        </v-card>
      </v-dialog>
    </v-row>
  </div>
</template>

<script>
import Api from '@/utils/api/hr';
import PcdChart from '@/components/modules/hr/PcdChart';
import LoadingProgress from '@/components/modules/hr/LoadingProgress';

export default {
  components: {
    LoadingProgress,
    // ToolbarProgressBar,
    PcdChart
  },
  props: {
    value: Boolean
  },
  data: () => ({
    loading: false,
    pcds: [],
    select: [],
    baseIndex: 0
  }),
  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },
    date() {
      return this.$store.state.currentDate;
    },
    signal() {
      return this.$store.state.hr.activeMarker || { id: 0, name: '' };
    },
    phases() {
      let signal = this.$store.state.hr.activeMarker || { phases: [] };
      return signal.phases;
    }
  },
  methods: {
    reset(id, time, phase) {
      if (phase <= 0 && this.phases.length > 0) {
        phase = this.phases[0];
      }
      this.pcds = [];
      this.select = [phase];
      this.baseIndex = 0;
      this.showAll();
    },
    showAll() {
      if (this.select.length > 0) {
        const id = this.signal.id;
        const time = this.date.getTime();
        const title = this.signal.name;
        this.getAllPCDData(id, this.select, time, title);
      }
    },
    getAllPCDData(id, phases, time, title) {
      try {
        this.loading = true;
        Api.fetchPCDs(id, phases, time).then(response => {
          if (response.data.status === 'OK') {
            let pcds = response.data.data;
            this.pcds = [];
            Object.keys(pcds).forEach((phaseId, index) => {
              let pcd = pcds[phaseId];
              let item = {
                info: pcd,
                title: index == 0 ? title : '',
                date: this.date,
                phase: phaseId
              };
              this.pcds.push(item);
            });
            this.baseIndex += 10;
          } else {
            console.log(response.data.message);
          }
          this.loading = false;
        });
      } catch (error) {
        console.log(error);
      }
    }
  }
};
</script>

<style scoped>
.v-toolbar__extension {
  padding: 0px !important;
}
</style>

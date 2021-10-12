<template>
  <div>
    <TitleBar title="PCD Chart" :loading="loading" :refresh="refreshData">
      <v-row style="height:30px" align="center" class="mt-n2">
        <v-col cols="9">
          <div style="width:280px">
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
          </div>
        </v-col>
        <v-col cols="3">
          <v-btn small outlined class="mt-1" color="white" @click.stop="showCorridorPCD">
            <v-icon left color="light-green accent-3">mdi-map-legend</v-icon>
            <span> Corridor PCD Charts </span>
          </v-btn>
        </v-col>
      </v-row>
    </TitleBar>
    <v-container>
      <v-card class="mb-8" v-for="(pcd, index) in pcds" :key="baseIndex + index">
        <PcdChart :info="pcd.info" :title="pcd.title" :date="pcd.date" :phase="pcd.phase" :height="pcd.height" />
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/hr';
import { mapState } from 'vuex';
import PcdChart from '@/components/hr/PcdChart';
import TitleBar from '@/components/hr/TitleBar';

export default {
  components: {
    TitleBar,
    PcdChart
  },
  data: () => ({
    loading: false,
    pcds: [],
    select: [],
    baseIndex: 0
  }),
  computed: {
    phases() {
      let signal = this.activeMarker || { phases: [] };
      return signal.phases;
    },
    ...mapState('hr', ['activeMarker', 'currentDate'])
  },
  watch: {
    currentDate() {
      this.refreshData();
    }
  },
  async mounted() {
    this.$bus.$on('GET_PCD', ({ marker, time }) => {
      this.select = [];
      this.presetPhase();
      this.fetchPCDData(marker.id, this.select, time, marker.name);
    });
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.presetPhase();
      this.showPCD();
    },
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
    refreshData() {
      this.fetchData();
    },

    showCorridorPCD() {
      this.$bus.$emit('SHOW_CORRIDOR_PCD');
    },

    showPCD() {
      if (this.select.length > 0) {
        const id = this.activeMarker.id;
        const time = this.currentDate.getTime();
        const title = this.activeMarker.name;
        this.fetchPCDData(id, this.select, time, title);
      }
    },
    fetchPCDData(id, phases, time, title) {
      try {
        this.loading = true;
        Api.fetchPCDs(id, phases, time).then(response => {
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
            //console.log(response.data.message);
            this.$store.dispatch('setSystemStatus', { text: response.data.message, timeout: 0, color: 'error' });
          }
          this.loading = false;
        });
      } catch (error) {
        //console.log(error);
        this.$store.dispatch('setSystemStatus', { text: error, timeout: 0, color: 'error' });
      }
    }
  }
};
</script>

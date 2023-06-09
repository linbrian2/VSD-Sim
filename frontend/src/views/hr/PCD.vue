<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
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
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn dark icon small elevation="5" fab v-on="on" @click.stop="showCorridorPCD">
                <v-icon>mdi-format-columns</v-icon>
              </v-btn>
            </template>
            <span>Corridor PCD Charts</span>
          </v-tooltip>
        </v-col>
      </v-row>
    </TitleBar>
    <v-container>
      <v-card class="mb-8" v-for="(pcd, index) in pcds" :key="baseIndex + index">
        <PcdChart :info="pcd.info" :title="pcd.title" :date="pcd.date" :phase="pcd.phase" :height="pcd.height" />
      </v-card>
    </v-container>
  </div>
  <div class="mobile" v-else>
    <v-container>
      <div class="d-flex align-items justify-center align-center">
        <div class="d-flex justify-space-between" :class="$vuetify.breakpoint.xs ? 'mt-1' : ''">
          <div class="mb-3" style="width:210px;">
            <v-combobox
              filled
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
        </div>
      </div>
      <v-card class="mb-2 px-7">
        <v-row>
          <v-col cols="12" class="pb-1 grid-center">Level</v-col>
          <v-col v-for="i in pcdLegend" :key="i.id" cols="6" class="pt-0 px-1">
            <v-card :color="i.color" class="grid-center">
              <h5 :style="`color: black`">{{ i.name }}</h5>
            </v-card>
          </v-col>
        </v-row>
      </v-card>
      <v-card class="mb-8" v-for="(pcd, index) in pcds" :key="baseIndex + index">
        <PcdChart :info="pcd.info" :title="pcd.title" :date="pcd.date" :phase="pcd.phase" :height="300" />
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/hr';
import { mapState } from 'vuex';
import { RouterPaths } from '@/utils/constants/router';
import PcdChart from '@/components/modules/hr/PcdChart';
import TitleBar from '@/components/modules/hr/TitleBar';

export default {
  components: {
    TitleBar,
    PcdChart
  },
  data: () => ({
    loading: false,
    pcds: [],
    select: [],
    baseIndex: 0,
    pcdLegend: [
      { name: 'Detector Activation', color: '#fff' },
      { name: 'Change to green', color: '#008000' },
      { name: 'Change to yellow', color: '#FFFF00' },
      { name: 'Change to red', color: '#FF0000' }
    ]
  }),
  computed: {
    phases() {
      let signal = this.activeMarker || { phases: [] };
      return signal.phases;
    },
    ...mapState(['currentDate']),
    ...mapState('hr', ['activeMarker'])
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
      const path = RouterPaths.HR_MULTI_PCD;
      this.$router.push({ path }).catch(() => {});
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
            this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loading = false;
        });
      } catch (error) {
        //console.log(error);
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    }
  }
};
</script>

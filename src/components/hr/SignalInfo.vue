<template>
  <div>
    <div class="signal-name">
      <span>{{ activeSignal.name }}</span>
      <span class="ml-2"
        ><v-chip class="mt-n1 white--text" color="green" outlined x-small
          ><span class="white--text">{{ activeSignal.id }}</span></v-chip
        ></span
      >
      <v-btn icon @click.stop="close()" class="signal-btn float-right">
        <v-icon color="white">mdi-close</v-icon>
      </v-btn>
    </div>
    <v-img :src="imageSrc" aspect-ratio="3.0" />
    <div class="time-display">{{ currentDate | date }}</div>
    <v-container>
      <v-card class="mt-n1" v-show="ready">
        <v-tabs color="deep-purple accent-4" v-model="activeTab" right show-arrows>
          <v-tab v-for="(phase, i) in phases" :key="i">
            <v-chip color="orange" small>P{{ phase }}</v-chip>
          </v-tab>
          <v-tab-item v-for="(plan, i) in plans" :key="i">
            <v-container fluid>
              <v-data-table dense disable-sort :headers="headers" :items="plan" hide-default-footer>
                <template v-slot:item.name="{ item }">
                  <v-chip color="success" outlined x-small style="width:35px;">{{ item.name | simplify }}</v-chip>
                </template>
                <template v-slot:item.start="{ item }">
                  {{ item.start | time }}
                </template>
                <template v-slot:item.end="{ item }">
                  {{ item.end | time }}
                </template>
                <template v-slot:item.AoG="{ item }"> {{ item.AoG }}% </template>
                <template v-slot:item.GT="{ item }"> {{ item.GT }}% </template>
                <template v-slot:item.PR="{ item }">
                  {{ item.PR.toFixed(2) }}
                </template>
              </v-data-table>
            </v-container>
          </v-tab-item>
        </v-tabs>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';

export default {
  data: () => ({
    menu: false,
    modal: false,
    headers: [
      { text: 'Plan#', value: 'name' },
      { text: 'Start', value: 'start' },
      { text: 'End', value: 'end' },
      { text: 'AoG', value: 'AoG' },
      { text: 'GT', value: 'GT' },
      { text: 'PR', value: 'PR' }
    ],
    activeTab: null
  }),
  computed: {
    ready() {
      return this.activeSignal && 'plans' in this.activeSignal && Object.keys(this.activeSignal.plans).length > 0;
    },
    imageSrc() {
      let id = this.activeMarker ? this.activeMarker.id : 0;
      return require(`@/assets/images/${id}.jpg`);
    },
    currentDate() {
      return this.$store.state.hr.currentDate;
    },
    activeMarker() {
      return this.$store.state.hr.activeMarker;
    },
    activeSignal() {
      return (
        this.$store.state.hr.currentSignal || {
          id: 6,
          name: 'No Signal',
          phases: [],
          detectors: [],
          plans: []
        }
      );
    },
    phases() {
      let phases = [];
      if ('plans' in this.activeSignal) {
        phases = Object.keys(this.activeSignal.plans);
      }
      return phases;
    },
    plans() {
      let plans = [];
      if ('plans' in this.activeSignal) {
        plans = Object.values(this.activeSignal.plans);
      }
      return plans;
    }
  },
  filters: {
    simplify: function(name) {
      return name.split(' ')[1];
    },
    time: function(timestamp) {
      const date = new Date(timestamp);
      const time = ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2);
      return time;
    },
    date: function(date) {
      return Utils.formatDateLong(date);
    }
  },
  methods: {
    close() {
      this.$store.commit('hr/TOGGLE_SHOW_PANEL');
    }
  }
};
</script>
<style lang="scss" scoped>
.signal-name {
  background-color: #607d8b;
  padding: 5px 10px;
  color: white;
  font-size: large;
  font-weight: bold;
}
.time-display {
  margin: 5px 10px;
  padding: 5px 10px;
  font-size: 1rem;
}
.signal-id {
  color: #555;
  font-size: small;
  font-weight: normal;
  margin-bottom: 30px;
}
.signal-btn {
  margin-top: -5px;
  margin-left: 5px;
  display: inline-block;
}
table.v-table tbody td {
  font-size: 8px !important;
}
.v-tab {
  padding: 0px !important;
}
</style>

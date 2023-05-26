<template>
  <div v-if="!$vuetify.breakpoint.mobile">
    <v-card class="mx-auto" elevation="0" style="position: absolute; top: 10px; left:10px; " color="rgb(0, 0, 0, 0.0)">
      <v-toolbar light dense floating height="40">
        <v-menu bottom left offset-y min-width="350" v-model="isMenuOpened">
          <template v-slot:activator="{ on, attrs }">
            <v-btn icon v-bind="attrs" v-on="on">
              <v-app-bar-nav-icon></v-app-bar-nav-icon>
              <v-chip small outlined class="ma-2 overline" :color="tripColor">
                Trip <strong>{{ currentTrip.id ? currentTrip.id : '___' }}</strong>
              </v-chip>
            </v-btn>
          </template>

          <v-card style="height: 500px; width: 440px">
            <v-card-actions class="mt-n4 d-flex justify-space-between">
              <div style="width: 220px">
                <DatePicker type="short" @date="dateSelected" />
              </div>
              <div>
                <v-btn small @click="openCSPIFull" class="mr-2 mt-2 primary" :loading="loading" :disabled="hideCSPIBtn">
                  CSPI
                </v-btn>
                <v-btn small icon @click.stop="refreshTripData" class="mr-2 mt-2" :loading="loading">
                  <v-icon>mdi-refresh</v-icon>
                </v-btn>
              </div>
            </v-card-actions>
            <TripList class="mt-n3" :headers="headers" :items="items" :showCSPI="true" />
          </v-card>
        </v-menu>

        <v-divider vertical class="ml-13" />
        <v-slider v-model="progress" class="ml-1 mt-6" style="width: 120px"></v-slider>

        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <span v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-chip small color=""> {{ playbackSpeed }}x </v-chip>
                </span>
              </template>
              <span> Playback Speed</span>
            </v-tooltip>
          </template>

          <v-list>
            <v-list-item v-for="item in speed_menu_items" :key="item.id" @click="speedMenuItemClicked(item.value)">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-divider vertical class="ml-2" />

        <v-btn icon @click="setPlayStart" :disabled="!validTrip">
          <v-icon v-text="playButtonIcon"></v-icon>
        </v-btn>
        <v-btn icon @click="setPlayStop" :disabled="!validTrip || stopState">
          <v-icon>mdi-stop</v-icon>
        </v-btn>
      </v-toolbar>

      <v-card-text style="height: 150px;" v-if="showPanel">
        <TripState :state="currentState" :color="stateColor" />
      </v-card-text>
    </v-card>
  </div>
  <div v-else>
    <v-card class="mx-auto" elevation="0" style="position: absolute; top: 10px; left:10px; " color="rgb(0, 0, 0, 0.0)">
      <v-toolbar light dense floating height="40">
        <v-menu bottom left offset-y min-width="350" v-model="isMenuOpened">
          <template v-slot:activator="{ on, attrs }">
            <v-btn icon v-bind="attrs" v-on="on">
              <v-app-bar-nav-icon></v-app-bar-nav-icon>
            </v-btn>
          </template>

          <v-card style="height: 500px; width: 455px">
            <v-card-actions class="mt-n4 d-flex justify-space-between">
              <div style="width: 150px">
                <DatePicker type="short" @date="dateSelected" />
              </div>
              <v-btn small icon @click.stop="refreshTripData" class="mr-2 mt-2" :loading="loading">
                <v-icon>mdi-refresh</v-icon>
              </v-btn>
            </v-card-actions>
            <TripList class="mt-n3" :headers="headers" :items="items" :showCSPI="true" />
          </v-card>
        </v-menu>

        <v-chip small outlined class="ma-2 overline" :color="tripColor">
          Trip <strong>{{ currentTrip.id ? currentTrip.id : '___' }}</strong>
        </v-chip>

        <v-slider v-model="progress" class="mt-6" style="width: 90px"></v-slider>

        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <span v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-chip small color=""> {{ playbackSpeed }}x </v-chip>
                </span>
              </template>
              <span> Playback Speed</span>
            </v-tooltip>
          </template>

          <v-list>
            <v-list-item v-for="item in speed_menu_items" :key="item.id" @click="speedMenuItemClicked(item.value)">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-toolbar>
    </v-card>
    <v-card class="mx-auto" elevation="0" style="position: absolute; top: 55px; left:10px; " color="rgb(0, 0, 0, 0.0)">
      <v-toolbar light dense floating height="40">
        <v-btn icon @click="setPlayStart" :disabled="!validTrip">
          <v-icon v-text="playButtonIcon"></v-icon>
        </v-btn>
        <v-btn icon @click="setPlayStop" :disabled="!validTrip || stopState">
          <v-icon>mdi-stop</v-icon>
        </v-btn>
      </v-toolbar>

      <v-card-text style="height: 150px;" v-if="showPanel">
        <TripState :state="currentState" :color="stateColor" />
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
import Api from '@/utils/api/cav';
import { mapState } from 'vuex';
import DatePicker from '@/components/modules/cav/DatePicker';
import TripList from '@/components/modules/cav/TripList';
import TripState from '@/components/modules/cav/TripState';
import CSPISignals from '@/utils/CSPISignals.js';

export default {
  props: ['entities'],
  components: { TripState, TripList, DatePicker },
  data: () => ({
    allSignals: CSPISignals,
    loading: false,
    isMenuOpened: false,
    playButtonIcon: 'mdi-play',
    currentDate: new Date(),
    showPanel: false,
    map_menu_items: [
      { title: 'Ongoing Trips', icon: 'mdi-playlist-remove ', id: 0 },
      { title: 'Completed Trips', icon: 'mdi-playlist-check ', id: 1 }
    ],
    headers: [
      { text: 'Start Time', value: 'startTime' },
      { text: 'Duration', value: 'duration' },
      { text: 'Distance', value: 'distance' },
      { text: 'Status', value: 'status' },
      { text: 'CSPI', value: 'segment' }
    ],
    items: [],
    speed_menu_items: [
      { title: '50x', value: 50 },
      { title: '30x', value: 30 },
      { title: '20x', value: 20 },
      { title: '10x', value: 10 },
      { title: '5x', value: 5 },
      { title: '1x', value: 1 }
    ]
  }),

  computed: {
    hideCSPIBtn() {
      return !this.items || (this.items && this.items.filter(x => x.cspiData).length == 0);
    },

    validTrip() {
      return Object.keys(this.currentTrip).length > 0;
    },

    stopState() {
      return this.playState === 'stop';
    },

    isLive() {
      return this.currentTrip.status === 0;
    },

    stateColor() {
      return this.currentTrip.status === 0 ? 'red' : this.darkMode ? 'white' : 'green darken-3';
    },

    tripColor() {
      return this.currentTrip.status === 0 ? 'red' : 'primary';
    },

    progress: {
      get() {
        return this.currentProgress;
      },
      set(value) {
        this.$store.commit('cav/SET_CURRENT_PROGRESS', value);
        this.$bus.$emit('PLAYBACK_PROGRESS_CHANGED', value);
      }
    },

    ...mapState(['darkMode']),
    ...mapState('cav', ['currentTrip', 'playState', 'currentState', 'currentProgress', 'playbackSpeed'])
  },

  watch: {
    isMenuOpened() {
      // if (value) {
      //   this.fetchTripList();
      // }
      // console.log(value);
    }
  },

  mounted() {
    this.$bus.$on('PLAY_STOP_BUTTON_PRESSED', () => {
      this.setPlayStop();
    });

    this.refreshTripData();
  },

  methods: {
    openCSPIFull() {
      this.$bus.$emit('TOGGLE_CSPI_FULL', true);
    },

    showDetails() {
      this.$store.commit('cav/TOGGLE_SHOW_PANEL');
    },

    speedMenuItemClicked(value) {
      this.$store.commit('cav/SET_PLAYBACK_SPEED', value);
    },

    setPlayStart() {
      if (this.validTrip) {
        if (this.playState === 'stop') {
          this.playButtonIcon = 'mdi-pause';
          this.$store.commit('cav/SET_PLAY_STATE', 'play');
        } else if (this.playState == 'play') {
          this.playButtonIcon = 'mdi-play';
          this.$store.commit('cav/SET_PLAY_STATE', 'pause');
        } else if (this.playState == 'pause') {
          this.playButtonIcon = 'mdi-pause';
          this.$store.commit('cav/SET_PLAY_STATE', 'resume');
        } else if (this.playState == 'resume') {
          this.playButtonIcon = 'mdi-play';
          this.$store.commit('cav/SET_PLAY_STATE', 'pause');
        }
        this.showPanel = true;
      }
    },

    setPlayStop() {
      if (this.validTrip) {
        this.showPanel = false;
        this.playButtonIcon = 'mdi-play';
        this.$store.commit('cav/SET_PLAY_STATE', 'stop');
      }
    },

    dateSelected(date) {
      this.currentDate = date;
      this.refreshTripData();
    },

    refreshTripData() {
      this.fetchTripList(this.currentDate.getTime());
      this.fetchTripListWithCSPI(this.currentDate.getTime());
    },

    processTrips(trips) {
      trips.forEach(trip => {
        let signalsToProcess = JSON.parse(JSON.stringify(this.allSignals));
        let encounteredSignals = [];
        if (trip.segment) {
          for (let i = 0; i < trip.segment.path.length; i++) {
            let node = { ...trip.segment.path[i], ...trip.segment.state[i] };
            let nearSignal = this.checkIfNearSignal(node, signalsToProcess);
            if (nearSignal) {
              signalsToProcess.splice(nearSignal.idx, 1);
              encounteredSignals.push({ signal: nearSignal.signal, idx: i, ...node });
            }
          }
          let uniqueEncounteredZones = encounteredSignals.map(x => x.signal.zoneId);
          let zoneOccurences = {};
          for (let i = 0; i < uniqueEncounteredZones.length; i++) {
            zoneOccurences[uniqueEncounteredZones[i]] = (zoneOccurences[uniqueEncounteredZones[i]] || 0) + 1;
          }

          let cspiData = [];
          let id = 1;
          for (const [zoneId, occurences] of Object.entries(zoneOccurences)) {
            if (occurences >= 3) {
              let encounteredSignalsInZone = encounteredSignals.filter(x => x.signal.zoneId == zoneId);
              cspiData.push(this.processCSPIData(trip, encounteredSignalsInZone, zoneId, occurences, id));
              id++;
            }
          }
          if (cspiData.length > 0) {
            cspiData.push(this.getAverage(cspiData));
          }
          trip.cspiData = cspiData;
        }
      });
      return trips;
    },

    processCSPIData(trip, signals, zoneId, occurences, id) {
      let startIdx = signals[0].idx;
      let endIdx = signals[signals.length - 1].idx;
      let avgSpd = null;
      let gr = null;
      let spm = null;

      let totalDist = 0;
      let wasAbove15mph = [false, false];
      let carStops = 0;
      let redStops = 0;
      let speedData = [];
      let stopsInTrip = [[trip.segment.state[startIdx].time, 0]];
      let redsInTrip = [[trip.segment.state[startIdx].time, 0]];
      for (let i = startIdx; i < endIdx; i++) {
        let durationInHours = (trip.segment.state[i + 1].time - trip.segment.state[i].time) / (1000 * 60 * 60);
        totalDist += trip.segment.state[i].speed * durationInHours;

        if (trip.segment.state[i].speed >= 15) {
          wasAbove15mph = [true, true];
        }
        if (wasAbove15mph[0] && trip.segment.state[i].speed < 5) {
          wasAbove15mph[0] = false;
          carStops++;
          stopsInTrip.push([trip.segment.state[i].time, carStops - 1]);
          stopsInTrip.push([trip.segment.state[i].time + 1, carStops]);
        }
        if (wasAbove15mph[1] && trip.segment.state[i].speed == 0) {
          wasAbove15mph[1] = false;
          redStops++;
          redsInTrip.push([trip.segment.state[i].time, redStops - 1]);
          redsInTrip.push([trip.segment.state[i].time + 1, redStops]);
        }
        speedData.push([trip.segment.state[i].time, trip.segment.state[i].speed]);
      }
      stopsInTrip.push([trip.segment.state[endIdx].time, carStops]);
      redsInTrip.push([trip.segment.state[endIdx].time, redStops]);
      if (redStops > occurences) {
        redsInTrip = null;
      }

      avgSpd = totalDist / ((trip.segment.state[endIdx].time - trip.segment.state[startIdx].time) / (1000 * 60 * 60));
      avgSpd = parseFloat(avgSpd).toFixed(2);

      spm = carStops / totalDist;

      redStops = redStops > occurences ? occurences : redStops;
      let greenStops = occurences - redStops < 0 ? 0 : occurences - redStops;
      gr = greenStops / redStops;

      let group = this.getGroupName(zoneId);
      let speedScore = this.getScore('speed', avgSpd);
      let grScore = this.getScore('gr', gr);
      let spmScore = this.getScore('spm', spm);
      return {
        id: id,
        tripId: trip.id,
        startTS: trip.segment.state[startIdx].time,
        endTS: trip.segment.state[endIdx].time,
        startIdx,
        endIdx,
        totalDist,
        zoneId,
        group,
        score: speedScore + grScore + spmScore,
        greenStops,
        redStops,
        avgSpd,
        gr,
        spm,
        speedScore,
        grScore,
        spmScore,
        stops: occurences,
        speedData,
        stopsInTrip,
        redsInTrip
      };
    },

    getAverage(data) {
      let speedScore = Math.round(data.reduce((sum, x) => sum + x.speedScore, 0) / data.length, 0);
      let grScore = Math.round(data.reduce((sum, x) => sum + x.grScore, 0) / data.length, 0);
      let spmScore = Math.round(data.reduce((sum, x) => sum + x.spmScore, 0) / data.length, 0);
      return {
        id: 999,
        group: 'Average',
        score: speedScore + grScore + spmScore,
        speedScore: speedScore,
        grScore: grScore,
        spmScore: spmScore
      };
    },

    getScore(type, val) {
      if (type == 'speed') {
        if (val == null || val <= 15) {
          return 8;
        } else if (val <= 20) {
          return 15;
        } else if (val <= 22) {
          return 18;
        } else if (val <= 24) {
          return 21;
        } else if (val <= 26) {
          return 24;
        } else if (val <= 28) {
          return 27;
        } else if (val <= 30) {
          return 30;
        } else if (val <= 32) {
          return 33;
        } else {
          return 36;
        }
      }
      if (type == 'gr') {
        if (val == null || val <= 1.0) {
          return 8;
        } else if (val <= 1.5) {
          return 12;
        } else if (val <= 2.0) {
          return 16;
        } else if (val <= 2.5) {
          return 20;
        } else if (val <= 3.0) {
          return 24;
        } else if (val <= 3.5) {
          return 28;
        } else if (val <= 4.0) {
          return 32;
        } else if (val <= 4.5) {
          return 36;
        } else {
          return 40;
        }
      }
      if (type == 'spm') {
        if (val == null) {
          return 17;
        } else if (val <= 0.7) {
          return 33;
        } else if (val <= 0.9) {
          return 31;
        } else if (val <= 1.1) {
          return 29;
        } else if (val <= 1.3) {
          return 27;
        } else if (val <= 1.5) {
          return 25;
        } else if (val <= 1.7) {
          return 23;
        } else if (val <= 1.9) {
          return 21;
        } else if (val <= 2.1) {
          return 19;
        } else {
          return 17;
        }
      } else return 0;
    },

    getGroupName(zoneId) {
      return zoneId == 0
        ? 'S020A'
        : zoneId == 1
        ? 'S020B'
        : zoneId == 2
        ? 'S020C'
        : zoneId == 3
        ? 'S018'
        : zoneId == 4
        ? 'S017'
        : 'S016';
    },

    checkIfNearSignal(node, signalsToProcess) {
      let nearSignal = null;
      signalsToProcess.forEach((signal, i) => {
        let sigPos = signal.position;
        if (
          node.lat < sigPos.lat + 0.0005 &&
          node.lat > sigPos.lat - 0.0005 &&
          node.lng < sigPos.lng + 0.0005 &&
          node.lng > sigPos.lng - 0.0005
        ) {
          nearSignal = { signal, idx: i };
        }
      });
      return nearSignal;
    },

    async fetchTripList(time) {
      try {
        const response = await Api.fetchTrips(time, 50);
        if (response.status === 200) {
          const trips = response.data;
          this.items = trips;
        }
      } catch (error) {
        console.log(error);
      }
    },

    async fetchTripListWithCSPI(time) {
      try {
        const response = await Api.fetchTripsCSPI(time, 50);
        if (response.status === 200) {
          const trips = response.data;
          const processedTrips = this.processTrips(trips);
          this.items = processedTrips;
          // console.log("Trips: %o", processedTrips);
          this.$bus.$emit('CAV_TRIPS_SELECTED', this.items);

          let cspiTrips = processedTrips.filter(x => x.cspiData && x.cspiData.length > 0);
          let text = `Computed ${cspiTrips.length} trips with CSPI values out of ${processedTrips.length} trips.`;
          this.$store.dispatch('setSystemStatus', { text: text, color: 'info' });
        }
      } catch (error) {
        console.log(error);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
@keyframes metronome-example {
  from {
    transform: scale(0.5);
  }

  to {
    transform: scale(1);
  }
}

.v-avatar--metronome {
  animation-name: metronome-example;
  animation-iteration-count: infinite;
  animation-direction: alternate;
}
</style>

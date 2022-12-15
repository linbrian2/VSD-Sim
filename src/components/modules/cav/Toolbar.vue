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

          <v-card style="height: 500px; width: 425px">
            <v-card-actions class="mt-n4 d-flex justify-space-between">
              <div style="width: 220px">
                <DatePicker type="short" @date="dateSelected" />
              </div>
              <v-btn small icon @click.stop="refreshTripData" class="mr-2 mt-2" :loading="loading">
                <v-icon>mdi-refresh</v-icon>
              </v-btn>
            </v-card-actions>
            <TripList class="mt-n3" :headers="headers" :items="items" />
          </v-card>
        </v-menu>

        <!-- <v-chip small outlined class="ma-2 overline" :color="tripColor">
          Trip <strong>{{ currentTrip.id ? currentTrip.id : '___' }}</strong>
        </v-chip> -->

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

        <v-divider vertical class="ml-1" />

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn icon v-on="on" @click.stop="showDetails">
              <v-icon color="info">mdi-information-outline </v-icon>
            </v-btn>
          </template>
          <span>Show Details</span>
        </v-tooltip>
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
            <TripList class="mt-n3" :headers="headers" :items="items" />
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

        <v-divider vertical class="ml-1" />

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn icon v-on="on" @click.stop="showDetails">
              <v-icon color="info">mdi-information-outline </v-icon>
            </v-btn>
          </template>
          <span>Show Details</span>
        </v-tooltip>
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
export default {
  props: ['entities'],
  components: { TripState, TripList, DatePicker },
  data: () => ({
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
      { text: 'Status', value: 'status' }
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

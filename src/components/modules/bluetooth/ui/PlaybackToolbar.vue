<template>
  <div>
    <v-tooltip bottom>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          small
          v-bind="attrs"
          v-on="on"
          @click="playbackToggle = !playbackToggle"
          style="position: absolute; top: 10px; left: 422px; height: 40px"
        >
          <v-icon>mdi-map-clock-outline</v-icon>
        </v-btn>
      </template>
      <span>{{ playbackToggle ? 'Toggle Playback Mode Off' : 'Toggle Playback Mode On' }}</span>
    </v-tooltip>
    <v-scroll-x-reverse-transition>
      <v-toolbar
        dense
        floating
        height="40"
        style="position: absolute; top: 10px; left: 494px; height: 40px"
        v-if="playbackToggle"
      >
        <v-chip
          @click="menu2 = !menu2"
          :disabled="playState == 'play' || playState == 'resume'"
          small
          outlined
          class="ma-2 overline"
        >
          {{ timeStr }}
        </v-chip>
        <v-slider
          v-model="progress"
          class="mt-6"
          style="width: 160px"
          min="0"
          :max="max"
          :loading="!fullDayDataDone"
          :disabled="!fullDayDataDone"
        ></v-slider>

        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <span v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-chip small color="" class="mr-2"> {{ playbackSpeed }}x </v-chip>
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

        <v-divider vertical />

        <v-btn icon @click="setPlayStart" :disabled="!fullDayDataDone">
          <v-icon v-text="playButtonIcon"></v-icon>
        </v-btn>
        <v-btn icon @click="setPlayStop" :disabled="stopState || !fullDayDataDone">
          <v-icon>mdi-stop</v-icon>
        </v-btn>
        <v-btn icon @click="progress = 0" :disabled="progress == 0">
          <v-icon>mdi-replay</v-icon>
        </v-btn>
      </v-toolbar>
    </v-scroll-x-reverse-transition>
    <v-scroll-x-reverse-transition>
      <v-toolbar dense floating height="40" style="position: absolute; top: 10px; right: 10px" loading>
        <div>
          Timeline Data availability:
          <v-tooltip bottom>
            <template v-slot:activator="{ on, attrs }">
              <v-icon color="green" class="px-1" v-bind="attrs" v-on="on" :disabled="!wazeFull">mdi-waze</v-icon>
            </template>
            <span>Waze Data - Full Day</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on, attrs }">
              <v-icon color="green" class="pr-1" v-bind="attrs" v-on="on" :disabled="!segmentsFull"
                >mdi-vector-line</v-icon
              >
            </template>
            <span>Segment Data - Full Day</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on, attrs }">
              <v-icon color="green" class="pr-1" v-bind="attrs" v-on="on" :disabled="!devicesFull">mdi-leak</v-icon>
            </template>
            <span>Traffic Flow Detector Data - Full Day</span>
          </v-tooltip>
        </div>
      </v-toolbar>
    </v-scroll-x-reverse-transition>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import { DateTime } from 'luxon';
import Utils from '@/utils/Utils';

export default {
  props: ['entities'],
  data: () => ({
    playbackToggle: false,
    playbackOpen: false,
    endDT: null,
    playbackInterval: null,
    timeInterval: null,
    loading: false,
    isMenuOpened: false,
    playButtonIcon: 'mdi-play',
    headers: [
      { text: 'Start Time', value: 'startTime' },
      { text: 'Duration', value: 'duration' },
      { text: 'Distance', value: 'distance' },
      { text: 'Status', value: 'status' },
    ],
    items: [],
    speed_menu_items: [
      { title: '60x', value: 60 },
      { title: '30x', value: 30 },
      { title: '15x', value: 15 },
      { title: '10x', value: 10 },
      { title: '5x', value: 5 },
      { title: '1x', value: 1 },
    ],
  }),
  beforeDestroy() {
    clearInterval(this.playbackInterval);
    clearInterval(this.timeInterval);
    if (this.$store.state.bluetooth.playState == 'play' || this.$store.state.bluetooth.playState == 'resume') {
      this.playButtonIcon = 'mdi-play';
      this.$store.commit('bluetooth/SET_PLAY_STATE', 'pause');
    }
  },
  mounted() {
    this.playbackInterval = setInterval(() => {
      if (this.$store.state.bluetooth.playState == 'play' || this.$store.state.bluetooth.playState == 'resume') {
        if (this.progress >= this.max - this.$store.state.bluetooth.playbackSpeed) {
          /* If End reached */
          this.progress = this.max;
          this.$bus.$emit('UPDATE_TIMELINE', this.time);
          this.setPlayStart();
        } else {
          /* If in progress */
          this.progress += this.$store.state.bluetooth.playbackSpeed;
          this.$bus.$emit('UPDATE_TIMELINE', this.time);
        }
      }
    }, 1000);
    this.timeInterval = setInterval(() => {
      if (!this.$store.state.bluetooth.modes.historical) {
        this.endDT = DateTime.local().setZone('America/New_York');
      } else {
        this.endDT = null;
      }
    }, 100);

    this.$bus.$on('PLAY_STOP_BUTTON_PRESSED', () => {
      this.setPlayStop();
    });

    this.$bus.$on('FETCH_HISTORICAL', (dt) => {
      setTimeout(() => {
        this.progress = dt.hour * 60 + dt.minute;
      }, 1000);
    });
  },
  methods: {
    /* Playback Stuff */
    speedMenuItemClicked(value) {
      this.$store.commit('bluetooth/SET_PLAYBACK_SPEED', value);
    },
    setPlayStart() {
      this.menu2 = false;
      if (this.playState === 'stop') {
        this.playButtonIcon = 'mdi-pause';
        this.$store.commit('bluetooth/SET_PLAY_STATE', 'play');
      } else if (this.playState == 'play') {
        this.playButtonIcon = 'mdi-play';
        this.$store.commit('bluetooth/SET_PLAY_STATE', 'pause');
      } else if (this.playState == 'pause') {
        this.playButtonIcon = 'mdi-pause';
        this.$store.commit('bluetooth/SET_PLAY_STATE', 'resume');
      } else if (this.playState == 'resume') {
        this.playButtonIcon = 'mdi-play';
        this.$store.commit('bluetooth/SET_PLAY_STATE', 'pause');
      }
    },

    setPlayStop() {
      this.playButtonIcon = 'mdi-play';
      this.$store.commit('bluetooth/SET_PLAY_STATE', 'stop');
      this.$bus.$emit('RESET_TO_SELECTED_TIME', this.$store.state.currentDate);
    },
  },
  computed: {
    wazeFull() {
      let apiData = this.$store.state.bluetooth.apiData;
      if (apiData && apiData.wazeFull && apiData.wazeFull.length > 0) {
        return apiData.wazeFull;
      } else {
        return null;
      }
    },
    segmentsFull() {
      let apiData = this.$store.state.bluetooth.apiData;
      if (apiData && apiData.segmentsFull && apiData.segmentsFull.length > 0) {
        return apiData.segmentsFull;
      } else {
        return null;
      }
    },
    devicesFull() {
      let apiData = this.$store.state.bluetooth.apiData;
      if (apiData && apiData.devicesFull && apiData.devicesFull.length > 0) {
        return apiData.devicesFull;
      } else {
        return null;
      }
    },
    fullDayDataDone() {
      return this.wazeFull;
    },

    max() {
      if (this.isToday) {
        let mins = this.endDT.hour * 60 + this.endDT.minute;
        return mins;
      } else {
        return 1439;
      }
    },

    time() {
      let selectedDT = this.currentDate;
      if (this.progress >= 0 && selectedDT) {
        let hours = Math.floor(this.progress / 60);
        let mins = this.progress % 60;
        let dt = DateTime.fromObject({
          year: selectedDT.getFullYear(),
          month: selectedDT.getMonth() + 1,
          day: selectedDT.getDate(),
          hour: hours,
          minute: mins,
          zone: 'America/New_York',
        });
        return dt;
      } else {
        return null;
      }
    },

    timeStr() {
      if (this.playbackToggle) {
        if (this.time) {
          return `${Utils.formatXX(this.time.hour)}:${Utils.formatXX(this.time.minute)}`;
        } else {
          return '-';
        }
      } else {
        return `${Utils.formatXX(this.currentDate.getHours())}:${Utils.formatXX(this.currentDate.getMinutes())}`;
      }
    },

    stopState() {
      return this.playState === 'stop';
    },

    isToday() {
      if (this.currentDate) {
        const today = new Date()
        return this.currentDate.getDate() == today.getDate() &&
               this.currentDate.getMonth() == today.getMonth() &&
               this.currentDate.getFullYear() == today.getFullYear()
      } else {
        return true
      }
    },

    progress: {
      get() {
        return this.currentProgress;
      },
      set(value) {
        this.$store.commit('bluetooth/SET_CURRENT_PROGRESS', value);
        this.$bus.$emit('PLAYBACK_PROGRESS_CHANGED', value);
      },
    },
    ...mapState(['currentDate']),
    ...mapState('bluetooth', ['playState', 'currentState', 'currentProgress', 'playbackSpeed']),
  },
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

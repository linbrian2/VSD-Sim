<template>
  <div class="playback-toolbar">
    <!-- TODO: Fix playback having double incrTimers -->
    <v-toolbar dense floating height="40" :style="`position: absolute; top: 10px; left: ${left}; height: 40px`">
      <v-chip small outlined class="ma-2 overline"> {{ timeStr }} </v-chip>
      <v-slider
        :disabled="loading"
        v-model="elapsedTime"
        class="mt-6"
        :style="`width: ${$vuetify.breakpoint.mobile ? '100px' : '120px'}`"
        min="0"
        :max="max"
        @input="input"
        @mousedown="stopIncrTimer"
        @mouseup="mouseReleased"
      ></v-slider>
      <v-menu bottom right offset-y>
        <template v-slot:activator="{ on: menu, attrs }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on: tooltip }">
              <span v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                <v-chip small color="" class="mr-2"> {{ playbackSpeed }} </v-chip>
              </span>
            </template>
            <span> Playback Speed</span>
          </v-tooltip>
        </template>
        <v-list>
          <v-list-item v-for="item in speedMenuItems" :key="item.id" @click="speedMenuItemClicked(item.title)">
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>

      <v-divider vertical />

      <v-btn icon @click="setPlayStart" :disabled="loading">
        <v-icon v-text="playButtonIcon"></v-icon>
      </v-btn>
      <v-btn v-if="!$vuetify.breakpoint.mobile" icon @click="setPlayStop" :disabled="stopState || loading">
        <v-icon>mdi-stop</v-icon>
      </v-btn>
      <v-btn v-if="!$vuetify.breakpoint.mobile" icon @click="replay" :disabled="elapsedTime == 0">
        <v-icon>mdi-replay</v-icon>
      </v-btn>
    </v-toolbar>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';

export default {
  props: {
    date: Date,
    duration: Number,
    loading: Boolean,
    left: {
      type: String,
      default: '20px'
    }
  },
  data() {
    return {
      sliderClickedFlag: false,
      iterations: 0,
      elapsedTime: 0,
      playbackSpeed: '10x',
      playButtonIcon: 'mdi-play',
      playState: 'stop',
      speedMenuItems: [
        { title: '60x', value: 60 },
        { title: '30x', value: 30 },
        { title: '15x', value: 15 },
        { title: '10x', value: 10 },
        { title: '5x', value: 5 }
      ],
      incrTimer: null
    };
  },
  computed: {
    max() {
      if (this.duration) {
        return this.duration;
      } else {
        return 24 * 60 - 1;
      }
    },
    stopState() {
      return this.playState === 'stop';
    },
    trackingTime() {
      if (this.date) {
        return new Date(this.date.getTime() + this.elapsedTime * 1000 * 60);
      } else {
        return this.date;
      }
    },
    timeStr() {
      if (this.date) {
        return Utils.formatTimeAsMinute(
          new Date(this.trackingTime.getTime() - (this.trackingTime.getTime() % (5 * 60 * 1000)))
        );
      } else {
        return '00:00';
      }
    }
  },
  beforeDestroy() {
    this.stopIncrTimer();
  },
  methods: {
    mouseReleased() {
      if (!this.loading && (this.playState == 'play' || this.playState == 'resume')) {
        this.startIncrTimer();
      }
    },
    reInit() {
      this.$emit('reInit');
    },
    input() {
      this.reInit();
      let trackingTime = new Date(this.date.getTime() + this.elapsedTime * 60 * 1000);
      this.$emit('processTime', trackingTime);
    },
    sliderClicked(flag) {
      this.sliderClickedFlag = flag;
    },
    replay() {
      this.elapsedTime = 0;
      this.reInit();
    },
    startIncrTimer() {
      this.incrTimer = setInterval(this.incrElapsedTime, 1000);
    },
    stopIncrTimer() {
      if (this.incrTimer) {
        clearInterval(this.incrTimer);
        this.incrTimer = null;
        if (this.playState == 'stop') {
          this.elapsedTime = 0;
        }
      }
    },
    incrElapsedTime() {
      if (this.elapsedTime >= this.max - 4) {
        this.setPlayStop();
      }
      if (!this.sliderClickedFlag) {
        this.iterations++;
        let incrVal = this.speedMenuItems.find(x => x.title === this.playbackSpeed).value;
        if (this.iterations >= 1 / incrVal) {
          this.elapsedTime += incrVal;
          let trackingTime = new Date(this.date.getTime() + this.elapsedTime * 60 * 1000);
          this.$emit('processTime', trackingTime, this.elapsedTime);
          this.iterations = 0;
        }
      }
    },
    speedMenuItemClicked(value) {
      this.playbackSpeed = value;
    },
    setPlayStart() {
      if (this.playState === 'stop') {
        this.playButtonIcon = 'mdi-pause';
        this.playState = 'play';
      } else if (this.playState == 'play') {
        this.playButtonIcon = 'mdi-play';
        this.playState = 'pause';
      } else if (this.playState == 'pause') {
        this.playButtonIcon = 'mdi-pause';
        this.playState = 'resume';
      } else if (this.playState == 'resume') {
        this.playButtonIcon = 'mdi-play';
        this.playState = 'pause';
      }
    },
    setPlayStop() {
      this.playButtonIcon = 'mdi-play';
      this.playState = 'stop';
      this.reInit();
    }
  },
  watch: {
    loading() {
      if (!this.loading) {
        this.setPlayStart();
      } else {
        this.elapsedTime = 0;
        this.setPlayStop();
      }
    },
    playState() {
      if (!this.loading && (this.playState == 'play' || this.playState == 'resume')) {
        this.startIncrTimer();
      } else {
        this.stopIncrTimer();
      }
    }
  }
};
</script>

<style></style>

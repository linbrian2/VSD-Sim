<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
    <SignalTimingTitleBar title="Signal Timing" :loading="loading" :refresh="refreshData">
      <div style="height:30px" class="d-flex justify-space-between align-center mt-2">
        <v-slider
          style="width:160px"
          v-model="sliderValue"
          dense
          color="orange darken-3"
          track-color="grey"
          height="18"
          always-dirty
          min="0"
          :max="sliderMax"
          class="mt-6"
          @change="sliderChanged"
        />

        <div class="ml-10">
          <v-menu offset-y>
            <template v-slot:activator="{ on, attrs }">
              <v-btn small rounded outlined color="white" dark v-bind="attrs" v-on="on">
                Speed: {{ selectedSpeed }}x
              </v-btn>
            </template>

            <v-list>
              <v-list-item v-for="(speed, i) in speed_items" :key="i" @click="speedMenuItemSelected(speed)">
                <v-list-item-title>SPEED: {{ speed }}X</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </div>
        <div class="ml-12">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn dark icon v-on="on" @click.stop="showTimeline">
                <v-icon :color="timelineVisible ? 'red' : ''">mdi-chart-gantt</v-icon>
              </v-btn>
            </template>
            <span>Show Timeline</span>
          </v-tooltip>
        </div>
      </div>
    </SignalTimingTitleBar>

    <GmapMap ref="mapRef" :options="options" :center="position" :zoom="11" map-type-id="roadmap" class="my-map">
      <SignalDisplay ref="signalRef" @startTask="startTimer" v-show="timing != null" />
    </GmapMap>
    <TimingToolbar v-show="timelineVisible" />
  </div>

  <div class="mobile" v-else>
    <SignalTimingTitleBar :loading="loading" :refresh="refreshData" :showRefresh="false" :showMap="false">
      <div style="height:30px" class="d-flex justify-space-between align-center mt-2">
        <v-slider
          style="width:160px"
          v-model="sliderValue"
          dense
          color="orange darken-3"
          track-color="grey"
          height="18"
          always-dirty
          min="0"
          :max="sliderMax"
          class="mt-6"
          @change="sliderChanged"
        />

        <div class="ml-10">
          <v-menu offset-y>
            <template v-slot:activator="{ on, attrs }">
              <v-btn small rounded outlined color="white" dark v-bind="attrs" v-on="on">
                Speed: {{ selectedSpeed }}x
              </v-btn>
            </template>

            <v-list>
              <v-list-item v-for="(speed, i) in speed_items" :key="i" @click="speedMenuItemSelected(speed)">
                <v-list-item-title>SPEED: {{ speed }}X</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </div>
        <div class="ml-12">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn dark icon v-on="on" @click.stop="showTimeline">
                <v-icon :color="timelineVisible ? 'red' : ''">mdi-chart-gantt</v-icon>
              </v-btn>
            </template>
            <span>Show Timeline</span>
          </v-tooltip>
        </div>
      </div>
    </SignalTimingTitleBar>

    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="11"
      map-type-id="roadmap"
      class="map-select-mobile"
    >
      <SignalDisplay ref="signalRef" @startTask="startTimer" v-show="timing != null" />
    </GmapMap>
    <TimingToolbar v-show="timelineVisible" />
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import { mapState, mapActions } from 'vuex';
import MapStyle from '@/utils/MapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import SignalDisplay from '@/components/modules/hr/SignalDisplay';
import TimingToolbar from '@/components/modules/hr/TimingToolbar';
import SignalTimingTitleBar from '@/components/modules/hr/SignalTimingTitleBar';

export default {
  components: {
    SignalTimingTitleBar,
    SignalDisplay,
    TimingToolbar
  },
  data: () => ({
    loading: false,
    map: null,
    timeControl: null,
    timeId: null,
    duration: 600 * 1000,
    intervalPeriod: 2000,
    sliderValue: 0,
    sliderMax: 864000,
    simulationStartTime: null,
    timelineVisible: false,
    speed_items: [1, 2, 4, 8, 16],
    selectedSpeed: 1,
    homeIcon: require('@/assets/home-24.png'),
    options: {
      zoomControl: true,
      zoomControlOptions: {
        position: 8
      },
      streetViewControl: false,
      fullscreenControl: true,
      mapTypeControl: false,

      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite']
      },
      gestureHandling: 'greedy'
    }
  }),
  computed: {
    markers() {
      return this.$store.state.hr.locations;
    },

    position() {
      return this.$store.state.position;
    },

    ...mapState(['currentDate']),
    ...mapState('hr', ['activeMarker', 'timing'])
  },
  watch: {
    currentDate(value) {
      this.simulationStartTime = Utils.getSameTimeAsToday(value);
      let start = this.getStartTime();
      this.loadAndUpdate(this.activeMarker, start);
    },
    sliderValue(value) {
      let startTime = Utils.scale2Date(this.currentDate, value / this.sliderMax);
      startTime = Utils.roundToMinutes(startTime, 1);
      this.updateTimePicker(startTime);
    }
  },
  mounted() {
    this.simulationStartTime = Utils.getSameTimeAsToday(this.currentDate);

    this.$bus.$on('GET_PLAYBACK_SIGNALS', ({ marker }) => {
      let start = this.getStartTime();
      this.loadAndUpdate(marker, start);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addHomeControl(map);
      this.addTimeControl(map);
      let start = this.getStartTime();
      this.loadAndUpdate(this.activeMarker, start);

      google.maps.event.addListener(map, 'projection_changed', () => {
        let proj = map.getProjection();
        this.$store.commit('hr/SET_MAP_PROJECTION', proj);
      });
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.loadPage(this.$vuetify.theme.dark);
  },

  beforeDestroy() {
    if (this.timeId != null) {
      clearInterval(this.timeId);
    }
  },

  methods: {
    loadPage(darkMode) {
      if (this.$refs.mapRef == null) {
        return;
      }

      // Use the default MapStyle no matter what
      if (darkMode && this.$refs.mapRef) {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: MapStyle });
        });
      } else {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: MapStyle });
        });
      }
    },

    refreshData() {
      let marker = this.activeMarker;
      if (marker != null) {
        let start = this.getStartTime();
        this.loadAndUpdate(marker, start);
      }
    },

    getMap() {
      return this.$refs.mapRef;
    },

    addHomeControl(map) {
      let options = {
        position: 'top_right',
        content: `<div style="margin:-5px 4px;"><img src="${this.homeIcon}"/></div>`,
        style: {
          width: '40px',
          height: '40px',
          margin: '10px',
          padding: '12px 3px',
          border: 'solid 1px #717B87',
          background: '#fff'
        },
        events: {
          click: () => {
            this.centerMap(map);
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    addTimeControl(map) {
      let options = {
        position: 'top_center',
        content: '',
        style: {
          width: '80px',
          height: '40px',
          margin: '10px 20px',
          padding: '12px 8px',
          border: 'solid 1px #717B87',
          color: '#00f',
          background: '#fff',
          fontSize: '14px',
          fontWeight: 'bold'
        }
      };
      this.timeControl = MapUtils.addControl(map, options);
    },

    centerMap(map) {
      if (map !== null && this.activeMarker) {
        map.panTo(this.activeMarker.position);
        map.setZoom(20);
      }
    },

    markerZoom(position, zoomLevel) {
      if (this.map !== null) {
        this.map.panTo(position);
        this.map.setZoom(zoomLevel);
      }
    },

    speedMenuItemSelected(speed) {
      //this.selectedSpeed = speed;
      console.log('speed=', speed);
    },

    showTimeline() {
      this.timelineVisible = !this.timelineVisible;
    },

    sliderChanged() {
      let startTime = Utils.scale2Date(this.currentDate, this.sliderValue / this.sliderMax);
      startTime = Utils.roundToMinutes(startTime, 1);
      this.setStartTime(startTime);
      this.loadAndUpdate(this.activeMarker, startTime.getTime());
    },

    timeChanged(time) {
      // Convert time string to date object
      let startTime = Utils.dateFromDateAndTimeString(this.currentDate, time);
      startTime = Utils.roundToMinutes(startTime, 1);
      this.setStartTime(startTime);
      this.loadAndUpdate(this.activeMarker, startTime.getTime());
    },

    updateSliderAndTime(currentTime) {
      // Set slider bar value
      this.sliderValue = Math.round(Utils.date2Scale(this.currentDate, currentTime) * this.sliderMax);

      // Set time picker time and time control on map
      this.updateTimePicker(currentTime);
      this.updateTimeControl(currentTime);
    },

    updateTimePicker(currentTime) {
      const time = Utils.formatTimeToMinutes(currentTime);
      if (this.$refs.timePicker) {
        this.$refs.timePicker.setTime(time);
      }
    },

    updateTimeControl(currentTime) {
      let timeStr = Utils.formatTime(currentTime);
      if (this.timeControl !== null) {
        this.timeControl.innerHTML = `<div style="text-align: center;vertical-align: middle;">${timeStr}</div>`;
      }
    },

    updateTime(time) {
      let t1 = Utils.roundToMinutes(time, 1);
      if (Math.abs(t1.getTime() - time.getTime()) < 5000) {
        this.updateSliderAndTime(time);
      }

      this.updateTimeControl(time);
    },

    setStartTime(startTime) {
      this.simulationStartTime = startTime;
    },

    getStartTime() {
      return this.simulationStartTime.getTime();
    },

    loadAndUpdate(marker, startTimestamp) {
      if (marker != null) {
        const currentTime = new Date(startTimestamp);
        this.updateSliderAndTime(currentTime);
        this.loadTimingData(marker.id, startTimestamp, this.duration);
        this.markerZoom(marker.position, 20);
      }
    },

    async loadTimingData(id, start, duration) {
      this.loading = true;
      await this.fetchTiming({ id, start, duration });
      this.loading = false;
    },

    startTimer() {
      let start = this.getStartTime();
      this.$store.commit('hr/SET_CURRENT_SIGNAL_TIME', new Date(start));

      if (this.timeId != null) {
        clearInterval(this.timeId);
      }

      let interval = this.intervalPeriod;
      let addedInterval = interval * this.selectedSpeed;
      this.timeId = setInterval(() => {
        let time = new Date(this.$store.state.hr.currentSignalTime.getTime() + addedInterval);
        this.$store.commit('hr/SET_CURRENT_SIGNAL_TIME', time);
        this.updateTime(time);
      }, interval);
    },

    ...mapActions('hr', ['fetchTiming'])
  }
};
</script>
<style>
.my-map {
  width: 100%;
  /* height: 100vh; */
  height: calc(100vh - 100px);
}
.gm-style div {
  -webkit-transform: scale(1.002);
}
.speed-control {
  background-color: white !important;
}
</style>

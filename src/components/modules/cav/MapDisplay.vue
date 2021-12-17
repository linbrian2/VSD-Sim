<template>
  <div>
    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="12"
      map-type-id="roadmap"
      class="my-map"
      style="margin-top:-1px; width: 100%; height:calc(100vh - 48px)"
    >
      <!-- trajectory -->
      <GmapPolyline :path.sync="segment" :options="segmentOptions" />
      <GmapMarker :position="startPoint" :clickable="false" :draggable="false" :icon="startIcon" />
      <GmapMarker :position="endPoint" :clickable="false" :draggable="false" :icon="endIcon" />

      <!-- Car -->
      <CarMarker :visible="carVisible" :position="currentCarPos" />
    </GmapMap>
    <Toolbar />
  </div>
</template>

<script>
/* global google */
import Api from '@/utils/api/cav';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import { LatLng, computeDistanceBetween } from 'spherical-geometry-js';
import Toolbar from '@/components/modules/cav/Toolbar';
import CarMarker from '@/components/modules/cav/CarMarker';
import useMapGeometry from '@/utils/MapGeometry';
import MapUtils from '@/utils/MapUtils.js';
import DarkMapStyle from '@/utils/DarkMapStyle.js';

export default {
  components: {
    Toolbar,
    CarMarker
  },
  data: () => ({
    homeIcon: require('@/assets/home-24.png'),
    startIcon: {
      url: require('@/assets/start.png'),
      size: { width: 32, height: 32, f: 'px', b: 'px' }
    },

    endIcon: {
      url: require('@/assets/end.png'),
      size: { width: 32, height: 32, f: 'px', b: 'px' }
    },
    markerOptions: {
      url: '',
      size: { width: 60, height: 90, f: 'px', b: 'px' },
      scaledSize: { width: 30, height: 45, f: 'px', b: 'px' }
    },
    map: null,
    options: {
      mapTypeControl: true,
      streetViewControl: false,
      fullscreenControl: true,
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'hybrid'],
        position: 6
      }
      // styles: MapStyles
    },

    segment: [],
    statelist: [],

    segmentOptions: {
      strokeColor: '#00ff00',
      strokeOpacity: 0.6,
      strokeWeight: 6
    },

    timeHandle: null,
    carUpdateTick: 250,
    prevCarDist: null,
    carVisible: false
  }),
  computed: {
    position() {
      return this.$store.state.position;
    },

    startPoint() {
      return this.segment && this.segment[0];
    },

    endPoint() {
      return this.segment && this.segment[this.segment.length - 1];
    },
    ...mapState('cav', ['currentCarPos', 'currentEvent', 'currentTrip', 'playState', 'playbackSpeed'])
  },
  watch: {
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(12);
      });
    },

    currentEvent(value) {
      if (value != null) {
        this.moveToSegmentEnd();

        if (value.path && value.path.length > 0) {
          this.segment = [...this.segment, ...value.path];
        }

        if (value.data && value.data.length > 0) {
          this.statelist = [...this.statelist, ...value.data];
        }
      }
    },

    currentTrip(value) {
      this.fetchRoute(this.map, value.id);
    },

    playState(state) {
      this.applyAnimation(state);
    }
  },

  mounted() {
    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addHomeControlToMap(map);
    });

    this.$bus.$on('PLAYBACK_PROGRESS_CHANGED', progress => {
      this.setProgress(progress);
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });
    this.loadPage(this.$vuetify.theme.dark);
  },

  beforeDestroy() {
    this.stopAnimation();
  },

  methods: {
    loadPage(darkMode) {
      if (this.$refs.mapRef == null) {
        return;
      }
      if (darkMode && this.$refs.mapRef) {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: DarkMapStyle });
        });
      } else {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: null });
        });
      }
    },

    getMap() {
      return this.$refs.mapRef;
    },

    addHomeControlToMap(map) {
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
            this.centerMap(map, this.segment);
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    centerMap(map, points) {
      if (points.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        points.forEach(point => bounds.extend(point));
        map.fitBounds(bounds);
      }
    },

    async fetchRoute(map, tripId) {
      this.loading = true;
      this.stopAnimation();

      try {
        const response = await Api.fetchTripSegment(tripId);
        if (response.data.status === 'OK') {
          const segment = response.data.data;

          // Center segment on map
          this.centerMap(map, segment.path);

          // Update segment
          this.segment = segment.path;
          this.statelist = segment.state;

          this.$bus.$emit('PLAY_STOP_BUTTON_PRESSED');
        }
      } catch (error) {
        console.log(error);
      }
      this.loading = false;
    },

    showCar(show) {
      this.carVisible = show;
    },

    //=============== animation functions ======================

    applyAnimation(state) {
      if (this.segment.length == 0) {
        return;
      }

      switch (state) {
        case 'play':
          this.startAnimation();
          break;
        case 'pause':
          this.stopTimer();
          break;
        case 'resume':
          this.updateTimer();
          break;
        case 'stop':
          this.stopAnimation();
          break;
      }
    },

    startAnimation() {
      if (!this.segment.length === 0) {
        return;
      }

      // Stop timer if not already
      this.stopAnimation();

      // Allow time for the initial map display
      this.updateTimer();
      this.showCar(true);

      // Set map zoom level
      this.map.setZoom(14);

      this.$store.commit('cav/SET_CURRENT_PROGRESS', 0);

      if (this.currentTrip.status > 0) {
        this.$store.commit('cav/SET_PLAYBACK_SPEED', 10);
      }
    },

    stopAnimation() {
      this.stopTimer();

      this.prevCarDist = 0;
      this.$store.commit('cav/SET_CAR_POSITION', this.segment[0]);
      this.$store.commit('cav/SET_CURRENT_STATE', this.statelist[0]);

      // Center map
      this.centerMap(this.map, this.segment);

      // Hide car
      this.showCar(false);

      //   this.$store.dispatch('WSDisconnect');
    },

    animateCar() {
      const result = this.nextPoint();
      if (result === null) {
        // Reach the end of segment path
        //  if (this.currentTrip.status > 0) {
        // Completed trip, just stop the animation
        this.$bus.$emit('PLAY_STOP_BUTTON_PRESSED');
        // } else {
        //   // Ongoing trip, let's launch the next step to waiting on data
        //   this.updateTimer();
        // }
        return;
      }

      // Move the car
      const { position, point } = result;
      this.$store.commit('cav/SET_CAR_POSITION', point);

      // Set current state
      const state = this.interpState(position);
      this.$store.commit('cav/SET_CURRENT_STATE', state);

      // Set current progress
      const progress = Math.round((position * 100) / this.segment.length);
      this.$store.commit('cav/SET_CURRENT_PROGRESS', progress);

      // Center map
      this.centerMapByCarPos(point);

      // Launch the next step
      this.updateTimer();
    },

    nextPoint() {
      const { getPointAtDistance, totalDistance } = useMapGeometry(this.segment);

      // Calculate the next location
      const currentSpeed = 50;
      const travelDistance = this.playbackSpeed * ((currentSpeed * this.carUpdateTick) / 1000);
      const d = this.prevCarDist + travelDistance;

      // Remember the current location
      if (this.currentTrip.status > 0) {
        this.prevCarDist = d;
      } else {
        if (d < totalDistance()) {
          this.prevCarDist = d;
        }
      }

      return getPointAtDistance(d);
    },

    interpState(position) {
      const ind = Math.floor(position);
      const m = position - ind;
      const s0 = this.statelist[ind];
      const s1 = this.statelist[ind - 1];
      return Utils.interpRound(s0, s1, m);
    },

    setProgress(progress) {
      const { getMetersAtPercentage } = useMapGeometry(this.segment);
      this.prevCarDist = getMetersAtPercentage(progress);
    },

    fastForward(secondsAgo) {
      if (!this.statelist || this.statelist.length < 1) {
        return;
      }

      const endIdx = this.statelist.length - 1;
      const endTime = this.statelist[endIdx].time;

      // Find the position whose time is the specified time ago from the end time
      let idx = endIdx;
      for (let i = endIdx - 1; i >= 0; i--) {
        const startTime = this.statelist[i].time;
        const duration = (endTime - startTime) / 100;
        if (duration >= secondsAgo) {
          idx = i;
          break;
        }
      }

      // Set the progress
      const progress = Math.floor((idx * 100) / this.statelist.length);
      this.setProgress(progress);
    },

    moveToSegmentEnd() {
      const { totalDistance } = useMapGeometry(this.segment);
      this.prevCarDist = totalDistance() - 10;
    },

    stopTimer() {
      if (this.timerHandle) {
        clearTimeout(this.timerHandle);
        this.timerHandle = null;
      }
    },

    updateTimer() {
      this.timerHandle = setTimeout(this.animateCar, this.carUpdateTick);
    },

    centerMapByCarPos(pos) {
      const center = this.map.getCenter();
      const radius = this.getMapViewportSize(this.map) / 3;
      if (computeDistanceBetween(center, pos) > radius) {
        this.map.setCenter(pos);
      }
    },

    getMapViewportSize(map) {
      let size = 100;
      if (map) {
        const bounds = map.getBounds();
        if (bounds) {
          const p0 = new LatLng(bounds.getNorthEast().lat(), bounds.getNorthEast().lng());
          const p1 = new LatLng(bounds.getSouthWest().lat(), bounds.getSouthWest().lng());
          const p2 = new LatLng(bounds.getNorthEast().lat(), bounds.getSouthWest().lng());
          const d0 = computeDistanceBetween(p0, p2);
          const d1 = computeDistanceBetween(p1, p2);
          size = Math.min(d0, d1);
        }
      }
      return size;
    }
  }
};
</script>
<style lang="scss" scoped>
.my-map {
  width: 101.5%;
  height: calc(100vh - 48px);
  margin-top: -12px;
  margin-left: -12px;
  margin-right: 0px;
}
</style>

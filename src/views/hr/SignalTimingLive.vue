<template>
  <div>
    <SignalTimingTitleBar title="Signal Timing" :loading="loading" :refresh="refreshData" />
    <GmapMap ref="mapRef" :options="options" :center="position" :zoom="11" map-type-id="roadmap" class="my-map">
      <SignalTiming ref="signalRef" :spat="spat" v-show="activeMarker != null" />
    </GmapMap>
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import MapStyle from '@/utils/MapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import SignalTiming from '@/components/modules/hr/SignalTiming';
import SignalTimingTitleBar from '@/components/modules/hr/SignalTimingTitleBar';
import { hrWebsocketUrl } from '@/utils/websocketUrl';

export default {
  components: {
    SignalTimingTitleBar,
    SignalTiming
  },
  data: () => ({
    loading: false,
    map: null,
    timeControl: null,
    timeId: null,
    homeIcon: require('@/assets/home-24.png'),
    spat: {},
    options: {
      zoomControl: true,
      zoomControlOptions: {
        position: 8
      },
      streetViewControl: false,
      fullscreenControl: true,

      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite']
      }
    }
  }),

  computed: {
    position() {
      return this.$store.state.position;
    },

    ...mapState(['currentDate']),
    ...mapState('hr', ['activeMarker', 'realTimeSignal'])
  },

  watch: {
    realTimeSignal(value) {
      // Send message to left side panel
      this.$bus.$emit('LIVE_UPDATE_SIGNALS', value);

      // Send message to this view
      if (this.activeMarker) {
        const permit = this.activeMarker.permit;
        if (permit in value) {
          this.spat = value[permit];
        }
      }
    }
  },
  mounted() {
    setTimeout(() => {
      this.startWebSocket();
    }, 500);

    this.$bus.$on('GET_LIVE_SIGNALS', ({ marker }) => {
      this.loadAndUpdate(marker);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addHomeControl(map);
      this.addTimeControl(map);
      this.loadAndUpdate(this.activeMarker);

      this.timeId = setInterval(() => {
        let time = new Date();
        this.updateTimeControl(time);
      }, 1000);

      google.maps.event.addListener(map, 'projection_changed', () => {
        let proj = map.getProjection();
        this.$store.commit('hr/SET_MAP_PROJECTION', proj);
        console.log('proj', proj);
      });
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.loadPage(this.$vuetify.theme.dark);
  },

  beforeDestroy() {
    this.stopWebSocket();
    if (this.timeId != null) {
      clearInterval(this.timeId);
    }
    this.$bus.$emit('LIVE_UPDATE_SIGNALS', {});
  },

  methods: {
    startWebSocket() {
      this.$store.dispatch('WSConnect', hrWebsocketUrl);
    },

    stopWebSocket() {
      this.$store.dispatch('WSDisconnect');
    },

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
        this.loadAndUpdate(marker);
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
          width: '86px',
          height: '40px',
          margin: '10px 20px',
          padding: '12px 8px',
          border: 'solid 1px #717B87',
          color: '#00f',
          background: '#fff',
          fontSize: '16px',
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

    updateTimeControl(currentTime) {
      let timeStr = Utils.formatTime(currentTime);
      if (this.timeControl !== null) {
        this.timeControl.innerHTML = `<div style="text-align: center;vertical-align: middle;">${timeStr}</div>`;
      }
    },

    loadAndUpdate(marker) {
      if (marker != null) {
        const currentTime = new Date();
        this.updateTimeControl(currentTime);
        this.markerZoom(marker.position, 20);
      }
    }
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
</style>

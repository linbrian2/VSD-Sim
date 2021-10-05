<template>
  <div>
    <LoadingProgress :loading="loading" color="deep-purple accent-4" />
    <GmapMap ref="mapRef" :options="options" :center="position" :zoom="11" map-type-id="roadmap" class="my-map">
      <GmapMarker
        v-for="m in markers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :label="m.label"
        :clickable="true"
        :icon="getMarker(m.id)"
        @click="markerClicked(m)"
      />
      <!-- <SignalDisplay ref="signalRef" /> -->
    </GmapMap>
    <!-- <PcdChartDialog ref="pcdRef" v-model="showPCDChart" /> -->
  </div>
</template>

<script>
/* global google */
import { mapActions } from 'vuex';
import MapStyles from '@/utils/MapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import LoadingProgress from '@/components/hr/LoadingProgress';

export default {
  components: {
    LoadingProgress,
  },
  props: {
    mapType: {
      type: Boolean,
      default: true
    }
  },
  data: () => ({
    mapMarker: {
      url: require('@/assets/green-icon-48.png'),
      size: { width: 30, height: 48, f: 'px', b: 'px' }
    },
    mapMarkerActive: {
      url: require('@/assets/orange-icon-48.png'),
      size: { width: 30, height: 48, f: 'px', b: 'px' }
    },
    homeIcon: require('@/assets/home-24.png'),
    markerOptions: {
      url: '',
      size: { width: 60, height: 90, f: 'px', b: 'px' },
      scaledSize: { width: 30, height: 45, f: 'px', b: 'px' }
    },
    showPanel: false,
    map: null,
    selectedKey: null,
    loading: false,
    options: {
      mapTypeControl: true,
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite']
      },
      styles: MapStyles
    }
  }),
  computed: {
    markers() {
      return this.$store.state.hr.locations;
    },
    position() {
      return this.$store.state.hr.position;
    },
    showPCDChart: {
      get() {
        return this.$store.state.showPCD;
      },
      set(show) {
        this.$store.commit('SHOW_PCD', show);
      }
    }
  },
  watch: {
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(11);
      });
    },
    markers(markers) {
      this.$refs.mapRef.$mapPromise.then(map => {
        this.centerMap(map, markers);
      });
    }
  },
  async mounted() {
    this.$bus.$on('NAME_SELECTED', name => {
      let marker = this.markers.find(m => m.name === name);
      if (marker != null) {
        this.markerClicked(marker);
      }
    });

    this.$bus.$on('SHOW_PCD', ({ id, time, phase }) => {
      this.$refs.pcdRef.reset(id, time, phase);
      this.$store.commit('SHOW_PCD', true);
    });

    this.$bus.$on('SHOW_SIGNALS', payload => {
      if (payload != null) {
        let duration = 600 * 1000;
        let start = new Date().getTime() - duration;
        this.loadTimingData(payload.id, start, duration);
        this.markerZoom(payload.position, payload.zoomLevel);
      }
    });

    this.$bus.$on('CENTER_MAP', () => {
      this.centerMap(this.map, this.markers);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
    });

    navigator.geolocation.getCurrentPosition(position => {
      this.$store.commit('hr/POSITION_SET', {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      });
    });
    this.options.mapTypeControl = this.mapType;

    this.addControls();
    await this.fetchLocations();
    let selected = this.$store.state.hr.activeMarker;
    if (selected !== null) {
      this.selectedKey = selected.id;
    }
  },
  methods: {
    getMap() {
      return this.$refs.mapRef;
    },
    addControls() {
      this.$refs.mapRef.$mapPromise.then(map => {
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
              this.centerMap(map, this.markers);
            }
          }
        };
        MapUtils.addControl(map, options);
      });
    },
    centerMap(map, markers) {
      if (markers.length > 2) {
        const bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length - 1; i++) {
          bounds.extend(markers[i].position);
        }
        map.fitBounds(bounds);
      }
    },
    getMarker(key) {
      return this.selectedKey === key ? this.mapMarkerActive : this.mapMarker;
    },
    markerZoom(position, zoomLevel) {
      this.map.panTo(position);
      this.map.setZoom(zoomLevel);
    },
    async loadTimingData(id, start, duration) {
      await this.fetchTiming({ id, start, duration });
      let timings = this.$store.state.hr.timing || { signals: [], detectors: [] };
      this.$refs.signalRef.applySignalsTiming(timings.signals);
      this.$refs.signalRef.applyDetectorsTiming(timings.detectors);
    },
    markerClicked(marker) {
      this.selectedKey = marker.id;
      this.$store.commit('hr/SET_ACTIVE_MARKER', marker);
      this.$emit('click', marker);
    },
    ...mapActions('hr', ['fetchLocations', 'fetchTiming'])
  }
};
</script>
<style>
.my-map {
  width: 100%;
  height: 100vh;
  /* height: calc(100vh - 50px); */
}
.gm-style div {
  -webkit-transform: scale(1.002);
}
</style>

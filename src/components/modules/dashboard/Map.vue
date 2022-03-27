<template>
  <div id="basic-map">
    <div v-if="!reload">
      <GmapMap
        ref="mapRef"
        :options="options"
        :center="center"
        :zoom="zoom"
        map-type-id="roadmap"
        class="map"
        :style="style"
      >
        <GmapMarker
          v-for="m in markers"
          :key="m.id"
          :position="m.position"
          :title="m.name"
          :clickable="true"
          :icon="getMarkerIcon(m.id)"
          @click="markerClicked(m)"
        />
        <GmapPolyline
          v-for="s in segments"
          :key="s.id"
          :title="s.desc"
          :path.sync="s.path"
          :options="segmentOptions(s)"
          @click="segmentClicked(s)"
        />
        <GmapCustomMarker
          alignment="center"
          v-for="s in segments"
          :key="`L-${s.id}`"
          :offsetX="0"
          :offsetY="-50"
          :marker="midPoint(s)"
        >
          <div v-if="s.id == selectedSegmentId">
            <v-chip small :color="getChipColor(s)" @click="segmentClicked(s)">{{ s.short }}</v-chip>
          </div>
        </GmapCustomMarker>
      </GmapMap>
      <WeatherOverlay :center="center"></WeatherOverlay>
    </div>
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import WeatherOverlay from '@/components/modules/dashboard/WeatherOverlay.vue';
import DarkMapStyle from '@/utils/DarkMapStyle.js';

export default {
  components: { WeatherOverlay, GmapCustomMarker },
  props: {
    apiInfo: Object,
    zoom: {
      type: Number,
      default: 12
    },
    height: {
      type: String,
      default: 'calc(100vh - 96px)'
    },
    disableDefaultUI: {
      type: Boolean,
      default: false
    },
    markers: {
      type: Array,
      default() {
        return [];
      }
    },
    segments: {
      type: Array,
      default() {
        return [];
      }
    },
    selectedIdx: Number,
    icons: {
      type: Array,
      default() {
        return [
          { path: 0, scale: 10.0, fillColor: '#05FF00', fillOpacity: 0.8, strokeWeight: 0.4 },
          { path: 0, scale: 10.0, fillColor: '#FF7F00', fillOpacity: 0.8, strokeWeight: 0.4 }
        ];
      }
    }
  },
  data() {
    return {
      reload: false,
      map: null,
      center: { lat: 39.14, lng: -75.5 },
      style: { marginTop: '-1px', width: '100%', height: this.height },
      options: {
        scrollwheel: true,
        disableDefaultUI: this.disableDefaultUI,
        mapTypeId: 'roadmap',
        streetViewControl: false,
        styles: DarkMapStyle,
        mapTypeControlOptions: null
      },
      selectedSegmentId: '',
      defaultSegmentOptions: {
        strokeColor: 'green',
        strokeOpacity: 1.0,
        strokeWeight: 8,
        zIndex: 100
      },

      redIcon: {
        path: 0,
        scale: 5.0,
        fillColor: 'white',
        fillOpacity: 0.6,
        strokeWeight: 1.0,
        strokeColor: 'white',
        strokeOpacity: 1.0
      },

      redDot: {
        path: 0,
        scale: 9.0,
        fillColor: '#057CFF',
        fillOpacity: 1.0,
        strokeWeight: 2.0,
        strokeColor: 'white',
        strokeOpacity: 1.0
      }
    };
  },
  mounted() {
    setTimeout(() => {
      this.loadPage(this.$vuetify.theme.dark);
    }, 1);

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.options.mapTypeControlOptions = {
        style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
        position: google.maps.ControlPosition.BOTTOM_CENTER,
        mapTypeIds: ['roadmap', 'hybrid']
      };
      this.centerMap(this.map, this.markers);
      this.detectMapCenterChange();
      this.$emit('map-ready', map);
    });

    this.$bus.$on('SELECT_FIRST', () => {
      this.triggerFirstSegmentClick();
    });

    this.$bus.$on('SELECT_SEGMENT_BY_NAME', name => {
      this.selectSegmentByName(name);
    });
  },
  methods: {
    getMarkerIcon(key) {
      return this.selectedMarkerId == key ? this.icons[1] : this.icons[0];
    },
    markerClicked(marker) {
      console.log(marker.id);
      this.selectedMarkerId = marker.id;
      this.$store.commit('dashboard/SET_ACTIVE_MARKER', marker);
      this.$emit('click', marker);
    },
    detectMapCenterChange() {
      google.maps.event.addListener(this.map, 'dragend', () => {
        let center = this.map.getCenter();
        this.center = { lat: center.lat(), lng: center.lng() };
      });
    },
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

    centerMap(map, markers) {
      if (markers && markers.length > 0) {
        console.log(0);
        let bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length; i++) {
          bounds.extend(markers[i].position);
        }
        map.setCenter(bounds.getCenter());
        if (markers.length == 0) {
          map.setCenter({ lat: 33.907, lng: -117.7 });
          map.setZoom(10);
        } else if (markers.length == 1) {
          map.setZoom(10);
        } else {
          map.fitBounds(bounds);
        }
      }
    },

    midPoint(segment) {
      return segment.path[Math.round(segment.path.length / 2)];
    },

    segmentOptions(segment) {
      const color = segment.id === this.selectedSegmentId ? 'blue' : Utils.getStrokeColor(segment.travelTime.level);
      return { ...this.defaultSegmentOptions, strokeColor: color };
    },

    mapMounted(map) {
      this.map = map;
      this.centerMapHandler(map);
    },

    centerMapHandler(map) {
      if (map && this.segments) {
        this.centerMapSegments(map, this.segments);
      }
    },

    zoomSelectHandler(map) {
      if (this.selectedSegmentId) {
        this.segments.forEach(s => {
          if (s.id === this.selectedSegmentId) {
            if (s) {
              map.panTo(this.midPoint(s));
              map.setZoom(12);
            }
          }
        });
      }
    },

    triggerFirstSegmentClick() {
      if (this.segments && this.segments.length > 0) {
        const selectedSegment = this.segments[0];
        this.segmentClicked(selectedSegment);
      }
    },

    segmentClicked(s) {
      this.selectedSegmentId = s.id;
      this.$emit('clicked', s);
    },

    getChipColor(s) {
      return this.selectedSegmentId === s.id ? 'blue' : 'white';
    },

    setCenterMap() {
      if (this.map && this.segments) {
        this.centerMapSegments(this.map, this.segments);
      }
    },

    centerMapSegments(map, segments) {
      if (segments.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        segments.forEach(segment => {
          segment.path.forEach(point => {
            bounds.extend(point);
          });
        });
        map.fitBounds(bounds, 0);
      }
    },

    selectSegmentByName(name) {
      const segment = this.segments.find(m => m.name === name);
      if (segment) {
        this.segmentClicked(segment);
      }
    }
  },
  watch: {
    segments() {
      if (this.segments && this.segments.length > 0) {
        this.centerMapSegments(this.map, this.segments);
      }
    },
    markers() {
      if (this.markers && this.markers.length > 0) {
        this.centerMap(this.map, this.markers);
      }
    },
    selectedMarkerId(selectedMarkerId) {
      console.log(`selectedMarkerId ${selectedMarkerId}`);
    }
  }
};
</script>

<style lang="scss" scoped></style>

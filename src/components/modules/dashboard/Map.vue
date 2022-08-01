<template>
  <div id="wrapper">
    <div id="google_map">
      <div v-show="selectedIdx == 0">
        <MapSegment
          ref="mapSegmentRef"
          :incidentSegmentLinks="incidentSegmentLinks"
          :incidentMarkers="incidentMarkers"
          @select="onSegmentSelected"
          @click="onMarkerClicked"
        />
      </div>
      <!-- <div> -->
      <div v-show="selectedIdx != 0">
        <GmapMap
          ref="mapRef"
          :options="options"
          :center="center"
          :zoom="zoom"
          map-type-id="roadmap"
          class="map"
          :style="style"
        >
          {{ selectedMarkers.selectedMarkers[selectedMarkers.selectedIdx] }}
          <GmapMarker
            v-for="m in markers"
            :key="m.id"
            :position="m.position"
            :title="m.name"
            :clickable="true"
            :icon="getMarkerIcon(m)"
            :options="markerOptions(m.id)"
          />
          <GmapPolyline
            v-for="s in segments"
            :key="s.id"
            :title="s.desc"
            :path.sync="s.path"
            :options="segmentOptions(s)"
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
      </div>
    </div>
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import MapSegment from '@/components/modules/dashboard/MapSegment';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import { mapState } from 'vuex';
import * as d3 from 'd3';

export default {
  components: { GmapCustomMarker, MapSegment },
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
      selectedColor: 'blue',
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
  computed: {
    currentPolyline() {
      if (this.segments) {
        let segmentList = this.segments.filter(x => x.id == this.selectedSegmentId);
        return segmentList[0];
      } else {
        return null;
      }
    },
    selectedMarkers() {
      return {
        selectedIdx: this.selectedIdx,
        selectedMarkers: [
          this.selectedTrafficIncident,
          this.selectedtrafficDevice,
          this.selectedSignalPerformanceIssue,
          this.selectedDetector,
          this.selectedSegment,
          this.selectedWazeAlert
        ]
      };
    },
    ...mapState('dashboard', [
      'incidentSegmentLinks',
      'incidentMarkers',
      'selectedTrafficIncident',
      'selectedtrafficDevice',
      'selectedSignalPerformanceIssue',
      'selectedDetector',
      'selectedSegment',
      'selectedWazeAlert'
    ])
  },
  mounted() {
    this.addSelectedMarker();
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
      if (this.markers && this.markers.length > 0) {
        this.centerMap(this.map, this.markers);
      }
      this.detectMapCenterChange();
      this.$emit('map-ready', map);
    });

    this.$bus.$on('SELECT_FIRST', () => {
      this.triggerFirstSegmentClick();
    });

    this.$bus.$on('SELECT_SEGMENT_BY_NAME', name => {
      this.selectSegmentByName(name);
    });

    this.changeSelectedColor();
  },
  methods: {
    changeSelectedColor() {
      setInterval(() => {
        let timeVal = parseFloat((new Date().getTime() % 2000) / 1000).toFixed(2);
        if (timeVal > 1) {
          timeVal = 2 - timeVal;
        }
        this.selectedColor = d3.interpolateLab('red', 'blue')(timeVal);
      }, 50);
    },
    onSegmentSelected(segmentId) {
      console.log(segmentId);
    },
    onMarkerClicked(marker) {
      console.log(marker);
    },
    getMarkerIcon(key) {
      let id = key.id;
      if (this.icons.length == 12) {
        // Waze Selected
        let typeId = key.subType;
        return this.selectedMarkerId == id ? this.getWazeIcon(typeId, 1) : this.getWazeIcon(typeId, 0);
      } else {
        return this.selectedMarkerId == id ? this.icons[1] : this.icons[0];
      }
    },
    getWazeIcon(key, active) {
      let id = key.id;
      if (id >= 10 && id <= 13)
        // Accident
        return active ? this.icons[3] : this.icons[2];
      else if (id >= 20 && id <= 25)
        // Jam
        return active ? this.icons[11] : this.icons[10];
      else if (id >= 90 && id <= 91)
        // Construction
        return active ? this.icons[5] : this.icons[4];
      else if (id >= 100 && id <= 104)
        // Closure
        return active ? this.icons[9] : this.icons[8];
      // Hazard
      else return active ? this.icons[7] : this.icons[6];
    },
    markerOptions(key) {
      const zIndex = this.selectedMarkerId == key ? 2 : 1;
      return { zIndex };
    },
    markerClicked(marker) {
      this.selectedMarkerId = marker.id;
      this.$store.commit('dashboard/SET_ACTIVE_MARKER', marker);
      this.$emit('click', marker);
    },
    detectMapCenterChange() {
      google.maps.event.addListener(this.map, 'dragend', () => {
        let center = this.map.getCenter();
        this.center = { lat: center.lat(), lng: center.lng() };
        this.$store.commit('SET_MAP_CENTER', this.center);
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

    centerMap(map, markers, zoom = null) {
      if (markers && markers.length > 0) {
        let bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length; i++) {
          bounds.extend(markers[i].position);
        }
        map.setCenter(bounds.getCenter());
        if (markers.length == 0) {
          map.setCenter({ lat: 33.907, lng: -117.7 });
          map.setZoom(zoom ? zoom : 10);
        } else if (markers.length == 1) {
          map.setZoom(zoom ? zoom : 10);
        } else {
          map.fitBounds(bounds);
        }
        let center = this.map.getCenter();
        this.center = { lat: center.lat(), lng: center.lng() };
        this.$store.commit('SET_MAP_CENTER', this.center);
      }
    },

    midPoint(segment) {
      return segment.path[Math.round(segment.path.length / 2)];
    },

    segmentOptions(segment) {
      const color =
        segment.id === this.selectedSegmentId ? this.selectedColor : Utils.getStrokeColor(segment.travelTime.level);
      const zIndex = segment.id === this.selectedSegmentId ? 2 : 1;
      return { ...this.defaultSegmentOptions, strokeColor: color, zIndex };
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

    centerMapSegments(map, segments, zoom = null) {
      if (segments.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        segments.forEach(segment => {
          segment.path.forEach(point => {
            bounds.extend(point);
          });
        });
        map.fitBounds(bounds, 0);
        if (zoom) {
          map.setZoom(zoom);
        }
        let center = this.map.getCenter();
        this.center = { lat: center.lat(), lng: center.lng() };
        this.$store.commit('SET_MAP_CENTER', this.center);
      }
    },

    selectSegmentByName(name) {
      const segment = this.segments.find(m => m.name === name);
      if (segment) {
        this.segmentClicked(segment);
      }
    },
    // this.centerMapSegments(this.map, this.segments);
    // this.centerMap(this.map, this.markers);
    addSelectedMarker() {
      switch (this.selectedIdx) {
        case 0:
          break;
        case 1:
          this.markerClicked(this.selectedtrafficDevice);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.markers.filter(x => x.id == this.selectedMarkerId),
              12
            );
          }, 1);
          break;
        case 2:
          this.markerClicked(this.selectedSignalPerformanceIssue);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.markers.filter(x => x.id == this.selectedMarkerId),
              15
            );
          }, 1);
          break;
        case 3:
          this.markerClicked(this.selectedDetector);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.markers.filter(x => x.id == this.selectedMarkerId),
              12
            );
          }, 1);
          break;
        case 4:
          this.segmentClicked(this.selectedSegment);
          setTimeout(() => {
            this.centerMapSegments(
              this.map,
              this.segments.filter(x => x.id == this.selectedSegmentId),
              13
            );
          }, 1);
          break;
        case 5:
          this.markerClicked(this.selectedWazeAlert);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.markers.filter(x => x.id == this.selectedMarkerId),
              12
            );
          }, 1);
          break;
      }
    }
  },
  watch: {
    selectedMarkers: {
      handler: function() {
        this.addSelectedMarker();
      },
      deep: true
    },
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

<style lang="scss" scoped>
#wrapper {
  position: relative;
}
#over_map {
  position: absolute;
  top: 10px;
  left: 10px;
}
</style>

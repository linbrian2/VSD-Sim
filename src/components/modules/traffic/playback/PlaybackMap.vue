<template>
  <div id="wrapper">
    <div id="google_map">
      <div>
        <GmapMap
          ref="mapRef"
          :options="options"
          :center="position"
          :zoom="zoom"
          map-type-id="roadmap"
          class="my-map"
          :style="
            `margin-top:-1px; width: 100%; height:${
              $vuetify.breakpoint.mobile && showPanel ? 'calc(50vh - 48px)' : 'calc(100vh - 48px)'
            }`
          "
        >
          <!-- Congested Routes -->
          <div v-if="isMapLayerVisible(0) && segments">
            <GmapPolyline
              v-for="s in segments"
              :key="s.id"
              :title="s.desc"
              :path.sync="s.path"
              :options="segmentOptions(s)"
              @click="segmentClicked(0, s.id)"
            />
            <!-- <GmapCustomMarker
              alignment="center"
              v-for="s in segments"
              :key="`L-${s.id}`"
              :offsetX="0"
              :offsetY="-50"
              :marker="midPoint(s)"
            >
              <div v-if="s.id == selectedMarkerId">
                <v-chip small :color="getChipColor(s)">{{ s.short }}</v-chip>
              </div>
            </GmapCustomMarker> -->
          </div>

          <!-- Waze -->
          <div v-if="isMapLayerVisible(1) && !isMapLayerVisible(2) && waze">
            <!-- Hazard -->
            <div v-if="isWazeLayerVisible(0)">
              <GmapMarker
                v-for="m in waze.filter(x => x.markerId == 0)"
                :key="m.id"
                :position="m.position"
                :title="m.description"
                :icon="getWazeIcon(m, m.id == selectedMarkerId)"
                :options="markerOptions(m.id, 20)"
                @click="handleMarkerClick(1, m.id)"
              />
            </div>
            <!-- Traffic Jam -->
            <div v-if="isWazeLayerVisible(1)">
              <GmapMarker
                v-for="m in waze.filter(x => x.markerId == 2)"
                :key="m.id"
                :position="m.position"
                :title="m.description"
                :icon="getWazeIcon(m, m.id == selectedMarkerId)"
                :options="markerOptions(m.id, 21)"
                @click="handleMarkerClick(1, m.id)"
              />
            </div>
            <!-- Road Closed -->
            <div v-if="isWazeLayerVisible(2)">
              <GmapMarker
                v-for="m in waze.filter(x => x.markerId == 4)"
                :key="m.id"
                :position="m.position"
                :title="m.description"
                :icon="getWazeIcon(m, m.id == selectedMarkerId)"
                :options="markerOptions(m.id, 22)"
                @click="handleMarkerClick(1, m.id)"
              />
            </div>
            <!-- Accident -->
            <div v-if="isWazeLayerVisible(3)">
              <GmapMarker
                v-for="m in waze.filter(x => x.markerId == 1)"
                :key="m.id"
                :position="m.position"
                :title="m.description"
                :icon="getWazeIcon(m, m.id == selectedMarkerId)"
                :options="markerOptions(m.id, 23)"
                @click="handleMarkerClick(1, m.id)"
              />
            </div>
          </div>

          <!-- Grouped Waze -->
          <div v-if="isMapLayerVisible(1) && isMapLayerVisible(2) && waze">
            <GmapCluster>
              <GmapMarker
                v-for="m in waze.filter(
                  x =>
                    (isWazeLayerVisible(0) && x.markerId == 0) ||
                    (isWazeLayerVisible(1) && x.markerId == 2) ||
                    (isWazeLayerVisible(2) && x.markerId == 4) ||
                    (isWazeLayerVisible(3) && x.markerId == 1)
                )"
                :key="m.id"
                :position="m.position"
                :title="m.description"
                :icon="getWazeIcon(m, m.id == selectedMarkerId)"
                :options="markerOptions(m.id, 100)"
                @click="handleMarkerClick(1, m.id)"
              />
            </GmapCluster>
          </div>

          <!-- Devices -->
          <div v-if="isMapLayerVisible(3) && btDevices">
            <div v-if="isDeviceLayerVisible(0)">
              <GmapMarker
                v-for="m in btDevices.filter(x => x.level < 2)"
                :key="m.id"
                :position="m.position"
                :title="m.description"
                :icon="getDeviceIcon(m.level, m.id == selectedMarkerId)"
                :options="markerOptions(m.id, 10)"
                @click="handleMarkerClick(3, m.id)"
              />
            </div>
            <div v-if="isDeviceLayerVisible(1)">
              <GmapMarker
                v-for="m in btDevices.filter(x => x.level >= 2 && x.level < 4)"
                :key="m.id"
                :position="m.position"
                :title="m.description"
                :icon="getDeviceIcon(m.level, m.id == selectedMarkerId)"
                :options="markerOptions(m.id, 11)"
                @click="handleMarkerClick(3, m.id)"
              />
            </div>
            <div v-if="isDeviceLayerVisible(2)">
              <GmapMarker
                v-for="m in btDevices.filter(x => x.level >= 4)"
                :key="m.id"
                :position="m.position"
                :title="m.description"
                :icon="getDeviceIcon(m.level, m.id == selectedMarkerId)"
                :options="markerOptions(m.id, 12)"
                @click="handleMarkerClick(3, m.id)"
              />
            </div>
          </div>

          <!-- Sensors -->
          <div v-if="isMapLayerVisible(4) && btSensors">
            <GmapMarker
              v-for="m in btSensors"
              :key="m.id"
              :position="m.position"
              :icon="sensorIcon"
              :options="markerOptions(m.id, 0)"
            />
          </div>

          <div v-if="isMapLayerVisible(6) && trafficIncidents">
            <GmapMarker
              v-for="m in trafficIncidents"
              :key="m.id"
              :position="m.location"
              :icon="getTrafficIncidentMarker(m)"
              :options="markerOptions(m.id, 99)"
              @click="handleMarkerClick(6, m.id)"
            />
          </div>
        </GmapMap>
      </div>
    </div>

    <!-- <div class="debug">
      <v-card class="pa-4" dense floating :style="`position: absolute; top: 80px; left: 280px; max-width: 480px;`">
        isMapLayerVisible(6): {{ isMapLayerVisible(6) }}<br />
        Map Debug:<br />
        selectedMarkerId: {{ selectedMarkerId }}
      </v-card>
    </div> -->
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import MapUtils from '@/utils/MapUtils.js';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import { mapState } from 'vuex';
import { mapIcons } from '@/mixins/mapIcons';
import { weatherCode } from '@/mixins/weatherCode';
import Constants from '@/utils/constants/bluetooth';
import GmapCluster from 'vue2-google-maps/src/components/cluster';
// import GmapCustomMarker from 'vue2-gmap-custom-marker';
import * as d3 from 'd3';

export default {
  mixins: [mapIcons, weatherCode],
  components: { GmapCluster /* , GmapCustomMarker */ },
  props: {
    mapLayers: Array,
    segments: Array,
    waze: Array,
    btDevices: Array,
    trafficIncidents: Array,
    startTime: Number,
    endTime: Number
  },
  data() {
    return {
      colorInterp: null,
      selectedColor: 'blue',
      sensorIcon: {
        path: 0,
        scale: 4.0,
        fillColor: '#0000FF',
        strokeColor: '#FFFFFF',
        fillOpacity: 0.8,
        strokeWeight: 1.2,
        strokeOpacity: 0.8
      },
      position: { lat: 39.1, lng: -75.6 },
      zoom: 9,
      selectedMarkerId: null,
      map: null,
      options: {
        zoomControl: true,
        zoomControlOptions: {
          position: 8
        },
        streetViewControl: false,
        fullscreenControl: true,
        mapTypeControl: false,

        mapTypeControlOptions: {
          mapTypeIds: ['roadmap', 'hybrid'],
          position: 2
        },
        styles: DarkMapStyle,
        gestureHandling: 'greedy'
      },
      defaultSegmentOptions: {
        strokeColor: 'green',
        strokeOpacity: 1.0,
        strokeWeight: 8,
        zIndex: 100
      },
      mapLayersSelection: [0, 1, 3]
    };
  },
  computed: {
    showPanel: {
      get() {
        return this.$store.state.traffic.showPanel;
      },
      set(show) {
        this.$store.commit('traffic/SHOW_PANEL', show);
      }
    },
    btLevels() {
      if (this.btDevices) {
        return this.btDevices.map(x => x.level);
      } else {
        return null;
      }
    },
    showLabel() {
      return this.mapLayerSelection.includes(Constants.LAYER_LABELS);
    },
    // position() {
    //   return this.$store.state.position;
    // },

    ...mapState(['currentDate']),
    ...mapState('bluetooth', ['mapLayerSelection', 'wazeLayerSelection', 'deviceLayerSelection']),
    ...mapState('traffic', ['btSensors'])
  },
  mounted() {
    this.$bus.$on('UPDATE_DARK_MODE', () => {
      this.loadPage(this.$vuetify.theme.dark);
    });
    this.$bus.$on('CENTER_SEGMENT', segment => {
      this.centerSegment(segment);
    });
    this.loadPage(this.$vuetify.theme.dark);

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addMapControls(map);
    });

    if (!this.colorInterp) {
      this.changeSelectedColor();
    }
  },
  beforeDestroy() {
    if (this.colorInterp) {
      clearInterval(this.colorInterp);
    }
  },
  methods: {
    getTrafficIncidentMarker(marker) {
      if (marker.id == this.selectedMarkerId) {
        if (this.trafficIncidents && this.startTime && this.endTime) {
          if (marker.startTime <= this.endTime && this.endTime <= marker.endTime) {
            return this.alertAnimatedIconActive;
          } else {
            return this.alertIconActive;
          }
        }
      } else {
        if (marker.startTime <= this.endTime && this.endTime <= marker.endTime) {
          return this.alertAnimatedIcon;
        } else {
          return this.alertIcon;
        }
      }
    },
    changeSelectedColor() {
      this.colorInterp = setInterval(() => {
        let timeVal = parseFloat((new Date().getTime() % 2000) / 1000).toFixed(2);
        if (timeVal > 1) {
          timeVal = 2 - timeVal;
        }
        this.selectedColor = d3.interpolateLab('red', 'blue')(timeVal);
      }, 50);
    },
    handleMarkerClick(type, id) {
      this.selectedMarkerId = id;
      this.$bus.$emit('DISPLAY_DETAILS', { id, type });
    },
    segmentOptions(segment) {
      const color = segment.id === this.selectedMarkerId ? this.selectedColor : Utils.getStrokeColor(segment.level);
      const zIndex = segment.id === this.selectedMarkerId ? 2 : 1;
      return { ...this.defaultSegmentOptions, strokeColor: color, zIndex };
    },
    getChipColor(s) {
      return this.selectedMarkerId === s.id ? 'blue' : 'white';
    },
    segmentClicked(type, id) {
      this.selectedMarkerId = id;
      this.$bus.$emit('DISPLAY_DETAILS', { id, type });
    },
    centerSegment(segment) {
      this.centerAndZoom(this.midPoint(segment), 14);
    },

    centerAndZoom(position, zoomLevel) {
      if (this.map) {
        this.map.panTo(position);
        this.map.setZoom(zoomLevel);
      }
    },
    onSegmentSelected(segmentId) {
      console.log(segmentId);
    },
    onMarkerClicked(marker) {
      console.log(marker);
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
    midPoint(s) {
      return s && s.path ? s.path[Math.round((s.path.length * 3) / 7)] : null;
    },
    getSegmentStrokeWeight() {
      return this.map ? this.map.getZoom() / 1.5 : 10;
    },
    getSegmentChipColor(segment) {
      return segment.status == 7 ? 'red' : 'white';
    },
    formatDisplay(seconds) {
      if (!seconds) {
        return '';
      }
      let minutes = seconds / 60;
      return `${minutes.toFixed(1)} min`;
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
    markerOptions(key, level = 1) {
      const zIndex = this.selectedMarkerId == key ? 9999 : level;
      return { optimized: false, zIndex };
    },
    addMapControls(map) {
      this.addHomeControl(map);
      // this.addMessageControl(map);
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
            this.centerMap(map, this.markers);
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    addMessageControl(map) {
      let options = {
        position: 'right',
        content: `<div style="margin:-5px 4px;"><img src="${this.msgIcon}"/></div>`,
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
            this.showBottomSheet();
          }
        }
      };
      MapUtils.addControl(map, options);
    },
    isMapLayerVisible(id) {
      const entry = this.mapLayerSelection.find(m => m === id);
      return entry !== undefined;
    },
    isWazeLayerVisible(id) {
      const entry = this.wazeLayerSelection.find(m => m === id);
      return entry !== undefined;
    },
    isDeviceLayerVisible(id) {
      const entry = this.deviceLayerSelection.find(m => m === id);
      return entry !== undefined;
    },
    loadPage(darkMode) {
      if (this.$refs.mapRef == null) {
        return;
      }
      if (darkMode && this.$refs.mapRef) {
        let style = DarkMapStyle;
        if (style.length > 16) style.splice(16);

        if (!this.showLabel) Constants.MAP_DISABLED_LABELS.forEach(x => style.push(x));
        else Constants.MAP_ENABLED_LABELS.forEach(x => style.push(x));
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: style });
        });
      } else {
        let style = [];
        if (style.length > 16) style.splice(16);

        if (!this.showLabel) Constants.MAP_DISABLED_LABELS.forEach(x => style.push(x));
        else Constants.MAP_ENABLED_LABELS.forEach(x => style.push(x));
        if (style.length > 16) style.splice(16);
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: style });
        });
      }
    },
    getMap() {
      return this.$refs.mapRef;
    }
  },
  watch: {
    showLabel() {
      this.loadPage(this.$vuetify.theme.dark);
    },
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(13);
      });
    },
    markers(markers) {
      this.$refs.mapRef.$mapPromise.then(map => {
        this.centerMap(map, markers);
      });
    }
  }
};
</script>

<style></style>

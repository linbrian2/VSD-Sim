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
          :center="position"
          :zoom="12"
          map-type-id="roadmap"
          class="my-map"
          style="margin-top:-1px; width: 100%; height:calc(100vh - 48px)"
        >
          <!-- Traffic Flow Detectors & Traffic Flow Issues -->
          <div v-if="isMapLayerVisible(0) || selectedIdx == 1">
            <GmapMarker
              v-for="m in markers"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getDefaultMarkerIcon(m, m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(0, m.id)"
            />

            <!-- Anomaly marker id -->
            <GmapCustomMarker
              alignment="bottomright"
              v-for="(m, index) in markers.filter(d => d.status > 0)"
              :key="index + 100"
              :offsetX="15"
              :offsetY="0"
              :marker="m.position"
            >
              <h3 style="color:white">{{ m.id }}</h3>
            </GmapCustomMarker>
          </div>

          <!-- Bluetooth Center locations -->
          <div v-if="isMapLayerVisible(1)">
            <GmapMarker
              v-for="s in segments"
              :key="`${s.id}-C`"
              :position="s.position"
              :title="s.name"
              :clickable="true"
              :icon="getSegmentMarkerIcon(s)"
              @click="handleMarkerClick(1, s.id)"
            />

            <!-- Travel Time Display -->
            <GmapCustomMarker
              alignment="bottomright"
              v-for="(s, index) in segments"
              :key="index + 200"
              :offsetX="12"
              :offsetY="7"
              :marker="s.position"
            >
              <div v-if="s.travelTime && s.status === 7">
                <v-chip small :color="getSegmentChipColor(s)" outlined>{{ formatDisplay(s.travelTime) }}</v-chip>
              </div>
            </GmapCustomMarker>

            <!-- Bluetooth Segments -->
            <!-- <GmapPolyline v-for="s in segments" :key="s.id" :path.sync="s.path" :options="getSegmentOptions(s)" /> -->
          </div>

          <!-- Weather Stations -->
          <div v-if="isMapLayerVisible(2)">
            <GmapMarker
              v-for="m in weatherMarkers"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getWeatherMarkerIcon(m)"
              @click="handleMarkerClick(2, m.id)"
            />

            <!-- Weather Station Temeperature -->
            <GmapCustomMarker
              alignment="topright"
              v-for="m in weatherMarkers"
              :key="`${m.id}-T`"
              :offsetX="10"
              :offsetY="-20"
              :marker="m.position"
            >
              <div v-if="m.temp > -100">
                <h3 style="color:white">{{ m.temp }}°F</h3>
              </div>
            </GmapCustomMarker>
          </div>

          <!-- Travel restrictions -->
          <div v-if="isMapLayerVisible(3)">
            <GmapMarker
              v-for="r in restrictions"
              :key="r.id"
              :position="r.position"
              :title="r.name"
              :clickable="true"
              :icon="restrictionIcon"
              @click="handleMarkerClick(3, r.id)"
            />
          </div>

          <!-- Anomaly Segments -->
          <div v-if="isMapLayerVisible(4)">
            <GmapPolyline
              v-for="(s, idx) in ongoingAnomalySegments"
              :key="`${s.id}-${idx}`"
              :path.sync="s.path"
              :options="anomalySegmentOptions"
              @click="handleMarkerClick(4, s.id)"
            />

            <GmapCustomMarker
              alignment="center"
              v-for="s in ongoingAnomalySegments"
              :key="`A-${s.id}`"
              :offsetX="0"
              :offsetY="0"
              :marker="midPoint(s)"
            >
              <div class="pulsate-effect"></div>
            </GmapCustomMarker>
          </div>

          <!-- Traffic Incidents -->
          <div v-if="selectedIdx == 0">
            <MapSegment
              ref="mapSegmentRef"
              :incidentSegmentLinks="incidentSegmentLinks"
              :incidentMarkers="incidentMarkers"
              @select="onSegmentSelected"
              @click="onMarkerClicked"
            />
          </div>

          <!-- Signal Issues -->
          <div v-if="isMapLayerVisible(5) || selectedIdx == 2">
            <GmapMarker
              v-for="m in signalPerformanceIssues"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getHRIcons(m, m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(7, m.id)"
            />
          </div>

          <!-- Device Anomalies -->
          <div v-if="isMapLayerVisible(6) || selectedIdx == 3">
            <GmapMarker
              v-for="m in deviceMarkers"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getMarker2Icon(m, m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(7, m.id)"
            />
          </div>

          <!-- Congested Routes -->
          <div v-if="isMapLayerVisible(7) || selectedIdx == 4">
            <GmapPolyline
              v-for="s in trafficSegments"
              :key="s.id"
              :title="s.desc"
              :path.sync="s.path"
              :options="segmentOptions(s)"
            />
            <GmapCustomMarker
              alignment="center"
              v-for="s in trafficSegments"
              :key="`L-${s.id}`"
              :offsetX="0"
              :offsetY="-50"
              :marker="midPoint(s)"
            >
              <div v-if="s.id == selectedSegmentId">
                <v-chip small :color="getChipColor(s)" @click="segmentClicked(s)">{{ s.short }}</v-chip>
              </div>
            </GmapCustomMarker>
          </div>

          <!-- Waze Alerts -->
          <div v-if="isMapLayerVisible(8) || selectedIdx == 5">
            <GmapMarker
              v-for="m in waze"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getWazeIcon(m, m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(7, m.id)"
            />
          </div>

          <!-- InfoWindow -->
          <!-- <InfoWindow :position="infoPosition" ref="infoWindow" /> -->

          <!-- Heatmap layer -->
          <!-- <GmapHeatMap :data="heatMapData" :options="{ maxIntensity: 15, dissipating: true, radius: 10 }" /> -->
        </GmapMap>
      </div>
    </div>
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import Devices from '@/utils/Devices.js';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import MapUtils from '@/utils/MapUtils.js';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import { mapState } from 'vuex';
import { mapIcons } from '@/mixins/mapIcons';
import { weatherCode } from '@/mixins/weatherCode';
import MapSegment from '@/components/modules/dashboard/MapSegment';
import * as d3 from 'd3';

export default {
  mixins: [mapIcons, weatherCode],
  props: {
    deviceLocations: Array,
    bluetoothLocations: Array,
    restrictions: Array,
    selectedIdx: Number
  },
  components: {
    GmapCustomMarker,
    MapSegment
  },
  data() {
    return {
      colorInterp: null,
      selectedColor: 'blue',
      selectedSegmentId: null,
      selectedMarkerId: null,
      map: null,
      options: {
        zoomControl: true,
        zoomControlOptions: {
          position: 8
        },
        streetViewControl: false,
        fullscreenControl: true,

        mapTypeControlOptions: {
          mapTypeIds: ['roadmap', 'hybrid'],
          position: 2
        },
        styles: DarkMapStyle
      },
      infoPosition: null,
      defaultSegmentOptions: {
        strokeColor: 'green',
        strokeOpacity: 1.0,
        strokeWeight: 8,
        zIndex: 100
      }
    };
  },
  computed: {
    deviceMarkers() {
      return Devices;
    },
    anomalySegmentOptions() {
      return {
        strokeColor: '#FA8072',
        strokeOpacity: 0.8,
        strokeWeight: this.getSegmentStrokeWeight()
      };
    },
    ongoingAnomalySegments() {
      return this.currentAnomalySegments.filter(s => s.status === 0);
    },
    weatherMarkers() {
      return this.weatherStations;
    },
    segments() {
      return this.bluetoothLocations;
    },
    markers() {
      if (this.mapRegionSelection < 0) {
        return this.deviceLocations;
      } else {
        return this.deviceLocations.filter(location => location.zone === this.mapRegionSelection);
      }
    },
    position() {
      return this.$store.state.position;
    },
    selectedMarkers() {
      return {
        selectedIdx: this.selectedIdx,
        selectedMarkers: [
          this.selectedTrafficIncident,
          this.markers,
          this.selectedSignalPerformanceIssue,
          this.selectedDetector,
          this.selectedSegment,
          this.selectedWazeAlert
        ]
      };
    },

    trafficSegments() {
      return this.$store.state.dashboard.segments;
    },

    ...mapState(['currentDate']),
    ...mapState('dashboard', [
      'incidentSegmentLinks',
      'incidentMarkers',
      'selectedTrafficIncident',
      'selectedtrafficDevice',
      'selectedSignalPerformanceIssue',
      'selectedDetector',
      'selectedSegment',
      'selectedWazeAlert',
      'trafficIncidents',
      'trafficDevices',
      'signalPerformanceIssues',
      'flowAnomData',
      'hrSummary',
      'detectors',
      'waze'
    ]),
    ...mapState('traffic', [
      'currentBluetoothAnomaly',
      'currentFlowAnomaly',
      'currentWeatherCode',
      'currentRestrictions',
      'currentAnomalySegments',
      'weatherStations',
      'mapRegionSelection',
      'mapLayersSelection',
      'incidentSettings'
    ])
  },
  mounted() {
    this.addSelectedMarker();
    this.loadPage(this.$vuetify.theme.dark);

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addMapControls(map);
      this.fetchSensorLocations();
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
    onSegmentSelected(segmentId) {
      console.log(segmentId);
    },
    onMarkerClicked(marker) {
      console.log(marker);
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
    getChipColor(s) {
      return this.selectedSegmentId === s.id ? 'blue' : 'white';
    },
    segmentOptions(segment) {
      const color =
        segment.id === this.selectedSegmentId ? this.selectedColor : Utils.getStrokeColor(segment.travelTime.level);
      const zIndex = segment.id === this.selectedSegmentId ? 2 : 1;
      return { ...this.defaultSegmentOptions, strokeColor: color, zIndex };
    },
    markerClicked(marker) {
      if (marker) {
        this.selectedMarkerId = marker.id;
        this.$store.commit('dashboard/SET_ACTIVE_MARKER', marker);
        this.$emit('click', marker);
      }
    },
    segmentClicked(s) {
      this.selectedSegmentId = s.id;
      this.$emit('clicked', s);
    },
    addSelectedMarker() {
      // console.log(`addSelectedMarker ${this.selectedIdx}`);
      switch (this.selectedIdx) {
        case 0:
          break;
        case 1:
          this.markerClicked(this.selectedtrafficDevice);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.markers.filter(x => x.id == this.selectedMarkerId),
              14
            );
          }, 1);
          break;
        case 2:
          this.markerClicked(this.selectedSignalPerformanceIssue);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.signalPerformanceIssues.filter(x => x.id == this.selectedMarkerId),
              13
            );
          }, 1);
          break;
        case 3:
          this.markerClicked(this.selectedDetector);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.deviceMarkers.filter(x => x.id == this.selectedMarkerId),
              12
            );
          }, 1);
          break;
        case 4:
          this.segmentClicked(this.selectedSegment);
          setTimeout(() => {
            this.centerMapSegments(
              this.map,
              this.trafficSegments.filter(x => x.id == this.selectedSegmentId),
              13
            );
          }, 1);
          break;
        case 5:
          this.markerClicked(this.selectedWazeAlert);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.waze.filter(x => x.id == this.selectedMarkerId),
              12
            );
          }, 1);
          break;
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
    handleMarkerClick(type, id) {
      this.selectedMarkerId = id;
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id, type });
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
    markerOptions(key) {
      const zIndex = this.selectedMarkerId == key ? 100 : 99;
      return { optimized: false, zIndex };
    },
    fetchSensorLocations() {
      this.$emit('fetchSensorLocations');
    },
    addMapControls(map) {
      this.addHomeControl(map);
      this.addMessageControl(map);
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
      const entry = this.mapLayersSelection.find(m => m === id);
      return entry !== undefined;
    },
    loadPage(darkMode) {
      if (this.$refs.mapRef == null) {
        return;
      }
      if (darkMode) {
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
    }
  },
  watch: {
    selectedMarkers: {
      handler: function() {
        this.addSelectedMarker();
      },
      deep: true
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

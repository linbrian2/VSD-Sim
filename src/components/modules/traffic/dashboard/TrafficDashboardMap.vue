<template>
  <div id="wrapper">
    <div id="google_map">
      <div>
        <GmapMap
          ref="mapRef"
          :options="options"
          :center="position"
          :zoom="12"
          map-type-id="roadmap"
          class="my-map"
          :style="
            `margin-top:-1px; width: 100%; height:${
              $vuetify.breakpoint.mobile && showPanel ? 'calc(50vh - 48px)' : 'calc(100vh - 48px)'
            }`
          "
        >
          <!-- Traffic Flow Detectors & Traffic Flow Issues -->
          <div v-if="isMapLayerVisible(LAYERS.TRAFFIC_FLOW)">
            <GmapMarker
              v-for="m in markers"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getDotMarker(m.status === 0, m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(LAYERS.TRAFFIC_FLOW, m.id)"
            />
          </div>

          <!-- Bluetooth Center locations -->
          <div v-if="isMapLayerVisible(LAYERS.BLUETOOTH)">
            <GmapMarker
              v-for="s in segments"
              :key="`${s.id}-C`"
              :position="s.position"
              :title="s.name"
              :clickable="true"
              :icon="getBluetoothMarkerIcon(s.id == selectedMarkerId)"
              @click="handleMarkerClick(LAYERS.BLUETOOTH, s.id)"
            />
          </div>

          <!-- Weather Stations -->
          <div v-if="isMapLayerVisible(LAYERS.WEATHER_STATIONS)">
            <GmapMarker
              v-for="m in weatherMarkers"
              :key="m.id"
              :position="m.position"
              :title="`${m.name} - ${m.temp}°F`"
              :clickable="true"
              :icon="getWeatherMarkerIcon(m)"
              @click="handleMarkerClick(LAYERS.WEATHER_STATIONS, m.id, m)"
            />
            <MaskMarker :position="weatherMarkerPosition" />
          </div>

          <!-- Travel restrictions -->
          <div v-if="isMapLayerVisible(LAYERS.RESTRICTIONS) || selectedIdx == CARD_IDS.RESTRICTIONS_ID">
            <GmapMarker
              v-for="r in trafficRestrictions"
              :key="r.id"
              :position="r.position"
              :title="r.description"
              :clickable="true"
              :icon="getRestrictionIcon(r.id == selectedMarkerId)"
              @click="handleMarkerClick(LAYERS.RESTRICTIONS, r.id)"
            />
          </div>

          <!-- Traffic Flow Issues -->
          <div v-if="selectedIdx == CARD_IDS.FLOW_ANOMALIES_ID">
            <GmapMarker
              v-for="m in anomalyMarkers"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getDotMarkerIcon(m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(LAYERS.TRAFFIC_FLOW, m.id)"
            />

            <!-- Anomaly marker id -->
            <GmapCustomMarker
              alignment="bottomright"
              v-for="(m, index) in anomalyMarkers"
              :key="index + 100"
              :offsetX="15"
              :offsetY="0"
              :marker="m.position"
            >
              <h3 style="color:white">{{ m.id }}</h3>
            </GmapCustomMarker>
          </div>

          <!-- Signal Issues -->
          <div v-if="isMapLayerVisible(LAYERS.ISSUED_SIGNALS) || selectedIdx == CARD_IDS.SIGNAL_ISSUES_ID">
            <GmapMarker
              v-for="m in signalIssues"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getHRIcons(m, m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(LAYERS.ISSUED_SIGNALS, m.id)"
            />
          </div>

          <!-- Waze Alerts -->
          <div v-if="isMapLayerVisible(LAYERS.WAZE_ALERTS) || selectedIdx == CARD_IDS.WAZE_ALERTS_ID">
            <GmapMarker
              v-for="m in wazeAlerts"
              :key="m.id"
              :position="m.position"
              :title="m.description"
              :clickable="true"
              :icon="getWazeIcon(m, m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(LAYERS.WAZE_ALERTS, m.id)"
            />
          </div>

          <!-- Congested Routes -->
          <div v-if="isMapLayerVisible(LAYERS.CONGESTED_SEGMENTS) || selectedIdx == CARD_IDS.CONGESTED_ROUTES_ID">
            <GmapPolyline
              v-for="s in congestedSegments"
              :key="s.id"
              :title="s.desc"
              :path.sync="s.path"
              :options="segmentOptions(s)"
              @click="handleMarkerClick(LAYERS.CONGESTED_SEGMENTS, s.id)"
            />
          </div>

          <!-- Device Anomalies -->
          <div v-if="isMapLayerVisible(LAYERS.ANOMALY_DEVICES) || selectedIdx == CARD_IDS.DEDVICE_ANOMALIES_ID">
            <GmapMarker
              v-for="m in deviceAnomalies"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :clickable="true"
              :icon="getDeviceMarkerIcon(m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              @click="handleMarkerClick(LAYERS.ANOMALY_DEVICES, m.id)"
            />
          </div>

          <div v-if="isMapLayerVisible(LAYERS.INCIDENTS) || selectedIdx == CARD_IDS.INCIDENTS_ID">
            <GmapMarker
              v-for="m in trafficIncidents"
              :key="m.id"
              :title="`Incident ${m.id}`"
              :position="m.location"
              :icon="getTrafficIncidentMarker(m)"
              :options="markerOptions(m.id, 99)"
              :clickable="true"
              @click="handleMarkerClick(LAYERS.INCIDENTS, m.id)"
            />
            <GmapCustomMarker
              alignment="center"
              v-for="s in trafficIncidents.filter(item => item.status === 0)"
              :key="`A-${s.id}`"
              :offsetX="0"
              :offsetY="0"
              :marker="s.location"
            >
              <div class="pulsate-effect"></div>
            </GmapCustomMarker>
          </div>

          <div v-if="isMapLayerVisible(LAYERS.CAMERAS) && cameraMarkers">
            <GmapMarker
              v-for="m in cameraMarkers"
              :key="m.id"
              :position="m.position"
              :title="m.name"
              :icon="getCameraMarkerIcon(m.id == selectedMarkerId)"
              :options="markerOptions(m.id)"
              :clickable="true"
              @click="handleMarkerClick(LAYERS.CAMERAS, m.id)"
            />
          </div>
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
import Constants from '@/utils/constants/dashboard.js';
import TrafficConstants from '@/utils/constants/traffic.js';
import MaskMarker from '@/components/modules/traffic/common/MaskMarker.vue';
import { mapState } from 'vuex';
import { mapIcons } from '@/mixins/mapIcons';
import { weatherCode } from '@/mixins/weatherCode';
import Colors from '@/utils/Colors.js';

export default {
  mixins: [mapIcons, weatherCode],
  props: {
    deviceLocations: Array,
    bluetoothLocations: Array,
    restrictions: Array,
    trafficCameras: Array,
    selectedIdx: Number
  },
  components: {
    MaskMarker,
    GmapCustomMarker
  },
  data() {
    return {
      colorInterp: null,
      selectedColor: 'blue',
      selectedSegmentId: null,
      selectedMarkerId: null,
      selectedMarkerType: -1,
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
          position: 6
        },
        styles: DarkMapStyle,
        gestureHandling: 'greedy'
      },
      weatherMarkerPosition: { lat: 0, lng: 0 },
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
    weatherMarkers() {
      return this.weatherStations;
    },
    cameraMarkers() {
      return this.trafficCameras;
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
    anomalyMarkers() {
      return this.deviceLocations.filter(d => d.status > 0);
    },

    position() {
      return this.$store.state.position;
    },
    selectedMarkers() {
      return {
        selectedIdx: this.selectedIdx,
        selectedMarkers: [
          this.markers,
          this.selectedTrafficIncident,
          this.selectedRestriction,
          this.selectedSignalIssue,
          this.selectedAnomalyDevice,
          this.selectedCongestedSegment,
          this.selectedWazeAlert
        ]
      };
    },

    ...mapState(['currentDate']),
    ...mapState('dashboard', [
      'incidentSegmentLinks',
      'incidentMarkers',
      'selectedTrafficIncident',
      'selectedTrafficDevice',
      'selectedRestriction',
      'selectedSignalIssue',
      'selectedAnomalyDevice',
      'selectedCongestedSegment',
      'selectedWazeAlert',
      'deviceAnomalies',
      'trafficIncidents',
      'trafficDevices',
      'trafficRestrictions',
      'signalIssues',
      'congestedSegments',
      'wazeAlerts'
    ]),
    ...mapState('traffic', [
      'showPanel',
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

  watch: {
    showPanel(show) {
      if (!show) {
        this.weatherMarkerPosition = { lat: 0, lng: 0 };
      }
    },
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
  },

  created() {
    this.CARD_IDS = {
      INCIDENTS_ID: Constants.CARD_DATA_INCIDENTS_ID,
      RESTRICTIONS_ID: Constants.CARD_DATA_RESTRICTIONS_ID,
      FLOW_ANOMALIES_ID: Constants.CARD_DATA_FLOW_ANOMALIES_ID,
      SIGNAL_ISSUES_ID: Constants.CARD_DATA_SIGNAL_ISSUES_ID,
      DEDVICE_ANOMALIES_ID: Constants.CARD_DATA_DEDVICE_ANOMALIES_ID,
      CONGESTED_ROUTES_ID: Constants.CARD_DATA_CONGESTED_ROUTES_ID,
      WAZE_ALERTS_ID: Constants.CARD_DATA_WAZE_ALERTS_ID
    };

    this.LAYERS = {
      TRAFFIC_FLOW: TrafficConstants.LAYER_DEVICE_TRAFFIC,
      BLUETOOTH: TrafficConstants.LAYER_DEVICE_BLUETOOTH,
      WEATHER_STATIONS: TrafficConstants.LAYER_DEVICE_WEATHER,
      RESTRICTIONS: TrafficConstants.LAYER_DEVICE_RESTRICTIONS,
      ISSUED_SIGNALS: TrafficConstants.LAYER_DEVICE_SIGNALS,
      WAZE_ALERTS: TrafficConstants.LAYER_DEVICE_WAZE_ALERTS,
      CONGESTED_SEGMENTS: TrafficConstants.LAYER_DEVICE_CONGESTED_SEGMENTS,
      ANOMALY_DEVICES: TrafficConstants.LAYER_DEVICE_ANOMALY_DEVICES,
      INCIDENTS: TrafficConstants.LAYER_DEVICE_INCIDENTS,
      CAMERAS: TrafficConstants.LAYER_DEVICE_CAMERAS
    };
  },

  mounted() {
    this.addSelectedMarker();
    this.$bus.$on('UPDATE_DARK_MODE', () => {
      this.loadPage(this.$vuetify.theme.dark);
    });

    this.$bus.$on('HOME_CENTER_MAP', () => {
      this.centerMap(this.map, this.markers);
    });

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
    getTrafficIncidentMarker(marker) {
      if (marker.id == this.selectedMarkerId) {
        if (this.trafficIncidents && this.trafficIncidents.length > 0 && this.startTime && this.endTime) {
          if (marker.startTime <= this.endTime && this.endTime <= marker.endTime) {
            return this.alertAnimatedIconActive;
          } else {
            return this.alertIconActive;
          }
        } else {
          return this.alertIconActive;
        }
      } else {
        if (marker.startTime <= this.endTime && this.endTime <= marker.endTime) {
          return this.alertAnimatedIcon;
        } else {
          return this.alertIcon;
        }
      }
    },
    getSegmentOptions(segment) {
      const color = segment.status === 7 ? '#FA8072' : '#195f3d';
      return {
        strokeColor: color,
        strokeOpacity: 0.8,
        strokeWeight: this.map.getZoom() / 1.5
      };
    },

    getSegmentCenter(path) {
      let idx = Math.round(path.length / 3);
      return path[idx];
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

    changeSelectedColor() {
      this.colorInterp = setInterval(() => {
        let timeVal = parseFloat((new Date().getTime() % 2000) / 1000).toFixed(2);
        if (timeVal > 1) {
          timeVal = 2 - timeVal;
        }
        this.selectedColor = Colors.getRedBlueColor(timeVal);
      }, 200);
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
      if (s) {
        this.selectedSegmentId = s.id;
        this.$emit('clicked', s);
      }
    },

    incidentClicked(incident) {
      this.markerClicked(incident);
      const markers = this.trafficIncidents.filter(x => x.id == this.selectedMarkerId);
      setTimeout(() => {
        this.centerMap(this.map, markers, 14);
      }, 1);
    },

    addSelectedMarker() {
      switch (this.selectedIdx) {
        case Constants.CARD_DATA_INCIDENTS_ID:
          {
            this.markerClicked(this.selectedTrafficIncident);
            const markers = this.trafficIncidents.filter(x => x.id == this.selectedMarkerId);
            setTimeout(() => {
              this.centerMap(this.map, markers, 14);
            }, 1);
          }
          break;
        case Constants.CARD_DATA_RESTRICTIONS_ID:
          {
            this.markerClicked(this.selectedRestriction);
            const markers = this.trafficRestrictions.filter(x => x.id == this.selectedMarkerId);
            setTimeout(() => {
              this.centerMap(this.map, markers, 14);
            }, 1);
          }
          break;
        case Constants.CARD_DATA_SIGNAL_ISSUES_ID:
          this.markerClicked(this.selectedSignalIssue);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.signalIssues.filter(x => x.id == this.selectedMarkerId),
              13
            );
          }, 1);
          break;
        case Constants.CARD_DATA_DEDVICE_ANOMALIES_ID:
          this.markerClicked(this.selectedAnomalyDevice);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.deviceMarkers.filter(x => x.id == this.selectedMarkerId),
              14
            );
          }, 1);
          break;
        case Constants.CARD_DATA_CONGESTED_ROUTES_ID:
          this.segmentClicked(this.selectedCongestedSegment);
          setTimeout(() => {
            this.centerMapSegments(
              this.map,
              this.congestedSegments.filter(x => x.id == this.selectedSegmentId),
              13
            );
          }, 1);
          break;
        case Constants.CARD_DATA_WAZE_ALERTS_ID:
          this.markerClicked(this.selectedWazeAlert);
          setTimeout(() => {
            this.centerMap(
              this.map,
              this.wazeAlerts.filter(x => x.id == this.selectedMarkerId),
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

    clearMarkerSelection() {
      this.selectedMarkerId = -1;
      this.selectedMarkerType = -1;
      this.selectedSegmentId = -1;
    },

    handleMarkerClick(type, id, marker = null) {
      this.selectedMarkerType = type;
      this.selectedMarkerId = id;
      this.weatherMarkerPosition =
        type === TrafficConstants.LAYER_DEVICE_WEATHER && marker ? marker.position : { lat: 0, lng: 0 };
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

    zoomSelectMarker(map) {
      const markers = [this.markers, this.segments, this.weatherMarkers, this.restrictions];

      const type = this.selectedMarkerType;
      if (this.selectedMarkerId && type >= 0 && type < markers.length) {
        const marker = markers[type].find(m => m.id == this.selectedMarkerId);
        if (marker) {
          map.panTo(marker.position);
          map.setZoom(16);
        }
      }
    },

    markerOptions(key, level = 1) {
      const zIndex = this.selectedMarkerId == key ? 9999 : level;
      return { optimized: false, zIndex };
    },

    fetchSensorLocations() {
      this.$emit('fetchSensorLocations');
    },

    addMapControls(map) {
      this.addHomeControl(map);
      this.addPointControl(map);
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
            this.$emit('home-clicked');
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    addPointControl(map) {
      let options = {
        position: 'right',
        content: `<div class="non-selection" style="margin:-5px 4px;"><img src="${this.zoomIcon}"/></div>`,
        title: 'Click to zoom in the selected marker',
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
            this.zoomSelectMarker(map);
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
      if (this.selectedIdx >= 0) {
        return false;
      }
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
  }
};
</script>

<style></style>

<template>
  <div>
    <!-- Traffic Flow Issues -->
    <div v-if="selectedIdx == CARD_IDS.CARD_DATA_FLOW_ANOMALIES_ID">
      <GmapMarker
        v-for="m in markers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :clickable="true"
        :icon="getDotMarkerIcon(m.id == selectedMarkerId)"
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

    <!-- Travel restrictions -->
    <div v-if="selectedIdx == Constants.CARD_DATA_RESTRICTIONS_ID">
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

    <!-- Signal Issues -->
    <div v-if="selectedIdx == CARD_IDS.CARD_DATA_SIGNAL_ISSUES_ID">
      <GmapMarker
        v-for="m in signalPerformanceIssues"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :icon="getHRIcons(m, m.id == selectedMarkerId)"
        :options="markerOptions(m.id)"
      />
    </div>

    <!-- Waze Alerts -->
    <div v-if="selectedIdx == CARD_IDS.CARD_DATA_WAZE_ALERTS_ID">
      <GmapMarker
        v-for="m in waze"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :icon="getWazeIcon(m, m.id == selectedMarkerId)"
        :options="markerOptions(m.id)"
      />
    </div>

    <!-- Congested Routes -->
    <div v-if="selectedIdx == CARD_IDS.CARD_DATA_CONGESTED_ROUTES_ID">
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

    <!-- Device Anomalies -->
    <div v-if="selectedIdx == CARD_IDS.CARD_DATA_DEDVICE_ANOMALIES_ID">
      <GmapMarker
        v-for="m in deviceMarkers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :icon="getMarker2Icon(m, m.id == selectedMarkerId)"
        :options="markerOptions(m.id)"
      />
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
    trafficCameras: Array,
    selectedIdx: Number
  },
  components: {
    GmapCustomMarker
  },
  data() {
    return {
      colorInterp: null,
      selectedColor: 'blue',
      selectedSegmentId: null,
      selectedMarkerId: null,
      selectedMarkerType: -1,
      map: null
    };
  },
  computed: {
    deviceMarkers() {
      return Devices;
    },
    ongoingAnomalySegments() {
      return this.currentAnomalySegments.filter(s => s.status === 0);
    },
    markers() {
      if (this.mapRegionSelection < 0) {
        return this.deviceLocations;
      } else {
        return this.deviceLocations.filter(location => location.zone === this.mapRegionSelection);
      }
    },

    trafficSegments() {
      return this.congestedSegments;
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
      'congestedSegments'.
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

  created() {
    this.CARD_IDS = {
      CARD_DATA_INCIDENTS_ID: Constants.CARD_DATA_INCIDENTS_ID,
      CARD_DATA_FLOW_ANOMALIES_ID: Constants.CARD_DATA_FLOW_ANOMALIES_ID,
      CARD_DATA_SIGNAL_ISSUES_ID: Constants.CARD_DATA_SIGNAL_ISSUES_ID,
      CARD_DATA_DEDVICE_ANOMALIES_ID: Constants.CARD_DATA_DEDVICE_ANOMALIES_ID,
      CARD_DATA_CONGESTED_ROUTES_ID: Constants.CARD_DATA_CONGESTED_ROUTES_ID,
      CARD_DATA_WAZE_ALERTS_ID: Constants.CARD_DATA_WAZE_ALERTS_ID,
      CARD_DATA_RESTRICTIONS_ID: Constants.CARD_DATA_RESTRICTIONS_ID
    };
  },

  mounted() {
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
      if (s) {
        this.selectedSegmentId = s.id;
        this.$emit('clicked', s);
      }
    },



    midPoint(s) {
      return s && s.path ? s.path[Math.round((s.path.length * 3) / 7)] : null;
    },

    getSegmentStrokeWeight() {
      return this.map ? this.map.getZoom() / 1.5 : 10;
    },

    handleMarkerClick(type, id) {
      this.selectedMarkerType = type;
      this.selectedMarkerId = id;
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id, type });
    },


    markerOptions(key, level = 1) {
      const zIndex = this.selectedMarkerId == key ? 9999 : level;
      return { optimized: false, zIndex };
    }
  }
};
</script>

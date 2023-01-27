<template>
  <div>
    <div v-if="trafficIncidents && trafficIncidents.length > 0">
      <GmapMarker
        v-for="m in trafficIncidents"
        :key="m.id"
        :title="`Incident ${m.id}`"
        :position="m.location"
        :icon="getTrafficIncidentMarker(m)"
        :options="markerOptions(m.id, 99)"
        :clickable="true"
        @click="handleMarkerClick(4, m.id)"
      />
    </div>

    <!-- Traffic Flow Detectors -->
    <div v-if="isMapLayerVisible(0)">
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

    <!-- Bluetooth Center locations -->
    <div v-if="isMapLayerVisible(1)">
      <GmapMarker
        v-for="s in bluetoothLocations"
        :key="`${s.id}-C`"
        :position="s.position"
        :title="s.name"
        :clickable="true"
        :icon="getBluetoothMarkerIcon(s.id == selectedMarkerId)"
        @click="handleMarkerClick(1, s.id)"
      />

      <!-- Travel Time Display -->
      <GmapCustomMarker
        alignment="bottomright"
        v-for="(s, index) in bluetoothLocations"
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
      <!-- <GmapPolyline v-for="s in bluetoothLocations" :key="s.id" :path.sync="s.path" :options="getSegmentOptions(s)" /> -->
    </div>

    <!-- Weather Stations -->
    <div v-if="isMapLayerVisible(2)">
      <GmapMarker
        v-for="m in weatherStations"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :clickable="true"
        :icon="getNormalWeatherMarkerIcon(m, m.id === selectedMarkerId)"
        @click="handleMarkerClick(2, m.id)"
      />

      <!-- Weather Station Temeperature -->
      <GmapCustomMarker
        alignment="topright"
        v-for="m in weatherStations"
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

    <!-- Traffic Signals -->
    <div v-if="isMapLayerVisible(5)">
      <GmapMarker
        v-for="m in signals"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :icon="getHRIcons(m, m.id == selectedMarkerId)"
        :options="markerOptions(m.id)"
      />
    </div>

    <!-- Waze Alerts -->
    <div v-if="isMapLayerVisible(6)">
      <GmapMarker
        v-for="m in wazeAlerts"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :icon="getWazeIcon(m, m.id == selectedMarkerId)"
        :options="markerOptions(m.id)"
      />
    </div>

    <div v-if="isMapLayerVisible(10) && cameraMarkers">
      <GmapMarker
        v-for="m in cameraMarkers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :icon="getCameraMarkerIcon(m.id == selectedMarkerId)"
        :options="markerOptions(m.id)"
        :clickable="true"
        @click="handleMarkerClick(10, m.id)"
      />
    </div>
  </div>
</template>

<script>
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import { mapState } from 'vuex';
import { mapIcons } from '@/mixins/mapIcons';
import { weatherCode } from '@/mixins/weatherCode';

export default {
  mixins: [mapIcons, weatherCode],
  props: {
    map: Object,
    deviceLocations: Array,
    bluetoothLocations: Array,
    weatherStations: Array,
    restrictions: Array,
    trafficCameras: Array,
    trafficIncidents: Array,
    wazeAlerts: Array,
    signals: Array
  },
  components: {
    GmapCustomMarker
  },
  data() {
    return {
      selectedMarkerId: null,
      selectedMarkerType: -1
    };
  },
  computed: {
    cameraMarkers() {
      return this.trafficCameras;
    },
    markers() {
      if (this.mapRegionSelection < 0) {
        return this.deviceLocations;
      } else {
        return this.deviceLocations.filter(location => location.zone === this.mapRegionSelection);
      }
    },

    ...mapState('traffic', ['mapRegionSelection', 'mapLayersSelection'])
  },

  methods: {
    isMapLayerVisible(id) {
      const entry = this.mapLayersSelection.find(m => m === id);
      return entry !== undefined;
    },

    filterMarkers(markers) {
      return this.mapRegionSelection < 0
        ? markers
        : markers.filter(location => location.zone === this.mapRegionSelection);
    },

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

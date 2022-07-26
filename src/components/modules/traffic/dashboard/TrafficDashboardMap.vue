<template>
  <div>
    <!-- AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: {{ segments }} -->
    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="12"
      map-type-id="roadmap"
      class="my-map"
      style="margin-top:-1px; width: 100%; height:calc(100vh - 48px)"
    >
      <!-- Dashboard -->
      <!-- <GmapMarker
      v-for="m in markers"
      :key="m.id"
      :position="m.position"
      :title="m.name"
      :clickable="true"
      :icon="getMarkerIcon(m)"
      :options="markerOptions(m.id)"
    />
    <GmapPolyline v-for="s in segments" :key="s.id" :title="s.desc" :path.sync="s.path" :options="segmentOptions(s)" />
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
    </GmapCustomMarker> -->

      <!-- Traffic FLow Detectors -->
      <div v-if="isMapLayerVisible(0)">
        <GmapMarker
          v-for="m in markers"
          :key="m.id"
          :position="m.position"
          :title="m.name"
          :clickable="true"
          :icon="getDefaultMarkerIcon(m)"
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

      <!-- InfoWindow -->
      <!-- <InfoWindow :position="infoPosition" ref="infoWindow" /> -->

      <!-- Heatmap layer -->
      <!-- <GmapHeatMap :data="heatMapData" :options="{ maxIntensity: 15, dissipating: true, radius: 10 }" /> -->
    </GmapMap>
  </div>
</template>

<script>
/* global google */
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import MapUtils from '@/utils/MapUtils.js';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import { mapState } from 'vuex';
import { mapIcons } from '@/mixins/mapIcons';
import { weatherCode } from '@/mixins/weatherCode';

export default {
  mixins: [mapIcons, weatherCode],
  props: {
    deviceLocations: Array,
    bluetoothLocations: Array,
    restrictions: Array
  },
  components: {
    GmapCustomMarker
  },
  data() {
    return {
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
      infoPosition: null
    };
  },
  computed: {
    anomalySegmentOptions() {
      return {
        strokeColor: '#FA8072',
        strokeOpacity: 0.8,
        strokeWeight: this.getSegmentStrokeWeight()
      };
    },
    getSegmentStrokeWeight() {
      return this.map ? this.map.getZoom() / 1.5 : 10;
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
    ...mapState(['currentDate']),
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
    this.loadPage(this.$vuetify.theme.dark);

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addMapControls(map);
      this.fetchSensorLocations();
    });
  },
  methods: {
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
      console.log(type);
      console.log(id);
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id, type });
    },
    centerMap(map, markers) {
      if (map && markers.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length; i++) {
          bounds.extend(markers[i].position);
        }
        map.fitBounds(bounds, 0);
      }
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
    markerOptions(key) {
      const zIndex = this.selectedMarkerId == key ? 2 : 1;
      return { zIndex };
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

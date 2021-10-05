<template>
  <GmapMap ref="mapRef" :options="options" :center="position" :zoom="12" map-type-id="roadmap" class="my-map">
    <!-- Traffic FLow Detectors -->
    <div v-if="isMapLayerVisible(0)">
      <GmapMarker
        v-for="m in markers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :clickable="true"
        :icon="getMarkerIcon(m)"
        @click="markerClicked(m)"
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
        @click="segmentClicked(s)"
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
          <!-- <div v-if="s.status != 7" class="white--text">{{ formatDisplay(s.travelTime) }}</div> -->
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
        @click="weatherMarkerClicked(m)"
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
        @click="restrictionClicked(r)"
      />
    </div>

    <!-- Anomaly Segments -->
    <div v-if="isMapLayerVisible(4)">
      <GmapPolyline
        v-for="(s, idx) in currentAnomalySegments"
        :key="`${s.id}-${idx}`"
        :path.sync="s.path"
        :options="anomalySegmentOptions"
        @click="anomalySegmentClicked(s)"
      />

      <GmapCustomMarker
        alignment="center"
        v-for="s in currentAnomalySegments"
        :key="`A-${s.id}`"
        :offsetX="0"
        :offsetY="0"
        :marker="midPoint(s)"
      >
        <div class="pulsate-effect"></div>
      </GmapCustomMarker>
    </div>

    <!-- Heatmap layer -->
    <GmapHeatMap :data="heatMapData" :options="{ maxIntensity: 15, dissipating: true, radius: 10 }" />
  </GmapMap>
</template>

<script>
/* global google */
import MapStyles from '@/utils/DarkMapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import { gmapApi } from 'vue2-google-maps';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import GmapHeatMap from '@/components/traffic/dashboard/HeatMap';
import { weatherCode } from '@/mixins/weatherCode';
import { mapIcons } from '@/mixins/mapIcons';

export default {
  mixins: [weatherCode, mapIcons],

  components: {
    GmapCustomMarker,
    GmapHeatMap
  },

  data: () => ({
    selectedMarker: null,
    map: null,

    deviceLocations: [],
    bluetoothLocations: [],
    restrictions: [],

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
      styles: MapStyles
    }
  }),

  computed: {
    google: gmapApi,

    markers() {
      if (this.mapRegionSelection < 0) {
        return this.deviceLocations;
      } else {
        return this.deviceLocations.filter(location => location.zone === this.mapRegionSelection);
      }
    },

    weatherMarkers() {
      return this.weatherStations;
    },

    segments() {
      return this.bluetoothLocations;
    },

    position() {
      return this.$store.state.traffic.position;
    },

    anomalySegmentOptions() {
      return {
        strokeColor: '#FA8072',
        strokeOpacity: 0.8,
        strokeWeight: this.getSegmentStrokeWeight()
      };
    },

    heatMapData() {
      return (
        this.google &&
        this.markers
          .filter(marker => marker.status > 0)
          .map(marker => {
            // the Heatmap API is one of the few that *must* use a LatLng object
            return {
              location: new this.google.maps.LatLng(marker.position),
              weight: 0
            };
          })
      );
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
  },

  mounted() {
    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addMapControls(map);
      this.fetchSensorLocations();
    });
  },

  methods: {

    getMap() {
      return this.$refs.mapRef;
    },

    isMapLayerVisible(id) {
      const entry = this.mapLayersSelection.find(m => m === id);
      return entry !== undefined;
    },

    addMapControls(map) {
      this.addHomeControl(map);
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

    midPoint(s) {
      return s.path[Math.round((s.path.length * 3) / 7)];
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

    centerMap(map, markers) {
      if (map && markers.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length; i++) {
          bounds.extend(markers[i].position);
        }
        map.fitBounds(bounds, 0);
      }
    },

    formatDisplay(seconds) {
      if (!seconds) {
        return '';
      }
      let minutes = seconds / 60;
      return `${minutes.toFixed(1)} min`;
    },

    getSegmentCenter(path) {
      let idx = Math.round(path.length / 3);
      return path[idx];
    },

    getSegmentChipColor(segment) {
      return segment.status == 7 ? 'red' : 'white';
    },

    getSegmentStrokeWeight() {
      return this.map ? this.map.getZoom() / 1.5 : 10;
    },

    getStrokeColor(level) {
      const colors = ['#808080', '#0000FF', '#339900', '#00FF33', '#D7DF01', '#FFCC55', '#FF6600', '#FF0000'];
      return colors[level];
    },

    getSegmentOptions(segment) {
      const color = segment.status === 7 ? '#FA8072' : '#195f3d';
      return {
        strokeColor: color,
        strokeOpacity: 0.8,
        strokeWeight: this.map.getZoom() / 1.5
      };
    }
  }
};
</script>
<style>
.my-map {
  width: 101.5%;
  height: calc(100vh - 48px);
  margin-top: -12px;
  margin-left: -12px;
  margin-right: 0px;
}
.gm-style div {
  -webkit-transform: scale(1.003);
}

.badge {
  display: inline-block;
  min-width: 10px;
  padding: 8px 11px;
  margin-top: -8px;
  font-size: 14px;
  font-weight: 700;
  color: #000;
  line-height: 1;
  vertical-align: middle;
  white-space: nowrap;
  text-align: center;
  background-color: #fff;
  border-radius: 50%;
  border: none;
  outline: none;
}

.status-btn {
  background: none rgb(255, 255, 255);
  border: 0px;
  margin: 10px;
  padding: 0px;
  text-transform: none;
  appearance: none;
  position: absolute;
  cursor: pointer;
  border-radius: 2px;
  height: 40px;
  width: 40px;
  box-shadow: rgba(0, 0, 0, 0.3) 0px 1px 4px -1px;
  overflow: hidden;
  top: 60px;
  right: 0px;
}

.w3-badge {
  background-color: #000;
  color: #fff;
  display: inline-block;
  text-align: center;
  border-radius: 50%;
  vertical-align: middle;
}

.w3-green,
.w3-hover-green:hover {
  color: #fff !important;
  background-color: #4caf50 !important;
}
.w3-red,
.w3-hover-red:hover {
  color: #f00 !important;
  /* background-color: #f44336 !important; */
  border: 1px solid #ef5058;
}

@keyframes pulsate {
  0% {
    transform: scale(0.1);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: scale(1.2);
    opacity: 0;
  }
}

.pulsate-effect {
  width: 80px;
  height: 80px;
  position: absolute;
  top: -40px;
  left: -40px;
  display: block;
  background: rgba(255, 0, 0, 0.5);
  border: 2px solid rgb(255, 0, 0, 0.6);
  border-radius: 50%;
  opacity: 0.1;
  animation: pulsate 1s ease-out infinite;
  -webkit-animation: pulsate 1s ease-out infinite;
}

.pin-red {
  width: 20px;
  height: 20px;
  position: relative;
  top: 10px;
  left: 10px;
  background: rgba(255, 0, 0, 1);
  border: 2px solid #5b50ef;
  border-radius: 50%;
  z-index: 1000;
}
</style>

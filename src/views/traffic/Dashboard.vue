<template>
  <div>
    <!-- Right display panel -->
    <RightPanel name="dashboardSideBarWidth" :title="currentTitle">
      <component :is="currentComponent" v-bind="currentProperties" ref="refPanelInfo" />
    </RightPanel>

    <!-- Maps -->
    <GmapMap ref="mapRef" :options="options" :center="position" :zoom="12" map-type-id="roadmap" class="my-map"
      style="margin-top:-1px; width: 100%; height:calc(100vh - 48px)">
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
          v-for="(s, idx) in ongoingAnomalySegments"
          :key="`${s.id}-${idx}`"
          :path.sync="s.path"
          :options="anomalySegmentOptions"
          @click="anomalySegmentClicked(s)"
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
      <GmapHeatMap :data="heatMapData" :options="{ maxIntensity: 15, dissipating: true, radius: 10 }" />
    </GmapMap>

    <Toolbar :entities="entities" />

    <!-- Bottom Sheet Display -->
    <v-bottom-sheet v-model="sheet">
      <v-sheet class="text-center" height="200px">
        <v-btn class="mt-6" text color="red" @click="sheet = !sheet">
          close
        </v-btn>
        <div class="py-3">
          Example page
        </div>
      </v-sheet>
    </v-bottom-sheet>

    <!-- Popup Dialogs -->
    <DeviceDialog v-model="showDeviceInfo" ref="deviceDialog" />
    <BluetoothDialog v-model="showBluetoothInfo" ref="bluetoothDialog" />
    <WeatherDialog v-model="showWeatherInfo" ref="weatherDialog" />
    <AnomalySegmentDialog v-model="showAnomalySegmentInfo" ref="anomalySegmentDialog" />
    <RestrictionDialog v-model="showRestrictionInfo" ref="restrictionDialog" />
    <SelectionDialog v-model="showSelection" ref="selectionDialog" />
  </div>
</template>

<script>
/* global google */
import { mapState, mapActions } from 'vuex';
import { gmapApi } from 'vue2-google-maps';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import GmapHeatMap from '@/components/traffic/dashboard/HeatMap';
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils.js';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import Constants from '@/utils/constants/traffic';
import { weatherCode } from '@/mixins/weatherCode';
import { mapIcons } from '@/mixins/mapIcons';

import RightPanel from '@/components/traffic/common/RightPanel';
import InfoWindow from '@/components/traffic/dashboard/InfoWindow';

import Toolbar from '@/components/traffic/dashboard/Toolbar';

import FlowDataInfo from '@/components/traffic/dashboard/FlowDataInfo';
import BluetoothDataInfo from '@/components/traffic/dashboard/BluetoothDataInfo';
import WeatherDataInfo from '@/components/traffic/dashboard/WeatherDataInfo';
import AnomalySegmentInfo from '@/components/traffic/dashboard/AnomalySegmentInfo';

import DeviceDialog from '@/components/traffic/dashboard/DeviceDialog';
import BluetoothDialog from '@/components/traffic/dashboard/BluetoothDialog';
import WeatherDialog from '@/components/traffic/dashboard/WeatherDialog';
import RestrictionDialog from '@/components/traffic/dashboard/RestrictionDialog';
import AnomalySegmentDialog from '@/components/traffic/dashboard/AnomalySegmentDialog';
import SelectionDialog from '@/components/traffic/dashboard/SelectionDialog';

export default {
  mixins: [weatherCode, mapIcons],
  components: {
    Toolbar,
    RightPanel,
    InfoWindow,
    GmapCustomMarker,
    GmapHeatMap,
    FlowDataInfo,
    BluetoothDataInfo,
    WeatherDataInfo,
    AnomalySegmentInfo,
    DeviceDialog,
    BluetoothDialog,
    WeatherDialog,
    RestrictionDialog,
    AnomalySegmentDialog,
    SelectionDialog
  },

  data: () => ({
    loading: false,
    sheet: false,

    selectedMarker: null,
    currentComponent: null,
    currentTitle: '',

    showDeviceInfo: false,
    showBluetoothInfo: false,
    showWeatherInfo: false,
    showRestrictionInfo: false,
    showAnomalySegmentInfo: false,
    showSelection: false,

    map: null,
    infoPosition: null,

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
      styles: DarkMapStyle
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

    ongoingAnomalySegments() {
      return this.currentAnomalySegments.filter(s => s.status === 0);
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

    currentProperties() {
      if (this.currentComponent === FlowDataInfo) {
        return { marker: this.selectedMarker };
      } else if (this.currentComponent === BluetoothDataInfo) {
        return { segment: this.selectedMarker };
      } else if (this.currentComponent === WeatherDataInfo) {
        return { station: this.selectedMarker };
      } else if (this.currentComponent === AnomalySegmentInfo) {
        return { segment: this.selectedMarker };
      }
      return {};
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
    },

    entities() {
      return this.generateSearchEntities();
    },

    ...mapState('traffic', [
      'currentBluetoothAnomaly',
      'currentFlowAnomaly',
      'currentWeatherCode',
      'currentRestrictions',
      'currentAnomalySegments',
      'weatherStations',
      'currentDate',
      'mapRegionSelection',
      'mapLayersSelection',
      'incidentSettings'
    ])
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
    },

    currentFlowAnomaly: function(value) {
      if (value) {
        this.updateMarkerStatus(value);
      }
    },

    currentBluetoothAnomaly: function(value) {
      if (value) {
        this.updateSegmentStatus(value);
      }
    },

    currentWeatherCode: function(value) {
      if (value) {
        this.updateWeatherStatus(value);
      }
    },

    currentRestrictions: function(value) {
      if (value) {
        this.updateRestrictionsStatus(value);
      }
    },

    currentAnomalySegments: function(value) {
      if (value) {
        this.updateAnomalySegementsStatus(value);
      }
    }
  },

  mounted() {
    this.loadPage(this.$vuetify.theme.dark);

    this.showScrollBar(false);

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addMapControls(map);
      this.fetchSensorLocations();
    });

    this.$bus.$on('SHOW_SELECTION_POPUP', id => {
      this.showSelectionDialog(id);
    });

    this.$bus.$on('DISPLAY_MARKER_DETAILS', ({ id, type }) => {
      this.handleMarkerClick(type, id);
    });

    this.$bus.$on('MAP_LAYER_SELECTION', ({ id, type }) => {
      this.handleMapLayerSelection(type, id);
    });

    this.$bus.$on('MAP_CENTER_SEGMENT', ({ segment }) => {
      this.centerSegment(segment);
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    setTimeout(() => {
      this.fetchLatestData();
    }, 2000);
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', false);
  },

  destroyed: function() {
    this.showScrollBar(true);
  },

  methods: {
    loadPage(darkMode) {
      if (this.$refs.mapRef == null) {
        return
      }
      if (darkMode) {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({styles: DarkMapStyle})
        })
      } else {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({styles: null})
        })
      }
    },
    
    showScrollBar(show) {
      let html = document.getElementsByTagName('html')[0];
      html.style.overflowY = show ? 'auto' : 'hidden';
    },

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

    showPanelIfNot() {
      if (!this.$store.state.traffic.showPanel) {
        this.$store.commit('traffic/SHOW_PANEL', true);
      }
    },

    handleMarkerClick(type, id) {
      let marker = null;
      switch (type) {
        case 0:
          marker = this.markers.find(m => m.id === id);
          this.markerClicked(marker);
          break;
        case 1:
          marker = this.bluetoothLocations.find(m => m.id === id);
          this.segmentClicked(marker);
          break;
        case 2:
          marker = this.weatherStations.find(m => m.id === id);
          this.weatherMarkerClicked(marker);
          break;
        case 3:
          marker = this.restrictions.find(m => m.id === id);
          this.restrictionClicked(marker);
          break;
        case 4:
          marker = this.currentAnomalySegments.find(m => m.id === id);
          this.anomalySegmentClicked(marker);
          break;
      }
    },

    markerClicked(marker) {
      // if (marker && this.$refs.deviceDialog) {
      //   this.$refs.deviceDialog.init(marker);
      //   this.showDeviceInfo = true;
      // }

      // this.infoPosition = marker.position;
      // this.$refs.infoWindow.show({});

      this.showPanelIfNot();
      this.selectedMarker = marker;
      this.currentTitle = 'Traffic Flow Detector';
      if (this.currentComponent !== FlowDataInfo) {
        this.currentComponent = FlowDataInfo;
      } else {
        setTimeout(() => {
          if (this.$refs.refPanelInfo) {
            this.$refs.refPanelInfo.init(marker);
            //this.$vuetify.goTo('#info', { container: FlowDataInfo });
          }
        }, 250);
      }
    },

    segmentClicked(segment) {
      // if (segment && this.$refs.bluetoothDialog) {
      //   this.$refs.bluetoothDialog.init(segment);
      //   this.showBluetoothInfo = true;
      // }
      this.showPanelIfNot();
      this.selectedMarker = segment;
      this.currentTitle = 'Bluetooth Travel Time';
      if (this.currentComponent !== BluetoothDataInfo) {
        this.currentComponent = BluetoothDataInfo;
      } else {
        setTimeout(() => {
          if (this.$refs.refPanelInfo) {
            this.$refs.refPanelInfo.init(segment);
          }
        }, 250);
      }
    },

    weatherMarkerClicked(marker) {
      // if (marker && this.$refs.weatherDialog) {
      //   this.$refs.weatherDialog.init(marker);
      //   this.showWeatherInfo = true;
      // }
      this.showPanelIfNot();
      this.selectedMarker = marker;
      this.currentTitle = 'Weather Station';
      if (this.currentComponent !== WeatherDataInfo) {
        this.currentComponent = WeatherDataInfo;
      } else {
        if (this.$refs.refPanelInfo) {
          this.$refs.refPanelInfo.init(marker);
        }
      }
    },

    restrictionClicked(marker) {
      if (marker && this.$refs.restrictionDialog) {
        this.$refs.restrictionDialog.init(marker);
        this.showRestrictionInfo = true;
      }
      this.selectedMarker = marker;
      this.currentTitle = 'Restrictions';
    },

    anomalySegmentClicked(segment) {
      // if (segment && this.$refs.anomalySegmentDialog) {
      //   this.$refs.anomalySegmentDialog.init(segment);
      //   this.showAnomalySegmentInfo = true;
      // }
      this.showPanelIfNot();
      this.selectedMarker = segment;
      this.currentTitle = 'Current Incident';
      if (this.currentComponent !== AnomalySegmentInfo) {
        this.currentComponent = AnomalySegmentInfo;
      } else {
        if (this.$refs.refPanelInfo) {
          this.$refs.refPanelInfo.init(segment);
        }
      }

      setTimeout(() => {
        this.centerSegment(segment);
      }, 500);
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

    showBottomSheet() {
      this.sheet = !this.sheet;
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
    },

    updateMarkerStatus(data) {
      let map = new Map();
      data.forEach(d => {
        map.set(d.deviceId, d.status);
      });
      this.deviceLocations.forEach(d => {
        if (map.has(d.id)) {
          d.status = map.get(d.id);
        }
      });
    },

    updateSegmentStatus(data) {
      let map = new Map();
      data.forEach(d => {
        map.set(d.linkId, { status: d.status, travelTime: d.travelTime });
      });
      this.bluetoothLocations.forEach(d => {
        if (map.has(d.id)) {
          let value = map.get(d.id);
          d.status = value.status;
          d.travelTime = value.travelTime;
        }
      });
    },

    updateWeatherStatus(data) {
      let map = new Map();
      data.forEach(d => {
        const data = { code: d.code, temp: d.temp };
        map.set(d.stationId, data);
      });
      this.weatherStations.forEach(d => {
        if (map.has(d.id)) {
          const data = map.get(d.id);
          d.code = data.code;
          d.temp = Math.round(data.temp);
        }
      });
    },

    updateRestrictionsStatus(data) {
      this.restrictions = data.map(d => {
        return {
          id: d.id,
          name: d.name,
          position: d.position,
          data: d.data
        };
      });
    },

    updateAnomalySegementsStatus(data) {
      if (data.length > 0) {
        // const id = data[0].id;
        // const msg = `Anomaly segment ${id} detected`;
        //this.$store.dispatch('traffic/setSystemStatus', { text: msg, color: 'orange darken-2' });
      }
    },

    showSelectionDialog(type) {
      if (this.$refs.selectionDialog) {
        if (type === 0) {
          this.$refs.selectionDialog.init(
            Constants.DEVICE_TRAFFIC_ICON,
            Constants.DEVICE_TRAFFIC,
            0,
            this.deviceLocations
          );
        } else if (type === 1) {
          this.$refs.selectionDialog.init(
            Constants.DEVICE_BLUETOOTH_ICON,
            Constants.DEVICE_BLUETOOTH,
            1,
            this.bluetoothLocations
          );
        } else if (type === 2) {
          this.$refs.selectionDialog.init(
            Constants.DEVICE_WEATHER_ICON,
            Constants.DEVICE_WEATHER,
            2,
            this.weatherStations
          );
        } else if (type === 3) {
          this.$refs.selectionDialog.init(
            Constants.DEVICE_RESTRICTIONS_ICON,
            Constants.DEVICE_RESTRICTIONS,
            3,
            this.restrictions
          );
        } else if (type === 4) {
          this.$refs.selectionDialog.init(
            Constants.DEVICE_SEGMENTS_ICON,
            Constants.DEVICE_SEGMENTS,
            4,
            this.currentAnomalySegments
          );
        }
        this.showSelection = true;
      }
    },

    fetchDevices() {
      try {
        Api.fetchAnomalyDevices().then(response => {
          let data = response.data;
          this.deviceLocations = data.map(obj => ({ ...obj, status: 0 }));

          if (this.deviceLocations.length > 0) {
            this.$store.commit('traffic/SET_ACTIVE_MARKER', this.deviceLocations[0]);
          }
        });
      } catch (error) {
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchBluetoothAnomalyDevices() {
      try {
        Api.fetchBluetoothAnomalyDevices().then(response => {
          let data = response.data;
          this.bluetoothLocations = data.map(obj => ({ ...obj, status: 0 }));
        });
      } catch (error) {
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchSensorLocations() {
      this.fetchDevices();
      this.fetchBluetoothAnomalyDevices();

      if (this.weatherStations.length === 0) {
        this.fetchWeatherStations();
      }
    },

    async fetchLatestData() {
      const secondsAgo = 600;
      const { severity, duration } = this.incidentSettings;

      this.loading = true;
      try {
        // Now we await for both results, whose async processes have already been started
        const [anomalyData, travelTimeData, weatherData, restrictionData, anomalySegments] = await Promise.all([
          Api.fetchLatestAnomalyData(secondsAgo),
          Api.fetchLatestBluetoothAnomalyData(secondsAgo),
          Api.fetchLatestWeatherData(secondsAgo),
          Api.fetchLatestRestrictionData(secondsAgo),
          Api.fetchLatestAnomalySegments(3600, severity, duration)
        ]);

        if (anomalyData.data.status === 'OK') {
          this.$store.commit('traffic/SET_CURRENT_FLOW_ANOMALY_DATA', anomalyData.data.data);
        }

        if (travelTimeData.data.status === 'OK') {
          this.$store.commit('traffic/SET_CURRENT_BLUETOOTH_ANOMALY_DATA', travelTimeData.data.data);
        }

        if (weatherData.data.status === 'OK') {
          this.$store.commit('traffic/SET_CURRENT_WEATHER_DATA', weatherData.data.data);
        }

        if (restrictionData.data.status === 'OK') {
          this.$store.commit('traffic/SET_CURRENT_RESTRICTION_DATA', restrictionData.data.data);
        }

        if (anomalySegments.data.status === 'OK') {
          this.$store.commit('traffic/SET_CURRENT_ANOMALY_SEGMENTS', anomalySegments.data.data);
        }
      } catch (error) {
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    generateSearchEntities() {
      const items1 = this.deviceLocations.map(d => ({
        id: d.id,
        type: 0,
        desc: Constants.DEVICE_TRAFFIC + ':' + d.name
      }));

      const items2 = this.bluetoothLocations.map(d => ({
        id: d.id,
        type: 1,
        desc: Constants.DEVICE_BLUETOOTH + ':' + d.id + ' - ' + d.name
      }));

      const items3 = this.weatherStations.map(d => ({
        id: d.id,
        type: 2,
        desc: Constants.DEVICE_WEATHER + ':' + d.name
      }));

      const items4 = this.restrictions.map(d => ({
        id: d.id,
        type: 3,
        desc: Constants.DEVICE_RESTRICTIONS + ':' + d.name
      }));

      const items5 = this.currentAnomalySegments.map(d => ({
        id: d.id,
        type: 4,
        desc: Constants.DEVICE_SEGMENTS + ':' + d.id
      }));

      let result = [];
      if (items1) {
        result.push(...items1);
      }

      if (items2) {
        result.push(...items2);
      }

      if (items3) {
        result.push(...items3);
      }

      if (items4) {
        result.push(...items4);
      }

      if (items5) {
        result.push(...items5);
      }

      return result;
    },

    ...mapActions('traffic', ['fetchWeatherStations'])
  }
};
</script>
<style>
/* .my-map {
  width: 101.5%;
  height: calc(100vh - 48px);
  margin-top: -12px;
  margin-left: 0px;
  margin-right: 0px;
} */
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

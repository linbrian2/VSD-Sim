<template>
  <div>
    <!-- Right display panel -->
    <RightPanel
      name="dashboardSideBarWidth"
      :title="currentTitle"
      :tableButton="!trafficInfoShow"
      v-if="!$vuetify.breakpoint.mobile"
    >
      <template v-slot:buttons v-if="!trafficInfoShow">
        <div class="mr-6 mt-3">
          <v-btn icon small @click.stop="prevItemClicked"><v-icon>mdi-chevron-left</v-icon></v-btn>
          <v-btn icon small @click.stop="nextItemClicked"><v-icon>mdi-chevron-right</v-icon></v-btn>
        </div>
      </template>

      <component v-if="trafficInfoShow" :is="currentComponent" v-bind="currentProperties" ref="refPanelInfo" />
      <InfoColumn v-else :selectedIdx="selectedIdx" :cardData="cardData" />
    </RightPanel>

    <!-- Maps -->
    <TrafficDashboardMap
      :deviceLocations="deviceLocations"
      :bluetoothLocations="bluetoothLocations"
      :restrictions="restrictions"
      :trafficCameras="trafficCameras"
      :selectedIdx="selectedIdx"
      @fetchSensorLocations="fetchSensorLocations"
      @home-clicked="onHomeClick"
    />

    <BottomDataDisplay
      name="dashboardSideBarWidth"
      :title="currentTitle"
      :tableButton="!trafficInfoShow"
      v-if="$vuetify.breakpoint.mobile"
    >
      <component v-if="trafficInfoShow" :is="currentComponent" v-bind="currentProperties" ref="refPanelInfo" />
      <InfoColumn v-else :selectedIdx="selectedIdx" :cardData="cardData" />
    </BottomDataDisplay>

    <DashboardInfoOverlay @cardClicked="cardClicked" />

    <Toolbar :entities="entities" />

    <!-- Bottom Sheet Display -->
    <v-bottom-sheet v-model="sheet">
      <v-app-bar dense>
        <v-toolbar-title>Notifications</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn icon @click="sheet = !sheet">
          <v-icon small>mdi-close</v-icon>
        </v-btn>
      </v-app-bar>

      <v-sheet class="text-center" height="400px">
        <div class="py-3">
          Coming soon ...
        </div>
      </v-sheet>
    </v-bottom-sheet>
    <!-- Popup Dialogs -->
    <SelectionDialog v-model="showSelection" ref="selectionDialog" />
    <VideoPlayerDialog ref="vpRef" videoType="application/x-mpegURL" v-model="showVideoPlayer" />
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import { gmapApi } from 'vue2-google-maps';
import Api from '@/utils/api/traffic';
import Constants from '@/utils/constants/traffic';

import RightPanel from '@/components/modules/traffic/common/RightPanel';
import InfoWindow from '@/components/modules/traffic/dashboard/InfoWindow';

import Toolbar from '@/components/modules/traffic/dashboard/Toolbar';

import FlowDataInfo from '@/components/modules/traffic/dashboard/FlowDataInfo';
import BluetoothDataInfo from '@/components/modules/traffic/dashboard/BluetoothDataInfo';
import WeatherDataInfo from '@/components/modules/traffic/dashboard/WeatherDataInfo';
import IncidentDataInfo from '@/components/modules/traffic/dashboard/IncidentDataInfo';
import CameraMultiView from '@/components/modules/traffic/dashboard/CameraMultiView';

import SelectionDialog from '@/components/modules/traffic/dashboard/SelectionDialog';

import Utils from '@/utils/Utils';
import InfoColumn from '@/components/modules/dashboard/InfoColumn.vue';
import DashboardInfoOverlay from '@/components/modules/dashboard/app/DashboardInfoOverlay.vue';
import TrafficDashboardMap from '@/components/modules/traffic/dashboard/TrafficDashboardMap.vue';
import BottomDataDisplay from '@/components/modules/traffic/common/BottomDataDisplay.vue';
import VideoPlayerDialog from '@/components/modules/traffic/common/VideoPlayerDialog';
import { getVideoUrl } from '@/utils/DeldotVideoUrl';

export default {
  components: {
    Toolbar,
    RightPanel,
    InfoWindow,
    FlowDataInfo,
    BluetoothDataInfo,
    WeatherDataInfo,
    IncidentDataInfo,
    CameraMultiView,
    SelectionDialog,
    InfoColumn,
    TrafficDashboardMap,
    DashboardInfoOverlay,
    BottomDataDisplay,
    VideoPlayerDialog
  },

  data: () => ({
    trafficInfoShow: false,
    showVideoPlayer: false,
    cardData: null,
    selectedIdx: -1,
    selectedSegmentId: '',

    loading: false,
    sheet: false,

    selectedMarker: null,
    currentComponent: null,
    currentTitle: '',

    showSelection: false,

    map: null,

    deviceLocations: [],
    bluetoothLocations: [],
    restrictions: []
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

    position() {
      return this.$store.state.position;
    },

    currentProperties() {
      if (this.currentComponent === FlowDataInfo) {
        return { marker: this.selectedMarker };
      } else if (this.currentComponent === BluetoothDataInfo) {
        return { segment: this.selectedMarker };
      } else if (this.currentComponent === WeatherDataInfo) {
        return { station: this.selectedMarker };
      } else if (this.currentComponent === IncidentDataInfo) {
        return { segment: this.selectedMarker };
      } else if (this.currentComponent === CameraMultiView) {
        return { camera: this.selectedMarker };
      }
      return {};
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

    ...mapState(['currentDate']),
    ...mapState('traffic', [
      'showPanel',
      'currentBluetoothAnomaly',
      'currentFlowAnomaly',
      'currentWeatherCode',
      'currentRestrictions',
      'currentAnomalySegments',
      'weatherStations',
      'trafficCameras',
      'mapRegionSelection',
      'mapLayersSelection',
      'incidentSettings'
    ])
  },

  watch: {
    showPanel(show) {
      if (!show) {
        // Destroy multi camera view to save internet bandwidth
        if (this.currentComponent === CameraMultiView) {
          this.currentComponent = null;
        }
      }
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
    this.$bus.$on('SHOW_SELECTION_POPUP', id => {
      if (id === 0) {
        this.showSelectionDialog();
      } else if (id === 1) {
        this.showCameraMultiView();
      }
    });

    this.$bus.$on('DISPLAY_MARKER_DETAILS', ({ id, type }) => {
      this.selectedIdx = -1;
      this.handleMarkerClick(type, id);
    });

    this.$bus.$on('PLAY_POPUP_VIDEO', id => {
      this.playVideo(id);
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
    prevItemClicked() {
      this.$bus.$emit('SELECT_TABLE_ROW', -1);
    },

    nextItemClicked() {
      this.$bus.$emit('SELECT_TABLE_ROW', 1);
    },

    onHomeClick() {
      // Restore everything
      if (this.selectedIdx !== -1) {
        this.selectedIdx = -1;
        this.$bus.$emit('DISPLAY_MARKER_DETAILS', {});
        this.$store.commit('traffic/SHOW_PANEL', false);
      }
    },

    cardClicked(payload) {
      this.trafficInfoShow = false;
      this.selectedIdx = payload.idx;
      if (this.selectedIdx < 0) {
        this.$store.commit('traffic/SHOW_PANEL', false);
        setTimeout(() => {
          this.$bus.$emit('HOME_CENTER_MAP');
        }, 200);
      } else {
        this.showPanelIfNot();
      }

      this.cardData = payload.cardData;
      this.currentTitle =
        this.selectedIdx >= 0 && this.cardData[this.selectedIdx] ? this.cardData[this.selectedIdx].title : '';
    },

    playVideo(id) {
      const url = getVideoUrl(id);
      if (url) {
        if (this.$refs.vpRef) {
          this.$refs.vpRef.changeVideoSource(url);
          this.$refs.vpRef.changeTitle(id);
          this.$refs.vpRef.changeCaption('LIVE');
          this.$refs.vpRef.changeIcon('mdi-video');
        }
        this.showVideoPlayer = true;
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

    segmentOptions(segment) {
      const color =
        segment.id === this.selectedSegmentId ? this.selectedColor : Utils.getStrokeColor(segment.travelTime.level);
      const zIndex = segment.id === this.selectedSegmentId ? 2 : 1;
      return { ...this.defaultSegmentOptions, strokeColor: color, zIndex };
    },

    showScrollBar(show) {
      let html = document.getElementsByTagName('html')[0];
      html.style.overflowY = show ? 'auto' : 'hidden';
    },

    showPanelIfNot() {
      if (!this.$store.state.traffic.showPanel) {
        this.$store.commit('traffic/SHOW_PANEL', true);
      }
    },

    handleMarkerClick(type, id) {
      this.trafficInfoShow = true;
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
        case 10:
          marker = this.trafficCameras.find(m => m.id === id);
          this.cameraMarkerClicked(marker);
          break;
        default:
          console.log('Unimplemented Marker Click Case');
          break;
      }
    },

    markerClicked(marker) {
      this.trafficInfoShow = true;
      this.showPanelIfNot();
      this.selectedMarker = marker;
      this.currentTitle = 'Traffic Flow Detector';
      if (this.currentComponent !== FlowDataInfo) {
        this.currentComponent = FlowDataInfo;
      } else {
        setTimeout(() => {
          if (this.$refs.refPanelInfo) {
            this.$refs.refPanelInfo.init(marker);
          }
        }, 250);
      }
    },

    segmentClicked(segment) {
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
      this.showPanelIfNot();
      this.selectedMarker = marker;
      this.currentTitle = 'Weather Station';
      if (this.currentComponent !== WeatherDataInfo) {
        this.currentComponent = WeatherDataInfo;
      } else {
        if (this.$refs.refPanelInfo) {
          setTimeout(() => {
            if (this.$refs.refPanelInfo) {
              this.$refs.refPanelInfo.init(marker);
            }
          }, 250);
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
      this.showPanelIfNot();
      this.selectedMarker = segment;
      this.currentTitle = 'Current Incident';
      if (this.currentComponent !== IncidentDataInfo) {
        this.currentComponent = IncidentDataInfo;
      } else {
        if (this.$refs.refPanelInfo) {
          this.$refs.refPanelInfo.init(segment);
        }
      }
      this.$bus.$emit('CENTER_INCIDENT_SEGMENT', segment);
    },

    cameraMarkerClicked(camera) {
      if (this.showPanel && this.currentComponent === CameraMultiView) {
        this.addCameraToView(camera);
      } else {
        this.playVideo(camera.id);
      }
    },

    addCameraToView(camera) {
      this.showPanelIfNot();
      this.selectedMarker = camera;
      this.currentTitle = 'Traffic Cameras';
      if (this.currentComponent !== CameraMultiView) {
        this.currentComponent = CameraMultiView;
      }
      this.$bus.$emit('ADD_CAMERA_TO_VIEW', camera.id);
    },

    showCameraMultiView() {
      this.trafficInfoShow = true;
      this.showPanelIfNot();
      this.currentTitle = 'Traffic Cameras';
      if (this.currentComponent !== CameraMultiView) {
        this.currentComponent = CameraMultiView;
      }
    },

    showBottomSheet() {
      this.sheet = !this.sheet;
    },

    getStrokeColor(level) {
      const colors = ['#808080', '#0000FF', '#339900', '#00FF33', '#D7DF01', '#FFCC55', '#FF6600', '#FF0000'];
      return colors[level];
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
        //this.$store.dispatch('setSystemStatus', { text: msg, color: 'orange darken-2' });
      }
    },

    showSelectionDialog() {
      if (this.$refs.selectionDialog) {
        this.$refs.selectionDialog.init({
          flowDetectors: this.deviceLocations,
          bluetoothSensors: this.bluetoothLocations,
          weatherStations: this.weatherStations,
          restrictions: this.restrictions,
          anomalySegments: this.currentAnomalySegments
        });
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
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchBluetoothAnomalyDevices() {
      try {
        Api.fetchBluetoothAnomalyDevices().then(response => {
          let data = response.data;
          this.bluetoothLocations = data.map(obj => ({ ...obj, status: 0 }));
        });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    fetchSensorLocations() {
      this.fetchDevices();
      this.fetchBluetoothAnomalyDevices();
      this.fetchTrafficCameras();
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
          this.$store.commit('dashboard/SET_TRAFFIC_INCIDENTS', anomalySegments.data.data);
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
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

    ...mapActions('traffic', ['fetchWeatherStations', 'fetchTrafficCameras'])
  }
};
</script>
<style>
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

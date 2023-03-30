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
          <v-chip color="teal" outlined small>
            <span class="white--text">{{ selection }}</span>
          </v-chip>
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
      ref="trafficDashboardMap"
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
    <ChartDialog ref="chartDialog" v-model="showChartDialog" />
    <VMSDisplayDialog ref="vmsDialog" v-model="showVMSDialog" />
    <VideoPlayerDialog ref="vpRef" videoType="application/x-mpegURL" v-model="showVideoPlayer" />
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import { gmapApi } from 'vue2-google-maps';
import Api from '@/utils/api/traffic';
import Constants from '@/utils/constants/traffic';
import DashboardConstants from '@/utils/constants/dashboard.js';

import ChartDialog from '@/components/modules/traffic/common/ChartDialog';
import RightPanel from '@/components/modules/traffic/common/RightPanel';
import InfoWindow from '@/components/modules/traffic/dashboard/InfoWindow';
import VMSDisplayDialog from '@/components/modules/traffic/common/VMSDisplayDialog';

import Toolbar from '@/components/modules/traffic/dashboard/Toolbar';
import SelectionDialog from '@/components/modules/traffic/dashboard/SelectionDialog';

import FlowDataInfo from '@/components/modules/traffic/dashboard/FlowDataInfo';
import BluetoothDataInfo from '@/components/modules/traffic/dashboard/BluetoothDataInfo';
import WeatherDataInfo from '@/components/modules/traffic/dashboard/WeatherDataInfo';
import IncidentDataInfo from '@/components/modules/traffic/dashboard/IncidentDataInfo';
import CameraMultiView from '@/components/modules/traffic/dashboard/CameraMultiView';

import SensorErrorInfo from '@/components/modules/status/SensorErrorInfo';
import CongestedRouteInfo from '@/components/modules/traffic/dashboard/CongestedRouteInfo';
import SignalIssueInfo from '@/components/modules/traffic/dashboard/SignalIssueInfo';
import WazeAlertInfo from '@/components/modules/traffic/dashboard/WazeAlertInfo';
import RestrictionDataInfo from '@/components/modules/traffic/dashboard/RestrictionDataInfo';

import Utils from '@/utils/Utils';
import InfoColumn from '@/components/modules/dashboard/InfoColumn.vue';
import DashboardInfoOverlay from '@/components/modules/dashboard/DashboardInfoOverlay.vue';
import TrafficDashboardMap from '@/components/modules/traffic/dashboard/TrafficDashboardMap.vue';
import BottomDataDisplay from '@/components/modules/traffic/common/BottomDataDisplay.vue';
import VideoPlayerDialog from '@/components/modules/traffic/common/VideoPlayerDialog';
import { getVideoUrl } from '@/utils/DeldotVideoUrl';

export default {
  components: {
    Toolbar,
    RightPanel,
    InfoWindow,
    ChartDialog,
    VMSDisplayDialog,
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
    VideoPlayerDialog,
    SensorErrorInfo,
    CongestedRouteInfo,
    SignalIssueInfo,
    WazeAlertInfo,
    RestrictionDataInfo
  },

  data: () => ({
    trafficInfoShow: false,
    showChartDialog: false,
    showVideoPlayer: false,
    showVMSDialog: false,
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
        return this.deviceLocations.filter(location => location.region === this.mapRegionSelection);
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
      } else if (this.currentComponent === SensorErrorInfo) {
        return { marker: this.selectedMarker };
      } else if (this.currentComponent === CongestedRouteInfo) {
        return { segment: this.selectedMarker };
      } else if (this.currentComponent === SignalIssueInfo) {
        return { signal: this.selectedMarker };
      } else if (this.currentComponent === WazeAlertInfo) {
        return { waze: this.selectedMarker };
      } else if (this.currentComponent === RestrictionDataInfo) {
        return { restriction: this.selectedMarker };
      }

      return {};
    },

    entities() {
      return this.generateSearchEntities();
    },

    selection() {
      return this.selectedIdx != -1 ? this.getCurrentSelection(this.selectedIdx) : '';
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', [
      'showPanel',
      'currentTravelTimeAnomaly',
      'currentFlowAnomaly',
      'currentWeather',
      'currentRestrictions',
      'weatherStations',
      'trafficCameras',
      'mapRegionSelection',
      'mapLayersSelection',
      'incidentSettings'
    ]),
    ...mapState('dashboard', [
      'trafficIncidents',
      'trafficRestrictions',
      'signalIssues',
      'deviceAnomalies',
      'congestedSegments',
      'wazeAlerts',
      'selectedTrafficIncident',
      'selectedTrafficDevice',
      'selectedRestriction',
      'selectedSignalIssue',
      'selectedAnomalyDevice',
      'selectedCongestedSegment',
      'selectedWazeAlert'
    ])
  },

  watch: {
    showPanel(show) {
      if (!show) {
        // Destroy multi camera view to save internet bandwidth
        if (this.currentComponent === CameraMultiView) {
          this.currentComponent = null;
        }

        // Clear maker selection
        this.clearMarkerSelection();
      }
    },

    isCameraView() {
      return this.currentComponent === CameraMultiView;
    },

    currentFlowAnomaly: function(value) {
      if (value) {
        this.updateMarkerStatus(value);
      }
    },

    currentTravelTimeAnomaly: function(value) {
      if (value) {
        this.updateBluetoothSegmentStatus(value);
      }
    },

    currentWeather: function(value) {
      if (value) {
        this.updateWeatherStatus(value);
      }
    },

    currentRestrictions: function(value) {
      if (value) {
        this.updateRestrictionsStatus(value);
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

    this.$bus.$on('DISPLAY_MARKER_DETAILS', ({ id, type, trigger }) => {
      if (this.selectedIdx === -1) {
        this.handleMarkerClick(type, id, trigger);
      } else {
        this.handleAnomalyMarkerClick(type, id);
      }
    });

    this.$bus.$on('SHOW_CHART_DATA', ({ title, data }) => {
      this.showChart(title, data);
    });

    this.$bus.$on('PLAY_POPUP_VIDEO', id => {
      this.playVideo(id);
    });

    this.$bus.$on('SHOW_POPUP_VMS', vms => {
      this.showVMS(vms);
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });
  },

  created() {
    window.addEventListener('keydown', this.keydownListener);
    this.$store.commit('traffic/SHOW_PANEL', false);
  },

  destroyed: function() {
    window.removeEventListener('keydown', this.keydownListener);
    this.showScrollBar(true);
  },

  methods: {
    keydownListener(e) {
      if (e.key === 'ArrowUp') {
        this.prevItemClicked();
      } else if (e.key === 'ArrowDown') {
        this.nextItemClicked();
      }
    },

    prevItemClicked() {
      this.selectSelection(this.selectedIdx, -1);
    },

    nextItemClicked() {
      this.selectSelection(this.selectedIdx, 1);
    },

    getCurrentSelection(selectIdx) {
      let objectList = null;
      let selectedObject = null;

      switch (selectIdx) {
        case DashboardConstants.CARD_DATA_INCIDENTS_ID:
          objectList = this.trafficIncidents;
          selectedObject = this.selectedTrafficIncident;
          break;
        case DashboardConstants.CARD_DATA_RESTRICTIONS_ID:
          objectList = this.trafficRestrictions;
          selectedObject = this.selectedRestriction;
          break;
        case DashboardConstants.CARD_DATA_SIGNAL_ISSUES_ID:
          objectList = this.signalIssues;
          selectedObject = this.selectedSignalIssue;
          break;
        case DashboardConstants.CARD_DATA_DEDVICE_ANOMALIES_ID:
          objectList = this.deviceAnomalies;
          selectedObject = this.selectedAnomalyDevice;
          break;
        case DashboardConstants.CARD_DATA_CONGESTED_ROUTES_ID:
          objectList = this.congestedSegments;
          selectedObject = this.selectedCongestedSegment;
          break;
        case DashboardConstants.CARD_DATA_WAZE_ALERTS_ID:
          objectList = this.wazeAlerts;
          selectedObject = this.selectedWazeAlert;
          break;
      }

      const total = objectList ? objectList.length : 0;
      const index = objectList && selectedObject ? objectList.findIndex(item => item.id === selectedObject.id) : 0;
      let result = `${index + 1} of ${total}`;

      return result;
    },

    anomalyMarkerSelection(selectIdx, data) {
      if (selectIdx === -1 || !data) {
        return;
      }

      switch (selectIdx) {
        case DashboardConstants.CARD_DATA_INCIDENTS_ID:
          this.$store.commit('dashboard/SET_SELECTED_TRAFFIC_INCIDENT', data);
          break;
        case DashboardConstants.CARD_DATA_RESTRICTIONS_ID:
          this.$store.commit('dashboard/SET_SELECTED_TRAFFIC_RESTRICTION', data);
          break;
        case DashboardConstants.CARD_DATA_SIGNAL_ISSUES_ID:
          this.$store.commit('dashboard/SET_SELECTED_SIGNAL_ISSUE', data);
          break;
        case DashboardConstants.CARD_DATA_DEDVICE_ANOMALIES_ID:
          this.$store.commit('dashboard/SET_SELECTED_ANOMALY_DEVICE', data);
          break;
        case DashboardConstants.CARD_DATA_CONGESTED_ROUTES_ID:
          this.$store.commit('dashboard/SET_SELECTED_CONGESTED_SEGMENT', data);
          break;
        case DashboardConstants.CARD_DATA_WAZE_ALERTS_ID:
          this.$store.commit('dashboard/SET_SELECTED_WAZE_ALERT', data);
          break;
      }
    },

    selectSelection(selectIdx, dir) {
      if (selectIdx < 0) {
        return;
      }

      let data = null;
      switch (selectIdx) {
        case DashboardConstants.CARD_DATA_INCIDENTS_ID:
          data = this.getNextObject(this.trafficIncidents, this.selectedTrafficIncident, dir);
          break;
        case DashboardConstants.CARD_DATA_RESTRICTIONS_ID:
          data = this.getNextObject(this.trafficRestrictions, this.selectedRestriction, dir);
          break;
        case DashboardConstants.CARD_DATA_SIGNAL_ISSUES_ID:
          data = this.getNextObject(this.signalIssues, this.selectedSignalIssue, dir);
          break;
        case DashboardConstants.CARD_DATA_DEDVICE_ANOMALIES_ID:
          data = this.getNextObject(this.deviceAnomalies, this.selectedAnomalyDevice, dir);
          break;
        case DashboardConstants.CARD_DATA_CONGESTED_ROUTES_ID:
          data = this.getNextObject(this.congestedSegments, this.selectedCongestedSegment, dir);
          break;
        case DashboardConstants.CARD_DATA_WAZE_ALERTS_ID:
          data = this.getNextObject(this.wazeAlerts, this.selectedWazeAlert, dir);
          break;
      }

      this.anomalyMarkerSelection(selectIdx, data);
    },

    getNextObject(objectList, selectedObject, dir) {
      if (!objectList || objectList.length === 0) {
        return null;
      }

      const currentIndex = selectedObject ? objectList.findIndex(item => item.id === selectedObject.id) : -1;
      let idx = 0;
      if (dir > 0) {
        idx = (currentIndex + 1) % objectList.length;
      } else {
        idx = currentIndex <= 0 ? objectList.length - 1 : currentIndex - 1;
      }

      return objectList[idx];
    },

    clearMarkerSelection() {
      if (this.$refs.trafficDashboardMap) {
        this.$refs.trafficDashboardMap.clearMarkerSelection();
      }
    },

    onHomeClick() {
      // Restore everything
      if (this.selectedIdx !== -1) {
        this.selectedIdx = -1;
        this.clearMarkerSelection();
        this.$bus.$emit('DISPLAY_MARKER_DETAILS', {});
        this.$store.commit('traffic/SHOW_PANEL', false);
      }
    },

    cardClicked(payload) {
      this.trafficInfoShow = false;
      this.$store.commit('dashboard/SHOW_TABLE', false);

      this.selectedIdx = payload.idx;
      if (this.selectedIdx < 0) {
        this.clearMarkerSelection();

        // Only hide the right now when the view is not a camera view
        if (!this.isCameraView) {
          this.$store.commit('traffic/SHOW_PANEL', false);
        }

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

    showChart(title, data) {
      this.showChartDialog = true;
      this.$refs.chartDialog.init(title, data);
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

    showVMS(vms) {
      if (this.$refs.vmsDialog) {
        this.$refs.vmsDialog.init(vms);
      }
      this.showVMSDialog = true;
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

    handleMarkerClick(type, id, trigger) {
      this.trafficInfoShow = true;
      let marker = null;
      switch (type) {
        case Constants.LAYER_DEVICE_TRAFFIC: // detectors
          marker = this.markers.find(m => m.id === id);
          if (marker) {
            this.markerClicked(marker);
          }
          break;
        case Constants.LAYER_DEVICE_BLUETOOTH: // Bluetooth
          marker = this.bluetoothLocations.find(m => m.id === id);
          if (marker) {
            this.segmentClicked(marker);
          }
          break;
        case Constants.LAYER_DEVICE_WEATHER: // weather stations
          marker = this.weatherStations.find(m => m.id === id);
          if (marker) {
            this.weatherMarkerClicked(marker);
          }
          break;
        case Constants.LAYER_DEVICE_RESTRICTIONS: //travel restrictions
          marker = this.trafficRestrictions.find(m => m.id === id);
          if (marker) {
            this.restrictionClicked(marker);
          }
          break;
        case Constants.LAYER_DEVICE_INCIDENTS: // incident
          marker = this.trafficIncidents.find(m => m.id === id);
          if (marker) {
            this.trafficIncidentClicked(marker, trigger);
          }
          break;
        case Constants.LAYER_DEVICE_SIGNALS: // Signal Issues
          marker = this.signalIssues.find(m => m.id === id);
          if (marker) {
            this.signalIssueClicked(marker);
          }
          break;
        case Constants.LAYER_DEVICE_WAZE_ALERTS: // Waze Alerts
          marker = this.wazeAlerts.find(m => m.id === id);
          if (marker) {
            this.wazeAlertClicked(marker);
          }
          break;
        case Constants.LAYER_DEVICE_CONGESTED_SEGMENTS: // Congested Routes
          marker = this.congestedSegments.find(m => m.id === id);
          if (marker) {
            this.congestedRouteClicked(marker);
          }
          break;
        case Constants.LAYER_DEVICE_ANOMALY_DEVICES: // Device Anomalies
          marker = this.deviceAnomalies.find(m => m.id === id);
          if (marker) {
            this.anomlyDeviceClicked(marker);
          }
          break;
        case Constants.LAYER_DEVICE_CAMERAS: // cameras
          marker = this.trafficCameras.find(m => m.id === id);
          if (marker) {
            this.cameraMarkerClicked(marker);
          }
          break;
        default:
          console.log('Unimplemented Marker Click Case');
          break;
      }
    },

    handleAnomalyMarkerClick(type, id) {
      let marker = null;
      switch (type) {
        case Constants.LAYER_DEVICE_RESTRICTIONS: // travel restrictions
          marker = this.trafficRestrictions.find(m => m.id === id);
          this.anomalyMarkerSelection(DashboardConstants.CARD_DATA_RESTRICTIONS_ID, marker);
          break;
        case Constants.LAYER_DEVICE_INCIDENTS: // incident
          marker = this.trafficIncidents.find(m => m.id === id);
          this.anomalyMarkerSelection(DashboardConstants.CARD_DATA_INCIDENTS_ID, marker);
          break;
        case Constants.LAYER_DEVICE_SIGNALS: // Signal Issues
          marker = this.signalIssues.find(m => m.id === id);
          this.anomalyMarkerSelection(DashboardConstants.CARD_DATA_SIGNAL_ISSUES_ID, marker);
          break;
        case Constants.LAYER_DEVICE_WAZE_ALERTS: // Waze Alerts
          marker = this.wazeAlerts.find(m => m.id === id);
          this.anomalyMarkerSelection(DashboardConstants.CARD_DATA_WAZE_ALERTS_ID, marker);
          break;
        case Constants.LAYER_DEVICE_CONGESTED_SEGMENTS: // Congested Routes
          marker = this.congestedSegments.find(m => m.id === id);
          this.anomalyMarkerSelection(DashboardConstants.CARD_DATA_CONGESTED_ROUTES_ID, marker);
          break;
        case Constants.LAYER_DEVICE_ANOMALY_DEVICES: // Device Anomalies
          marker = this.deviceAnomalies.find(m => m.id === id);
          this.anomalyMarkerSelection(DashboardConstants.CARD_DATA_DEDVICE_ANOMALIES_ID, marker);
          break;
        default:
          console.log('Unimplemented anomaly Marker Click Case');
          break;
      }
    },

    showInfoPanel(marker, title, component) {
      this.showPanelIfNot();
      this.selectedMarker = marker;
      this.currentTitle = title;
      if (this.currentComponent !== component) {
        this.currentComponent = component;
      } else {
        setTimeout(() => {
          if (this.$refs.refPanelInfo) {
            this.$refs.refPanelInfo.init(marker);
          }
        }, 250);
      }
    },

    markerClicked(marker) {
      this.trafficInfoShow = true;
      this.showInfoPanel(marker, 'Traffic Flow Detector', FlowDataInfo);
    },

    segmentClicked(segment) {
      this.showInfoPanel(segment, 'Travel Time', BluetoothDataInfo);
    },

    weatherMarkerClicked(marker) {
      this.showInfoPanel(marker, 'Weather Station', WeatherDataInfo);
    },

    trafficIncidentClicked(marker, trigger) {
      this.showInfoPanel(marker, 'Current Incident', IncidentDataInfo);
      if (trigger && this.$refs.trafficDashboardMap) {
        setTimeout(() => {
          this.$refs.trafficDashboardMap.incidentClicked(marker);
        }, 500);
      }
    },

    restrictionClicked(marker) {
      this.showInfoPanel(marker, 'Restriction', RestrictionDataInfo);
    },

    signalIssueClicked(marker) {
      this.showInfoPanel(marker, 'Signal Issue', SignalIssueInfo);
    },

    wazeAlertClicked(marker) {
      this.showInfoPanel(marker, 'Waze Alert', WazeAlertInfo);
    },

    congestedRouteClicked(segment) {
      this.showInfoPanel(segment, 'Congested Route', CongestedRouteInfo);
    },

    anomlyDeviceClicked(marker) {
      this.showInfoPanel(marker, 'Device Anomaly', SensorErrorInfo);
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

    updateBluetoothSegmentStatus(data) {
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
      data.forEach(d => {
        const index = this.restrictions.findIndex(r => r.id === d.id);
        if (index != -1) {
          this.restrictions[index] = d;
        } else {
          this.restrictions.push(d);
        }
      });
    },

    showSelectionDialog() {
      if (this.$refs.selectionDialog) {
        this.$refs.selectionDialog.init({
          flowDetectors: this.deviceLocations,
          bluetoothSensors: this.bluetoothLocations,
          weatherStations: this.weatherStations,
          restrictions: this.restrictions,
          incidents: this.trafficIncidents
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

      const items5 = this.trafficIncidents.map(d => ({
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

<template>
  <div class="bluetooth-dashboard">
    <BluetoothMap />
    <Toolbar
      :searchItems="$store.state.bluetooth.apiData.segments"
      :fetchDone="fetchDone"
      v-show="mapLayerSelection && $store.state.bluetooth.map"
    />
    <TrafficRouting />
    <PlaybackToolbar />
    <v-scale-transition>
      <ToolbarAddTT v-show="$store.state.bluetooth.modes.addFromMap" />
    </v-scale-transition>
    <v-tooltip left>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          v-bind="attrs"
          v-on="on"
          @click="resetMapZoom"
          fab
          small
          style="position: absolute; top: 76vh; right: 10px"
        >
          <v-icon>mdi-home-outline</v-icon>
        </v-btn>
      </template>
      <span>Reset Map</span>
    </v-tooltip>
    <v-tooltip left>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          v-bind="attrs"
          v-on="on"
          fab
          small
          style="position: absolute; top: 82vh; right: 10px"
          @click="changeZoom(1)"
        >
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </template>
      <span>Zoom in</span>
    </v-tooltip>
    <v-tooltip left>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          v-bind="attrs"
          v-on="on"
          fab
          small
          style="position: absolute; top: 88vh; right: 10px"
          @click="changeZoom(-1)"
        >
          <v-icon>mdi-minus</v-icon>
        </v-btn>
      </template>
      <span>Zoom out</span>
    </v-tooltip>
    <!-- Overlay -->
    <Dialogs />
    <!-- Notifications -->
    <Notifications />
  </div>
</template>

<script>
import { MarkerClusterer } from "@googlemaps/markerclusterer";
import { mapState } from 'vuex';
// Pages
import TrafficRouting from '@/views/bluetooth/TrafficRouting';
// UI
import BluetoothMap from '@/components/modules/bluetooth/ui/BluetoothMap';
import Notifications from '@/components/modules/bluetooth/ui/Notifications';
import Toolbar from '@/components/modules/bluetooth/ui/Toolbar.vue';
import Dialogs from '@/components/modules/bluetooth/dialogs/Dialogs.vue';
import ToolbarAddTT from '@/components/modules/bluetooth/ui/ToolbarAddTT.vue';
import PlaybackToolbar from '@/components/modules/bluetooth/ui/PlaybackToolbar.vue';
// Utils
import Utils from '@/utils/Utils';
/* import MapOption from '@/utils/MapOption.js'; */
import * as infoWindows from '@/utils/InfoWindows.js';
// Custom Event Bus (yang)
import { eventBus } from '@/utils/EventBusBT.js';

/* eslint-disable no-undef */

export default {
  name: 'App',
  components: {
    /* UI */
    BluetoothMap,
    Notifications,
    Dialogs,
    Toolbar,
    ToolbarAddTT,
    PlaybackToolbar,
    /* Pages */
    TrafficRouting
  },
  data() {
    return {
      showTrafficRouting: false,
      showLabels: null,
      selectionMode: false,
      bbox: null,
      bbox1: [39.74, -75.79],
      bbox2: [39.6, -75.525],
    };
  },
  beforeDestroy() {
    if (this.updateInterval) {
      clearInterval(this.updateInterval)
    }
  },
  mounted() {
    this.refreshMapObjects()
    /* Update every 5 minutes*/
    this.updateInterval = setInterval(() => {
      if (this.fetchDone && this.$store.state.bluetooth.autoUpdate) {
        this.$store.state.bluetooth.timeSinceUpdate++;
        if (this.$store.state.bluetooth.timeSinceUpdate == 300) {
          this.$store.state.bluetooth.timeSinceUpdate = 0
          this.$store.state.currentDate = new Date()
        }
      }
    }, 1000);
    /* Other */
    /* this.showLabels = this.mapLayerSelection.includes(6); */
    const vm = this;
    window.getAdditionalInfo = function(data) {
      vm.getAdditionalInfoMain(data);
    };
    window.getAdditionalDeviceInfo = function(data) {
      vm.getAdditionalDeviceInfo(data);
    };

    this.$bus.$on('RESET_TO_SELECTED_TIME', date => { this.resetSegmentsAndWaze(date) });
    this.$bus.$on('UPDATE_TIMELINE', date => { this.updateTimeline(date) });
    this.$bus.$on('DISPLAY_MARKER_DETAILS', item => { this.handleMarkerClick(item) });
    this.$bus.$on('SHOW_SELECTION_POPUP', id => { this.showSelectionDialog(id) });
    this.$bus.$on('GO_TO_SEGMENT_LOCATION', seg => { this.goToSegmentLocation(seg.info.coordinates, seg.info.name) });
    this.$bus.$on('GO_TO_MARKER_LOCATION', (obj, markerType) => { this.goToMarkerLocation(obj, markerType) });
    this.$bus.$on('CREATE_SEGMENTS', () => { this.createSegments() });
    this.$bus.$on('CREATE_DEVICES', () => { this.createDevices() });
    this.$bus.$on('CREATE_WAZE_ALERTS', () => { this.createWazeAlerts() });
    this.$bus.$on('ADD_SEGMENTS', () => { this.addSegments(); });
    this.$bus.$on('ADD_MARKERS', markers => { this.addMarkers(markers) });
    this.$bus.$on('ADD_WAZE_CLUSTERS', () => { this.addWazeClusters() });
  },
  methods: {
    resetSegmentsAndWaze(time) {
      console.log('Reset to selected time:\n %o', time);
      this.createSegments();
      if (this.mapLayerSelection.includes(0)) this.addSegments();
        this.createWazeAlerts();
      if (this.isWazeMarkers) {
        this.addMarkers(this.filteredWazeMarkers);
      }
      if (this.isWazeClusters) 
        this.addWazeClusters();
    },
    updateTimeline(date) {
      this.changeDeviceLevels(date);

      this.createWazeAlerts(60, date);
      if (this.isWazeMarkers) {
        this.addMarkers(this.filteredWazeMarkers);
      }
      if (this.isWazeClusters)
        this.addWazeClusters();

      this.changeSegmentLevels(date);
    },
    changeDeviceLevels(date) {
      const devicesFull = this.$store.state.bluetooth.apiData.devicesFull;
      const deviceMarkers = this.$store.state.bluetooth.deviceMarkers;
      if (devicesFull && devicesFull.length > 0) {
        devicesFull.forEach(d => {
          let device = {
            county: d.county,
            deviceId: d.deviceId,
            flowData: d.flowData,
            lanes: d.lane,
            lat: d.lat,
            lng: d.lng,
            stationCount: d.stationCount,
            title: d.title,
            type: d.type,
            uid: d.uid
          };
          if (d.flowData) {
            d.flowData.slice().forEach(t => {
              let timelineTS = t.createdTime;
              if (timelineTS < date.ts && timelineTS > date.ts - 10 * 60 * 1000) {
                device.status = t.status;
                return;
              }
            });
            deviceMarkers.forEach(m => {
              if (m.title == d.title) {
                if (device.status && m.zIndex != device.status.level) {
                  let clickWindow = new google.maps.InfoWindow(infoWindows.getDevCW(device));
                  m.setOptions({
                    zIndex: device.status.level,
                    strokeColor: device.status.color,
                    clickWindow: clickWindow
                  });
                }
                return;
              }
            });
          }
        });
      }
    },
    changeSegmentLevels(date) {
      const segmentsFull = this.$store.state.bluetooth.apiData.segmentsFull;
      const segmentPolylines = this.$store.state.bluetooth.segmentPolylines;
      if (segmentsFull && segmentsFull.length > 0) {
        segmentsFull.forEach(s => {
          let segment = {
            info: s.info,
            travelTime: null
          };
          if (s.timeline) {
            s.timeline
              .slice()
              .reverse()
              .forEach(t => {
                let timelineTS = t.calculationTimestamp.value;
                if (timelineTS < date.ts && timelineTS > date.ts - 10 * 60 * 1000) {
                  segment.travelTime = t;
                  return;
                }
              });
            segmentPolylines.forEach(p => {
              if (p.title == s.info.name) {
                let level = segment.travelTime ? segment.travelTime.level : -3;
                let clickWindow = new google.maps.InfoWindow(infoWindows.getSegCW(segment));
                p.setOptions({ zIndex: level, strokeColor: Utils.getStrokeColor(level), clickWindow: clickWindow });
                return;
              }
            });
          }
        });
      }
    },
    resetMapZoom() {
      this.$store.state.bluetooth.map.setCenter({ lat: 39.14, lng: -75.5 });
      this.$store.state.bluetooth.map.setZoom(9);
      this.$store.state.bluetooth.prefs.map.center = { lat: 39.14, lng: -75.5 };
      this.$store.state.bluetooth.prefs.map.zoom = 9;
    },
    changeZoom(val) {
      let map = this.$store.state.bluetooth.map;
      let newZoom = map.getZoom() + val;
      map.setZoom(newZoom);
    },
    handleMarkerClick(item) {
      this.goToSegmentLocation(item.info.coordinates, item.info.name);
    },
    showSelectionDialog(id) {
      let d = this.$store.state.bluetooth.dialog;
      switch (id) {
        case 0:
          d.congestion = true;
          break;
        case 1:
          d.btSensors = true;
          break;
        case 2:
          d.devices = true;
          break;
        case 3:
          d.wazeAlerts = true;
          break;
        default:
          alert('Unimplemented');
          break;
      }
    },
    initMapBB() {
      this.geocoder = new google.maps.Geocoder();
      this.bbox = new google.maps.Rectangle({
        strokeColor: 'orange',
        strokeOpacity: 1,
        strokeWeight: 4,
        fillColor: 'orange',
        fillOpacity: 0,
        zIndex: 99,
        bounds: {
          north: this.bbox2[0],
          south: this.bbox1[0],
          east: this.bbox2[1],
          west: this.bbox1[1]
        }
      });
      google.maps.event.addListener(this.bbox, 'click', event => {
        console.log(event.latLng);
        let Lat = event.latLng.lat();
        let Lng = event.latLng.lng();
        console.log(Lat);
        console.log(Lng);
        let LatLngStr = `${Lat},${Lng}`;
        console.log(LatLngStr);
        if (this.clickIndex == 0) {
          this.clickIndex = 1;
          this.updateSourceMarker(event.latLng);
          eventBus.$emit('newSource', LatLngStr);
        } else if (this.clickIndex == 1) {
          this.clickIndex = 0;
          this.endLatLng = [Lat + 0.004, Lng];
          this.updateDestMarker(event.latLng);
          eventBus.$emit('newDest', LatLngStr);
        }
      });
    },
    addBB() {
      if (this.bbox) {
        this.showTrafficRouting = true;
        let poi = this.bbox.bounds.getCenter().toJSON();
        poi.lat -= 0.002;
        poi.lng -= 0.055;
        this.$store.state.bluetooth.map.setCenter(poi);
        this.$store.state.bluetooth.map.setZoom(12);
        this.bbox.setMap(this.$store.state.bluetooth.map);
      }
    },
    removeBB() {
      if (this.bbox) {
        this.showTrafficRouting = false;
        this.bbox.setMap(null);
      }
      this.clearRoutingComponent();
    },
    refreshMapObjects() {
      let layers = this.mapLayerSelection;
      if (layers.includes(0)) this.addSegments();
      if (layers.includes(1)) this.addMarkers(this.$store.state.bluetooth.sensorMarkers);
      if (layers.includes(2)) this.addMarkers(this.filteredDeviceMarkers);
      if (this.isWazeMarkers) {
        this.addMarkers(this.filteredWazeMarkers);
      }
      if (this.isWazeClusters) this.addWazeClusters();
    },
    /* Segments Methods */
    createSegments() {
      this.removeSegments();
      this.$store.state.bluetooth.segmentPolylines = [];
      this.$store.state.bluetooth.apiData.segments.forEach(s => {
        this.createSegment(s, this.$store.state.bluetooth.map);
      });
    },
    createSegment(data, map) {
      var clickWindow = new google.maps.InfoWindow(infoWindows.getSegCW(data));
      var currPath = [];
      data.info.coordinates.forEach(c => currPath.push({ lat: c.lat, lng: c.lng }));
      var path = new google.maps.Polyline({
        path: currPath,
        title: data.info.name,
        geodesic: true,
        strokeColor: Utils.getStrokeColor(data.travelTime.level),
        zIndex: data.travelTime.level,
        strokeOpacity: 0.8,
        strokeWeight: map.getZoom() / 3,
        clickWindow: clickWindow,
        data: data
      });
      this.$store.state.bluetooth.segmentPolylines.push(path);
      google.maps.event.addListener(path, 'click', event => {
        if (this.$store.state.bluetooth.modes.addFromMap) {
          if (!this.$store.state.bluetooth.multigraphSegs) {
            this.$store.state.bluetooth.multigraphSegs = []
          } 
          if (path.strokeColor != 'purple') {
            path.setOptions({ strokeWeight: 10, strokeOpacity: 1, strokeColor: 'purple' });
            this.$store.state.bluetooth.multigraphSegs.push(data);
          } else {
            let strokeColor = Utils.getStrokeColor(data.travelTime.level);
            path.setOptions({ strokeWeight: map.getZoom() / 3, strokeOpacity: 0.8, strokeColor: strokeColor });
            this.$store.state.bluetooth.multigraphSegs = this.$store.state.bluetooth.multigraphSegs.filter(s => s.info.description != data.info.description);
          }
        } else {
          path.clickWindow.setOptions({ position: event.latLng });
          path.clickWindow.open(map);
        }
      });
      google.maps.event.addListener(map, 'zoom_changed', () => {
        let zoom = map.getZoom();
        this.$store.state.bluetooth.prefs.map.zoom = zoom;
        path.setOptions({ strokeWeight: zoom / 3 });
      });
      google.maps.event.addListener(map, 'dragend', () => {
        let center = map.getCenter();
        this.$store.state.bluetooth.prefs.map.center = { lat: center.lat(), lng: center.lng() };
      });
    },
    addSegments() {
      if (this.$store.state.bluetooth.segmentPolylines) {
        this.$store.state.bluetooth.segmentPolylines.forEach(p => {
          p.setMap(this.$store.state.bluetooth.map);
        });
      }
    },
    removeSegments() {
      if (this.$store.state.bluetooth.segmentPolylines) {
        this.$store.state.bluetooth.segmentPolylines.forEach(s => {
          s.setMap(null);
        });
      }
    },
    /* Marker Methods */
    addMarkers(markers) {
      console.log("Add Markers");
      if (markers) {
        markers.forEach(m => {
          m.setMap(this.$store.state.bluetooth.map);
        });
      }
    },
    removeMarkers(markers) {
      if (markers) {
        markers.forEach(m => {
          m.setMap(null);
        });
      }
    },
    /* Sensors Methods */
    createSensors() {
      this.removeMarkers(this.$store.state.bluetooth.sensorMarkers);
      this.$store.state.bluetooth.sensorMarkers = [];
      this.$store.state.bluetooth.apiData.sensors.forEach(s => {
        this.createSensor(s, this.$store.state.bluetooth.map);
      });
    },
    createSensor(data, map) {
      var hoverWindow = new google.maps.InfoWindow({ content: '<div id="content">' + data.node + '</div>' });
      var clickWindow = new google.maps.InfoWindow(infoWindows.getSenCW(data));
      var marker = new google.maps.Marker({
        position: { lat: data.lat, lng: data.lng },
        title: data.node,
        clickWindow: clickWindow,
        icon: {
          size: new google.maps.Size(35, 35),
          scaledSize: new google.maps.Size(35, 35),
          url: 'http://maps.google.com/mapfiles/kml/paddle/blu-circle.png'
        }
      });
      this.$store.state.bluetooth.sensorMarkers.push(marker);
      marker.addListener('mouseover', function() {
        hoverWindow.open(map, marker);
      });
      marker.addListener('mouseout', function() {
        hoverWindow.close();
      });
      google.maps.event.addListener(marker, 'click', function(event) {
        hoverWindow.close();
        clickWindow.setOptions({ position: event.latLng });
        clickWindow.open(map);
      });
    },
    /* Devices Methods */
    createDevices() {
      this.removeMarkers(this.$store.state.bluetooth.deviceMarkers);
      this.$store.state.bluetooth.deviceMarkers = [];
      this.$store.state.bluetooth.apiData.devices.forEach(d => {
        this.createDevice(d, this.$store.state.bluetooth.map);
      });
    },
    createDevice(data, map) {
      /* if (data.status.level <= 2) return */
      var hoverWindow = new google.maps.InfoWindow({ content: '<div id="content">' + data.title + '</div>' });
      var clickWindow = new google.maps.InfoWindow(infoWindows.getDevCW(data));
      /* let deviceTypeId = this.getDeviceTypeId(data.status.level) */
      var marker = new google.maps.Marker({
        /* deviceTypeId: deviceTypeId, */
        level: data.status.level,
        position: { lat: data.lat, lng: data.lng },
        title: data.title,
        clickWindow: clickWindow,
        zIndex: data.status.level,
        icon: {
          size: new google.maps.Size(35, 35),
          scaledSize: new google.maps.Size(35, 35),
          url: this.getDeviceURL(data.status.level)
        }
      });
      this.$store.state.bluetooth.deviceMarkers.push(marker);
      marker.addListener('mouseover', function() {
        hoverWindow.open(map, marker);
      });
      marker.addListener('mouseout', function() {
        hoverWindow.close();
      });
      google.maps.event.addListener(marker, 'click', function(event) {
        hoverWindow.close();
        clickWindow.setOptions({
          position: event.latLng
        });
        clickWindow.open(map);
      });
    },
    getDeviceURL(level) {
      if (level == 1) return require('@/assets/flow-marker-1.png');
      else if (level == 2) return require('@/assets/flow-marker-2.png');
      else if (level == 3) return require('@/assets/flow-marker-3.png');
      else if (level == 4) return require('@/assets/flow-marker-4.png');
      else if (level == 5) return require('@/assets/flow-marker-5.png');
      else if (level == 6) return require('@/assets/flow-marker-6.png');
      else return require('@/assets/flow-marker-0.png');
    },
    /* Waze Data Methods */
    createWazeAlerts(dur = null, timeEnd = null) {
      if (!dur || !timeEnd) {
        this.removeWazeClusters();
        this.removeMarkers(this.$store.state.bluetooth.wazeMarkers);
        this.$store.state.bluetooth.wazeMarkers = [];
        this.$store.state.bluetooth.apiData.waze.forEach(w => {
          this.$store.state.bluetooth.subtoggles.forEach(st => {
            if (w.alertType.name == st) {
              this.createWazeAlert(w, this.$store.state.bluetooth.map);
              return;
            }
          });
        });
      } else {
        /* this.$store.state.bluetooth.wazeMarkers = [] */
        const oldWazeClusters = this.$store.state.bluetooth.wazeClusters;
        let sliceIdx = this.$store.state.bluetooth.wazeMarkers.length;
        let timeStart = timeEnd.minus({ minutes: dur });
        let startTS = timeStart.ts;
        let endTS = timeEnd.ts;
        this.$store.state.bluetooth.apiData.wazeFull
          .filter(w => w.alertTimeTS > startTS && w.alertTimeTS < endTS)
          .forEach(w => {
            this.$store.state.bluetooth.subtoggles.forEach(st => {
              if (w.alertType.name == st) {
                this.createWazeAlert(w, this.$store.state.bluetooth.map);
                return;
              }
            });
          });
        let oldWazeMarkers = this.$store.state.bluetooth.wazeMarkers.slice(0, sliceIdx);
        this.$store.state.bluetooth.wazeMarkers = this.$store.state.bluetooth.wazeMarkers.slice(sliceIdx);
        setTimeout(() => {
          this.removeWazeClusters(oldWazeClusters);
          this.removeMarkers(oldWazeMarkers);
        }, 420);
      }
    },
    createWazeAlert(data, map) {
      let title =
        data.subType && data.subType.name
          ? data.subType.name
          : data.description
          ? data.description
          : data.alertType.name;
      var hoverWindow = new google.maps.InfoWindow({
        content: '<div id="content">' + title + '</div>'
      });
      var clickWindow = new google.maps.InfoWindow(infoWindows.getWazeCW(data));
      let iconURL = this.getIcon(data.alertType.id);
      let wazeTypeId = this.getWazeTypeId(data.alertType.id);
      if (!iconURL) {
        return;
      }
      var marker = new google.maps.Marker({
        wazeTypeId: wazeTypeId,
        position: { lat: data.lat, lng: data.lng },
        title: title,
        clickWindow: clickWindow,
        icon: {
          size: new google.maps.Size(28, 28),
          scaledSize: new google.maps.Size(28, 28),
          url: iconURL
        },
        alertType: data.alertType.name
      });
      this.$store.state.bluetooth.wazeMarkers.push(marker);
      marker.addListener('mouseover', function() {
        hoverWindow.open(map, marker);
      });
      marker.addListener('mouseout', function() {
        hoverWindow.close();
      });
      google.maps.event.addListener(marker, 'click', function(event) {
        hoverWindow.close();
        clickWindow.setOptions({ position: event.latLng });
        clickWindow.open(map);
      });
    },
    getIcon(id) {
      if (id >= 10 && id <= 13)
        // Accident
        return require('@/assets/waze-accident.png');
      else if (id >= 20 && id <= 25)
        // Jam
        return require('@/assets/waze-traffic-jam.png');
      else if (id >= 90 && id <= 91)
        // Construction
        return require('@/assets/waze-construction.png');
      else if (id >= 100 && id <= 104)
        // Closure
        return require('@/assets/waze-road-closed.png');
      // Hazard
      else return require('@/assets/waze-hazard.png');
    },
    getWazeTypeId(id) {
      if (id >= 10 && id <= 13)
        // Accident
        return 3;
      else if (id >= 20 && id <= 25)
        // Jam
        return 1;
      else if (id >= 90 && id <= 91)
        // Construction
        return 4;
      else if (id >= 100 && id <= 104)
        // Closure
        return 2;
      else return 0;
    },
    addWazeClusters() {
      console.log("Add Waze Cluster");
      let map = this.$store.state.bluetooth.map
      if (this.filteredWazeMarkers) {
        let markers = this.filteredWazeMarkers        
        this.$store.state.bluetooth.wazeClusters = new MarkerClusterer({ markers, map }); 
      }
    },
    removeWazeClusters(clusters = null) {
      if (clusters) {
        clusters.clearMarkers();
      } else {
        if (this.$store.state.bluetooth.wazeClusters) {
          this.$store.state.bluetooth.wazeClusters.clearMarkers();
        }
      }
    },
    goToMarkerLocation(obj, markerType) {
      let loc = { lat: obj.lat, lng: obj.lng };
      this.$store.state.bluetooth.map.setCenter(loc);
      this.$store.state.bluetooth.map.setZoom(17);
      if (markerType == 'devices') {
        this.$bus.$emit('CHANGE_LAYER', 2, 'add');
      } else if (markerType == 'sensors') {
        this.$bus.$emit('CHANGE_LAYER', 1, 'add');
      } else if (markerType == 'waze') {
        this.$bus.$emit('CHANGE_LAYER', 3, 'add');
      }
    },
    goToSegmentLocation(coords, name) {
      var bounds = new google.maps.LatLngBounds();
      coords.forEach(c => {
        bounds.extend({ lat: c.lat, lng: c.lng });
      });
      this.$store.state.bluetooth.map.setCenter(bounds.getCenter());
      this.$store.state.bluetooth.map.fitBounds(bounds);
      this.$store.state.bluetooth.segmentPolylines.forEach(p => {
        if (p.title == name) {
          let center = null;
          if (coords.length % 2 == 0) {
            let c = coords[coords.length / 2];
            center = new google.maps.LatLng(c.lat, c.lng);
          } else {
            let c1 = coords[Math.floor(coords.length / 2)];
            let c2 = coords[Math.ceil(coords.length / 2)];
            center = new google.maps.LatLng((c1.lat + c2.lat) / 2, (c1.lng + c2.lng) / 2);
          }
          this.closeAllInfoWIndows();
          p.clickWindow.setOptions({ position: center });
          p.clickWindow.open(this.$store.state.bluetooth.map);
        }
      });
    },
    closeAllInfoWIndows() {
      this.$store.state.bluetooth.segmentPolylines.forEach(p => {
        p.clickWindow.close();
      });
    },
    getAdditionalDeviceInfo(data) {
      console.log('Open Device Dialog: %o', data);
    },
    getAdditionalInfoMain(name) {
      this.$store.state.bluetooth.apiData.segments.forEach(s => {
        if (s.info && s.info.name == name) {
          this.$store.state.bluetooth.selectedSeg.data = s;
          return;
        }
      });
      this.$store.state.bluetooth.dialog.tt = true;
    },
  },
  watch: {
    '$store.state.bluetooth.modes.addFromMap': {
      handler: function(newVal, oldVal) {
        if (newVal && !oldVal) {
          this.$store.state.bluetooth.multigraphSegs = [];
        } else {
          this.$store.state.bluetooth.multigraphSegs.forEach(s => {
            this.$store.state.bluetooth.segmentPolylines.forEach(p => {
              if (p.title == s.info.name) {
                let zoom = this.$store.state.bluetooth.map.getZoom() / 3;
                let strokeColor = Utils.getStrokeColor(s.travelTime.level);
                p.setOptions({ strokeWeight: zoom, strokeOpacity: 0.8, strokeColor: strokeColor });
              }
            });
          });
          this.$store.state.bluetooth.multigraphSegs = null;
        }
      }
    },
    '$store.state.bluetooth.mapLayerSelection': {
      deep: true,
      handler: function(newVal, oldVal) {
        /* console.log('Old\n%o\nNew\n%o', oldVal, newVal); */
        /* Add */
        let addDiff = newVal.filter(x => !oldVal.includes(x));
        if (addDiff.length > 0) {
          /* console.log('Add: %o', addDiff[0]); */
          if (addDiff[0] == 0) this.addSegments();
          else if (addDiff[0] == 1) this.addMarkers(this.$store.state.bluetooth.sensorMarkers);
          else if (addDiff[0] == 2) this.addMarkers(this.filteredDeviceMarkers);
          else if (addDiff[0] == 3) {
            this.removeWazeClusters();
            if (this.isWazeMarkers) {
              this.addMarkers(this.filteredWazeMarkers);
            }
            if (this.mapLayerSelection.includes(4)) {
              this.addWazeClusters();
            }
          } else if (addDiff[0] == 4 && this.isWazeClusters) {
            this.removeMarkers(this.$store.state.bluetooth.wazeMarkers);
            this.addWazeClusters();
          } else if (addDiff[0] == 5) this.addBB();
          else if (addDiff[0] == 6) this.showLabels = true;
        }
        /* Delete */
        let delDiff = oldVal.filter(x => !newVal.includes(x));
        if (delDiff.length > 0) {
          /* console.log('Delete: %o', delDiff[0]); */
          if (delDiff[0] == 0) this.removeSegments();
          else if (delDiff[0] == 1) this.removeMarkers(this.$store.state.bluetooth.sensorMarkers);
          else if (delDiff[0] == 2) this.removeMarkers(this.$store.state.bluetooth.deviceMarkers);
          else if (delDiff[0] == 3) {
            this.removeMarkers(this.$store.state.bluetooth.wazeMarkers);
            this.removeWazeClusters();
          } else if (delDiff[0] == 4) this.removeWazeClusters();
          else if (delDiff[0] == 5) this.removeBB();
          else if (delDiff[0] == 6) this.showLabels = false;
        }
      }
    },
    '$store.state.bluetooth.wazeLayerSelection'() {
      if (this.isWazeMarkers) {
        this.removeMarkers(this.$store.state.bluetooth.wazeMarkers);
        this.addMarkers(this.filteredWazeMarkers);
      } else if (this.isWazeClusters) {
        this.removeWazeClusters();
        this.addWazeClusters();
      }
    },
    '$store.state.bluetooth.deviceLayerSelection'() {
      let mapLayerSelection = this.$store.state.bluetooth.mapLayerSelection;
      if (mapLayerSelection.includes(2)) {
        this.removeMarkers(this.$store.state.bluetooth.deviceMarkers);
        this.addMarkers(this.filteredDeviceMarkers);
      }
    },
  },
  computed: {
    position() {
      return this.$store.state.position;
    },
    filteredWazeMarkers() {
      if (this.$store.state.bluetooth.wazeLayerSelection && this.$store.state.bluetooth.wazeMarkers) {
        let filteredWazeMarkers = this.$store.state.bluetooth.wazeMarkers.filter(w =>
          this.$store.state.bluetooth.wazeLayerSelection.includes(w.wazeTypeId)
        );
        return filteredWazeMarkers;
      } else {
        return this.$store.state.bluetooth.wazeMarkers;
      }
    },
    filteredDeviceMarkers() {
      if (this.$store.state.bluetooth.deviceLayerSelection && this.$store.state.bluetooth.deviceMarkers) {
        let filterLevels = [];
        if (this.$store.state.bluetooth.deviceLayerSelection.includes(0)) {
          filterLevels.push(-1, 0, 1);
        }
        if (this.$store.state.bluetooth.deviceLayerSelection.includes(1)) {
          filterLevels.push(2, 3, 4);
        }
        if (this.$store.state.bluetooth.deviceLayerSelection.includes(2)) {
          filterLevels.push(5, 6);
        }
        let filteredDeviceMarkers = this.$store.state.bluetooth.deviceMarkers.filter(w =>
          filterLevels.includes(w.level)
        );
        return filteredDeviceMarkers;
      } else {
        return this.$store.state.bluetooth.wazeMarkers;
      }
    },
    getPage() {
      return this.$store.state.bluetooth.selectedPage;
    },
    mapLayerSelection() {
      return this.$store.state.bluetooth.mapLayerSelection;
    },
    fetchDone() {
      return (
        !!this.$store.state.bluetooth.apiData.segments &&
        !!this.$store.state.bluetooth.apiData.sensors &&
        !!this.$store.state.bluetooth.apiData.devices &&
        !!this.$store.state.bluetooth.apiData.waze
      );
    },
    isWazeMarkers() {
      return this.mapLayerSelection.includes(3) && !this.mapLayerSelection.includes(4)
    },
    isWazeClusters() {
      return this.mapLayerSelection.includes(3) && this.mapLayerSelection.includes(4)
    },
    ...mapState(['currentDate']),
  }
};
</script>

<style lang="scss">
.main-card {
  padding: 20px;
  height: 88vh;
  overflow-y: auto;
  overflow-x: hidden;
  margin: 0px 12px 0px;
}
.close-button {
  position: absolute;
  right: 0px;
  padding: 10px;
}
.grid-center {
  display: grid;
  place-items: center;
}
.grid-left {
  display: grid;
  place-items: start;
}
.grid-right {
  display: grid;
  place-items: end;
}
</style>

<style lang="scss">
div#content {
  color: black;
}
</style>

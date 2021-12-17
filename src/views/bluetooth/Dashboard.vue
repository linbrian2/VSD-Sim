<template>
  <div class="bluetooth-dashboard">
    <BluetoothMap />
    <Toolbar :searchItems="apiData.segments" :fetchDone="fetchDone" v-show="mapLayerSelection && map" />
    <TrafficRouting />
    <PlaybackToolbar />
    <v-scale-transition>
      <ToolbarAddTT v-show="modes.addFromMap" />
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
  </div>
</template>

<script>
import { MarkerClusterer } from '@googlemaps/markerclusterer';
import { mapState } from 'vuex';
// Pages
import TrafficRouting from '@/views/bluetooth/TrafficRouting';
// UI
import BluetoothMap from '@/components/modules/bluetooth/ui/BluetoothMap';
import Toolbar from '@/components/modules/bluetooth/ui/Toolbar.vue';
import Dialogs from '@/components/modules/bluetooth/dialogs/Dialogs.vue';
import ToolbarAddTT from '@/components/modules/bluetooth/ui/ToolbarAddTT.vue';
import PlaybackToolbar from '@/components/modules/bluetooth/ui/PlaybackToolbar.vue';
// Utils
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/bluetooth';
import * as infoWindows from '@/utils/InfoWindows.js';

/* eslint-disable no-undef */

export default {
  name: 'App',
  components: {
    /* UI */
    BluetoothMap,
    Dialogs,
    Toolbar,
    ToolbarAddTT,
    PlaybackToolbar,
    /* Pages */
    TrafficRouting
  },
  mounted() {
    if (this.map) {
      this.refreshMapObjects();
    }
    /* Other */
    const vm = this;
    window.getAdditionalInfo = function(data) {
      vm.getAdditionalInfoMain(data);
    };

    this.$bus.$on('RESET_TO_SELECTED_TIME', () => {
      this.resetSegmentsAndWaze();
    });
    this.$bus.$on('UPDATE_TIMELINE', date => {
      this.updateTimeline(date);
    });
    this.$bus.$on('BT_DISPLAY_MARKER_DETAILS', item => {
      this.handleMarkerClick(item);
    });
    this.$bus.$on('GO_TO_SEGMENT_LOCATION', seg => {
      this.goToSegmentLocation(seg.info.coordinates, seg.info.name);
    });
    this.$bus.$on('GO_TO_MARKER_LOCATION', (obj, markerType) => {
      this.goToMarkerLocation(obj, markerType);
    });
    this.$bus.$on('CREATE_SEGMENTS', () => {
      this.createSegments();
    });
    this.$bus.$on('CREATE_DEVICES', () => {
      this.createDevices();
    });
    this.$bus.$on('CREATE_WAZE_ALERTS', () => {
      this.createWazeAlerts();
    });
    this.$bus.$on('CREATE_SENSORS', () => {
      this.createSensors();
    });
    this.$bus.$on('ADD_SEGMENTS', () => {
      this.addSegments();
    });
    this.$bus.$on('ADD_MARKERS', markers => {
      this.addMarkers(markers);
    });
    this.$bus.$on('ADD_WAZE_CLUSTERS', () => {
      this.addWazeClusters();
    });
  },
  methods: {
    resetSegmentsAndWaze() {
      this.createSegments();
      if (this.mapLayerSelection.includes(Constants.LAYER_CONGESTION)) {
        this.addSegments();
      }
      this.createWazeAlerts();
      if (this.isWazeMarkers) {
        this.addMarkers(this.filteredWazeMarkers);
      }
      if (this.isWazeClusters) this.addWazeClusters();
    },
    updateTimeline(date) {
      this.changeDeviceLevels(date);

      this.createWazeAlerts(60, date);
      if (this.isWazeMarkers) {
        this.addMarkers(this.filteredWazeMarkers);
      }
      if (this.isWazeClusters) this.addWazeClusters();

      this.changeSegmentLevels(date);
    },
    changeDeviceLevels(date) {
      const devicesFull = this.apiData.devicesFull;
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
            this.deviceMarkers.forEach(m => {
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
      const segmentsFull = this.apiData.segmentsFull;
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
            this.segmentPolylines.forEach(p => {
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
      this.map.setCenter({ lat: 39.14, lng: -75.5 });
      this.map.setZoom(9);
    },
    changeZoom(val) {
      let map = this.map;
      let newZoom = map.getZoom() + val;
      map.setZoom(newZoom);
    },
    handleMarkerClick(item) {
      this.goToSegmentLocation(item.info.coordinates, item.info.name);
    },
    refreshMapObjects() {
      if (this.mapLayerSelection.includes(Constants.LAYER_CONGESTION)) this.addSegments();
      if (this.isWazeMarkers) this.addMarkers(this.filteredWazeMarkers);
      if (this.isWazeClusters) this.addWazeClusters();
      if (this.mapLayerSelection.includes(Constants.LAYER_DEVICES)) this.addMarkers(this.filteredDeviceMarkers);
      if (this.mapLayerSelection.includes(Constants.LAYER_BLUETOOTH_SENSORS)) this.addMarkers(this.sensorMarkers);
    },
    /* Segments Methods */
    createSegments() {
      this.removeSegments();
      this.$store.state.bluetooth.segmentPolylines = [];
      this.apiData.segments.forEach(s => {
        this.createSegment(s, this.map);
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
      this.segmentPolylines.push(path);
      google.maps.event.addListener(path, 'click', event => {
        if (this.modes.addFromMap) {
          if (!this.multigraphSegs) {
            this.$store.state.bluetooth.multigraphSegs = [];
          }
          if (path.strokeColor != 'purple') {
            path.setOptions({ strokeWeight: 10, strokeOpacity: 1, strokeColor: 'purple' });
            this.$store.state.bluetooth.multigraphSegs.push(data);
          } else {
            let strokeColor = Utils.getStrokeColor(data.travelTime.level);
            path.setOptions({ strokeWeight: map.getZoom() / 3, strokeOpacity: 0.8, strokeColor: strokeColor });
            this.$store.state.bluetooth.multigraphSegs = this.multigraphSegs.filter(
              s => s.info.description != data.info.description
            );
          }
        } else {
          path.clickWindow.setOptions({ position: event.latLng });
          path.clickWindow.open(map);
        }
      });
      google.maps.event.addListener(map, 'zoom_changed', () => {
        let zoom = map.getZoom();
        path.setOptions({ strokeWeight: zoom / 3 });
      });
    },
    addSegments() {
      if (this.segmentPolylines) {
        this.segmentPolylines.forEach(p => {
          p.setMap(this.map);
        });
      }
    },
    removeSegments() {
      if (this.segmentPolylines) {
        this.segmentPolylines.forEach(s => {
          s.setMap(null);
        });
      }
    },
    /* Marker Methods */
    addMarkers(markers) {
      if (markers) {
        markers.forEach(m => {
          m.setMap(this.map);
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
      this.removeMarkers(this.sensorMarkers);
      this.$store.state.bluetooth.sensorMarkers = [];
      this.apiData.sensors.forEach(s => {
        this.createSensor(s, this.map);
      });
    },
    createSensor(data, map) {
      var hoverWindow = new google.maps.InfoWindow({ content: '<div id="content">' + data.node + '</div>' });
      var clickWindow = new google.maps.InfoWindow(infoWindows.getSenCW(data));
      var marker = new google.maps.Marker({
        position: { lat: data.lat, lng: data.lng },
        title: data.node,
        clickWindow: clickWindow,
        zIndex: 1,
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
      this.removeMarkers(this.deviceMarkers);
      this.$store.state.bluetooth.deviceMarkers = [];
      this.apiData.devices.forEach(d => {
        this.createDevice(d, this.map);
      });
    },
    createDevice(data, map) {
      var hoverWindow = new google.maps.InfoWindow({ content: '<div id="content">' + data.title + '</div>' });
      var clickWindow = new google.maps.InfoWindow(infoWindows.getDevCW(data));
      var marker = new google.maps.Marker({
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
        this.removeMarkers(this.wazeMarkers);
        this.$store.state.bluetooth.wazeMarkers = [];
        this.apiData.waze.forEach(w => {
          this.subtoggles.forEach(st => {
            if (w.alertType.name == st) {
              this.createWazeAlert(w, this.map);
              return;
            }
          });
        });
      } else {
        const oldWazeClusters = this.wazeClusters;
        let sliceIdx = this.wazeMarkers.length;
        let timeStart = timeEnd.minus({ minutes: dur });
        let startTS = timeStart.ts;
        let endTS = timeEnd.ts;
        this.apiData.wazeFull
          .filter(w => w.alertTimeTS > startTS && w.alertTimeTS < endTS)
          .forEach(w => {
            this.subtoggles.forEach(st => {
              if (w.alertType.name == st) {
                this.createWazeAlert(w, this.map);
                return;
              }
            });
          });
        let oldWazeMarkers = this.wazeMarkers.slice(0, sliceIdx);
        this.$store.state.bluetooth.wazeMarkers = this.wazeMarkers.slice(sliceIdx);
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
      let map = this.map;
      if (this.filteredWazeMarkers) {
        let markers = this.filteredWazeMarkers;
        this.$store.state.bluetooth.wazeClusters = new MarkerClusterer({ markers, map });
      }
    },
    removeWazeClusters(clusters = null) {
      if (clusters) {
        clusters.clearMarkers();
      } else {
        if (this.wazeClusters) {
          this.wazeClusters.clearMarkers();
        }
      }
    },
    goToMarkerLocation(obj, markerType) {
      let loc = { lat: obj.lat, lng: obj.lng };
      this.map.setCenter(loc);
      this.map.setZoom(17);
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
      this.map.setCenter(bounds.getCenter());
      this.map.fitBounds(bounds);
      this.segmentPolylines.forEach(p => {
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
          p.clickWindow.open(this.map);
        }
      });
    },
    closeAllInfoWIndows() {
      this.segmentPolylines.forEach(p => {
        p.clickWindow.close();
      });
    },
    getAdditionalInfoMain(name) {
      this.apiData.segments.forEach(s => {
        if (s.info && s.info.name == name) {
          this.selectedSeg.data = s;
          return;
        }
      });
      this.$store.commit('bluetooth/SET_TT_DIALOG', true);
    }
  },
  watch: {
    map() {
      this.refreshMapObjects();
    },
    'modes.addFromMap': {
      handler: function(newVal, oldVal) {
        if (newVal && !oldVal) {
          this.$store.state.bluetooth.multigraphSegs = [];
        } else {
          this.multigraphSegs.forEach(s => {
            this.segmentPolylines.forEach(p => {
              if (p.title == s.info.name) {
                let zoom = this.map.getZoom() / 3;
                let strokeColor = Utils.getStrokeColor(s.travelTime.level);
                p.setOptions({ strokeWeight: zoom, strokeOpacity: 0.8, strokeColor: strokeColor });
              }
            });
          });
          this.$store.state.bluetooth.multigraphSegs = null;
        }
      }
    },
    mapLayerSelection: {
      deep: true,
      handler: function(newVal, oldVal) {
        /* Add */
        let addDiff = newVal.filter(x => !oldVal.includes(x));
        if (addDiff.length > 0) {
          if (addDiff[0] == Constants.LAYER_CONGESTION) this.addSegments();
          else if (addDiff[0] == Constants.LAYER_WAZE) {
            this.removeWazeClusters();
            if (this.isWazeMarkers) {
              this.addMarkers(this.filteredWazeMarkers);
            }
            if (this.mapLayerSelection.includes(2)) {
              this.addWazeClusters();
            }
          } else if (addDiff[0] == Constants.LAYER_GROUPED_WAZE) {
            this.removeMarkers(this.$store.state.bluetooth.wazeMarkers);
            this.addWazeClusters();
          } else if (addDiff[0] == Constants.LAYER_DEVICES) this.addMarkers(this.filteredDeviceMarkers);
          else if (addDiff[0] == Constants.LAYER_BLUETOOTH_SENSORS) this.addMarkers(this.sensorMarkers);
        }
        /* Delete */
        let delDiff = oldVal.filter(x => !newVal.includes(x));
        if (delDiff.length > 0) {
          if (delDiff[0] == Constants.LAYER_CONGESTION) this.removeSegments();
          else if (delDiff[0] == Constants.LAYER_WAZE) {
            this.removeMarkers(this.$store.state.bluetooth.wazeMarkers);
            this.removeWazeClusters();
          } else if (delDiff[0] == Constants.LAYER_GROUPED_WAZE) {
            this.removeWazeClusters();
          } else if (delDiff[0] == Constants.LAYER_DEVICES) this.removeMarkers(this.deviceMarkers);
          else if (delDiff[0] == Constants.LAYER_BLUETOOTH_SENSORS) this.removeMarkers(this.sensorMarkers);
        }
      }
    },
    wazeLayerSelection() {
      if (this.isWazeMarkers) {
        this.removeMarkers(this.wazeMarkers);
        this.addMarkers(this.filteredWazeMarkers);
      } else if (this.isWazeClusters) {
        this.removeWazeClusters();
        this.addWazeClusters();
      }
    },
    deviceLayerSelection() {
      if (this.mapLayerSelection.includes(3)) {
        this.removeMarkers(this.deviceMarkers);
        this.addMarkers(this.filteredDeviceMarkers);
      }
    }
  },
  computed: {
    filteredWazeMarkers() {
      if (this.wazeLayerSelection && this.wazeMarkers) {
        let filteredWazeMarkers = this.wazeMarkers.filter(w => this.wazeLayerSelection.includes(w.wazeTypeId));
        return filteredWazeMarkers;
      } else {
        return this.wazeMarkers;
      }
    },
    filteredDeviceMarkers() {
      if (this.deviceLayerSelection && this.deviceMarkers) {
        let filterLevels = [];
        if (this.deviceLayerSelection.includes(0)) {
          filterLevels.push(-1, 0, 1);
        }
        if (this.deviceLayerSelection.includes(1)) {
          filterLevels.push(2, 3, 4);
        }
        if (this.deviceLayerSelection.includes(2)) {
          filterLevels.push(5, 6);
        }
        let filteredDeviceMarkers = this.deviceMarkers.filter(w => filterLevels.includes(w.level));
        return filteredDeviceMarkers;
      } else {
        return this.wazeMarkers;
      }
    },
    fetchDone() {
      return !!this.apiData.segments && !!this.apiData.sensors && !!this.apiData.devices && !!this.apiData.waze;
    },
    isWazeMarkers() {
      return this.mapLayerSelection.includes(1) && !this.mapLayerSelection.includes(2);
    },
    isWazeClusters() {
      return this.mapLayerSelection.includes(1) && this.mapLayerSelection.includes(2);
    },
    ...mapState(['currentDate', 'position']),
    ...mapState('bluetooth', [
      'map',
      'modes',
      'mapLayerSelection',
      'wazeLayerSelection',
      'deviceLayerSelection',
      'dialog',
      'apiData',
      'sensorMarkers',
      'deviceMarkers',
      'segmentPolylines',
      'multigraphSegs',
      'wazeClusters',
      'wazeMarkers',
      'selectedSeg',
      'subtoggles'
    ])
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

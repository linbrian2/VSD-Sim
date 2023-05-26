<template>
  <div>
    <!-- Right display panel -->
    <RightPanel :title="currentTitle" v-if="!$vuetify.breakpoint.mobile">
      <component :is="currentComponent" v-bind="currentProperties" ref="refPanelInfo" />
    </RightPanel>

    <!-- Maps -->
    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="10"
      map-type-id="roadmap"
      class="my-map"
      :style="
        `margin-top:-1px; width: 100%; height:${
          $vuetify.breakpoint.mobile && showPanel ? 'calc(50vh - 48px)' : 'calc(100vh - 48px)'
        }`
      "
    >
      <!-- Traffic Flow Detectors -->
      <GmapMarker
        v-for="m in markers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :clickable="true"
        :icon="getMarkerErrorIcon(m)"
        :options="markerOptions(m.id)"
        @click="markerClicked(m)"
      />

      <!-- Mask marker -->
      <MaskMarker :position="markerPosition" />
    </GmapMap>

    <Toolbar
      :counts="classCounts"
      :entities="entities"
      @system="showSystemInfo"
      @filter="deviceFiltering"
      @type="errorTypeSelect"
      @search="showSearchResult"
    />

    <BottomDataDisplay
      name="dashboardSideBarWidth"
      :title="currentTitle"
      v-if="$vuetify.breakpoint.mobile && showPanel"
    >
      <component :is="currentComponent" v-bind="currentProperties" ref="refPanelInfo" />
    </BottomDataDisplay>
  </div>
</template>

<script>
/* global google */
import { mapState, mapActions } from 'vuex';
import { gmapApi } from 'vue2-google-maps';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import TrafficApi from '@/utils/api/traffic';
import Constants from '@/utils/constants/status';
import StatusApi from '@/utils/api/status';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import { mapIcons } from '@/mixins/mapIcons';
import MaskMarker from '@/components/modules/traffic/common/MaskMarker.vue';
import RightPanel from '@/components/modules/traffic/common/RightPanel';
import Toolbar from '@/components/modules/status/Toolbar';
import SensorErrorInfo from '@/components/modules/status/SensorErrorInfo';
import SystemQualityInfo from '@/components/modules/status/SystemQualityInfo';
import BottomDataDisplay from '@/components/modules/traffic/common/BottomDataDisplay.vue';

export default {
  mixins: [mapIcons],
  components: {
    RightPanel,
    MaskMarker,
    GmapCustomMarker,
    Toolbar,
    SensorErrorInfo,
    SystemQualityInfo,
    BottomDataDisplay
  },

  data: () => ({
    loading: false,
    errorIcons: [],
    errorCountThreshold: 5,
    selectedMarker: null,
    currentComponent: null,
    currentTitle: '',
    mapRegionSelection: -1,
    sensorTypeSelection: -1,
    selectedMarkerId: -1,
    errorTypeSelection: -1,

    map: null,
    infoPosition: null,
    markerPosition: { lat: 0, lng: 0 },
    devices: [],
    errorCounts: [],

    options: {
      zoomControl: true,
      zoomControlOptions: {
        position: 8
      },
      mapTypeControl: false,
      streetViewControl: false,
      fullscreenControl: true,

      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'hybrid']
      },
      styles: DarkMapStyle,
      gestureHandling: 'greedy'
    }
  }),

  computed: {
    google: gmapApi,

    filteredMarkers() {
      let devices = this.devices;
      if (this.mapRegionSelection > 0 && this.sensorTypeSelection < 0) {
        devices = this.devices.filter(location => location.region === this.mapRegionSelection);
      } else if (this.mapRegionSelection < 0 && this.sensorTypeSelection > 0) {
        devices = this.devices.filter(location => location.type === this.sensorTypeSelection);
      } else if (this.mapRegionSelection > 0 && this.sensorTypeSelection > 0) {
        devices = this.devices.filter(
          location => location.region === this.mapRegionSelection && location.type === this.sensorTypeSelection
        );
      }
      return devices;
    },

    markers() {
      return this.errorTypeSelection < 0
        ? this.filteredMarkers
        : this.filteredMarkers.filter(location => location.errorTypes.includes(this.errorTypeSelection));
    },

    classCounts() {
      return this.updateClassCounts(this.filteredMarkers, this.errorCounts, this.errorCountThreshold);
    },

    entities() {
      return this.markers.map(marker => ({ id: marker.id, desc: marker.name }));
    },

    position() {
      return this.$store.state.position;
    },

    currentProperties() {
      if (this.currentComponent === SensorErrorInfo) {
        return { marker: this.selectedMarker };
      }
      return {};
    },

    ...mapState('traffic', ['showPanel']),
    ...mapState(['currentDate'])
  },

  watch: {
    showPanel(show) {
      if (!show) {
        // Clear maker selection
        this.markerPosition = { lat: 0, lng: 0 };
      }
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
    },

    async currentDate() {
      if (this.devices.length == 0) {
        await this.fetchDevices();
      } else {
        this.devices.forEach(device => (device.status = 0));
      }
      await this.fetchLatestData();
    }
  },

  mounted() {
    this.loadPage(this.$vuetify.theme.dark);
    // this.showScrollBar(false);

    this.$refs.mapRef.$mapPromise.then(map => {
      this.errorIcons = this.createErrorTypeIcons();
      this.map = map;
      this.addMapControls(map);
      this.fetchDevices();
    });

    setTimeout(() => {
      this.fetchLatestData();
    }, 500);
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
      this.addPointControl(map);
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
            this.zoomSelectedMarker();
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

    showSearchResult(item) {
      const marker = this.markers.find(marker => marker.id === item.id);
      if (marker) {
        this.markerClicked(marker);
      }
    },

    markerClicked(marker) {
      // Update mask marker position to the current marker location
      this.markerPosition = marker.position;

      this.showPanelIfNot();
      this.selectedMarker = marker;
      this.currentTitle = 'Detector Quality';
      if (this.currentComponent !== SensorErrorInfo) {
        this.currentComponent = SensorErrorInfo;
      } else {
        setTimeout(() => {
          if (this.$refs.refPanelInfo) {
            this.$refs.refPanelInfo.init(marker);
          }
        }, 250);
      }
    },

    showSystemInfo() {
      this.showPanelIfNot();
      this.currentTitle = 'System Quality Info';
      if (this.currentComponent !== SystemQualityInfo) {
        this.currentComponent = SystemQualityInfo;
      }
    },

    getMarkerErrorIcon(marker) {
      if (marker) {
        if (marker.status >= 0 && marker.status < this.errorIcons.length) {
          return this.errorIcons[marker.status];
        }
      }
      return this.getMarkerIcon(marker);
    },

    markerOptions(key) {
      const zIndex = this.selectedMarkerId == key ? 100 : 99;
      return { optimized: false, zIndex };
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

    zoomSelectedMarker() {
      const marker = this.markers.find(m => m.id == this.selectedMarker.id);
      if (marker) {
        this.map.panTo(marker.position);
        this.map.setZoom(14);
      }
    },

    deviceFiltering({ regionId, typeId }) {
      this.mapRegionSelection = regionId;
      this.sensorTypeSelection = typeId;
    },

    errorTypeSelect(classId) {
      this.errorTypeSelection = classId;
    },

    createErrorTypeIcons() {
      const icons = [];
      for (let i = 0; i < 8; i++) {
        const icon = {
          path: 0,
          scale: 10.0,
          fillColor: Constants.DISTINCT_COLORS[i],
          fillOpacity: 1.0,
          strokeWeight: 0.4
        };
        icons.push(icon);
      }
      return icons;
    },

    createDataClasses() {
      const dataClasses = [];
      for (let i = 0; i < 8; i++) {
        let label = `${i}: ${Constants.QUALITY_ERROR_NAMES[i]}`;
        const name = label + this.addSpace(50 - label.length);

        dataClasses.push({
          from: i,
          to: i,
          color: Constants.DISTINCT_COLORS[i],
          name
        });
      }
      return dataClasses;
    },

    updateMarkerStatus(data, threshold) {
      let map = new Map();
      data.forEach(d => {
        if (d.counts[0] > threshold) {
          const status = this.getDominantErrorType(d.counts, 1);
          const errorTypes = this.getErrorTypes(d.counts);
          map.set(d.id, { status, errorTypes });
        }
      });

      this.devices.forEach(d => {
        if (map.has(d.id)) {
          const value = map.get(d.id);
          d.status = value.status;
          d.errorTypes = value.errorTypes;
        } else {
          d.status = 0;
          d.errorTypes = [0];
        }
      });
    },

    updateClassCounts(devices, data, threshold) {
      const deviceSet = new Set(devices.map(d => d.id));

      const classCounts = Array(8).fill(0);
      classCounts[0] = deviceSet.size;
      //console.log(deviceSet.size, data.length);
      data.forEach(d => {
        if (deviceSet.has(d.id) && d.counts[0] > threshold) {
          classCounts[0]--;
          for (let i = 1; i < d.counts.length; i++) {
            if (d.counts[i] > 0) {
              classCounts[i]++;
            }
          }
        }
      });

      return classCounts;
    },

    getDominantErrorType(counts, startIdx) {
      let max = counts[startIdx];
      let maxIndex = startIdx;

      for (var i = startIdx + 1; i < counts.length; i++) {
        if (counts[i] > max) {
          maxIndex = i;
          max = counts[i];
        }
      }

      return maxIndex;
    },

    getErrorTypes(counts) {
      let result = [];
      if (counts[0] > 0) {
        for (var i = 1; i < counts.length; i++) {
          if (counts[i] > 0) {
            result.push(i);
          }
        }
      } else {
        result.push(0);
      }
      return result;
    },

    async fetchDevices() {
      try {
        const response = await TrafficApi.fetchDevices();
        this.devices = response.data.map(obj => ({ ...obj, status: 0, errorTypes: [] }));
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async fetchLatestData() {
      this.loading = true;
      try {
        const response = await StatusApi.fetchSensorsErrorCounts(this.currentDate.getTime());
        const data = this.parseResponseData(response);

        if (data) {
          this.errorCounts = data;
          this.updateMarkerStatus(data, this.errorCountThreshold);
        }
      } catch (error) {
        console.log(error);
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    parseResponseData(response) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'info' });
      }
      return result;
    },

    ...mapActions('traffic', ['fetchWeatherStations'])
  }
};
</script>
<style>
.gm-style div {
  -webkit-transform: scale(1.003);
}
</style>

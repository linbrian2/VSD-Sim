<template>
  <div>
    <!-- Right display panel -->
    <RightPanel :title="currentTitle">
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
      style="margin-top:-1px; width: 100%; height:calc(100vh - 48px)"
    >
      <!-- Traffic FLow Detectors -->
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
    </GmapMap>
    <Toolbar @region-select="mapRegionSelect" @type-select="errorTypeSelect" />
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
import RightPanel from '@/components/modules/traffic/common/RightPanel';
import Toolbar from '@/components/modules/status/Toolbar';
import SensorErrorInfo from '@/components/modules/status/SensorErrorInfo';

export default {
  mixins: [mapIcons],
  components: {
    RightPanel,
    GmapCustomMarker,
    Toolbar,
    SensorErrorInfo
  },

  data: () => ({
    loading: false,
    errorIcons: [],

    selectedMarker: null,
    currentComponent: null,
    currentTitle: '',
    mapRegionSelection: -1,
    selectedMarkerId: -1,
    errorTypeSelection: -1,

    map: null,
    infoPosition: null,
    devices: [],

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
        if (this.errorTypeSelection < 0) {
          return this.devices;
        } else {
          return this.devices.filter(location => location.status === this.errorTypeSelection);
        }
      } else {
        if (this.errorTypeSelection < 0) {
          return this.devices.filter(location => location.flags === this.mapRegionSelection);
        } else {
          return this.devices.filter(
            location => location.flags === this.mapRegionSelection && location.status === this.errorTypeSelection
          );
        }
      }
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

    ...mapState(['currentDate'])
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

    async currentDate() {
      await this.fetchDevices();
      await this.fetchLatestData();
    }
  },

  mounted() {
    this.loadPage(this.$vuetify.theme.dark);
    this.showScrollBar(false);

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
      }
    },

    markerClicked(marker) {
      if (marker.status == 0) {
        return;
      }
      this.showPanelIfNot();
      this.selectedMarker = marker;
      this.currentTitle = 'Detector Quality Check';
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

    mapRegionSelect(regionId) {
      this.mapRegionSelection = regionId;
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
          const dominantErrorType = this.getDominantErrorType(d.counts, 1);
          map.set(d.id, dominantErrorType);
        }
      });
      this.devices.forEach(d => {
        if (map.has(d.id)) {
          d.status = map.get(d.id);
        }
      });
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

    async fetchDevices() {
      try {
        const response = await TrafficApi.fetchDevices();
        this.devices = response.data.map(obj => ({ ...obj, status: 0 }));
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
          this.updateMarkerStatus(data, 20);
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

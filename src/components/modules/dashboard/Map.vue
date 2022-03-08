<template>
  <div id="basic-map">
    <div v-if="!reload">
      <GmapMap
        ref="mapRef"
        :options="options"
        :center="center"
        :zoom="zoom"
        map-type-id="roadmap"
        class="map"
        :style="style"
      >
        <GmapMarker
          v-for="m in markers"
          :key="m.id"
          :position="m.position"
          :title="m.name"
          :clickable="true"
          :icon="m.icon || icons[0]"
        />
      </GmapMap>
      <GmapPolyline
        v-for="m in polylines"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :path="m.coordinates"
        :clickable="true"
      />
      <div :style="`position: absolute; top: 13px; left: 1014px; `">
        <v-btn icon @click="showPrefDialog = true"><v-icon>mdi-cog</v-icon></v-btn>
      </div>
      <WeatherOverlay :center="center"></WeatherOverlay>
      <PreferenceDialog :data="apiInfo" :openDialog="showPrefDialog" @closeDialog="showPrefDialog = false" />
    </div>
  </div>
</template>

<script>
/* global google */
import WeatherOverlay from '@/components/modules/dashboard/WeatherOverlay.vue';
import PreferenceDialog from '@/components/modules/dashboard/PreferenceDialog.vue';
import DarkMapStyle from '@/utils/DarkMapStyle.js';

export default {
  components: { WeatherOverlay, PreferenceDialog },
  props: {
    zoom: {
      type: Number,
      default: 12
    },
    height: {
      type: String,
      default: 'calc(100vh - 96px)'
    },
    disableDefaultUI: {
      type: Boolean,
      default: false
    },
    markers: {
      type: Array,
      default() {
        return [];
      }
    },
    polylines: {
      type: Array,
      default() {
        return [];
      }
    },
    icons: {
      type: Array,
      default() {
        return [
          { path: 0, scale: 10.0, fillColor: '#05FF00', fillOpacity: 0.8, strokeWeight: 0.4 },
          { path: 0, scale: 10.0, fillColor: '#FF7F00', fillOpacity: 0.8, strokeWeight: 0.4 }
        ];
      }
    }
  },
  data() {
    return {
      showPrefDialog: false,
      reload: false,
      map: null,
      center: { lat: 39.14, lng: -75.5 },
      style: { marginTop: '-1px', width: '100%', height: this.height },
      options: {
        scrollwheel: true,
        disableDefaultUI: this.disableDefaultUI,
        mapTypeId: 'roadmap',
        streetViewControl: false,
        styles: DarkMapStyle,
        mapTypeControlOptions: null
      }
    };
  },
  mounted() {
    setTimeout(() => {
      this.loadPage(this.$vuetify.theme.dark);
    }, 1);

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.options.mapTypeControlOptions = {
        style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
        position: google.maps.ControlPosition.BOTTOM_CENTER,
        mapTypeIds: ['roadmap', 'hybrid']
      };
      this.centerMap(this.map, this.markers);
      this.detectMapCenterChange();
      this.$emit('map-ready', map);
    });
  },
  methods: {
    getMarkerIcon(key) {
      if (this.selectedMarkerIds.length > 0) {
        return this.selectedMarkerIds.includes(key) ? this.getActiveIcon() : this.getNormalIcon();
      }
      return this.selectedMarkerId === key ? this.getActiveIcon() : this.getNormalIcon();
    },
    getNormalIcon() {
      return this.icons ? this.icons[0] : this.mapMarker;
    },
    getActiveIcon() {
      return this.icons ? this.icons[1] : this.mapMarkerActive;
    },
    markerClicked(marker) {
      this.selectedMarkerIds = [];
      this.selectedMarkerId = marker.id;
      this.$store.commit('traffic/SET_ACTIVE_MARKER', marker);
      this.$emit('click', marker);
    },
    detectMapCenterChange() {
      google.maps.event.addListener(this.map, 'dragend', () => {
        let center = this.map.getCenter();
        this.center = { lat: center.lat(), lng: center.lng() };
      });
    },
    loadPage(darkMode) {
      if (this.$refs.mapRef == null) {
        return;
      }
      if (darkMode && this.$refs.mapRef) {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: DarkMapStyle });
        });
      } else {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: null });
        });
      }
    },

    centerMap(map, markers) {
      console.log('Map: %o', map);
      console.log('Markers: %o', markers);
      if (markers && markers.length > 0) {
        console.log(0);
        let bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length; i++) {
          bounds.extend(markers[i].position);
        }
        map.setCenter(bounds.getCenter());
        if (markers.length == 0) {
          map.setCenter({ lat: 33.907, lng: -117.7 });
          map.setZoom(10);
        } else if (markers.length == 1) {
          map.setZoom(10);
        } else {
          map.fitBounds(bounds);
        }
      }
    }
  },
  watch: {
    markers() {
      if (this.markers && this.markers.length > 0) {
        this.centerMap(this.map, this.markers);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
</style>

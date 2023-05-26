<template>
  <div>
    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="12"
      map-type-id="roadmap"
      :class="$vuetify.breakpoint.mobile && selectedMarkerId ? 'map-select-mobile' : 'map-select-desktop'"
    >
      <GmapMarker
        v-for="m in markers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :clickable="true"
        :icon="getMarkerIcon(m.id)"
        @click="markerClicked(m)"
      />

      <div v-if="markerLabel">
        <GmapCustomMarker
          alignment="center"
          v-for="m in markers"
          :key="`L-${m.id}`"
          :offsetX="50"
          :offsetY="10"
          :marker="m.position"
        >
          <div v-if="isSelected(m.id)">
            <v-chip small outlined color="white">{{ m.uid || m.permit || m.id }}</v-chip>
          </div>
        </GmapCustomMarker>
      </div>
    </GmapMap>
  </div>
</template>

<script>
/* global google */
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import OutlierRemoval from '@/utils/OutlierRemoval.js';
import GmapCustomMarker from 'vue2-gmap-custom-marker';

export default {
  props: {
    markers: Array,
    icons: Array
  },

  components: {
    GmapCustomMarker
  },

  // https://github.com/xkjyeah/vue-google-maps/issues/94
  data: () => ({
    markerLabel: false,
    mapMarker: {
      url: require('@/assets/green-icon-48.png'),
      size: { width: 30, height: 48, f: 'px', b: 'px' }
    },
    mapMarkerActive: {
      url: require('@/assets/orange-icon-48.png'),
      size: { width: 30, height: 48, f: 'px', b: 'px' }
    },
    homeIcon: require('@/assets/home-24.png'),
    zoomIcon: require('@/assets/zoom-24.png'),
    markerOptions: {
      url: '',
      size: { width: 20, height: 20, f: 'px', b: 'px' },
      anchor: { x: 10, y: 10 }
    },
    map: null,
    selectedMarkerId: null,
    selectedMarkerIds: [],
    options: {
      mapTypeControl: true,
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite']
      },

      streetViewControl: false,
      fullscreenControl: true,
      zoomControl: true,
      gestureHandling: 'greedy'
    }
  }),

  computed: {
    position() {
      return this.$store.state.position;
    }
    //google: gmapApi
  },

  watch: {
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(11);
      });
    },
    markers(markers) {
      this.$refs.mapRef.$mapPromise.then(map => {
        this.centerMap(map, markers);
      });
    }
  },

  async mounted() {
    this.loadPage(this.$vuetify.theme.dark);

    this.$bus.$on('NAME_SELECTED', name => {
      let marker = this.markers.find(m => m.name === name);
      if (marker != null) {
        this.markerClicked(marker);
      }
    });

    this.$bus.$on('ID_SELECTED', id => {
      let marker = this.markers.find(m => m.id == id);
      if (marker != null) {
        this.markerClicked(marker);
      }
    });

    // this.$bus.$on('SELECT_FIRST', () => {
    //   this.triggerFirstMarkerClick();
    // });

    this.$bus.$on('CENTER_MAP', () => {
      this.centerMap(this.map, this.markers);
    });

    this.$bus.$on('ZOOM_SELECTED', () => {
      this.zoomSelectedMarker();
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
    });

    this.addMapControls();
  },

  methods: {
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

    getMap() {
      return this.$refs.mapRef;
    },

    addMapControls() {
      this.$refs.mapRef.$mapPromise.then(map => {
        this.setMapIconLocations(map);
        this.addHomeControl(map);
        this.addPointControl(map);
      });
    },

    setMapIconLocations(map) {
      map.setOptions({
        // fullscreenControlOptions: {
        //   position: google.maps.ControlPosition.LEFT_TOP
        // },

        zoomControlOptions: {
          position: google.maps.ControlPosition.RIGHT_CENTER
        }
      });
    },

    addHomeControl(map) {
      let options = {
        position: 'top_right',
        content: `<div style="margin:-5px 4px;"><img src="${this.homeIcon}"/></div>`,
        title: 'Click to center the map',
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
        content: `<div style="margin:-5px 4px;"><img src="${this.zoomIcon}"/></div>`,
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

    setMarkerLabel(show) {
      this.markerLabel = show;
    },

    triggerFirstMarkerClick() {
      if (this.markers && this.markers.length > 0) {
        const selectedMarker = this.markers[0];
        this.selectedMarkerId = selectedMarker.id;
        this.markerClicked(selectedMarker);
      }
    },

    getSelectedMarker() {
      return this.markers.find(m => m.id == this.selectedMarkerId);
    },

    centerAndZoom(marker, zoomLevel) {
      if (this.map) {
        this.map.panTo(marker.position);
        this.map.setZoom(zoomLevel);
      }
    },
    centerMap(map, markers) {
      if (markers.length > 0) {
        const outlierRemoval = new OutlierRemoval(5.0);
        const points = outlierRemoval.remove(markers.map(item => item.position));
        const bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < points.length; i++) {
          bounds.extend(points[i]);
        }
        map.fitBounds(bounds, 0);
      }
    },

    zoomSelectedMarker() {
      const marker = this.markers.find(m => m.id == this.selectedMarkerId);
      if (marker) {
        this.map.panTo(marker.position);
        this.map.setZoom(14);
      }
    },

    getMarkerIcon(key) {
      if (this.selectedMarkerIds.length > 0) {
        if (this.selectedMarkerIds.includes(key)) {
          if (this.selectedMarkerId === key) {
            return this.getSelectedIcon();
          } else {
            return this.getActiveIcon();
          }
        } else {
          return this.getNormalIcon();
        }
      }
      return this.selectedMarkerId === key ? this.getActiveIcon() : this.getNormalIcon();
    },

    getNormalIcon() {
      return this.icons ? this.icons[0] : this.mapMarker;
    },

    getActiveIcon() {
      return this.icons ? this.icons[1] : this.mapMarkerActive;
    },

    getSelectedIcon() {
      return this.icons ? this.icons[this.icons.length - 1] : this.mapMarkerActive;
    },

    markerClicked(marker) {
      this.selectedMarkerIds = [];
      this.selectedMarkerId = marker.id;
      this.$store.commit('traffic/SET_ACTIVE_MARKER', marker);
      this.$emit('click', marker);
    },

    isSelected(id) {
      return id === this.selectedMarkerId || this.selectedMarkerIds.includes(id);
    },

    selectById(id) {
      this.selectedMarkerId = id;
    },

    selectByIds(ids, centered = true) {
      this.selectedMarkerIds = [];
      Object.assign(this.selectedMarkerIds, ids);

      if (ids.length > 0 && centered) {
        let id = ids[Math.floor(ids.length / 2)];
        let marker = this.markers.find(m => m.id == id);
        if (marker != null) {
          this.centerAndZoom(marker, 12);
        }
      }
    }
  }
};
</script>
<style lang="scss" scoped>
.gm-style div {
  -webkit-transform: scale(1.005);
}
</style>

<template>
  <div>
    <GmapMap ref="mapRef" :options="options" :center="position" :zoom="11" map-type-id="roadmap" class="multi-map">
      <GmapMarker
        v-for="m in markers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :label="m.label"
        :clickable="true"
        :icon="getMarker(m.id)"
        @click="markerClicked(m)"
      />
    </GmapMap>
  </div>
</template>

<script>
/* global google */
import Vue from 'vue';
import { mapActions } from 'vuex';
import MapUtils from '@/utils/MapUtils.js';
import DarkMapStyle from '@/utils/DarkMapStyle.js';

export default {
  data: () => ({
    mapMarker: {
      url: require('@/assets/green-icon-48.png'),
      size: { width: 30, height: 48, f: 'px', b: 'px' }
    },
    mapMarkerActive: {
      url: require('@/assets/orange-icon-48.png'),
      size: { width: 30, height: 48, f: 'px', b: 'px' }
    },
    homeIcon: require('@/assets/home-24.png'),
    map: null,
    selectedMarkers: [],
    options: {
      mapTypeControl: false,
      streetViewControl: false,
      fullscreenControl: false,
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite']
      }
    }
  }),
  computed: {
    markers() {
      return this.$store.state.hr.locations;
    },
    position() {
      return this.$store.state.hr.position;
    }
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

  mounted() {
    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addControls(map);
      this.centerMap(this.map, this.markers);
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    let locations = this.$store.state.hr.locations;
    if (locations.length == 0) {
      this.fetchLocations();
    }
    this.loadPage(this.$vuetify.theme.dark);
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

    resize() {
      setTimeout(() => {
        Vue.$gmapDefaultResizeBus.$emit('resize');
        //google.maps.event.trigger(this.map, 'resize');
      }, 400);
    },

    addControls(map) {
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

    centerMap(map, markers) {
      if (markers.length > 2) {
        const bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length - 1; i++) {
          bounds.extend(markers[i].position);
        }
        map.fitBounds(bounds);
      }
    },

    getMarker(key) {
      return this.selectedMarkers.includes(key) ? this.mapMarkerActive : this.mapMarker;
    },

    markerClicked(marker) {
      let index = this.selectedMarkers.findIndex(element => element === marker.id);
      if (index < 0) {
        this.selectedMarkers.push(marker.id);
      } else {
        this.selectedMarkers.splice(index, 1);
      }
      this.$emit('click', { marker: marker, action: index >= 0 ? 1 : 0 });
    },

    clearSelection() {
      this.selectedMarkers = [];
    },

    ...mapActions('hr', ['fetchLocations'])
  }
};
</script>
<style lang="scss">
.multi-map {
  width: 100%;
  height: 520px;
}
</style>

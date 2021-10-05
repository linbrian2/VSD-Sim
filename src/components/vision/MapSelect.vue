<template>
  <div>
    <GmapMap ref="mapRef" :options="options" :center="position" :zoom="11" map-type-id="roadmap" class="my-map">
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
import { mapActions } from 'vuex';
import EventBus from '@/utils/EventBus';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import MapUtils from '@/utils/MapUtils.js';

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
    markerOptions: {
      url: '',
      size: { width: 60, height: 90, f: 'px', b: 'px' },
      scaledSize: { width: 30, height: 45, f: 'px', b: 'px' }
    },
    map: null,
    selectedKey: null,
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
      return this.$store.state.vision.locations;
    },
    position() {
      return this.$store.state.vision.position;
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
  async mounted() {
    EventBus.$on('NAME_SELECTED', name => {
      let marker = this.markers.find(m => m.name === name);
      if (marker != null) {
        this.markerClicked(marker);
      }
    });

    EventBus.$on('CENTER_MAP', () => {
      this.centerMap(this.map, this.markers);
    });

    EventBus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
    });

    this.addControls();
    await this.fetchLocations();
    let selected = this.$store.state.vision.activeMarker;
    if (selected !== null) {
      this.selectedKey = selected.id;
    }

    this.loadPage(this.$vuetify.theme.dark);
  },
  methods: {
    loadPage(darkMode) {
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
    getMap() {
      return this.$refs.mapRef;
    },
    addControls() {
      this.$refs.mapRef.$mapPromise.then(map => {
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
      });
    },
    centerMap(map, markers) {
      if (markers.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length; i++) {
          bounds.extend(markers[i].position);
        }
        map.fitBounds(bounds);
      }
    },
    getMarker(key) {
      return this.selectedKey === key ? this.mapMarkerActive : this.mapMarker;
    },
    markerClicked(marker) {
      this.selectedKey = marker.id;
      this.$store.commit('vision/SET_ACTIVE_MARKER', marker);
      this.$emit('click', marker);
    },
    ...mapActions('vision', ['fetchLocations'])
  }
};
</script>
<style lang="scss" scoped>
.my-map {
  width: 100%;
  height: calc(100vh - 84px);
}
.gm-style div {
  -webkit-transform: scale(1.002);
}
</style>

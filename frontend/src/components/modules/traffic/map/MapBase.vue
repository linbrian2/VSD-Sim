<template>
  <div>
    <GmapMap ref="mapRef" :options="options" :center="position" :zoom="11" map-type-id="roadmap" :class="mapClass">
      <slot></slot>
    </GmapMap>
  </div>
</template>

<script>
/* global google */
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import MapUtils from '@/utils/MapUtils.js';

export default {
  props: {
    smallMap: { type: Boolean, default: false },
    selectedId: { type: String, default: '' },
    mapStyle: { type: String, default: '' },
    mapOptions: { type: Object, default: () => ({}) }
  },

  data: () => ({
    homeIcon: require('@/assets/home-24.png'),
    zoomIcon: require('@/assets/zoom-24.png'),
    layerIcon: require('@/assets/layers-24.png'),
    map: null,
    options: {
      mapTypeControl: true,
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite']
      },
      streetViewControl: false,
      zoomControl: true,
      fullscreenControl: true,
      styles: DarkMapStyle,
      gestureHandling: 'greedy'
    }
  }),

  computed: {
    mapClass() {
      if (this.mapStyle) {
        return this.mapStyle;
      } else {
        return this.smallMap || (this.$vuetify.breakpoint.mobile && this.selectedId != '')
          ? 'map-select-mobile'
          : 'map-select-desktop';
      }
    },

    position() {
      return this.$store.state.position;
    }
  },

  watch: {
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(11);
      });
    }
  },

  mounted() {
    this.loadPage(this.$vuetify.theme.dark);

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.setMapIconLocations(map);
      this.addHomeControl(map);
      this.addPointControl(map);
      this.$emit('map-ready', map);
    });

    // Merge map options
    if (Object.keys(this.mapOptions).length > 0) {
      this.options = { ...this.options, ...this.mapOptions };
    }
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
      return this.map;
    },

    setMapIconLocations(map) {
      map.setOptions({
        // mapTypeControlOptions: {
        //   position: google.maps.ControlPosition.TOP_CENTER
        // },

        // fullscreenControlOptions: {
        //   position: google.maps.ControlPosition.TOP_CENTER
        // },

        zoomControlOptions: {
          position: google.maps.ControlPosition.RIGHT_CENTER
        }
      });
    },

    addHomeControl(map) {
      let options = {
        position: 'top_right',
        content: `<div class="non-selection" style="margin:-5px 4px;"><img src="${this.homeIcon}"/></div>`,
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
            this.$emit('center-map', map);
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
            this.$emit('zoom-select', map);
          }
        }
      };
      MapUtils.addControl(map, options);
    }
  }
};
</script>
<style lang="scss" scoped>
.map-select {
  width: 100%;
  height: calc(100vh - 88px);
}

// .gm-style div {
//   -webkit-transform: scale(1.005);
// }

.non-selection {
  -moz-user-select: none;
  -khtml-user-select: none;
  -webkit-user-select: none;
  user-select: none;
}
</style>

<template>
  <div id="bluetooth-map">
    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="12"
      map-type-id="roadmap"
      class="my-map"
      style="margin-top:-1px; width: 100%; height:calc(100vh - 48px)"
    >
      <!-- <GmapPolyline :path.sync="segment" :options="segmentOptions" />
      <GmapMarker :position="startPoint" :clickable="false" :draggable="false" :icon="startIcon" />
      <GmapMarker :position="endPoint" :clickable="false" :draggable="false" :icon="endIcon" /> -->
    </GmapMap>
  </div>
</template>

<script>
import DarkMapStyle from '@/utils/DarkMapStyle.js';

export default {
  data: () => ({
    homeIcon: require('@/assets/home-24.png'),
    startIcon: {
      url: require('@/assets/start.png'),
      size: { width: 32, height: 32, f: 'px', b: 'px' }
    },
    endIcon: {
      url: require('@/assets/end.png'),
      size: { width: 32, height: 32, f: 'px', b: 'px' }
    },
    markerOptions: {
      url: '',
      size: { width: 60, height: 90, f: 'px', b: 'px' },
      scaledSize: { width: 30, height: 45, f: 'px', b: 'px' }
    },
    options: {
      center: { lat: 39.14, lng: -75.5 },
      scrollwheel: true,
      disableDefaultUI: true,
      zoom: 9,
      mapTypeId: "roadmap",
    },
  }),
  computed: {
    position() {
      return this.$store.state.position;
    },
  },
  watch: {
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(12);
      });
    },
  },

  mounted() {
    this.$refs.mapRef.$mapPromise.then(map => {
      this.$store.state.bluetooth.map = map;
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });
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
  }
};
</script>
<style lang="scss" scoped>
.my-map {
  width: 101.5%;
  height: calc(100vh - 48px);
} 
</style>

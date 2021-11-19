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
    </GmapMap>
  </div>
</template>

<script>
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import Constants from '@/utils/constants/bluetooth';
import { mapState } from 'vuex';

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
      mapTypeId: 'roadmap'
    }
  }),
  computed: {
    map: {
      get() {
        return this.$store.state.bluetooth.map;
      },
      set(val) {
        this.$store.commit('bluetooth/SET_MAP', val);
      }
    },
    position() {
      return this.$store.state.position;
    },
    showLabel() {
      return this.mapLayerSelection.includes(Constants.LAYER_LABELS);
    },
    ...mapState('bluetooth', ['mapLayerSelection'])
  },
  watch: {
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(12);
      });
    },
    showLabel() {
      this.loadPage();
    }
  },

  mounted() {
    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
    });

    this.$bus.$on('UPDATE_DARK_MODE', () => {
      this.loadPage();
    });
    this.loadPage(this.$vuetify.theme.dark);
  },

  methods: {
    loadPage() {
      let darkMode = this.$vuetify.theme.dark;
      if (this.$refs.mapRef == null) {
        return;
      }
      if (darkMode && this.$refs.mapRef) {
        let style = DarkMapStyle;
        if (style.length > 16) style.splice(16);

        if (!this.showLabel) Constants.MAP_DISABLED_LABELS.forEach(x => style.push(x));
        else Constants.MAP_ENABLED_LABELS.forEach(x => style.push(x));
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: style });
        });
      } else {
        let style = [];
        if (style.length > 16) style.splice(16);

        if (!this.showLabel) Constants.MAP_DISABLED_LABELS.forEach(x => style.push(x));
        else Constants.MAP_ENABLED_LABELS.forEach(x => style.push(x));
        if (style.length > 16) style.splice(16);
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: style });
        });
      }
    },

    getMap() {
      return this.$refs.mapRef;
    }
  }
};
</script>
<style lang="scss" scoped>
.my-map {
  width: 101.5%;
  height: calc(100vh - 48px);
}
</style>

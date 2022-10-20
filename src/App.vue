<template>
  <v-app>
    <router-view />
  </v-app>
</template>

<script>
import { mapState } from 'vuex';
import EventBus from '@/utils/EventBus';
import Highcharts from 'highcharts';
import ChartStyles from '@/utils/ChartStyles.js';

export default {
  mounted() {
    this.$vuetify.theme.dark = this.darkMode;
    this.setHCTheme(this.darkMode);
  },

  computed: {
    ...mapState(['darkMode'])
  },

  watch: {
    darkMode(value) {
      this.$vuetify.theme.dark = value;
      this.setHCTheme(value);
      EventBus.$emit('UPDATE_DARK_MODE', value);
      this.$bus.$emit('UPDATE_DARK_MODE', value);
    }
  },

  methods: {
    setHCTheme(darkMode) {
      if (darkMode == true) {
        Highcharts.theme = ChartStyles.getDarkTheme();
      } else {
        Highcharts.theme = ChartStyles.getLightTheme();
      }
      Highcharts.setOptions(Highcharts.theme);
    }
  }
};
</script>

<style lang="scss">
.map-select-mobile {
  width: 100%;
  height: calc(50vh - 84px);
}
.map-select-desktop {
  width: 100%;
  height: calc(100vh - 84px);
}
</style>

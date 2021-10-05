<template>
  <v-app>
    <!-- <div id="nav">
      <router-link to="/flow"> Flow </router-link>/
      <router-link to="/hr"> HR </router-link>/
      <router-link to="/vision"> Vision </router-link>/
      <router-link to="/bluetooth"> Bluetooth </router-link>/
      <router-link to="/cav"> CAV </router-link>/
      <router-link to="/status"> Health Monitoring </router-link>
    </div> -->
    <router-view />
  </v-app>
</template>

<script>
import EventBus from '@/utils/EventBus';
import Highcharts from 'highcharts';
import ChartStyles from '@/utils/ChartStyles.js';

export default {
  mounted() {
    this.$vuetify.theme.dark = this.$store.state.darkMode;
    this.setHCTheme(this.$store.state.darkMode);
  },
  methods: {
    setHCTheme(darkMode) {
      if (darkMode == true) {
        Highcharts.theme = ChartStyles.getDarkTheme();
      } else {
        Highcharts.theme = ChartStyles.getLightTheme();
      }
      Highcharts.setOptions(Highcharts.theme);
    },
  },
  watch: {
    '$store.state.darkMode'(darkMode) {
      this.$vuetify.theme.dark = darkMode;
      this.setHCTheme(darkMode);
      EventBus.$emit('UPDATE_DARK_MODE', darkMode);
      this.$bus.$emit('UPDATE_DARK_MODE', darkMode);
    }
  }
}
</script>
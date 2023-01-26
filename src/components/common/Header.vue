<template>
  <div>
    <v-app-bar app dark flat dense :clipped-left="clipLeft" :clipped-right="clipRight" color="black">
      <v-btn icon @click.stop="showDrawer">
        <v-icon>mdi-view-grid-outline</v-icon>
      </v-btn>

      <div class="hidden-md-and-down">
        <router-link to="/">
          <v-img
            alt="Logo"
            class="shrink mr-2"
            contain
            src="@/assets/logo.png"
            transition="scale-transition"
            width="100"
          />
        </router-link>
      </div>

      <div class="hidden-sm-and-down">
        <NavDropdown :title="title" />
      </div>
      <v-toolbar-title class="hidden-sm-and-down overline">{{ title }}</v-toolbar-title>
      <template v-if="actionItems && actionItems.length > 0">
        <NavDropdown :items="actionItems" :title="itemTitle" />
        <v-toolbar-title class="overline hidden-sm-and-down">{{ itemTitle }}</v-toolbar-title>
      </template>

      <v-spacer></v-spacer>
      <div v-if="showDate">
        <MenuDatePicker :date="currentDate" />
      </div>
      <div v-if="showWeatherInfo" class="hidden-sm-and-down">
        <WeatherOverlay :center="mapCenter" :showCurrentTime="showCurrentTime" />
      </div>

      <v-spacer></v-spacer>

      <slot></slot>

      <LoggedInUser v-if="user" :user="user" />

      <ThreeDotMenu :items="dotItems" @menuItemclick="menuItemClicked" v-if="dotItems.length > 0" />
    </v-app-bar>

    <NavDrawer />
    <SnackBar />
    <SettingsDialog />
  </div>
</template>

<script>
import { mapState } from 'vuex';
import Highcharts from 'highcharts';
import { RouterNames } from '@/utils/constants/router';
import MenuDatePicker from '@/components/common/MenuDatePicker';
import NavDropdown from '@/components/common/NavDropdown';
import NavDrawer from '@/components/nav/NavDrawer';
import LoggedInUser from '@/components/common/LoggedInUser';
import ThreeDotMenu from '@/components/common/ThreeDotMenu';
import SnackBar from '@/components/common/SnackBar';
import ChartStyles from '@/utils/ChartStyles.js';
import WeatherOverlay from '@/components/common/WeatherOverlay.vue';
import SettingsDialog from './SettingsDialog.vue';

export default {
  props: {
    title: String,
    actionItems: Array,
    dotItems: {
      type: Array,
      default: () => []
    },
    showWeatherInfo: {
      type: Boolean,
      default: false
    },
    showCurrentTime: {
      type: Boolean,
      default: false
    },
    showDate: {
      type: Boolean,
      default: true
    },
    clipLeft: {
      type: Boolean,
      default: true
    },
    clipRight: {
      type: Boolean,
      default: false
    }
  },

  components: {
    NavDropdown,
    LoggedInUser,
    MenuDatePicker,
    NavDrawer,
    ThreeDotMenu,
    SnackBar,
    WeatherOverlay,
    SettingsDialog
  },

  data: () => ({}),

  computed: {
    icon() {
      return this.$store.state.traffic.showPanel ? 'mdi-close' : 'mdi-menu';
    },

    isDashboard() {
      return this.$route.name === RouterNames.TRAFFIC_DASHBOARD;
    },

    itemTitle() {
      return this.$route.name;
    },

    user() {
      return this.$store.state.auth.user;
    },

    ...mapState(['currentDate', 'darkMode', 'mapCenter'])
  },

  watch: {
    darkMode(value) {
      this.$vuetify.theme.dark = value;
      this.setHighChartsTheme(value);
      this.$bus.$emit('UPDATE_DARK_MODE', value);
    }
  },

  mounted() {
    this.$vuetify.theme.dark = this.$store.state.darkMode;
    this.setHighChartsTheme(this.$store.state.darkMode);
  },

  methods: {
    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    setHighChartsTheme(darkMode) {
      Highcharts.theme = darkMode ? ChartStyles.getDarkTheme() : ChartStyles.getLightTheme();
      Highcharts.setOptions(Highcharts.theme);
    },

    menuItemClicked(action) {
      this.$emit('menuItemclick', action);
    }
  }
};
</script>

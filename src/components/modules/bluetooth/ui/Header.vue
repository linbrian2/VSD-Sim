<template>
  <div>
    <v-app-bar app dark flat dense :clipped-left="clipLeft" :clipped-right="clipRight" color="black">
      <v-btn icon @click.stop="showDrawer">
        <v-icon>mdi-view-grid-outline</v-icon>
      </v-btn>

      <div class="d-flex align-center">
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

      <NavDropdown :title="title" />
      <v-toolbar-title>{{ title }}</v-toolbar-title>
      <NavDropdown :items="actionItems" :title="itemTitle" />
      <v-toolbar-title class="overline">{{ itemTitle }}</v-toolbar-title>

      <v-spacer></v-spacer>
      <div v-if="showDate">
        <MenuDatePicker :date="currentDate" />
      </div>
      <div>
        <v-tooltip right>
          <template v-slot:activator="{ on, attrs }">
            <v-chip
              v-bind="attrs"
              v-on="on"
              @click="$store.state.bluetooth.timePickerMenu = !$store.state.bluetooth.timePickerMenu"
              small
              outlined
              class="overline"
              color="white"
            >
              {{ timeStr }}
            </v-chip>
          </template>
          <span>{{ timeRemaining }}</span>
        </v-tooltip>
      </div>
      <v-spacer></v-spacer>

      <slot></slot>

      <LoggedInUser v-if="user" :user="user" />

      <ThreeDotMenu :items="dotItems" @menuItemclick="menuItemClicked" v-if="dotItems.length > 0" />
    </v-app-bar>

    <NavDrawer />
    <SnackBar />
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
import Utils from '@/utils/Utils';

export default {
  props: {
    title: String,
    actionItems: Array,
    dotItems: {
      type: Array,
      default: () => []
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
    SnackBar
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

    timeStr() {
      return `${Utils.formatXX(this.currentDate.getHours())}:${Utils.formatXX(this.currentDate.getMinutes())}`;
    },

    timeRemaining() {
      if (this.$store.state.bluetooth.autoUpdate) {
        let min = 5 - Math.ceil(this.timeSinceUpdate / 60);
        let sec = 60 - (this.timeSinceUpdate % 60);
        if (sec == 60) sec = 0;
        return `Next update in ${min}m ${sec}s`;
      } else {
        return `Auto-update disabled`;
      }
    },
    ...mapState(['currentDate', 'darkMode']),
    ...mapState('bluetooth', ['timeSinceUpdate'])
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
      console.log(action);
      this.$emit('menuItemclick', action);
    }
  }
};
</script>

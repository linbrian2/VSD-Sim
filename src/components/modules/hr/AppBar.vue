<template>
  <div>
    <v-app-bar app dark flat dense clipped-left color="black">
      <v-btn icon @click.stop="showDrawer">
        <v-icon>mdi-view-grid-outline</v-icon>
      </v-btn>

      <div class="d-flex align-center">
        <router-link to="/">
          <v-img
            alt="Logo"
            class="shrink mr-2"
            contain
            src="@/assets/ai-logo.png"
            transition="scale-transition"
            width="100"
          />
        </router-link>
      </div>
      <NavDropdown />
      <v-toolbar-title>{{ title }}</v-toolbar-title>
      <NavDropdown :items="action_menu_items" />
      <v-toolbar-title class="overline">{{ itemTitle }}</v-toolbar-title>

      <v-spacer></v-spacer>

      <div v-if="!isDashboard">
        <MenuDatePicker :date="currentDate" />
      </div>

      <v-spacer></v-spacer>

      <div v-show="$vuetify.breakpoint.mdAndUp">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(0)" icon v-on="on" @click.stop="showDashboard()">
              <v-icon>mdi-view-dashboard</v-icon>
            </v-btn>
          </template>
          <span>Dashboard</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(1)" icon v-on="on" @click.stop="showPCD()">
              <v-icon>mdi-chart-areaspline</v-icon></v-btn
            >
          </template>
          <span>Show PCD</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(2)" icon v-on="on" @click.stop="showSignal()">
              <v-icon>mdi-clock</v-icon>
            </v-btn>
          </template>
          <span>Signal Timing</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(3)" icon v-on="on" @click.stop="showPerformanceMeasures()">
              <v-icon>mdi-chart-bar</v-icon>
            </v-btn>
          </template>
          <span>Performance Measures</span>
        </v-tooltip>
      </div>

      <LoggedInUser v-if="user" :user="user" />

      <v-divider vertical class="ml-2" />

      <div>
        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on, attrs }">
            <v-btn icon v-bind="attrs" v-on="on">
              <v-icon>mdi-dots-vertical</v-icon>
            </v-btn>
          </template>

          <v-list>
            <v-list-item v-for="(item, i) in menu_items" :key="i" @click="menuItemClicked(i)">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
    </v-app-bar>

    <NavDrawer />
    <SnackBar />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import { RouterNames, RouterPaths } from '@/utils/constants/router';
import MenuDatePicker from '@/components/common/MenuDatePicker';
import NavDrawer from '@/components/nav/NavDrawer';
import NavDropdown from '@/components/common/NavDropdown';
import SnackBar from '@/components/common/SnackBar';
import LoggedInUser from '@/components/common/LoggedInUser';
import AppConstants from '@/utils/constants/app';
import { mapState } from 'vuex';

export default {
  props: ['drawer'],
  components: {
    NavDropdown,
    LoggedInUser,
    MenuDatePicker,
    NavDrawer,
    SnackBar
  },

  data: () => ({
    date: Utils.formatDate(Utils.yesterday()),
    title: AppConstants.HR_APP_TITLE,
    menu: false,
    menu_items: [
      { title: 'Corridor PCD Charts' },
      { title: 'Corridor Occupancy Charts' },
      { title: 'Toggle Dark Mode' }
    ],
    action_menu_items: [
      { title: RouterNames.HR_DASHBOARD, url: RouterPaths.HR_DASHBOARD },
      { title: RouterNames.HR_PCD, url: RouterPaths.HR_PCD },
      { title: RouterNames.HR_SIGNAL, url: RouterPaths.HR_SIGNAL },
      { title: RouterNames.HR_PERFORMANCE_MEASURES, url: RouterPaths.HR_PERFORMANCE_MEASURES },
      { divider: true },
      { title: RouterNames.HR_MULTI_PCD, url: RouterPaths.HR_MULTI_PCD },
      { title: RouterNames.HR_MULTI_OCC, url: RouterPaths.HR_MULTI_OCC },
    ]
  }),
  computed: {
    icon() {
      return this.$store.state.hr.showPanel ? 'mdi-close' : 'mdi-menu';
    },
    dateFormatted() {
      return this.date ? Utils.formatDateLong(Utils.dateFromString(this.date)) : '';
    },
    panelVisible() {
      return this.$store.state.hr.showPanel;
    },

    isDashboard() {
      return this.$route.name === RouterNames.HR_DASHBOARD;
    },

    itemTitle() {
      return this.$route.name;
    },

    user() {
      return this.$store.state.auth.user;
    },

    showSelectionDlg: {
      get() {
        return this.$store.state.hr.showSelectionDlg;
      },
      set(show) {
        this.$store.commit('hr/SHOW_SELECTION_DLG', show);
      }
    },
    ...mapState(['currentDate']),
    ...mapState('hr', ['activeMarker', 'currentSignal'])
  },

  created() {
    this.$bus.$on('SHOW_CORRIDOR_PCD', () => {
      this.showCooridorPCDCharts();
    });
  },

  methods: {
    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    isMobile() {
      return this.$vuetify.breakpoint.xsOnly;
    },
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.HR_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.HR_PCD ? 'orange' : 'teal';
        case 2:
          return this.$route.name === RouterNames.HR_SIGNAL ? 'orange' : 'teal';
        case 3:
          return this.$route.name === RouterNames.HR_PERFORMANCE_MEASURES ? 'orange' : 'teal';
      }
    },
    allowedDates(val) {
      let date0 = parseInt(val.split('-').join(''));
      let date1 = Utils.getTodayNumber();
      return date0 <= date1;
    },

    showPanel() {
      this.$store.commit('hr/TOGGLE_SHOW_PANEL');
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },
    showDashboard() {
      this.switchTo(RouterPaths.HR_DASHBOARD);
    },
    showPCD() {
      this.switchTo(RouterPaths.HR_PCD);
    },
    showSignal() {
      this.switchTo(RouterPaths.HR_SIGNAL);
    },
    showPerformanceMeasures() {
      this.switchTo(RouterPaths.HR_PERFORMANCE_MEASURES);
    },
    showCooridorPCDCharts() {
      this.switchTo(RouterPaths.HR_MULTI_PCD);
    },
    showCooridorOccCharts() {
      this.switchTo(RouterPaths.HR_MULTI_OCC);
    },

    menuItemClicked(idx) {
      switch (idx) {
        case 0:
          this.showCooridorPCDCharts();
          break;
        case 1:
          this.showCooridorOccCharts();
          break;
        case 2:
          this.toggleDarkMode();
          break;
      }
    },

    toggleDarkMode() {
      this.$store.dispatch('saveDarkMode', !this.$vuetify.theme.dark);
    }
  }
};
</script>

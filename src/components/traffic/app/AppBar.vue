<template>
  <div>
    <v-app-bar app dark flat dense :clipped-left="!isDashboard" :clipped-right="isDashboard" color="black">

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
      <v-toolbar-title v-show="$vuetify.breakpoint.mdAndUp">{{ title }}</v-toolbar-title>

      <v-spacer></v-spacer>
      <div v-if="!isDashboard">
        <MenuDatePicker :date="currentDate" @prev="prevDate" @next="nextDate" @setdate="dateSelected" />
      </div>

      <v-spacer></v-spacer>

      <div v-show="$vuetify.breakpoint.mdAndUp">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('dashboard')" icon v-on="on" @click.stop="showDashboard()">
              <v-icon>mdi-view-dashboard</v-icon>
            </v-btn>
          </template>
          <span>Dashboard</span>
        </v-tooltip>

        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <v-btn class="mx-1" fab :color="color('chart')" icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-icon>mdi-chart-line</v-icon>
                </v-btn>
              </template>
              <span>Show Charts</span>
            </v-tooltip>
          </template>

          <v-list>
            <template v-for="(item, index) in chart_menu_items">
              <v-divider v-if="item.divider" :key="index"></v-divider>
              <v-list-item v-else :key="index" @click="dataMenuItemClicked(item.action)">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item>
            </template>
          </v-list>
        </v-menu>

        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <v-btn class="mx-1" fab :color="color('anomaly')" icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-icon>mdi-view-comfy</v-icon>
                </v-btn>
              </template>
              <span>Anomaly Map</span>
            </v-tooltip>
          </template>

          <v-list>
            <v-list-item v-for="item in anomaly_menu_items" :key="item.id" @click="dataMenuItemClicked(item.action)">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('predict')" icon v-on="on" @click.stop="showTrafficPredict()">
              <v-icon>mdi-waveform</v-icon>
            </v-btn>
          </template>
          <span>Traffic Flow Prediction</span>
        </v-tooltip>
      </div>

      <!-- notification -->
      <v-divider vertical class="ml-2" />
      <v-menu offset-y origin="center center" class="elelvation-1" transition="scale-transition">
        <template #activator="{ on }">
          <v-btn slot="activator" icon text v-on="on">
            <div v-if="getNotification.length > 0">
              <v-badge color="red" overlap>
                <span slot="badge">{{ getNotification.length }}</span>
                <v-icon medium>mdi-bell</v-icon>
              </v-badge>
            </div>
            <div v-else>
              <v-icon medium>mdi-bell-outline</v-icon>
            </div>
          </v-btn>
        </template>
        <IncidentNotificationList v-show="getNotification.length > 0" :items="getNotification" />
      </v-menu>

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
            <template v-for="(item, index) in menu_items">
              <v-divider v-if="item.divider" :key="index"></v-divider>
              <v-list-item v-else :key="index" @click="menuItemClicked(item.action)">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item>
            </template>
          </v-list>
        </v-menu>
      </div>
    </v-app-bar>

    <NavDrawer />
    <SnackBar />
    <FlowChartDialog v-model="showFlowChart" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import MenuDatePicker from '@/components/common/MenuDatePicker';
import NavDrawer from '@/components/nav/NavDrawer';
import LoggedInUser from '@/components/common/LoggedInUser';
import SnackBar from '@/components/common/SnackBar';
import FlowChartDialog from '@/components/traffic/app/FlowChartDialog';
import IncidentNotificationList from '@/components/traffic/dashboard/IncidentNotificationList';
import { mapState, mapGetters } from 'vuex';
import Highcharts from 'highcharts';
import ChartStyles from '@/utils/ChartStyles.js';

export default {
  props: ['drawer'],

  components: {
    LoggedInUser,
    MenuDatePicker,
    NavDrawer,
    SnackBar,
    FlowChartDialog,
    IncidentNotificationList
  },

  data: () => ({
    title: 'Traffic Flow Data',
    menu_items: [
      { title: 'Combined Anomaly Map', action: 6 },
      { divider: true },
      { title: 'Toggle Dark Mode', action: 8 },
      { title: 'Toggle Fullscreen', action: 9 }
    ],

    chart_menu_items: [
      { title: 'Traffic Flow', action: Constants.PAGE_FLOW },
      { divider: true },
      { title: 'Travel Time Data', action: Constants.PAGE_TRAVEL_TIME_DATA },
      { title: 'Weather Data', action: Constants.PAGE_WEATHER },
      { divider: true },
      { title: 'Traffic LCM', action: Constants.PAGE_LCM }
    ],

    anomaly_menu_items: [
      { title: 'Traffic Flow Map', action: Constants.PAGE_ANOMALY },
      { title: 'Travel Time Map', action: Constants.PAGE_TRAVEL_TIME_MAP },
      { title: 'Historical Incidents', action: Constants.PAGE_INCIDENTS }
    ]
  }),

  computed: {
    icon() {
      return this.$store.state.traffic.showPanel ? 'mdi-close' : 'mdi-menu';
    },

    isDashboard() {
      return this.$route.name === 'TrafficDashboard';
    },

    user() {
      return this.$store.state.auth.user;
    },

    ...mapState('traffic', ['currentAction', 'currentDate', 'showFlowChart']),
    ...mapGetters('traffic', ['getNotification'])
  },

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

    color(name) {
      if (name === this.currentAction) {
        return 'orange';
      } else if (name === 'chart') {
        const item = this.chart_menu_items.find(item => item.action === this.currentAction);
        return item ? 'orange' : 'teal';
      } else if (name === 'anomaly') {
        const item = this.anomaly_menu_items.find(item => item.action === this.currentAction);
        return item ? 'orange' : 'teal';
      } else {
        return 'teal';
      }
    },

    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    dateSelected(date) {
      this.$store.commit('traffic/SET_CURRENT_DATE', date);
    },

    prevDate() {
      this.$store.dispatch('traffic/incCurrentDate', -1);
    },

    nextDate() {
      this.$store.dispatch('traffic/incCurrentDate', 1);
    },

    setCurrentAction(action) {
      this.$store.commit('traffic/SET_CURRENT_ACTION', action);
    },

    showDashboard() {
      this.setCurrentAction(Constants.PAGE_DASHBOARD);
      this.$router.push({ path: '/flow/' }).catch(() => {});
    },

    showTrafficFlow() {
      this.setCurrentAction(Constants.PAGE_FLOW);
      this.$router.push({ path: '/flow/trafficFlow' }).catch(() => {});
    },

    showTrafficLCM() {
      this.setCurrentAction(Constants.PAGE_LCM);
      this.$router.push({ path: '/flow/lcm' }).catch(() => {});
    },

    showTrafficPredict() {
      this.setCurrentAction(Constants.PAGE_PREDICT);
      this.$router.push({ path: '/flow/predict' }).catch(() => {});
    },

    showAnomalyMap() {
      this.setCurrentAction(Constants.PAGE_ANOMALY);
      this.$router.push({ path: '/flow/anomaly' }).catch(() => {});
    },

    showWeatherData() {
      this.setCurrentAction(Constants.PAGE_WEATHER);
      this.$router.push({ path: '/flow/weather' }).catch(() => {});
    },

    showTravelTimeData() {
      this.setCurrentAction(Constants.PAGE_TRAVEL_TIME_DATA);
      this.$router.push({ path: '/flow/travelTimeData' }).catch(() => {});
    },

    showTravelTimeMap() {
      this.setCurrentAction(Constants.PAGE_TRAVEL_TIME_MAP);
      this.$router.push({ path: '/flow/travelTimeMap' }).catch(() => {});
    },

    showHistoricalIncidents() {
      this.setCurrentAction(Constants.PAGE_INCIDENTS);
      this.$router.push({ path: '/flow/incidents' }).catch(() => {});
    },

    showCombinedAnomalyMap() {
      this.setCurrentAction(Constants.PAGE_COMBINED_ANOMALY);
      this.$router.push({ path: '/flow/combinedAnomaly' }).catch(() => {});
    },

    menuItemClicked(idx) {
      switch (idx) {
        case 0:
          this.showDashboard();
          break;
        case 1:
          this.showTrafficFlow();
          break;
        case 2:
          this.showTrafficPredict();
          break;
        case 3:
          this.showAnomalyMap();
          break;
        case 4:
          this.showWeatherData();
          break;
        case 5:
          this.showTravelTime();
          break;
        case 6:
          this.showCombinedAnomalyMap();
          break;
        case 7:
          this.showHistoricalIncidents();
          break;
        case 8:
          this.toggleDarkMode();
          break;
        case 9:
          Utils.toggleFullScreen();
          break;
      }
    },

    toggleDarkMode() {
      this.$store.dispatch('saveDarkMode', !this.$vuetify.theme.dark);
    },

    performTask(action) {
      switch (action) {
        case Constants.PAGE_FLOW:
          this.showTrafficFlow();
          break;

        case Constants.PAGE_LCM:
          this.showTrafficLCM();
          break;

        case Constants.PAGE_WEATHER:
          this.showWeatherData();
          break;

        case Constants.PAGE_TRAVEL_TIME_DATA:
          this.showTravelTimeData();
          break;

        case Constants.PAGE_ANOMALY:
          this.showAnomalyMap();
          break;

        case Constants.PAGE_TRAVEL_TIME_MAP:
          this.showTravelTimeMap();
          break;

        case Constants.PAGE_INCIDENTS:
          this.showHistoricalIncidents();
          break;
      }
    },

    dataMenuItemClicked(action) {
      setTimeout(() => {
        this.performTask(action);
      }, 100);
    }
  },

  watch: {
    '$store.state.darkMode'(darkMode) {
      this.$vuetify.theme.dark = darkMode;
      this.setHCTheme(darkMode);
      this.$bus.$emit('UPDATE_DARK_MODE', darkMode);
    }
  }
};
</script>

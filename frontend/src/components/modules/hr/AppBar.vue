<template>
  <Header :title="title" :showDate="!isDashboard" :actionItems="action_menu_items">
    <div v-show="$vuetify.breakpoint.mdAndUp">
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn disabled class="mx-1" fab :color="color(0)" icon v-on="on" @click.stop="showDashboard()">
            <v-icon>mdi-view-dashboard</v-icon>
          </v-btn>
        </template>
        <span>Dashboard</span>
      </v-tooltip>

      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn disabled class="mx-1" fab :color="color(1)" icon v-on="on" @click.stop="showPCD()">
            <v-icon>mdi-chart-areaspline</v-icon></v-btn
          >
        </template>
        <span>PCD</span>
      </v-tooltip>

      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn class="mx-1" fab :color="color(2)" icon v-on="on" @click.stop="showTimeSpaceDiagram">
            <v-icon>mdi-chart-timeline</v-icon></v-btn
          >
        </template>
        <span>Time Space Diagram</span>
      </v-tooltip>

      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn disabled class="mx-1" fab :color="color(3)" icon v-on="on" @click.stop="showSignal()">
            <v-icon>mdi-clock</v-icon>
          </v-btn>
        </template>
        <span>Signal Timing</span>
      </v-tooltip>

      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn disabled class="mx-1" fab :color="color(4)" icon v-on="on" @click.stop="showPerformanceMeasures()">
            <v-icon>mdi-chart-bar</v-icon>
          </v-btn>
        </template>
        <span>Performance Measures</span>
      </v-tooltip>
    </div>
    <!-- notification -->
    <!-- <v-divider vertical class="ml-1 hidden-xs-only" />
    <NotificationDropdown /> -->
    <v-divider vertical class="ml-1 hidden-xs-only" />
  </Header>
</template>

<script>
import Header from '@/components/common/Header';
import AppConstants from '@/utils/constants/app';
import { RouterNames, RouterPaths } from '@/utils/constants/router';
// import NotificationDropdown from '@/components/common/NotificationDropdown';

export default {
  components: {
    Header
    // NotificationDropdown
  },

  data: () => ({
    title: AppConstants.HR_APP_TITLE,
    action_menu_items: [
      { title: RouterNames.HR_DASHBOARD, url: RouterPaths.HR_DASHBOARD },
      { title: RouterNames.HR_PCD, url: RouterPaths.HR_PCD },
      { title: RouterNames.HR_TIME_SPACE_DIAGRAM, url: RouterPaths.HR_TIME_SPACE_DIAGRAM },
      { title: RouterNames.HR_SIGNAL_DISPLAY, url: RouterPaths.HR_SIGNAL_DISPLAY },
      { title: RouterNames.HR_PERFORMANCE_MEASURES, url: RouterPaths.HR_PERFORMANCE_MEASURES }
    ]
  }),
  computed: {
    isDashboard() {
      return this.$route.name === RouterNames.HR_DASHBOARD;
    }
  },

  methods: {
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.HR_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.HR_PCD ? 'orange' : 'teal';
        case 2:
          return this.$route.name === RouterNames.HR_TIME_SPACE_DIAGRAM ? 'orange' : 'teal';
        case 3:
          return this.$route.name === RouterNames.HR_SIGNAL_DISPLAY ? 'orange' : 'teal';
        case 4:
          return this.$route.name === RouterNames.HR_PERFORMANCE_MEASURES ? 'orange' : 'teal';
      }
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
    showTimeSpaceDiagram() {
      this.switchTo(RouterPaths.HR_TIME_SPACE_DIAGRAM);
    },
    showSignal() {
      this.switchTo(RouterPaths.HR_SIGNAL_DISPLAY);
    },
    showPerformanceMeasures() {
      this.switchTo(RouterPaths.HR_PERFORMANCE_MEASURES);
    },
    showCooridorPCDCharts() {
      this.switchTo(RouterPaths.HR_MULTI_PCD);
    },
    showCooridorOccCharts() {
      this.switchTo(RouterPaths.HR_MULTI_OCC);
    }
  }
};
</script>

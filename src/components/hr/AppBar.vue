<template>
  <div>
    <v-app-bar app dark flat dense clipped-left color="black">
      <NavigationDropdown />

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
      <v-toolbar-title v-show="$vuetify.breakpoint.mdAndUp">{{ title }}</v-toolbar-title>

      <v-spacer></v-spacer>
      <div v-if="!isDashboard">
        <v-menu
          ref="menu"
          v-model="menu"
          :close-on-content-click="false"
          :return-value.sync="date"
          transition="scale-transition"
          offset-y
        >
          <template v-slot:activator="{ on }">
            <v-text-field
              hide-details
              v-model="dateFormatted"
              label=""
              readonly
              v-on="on"
              style="width: 280px; color:grey;"
            >
              <template v-slot:prepend>
                <v-icon class="mt-1" color="grey">mdi-calendar-month</v-icon>
              </template>
            </v-text-field>
          </template>
          <v-date-picker v-model="date" :allowed-dates="allowedDates" no-title scrollable color="green lighten-1">
            <v-spacer></v-spacer>
            <v-btn text color="primary" @click="menu = false">Cancel</v-btn>
            <v-btn text color="primary" @click="dateSelected">OK</v-btn>
          </v-date-picker>
        </v-menu>
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

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('pcd')" icon v-on="on" @click.stop="showPCD()">
              <v-icon>mdi-chart-areaspline</v-icon></v-btn
            >
          </template>
          <span>Show PCD</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('signal')" icon v-on="on" @click.stop="showSignal()">
              <v-icon>mdi-clock</v-icon>
            </v-btn>
          </template>
          <span>Signal Timing</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('pref')" icon v-on="on" @click.stop="showPerformanceMeasures()">
              <v-icon>mdi-chart-bar</v-icon>
            </v-btn>
          </template>
          <span>Performance Measures</span>
        </v-tooltip>
      </div>
      <v-divider vertical />
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
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/hr';
import NavigationDropdown from '@/components/NavigationDropdown'
import { mapState } from 'vuex';

export default {
  props: ['drawer'],
  components: {
    NavigationDropdown,
  },
  data: () => ({
    date: Utils.formatDate(Utils.yesterday()),
    title: 'High Resolution Data',
    menu: false,
    menu_items: [
      { title: 'Corridor PCD Charts' },
      { title: 'Corridor Occupancy Charts' },
      { title: 'Toggle Dark Mode' }
    ],
    app_menu_items: [
      { title: 'Traffic Flow Data', url: '/flow' },
      { title: 'High Resolution Data', url: '/hr' },
      { title: 'Machine Vision on Traffic Cameras', url: '/vision' },
      { title: 'Bluetooth and Waze Data', url: '/bluetooth' },
      { title: 'CAV Data', url: '/cav' },
      { title: 'Health Monitoring', url: '/status' }
    ]
  }),
  computed: {
    icon() {
      return this.$store.state.hr.showPanel ? 'mdi-close' : 'mdi-menu';
    },
    currentDate() {
      return this.$store.state.hr.currentDate;
    },
    dateFormatted() {
      return this.date ? Utils.formatDateLong(Utils.dateFromString(this.date)) : '';
    },
    panelVisible() {
      return this.$store.state.hr.showPanel;
    },

    isDashboard() {
      return this.currentAction == Constants.PAGE_DASHBOARD;
    },

    showSelectionDlg: {
      get() {
        return this.$store.state.hr.showSelectionDlg;
      },
      set(show) {
        this.$store.commit('hr/SHOW_SELECTION_DLG', show);
      }
    },
    ...mapState('hr', ['activeMarker', 'currentSignal', 'currentDate', 'currentAction'])
  },

  created() {
    this.$bus.$on('SHOW_CORRIDOR_PCD', () => {
      this.showCooridorPCDCharts();
    });
  },

  methods: {
    isMobile() {
      return this.$vuetify.breakpoint.xsOnly;
    },
    color(name) {
      return this.currentAction === name ? 'orange' : 'teal';
    },
    allowedDates(val) {
      let date0 = parseInt(val.split('-').join(''));
      let date1 = Utils.getTodayNumber();
      return date0 <= date1;
    },
    dateSelected() {
      this.$refs.menu.save(this.date);
      this.$store.commit('hr/SET_CURRENT_DATE', Utils.dateFromString(this.date));
    },
    setCurrentAction(action) {
      this.$store.commit('hr/SET_CURRENT_ACTION', action);
    },
    showPanel() {
      this.$store.commit('hr/TOGGLE_SHOW_PANEL');
    },
    showDashboard() {
      this.setCurrentAction(Constants.PAGE_DASHBOARD);
      this.$router.push({ path: '/hr' }).catch(() => {});
    },
    showPCD() {
      this.setCurrentAction(Constants.PAGE_PCD);
      this.$router.push({ path: '/hr/pcd' }).catch(() => {});
    },
    showSignal() {
      this.setCurrentAction(Constants.PAGE_SIGNAL);
      this.$router.push({ path: '/hr/signal' }).catch(() => {});
    },
    showPerformanceMeasures() {
      this.setCurrentAction(Constants.PAGE_PREF);
      this.$router.push({ path: '/hr/pref' }).catch(() => {});
    },
    showCooridorPCDCharts() {
      this.setCurrentAction(Constants.PAGE_MULTI_PCD);
      this.$router.push({ path: '/hr/pcds' }).catch(() => {});
    },

    showCooridorOccCharts() {
      this.setCurrentAction(Constants.PAGE_MULTI_OCC);
      this.$router.push({ path: '/hr/occs' }).catch(() => {});
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
  },
};
</script>

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
      <v-toolbar-title v-show="$vuetify.breakpoint.mdAndUp">{{ title }}</v-toolbar-title>

      <v-spacer></v-spacer>
      <MenuDatePicker :date="currentDate" @prev="prevDate" @next="nextDate" @setdate="dateSelected" />
      <v-spacer></v-spacer>

      <div v-show="$vuetify.breakpoint.mdAndUp">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('dashboard')" icon v-on="on" @click.stop="showDashboard">
              <v-icon>mdi-cctv</v-icon>
            </v-btn>
          </template>
          <span>Traffic Cameras</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('reID')" icon v-on="on" @click.stop="showReID">
              <v-icon>mdi-car-2-plus</v-icon>
            </v-btn>
          </template>
          <span>Vehicle Re-ID</span>
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

    <v-snackbar v-model="snackbar.showing" :color="snackbar.color" :timeout="snackbar.timeout">
      {{ snackbar.text }}
      <v-btn text @click="snackbar.showing = false" class="float-right">Close</v-btn>
    </v-snackbar>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/vision';
import { mapState } from 'vuex';
import MenuDatePicker from '@/components/common/MenuDatePicker';
import SnackBar from '@/components/common/SnackBar';
import NavDrawer from '@/components/nav/NavDrawer';
import LoggedInUser from '@/components/common/LoggedInUser';

export default {
  props: ['drawer'],

  components: {
    LoggedInUser,
    MenuDatePicker,
    NavDrawer,
    SnackBar
  },

  data: () => ({
    date: Utils.formatDate(new Date()),
    menu: false,
    title: 'Machine Vision',
    menu_items: [{ title: 'Traffic Cameras' }, { title: 'Vehicle Re-ID' }, { title: 'Toggle Dark Mode' }]
  }),

  computed: {
    icon() {
      return this.$store.state.vision.showPanel ? 'mdi-close' : 'mdi-menu';
    },

    panelVisible() {
      return this.$store.state.vision.showPanel;
    },

    dateFormatted() {
      return this.date ? Utils.formatDateLong(Utils.dateFromString(this.date)) : '';
    },

    isDashboard() {
      return this.$route.name === 'VisionDashboard';
    },

    user() {
      return this.$store.state.auth.user;
    },

    ...mapState('vision', ['activeMarker', 'currentAction', 'currentDate'])
  },
  methods: {
    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    color(name) {
      return this.currentAction === name ? 'orange' : 'teal';
    },
    allowedDates(val) {
      let date0 = parseInt(val.split('-').join(''));
      let date1 = Utils.getTodayNumber();
      return date0 <= date1;
    },

    dateSelected(date) {
      this.$store.commit('vision/SET_CURRENT_DATE', date);
    },

    prevDate() {
      this.$store.dispatch('vision/incCurrentDate', -1);
    },

    nextDate() {
      this.$store.dispatch('vision/incCurrentDate', 1);
    },

    setCurrentAction(action) {
      this.$store.commit('vision/SET_CURRENT_ACTION', action);
    },
    showPanel() {
      this.$store.commit('vision/TOGGLE_SHOW_PANEL');
    },
    showDashboard() {
      this.setCurrentAction(Constants.PAGE_DASHBOARD);
      this.$router.push({ path: '/vision' }).catch(() => {});
    },
    showFlow() {
      this.setCurrentAction(Constants.PAGE_FLOW);
      this.$router.push({ path: '/vision/flow' }).catch(() => {});
    },
    showReID() {
      this.setCurrentAction(Constants.PAGE_RE_ID);
      this.$router.push({ path: '/vision/re-id' }).catch(() => {});
    },
    showCounts() {
      this.setCurrentAction(Constants.PAGE_COUNT);
      this.$router.push({ path: '/vision/count' }).catch(() => {});
    },
    showVideo() {
      this.setCurrentAction(Constants.PAGE_VIDEO);
      this.$router.push({ path: '/video' }).catch(() => {});
    },
    menuItemClicked(idx) {
      switch (idx) {
        case 0:
          this.showDashboard();
          break;
        case 1:
          this.showReID();
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

<style lang="scss" scoped>
.text-input-blue .v-text-field__slot input {
  color: #00f !important;
}
.options {
  position: absolute;
  right: 10px;
}
</style>

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
      <MenuDatePicker :date="currentDate" />
      <v-spacer></v-spacer>

      <div v-show="$vuetify.breakpoint.mdAndUp">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(0)" icon v-on="on" @click.stop="showDashboard">
              <v-icon>mdi-cctv</v-icon>
            </v-btn>
          </template>
          <span>Traffic Cameras</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(1)" icon v-on="on" @click.stop="showReID">
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
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import { RouterNames, RouterPaths } from '@/utils/constants/router';
import { mapState } from 'vuex';
import MenuDatePicker from '@/components/common/MenuDatePicker';
import NavDropdown from '@/components/common/NavDropdown';
import SnackBar from '@/components/common/SnackBar';
import NavDrawer from '@/components/nav/NavDrawer';
import LoggedInUser from '@/components/common/LoggedInUser';
import AppConstants from '@/utils/constants/app';

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
    date: Utils.formatDate(new Date()),
    menu: false,
    title: AppConstants.VISION_APP_TITLE,
    menu_items: [{ title: 'Traffic Cameras' }, { title: 'Vehicle Re-ID' }, { title: 'Toggle Dark Mode' }],
    action_menu_items: [
      { title: RouterNames.VISION_DASHBOARD, url: RouterPaths.VISION_DASHBOARD },
      { title: RouterNames.VISION_REID, url: RouterPaths.VISION_REID }
    ]
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
      return this.$route.name === RouterNames.VISION_DASHBOARD;
    },

    itemTitle() {
      return this.$route.name;
    },

    user() {
      return this.$store.state.auth.user;
    },

    ...mapState(['currentDate']),
    ...mapState('vision', ['activeMarker'])
  },
  methods: {
    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.VISION_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.VISION_REID ? 'orange' : 'teal';
      }
    },

    allowedDates(val) {
      let date0 = parseInt(val.split('-').join(''));
      let date1 = Utils.getTodayNumber();
      return date0 <= date1;
    },

    showPanel() {
      this.$store.commit('vision/TOGGLE_SHOW_PANEL');
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showDashboard() {
      this.switchTo(RouterPaths.VISION_DASHBOARD);
    },

    showReID() {
      this.switchTo(RouterPaths.VISION_REID);
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

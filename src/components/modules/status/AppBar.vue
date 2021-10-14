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
        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(0)" icon v-on="on" @click.stop="showDashboard()">
              <v-icon>mdi-list-status</v-icon>
            </v-btn>
          </template>
          <span>Service Health Status</span>
        </v-tooltip>

        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(1)" icon v-on="on" @click.stop="showQualityCheck()">
              <v-icon>mdi-check-decagram</v-icon>
            </v-btn>
          </template>
          <span>Traffic Flow Quality</span>
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
import MenuDatePicker from '@/components/common/MenuDatePicker';
import NavDrawer from '@/components/nav/NavDrawer';
import NavDropdown from '@/components/common/NavDropdown';
import SnackBar from '@/components/common/SnackBar';
import LoggedInUser from '@/components/common/LoggedInUser';
import { RouterNames, RouterPaths } from '@/utils/constants/router';
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
    title: AppConstants.STATUS_APP_TITLE,
    menu: false,
    menu_items: [{ title: 'Toggle Dark Mode' }],
    action_menu_items: [
      { title: RouterNames.STATUS_DASHBOARD, url: RouterPaths.STATUS_DASHBOARD },
      { title: RouterNames.STATUS_QUALITY_CHECK, url: RouterPaths.STATUS_QUALITY_CHECK },
    ]
  }),
  computed: {
    icon() {
      return this.$store.state.showPanel ? 'mdi-close' : 'mdi-menu';
    },

    isDashboard() {
      return this.$route.name === RouterNames.STATUS_DASHBOARD;
    },

    itemTitle() {
      return this.$route.name;
    },

    user() {
      return this.$store.state.auth.user;
    },

    ...mapState(['currentDate'])
  },
  methods: {
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.STATUS_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.STATUS_QUALITY_CHECK ? 'orange' : 'teal';
      }
    },

    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showDashboard() {
      this.switchTo(RouterPaths.STATUS_DASHBOARD);
    },

    showQualityCheck() {
      this.switchTo(RouterPaths.STATUS_QUALITY_CHECK);
    },

    toggleDarkMode() {
      this.$store.dispatch('saveDarkMode', !this.$vuetify.theme.dark);
    },

    menuItemClicked(idx) {
      switch (idx) {
        case 0:
          this.toggleDarkMode();
          break;
      }
    }
  }
};
</script>

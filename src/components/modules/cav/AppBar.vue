<template>
  <div>
    <v-app-bar app dark flat dense clipped-right color="dark">
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

      <div v-show="$vuetify.breakpoint.smAndUp">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(0)" icon v-on="on" @click.stop="showTripData">
              <v-icon>mdi-format-list-bulleted</v-icon>
            </v-btn>
          </template>
          <span>Trip Data</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color(1)" icon v-on="on" @click.stop="showTripStatus">
              <v-icon>mdi-truck-check-outline </v-icon>
            </v-btn>
          </template>
          <span>Trip Status</span>
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
    <LoadingProgress :loading="progressLoading" color="primary" />
  </div>
</template>

<script>
import NavDrawer from '@/components/nav/NavDrawer';
import LoggedInUser from '@/components/common/LoggedInUser';
import NavDropdown from '@/components/common/NavDropdown';
import SnackBar from '@/components/common/SnackBar';
import LoadingProgress from '@/components/modules/cav/LoadingProgress';
import { RouterNames, RouterPaths } from '@/utils/constants/router';
import AppConstants from '@/utils/constants/app';
import { mapState } from 'vuex';

export default {
  props: ['drawer'],
  components: {
    NavDropdown,
    LoggedInUser,
    NavDrawer,
    SnackBar,
    LoadingProgress
  },
  data: () => ({
    title: AppConstants.CAV_APP_TITLE,
    menu_items: [{ title: 'Trip Status' }, { title: 'Trip Data' }, { title: 'Toggle Dark Mode' }],
    action_menu_items: [
      { title: RouterNames.CAV_DASHBOARD, url: RouterPaths.CAV_DASHBOARD },
      { title: RouterNames.CAV_STATUS, url: RouterPaths.CAV_STATUS }
    ]
  }),

  computed: {
    user() {
      return this.$store.state.auth.user;
    },

    itemTitle() {
      return this.$route.name;
    },

    ...mapState('cav', ['progressLoading'])
  },

  methods: {
    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.CAV_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.CAV_STATUS ? 'orange' : 'teal';
      }
    },

    togglePanel() {
      this.$store.commit('cav/TOGGLE_SHOW_PANEL');
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showTripStatus() {
      this.switchTo(RouterPaths.CAV_STATUS);
    },

    showTripData() {
      this.switchTo(RouterPaths.CAV_DASHBOARD);
    },

    menuItemClicked(idx) {
      switch (idx) {
        case 0:
          this.showTripStatus();
          break;
        case 1:
          this.showTripData();
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
</style>

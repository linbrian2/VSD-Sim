<template>
  <v-navigation-drawer
    v-model="showDrawer"
    class="app-drawer"
    temporary
    hide-overlay
    fixed
    :mini-variant.sync="mini"
    mini-variant-width="64"
    width="350"
  >
    <v-toolbar dense flat fixed overflow dark>
      <v-icon class="mr-6">mdi-view-grid-outline</v-icon>
      <v-toolbar-title>
        <span class="hidden-sm-and-down">AI-TOMS</span>
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <slot name="buttons"></slot>
        <v-btn icon small @click="showDrawer = false"><v-icon>mdi-close</v-icon></v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <vue-perfect-scrollbar class="app-drawer__scrollbar">
      <div class="app-drawer__inner">
        <nav-list :items="menuItems" :mini="mini" />
      </div>
    </vue-perfect-scrollbar>

    <template #append>
      <v-divider></v-divider>
      <div class="d-flex justify-space-between" :class="color">
        <v-tooltip top>
          <template v-slot:activator="{ on }">
            <v-btn icon tile v-on="on" class="mx-auto" @click.stop="handleDarkMode">
              <v-icon>mdi-theme-light-dark </v-icon>
            </v-btn>
          </template>
          <span>Toggle Dark Theme</span>
        </v-tooltip>

        <v-tooltip top>
          <template v-slot:activator="{ on }">
            <v-btn icon tile v-on="on" class="mx-auto" @click.stop="handleFullScreen">
              <v-icon>mdi-fullscreen</v-icon>
            </v-btn>
          </template>
          <span>Toggle Fullscreen</span>
        </v-tooltip>

        <v-tooltip top>
          <template v-slot:activator="{ on }">
            <v-btn icon tile v-on="on" class="mx-auto" @click.stop="handleSettings">
              <v-icon>mdi-cog-outline</v-icon>
            </v-btn>
          </template>
          <span>Settings</span>
        </v-tooltip>
      </div>
    </template>
  </v-navigation-drawer>
</template>

<script>
import VuePerfectScrollbar from 'vue-perfect-scrollbar';
import Utils from '@/utils/Utils';
import NavList from '@/components/nav/NavList';
import routes from '@/components/nav/configs';
import { RouterPaths } from '@/utils/constants/router';

export default {
  name: 'AppDrawer',
  components: {
    VuePerfectScrollbar,
    NavList
  },

  props: {},
  data() {
    return {
      mini: false,
      scrollSettings: {
        maxScrollbarLength: 160
      }
    };
  },

  computed: {
    showDrawer: {
      get() {
        return this.$store.state.showDrawer;
      },
      set(show) {
        this.$store.commit('SHOW_DRAWER', show);
      }
    },

    color() {
      return this.$store.state.darkMode ? 'grey darken-3' : '';
    },

    menuItems() {
      return this.filterRouteItem(routes[0].children);
    }
  },

  mounted() {
    // Because switch different apps will reload the NavDrawer, we need to specifically close the drawer if
    // it is reloaded, otherwise the drawer will remain opened.
    // Of course, if it stays in the same app(from example flow), then click an item will automatically close the drawer
    setTimeout(() => {
      this.showDrawer = false;
    }, 300);
  },

  methods: {
    filterRouteItem(routes) {
      return routes
        .filter(item => item.meta.hidden !== true)
        .map(item => ({
          title: item.meta.title,
          icon: item.meta.icon,
          path: item.path,
          isNew: false,
          children: item.children ? this.filterRouteItem(item.children) : []
        }));
    },

    handleDarkMode() {
      this.$store.dispatch('saveDarkMode', !this.$vuetify.theme.dark);
    },

    handleFullScreen() {
      Utils.toggleFullScreen();
    },

    handleSettings() {
      this.$bus.$emit('TOGGLE_SETTINGS_DIALOG', true);
    }
  }
};
</script>

<style lang="sass" scoped>
.app-drawer
  &__srollbar
    max-height:  calc(100vh - 64px - 36px - 44px)
  &__inner
    height: calc(100vh - 64px - 36px - 44px)
</style>

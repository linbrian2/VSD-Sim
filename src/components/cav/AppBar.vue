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

      <v-toolbar-title v-show="$vuetify.breakpoint.mdAndUp">{{ title }}</v-toolbar-title>

      <v-spacer></v-spacer>

      <div v-show="$vuetify.breakpoint.smAndUp">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('data')" icon v-on="on" @click.stop="showTripData">
              <v-icon>mdi-format-list-bulleted</v-icon>
            </v-btn>
          </template>
          <span>Trip Data</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('status')" icon v-on="on" @click.stop="showTripStatus">
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
import SnackBar from '@/components/common/SnackBar';
import LoadingProgress from '@/components/cav/LoadingProgress';
import { mapState } from 'vuex';

export default {
  props: ['drawer'],
  components: {
    LoggedInUser,
    NavDrawer,
    SnackBar,
    LoadingProgress
  },
  data: () => ({
    title: 'CAV Data',
    menu_items: [{ title: 'Trip Status' }, { title: 'Trip Data' }, { title: 'Toggle Dark Mode' }]
  }),

  computed: {
    user() {
      return this.$store.state.auth.user;
    },

    ...mapState('cav', ['currentAction', 'progressLoading'])
  },

  methods: {
    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    color(name) {
      return this.currentAction === name ? 'orange' : 'white';
    },

    setCurrentAction(action) {
      this.$store.commit('cav/SHOW_PANEL', false);
      this.$store.commit('cav/SET_CURRENT_ACTION', action);
    },

    togglePanel() {
      this.$store.commit('cav/TOGGLE_SHOW_PANEL');
    },

    showTripStatus() {
      this.setCurrentAction('status');
      this.$router.push({ path: '/cav/status' }).catch(() => {});
    },

    showTripData() {
      this.setCurrentAction('data');
      this.$router.push({ path: '/cav' }).catch(() => {});
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

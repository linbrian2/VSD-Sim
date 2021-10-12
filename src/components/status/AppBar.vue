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
      <div v-if="!isDashboard">
        <MenuDatePicker :date="currentDate" @prev="prevDate" @next="nextDate" @setdate="dateSelected" />
      </div>
      <v-spacer></v-spacer>

      <div v-show="$vuetify.breakpoint.mdAndUp">
        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('dashboard')" icon v-on="on" @click.stop="showDashboard()">
              <v-icon>mdi-list-status</v-icon>
            </v-btn>
          </template>
          <span>Service Health Status</span>
        </v-tooltip>

        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('quality')" icon v-on="on" @click.stop="showQualityCheck()">
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
import SnackBar from '@/components/common/SnackBar';
import LoggedInUser from '@/components/common/LoggedInUser';
import { mapState } from 'vuex';

export default {
  props: ['drawer'],
  components: {
    LoggedInUser,
    MenuDatePicker,
    NavDrawer,
    SnackBar
  },
  data: () => ({
    title: 'System Monitoring',
    menu: false,
    menu_items: [{ title: 'Toggle Dark Mode' }]
  }),
  computed: {
    icon() {
      return this.$store.state.showPanel ? 'mdi-close' : 'mdi-menu';
    },

    isDashboard() {
      return this.$route.name === 'StatusDashboard';
    },

    user() {
      return this.$store.state.auth.user;
    },

    ...mapState('status', ['currentDate', 'currentAction'])
  },
  methods: {
    color(name) {
      return this.currentAction === name ? 'orange' : 'teal';
    },

    setCurrentAction(action) {
      this.$store.commit('status/SET_CURRENT_ACTION', action);
    },

    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    dateSelected(date) {
      this.$store.commit('status/SET_CURRENT_DATE', date);
    },

    prevDate() {
      this.$store.dispatch('status/incCurrentDate', -1);
    },

    nextDate() {
      this.$store.dispatch('status/incCurrentDate', 1);
    },

    showDashboard() {
      this.setCurrentAction('dashboard');
      this.$router.push({ path: '/status' }).catch(() => {});
    },

    showQualityCheck() {
      this.setCurrentAction('quality');
      this.$router.push({ path: '/status/quality' }).catch(() => {});
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

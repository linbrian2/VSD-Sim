<template>
  <div>
    <v-app-bar app dark flat dense clipped-left color="black">
      <NavigationDropdown :title="title" />

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
      <v-divider vertical />
        <div>
          <v-menu bottom right offset-y>
            <template v-slot:activator="{ on, attrs }">
              <v-btn icon v-bind="attrs" v-on="on">
                <v-icon>mdi-dots-vertical</v-icon>
              </v-btn>
            </template>
  
            <v-list>
              <v-list-item v-for="(item, i) in menu_items" :key="i"      @click="menuItemClicked(i)">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </div>

      <LoggedInUser v-if="user.name" :user="user" />
    </v-app-bar>
  </div>
</template>

<script>
import NavigationDropdown from '@/components/NavigationDropdown'
import LoggedInUser from '@/components/status/LoggedInUser';
import { mapState } from 'vuex';

export default {
  props: ['drawer'],
  components: { 
    LoggedInUser,
    NavigationDropdown,
  },
  data: () => ({
    title: 'Health Monitoring',
    menu: false,
    menu_items: [
      { title: 'Toggle Dark Mode' }
    ],

  }),
  computed: {
    icon() {
      return this.$store.state.showPanel ? 'mdi-close' : 'mdi-menu';
    },

    isDashboard() {
      return this.currentAction == 'dashboard';
    },

    user() {
      const name = this.currentUser['X-Auth-Name'];
      const email = this.currentUser['X-Auth-Email'];
      return { name, email };
    },
    ...mapState('status', ['currentUser', 'currentDate', 'currentAction'])
  },
  methods: {
    color(name) {
      return this.currentAction === name ? 'orange' : 'teal';
    },

    setCurrentAction(action) {
      this.$store.commit('status/SET_CURRENT_ACTION', action);
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
    },
  },
};
</script>

<template>
  <Header :title="title" :showDate="false" :actionItems="action_menu_items" @menuItemclick="menuItemClicked">
    <div v-show="$vuetify.breakpoint.mdAndUp">
      <v-tooltip left>
        <template v-slot:activator="{ on }">
          <v-btn class="mx-1" fab :color="color(0)" icon v-on="on" @click.stop="showDashboard()">
            <v-icon>mdi-view-dashboard</v-icon>
          </v-btn>
        </template>
        <span>Dashboard</span>
      </v-tooltip>
    </div>
    <v-divider vertical />
  </Header>
</template>

<script>
import Header from '@/components/common/Header.vue';
import AppConstants from '@/utils/constants/app';
import { RouterNames, RouterPaths } from '@/utils/constants/router';

export default {
  components: {
    Header
  },
  data: () => ({
    title: AppConstants.TRAFFIC_ROUTING_APP_TITLE,
    action_menu_items: [
      { title: RouterNames.TRAFFIC_ROUTING_DASHBOARD, url: RouterPaths.TRAFFIC_ROUTING_DASHBOARD }
    ],
    menu_items: [
      { title: 'Dashboard', action: 0 },
    ]
  }),
  methods: {
    menuItemClicked(i) {
      switch (i) {
        case 0:
          this.showDashboard();
          break;
      }
    },
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.TRAFFIC_ROUTING_DASHBOARD ? 'orange' : 'teal';
      }
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showDashboard() {
      this.switchTo(RouterPaths.TRAFFIC_ROUTING_DASHBOARD);
    },
  }
};
</script>

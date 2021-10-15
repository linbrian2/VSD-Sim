<template>
  <Header :title="title" :actionItems="action_menu_items">
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
  </Header>
</template>

<script>
import Header from '@/components/common/Header';
import AppConstants from '@/utils/constants/app';
import { RouterNames, RouterPaths } from '@/utils/constants/router';

export default {
  components: {
    Header
  },
  data: () => ({
    title: AppConstants.STATUS_APP_TITLE,
    action_menu_items: [
      { title: RouterNames.STATUS_DASHBOARD, url: RouterPaths.STATUS_DASHBOARD },
      { title: RouterNames.STATUS_QUALITY_CHECK, url: RouterPaths.STATUS_QUALITY_CHECK }
    ]
  }),
  methods: {
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.STATUS_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.STATUS_QUALITY_CHECK ? 'orange' : 'teal';
      }
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showDashboard() {
      this.switchTo(RouterPaths.STATUS_DASHBOARD);
    },

    showQualityCheck() {
      this.switchTo(RouterPaths.STATUS_QUALITY_CHECK);
    }
  }
};
</script>

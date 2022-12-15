<template>
  <Header :title="title" :actionItems="action_menu_items">
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
          <v-btn class="mx-1" fab :color="color(1)" icon v-on="on" @click.stop="showMultiview">
            <v-icon>mdi-table</v-icon>
          </v-btn>
        </template>
        <span>Camera Multiview</span>
      </v-tooltip>
    </div>
  </Header>
</template>

<script>
import Header from '@/components/common/Header';
import { RouterNames, RouterPaths } from '@/utils/constants/router';
import AppConstants from '@/utils/constants/app';

export default {
  components: {
    Header
  },

  data: () => ({
    title: AppConstants.VISION_APP_TITLE,
    action_menu_items: [
      { title: RouterNames.VISION_DASHBOARD, url: RouterPaths.VISION_DASHBOARD },
      { title: RouterNames.VISION_MULTIVIEW, url: RouterPaths.VISION_MULTIVIEW }
    ]
  }),

  methods: {
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.VISION_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.VISION_MULTIVIEW ? 'orange' : 'teal';
      }
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showDashboard() {
      this.switchTo(RouterPaths.VISION_DASHBOARD);
    },

    showMultiview() {
      this.switchTo(RouterPaths.VISION_MULTIVIEW);
    }
  }
};
</script>

<template>
  <div>
    <Header :title="title" :showDate="false" :clipLeft="false" clipRight :actionItems="action_menu_items">
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
    </Header>
    <LoadingProgress :loading="progressLoading" color="primary" />
  </div>
</template>

<script>
import { mapState } from 'vuex';
import Header from '@/components/common/Header';
import AppConstants from '@/utils/constants/app';
import LoadingProgress from '@/components/modules/cav/LoadingProgress';
import { RouterNames, RouterPaths } from '@/utils/constants/router';

export default {
  components: {
    Header,
    LoadingProgress
  },
  data: () => ({
    title: AppConstants.CAV_APP_TITLE,
    action_menu_items: [
      { title: RouterNames.CAV_DASHBOARD, url: RouterPaths.CAV_DASHBOARD },
      { title: RouterNames.CAV_STATUS, url: RouterPaths.CAV_STATUS },
    ]
  }),
  computed: {
    ...mapState('cav', ['progressLoading'])
  },

  methods: {
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.CAV_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.CAV_STATUS ? 'orange' : 'teal';
      }
    },
    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showTripStatus() {
      this.switchTo(RouterPaths.CAV_STATUS);
    },

    showTripData() {
      this.switchTo(RouterPaths.CAV_DASHBOARD);
    }
  }
};
</script>

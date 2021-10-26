<template>
  <Header :title="title" :actionItems="action_menu_items">
    <div v-show="$vuetify.breakpoint.mdAndUp">
      <v-tooltip left>
        <template v-slot:activator="{ on }">
          <v-btn class="mx-1" fab :color="color(0)" icon v-on="on" @click.stop="showDashboard()">
            <v-icon>mdi-view-dashboard</v-icon>
          </v-btn>
        </template>
        <span>Dashboard</span>
      </v-tooltip>

      <v-tooltip left>
        <template v-slot:activator="{ on }">
          <v-btn class="mx-1" fab :color="color(1)" icon v-on="on" @click.stop="showMultigraph()">
            <v-icon>mdi-chart-areaspline</v-icon>
          </v-btn>
        </template>
        <span>Multigraph</span>
      </v-tooltip>

      <v-tooltip left>
        <template v-slot:activator="{ on }">
          <v-btn class="mx-1" fab :color="color(2)" icon v-on="on" @click.stop="showBreakdownProb()">
            <v-icon>mdi-dice-6</v-icon>
          </v-btn>
        </template>
        <span>Breakdown Probability</span>
      </v-tooltip>
    </div>
  </Header>
</template>

<script>
import { mapState } from 'vuex';
import Header from '@/components/common/Header';
import AppConstants from '@/utils/constants/app';
import { RouterNames, RouterPaths } from '@/utils/constants/router';

export default {
  components: {
    Header
  },
  data: () => ({
    title: AppConstants.BLUETOOTH_APP_TITLE,
    action_menu_items: [
      { title: RouterNames.BLUETOOTH_NEW_DASHBOARD, url: RouterPaths.BLUETOOTH_NEW_DASHBOARD },
      { title: RouterNames.BLUETOOTH_NEW_MULTIGRAPH, url: RouterPaths.BLUETOOTH_NEW_MULTIGRAPH },
      { title: RouterNames.BLUETOOTH_NEW_BREAKDOWNPROB, url: RouterPaths.BLUETOOTH_NEW_BREAKDOWNPROB }
    ]
  }),
  computed: {
    ...mapState(['currentDate'])
  },
  methods: {
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.BLUETOOTH_NEW_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.BLUETOOTH_NEW_MULTIGRAPH ? 'orange' : 'teal';
        case 2:
          return this.$route.name === RouterNames.BLUETOOTH_NEW_BREAKDOWNPROB ? 'orange' : 'teal';
      }
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showDashboard() {
      this.switchTo(RouterPaths.BLUETOOTH_NEW_DASHBOARD);
    },

    showMultigraph() {
      this.switchTo(RouterPaths.BLUETOOTH_NEW_MULTIGRAPH);
    },

    showBreakdownProb() {
      this.switchTo(RouterPaths.BLUETOOTH_NEW_BREAKDOWNPROB);
    }
  }
};
</script>

<template>
  <Header :title="title" :actionItems="action_menu_items" :dotItems="menu_items" @menuItemclick="menuItemClicked">
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
    <v-divider vertical />
  </Header>
</template>

<script>
import { mapState } from 'vuex';
import Header from '@/components/modules/bluetooth/ui/Header.vue';
import AppConstants from '@/utils/constants/app';
import { RouterNames, RouterPaths } from '@/utils/constants/router';

export default {
  components: {
    Header
  },
  data: () => ({
    title: AppConstants.BLUETOOTH_APP_TITLE,
    action_menu_items: [
      { title: RouterNames.BLUETOOTH_DASHBOARD, url: RouterPaths.BLUETOOTH_DASHBOARD },
      { title: RouterNames.BLUETOOTH_MULTIGRAPH, url: RouterPaths.BLUETOOTH_MULTIGRAPH },
      { title: RouterNames.BLUETOOTH_BREAKDOWNPROB, url: RouterPaths.BLUETOOTH_BREAKDOWNPROB }
    ],
    menu_items: [
      { title: 'Dashboard', action: 0 },
      { title: 'Multi-graph', action: 1 },
      { title: 'Breakdown Probability', action: 2 },
      { title: 'Reset Map', action: 3 },
      { title: 'Reset Date/Time', action: 4 },
      { title: 'Toggle Auto-update', action: 5 }
    ]
  }),
  computed: {
    ...mapState(['currentDate'])
  },
  methods: {
    menuItemClicked(i) {
      console.log(`Item ${i} clicked`);
      switch (i) {
        case 0:
          this.showDashboard();
          break;
        case 1:
          this.showMultigraph();
          break;
        case 2:
          this.showBreakdownProb();
          break;
        case 3:
          this.$store.state.bluetooth.map.setCenter({ lat: 39.14, lng: -75.5 });
          this.$store.state.bluetooth.map.setZoom(9);
          break;
        case 4:
          this.$store.state.currentDate = new Date();
          break;
        case 5:
          {
            let notifText = 'Auto-update enabled.';
            if (this.$store.state.bluetooth.autoUpdate) {
              notifText = 'Auto-update disabled.';
            }
            this.$store.commit('bluetooth/SET_NOTIFICATION', {
              show: true,
              text: notifText,
              timeout: 2500,
              color: 'info'
            });
            this.$store.state.bluetooth.autoUpdate = !this.$store.state.bluetooth.autoUpdate;
          }
          break;
        default:
          alert(`Item ${i}: Not implemented.`);
          break;
      }
    },
    color(idx) {
      switch (idx) {
        case 0:
          return this.$route.name === RouterNames.BLUETOOTH_DASHBOARD ? 'orange' : 'teal';
        case 1:
          return this.$route.name === RouterNames.BLUETOOTH_MULTIGRAPH ? 'orange' : 'teal';
        case 2:
          return this.$route.name === RouterNames.BLUETOOTH_BREAKDOWNPROB ? 'orange' : 'teal';
      }
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showDashboard() {
      this.switchTo(RouterPaths.BLUETOOTH_DASHBOARD);
    },

    showMultigraph() {
      this.switchTo(RouterPaths.BLUETOOTH_MULTIGRAPH);
    },

    showBreakdownProb() {
      this.switchTo(RouterPaths.BLUETOOTH_BREAKDOWNPROB);
    }
  }
};
</script>

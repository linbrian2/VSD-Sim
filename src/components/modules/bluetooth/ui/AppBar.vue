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
      { title: RouterNames.BLUETOOTH_MULTIGRAPH, url: RouterPaths.BLUETOOTH_MULTIGRAPH }
    ],
    menu_items: [
      { title: 'Dashboard', action: 0 },
      { title: 'Multi-graph', action: 1 },
      { title: 'Reset Map', action: 2 },
      { title: 'Reset Date/Time', action: 3 },
      { title: 'Toggle Auto-update', action: 4 },
      { title: 'Toggle Full Day Availability Icons', action: 5 }
    ]
  }),
  computed: {
    map() {
      return this.$store.state.bluetooth.map;
    },
    autoUpdate: {
      get() {
        return this.$store.state.bluetooth.autoUpdate;
      },
      set(val) {
        this.$store.commit('bluetooth/SET_AUTO_UPDATE', val);
      }
    },
    showFullDayAvail: {
      get() {
        return this.$store.state.bluetooth.showFullDayAvail;
      },
      set(val) {
        this.$store.commit('bluetooth/SET_FULL_DAY_AVAIL', val);
      }
    },
    ...mapState(['currentDate'])
  },
  methods: {
    menuItemClicked(i) {
      switch (i) {
        case 0:
          this.showDashboard();
          break;
        case 1:
          this.showMultigraph();
          break;
        case 2:
          this.map.setCenter({ lat: 39.14, lng: -75.5 });
          this.map.setZoom(9);
          break;
        case 3:
          this.$store.commit('SET_CURRENT_DATE', new Date());
          break;
        case 4:
          {
            let notifText = 'Auto-update enabled.';
            if (this.autoUpdate) {
              notifText = 'Auto-update disabled.';
            }
            this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
            this.autoUpdate = !this.autoUpdate;
          }
          break;
        case 5:
          this.showFullDayAvail = !this.showFullDayAvail;
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
    }
  }
};
</script>

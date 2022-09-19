<template>
  <div>
    <Header
      :title="title"
      :showDate="false"
      :showWeatherInfo="true"
      :showCurrentTime="true"
      :clipLeft="false"
      :clipRight="true"
      :actionItems="action_menu_items"
    >
      <div v-show="$vuetify.breakpoint.mdAndUp">
        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <v-btn class="mx-1" fab :color="color('dashboard')" icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-icon>mdi-menu</v-icon>
                </v-btn>
              </template>
              <span>Dashboard</span>
            </v-tooltip>
          </template>

          <v-list>
            <template v-for="(item, index) in dash_menu_items">
              <v-divider v-if="item.divider" :key="index"></v-divider>
              <v-list-item v-else :key="index" @click="dataMenuItemClicked(item.url)">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item>
            </template>
          </v-list>
        </v-menu>
      </div>
      <!-- notification -->
      <NotificationDropdown />
      <v-divider vertical class="ml-2" />
    </Header>
  </div>
</template>

<script>
import { RouterNames, RouterPaths } from '@/utils/constants/router';
import AppConstants from '@/utils/constants/app';
import Header from '@/components/common/Header';
import NotificationDropdown from '@/components/common/NotificationDropdown';

export default {
  components: {
    Header,
    NotificationDropdown
  },

  data: () => ({
    dash_menu_items: [
      { title: RouterNames.TRAFFIC_MULTIGRAPH, url: RouterPaths.TRAFFIC_MULTIGRAPH },
      { divider: true },
      { title: RouterNames.TRAFFIC_RESPONSIVE_DATA, url: RouterPaths.TRAFFIC_RESPONSIVE_DATA },
      { divider: true },
      { title: RouterNames.TRAFFIC_ANOMALY, url: RouterPaths.TRAFFIC_ANOMALY },
      { title: RouterNames.TRAVEL_TIME_MAP, url: RouterPaths.TRAVEL_TIME_MAP },
      { title: RouterNames.TRAFFIC_INCIDENT, url: RouterPaths.TRAFFIC_INCIDENT },
      { divider: true },
      { title: RouterNames.TRAFFIC_PREDICT, url: RouterPaths.TRAFFIC_PREDICT }
    ],
    isDashboard: true,
    title: AppConstants.DASHBOARD_APP_TITLE,
    action_menu_items: [
      // { title: RouterNames.TRAFFIC_DASHBOARD, url: RouterPaths.TRAFFIC_DASHBOARD }
    ]
  }),

  methods: {
    color(name) {
      if (name === 'dashboard') {
        const item = this.dash_menu_items.find(item => item.name === this.$route.name);
        return item ? 'orange' : 'teal';
      }
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showTrafficDashboard() {
      this.switchTo(RouterPaths.TRAFFIC_DASHBOARD);
    },

    showBluetoothDashboard() {
      this.switchTo(RouterPaths.BLUETOOTH_DASHBOARD);
    },
    performTask(name) {
      switch (name) {
        case RouterNames.TRAFFIC_DASHBOARD:
          this.showTrafficDashboard();
          break;

        case RouterNames.BLUETOOTH_DASHBOARD:
          this.showBluetoothDashboard();
          break;
      }
    },

    dataMenuItemClicked(url) {
      setTimeout(() => {
        this.switchTo(url);
      }, 100);
    }
  }
};
</script>

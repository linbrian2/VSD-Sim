<template>
  <div>
    <Header
      :title="title"
      :showDate="!isDashboard"
      :clipLeft="!isDashboard"
      :clipRight="isDashboard"
      :actionItems="action_menu_items"
    >
      <div v-show="$vuetify.breakpoint.mdAndUp">
        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <v-btn class="mx-1" fab :color="color('dashboard')" icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-icon>mdi-view-dashboard</v-icon>
                </v-btn>
              </template>
              <span>Dashboard</span>
            </v-tooltip>
          </template>

          <v-list>
            <template v-for="(item, index) in dash_menu_items">
              <v-divider v-if="item.divider" :key="index"></v-divider>
              <v-list-item v-else :key="index" @click="dataMenuItemClicked(item.name)">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item>
            </template>
          </v-list>
        </v-menu>

        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <v-btn class="mx-1" fab :color="color('chart')" icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-icon>mdi-chart-line</v-icon>
                </v-btn>
              </template>
              <span>Show Charts</span>
            </v-tooltip>
          </template>

          <v-list>
            <template v-for="(item, index) in chart_menu_items">
              <v-divider v-if="item.divider" :key="index"></v-divider>
              <v-list-item v-else :key="index" @click="dataMenuItemClicked(item.name)">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item>
            </template>
          </v-list>
        </v-menu>

        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on: menu, attrs }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <v-btn class="mx-1" fab :color="color('anomaly')" icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                  <v-icon>mdi-view-comfy</v-icon>
                </v-btn>
              </template>
              <span>Anomaly Map</span>
            </v-tooltip>
          </template>

          <v-list>
            <v-list-item v-for="item in anomaly_menu_items" :key="item.id" @click="dataMenuItemClicked(item.name)">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-1" fab :color="color('predict')" icon v-on="on" @click.stop="showTrafficPredict()">
              <v-icon>mdi-waveform</v-icon>
            </v-btn>
          </template>
          <span>Traffic Flow Prediction</span>
        </v-tooltip>
      </div>

      <!-- notification -->
      <v-divider vertical class="ml-2" />
      <v-menu offset-y origin="center center" class="elelvation-1" transition="scale-transition">
        <template #activator="{ on }">
          <v-btn slot="activator" icon text v-on="on">
            <div v-if="getNotification.length > 0">
              <v-badge color="red" overlap>
                <span slot="badge">{{ getNotification.length }}</span>
                <v-icon medium>mdi-bell</v-icon>
              </v-badge>
            </div>
            <div v-else>
              <v-icon medium>mdi-bell-outline</v-icon>
            </div>
          </v-btn>
        </template>
        <IncidentNotificationList v-show="getNotification.length > 0" :items="getNotification" />
      </v-menu>
    </Header>
    <FlowChartDialog v-model="showFlowChart" />
  </div>
</template>

<script>
import { RouterNames, RouterPaths } from '@/utils/constants/router';
import AppConstants from '@/utils/constants/app';
import Header from '@/components/common/Header';
import FlowChartDialog from '@/components/modules/traffic/app/FlowChartDialog';
import IncidentNotificationList from '@/components/modules/traffic/dashboard/IncidentNotificationList';
import { mapState, mapGetters } from 'vuex';

export default {
  components: {
    Header,
    FlowChartDialog,
    IncidentNotificationList
  },

  data: () => ({
    title: AppConstants.TRAFFIC_APP_TITLE,

    dash_menu_items: [
      { title: 'Dashboard', name: RouterNames.TRAFFIC_DASHBOARD },
      { title: 'Bluetooth Dashboard', name: RouterNames.TRAFFIC_BT_DASHBOARD }
    ],

    chart_menu_items: [
      { title: 'Traffic Flow Data', name: RouterNames.TRAFFIC_FLOW },
      { title: 'Travel Time Data', name: RouterNames.TRAVEL_TIME_DATA },
      { title: 'Weather Data', name: RouterNames.TRAFFIC_WEATHER },
      { divider: true },
      { title: 'Multigraph', name: RouterNames.TRAFFIC_MULTIGRAPH },
      { divider: true },
      { title: 'Traffic LCM', name: RouterNames.TRAFFIC_LCM }
    ],

    anomaly_menu_items: [
      { title: 'Traffic Flow Map', name: RouterNames.TRAFFIC_ANOMALY },
      { title: 'Travel Time Map', name: RouterNames.TRAVEL_TIME_MAP },
      { title: 'Historical Incidents', name: RouterNames.TRAFFIC_INCIDENT }
    ],

    action_menu_items: [
      { title: RouterNames.TRAFFIC_DASHBOARD, url: RouterPaths.TRAFFIC_DASHBOARD },
      { title: RouterNames.TRAFFIC_BT_DASHBOARD, url: RouterPaths.TRAFFIC_BT_DASHBOARD },
      { divider: true },
      { title: RouterNames.TRAFFIC_FLOW, url: RouterPaths.TRAFFIC_FLOW },
      { title: RouterNames.TRAVEL_TIME_DATA, url: RouterPaths.TRAVEL_TIME_DATA },
      { title: RouterNames.TRAFFIC_WEATHER, url: RouterPaths.TRAFFIC_WEATHER },
      { title: RouterNames.TRAFFIC_MULTIGRAPH, url: RouterPaths.TRAFFIC_MULTIGRAPH },
      { divider: true },
      { title: RouterNames.TRAFFIC_ANOMALY, url: RouterPaths.TRAFFIC_ANOMALY },
      { title: RouterNames.TRAVEL_TIME_MAP, url: RouterPaths.TRAVEL_TIME_MAP },
      { title: RouterNames.TRAFFIC_INCIDENT, url: RouterPaths.TRAFFIC_INCIDENT },
      { divider: true },
      { title: RouterNames.TRAFFIC_PREDICT, url: RouterPaths.TRAFFIC_PREDICT },
      { divider: true },
      { title: RouterNames.TRAFFIC_LCM, url: RouterPaths.TRAFFIC_LCM }
    ]
  }),

  computed: {
    isDashboard() {
      return this.$route.name === RouterNames.TRAFFIC_DASHBOARD;
    },

    ...mapState('traffic', ['showFlowChart']),
    ...mapGetters('traffic', ['getNotification'])
  },

  methods: {
    color(name) {
      if (name === 'dashboard') {
        const item = this.dash_menu_items.find(item => item.name === this.$route.name);
        return item ? 'orange' : 'teal';
      } else if (name === 'chart') {
        const item = this.chart_menu_items.find(item => item.name === this.$route.name);
        return item ? 'orange' : 'teal';
      } else if (name === 'anomaly') {
        const item = this.anomaly_menu_items.find(item => item.name === this.$route.name);
        return item ? 'orange' : 'teal';
      } else if (name === 'predict') {
        return this.$route.name === RouterNames.TRAFFIC_PREDICT ? 'orange' : 'teal';
      } else {
        return 'teal';
      }
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    showDashboard() {
      this.switchTo(RouterPaths.TRAFFIC_DASHBOARD);
    },

    showBTDashboard() {
      this.switchTo(RouterPaths.TRAFFIC_BT_DASHBOARD);
    },

    showTrafficFlow() {
      this.switchTo(RouterPaths.TRAFFIC_FLOW);
    },

    showTrafficLCM() {
      this.switchTo(RouterPaths.TRAFFIC_LCM);
    },

    showTrafficPredict() {
      this.switchTo(RouterPaths.TRAFFIC_PREDICT);
    },

    showAnomalyMap() {
      this.switchTo(RouterPaths.TRAFFIC_ANOMALY);
    },

    showWeatherData() {
      this.switchTo(RouterPaths.TRAFFIC_WEATHER);
    },

    showMultigraph() {
      this.switchTo(RouterPaths.TRAFFIC_MULTIGRAPH);
    },

    showTravelTimeData() {
      this.switchTo(RouterPaths.TRAVEL_TIME_DATA);
    },

    showTravelTimeMap() {
      this.switchTo(RouterPaths.TRAVEL_TIME_MAP);
    },

    showHistoricalIncidents() {
      this.switchTo(RouterPaths.TRAFFIC_INCIDENT);
    },

    performTask(name) {
      switch (name) {
        case RouterNames.TRAFFIC_FLOW:
          this.showTrafficFlow();
          break;

        case RouterNames.TRAFFIC_DASHBOARD:
          this.showDashboard();
          break;

        case RouterNames.TRAFFIC_BT_DASHBOARD:
          this.showBTDashboard();
          break;

        case RouterNames.TRAFFIC_LCM:
          this.showTrafficLCM();
          break;

        case RouterNames.TRAFFIC_WEATHER:
          this.showWeatherData();
          break;

        case RouterNames.TRAFFIC_MULTIGRAPH:
          this.showMultigraph();
          break;

        case RouterNames.TRAVEL_TIME_DATA:
          this.showTravelTimeData();
          break;

        case RouterNames.TRAFFIC_ANOMALY:
          this.showAnomalyMap();
          break;

        case RouterNames.TRAVEL_TIME_MAP:
          this.showTravelTimeMap();
          break;

        case RouterNames.TRAFFIC_INCIDENT:
          this.showHistoricalIncidents();
          break;
      }
    },

    dataMenuItemClicked(name) {
      setTimeout(() => {
        this.performTask(name);
      }, 100);
    }
  }
};
</script>

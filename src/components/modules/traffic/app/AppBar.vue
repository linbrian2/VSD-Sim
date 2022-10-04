<template>
  <div>
    <Header :title="title" :showDate="!isDashboard" :clipLeft="true" :clipRight="true" :actionItems="actionItems">
      <div class="hidden-md-and-down">
        <MenuButton
          :items="chart_menu_items"
          tooltip="Show Charts"
          icon="mdi-chart-line"
          :color="color('chart')"
          @menuItemclick="dataMenuItemClicked"
        />

        <MenuButton
          :items="anomaly_menu_items"
          tooltip="Anomaly Map"
          icon="mdi-view-comfy"
          :color="color('anomaly')"
          @menuItemclick="dataMenuItemClicked"
        />

        <MenuButton
          :items="prediction_menu_items"
          tooltip="Traffic Prediction"
          icon="mdi-waveform"
          :color="color('predict')"
          @menuItemclick="dataMenuItemClicked"
        />
      </div>

      <!-- notification -->
      <v-divider vertical class="ml-2 hidden-xs-only" />
      <NotificationDropdown />
      <v-divider vertical class="ml-2 hidden-xs-only" />
    </Header>
    <FlowChartDialog v-model="showFlowChart" />
  </div>
</template>

<script>
import { RouterNames, RouterPaths } from '@/utils/constants/router';
import AppConstants from '@/utils/constants/app';
import Header from '@/components/common/Header';
import MenuButton from '@/components/common/MenuButton';
import FlowChartDialog from '@/components/modules/traffic/app/FlowChartDialog';
import NotificationDropdown from '@/components/common/NotificationDropdown';
import { mapState, mapGetters } from 'vuex';

export default {
  components: {
    Header,
    MenuButton,
    FlowChartDialog,
    NotificationDropdown
  },

  data: () => ({
    title: AppConstants.TRAFFIC_APP_TITLE,

    chart_menu_items: [
      { title: RouterNames.TRAFFIC_MULTIGRAPH, url: RouterPaths.TRAFFIC_MULTIGRAPH },
      { divider: true },
      { title: RouterNames.TRAFFIC_SIGNALS, url: RouterPaths.TRAFFIC_SIGNALS },
      { title: RouterNames.TRAFFIC_RESPONSIVE_DATA, url: RouterPaths.TRAFFIC_RESPONSIVE_DATA },

      { divider: true },
      { title: RouterNames.TRAFFIC_LCM, url: RouterPaths.TRAFFIC_LCM },
      { divider: true },
      { title: RouterNames.TRAFFIC_ROUTING, url: RouterPaths.TRAFFIC_ROUTING }
    ],

    anomaly_menu_items: [
      { title: RouterNames.TRAFFIC_ANOMALY, url: RouterPaths.TRAFFIC_ANOMALY },
      { title: RouterNames.TRAVEL_TIME_MAP, url: RouterPaths.TRAVEL_TIME_MAP },
      { title: RouterNames.TRAFFIC_INCIDENT_LIST, url: RouterPaths.TRAFFIC_INCIDENT_LIST }
    ],

    prediction_menu_items: [{ title: RouterNames.TRAFFIC_PREDICT, url: RouterPaths.TRAFFIC_PREDICT }],

    action_main_items: [
      { title: RouterNames.TRAFFIC_MULTIGRAPH, url: RouterPaths.TRAFFIC_MULTIGRAPH },
      { divider: true },
      { title: RouterNames.TRAFFIC_SIGNALS, url: RouterPaths.TRAFFIC_SIGNALS },
      { title: RouterNames.TRAFFIC_RESPONSIVE_DATA, url: RouterPaths.TRAFFIC_RESPONSIVE_DATA },
      { divider: true },
      { title: RouterNames.TRAFFIC_ANOMALY, url: RouterPaths.TRAFFIC_ANOMALY },
      { title: RouterNames.TRAVEL_TIME_MAP, url: RouterPaths.TRAVEL_TIME_MAP },
      { title: RouterNames.TRAFFIC_INCIDENT_LIST, url: RouterPaths.TRAFFIC_INCIDENT_LIST },
      { divider: true },
      { title: RouterNames.TRAFFIC_PREDICT, url: RouterPaths.TRAFFIC_PREDICT },
      { divider: true },
      { title: RouterNames.TRAFFIC_DATA_PLAYBACK, url: RouterPaths.TRAFFIC_DATA_PLAYBACK }
    ],

    action_optional_items: [
      { divider: true },
      { title: RouterNames.TRAFFIC_LCM, url: RouterPaths.TRAFFIC_LCM },
      { title: RouterNames.TRAFFIC_ROUTING, url: RouterPaths.TRAFFIC_ROUTING }
    ]
  }),

  computed: {
    isDashboard() {
      return this.$route.name === RouterNames.TRAFFIC_DASHBOARD;
    },

    actionItems() {
      const items = [];
      items.push(...this.action_main_items);
      if (this.isDevUser) {
        items.push(...this.action_optional_items);
      }
      return items;
    },

    ...mapState('traffic', ['showFlowChart']),
    ...mapGetters('auth', ['isDevUser'])
  },

  methods: {
    color(name) {
      if (name === 'chart') {
        const item = this.chart_menu_items.find(item => item.title === this.$route.name);
        return item ? 'orange' : 'teal';
      } else if (name === 'anomaly') {
        const item = this.anomaly_menu_items.find(item => item.title === this.$route.name);
        return item ? 'orange' : 'teal';
      } else if (name === 'predict') {
        const item = this.prediction_menu_items.find(item => item.title === this.$route.name);
        return item ? 'orange' : 'teal';
      } else {
        return 'teal';
      }
    },

    switchTo(path) {
      this.$router.push({ path }).catch(() => {});
    },

    dataMenuItemClicked(url) {
      setTimeout(() => {
        this.switchTo(url);
      }, 100);
    }
  }
};
</script>

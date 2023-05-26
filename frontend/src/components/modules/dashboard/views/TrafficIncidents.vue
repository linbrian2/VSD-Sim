<template>
  <div class="incidents">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemPerPage"
      disable-sort
      :hide-default-header="!showTable"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1 mx-2"
      v-show="showTable"
    >
      <template v-slot:[`item.id`]="{ item }">
        <div class="d-flex">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <span v-on="on">
                <v-icon v-if="item.status === 0" color="green" class="mr-2">mdi-progress-check</v-icon>
                <v-icon v-else color="grey" class="mr-2">mdi-check-circle-outline</v-icon>
              </span>
            </template>
            <span>{{ item.status === 0 ? 'ONGOING' : 'COMPLETED' }}</span>
          </v-tooltip>
          <div>{{ item.id }}</div>
        </div>
      </template>

      <template v-slot:[`item.shortName`]="{ item }">
        <v-chip color="success" outlined style="width:150px;">{{ item.shortName }}</v-chip>
      </template>

      <template v-slot:[`item.severity`]="{ item }">
        <v-chip small :color="item.severityColor">
          <strong class="black--text">{{ item.severity }}</strong>
        </v-chip>
      </template>

      <template v-slot:[`item.route`]="{ item }">
        {{ item.route }} {{ item.direction }}
        <v-badge class="ml-2" color="blue" :content="item.segmentCount" bordered> </v-badge>
      </template>

      <template v-slot:[`item.startTime`]="{ item }">
        {{ getTime(item.startTime) }}
      </template>

      <template v-slot:[`item.duration`]="{ item }"> {{ item.duration }} min </template>
    </v-data-table>

    <IncidentDataInfo :segment="selectedTrafficIncident" v-if="selectedTrafficIncident" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic.js';
import IncidentDataInfo from '@/components/modules/traffic/dashboard/IncidentDataInfo';
import { mapState, mapGetters } from 'vuex';

export default {
  props: {
    maxItems: Number
  },
  components: {
    IncidentDataInfo
  },

  data: () => ({
    reload: false,
    listLimit: 0,
    segments: [],
    headers: [
      { text: 'ID', value: 'id' },
      { text: 'Route', value: 'route' },
      { text: 'Start Time', value: 'startTime' },
      { text: 'Duration', value: 'duration' },
      { text: 'Severity', value: 'severity' }
    ]
  }),

  computed: {
    height() {
      return this.maxItems > 5 ? '35vh' : 'null';
    },
    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },
    items() {
      return this.trafficIncidents;
    },

    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['trafficIncidents', 'selectedTrafficIncident', 'showTable'])
  },

  mounted() {
    if (this.trafficIncidents && this.trafficIncidents.length > 0) {
      this.handleRowClick(this.items[0]);
    }
  },

  methods: {
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)}`;
    },

    getTime(date) {
      return Utils.formatTimeAsMinute(new Date(date));
    },

    itemRowBackground(item) {
      return item.id == this.selectedTrafficIncident.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id: item.id, type: Constants.LAYER_DEVICE_INCIDENTS });
    },

    getEvidenceIcon(name) {
      const ICONS = {
        flow: Constants.DEVICE_TRAFFIC_ICON,
        bluetooth: Constants.DEVICE_BLUETOOTH_ICON,
        waze: Constants.DEVICE_WAZE_ICON,
        restriction: Constants.DEVICE_RESTRICTIONS_ICON,
        weather: Constants.DEVICE_WEATHER_ICON,
        alert: Constants.DEVICE_ALERT_ICON
      };
      return ICONS[name];
    },

    getEvidenceColor(name) {
      const ICONS = {
        flow: 'purple',
        bluetooth: 'blue darken-4',
        waze: 'cyan accent-4',
        restriction: 'orange',
        weather: 'grey',
        alert: 'red'
      };
      return ICONS[name];
    }
  }
};
</script>

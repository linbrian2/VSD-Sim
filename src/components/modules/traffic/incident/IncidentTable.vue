<template>
  <v-data-table
    :height="height"
    fixed-header
    :headers="headers"
    :items="incidents"
    :items-per-page="itemsPerPage"
    hide-default-footer
    :item-class="itemRowBackground"
    @click:row="handleRowClick"
    class="elevation-1"
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
      <v-chip small :color="item.severityColor"
        ><strong class="black--text">{{ item.severity }}</strong></v-chip
      >
    </template>

    <template v-slot:[`item.route`]="{ item }">
      {{ item.route }} {{ item.direction }}
      <!-- <v-badge class="ml-2" color="blue" :content="item.segmentCount" bordered> </v-badge> -->
    </template>
    <template v-slot:[`item.startTime`]="{ item }">
      {{ item.startTime | time }}
    </template>
    <template v-slot:[`item.endTime`]="{ item }">
      {{ item.endTime | time }}
    </template>
    <template v-slot:[`item.evidenceCounts`]="{ item }">
      <v-badge
        class="mr-1"
        v-for="(count, name, index) in item.evidenceCounts"
        :color="getEvidenceColor(name)"
        :key="index"
        :content="count"
        inline
      >
        <v-icon small v-text="getEvidenceIcon(name)"></v-icon>
      </v-badge>
    </template>
    <template v-slot:[`item.mitigation`]="{ item }">
      <div v-if="item.mitigation">
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-icon color="green" v-bind="attrs" v-on="on" @click="showMitigationSolutions(item)"
              >mdi-traffic-light</v-icon
            >
          </template>
          <span>Mitigation Solutions</span>
        </v-tooltip>
      </div>
    </template>
    <template v-slot:[`item.duration`]="{ item }"> {{ item.duration }} min </template>
  </v-data-table>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import { RouterPaths } from '@/utils/constants/router';
export default {
  props: {
    height: { type: Number, default: 350 },
    incidents: Array
  },

  data: () => ({
    itemsPerPage: 500,
    headers: [
      { text: 'ID', value: 'id' },
      { text: 'Route', value: 'route' },
      { text: 'Region', value: 'region' },
      { text: 'Severity', value: 'severity' },
      { text: 'Start', value: 'startTime' },
      { text: 'End', value: 'endTime' },
      { text: 'Duration', value: 'duration' },
      { text: 'Evidences', value: 'evidenceCounts' },
      { text: 'Mitigation', value: 'mitigation' }
    ],
    selectedRowId: null
  }),

  filters: {
    time(time) {
      const d = new Date(time);
      // const dd = Utils.formatDate(d);
      const tt = Utils.formatTimeAsMinute(d);
      return tt; //`${dd} ${tt}`;
    }
  },

  mounted() {
    if (this.$route.params.id) {
      setTimeout(() => {
        let id = parseInt(this.$route.params.id);
        const item = this.incidents.find(m => m.id === id);
        this.handleRowClick(item);
      }, 3000);
    }
  },

  methods: {
    itemRowBackground(item) {
      return item.id == this.selectedRowId ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      console.log(item);
      this.selectedRowId = item.id;
      this.$emit('click', item);
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
    },

    showMitigationSolutions(item) {
      const id = item.id;
      const path = RouterPaths.TRAFFIC_MITIGATION;
      this.$router.push({ path: `${path}/${id}` }).catch(() => {});
    }
  }
};
</script>

<style lang="scss">
.table_tr_selected {
  background-color: #eee;
}
.table_tr_normal {
  background-color: transparent !important;
}

.v-data-table > .v-data-table__wrapper > table > tbody > tr > th,
.v-data-table > .v-data-table__wrapper > table > thead > tr > th,
.v-data-table > .v-data-table__wrapper > table > tfoot > tr > th {
  font-size: 14px !important;
}

.v-expansion-panel-content__wrap {
  padding: 0 !important;
}

.table_tr_selected {
  background-color: #a9a9a9;
}
</style>

<template>
  <div class="incidents">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="incidents"
      :items-per-page="showTable && maxItems > listLimit ? maxItems : listLimit"
      disable-sort
      :hide-default-header="!showTable"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
      :search="search"
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
        <v-badge class="ml-2" color="blue" :content="item.segmentCount" bordered> </v-badge>
      </template>

      <template v-slot:[`item.startTime`]="{ item }">
        {{ getTime(item.startTime) }}
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
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/dashboard';
import { mapGetters } from 'vuex';

export default {
  props: {
    preSelect: { type: Boolean, default: true },
    incidents: Array,
    search: String
  },

  data: () => ({
    maxItems: 10,
    headers: [
      { text: 'Route', value: 'route' },
      { text: 'Start Time', value: 'startTime' },
      { text: 'Duration', value: 'duration' },
      // { text: 'Evidences', value: 'evidenceCounts' },
      { text: 'Severity', value: 'severity' }
    ],
    selectedRowId: null
  }),

  computed: {
    height() {
      if (this.showTable && this.maxItems > 12) {
        return 'calc(80vh - 48px)';
      } else {
        return null;
      }
    },
    showTable: {
      get() {
        return this.$store.state.dashboard.showTable;
      },
      set(show) {
        this.$store.commit('dashboard/SHOW_TABLE', show);
      }
    },
    listLimit() {
      if (this.getSetting) {
        return this.getSetting('dashboard', 'limitListings');
      } else {
        return 0;
      }
    },
    ...mapGetters(['getSetting'])
  },

  filters: {
    time(time) {
      if (time) {
        const d = new Date(time);
        const dd = Utils.formatDate(d);
        const tt = Utils.formatTimeAsMinute(d);
        return `${dd} ${tt}`;
      }
    }
  },

  mounted() {
    if (this.preSelect && this.incidents && this.incidents.length > 0) {
      this.handleRowClick(this.incidents[0]);
    }
  },

  methods: {
    getTime(date) {
      return Utils.formatTimeAsMinute(new Date(date));
    },

    itemRowBackground(item) {
      return item.id == this.selectedRowId ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
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
    }

    /* showMitigationSolutions(item) {
      const id = item.id;
      const path = RouterPaths.TRAFFIC_MITIGATION;
      this.$router.push({ path: `${path}/${id}` }).catch(() => {});
    } */
  },

  watch: {
    incidents(data) {
      if (data.length > 0) {
        this.handleRowClick(this.incidents[0]);
      }
    }
  }
};
</script>

<style lang="scss">
.table_tr_selected {
  background-color: rgb(238, 238, 238);
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
  background-color: #bfd707;
}
</style>

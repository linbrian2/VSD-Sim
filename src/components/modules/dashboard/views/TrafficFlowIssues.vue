<template>
  <div class="detectors">
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
      <template v-slot:[`item.state`]="{ item }">
        <v-chip small :color="item.status === 0 ? 'green' : 'red'">
          <strong class="black--text">{{ item.state }}</strong>
        </v-chip>
      </template>
    </v-data-table>

    <!-- Data Display -->
    <template v-if="!reload">
      <FlowDataInfo :marker="selectedTrafficDevice" />
    </template>
  </div>
</template>

<script>
import FlowDataInfo from '@/components/modules/traffic/dashboard/FlowDataInfo';
import Utils from '@/utils/Utils';
import { mapGetters, mapState } from 'vuex';

export default {
  props: {
    data: Object,
    maxItems: Number
  },

  components: {
    FlowDataInfo
  },

  data: () => ({
    height: null,
    itemsPerPage: 0,
    reload: false,
    listLimit: 0,
    items: [],
    headers: []
  }),

  computed: {
    selectedTrafficDevice: {
      get() {
        return this.$store.state.dashboard.selectedTrafficDevice;
      },
      set(val) {
        this.$store.state.dashboard.selectedTrafficDevice = val;
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },

    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },

    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['trafficDevices', 'selectedTrafficDevice', 'showTable'])
  },

  filters: {
    time(time) {
      const d = new Date(time);
      const dd = Utils.formatDate(d);
      const tt = Utils.formatTimeAsMinute(d);
      return `${dd} ${tt}`;
    }
  },

  mounted() {
    if (this.trafficDevices) {
      this.prepareTrafficDetectors(this.trafficDevices);
      this.handleRowClick(this.items[0]);
    }

    this.itemsPerPage = this.listLimit;
    if (this.showTable) {
      this.expandTable();
    }
  },

  methods: {
    expandTable() {
      if (this.itemsPerPage == 0) {
        this.prepareTrafficDetectors(this.trafficDevices);
        if (this.maxItems > 12) {
          this.height = 'calc(95vh - 48px)';
        }
        if (this.maxItems == 1) {
          this.itemsPerPage = 1.1;
        } else {
          this.itemsPerPage = this.maxItems;
        }
      } else {
        this.prepareTrafficDetectors([this.selectedTrafficDevice]);
        this.height = null;
        this.itemsPerPage = 0;
      }
    },
    prepareTrafficDetectors(data) {
      this.headers = [
        { text: 'Device', value: 'device' },
        { text: 'Status', value: 'state' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        device: d.name || d.device,
        status: d.status,
        state: d.status ? (d.status === 0 ? 'Normal' : 'Anomaly') : d.state
      }));
    },

    itemRowBackground(item) {
      return item.device == this.selectedTrafficDevice.device ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedTrafficDevice = item;
      this.$emit('click', item);
    },

    fetchInfo(device) {
      console.log('Fetch Device Info: %o', device);
    }
  },

  watch: {
    listLimit(limit) {
      if (!this.showTable) {
        this.itemsPerPage = limit;
      }
    },
    showTable() {
      this.expandTable();
    },
    selectedTrafficDevice(device) {
      this.fetchInfo(device);
    },
    trafficDevices() {
      if (this.trafficDevices) {
        this.prepareTrafficDetectors(this.trafficDevices);
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

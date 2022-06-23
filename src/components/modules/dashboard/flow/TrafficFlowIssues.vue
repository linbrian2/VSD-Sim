<template>
  <div class="detectors">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :hide-default-header="itemsPerPage == 1"
      hide-default-footer
      disable-sort
      :items-per-page="itemsPerPage"
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
    >
      <template v-slot:[`item.state`]="{ item }">
        <v-chip small :color="'red'">
          <strong class="black--text">{{ item.state }}</strong>
        </v-chip>
      </template>
      <template v-slot:[`item.actions`] v-if="itemsPerPage == 1">
        <div class="grid-right pr-6">
          <v-icon small @click="expandTable">
            mdi-arrow-expand-down
          </v-icon>
        </div>
      </template>
      <template v-slot:[`footer`] v-if="itemsPerPage != 1">
        <v-btn block @click="expandTable">
          <v-icon>{{ itemsPerPage == 1 ? 'mdi-arrow-expand-down' : 'mdi-arrow-expand-up' }}</v-icon>
        </v-btn>
      </template>
    </v-data-table>
    <!-- Data Display -->
    <template v-if="!reload">
      <FlowDataInfo :marker="selectedtrafficDevice" />
    </template>
  </div>
</template>

<script>
import FlowDataInfo from '@/components/modules/dashboard/flow/FlowDataInfo';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';

export default {
  props: {
    data: Object,
    maxItems: Number,
    infoColumnCount: Number
  },

  components: {
    FlowDataInfo
  },

  data: () => ({
    height: null,
    itemsPerPage: 1,
    reload: false,
    items: [],
    headers: []
  }),

  computed: {
    selectedtrafficDevice: {
      get() {
        return this.$store.state.dashboard.selectedtrafficDevice;
      },
      set(val) {
        this.$store.state.dashboard.selectedtrafficDevice = val;
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },
    ...mapState('dashboard', ['trafficDevices'])
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
  },

  methods: {
    expandTable() {
      if (this.itemsPerPage == 1) {
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
        this.prepareTrafficDetectors([this.selectedtrafficDevice]);
        this.height = null;
        this.itemsPerPage = 1;
      }
    },
    prepareTrafficDetectors(data) {
      this.headers = [
        { text: 'Device', value: 'device' },
        { text: 'Status', value: 'state' },
        { text: '', value: 'actions' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        device: d.name || d.device,
        status: d.status,
        state: d.status ? (d.status === 0 ? 'Normal' : 'Anomaly') : d.state
      }));
    },

    itemRowBackground(item) {
      return item.device == this.selectedtrafficDevice.device ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedtrafficDevice = item;
      this.$emit('click', item);
    },

    fetchInfo(device) {
      console.log('Fetch Device Info: %o', device);
    }
  },

  watch: {
    selectedtrafficDevice(device) {
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

<style lang="scss"></style>

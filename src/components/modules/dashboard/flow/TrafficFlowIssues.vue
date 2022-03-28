<template>
  <div class="detectors">
    <!-- {{ selectedtrafficDevice }} -->
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      hide-default-footer
      :items-per-page="itemsPerPage"
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
    >
      <template v-slot:[`item.status`]="{ item }">
        <v-chip small :color="'red'"
          ><strong class="black--text">{{ item.status }}</strong></v-chip
        >
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
    height: { type: Number, default: 190 },
    itemsPerPage: {type: Number, default: 3},
    data: Object
  },

  components: {
    FlowDataInfo
  },

  data: () => ({
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
    }
    if (!this.selectedtrafficDevice) {
      this.handleRowClick(this.items[0]);
    }
  },

  methods: {
    prepareTrafficDetectors(data) {
      this.headers = [
        { text: 'Device', value: 'device' },
        { text: 'Status', value: 'state' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        device: d.name,
        status: d.status,
        state: d.status === 0 ? 'Normal' : 'Anomaly'
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
      }
      if (!this.selectedtrafficDevice) {
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

<style lang="scss"></style>

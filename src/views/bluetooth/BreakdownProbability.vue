<template>
  <div class="breakdown-probability">
    <v-container fluid style="max-width: 85%">
      <v-card class="rounded-0 elevation-5" :loading="loadingData" style="height: 90vh; overflow-y: auto;">
        <v-card-title>Breakdown Probability</v-card-title>
        <v-card-text class="mb-10">
          <v-container fluid>
            <v-autocomplete
              v-model="selectedDevice"
              dense
              :items="devicesBP"
              :loading="isLoading"
              :search-input.sync="search"
              color="white"
              hide-no-data
              hide-selected
              item-text="fullName"
              item-value="API"
              :label="label"
              return-object
              prepend-icon="mdi-dice-6"
            />
            <div v-if="selectedDevice && !reload" fluid class="pt-3">
              <BPGraph :title="selectedDevice.fullName" :bpInfo="bpInfo" :options="bpGraphOptions" />
            </div>
          </v-container>
        </v-card-text>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import BPGraph from '@/components/modules/bluetooth/graphs/BPGraph';
import Api from '@/utils/api/bluetooth.js';

export default {
  components: {
    BPGraph
  },
  data: () => ({
    reload: false,
    bpGraphOptions: {
      containerName: 'small-bp-container',
      height: 500,
      smallChart: true,
      legendEnabled: false,
      marginRight: 125,
      titleOffset: -30
    },
    selectedDevice: null,
    bpInfo: null,
    deviceList: null,
    isLoading: false,
    search: null
  }),
  mounted() {
    this.createDeviceList();
  },
  methods: {
    createDeviceList() {
      this.deviceList = [];
      this.devicesBP.forEach(d => {
        this.deviceList.push(d.apiReqName);
      });
    },
    fetchBPInfo(bp) {
      if (bp) {
        let dt = this.currentDate.getTime();
        this.fetchBP(bp.deviceId, bp.direction, dt);
      }
    },
    fetchBP(id, dir, endTS) {
      Api.fetchBP(id, dir, endTS).then(
        data => {
          this.bpInfo = data;
        },
        error => {
          console.log(error);
        }
      );
    },
    changeSelectedDevice() {
      if (this.$store.state.bluetooth.selectedDevice) {
        this.devicesBP.forEach(d => {
          if (this.$store.state.bluetooth.selectedDevice == d.deviceId) {
            this.selectedDevice = d.apiReqName;
            return;
          }
        });
      }
    }
  },
  watch: {
    loadingData(val, oldVal) {
      console.log(val);
      console.log(oldVal);
      if (oldVal == true && val == false) {
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },
    '$store.state.bluetooth.selectedDevice'() {
      this.changeSelectedDevice();
    },
    selectedDevice(data) {
      this.fetchBPInfo(data);
    },
    bpInfo() {
      this.$emit('bluetooth/SET_SELECTED_DEVICE', this.selectedDevice);
    }
  },
  computed: {
    loadingData() {
      return this.devicesBP.length == 0
    },
    devicesBP() {
      let devices = this.$store.state.bluetooth.apiData.devices;
      if (devices) {
        devices = devices.filter(d => d.bpInfoNB || d.bpInfoSB);
        let deviceInfo = [];
        devices.forEach(d => {
          if (d.bpInfoNB) {
            deviceInfo.push(d.bpInfoNB);
          }
          if (d.bpInfoSB) {
            deviceInfo.push(d.bpInfoSB);
          }
        });
        return deviceInfo;
      } else {
        return [];
      }
    },
    label() {
      if (this.devicesBP) {
        return `Search (Items: ${this.devicesBP.length})`;
      } else {
        return 'Search';
      }
    },
    ...mapState(['currentDate'])
  }
};
</script>

<style scoped>
.table {
  padding-top: 0;
}
</style>

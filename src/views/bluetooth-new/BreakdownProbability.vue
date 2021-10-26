<template>
  <div class="breakdown-probability">
    <v-container fluid style="max-width: 85%">
      <v-card class="rounded-0 elevation-5">
        <v-card-title>Breakdown Probability</v-card-title>
        <v-card-text class="mb-10">
          <v-container fluid>
            <v-autocomplete
              v-model="selectedDevice"
              dense
              :items="deviceInfo"
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
            <div v-if="selectedDevice" fluid class="pt-3">
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
import Utils from '@/utils/Utils';
import DateTimePicker from '@/components/modules/bluetooth/ui/DateTimePicker';
import BPGraph from '@/components/modules/bluetooth/graphs/BPGraph';
import Api from '@/utils/api/bluetooth.js';

import { DateTime } from 'luxon';

export default {
  components: {
    DateTimePicker,
    BPGraph,
  },
  data: () => ({
    bpGraphOptions: {
      containerName: 'small-bp-container',
      height: 500,
      smallChart: true,
      legendEnabled: false,
      marginRight: 125,
      titleOffset: -30,
    },
    selectedDatetime: null,
    selectedDevice: null,
    bpInfo: null,
    deviceList: null,
    isLoading: false,
    search: null,
  }),
  mounted() {
    console.log(this.$store.state.bluetooth.apiData.devices);
    let dt = DateTime.now().setZone('America/New_York').startOf('minute');
    this.selectedDatetime = new Date(dt.year, dt.month - 1, dt.day, dt.hour, dt.minute, 0);
    this.createDeviceList();
  },
  methods: {
    createDeviceList() {
      this.deviceList = [];
      this.deviceInfo.forEach((d) => {
        this.deviceList.push(d.apiReqName);
      });
    },
    fetchBPInfo(bp) {
      if (bp) {
        let date = this.currentDate
        let s = DateTime.fromObject({
          year: date.getFullYear(),
          month: date.getMonth() + 1,
          day: date.getDate(),
          hour: 0,
          minute: 0
        })
        let e = DateTime.fromObject({
          year: date.getFullYear(),
          month: date.getMonth() + 1,
          day: date.getDate(),
          hour: 23,
          minute: 59
        })
        console.log(s);
        console.log(e);
        let startStr = `${s.year}${Utils.formatXX(s.month)}${Utils.formatXX(s.day)}${Utils.formatXX(
          s.hour
        )}${Utils.formatXX(s.minute)}00`;
        let endStr = `${e.year}${Utils.formatXX(e.month)}${Utils.formatXX(e.day)}${Utils.formatXX(
          e.hour
        )}${Utils.formatXX(e.minute)}00`;
        let apiReqName = `${bp.deviceId}-${bp.direction}`;
        console.log(`Sending bp API request to /api/breakdown-prob/${apiReqName}-${startStr}-${endStr}`);
        this.fetchBP(apiReqName, startStr, endStr);
      }
    },
    fetchBP(apiReqName, start, end) {
      Api.fetchBP(apiReqName, start, end).then(
        (data) => {
          this.bpInfo = data;
        },
        (error) => {
          console.log(error);
        }
      );
    },
    changeSelectedDevice() {
      if (this.$store.state.bluetooth.selectedDevice) {
        console.log('Device Info: %o', this.deviceInfo);
        this.deviceInfo.forEach((d) => {
          if (this.$store.state.bluetooth.selectedDevice == d.deviceId) {
            this.selectedDevice = d.apiReqName;
            return;
          }
        });
      }
    },
  },
  watch: {
    '$store.state.bluetooth.selectedDevice'() {
      this.changeSelectedDevice();
    },
    selectedDevice(data) {
      this.fetchBPInfo(data);
    },
    currentDate() {
      this.fetchBPInfo(this.selectedDevice);
    },
    bpInfo() {
      this.$emit('bluetooth/SET_SELECTED_DEVICE', this.selectedDevice);
    },
  },
  computed: {
    deviceInfo() {
      let devices = this.$store.state.bluetooth.apiData.devices;
      console.log(devices);
      if (devices) {
        devices = devices.filter((d) => d.bpInfoNB || d.bpInfoSB);
        let deviceInfo = [];
        devices.forEach((d) => {
          if (d.bpInfoNB) {
            deviceInfo.push(d.bpInfoNB);
          }
          if (d.bpInfoSB) {
            deviceInfo.push(d.bpInfoSB);
          }
        });
        console.log(deviceInfo);
        return deviceInfo;
      } else {
        return null;
      }
    },
    label() {
      if (this.deviceInfo) {
        return `Search (Items: ${this.deviceInfo.length})`;
      } else {
        return 'Search';
      }
    },
    ...mapState(['currentDate'])
  },
};
</script>

<style scoped>
.table {
  padding-top: 0;
}
</style>

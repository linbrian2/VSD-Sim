<template>
  <v-card class="main-card">
    <v-col>
      <v-row>
        <v-col cols="12" offset-lg="3" lg="6">
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
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" offset-lg="2" lg="8">
          <v-card class="mb-6" max-width="100%" v-if="selectedDevice">
            <v-card-actions class="px-5 py-0 pb-0">
              <DateTimePicker
                v-model="selectedDatetime"
                :dialogWidth="420"
                timeFormat="HH:mm:ss"
                v-on:update="update"
                :textFieldProps="{ outlined: false, dense: true, class: 'mt-4' }"
                v-on:updateCurr="update"
                :datePickerProps="{
                  min: dateMin,
                  max: dateMax,
                  color: 'deep-purple lighten-1',
                  headerColor: 'transparent'
                }"
                :timePickerProps="{
                  min: timeMin,
                  max: timeMax,
                  color: 'deep-purple lighten-1',
                  headerColor: 'transparent'
                }"
              />
              <v-spacer />
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn
                    small
                    color="purple"
                    outlined
                    fab
                    v-bind="attrs"
                    v-on="on"
                    v-if="bpInfo"
                    class="mr-2"
                    @click.stop="$store.state.bluetooth.dialog.bp = true"
                  >
                    <v-icon>mdi-chart-areaspline-variant</v-icon>
                  </v-btn>
                  <v-btn small color="purple" outlined fab v-bind="attrs" v-on="on" v-else class="mr-2" disabled>
                    <v-icon>mdi-chart-areaspline-variant</v-icon>
                  </v-btn>
                </template>
                <span>Graphs</span>
              </v-tooltip>
            </v-card-actions>
            <v-card-text>
              <BPGraph :title="selectedDevice.fullName" :bpInfo="bpInfo" :options="bpGraphOptions" />
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-col>
  </v-card>
</template>

<script>
import Utils from '@/utils/Utils';
import DateTimePicker from '@/components/modules/bluetooth/ui/DateTimePicker';
import BPGraph from '@/components/modules/bluetooth/graphs/BPGraph';
import Api from '@/utils/api/bluetooth.js';

import { DateTime } from 'luxon';

export default {
  components: {
    DateTimePicker,
    BPGraph
  },
  data: () => ({
    bpGraphOptions: {
      containerName: 'small-bp-container',
      height: 300,
      smallChart: true,
      legendEnabled: false,
      marginRight: 125,
      titleOffset: -30
    },
    startDT: DateTime.fromObject({ year: 2020, month: 6, day: 1, hour: 0, minute: 0, zone: 'America/New_York' }),
    endDT: null,
    selectedDatetime: null,
    selectedDevice: null,
    vueAppURL: process.env.VUE_APP_URL,
    date: null,
    time: null,
    blacklist: [],
    selectedDeviceInfo: null,
    bpInfo: null,
    deviceList: null,
    headers: [
      {
        text: 'Device Info',
        align: 'start',
        value: 'name'
      },
      { text: 'value', value: 'value' }
    ],
    isLoading: false,
    search: null
  }),
  mounted() {
    console.log(this.$store.state.bluetooth.apiData.devices);
    let dt = DateTime.now()
      .setZone('America/New_York')
      .startOf('minute');
    this.selectedDatetime = new Date(dt.year, dt.month - 1, dt.day, dt.hour, dt.minute, 0);
    this.createDeviceList();
    setInterval(() => {
      this.endDT = DateTime.local().setZone('America/New_York');
    }, 1000);
  },
  methods: {
    createDeviceList() {
      this.deviceList = [];
      this.deviceInfo.forEach(d => {
        this.deviceList.push(d.apiReqName);
      });
    },
    fetchBPInfo(bp) {
      if (this.submittedDateTime && bp) {
        let s = this.submittedDateTime.minus({ hours: 5 });
        let e = this.submittedDateTime;
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
        console.log('Device Info: %o', this.deviceInfo);
        this.deviceInfo.forEach(d => {
          if (this.$store.state.bluetooth.selectedDevice == d.deviceId) {
            this.selectedDevice = d.apiReqName;
            return;
          }
        });
      }
    },
    update() {
      this.fetchBPInfo(this.selectedDevice);
    }
  },
  watch: {
    '$store.state.bluetooth.selectedDevice'() {
      this.changeSelectedDevice();
    },
    selectedDevice(data) {
      console.log(data);
      this.fetchBPInfo(data);
    },
    bpInfo(data) {
      console.log('bpInfo CHANGED!');
      this.$store.commit('SET_BP_INFO', data);
      this.$emit('bluetooth/SET_SELECTED_DEVICE', this.selectedDevice);
    }
  },
  computed: {
    deviceInfo() {
      let devices = this.$store.state.bluetooth.apiData.devices;
      console.log(devices);
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
        console.log(deviceInfo);
        return deviceInfo;
      } else {
        return null;
      }
    },
    submittedDateTime() {
      let date = this.selectedDatetime;
      if (date) {
        return DateTime.fromObject({
          year: date.getFullYear(),
          month: date.getMonth() + 1,
          day: date.getDate(),
          hour: date.getHours(),
          minute: date.getMinutes(),
          zone: 'America/New_York'
        });
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
    dateMin() {
      return this.startDT ? this.startDT.toISO().slice(0, 10) : null;
    },
    dateMax() {
      return this.endDT ? this.endDT.toISO().slice(0, 10) : null;
    },
    timeMin() {
      if (
        this.submittedDateTime &&
        this.startDT &&
        this.submittedDateTime.year == this.startDT.year &&
        this.submittedDateTime.month == this.startDT.month &&
        this.submittedDateTime.day == this.startDT.day
      ) {
        return this.startDT.toISO().slice(11, 16);
      } else {
        return '00:00';
      }
    },
    timeMax() {
      if (
        this.submittedDateTime &&
        this.endDT &&
        this.submittedDateTime.year == this.endDT.year &&
        this.submittedDateTime.month == this.endDT.month &&
        this.submittedDateTime.day == this.endDT.day
      ) {
        return this.endDT.toISO().slice(11, 16);
      } else {
        return '23:59';
      }
    }
  }
};
</script>

<style scoped>
.table {
  padding-top: 0;
}
</style>

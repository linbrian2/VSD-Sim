<template>
  <div>
    <!-- Time Picker -->
    <v-scroll-x-reverse-transition>
      <v-card style="position: absolute; top: 0px; left: 61.1%" v-show="$store.state.bluetooth.timePickerMenu">
        <v-time-picker v-model="time" scrollable color="green lighten-1">
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="cancelTimePicker"> Cancel </v-btn>
          <v-btn text color="primary" @click="timeSelected"> OK </v-btn>
        </v-time-picker>
      </v-card>
    </v-scroll-x-reverse-transition>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import Api from '@/utils/api/bluetooth.js';

export default {
  data() {
    return {
      time: null
    };
  },
  mounted() {
    this.time = new Date();
    if (this.currentDate) {
      this.time = this.currentDate;
    }
    console.log(this.$route.name);
    if (!this.fetchDone && this.$route.name != "Dashboard") {
        this.fetchData(true, true);
    }

    this.$bus.$on('CHANGE_BLUETOOTH_TIME', date => {
      this.setTime(date);
    });
  },
  computed: {
    filteredWazeMarkers() {
      if (this.$store.state.bluetooth.wazeLayerSelection && this.$store.state.bluetooth.wazeMarkers) {
        let filteredWazeMarkers = this.$store.state.bluetooth.wazeMarkers.filter(w =>
          this.$store.state.bluetooth.wazeLayerSelection.includes(w.wazeTypeId)
        );
        return filteredWazeMarkers;
      } else {
        return this.$store.state.bluetooth.wazeMarkers;
      }
    },
    isToday() {
      if (this.currentDate) {
        const today = new Date()
        return this.currentDate.getDate() == today.getDate() &&
               this.currentDate.getMonth() == today.getMonth() &&
               this.currentDate.getFullYear() == today.getFullYear()
      } else {
        return true
      }
    },
    isWazeMarkers() {
      return this.mapLayerSelection.includes(3) && !this.mapLayerSelection.includes(4)
    },
    isWazeClusters() {
      return this.mapLayerSelection.includes(3) && this.mapLayerSelection.includes(4)
    },
    ...mapState(['currentDate']),
    ...mapState('bluetooth', ['mapLayerSelection'])
  },
  methods: {
    /* API Invocation */
    displayAPIFail() {
      let notifText = 'Failed to fetch map data';
      this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'error' });
    },
    fetchData(dateChanged, timeChanged) {
      this.$bus.$emit('CHANGE_BLUETOOTH_TIME', this.currentDate);
      if (dateChanged) {
        this.getFullDayData(this.currentDate)
        this.getCurrTimeData(this.currentDate)
      } else if (timeChanged) {
        this.getCurrTimeData(this.currentDate)
      }
    },
    getFullDayData(currDate) {
      let dt = this.isToday ? null : currDate.getTime()
      this.$store.state.bluetooth.apiData.segmentsFull = null;
      this.$store.state.bluetooth.apiData.wazeFull = null;
      this.$store.state.bluetooth.apiData.devicesFull = null;
      if (this.isToday) {
        Api.initFullDay().then(() => { 
            this.getFullDaySegment(dt)
            this.getFullDayWaze(dt)
            this.getFullDayDevice(dt)
            if (!this.$store.state.bluetooth.apiData.sensors) {
              this.fetchSensors()
            }
            this.$store.state.bluetooth.timeSinceUpdate = 0
          },
          error => { console.log(error); }
        );
      } else {
        this.getFullDaySegment(dt)
        this.getFullDayWaze(dt)
        this.getFullDayDevice(dt)
        this.$store.state.bluetooth.timeSinceUpdate = 0
      }
    },
    getCurrTimeData(currDate) {
      let dt = currDate.getTime()
      this.$store.state.bluetooth.apiData.segments = null;
      this.$store.state.bluetooth.apiData.waze = null;
      this.$store.state.bluetooth.apiData.devices = null;
      this.getCurrTimeSegment(dt)
      this.getCurrTimeWaze(dt)
      this.getCurrTimeDevice(dt)
      this.$store.state.bluetooth.timeSinceUpdate = 0
    },
    getFullDaySegment(dt) {
      Api.fetchSegmentsFull(dt).then(
        data => { this.$store.state.bluetooth.apiData.segmentsFull = data; },
        error => { console.log(error); }
      );
    },
    getFullDayWaze(dt) {
      Api.fetchWazeDataFull(dt).then(
        data => { this.$store.state.bluetooth.apiData.wazeFull = data; },
        error => { console.log(error); }
      );
    },
    getFullDayDevice(dt) {
      Api.fetchDevicesFull(dt).then(
        data => { this.$store.state.bluetooth.apiData.devicesFull = data; },
        error => { console.log(error); }
      );
    },
    getCurrTimeSegment(dt) {
      Api.fetchSegments(dt).then(data => {
        this.$store.state.bluetooth.apiData.segments = data;
        this.$bus.$emit('CREATE_SEGMENTS')
        if (this.mapLayerSelection.includes(0)) {
          this.$bus.$emit('ADD_SEGMENTS');
        }
        let notifText = 'Successfully fetched segment data';
        this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'info' });
      }, error => {
        console.log(error);
        this.displayAPIFail();
      });
    },
    getCurrTimeWaze(dt) {
      Api.fetchWazeData(60, dt).then(data => {
        this.$store.state.bluetooth.apiData.waze = data;
        this.$bus.$emit('CREATE_WAZE_ALERTS')
        if (this.isWazeMarkers) {
          this.$bus.$emit('ADD_MARKERS', this.filteredWazeMarkers);
        }
        if (this.isWazeClusters) {
          this.$bus.$emit('ADD_WAZE_CLUSTERS')
        }
        let notifText = 'Successfully fetched waze data';
        this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'info' });
      }, error => {
        console.log(error);
        this.displayAPIFail();
      });
    },
    getCurrTimeDevice(dt) {
      Api.fetchDevicesBPData().then(
        dataBP => {
          Api.fetchDevices(dt).then(
            dataDev => {
              this.$store.state.bluetooth.apiData.devices = dataDev;
              dataBP.forEach((bpData, i) => {
                let added = false;
                this.$store.state.bluetooth.apiData.devices.forEach((d, j) => {
                  if (parseInt(bpData.deviceId) == d.deviceId) {
                    if (bpData.direction == 'NB') {
                      bpData.fullName = `${d.title} - NB`;
                      this.$set(d, 'bpInfoNB', bpData);
                      added = true;
                    } else if (bpData.direction == 'SB') {
                      bpData.fullName = `${d.title} - SB`;
                      this.$set(d, 'bpInfoSB', bpData);
                      added = true;
                    }
                  }
                });
              });
              this.$bus.$emit('CREATE_DEVICES')
              if (this.mapLayerSelection.includes(2)) {
                this.$bus.$emit('ADD_MARKERS', this.filteredWazeMarkers);
              }
              let notifText = 'Successfully fetched Device data';
              this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'info' });
            }, error => {
              console.log(error);
            }
          );
        }, error => {
          console.log(error);
        }
      );
    },
    fetchSensors() {
      Api.fetchSensors().then(data => {
        this.$store.state.bluetooth.apiData.sensors = data;
        this.$bus.$emit('CREATE_SENSORS')
        if (this.mapLayerSelection.includes(1)) {
          this.$bus.$emit('ADD_MARKERS', this.$store.state.bluetooth.sensorMarkers);
        }
      }, error => {
        console.log(error);
      });
    },
    /* Time Picker */
    setTime(date) {
      this.time = date;
    },
    cancelTimePicker() {
      this.time = this.currentDate;
      this.$store.state.bluetooth.timePickerMenu = false;
    },
    timeSelected() {
      if (typeof this.time == 'string') {
        let timeSplit = this.time.split(':');
        let cd = this.currentDate;
        this.$store.state.currentDate = new Date(
          cd.getFullYear(),
          cd.getMonth(),
          cd.getDate(),
          parseInt(timeSplit[0]),
          parseInt(timeSplit[1])
        );
        console.log(`New Time Selected: ${this.time}`);
        this.time = this.currentDate;
        this.$store.state.bluetooth.timePickerMenu = false;
      }
    },
  },
  watch: {
    '$store.state.bluetooth.map'(data) {
      if (data && !this.fetchDone) {
        this.fetchData(true, true);
      }
    },
    currentDate(n, o) {
      let dateChanged = (n.getFullYear() == o.getFullYear() && 
                         n.getMonth() == o.getMonth() && 
                         n.getDate() == o.getDate()) ? false : true
      let timeChanged = (n.getHours() == o.getHours() && n.getMinutes() == o.getMinutes()) ? false : true
      this.fetchData(dateChanged, timeChanged)
    }
  },
};
</script>

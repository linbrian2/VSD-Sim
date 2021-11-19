<template>
  <div>
    <!-- Time Picker -->
    <v-scroll-x-reverse-transition>
      <v-card style="position: absolute; top: 0px; left: 61.1%" v-show="timePickerMenu">
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
import { mapGetters, mapState } from 'vuex';
import Constants from '@/utils/constants/bluetooth';
import Api from '@/utils/api/bluetooth.js';
import Utils from '@/utils/Utils';

export default {
  data() {
    return {
      time: null,
      updateInterval: null,
      firstFullDayAPICall: true
    };
  },
  mounted() {
    if (this.$route.name != 'Dashboard ') {
      this.fetchData(true, true);
    }
    this.time = new Date();
    if (this.currentDate) {
      this.time = this.currentDate;
    }

    this.$bus.$on('CHANGE_BLUETOOTH_TIME', date => {
      this.setTime(date);
    });

    /* Update every 5 minutes*/
    if (!this.updateInterval) {
      this.updateInterval = setInterval(() => {
        if (this.fetchDone && this.autoUpdate) {
          this.timeSinceUpdate++;
          if (this.timeSinceUpdate == 300) {
            this.$store.commit('SET_CURRENT_DATE', new Date());
          }
        }
      }, 1000);
    }
  },
  beforeDestroy() {
    if (this.updateInterval) {
      clearInterval(this.updateInterval);
    }
  },
  computed: {
    filteredWazeMarkers() {
      if (this.wazeLayerSelection && this.wazeMarkers) {
        let filteredWazeMarkers = this.wazeMarkers.filter(w => this.wazeLayerSelection.includes(w.wazeTypeId));
        return filteredWazeMarkers;
      } else {
        return this.wazeMarkers;
      }
    },
    isToday() {
      if (this.currentDate) {
        const today = new Date();
        return (
          this.currentDate.getDate() == today.getDate() &&
          this.currentDate.getMonth() == today.getMonth() &&
          this.currentDate.getFullYear() == today.getFullYear()
        );
      } else {
        return true;
      }
    },
    isWazeMarkers() {
      return (
        this.mapLayerSelection.includes(Constants.LAYER_WAZE) &&
        !this.mapLayerSelection.includes(Constants.LAYER_GROUPED_WAZE)
      );
    },
    isWazeClusters() {
      return (
        this.mapLayerSelection.includes(Constants.LAYER_WAZE) &&
        this.mapLayerSelection.includes(Constants.LAYER_GROUPED_WAZE)
      );
    },
    filteredDeviceMarkers() {
      if (this.deviceLayerSelection && this.deviceMarkers) {
        let filterLevels = [];
        if (this.deviceLayerSelection.includes(Constants.DEVICES_LOW_TRAFFIC)) {
          filterLevels.push(-1, 0, 1);
        }
        if (this.deviceLayerSelection.includes(Constants.DEVICES_MEDIUM_TRAFFIC)) {
          filterLevels.push(2, 3, 4);
        }
        if (this.deviceLayerSelection.includes(Constants.DEVICES_HIGH_TRAFFIC)) {
          filterLevels.push(5, 6);
        }
        let filteredDeviceMarkers = this.deviceMarkers.filter(w => filterLevels.includes(w.level));
        return filteredDeviceMarkers;
      } else {
        return this.wazeMarkers;
      }
    },
    timePickerMenu: {
      get() {
        return this.$store.state.bluetooth.timePickerMenu;
      },
      set(menu) {
        this.$store.commit('bluetooth/SET_TIMEPICKER_MENU', menu);
      }
    },
    timeSinceUpdate: {
      get() {
        return this.$store.state.bluetooth.timeSinceUpdate;
      },
      set(value) {
        this.$store.commit('bluetooth/SET_TIME_SINCE_UPDATE', value);
      }
    },
    ...mapGetters('bluetooth', ['fetchDone']),
    ...mapState(['currentDate']),
    ...mapState('bluetooth', [
      'map',
      'mapLayerSelection',
      'apiData',
      'wazeLayerSelection',
      'wazeMarkers',
      'sensorMarkers',
      'deviceLayerSelection',
      'deviceMarkers',
      'autoUpdate',
      'playbackToggle',
      'apiLoading'
    ])
  },
  methods: {
    /* API Invocation */
    displayAPIFail() {
      let notifText = 'Failed to fetch map data';
      this.$store.dispatch('setSystemStatus', { text: notifText, color: 'error', timeout: 2500 });
    },
    fetchData(dateChanged, timeChanged) {
      this.$bus.$emit('CHANGE_BLUETOOTH_TIME', this.currentDate);
      if (dateChanged) {
        this.getFullDayData(this.currentDate);
        this.getCurrTimeData(this.currentDate);
      } else if (timeChanged) {
        this.getCurrTimeData(this.currentDate);
      }
    },
    getFullDayData(currDate) {
      let dt = this.isToday ? null : currDate.getTime();
      if (this.firstFullDayAPICall) {
        this.$store.commit('bluetooth/SET_API_DATA', { prop: 'allFull', data: null });
        if (this.isToday) {
          this.$store.commit('bluetooth/SET_API_LOADING', { prop: 'all', data: true });
          Api.initFullDay().then(
            res => {
              if (res) {
                this.getFullDaySegment(dt);
                this.getFullDayWaze(dt);
                this.getFullDayDevice(dt);
              } else {
                setTimeout(() => {
                  this.getFullDayData(currDate);
                }, 5000);
              }
              this.timeSinceUpdate = 0;
              this.firstFullDayAPICall = false;
            },
            error => {
              console.log(error);
            }
          );
        } else if (!this.isToday && this.playbackToggle) {
          this.$store.commit('bluetooth/SET_API_LOADING', { prop: 'all', data: true });
          this.getFullDaySegment(dt);
          this.getFullDayWaze(dt);
          this.getFullDayDevice(dt);
          this.timeSinceUpdate = 0;
          this.firstFullDayAPICall = false;
        }
      }
    },
    getCurrTimeData(currDate) {
      let dt = currDate.getTime();
      this.$store.commit('bluetooth/SET_API_DATA', { prop: 'allCurr', data: null });
      this.getCurrTimeSegment(dt);
      this.getCurrTimeWaze(dt);
      this.getCurrTimeDevice(dt);
      this.fetchSensors();
      this.timeSinceUpdate = 0;
    },
    getFullDaySegment(dt) {
      Api.fetchSegmentsFull(dt).then(
        data => {
          this.$store.commit('bluetooth/SET_API_DATA', { prop: 'segmentsFull', data: data });
          this.$store.commit('bluetooth/SET_API_LOADING', { prop: 'segmentsFull', data: false });
        },
        error => {
          console.log(error);
        }
      );
    },
    getFullDayWaze(dt) {
      Api.fetchWazeDataFull(dt).then(
        data => {
          this.$store.commit('bluetooth/SET_API_DATA', { prop: 'wazeFull', data: data });
          this.$store.commit('bluetooth/SET_API_LOADING', { prop: 'wazeFull', data: false });
        },
        error => {
          console.log(error);
        }
      );
    },
    getFullDayDevice(dt) {
      Api.fetchDevicesFull(dt).then(
        data => {
          this.$store.commit('bluetooth/SET_API_DATA', { prop: 'devicesFull', data: data });
          this.$store.commit('bluetooth/SET_API_LOADING', { prop: 'devicesFull', data: false });
        },
        error => {
          console.log(error);
        }
      );
    },
    getCurrTimeSegment(dt) {
      Api.fetchSegments(dt).then(
        data => {
          this.$store.commit('bluetooth/SET_API_DATA', { prop: 'segments', data: data });
          this.$bus.$emit('CREATE_SEGMENTS');
          if (this.mapLayerSelection.includes(Constants.LAYER_CONGESTION)) {
            this.$bus.$emit('ADD_SEGMENTS');
          }
          let routes = data
            .slice()
            .map(x => x.info.route)
            .filter(Utils.onlyUnique)
            .filter(x => !!x)
            .sort();
          this.$store.commit('bluetooth/SET_API_DATA', { prop: 'routes', data: routes });
          let notifText = 'Successfully fetched segment data';
          this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
        },
        error => {
          console.log(error);
          this.displayAPIFail();
        }
      );
    },
    getCurrTimeWaze(dt) {
      Api.fetchWazeData(60, dt).then(
        data => {
          this.$store.commit('bluetooth/SET_API_DATA', { prop: 'waze', data: data });
          this.$bus.$emit('CREATE_WAZE_ALERTS');
          if (this.isWazeMarkers) {
            this.$bus.$emit('ADD_MARKERS', this.filteredWazeMarkers);
          }
          if (this.isWazeClusters) {
            this.$bus.$emit('ADD_WAZE_CLUSTERS');
          }
          let notifText = 'Successfully fetched waze data';
          this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
        },
        error => {
          console.log(error);
          this.displayAPIFail();
        }
      );
    },
    getCurrTimeDevice(dt) {
      Api.fetchDevicesBPData().then(
        dataBP => {
          Api.fetchDevices(dt).then(
            dataDev => {
              this.$store.commit('bluetooth/SET_API_DATA', { prop: 'devices', data: dataDev });
              dataBP.forEach(bpData => {
                this.apiData.devices.forEach(d => {
                  if (parseInt(bpData.deviceId) == d.deviceId) {
                    if (bpData.direction == 'NB') {
                      bpData.fullName = `${d.title} - NB`;
                      this.$set(d, 'bpInfoNB', bpData);
                    } else if (bpData.direction == 'SB') {
                      bpData.fullName = `${d.title} - SB`;
                      this.$set(d, 'bpInfoSB', bpData);
                    }
                  }
                });
              });
              this.$bus.$emit('CREATE_DEVICES');
              if (this.mapLayerSelection.includes(Constants.LAYER_DEVICES)) {
                this.$bus.$emit('ADD_MARKERS', this.filteredDeviceMarkers);
              }
              let notifText = 'Successfully fetched Device data';
              this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
            },
            error => {
              console.log(error);
            }
          );
        },
        error => {
          console.log(error);
        }
      );
    },
    fetchSensors() {
      Api.fetchSensors().then(
        data => {
          this.$store.commit('bluetooth/SET_API_DATA', { prop: 'sensors', data: data });
          this.$bus.$emit('CREATE_SENSORS');
          if (this.mapLayerSelection.includes(Constants.LAYER_BLUETOOTH_SENSORS)) {
            this.$bus.$emit('ADD_MARKERS', this.sensorMarkers);
          }
        },
        error => {
          console.log(error);
        }
      );
    },
    /* Time Picker */
    setTime(date) {
      this.time = date;
    },
    cancelTimePicker() {
      this.time = this.currentDate;
      this.timePickerMenu = false;
    },
    timeSelected() {
      if (typeof this.time == 'string') {
        let timeSplit = this.time.split(':');
        let newDate = new Date(
          this.currentDate.getFullYear(),
          this.currentDate.getMonth(),
          this.currentDate.getDate(),
          parseInt(timeSplit[0]),
          parseInt(timeSplit[1])
        );
        this.$store.commit('SET_CURRENT_DATE', newDate);
        this.time = this.currentDate;
        this.timePickerMenu = false;
      }
    }
  },
  watch: {
    playbackToggle(newVal, oldVal) {
      if (oldVal == false && newVal == true) {
        if (this.firstFullDayAPICall) {
          this.getFullDayData(this.currentDate);
        }
      }
    },
    map(data) {
      if (data && !this.fetchDone) {
        this.fetchData(true, true);
      }
    },
    currentDate(n, o) {
      let dateChanged =
        n.getFullYear() == o.getFullYear() && n.getMonth() == o.getMonth() && n.getDate() == o.getDate() ? false : true;
      let timeChanged = n.getHours() == o.getHours() && n.getMinutes() == o.getMinutes() ? false : true;
      if (dateChanged) {
        this.firstFullDayAPICall = true;
      }
      this.fetchData(dateChanged, timeChanged);
    }
  }
};
</script>

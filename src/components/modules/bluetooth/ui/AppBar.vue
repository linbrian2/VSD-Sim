<template>
  <div>
    <v-app-bar app dark flat dense clipped-left color="black">
      <v-btn icon @click.stop="showDrawer">
        <v-icon>mdi-view-grid-outline</v-icon>
      </v-btn>

      <div class="d-flex align-center px-1">
        <router-link to="/">
          <v-img
            alt="Logo"
            class="shrink mr-2"
            contain
            src="@/assets/ai-logo.svg"
            transition="scale-transition"
            width="100"
          />
        </router-link>
      </div>
      <NavDropdown />
      <v-toolbar-title v-show="$vuetify.breakpoint.mdAndUp">{{ title }}</v-toolbar-title>
      <!-- Update Button -->
      <div class="middle-header">
        <div class="update-button">
          <template v-if="!inHistoricalMode">
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  fab
                  class="mt-6px"
                  color="blue-grey lighten-2"
                  @click="update"
                  v-bind="attrs"
                  v-on="on"
                  :loading="!fetchDone"
                  :disabled="!fetchDone || $store.state.bluetooth.modes.addFromMap"
                >
                  <v-icon>mdi-cached</v-icon>
                </v-btn>
              </template>
              <span>Last updated: {{ lastUpdateDT }}</span>
            </v-tooltip>
          </template>
          <template v-if="inHistoricalMode">
            <v-menu v-model="menu" :close-on-content-click="false" offset-y>
              <template v-slot:activator="{ on, attrs }">
                <div v-bind="attrs" v-on="on">
                  <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        icon
                        fab
                        dark
                        class="mt-6px"
                        color="yellow darken-2"
                        v-bind="attrs"
                        v-on="on"
                        :loading="!fetchHistDone"
                        :disabled="!fetchHistDone || $store.state.bluetooth.modes.addFromMap"
                      >
                        <v-icon>mdi-clock-time-three-outline</v-icon>
                      </v-btn>
                    </template>
                    <span>Historical Mode</span>
                  </v-tooltip>
                </div>
              </template>
              <v-card class="pa-3" width="355px">
                <v-card-actions>
                  Exit out of historical mode and use current time (Realtime data).
                </v-card-actions>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn text @click="menu = false"> Cancel </v-btn>
                  <v-btn color="primary" text @click="resetToCurr">
                    Confirm
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-menu>
          </template>
          <v-progress-circular :value="progress" size="12" class="mr-4 mt-6" v-if="showProgressCircle" />
          <DateTimePicker
            v-model="$store.state.bluetooth.selectedDatetime"
            :dialogWidth="420"
            timeFormat="HH:mm:ss"
            v-on:update="dtSubmitted"
            :textFieldProps="{ outlined: false, dense: true, class: 'mt-4' }"
            v-on:updateCurr="dtSubmittedCurr"
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
          <!-- <h5 class="mt-2 ml-5">
            {{$store.state.bluetooth.selectedDatetime}}<br/>
            {{submittedDateTime}}
          </h5> -->
        </div>
      </div>
      <!-- Tabs -->
      <div class="button-group" v-show="$vuetify.breakpoint.mdAndUp">
        <v-btn-toggle dense mandatory background-color="black" dark v-model="$store.state.bluetooth.selectedPage">
          <v-tooltip left>
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs" v-on="on">
                <v-icon>mdi-view-dashboard</v-icon>
              </v-btn>
            </template>
            <span>Dashboard</span>
          </v-tooltip>

          <v-tooltip left>
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs" v-on="on">
                <v-icon>mdi-chart-areaspline</v-icon>
              </v-btn>
            </template>
            <span>Multi-graph</span>
          </v-tooltip>

          <v-tooltip left>
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs" v-on="on">
                <v-icon>mdi-dice-6</v-icon>
              </v-btn>
            </template>
            <span>Breakdown Probability</span>
          </v-tooltip>
        </v-btn-toggle>
      </div>

      <LoggedInUser v-if="user" :user="user" />
      <v-divider vertical class="ml-2" />

      <div>
        <v-menu bottom right offset-y>
          <template v-slot:activator="{ on, attrs }">
            <v-btn icon v-bind="attrs" v-on="on">
              <v-icon>mdi-dots-vertical</v-icon>
            </v-btn>
          </template>

          <v-list>
            <v-list-item v-for="(item, i) in menu_items" :key="i" @click="menuItemClicked(i)">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
    </v-app-bar>
    <NavDrawer />
  </div>
</template>

<script>
import DateTimePicker from '@/components/modules/bluetooth/ui/DateTimePicker';
import NavDropdown from '@/components/common/NavDropdown';
import NavDrawer from '@/components/nav/NavDrawer';
import LoggedInUser from '@/components/common/LoggedInUser';
import { DateTime } from 'luxon';
import AppConstants from '@/utils/constants/app';

export default {
  props: ['fetchDone'],
  components: {
    NavDropdown,
    DateTimePicker,
    LoggedInUser,
    NavDrawer
  },
  data: () => ({
    title: AppConstants.BLUETOOTH_APP_TITLE,
    menu: false,
    showProgressCircle: false,
    startDT: DateTime.fromObject({ year: 2020, month: 6, day: 1, hour: 0, minute: 0, zone: 'America/New_York' }),
    endDT: null,
    menu_items: [
      { title: 'Dashboard' },
      { title: 'Multi-graph' },
      { title: 'Breakdown Probability' },
      { title: 'Reset Map' },
      { title: 'Toggle Progress Circle' },
      { title: 'Toggle Dark Mode' }
      /* { title: 'Debug Log'}, */
    ],
    appMenuItems: [],
    lastUpdateDT: 'N/A',
    timeSinceUpdate: 0,
    updateInterval: 300 // 5 Minutes = 300 seconds
  }),

  mounted() {
    this.appMenuItems = this.$store.state.bluetooth.appMenuItems;
    let dt = DateTime.now()
      .setZone('America/New_York')
      .startOf('minute');
    this.lastUpdateDT = dt.toLocaleString(DateTime.DATETIME_FULL);
    this.$store.state.bluetooth.selectedDatetime = new Date(dt.year, dt.month - 1, dt.day, dt.hour, dt.minute, 0);
    setInterval(() => {
      this.endDT = DateTime.local().setZone('America/New_York');
      if (this.fetchDone && !this.inHistoricalMode) {
        this.timeSinceUpdate++;
      }
    }, 1000);
  },
  methods: {
    showDrawer() {
      this.$store.commit('SHOW_DRAWER', true);
    },

    resetToCurr() {
      this.menu = false;
      let dt = DateTime.now()
        .setZone('America/New_York')
        .startOf('minute');
      this.$store.state.bluetooth.selectedDatetime = new Date(dt.year, dt.month - 1, dt.day, dt.hour, dt.minute, 0);
      this.dtSubmittedCurr();
    },
    dtSubmitted() {
      this.$store.state.bluetooth.modes.historical = true;
      this.timeSinceUpdate = 300;
      let dt = this.submittedDateTime;
      this.$bus.$emit('FETCH_HISTORICAL', dt);
    },
    dtSubmittedCurr() {
      this.$store.state.bluetooth.modes.historical = false;
      this.timeSinceUpdate = 300;
    },
    timeChanged() {
      console.log('Time Changed');
    },
    update() {
      if (!this.$store.state.bluetooth.modes.addFromMap && !this.inHistoricalMode) {
        let dt = DateTime.now()
          .setZone('America/New_York')
          .startOf('minute');
        this.$store.state.bluetooth.selectedDatetime = new Date(dt.year, dt.month - 1, dt.day, dt.hour, dt.minute, 0);
        this.timeSinceUpdate = 0;
        this.$emit('update');
        this.$bus.$emit('SEGMENTS_UPDATE');
      }
    },
    menuItemClicked(i) {
      console.log(`Item ${i} clicked`);
      switch (i) {
        case 0:
          this.$store.commit('bluetooth/SET_SELECTED_PAGE', 0);
          break;
        case 1:
          this.$store.commit('bluetooth/SET_SELECTED_PAGE', 1);
          break;
        case 2:
          this.$store.commit('bluetooth/SET_SELECTED_PAGE', 2);
          break;
        case 3:
          this.$store.state.bluetooth.map.setCenter({ lat: 39.14, lng: -75.5 });
          this.$store.state.bluetooth.map.setZoom(9);
          this.$store.state.bluetooth.prefs.map.center = { lat: 39.14, lng: -75.5 };
          this.$store.state.bluetooth.prefs.map.zoom = 9;
          break;
        case 4:
          if (this.showProgressCircle) {
            this.$store.commit('bluetooth/SET_NOTIFICATION', {
              show: true,
              text: 'Progress circle disabled.',
              timeout: 2500,
              color: 'info'
            });
            this.showProgressCircle = false;
          } else {
            this.$store.commit('bluetooth/SET_NOTIFICATION', {
              show: true,
              text: 'Progress circle enabled.',
              timeout: 2500,
              color: 'info'
            });
            this.showProgressCircle = true;
          }
          break;
        case 5:
          this.$vuetify.theme.dark = !this.$vuetify.theme.dark;
          this.$store.state.bluetooth.prefs.darkMode = this.$vuetify.theme.dark;
          break;
        case 6:
          console.log('Prefs: %o', this.$store.state.bluetooth.prefs);
          console.log('Map: %o', this.$store.state.bluetooth.map);
          break;
        default:
          alert(`Item ${i}: Not implemented.`);
          break;
      }
    }
  },
  computed: {
    user() {
      return this.$store.state.auth.user;
    },

    fetchHistDone() {
      if (
        this.inHistoricalMode &&
        !!this.$store.state.bluetooth.apiData.segments &&
        !!this.$store.state.bluetooth.apiData.waze
      ) {
        return true;
      } else {
        return false;
      }
    },
    inHistoricalMode() {
      if (this.$store.state.bluetooth.modes && this.$store.state.bluetooth.modes.historical) {
        return true;
      } else {
        return false;
      }
    },
    progress() {
      return (this.timeSinceUpdate / this.updateInterval) * 100;
    },
    getAppTitle() {
      return this.$store.state.bluetooth.appTitle;
    },
    submittedDateTime() {
      let date = this.$store.state.bluetooth.selectedDatetime;
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
  },
  watch: {
    progress(val) {
      if (val >= 100 && !this.inHistoricalMode) {
        this.update();
      }
    },
    fetchDone: {
      handler: function(val, oldVal) {
        if (val == true && oldVal == false) {
          let dt = DateTime.now().setZone('America/New_York');
          this.lastUpdateDT = dt.toLocaleString(DateTime.DATETIME_FULL);
          /* console.log(this.lastUpdateDT) */
        }
      }
    }
  }
};
</script>

<style lang="scss" scoped>
@media only screen and (max-width: 959px) {
  .middle-header {
    text-align: center;
    margin: 0 auto;
    padding: 10px;
    padding-right: 100px;
  }
}
@media only screen and (min-width: 960px) {
  .middle-header {
    text-align: center;
    margin: 0 auto;
    padding: 10px;
    padding-right: 320px;
  }
}
.mt-6px {
  margin-top: 6px;
}
.update-text {
  float: left;
  padding-top: 10px;
  padding-right: 5px;
}
.update-button {
  float: left;
  display: flex;
}
#logo {
  height: 30px;
}
.button-group {
  position: absolute;
  right: 125px;
}
.options {
  position: absolute;
  right: 10px;
}
.v-toolbar__title {
  color: white;
}
.v-progress-circular__underlay {
  stroke: rgba(100, 100, 100, 0.7) !important;
  z-index: 1;
}
</style>

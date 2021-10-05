<template>
  <v-dialog
    v-model="$store.state.bluetooth.dialog.devices"
    width="unset"
    transition="scroll-x-transition"
    scrollable
  >
    <v-card>
      <v-card-title>
         Traffic Flow Detectors
        <div class="middle-header">
          <v-text-field
            dense
            outlined
            v-model="deviceSearch"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
            style="width:25em;"
          >
            <template v-slot:append-outer>
              <v-menu bottom right offset-y>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn icon v-bind="attrs" v-on="on" class="ml-2" small>
                    <v-icon>mdi-dots-vertical</v-icon>
                  </v-btn>
                </template>

                <v-list>
                  <v-list-item v-for="(item, i) in menuItems" :key="i" @click="menuItemClicked(i)">
                    <v-list-item-title>{{ item.title }}</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </template>
          </v-text-field>
        </div>
        <v-btn
          icon
          class="close-button mr-4"
          @click="$store.state.bluetooth.dialog.devices = false"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text class="pr-0 pb-0">
        <v-data-table
          :headers="deviceHeaders"
          :items="deviceItems"
          :search="deviceSearch"
          :items-per-page="100"
          :footer-props="{
            'items-per-page-options': [10, 20, 50, 100, -1]
          }"
          :sort-by.sync="sortCol"
          :sort-desc.sync="sortDesc"
          height="69vh"
          fixed-header
          multi-sort
          item-key="id"
          class="elevation-1"
        >
          <template v-slot:[`item.actions`]="{ item }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-icon
                  small
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="viewItem(item)"
                  >mdi-arrow-right-bold</v-icon
                >
              </template>
              <span>Go to device</span>
            </v-tooltip>
            <v-tooltip bottom v-if="item.data.bpInfoNB">
              <template v-slot:activator="{ on, attrs }">
                <v-icon
                  small
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  >mdi-chevron-up-box</v-icon
                >
              </template>
              <span>Northbound Breakdown Probability</span>
            </v-tooltip>
            <v-tooltip bottom v-if="item.data.bpInfoSB">
              <template v-slot:activator="{ on, attrs }">
                <v-icon
                  small
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  >mdi-chevron-down-box</v-icon
                >
              </template>
              <span>Southbound Breakdown Probability</span>
            </v-tooltip>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import Utils from "@/utils/Utils";
import { DateTime } from "luxon";

export default {
  data() {
    return {
      sortCol: 'actions',
      sortDesc: true,
      deviceSearch: "",
      menuItems: [
        { title: 'Print JSON' },
        { title: 'Download JSON' },
      ],
    }
  },
  methods: {
    viewBP(item) {
      console.log(item)
    },
    menuItemClicked(idx) {
      if (idx == 0) {
        console.log("this.devices\n%o", this.devices);
        let notifText = 'Check console for info.'
        this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'info' } )
      }
      else if (idx == 1) {
        let dt = DateTime.now().setZone("America/New_York")
        let dtStr = dt.toLocaleString(DateTime.DATETIME_SHORT_WITH_SECONDS).replaceAll('/', '-').replaceAll(',', '')
        let fileName = `Devices (${dtStr})`
        console.log(fileName);
        Utils.downloadJSON(fileName, this.devices)
      }
    },
    viewItem(item) {
      this.$bus.$emit('GO_TO_MARKER_LOCATION', item.data, "devices")
      this.$store.state.bluetooth.dialog.devices = false
    }
  },
  computed: {
    devices() {
      let devices = this.$store.state.bluetooth.apiData.devices
      if (devices)
        return devices
      else 
        return []
    },
    deviceHeaders() {
      if (this.devices) {
        let headers = [
          { text: "Id", value: "id", align: "start" },
          { text: 'Device Id', value: "deviceId"},
          { text: "Name", value: "name"},
          { text: "Level", value: "level"},
          { text: "Type", value: "type"},
          { text: "County", value: "county"},
          { text: "Station Count", value: "stationCount"},
          { text: "Actions", value: "actions"},
        ]
        return headers
      } else {
        return null
      }
    },
    deviceItems() {
      if (this.devices) {
        let items = []
        this.devices.forEach((d, i) => {
          let bpCount = 0
          if (d.bpInfoNB)
            bpCount++
          if (d.bpInfoSB)
            bpCount++
          items.push({
            id: i + 1,
            deviceId: d.deviceId,
            name: d.title,
            level: d.status.level,
            type: d.type,
            county: d.county,
            stationCount: d.stationCount,
            data: d,
            actions: bpCount,
          })
        });
        return items
      } else {
        return null
      }
    }
  },
};
</script>

<style>
  .middle-header {
    text-align: center;
    margin: 0 auto;
    padding-right: 40px;
  }
</style>
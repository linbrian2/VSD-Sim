<template>
  <v-dialog v-model="$store.state.bluetooth.dialog.btSensors" width="unset" transition="scroll-x-transition" scrollable>
    <v-card>
      <v-card-title>
        Bluetooth Sensors
        <div class="middle-header">
          <v-text-field
            dense
            outlined
            v-model="btSearch"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
            style="width:20em;"
            class="pl-10"
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
        <v-btn icon class="close-button mr-4" @click="$store.state.bluetooth.dialog.btSensors = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text class="pr-0 pb-0">
        <v-data-table
          :headers="btHeaders"
          :items="btItems"
          :search="btSearch"
          :items-per-page="100"
          :footer-props="{
            'items-per-page-options': [10, 20, 50, 100, -1]
          }"
          height="69vh"
          fixed-header
          multi-sort
          item-key="id"
          class="elevation-1"
        >
          <template v-slot:[`item.actions`]="{ item }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-icon small class="mr-2" v-bind="attrs" v-on="on" @click="viewItem(item)"
                  >mdi-arrow-right-bold</v-icon
                >
              </template>
              <span>Go to Bluetooth Sensor</span>
            </v-tooltip>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import Utils from '@/utils/Utils';
import { DateTime } from 'luxon';

export default {
  data() {
    return {
      btSearch: '',
      menuItems: [{ title: 'Print JSON' }, { title: 'Download JSON' }]
    };
  },
  methods: {
    menuItemClicked(idx) {
      if (idx == 0) {
        console.log('this.sensors\n%o', this.sensors);
        let notifText = 'Check console for info.';
        this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'info' });
      } else if (idx == 1) {
        let dt = DateTime.now().setZone('America/New_York');
        let dtStr = dt
          .toLocaleString(DateTime.DATETIME_SHORT_WITH_SECONDS)
          .replaceAll('/', '-')
          .replaceAll(',', '');
        let fileName = `Sensors (${dtStr})`;
        console.log(fileName);
        Utils.downloadJSON(fileName, this.sensors);
      }
    },
    viewItem(item) {
      this.$bus.$emit('GO_TO_MARKER_LOCATION', item.data, 'sensors');
      this.$store.state.bluetooth.dialog.btSensors = false;
    }
  },
  computed: {
    sensors() {
      let sensors = this.$store.state.bluetooth.apiData.sensors;
      if (sensors) return sensors;
      else return [];
    },
    btHeaders() {
      if (this.sensors) {
        let headers = [
          { text: 'Id', value: 'id', align: 'start' },
          { text: 'Name', value: 'name' },
          { text: 'Latitude', value: 'lat' },
          { text: 'Longitude', value: 'lng' },
          { text: 'Actions', value: 'actions', sortable: false }
        ];
        return headers;
      } else {
        return null;
      }
    },
    btItems() {
      if (this.sensors) {
        let items = [];
        this.sensors.forEach((s, i) => {
          items.push({
            id: i + 1,
            name: s.node,
            lat: s.lat,
            lng: s.lng,
            data: s
          });
        });
        return items;
      } else {
        return null;
      }
    }
  }
};
</script>

<style>
.middle-header {
  text-align: center;
  margin: 0 auto;
  padding-right: 120px;
}
</style>

<template>
  <v-dialog
    v-model="$store.state.bluetooth.dialog.wazeAlerts"
    scrollable
    width="unset"
    transition="scroll-x-transition"
  >
    <v-card>
      <v-card-title>
        Waze Alerts
        <div class="middle-header">
          <v-text-field
            dense
            outlined
            v-model="wazeSearch"
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
        <v-btn icon class="close-button mr-4" @click="$store.state.bluetooth.dialog.wazeAlerts = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text class="pr-0 pb-0">
        <v-data-table
          :headers="wazeHeaders"
          :items="wazeItems"
          :search="wazeSearch"
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
              <span>Go to waze alert</span>
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
      wazeSearch: '',
      fullDayToggle: false,
      menuItems: [{ title: 'Print JSON' }, { title: 'Download JSON' }, { title: 'Toggle Full Day Data' }]
    };
  },
  methods: {
    menuItemClicked(idx) {
      if (idx == 0) {
        console.log('this.wazeAlerts\n%o', this.wazeAlerts);
        console.log('this.wazeAlertsFull\n%o', this.wazeAlertsFull);
        let notifText = 'Check console for info.';
        this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'info' });
      } else if (idx == 1) {
        let dt = DateTime.now();
        let dtStr = dt
          .toLocaleString(DateTime.DATETIME_SHORT_WITH_SECONDS)
          .replaceAll('/', '-')
          .replaceAll(',', '');
        let fileName = `Waze Alerts (${dtStr})`;
        Utils.downloadJSON(fileName, this.wazeAlertsFull);
      } else if (idx == 2) {
        this.fullDayToggle = !this.fullDayToggle;
      }
    },
    viewItem(item) {
      this.$bus.$emit('GO_TO_MARKER_LOCATION', item.data, 'waze');
      this.$store.state.bluetooth.dialog.wazeAlerts = false;
    }
  },
  computed: {
    wazeAlerts() {
      let wazeAlerts = this.$store.state.bluetooth.apiData.waze;
      if (wazeAlerts) return wazeAlerts;
      else return [];
    },
    wazeAlertsFull() {
      let wazeAlerts = this.$store.state.bluetooth.apiData.wazeFull;
      if (wazeAlerts) return wazeAlerts;
      else return [];
    },
    wazeHeaders() {
      if (this.wazeAlerts) {
        let headers = [
          { text: 'Id', value: 'id', align: 'start' },
          { text: 'Alert Time', value: 'time' },
          { text: 'Description', value: 'desc' },
          { text: 'County', value: 'county' },
          { text: 'City', value: 'city' },
          { text: 'Street', value: 'street' },
          { text: 'Road Type', value: 'roadType' },
          { text: 'Actions', value: 'actions', sortable: false }
        ];
        return headers;
      } else {
        return null;
      }
    },
    wazeItems() {
      let wazeData = null;
      if (this.fullDayToggle) {
        wazeData = this.wazeAlertsFull;
      } else {
        wazeData = this.wazeAlerts;
      }
      if (wazeData) {
        let items = [];
        wazeData.forEach((w, i) => {
          items.push({
            id: i + 1,
            time: w.alertTime.slice(0, w.alertTime.length - 2),
            county: w.address.county ? w.address.county : '-',
            city: w.address.city ? w.address.city : '-',
            street: w.address.street ? w.address.street : '-',
            desc: w.description ? w.description : 'No description',
            roadType: w.roadType.name,
            data: w
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
  padding-right: 60px;
}
</style>

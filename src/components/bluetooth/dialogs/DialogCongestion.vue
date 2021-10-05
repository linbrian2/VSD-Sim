<template>
  <v-dialog
    v-model="$store.state.bluetooth.dialog.congestion"
    width="unset"
    transition="scroll-x-transition"
    scrollable
  >
    <v-card>
      <v-card-title>
        Congestion
          <div class="middle-header">
          <v-text-field
            dense
            outlined
            v-model="segmentsSearch"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
            style="width:25em;"
          >
            <template v-slot:append-outer>
              <RouteMenuPopover />
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
          @click="$store.state.bluetooth.dialog.congestion = false"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text class="pr-0 pb-0">
        <v-data-table
          :headers="segmentsHeaders"
          :items="segmentsItems"
          :search="segmentsSearch"
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
                <v-icon
                  small
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="viewItem(item)"
                  >mdi-arrow-right-bold</v-icon
                >
              </template>
              <span>Go to segment</span>
            </v-tooltip>
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-icon
                  small
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="viewGraph(item)"
                  >mdi-chart-areaspline</v-icon
                >
              </template>
              <span>View Travel Time Info</span>
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

import RouteMenuPopover from '@/components/bluetooth/ui/RouteMenuPopover.vue';

export default {
  components: {
    RouteMenuPopover,
  },
  data() {
    return {
      segmentsSearch: "",
      selectedRoutes: [],
      menuItems: [
        { title: 'Print JSON' },
        { title: 'Download JSON' },
      ],
    }
  },
  methods: {
    viewGraph(item) {
      /* console.log(item.data); */
      this.$store.state.bluetooth.selectedSeg.data = item.data
      this.$store.state.bluetooth.dialog.tt = true
    },
    menuItemClicked(idx) {
      if (idx == 0) {
        console.log("this.segments\n%o", this.segments);
        let notifText = 'Check console for info.'
        this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'info' } )
      }
      else if (idx == 1) {
        let dt = DateTime.now().setZone("America/New_York")
        let dtStr = dt.toLocaleString(DateTime.DATETIME_SHORT_WITH_SECONDS).replaceAll('/', '-').replaceAll(',', '')
        let fileName = `Segments (${dtStr})`
        console.log(fileName);
        Utils.downloadJSON(fileName, this.segments)
      }
    },
    viewItem(item) {
      this.$bus.$emit('GO_TO_SEGMENT_LOCATION', item.data)
      this.$store.state.bluetooth.dialog.congestion = false
    },
    filterByRoute(segments) {
      let selectedRoutes = this.$store.state.bluetooth.selectedRoutes
      segments = segments.filter(s => {
        let validSegment = false
        selectedRoutes.forEach(x => {
          if (x == s.info.route) {
            validSegment = true
          }
        });
        return validSegment
      });
      return segments
    }
  },
  computed: {
    segments() {
      let segments = this.$store.state.bluetooth.apiData.segments
      if (segments)
        return segments
      else 
        return []
    },
    segmentsHeaders() {
      if (this.segments) {
        let headers = [
          { text: "Id", value: "id", align: "start" },
          { text: 'Last Updated', value: "date"},
          { text: "Name", value: "name"},
          { text: "Description", value: "desc" },
          { text: "Severity Level", value: "level"},
          { text: "Actions", value: "actions", sortable: false },
        ]
        return headers
      } else {
        return null
      }
    },
    segmentsItems() {
      if (this.segments) {
        let segments = this.segments.slice()
        let items = []
        if (this.$store.state.bluetooth.selectedRoutes.length > 0) {
          segments = this.filterByRoute(segments)
        }
        segments.forEach(s => {
          items.push({
            id: s.info.id,
            date: s.travelTime.calculationTimestamp.text,
            name: s.info.name,
            desc: s.info.description,
            level: s.travelTime.level,
            data: s,
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
  }
</style>
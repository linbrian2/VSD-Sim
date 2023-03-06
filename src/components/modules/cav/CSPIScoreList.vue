<template>
  <div>
    <v-data-table
      :headers="headers"
      :items="items"
      :items-per-page="100"
      item-key="name"
      sort-by="id"
      class="elevation-1"
      show-group-by
      hide-default-footer
    >
      <template v-slot:[`item.group`]="{ item }">
        <v-chip small :color="getGroupColor(item.group)">
          <h4 style="color: #3b3b3b;">{{ item.group }}</h4>
        </v-chip>
      </template>
      <template v-slot:[`item.score`]="{ item }">
        <v-chip small :color="getColor(item.score)">
          <h4 style="color: #3b3b3b;">{{ item.score }}</h4>
        </v-chip>
      </template>
      <template v-slot:[`item.speedScore`]="{ item }">
        <v-tooltip bottom v-if="item.group != 'Average'">
          <template v-slot:activator="{ on }">
            <h4 v-on="on">{{ item.speedScore }}</h4>
          </template>
          <span>Average Speed: {{ item.avgSpd != null ? `${item.avgSpd} mph` : 'N/A' }}</span>
        </v-tooltip>
        <h4 v-else>{{ item.speedScore }}</h4>
      </template>
      <template v-slot:[`item.grScore`]="{ item }">
        <v-tooltip bottom v-if="item.group != 'Average'">
          <template v-slot:activator="{ on }">
            <h4 v-on="on">{{ item.grScore }}</h4>
          </template>
          <span>
            Green Lights: {{ item.greenStops }}, Red Lights: {{ item.redStops }}<br />
            Green/Red Ratio: {{ item.gr != null ? item.gr.toFixed(2) : 'N/A' }}
          </span>
        </v-tooltip>
        <h4 v-else>{{ item.grScore }}</h4>
      </template>
      <template v-slot:[`item.spmScore`]="{ item }">
        <v-tooltip bottom v-if="item.group != 'Average'">
          <template v-slot:activator="{ on }">
            <h4 v-on="on">{{ item.spmScore }}</h4>
          </template>
          <span>Stops per Mile: {{ item.spm != null ? item.spm.toFixed(2) : 'N/A' }}</span>
        </v-tooltip>
        <h4 v-else>{{ item.spmScore }}</h4>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <div class="d-flex">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn small icon class="mr-2" v-on="on" @click="goToLink(item)" :disabled="item.group === 'Average'">
                <v-icon>mdi-eye</v-icon>
              </v-btn>
            </template>
            <span>View Details</span>
          </v-tooltip>

          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn
                small
                icon
                class="mr-2"
                v-on="on"
                @click="download(item)"
                v-if="!$vuetify.breakpoint.mobile && item.group"
              >
                <v-icon>mdi-download</v-icon>
              </v-btn>
            </template>
            <span>Download Data</span>
          </v-tooltip>
        </div>
      </template>
    </v-data-table>
    <TripDialog :dialog="dialog" :data="selectedTrip" @closeDialog="dialog = false" />
  </div>
</template>

<script>
import { mapState } from 'vuex';
import Utils from '@/utils/Utils';
import Colors from '@/utils/Colors.js';
import TripDialog from '@/components/modules/cav/TripDialog.vue';
export default {
  props: {
    items: Array,
    tripId: Number
  },

  components: {
    TripDialog
  },

  data: () => ({
    dialog: false,
    selectedTrip: null,
    headers: [
      { text: 'Signal Group', value: 'group', groupable: false, sortable: false },
      { text: 'CSPI Score', value: 'score', groupable: false, sortable: false },
      { text: 'Speed Score', value: 'speedScore', groupable: false, sortable: false },
      { text: 'G/R Score', value: 'grScore', groupable: false, sortable: false },
      { text: 'Stops Score', value: 'spmScore', groupable: false, sortable: false },
      { text: 'Stops', value: 'stops', groupable: false, sortable: false },
      { text: 'Action', value: 'actions', groupable: false, sortable: false }
    ]
  }),

  computed: {
    ...mapState('cav', ['currentTrip'])
  },

  methods: {
    goToLink(item) {
      this.dialog = true;
      this.selectedTrip = item;
    },

    getGroupColor(group) {
      if (group == 'S020A') {
        return '#339900';
      } else if (group == 'S020B') {
        return '#8CBEF8';
      } else if (group == 'S020C') {
        return '#FFFF00';
      } else if (group == 'S018') {
        return '#FFC031';
      } else if (group == 'S017') {
        return '#A452C8';
      } else if (group == 'S016') {
        return '#FF0000';
      } else {
        return 'white';
      }
    },
    getPhaseColor(phase) {
      if (phase == 2) {
        return 'rgba(0,118,230,0.9)';
      } else {
        return '#76FF03';
      }
    },

    getColor(score) {
      return Colors.getRedGreenColor((score - 33) / 76);
    },

    download(item) {
      let newItem = {
        id: item.id,
        group: item.group,
        score: item.score,
        speedScore: item.speedScore,
        speed: item.speed,
        grScore: item.grScore,
        gr: item.gr,
        spmScore: item.spmScore,
        stopsPerMile: item.stopsPerMile,
        stops: item.stops,
        start: item.start,
        end: item.end,
        phase: item.phase,
        redCount: item.redCount,
        devices: item.devices,
        distTotal: item.distTotal,
        data: item.data
      };
      Utils.downloadJSON(`Trip ${this.currentTrip.id}_${item.group} CSPI`, newItem);
    }
  }
};
</script>

<style lang="scss">
.v-data-table > .v-data-table__wrapper > table > tbody > tr > td {
  font-size: 12px;
}
</style>

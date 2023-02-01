<template>
  <v-dialog scrollable v-model="dialog" persistent max-width="1000px">
    <v-card>
      <v-app-bar dark dense>
        <v-icon small class="mr-2">mdi-format-list-bulleted</v-icon>
        <v-toolbar-title>CSPI Data</v-toolbar-title>

        <v-spacer></v-spacer>

        <div class="mr-10"></div>

        <v-btn icon @click="hideDialog">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-app-bar>
      <v-card-text :style="`height: 800px;`" v-if="items">
        <v-row>
          <v-col col="12">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
              <h3>Listing</h3>
            </v-subheader>
            <v-divider class="mb-1" />
          </v-col>
        </v-row>
        <v-data-table
          :headers="headers"
          :items="items"
          :items-per-page="100"
          item-key="name"
          hide-default-footer
          :single-expand="true"
          :expanded.sync="expanded"
          show-expand
          @click:row="handleRowClick"
        >
          <template v-slot:[`item.score`]="{ item }">
            <v-chip small :color="getColor(item.score)">
              <h4 style="color: #3b3b3b;">{{ item.score }}</h4>
            </v-chip>
          </template>
          <template v-slot:expanded-item="{ headers, item }">
            <td :colspan="headers.length" v-if="item.name && item.name != 'Average'">
              <v-data-table
                :headers="headers2"
                dense
                :items="getTable(item.id)"
                :items-per-page="100"
                item-key="name"
                sort-by="id"
                class="elevation-1 secondary"
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
                  <v-icon
                    small
                    class="mr-2"
                    @click="goToLink(item)"
                    v-if="!$vuetify.breakpoint.mobile && item.group != 'Average'"
                  >
                    mdi-eye
                  </v-icon>
                </template>
              </v-data-table>
            </td>
          </template>
        </v-data-table>
        <v-row>
          <v-col col="12">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
              <h3>Aggregate Data</h3>
            </v-subheader>
            <v-divider class="mb-1" />
          </v-col>
        </v-row>
        <v-data-table :headers="headers3" :items="items2" :items-per-page="100" item-key="group" hide-default-footer>
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
        </v-data-table>
      </v-card-text>
    </v-card>
    <TripDialog :dialog="tripDialog" :data="selectedTrip" @closeDialog="tripDialog = false" />
  </v-dialog>
</template>

<script>
import Utils from '@/utils/Utils';
import Colors from '@/utils/Colors.js';
import TripDialog from '@/components/modules/cav/TripDialog.vue';
import { mapState } from 'vuex';

export default {
  props: {
    dialog: Boolean
  },

  components: {
    TripDialog
  },

  data: () => ({
    tripDialog: false,
    selectedTrip: null,
    expanded: [],
    singleExpand: false,
    headers: [
      { text: 'Name', value: 'name', groupable: false, sortable: false },
      { text: 'Start Date', value: 'date', groupable: false, sortable: false },
      { text: 'CSPI Score', value: 'score', groupable: false, sortable: false },
      { text: 'Speed Score', value: 'speedScore', groupable: false, sortable: false },
      { text: 'Green/Red Score', value: 'grScore', groupable: false, sortable: false },
      { text: 'Stops/Mile Score', value: 'spmScore', groupable: false, sortable: false },
      { text: '', value: 'data-table-expand' }
    ],
    items: [],
    headers2: [
      { text: 'Signal Group', value: 'group', groupable: false, sortable: false },
      { text: 'CSPI Score', value: 'score', groupable: false, sortable: false },
      { text: 'Speed Score', value: 'speedScore', groupable: false, sortable: false },
      { text: 'Green/Red Score', value: 'grScore', groupable: false, sortable: false },
      { text: 'Stops/Mile Score', value: 'spmScore', groupable: false, sortable: false },
      { text: '# Stops', value: 'stops', groupable: false, sortable: false },
      { text: '', value: 'actions', groupable: false, sortable: false }
    ],
    headers3: [
      { text: 'Signal Group', value: 'group', groupable: false, sortable: false },
      { text: 'CSPI Score', value: 'score', groupable: false, sortable: false },
      { text: 'Speed Score', value: 'speedScore', groupable: false, sortable: false },
      { text: 'Green/Red Score', value: 'grScore', groupable: false, sortable: false },
      { text: 'Stops/Mile Score', value: 'spmScore', groupable: false, sortable: false },
      { text: '# Trips', value: 'tripCount', groupable: false, sortable: false }
    ],
    items2: [],
    trips: []
  }),

  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      },
      ...mapState('cav', ['currentEvent', 'currentTrip', 'currentState'])
    }
  },

  mounted() {
    this.$bus.$on('CAV_TRIPS_SELECTED', trips => {
      this.processTableData(trips);
    });
  },

  methods: {
    handleRowClick(value) {
      let data = this.trips.find(x => x.id == value.id);
      if (data) {
        const trip = Object.assign({}, data);
        this.$store.commit('cav/SET_CURRENT_TRIP', trip);
        this.$emit('click', value);
      }
    },
    goToLink(item) {
      this.tripDialog = true;
      this.selectedTrip = item;
    },
    processTableData(trips) {
      this.trips = trips;
      let cspiTrips = this.trips.filter(x => x.cspiData && x.cspiData.length > 0);
      let tableData = cspiTrips.map(x => {
        let avg = x.cspiData[x.cspiData.length - 1];
        return {
          id: x.id,
          date: Utils.formatDateTime(new Date(x.startTime)),
          name: `Trip ${x.id}`,
          score: avg.score,
          speedScore: avg.speedScore,
          grScore: avg.grScore,
          spmScore: avg.spmScore
        };
      });
      tableData.push(this.getAverage(tableData));
      this.items = tableData;
      let groups = ['S020A', 'S020B', 'S020C', 'S018', 'S017', 'S016'];
      let groupsData = [];
      groups.forEach(group => {
        let groupData = [];
        this.trips.forEach(trip => {
          if (trip.cspiData) {
            let info = trip.cspiData.find(x => x.group == group);
            if (info) groupData.push(info);
          }
        });
        let speedScore = Math.round(groupData.reduce((sum, x) => sum + x.speedScore, 0) / groupData.length, 0);
        let grScore = Math.round(groupData.reduce((sum, x) => sum + x.grScore, 0) / groupData.length, 0);
        let spmScore = Math.round(groupData.reduce((sum, x) => sum + x.spmScore, 0) / groupData.length, 0);
        if (groupData.length > 0) {
          groupsData.push({
            tripData: groupData,
            group: group,
            score: speedScore + grScore + spmScore,
            speedScore: speedScore,
            grScore: grScore,
            spmScore: spmScore,
            tripCount: groupData.length
          });
        }
      });
      this.items2 = groupsData;
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
    downloadAll(items) {
      let newItem = items.map(item => {
        if (item.data) {
          return {
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
        } else {
          return item;
        }
      });
      console.log(this.items);
      Utils.downloadJSON(`Trip ${this.currentTrip.id} CSPI`, newItem);
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
    },
    getTable(id) {
      return this.trips.find(x => x.id == id).cspiData.filter(x => x.group != 'Average') || [];
    },
    hideDialog() {
      this.$emit('closeDialog');
    },
    getColor(score) {
      return Colors.getRedGreenColor((score - 33) / 76);
    },
    getAverage(data) {
      let speedScore = Math.round(data.reduce((sum, x) => sum + x.speedScore, 0) / data.length, 0);
      let grScore = Math.round(data.reduce((sum, x) => sum + x.grScore, 0) / data.length, 0);
      let spmScore = Math.round(data.reduce((sum, x) => sum + x.spmScore, 0) / data.length, 0);
      return {
        name: 'Average',
        score: speedScore + grScore + spmScore,
        speedScore: speedScore,
        grScore: grScore,
        spmScore: spmScore
      };
    }
  }
};
</script>

<style lang="scss">
.highlighted {
  background-color: rgb(215, 215, 44);
}
</style>

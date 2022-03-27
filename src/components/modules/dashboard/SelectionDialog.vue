<template>
  <v-row>
    <v-dialog scrollable v-model="show" persistent width="unset" transition="scroll-x-transition">
      <v-card>
        <v-app-bar dark :color="color" dense>
          <v-icon small class="mr-2" v-text="icon"></v-icon>
          <v-toolbar-title>{{ title }}</v-toolbar-title>
          <v-spacer></v-spacer>

          <v-text-field
            class="ml-15 shrink"
            dark
            rounded
            dense
            outlined
            clearable
            v-model="search"
            prepend-inner-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
          ></v-text-field>

          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <div style="height: 450px;">
          <vue-perfect-scrollbar class="app-drawer__scrollbar">
            <div v-if="type == 0">
              <IncidentTable
                :height="420"
                :incidents="incidents"
                :preSelect="false"
                :search="search"
                @click="handleRowClick"
              />
            </div>
            <div v-else-if="type == 2">
              <SignalPerformanceIssuesTable
                :height="420"
                :summary="signalPerformanceIssues"
                :preSelect="false"
                :search="search"
                @click="handleRowClick"
              />
            </div>
            <div v-else>
              <v-data-table
                disable-sort
                :headers="headers"
                height="420"
                fixed-header
                :items="items"
                :items-per-page="itemsPerPage"
                hide-default-footer
                :item-class="itemRowBackground"
                @click:row="handleRowClick"
                :search="search"
              >
              </v-data-table>
            </div>
          </vue-perfect-scrollbar>
        </div>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Utils from '@/utils/Utils';
import VuePerfectScrollbar from 'vue-perfect-scrollbar';
import IncidentTable from '@/components/modules/dashboard/incidents/IncidentTable';
import SignalPerformanceIssuesTable from '@/components/modules/dashboard/issues/SignalPerformanceIssuesTable';

export default {
  components: {
    VuePerfectScrollbar,
    IncidentTable,
    SignalPerformanceIssuesTable
  },
  props: {
    value: Boolean
  },

  data: () => ({
    signalPerformanceIssues: [],
    incidents: [],
    loading: false,
    height: 470,
    legendY: 15,
    search: null,
    searchModel: null,
    icon: 'mdi-format-list-bulleted-square',
    title: '',
    itemsPerPage: 200,
    type: 0,
    items: [],
    headers: [],
    searchItems: [],
    scrollSettings: {
      maxScrollbarLength: 160
    }
  }),
  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },
    color() {
      return this.$store.state.darkMode ? '' : '#009688';
    }
  },

  methods: {
    init(icon, title, type, data) {
      this.icon = icon;
      this.type = type;
      this.title = title;
      this.search = '';
      if (type === 0) {
        this.prepareTrafficIncidents(data);
      } else if (type === 1) {
        this.prepareTrafficDetectors(data);
      } else if (type === 2) {
        this.prepareSignalPerformanceIssues(data);
      } else if (type === 4) {
        this.prepareHighCongestionRoutes(data);
      } else if (type === 5) {
        this.prepareReportedWazeAlerts(data);
      }
    },

    cleanUp() {},

    hideDialog() {
      this.cleanUp();
      this.show = false;
    },

    itemRowBackground(item) {
      if (this.type === 0) {
        if (item.status > 0) {
          return 'highlighted';
        }
      } else if (this.type === 1) {
        if (item.status > 0) {
          return 'highlighted';
        }
      }
      return '';
    },

    prepareTrafficIncidents(data) {
      this.incidents = data;
    },

    prepareTrafficDetectors(data) {
      this.headers = [
        { text: 'Device', value: 'device' },
        { text: 'Status', value: 'state' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        device: d.name,
        status: d.status,
        state: d.status === 0 ? 'Normal' : 'Anomaly'
      }));
    },

    prepareSignalPerformanceIssues(data) {
      this.signalPerformanceIssues = data;
    },

    prepareHighCongestionRoutes(data) {
      console.log(data);
      this.headers = [
        { text: 'Last Updated', value: 'lastUpdated' },
        { text: 'Name', value: 'name' },
        { text: 'Description', value: 'desc' },
        { text: 'Level', value: 'level' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        lastUpdated: d.travelTime.calculationTimestamp.text,
        name: d.name,
        desc: d.desc,
        level: d.travelTime.level
      }));
    },

    prepareReportedWazeAlerts(data) {
      this.headers = [
        { text: 'Time', value: 'time' },
        { text: 'Description', value: 'desc' },
        { text: 'Street', value: 'street' },
        { text: 'Road Type', value: 'roadType' },
        { text: 'Score', value: 'score' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        time: d.alertTime,
        desc: d.description,
        street: d.address.street,
        roadType: d.roadType.name,
        score: d.confidence
      }));
    },

    prepareBluetoothData(bluetoothLocations) {
      this.headers = [
        { text: 'Device', value: 'device' },
        { text: 'Travel Time', value: 'time' }
      ];
      this.items = bluetoothLocations.map(d => ({
        id: d.id,
        device: d.id.toUpperCase() + ' - ' + d.name,
        time: this.formatDisplay(d.travelTime)
      }));
    },

    prepareWeatherStations(weatherStations) {
      this.headers = [
        { text: 'Device', value: 'device' },
        { text: 'Temperature', value: 'temp' }
      ];
      this.items = weatherStations.map(d => ({
        id: d.id,
        device: d.name,
        temp: isNaN(d.temp) || d.temp < -100 ? 'N/A' : d.temp + '°F'
      }));
    },

    prepareTravelRestrictions(restrictions) {
      this.headers = [
        { text: 'Id', value: 'id' },
        { text: 'Type', value: 'type' },
        { text: 'Description', value: 'desc' }
      ];
      if (restrictions) {
        this.items = restrictions.map(d => ({
          id: d.id,
          type: d.name,
          desc: d.data && d.data.map(dd => dd.desc.replace(/<br ?\/?>/g, '')).join(' ')
        }));
      } else {
        this.items = [];
      }
    },

    prepareAnomalySegments(segments) {
      this.headers = [
        { text: 'Segment', value: 'shortName' },
        { text: 'Start Time', value: 'time' },
        { text: 'Duration', value: 'duration' },
        { text: 'Evidences', value: 'desc' },
        { text: 'Status', value: 'status' }
      ];

      this.items = segments.map(s => ({
        id: s.id,
        shortName: s.shortName,
        time: Utils.formatTime(new Date(s.startTime)),
        duration: s.duration + 'min',
        desc: this.consolidateAnomalyTypes(s.items),
        status: s.status === 0 ? 'ongoing' : 'completed'
      }));
    },

    consolidateAnomalyTypes(items) {
      const DESCRIPTIONS = ['', 'Flow', 'Bluetooth', 'Waze', 'Restriction', ''];
      const types = [...new Set(items.map(item => item.type))];
      return Array.from(types)
        .filter(t => t !== 5)
        .map(t => DESCRIPTIONS[t])
        .join(', ');
    },

    formatDisplay(seconds) {
      if (!seconds) {
        return '';
      }
      let minutes = seconds / 60;
      return `${minutes.toFixed(1)} min`;
    },

    handleRowClick(value) {
      const id = value.id;
      const type = this.type;
      this.hideDialog();
      console.log('A');
      this.$bus.$emit('DISPLAY_MARKER_DETAILS_DASHBOARD', { id, type });
    }
  }
};
</script>

<style lang="scss">
.highlighted {
  background-color: rgb(215, 215, 44);
}
</style>

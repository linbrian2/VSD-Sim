<template>
  <v-row>
    <v-dialog scrollable v-model="show" persistent max-width="900px">
      <v-card>
        <v-app-bar dark :color="color" dense>
          <v-icon small class="mr-2" v-text="icon"></v-icon>
          <v-toolbar-title>{{ title }}</v-toolbar-title>
          <v-spacer></v-spacer>

          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <div style="height: 550px;">
          <v-card>
            <v-toolbar dense>
              <v-tabs
                dense
                color="teal accent-4"
                fixed-tabs
                show-arrows="mobile"
                class="pr-1"
                v-model="tab"
                @change="tabChanged"
              >
                <v-tab v-for="category in categories" :key="category.id">
                  <v-icon left>{{ category.icon }}</v-icon>
                </v-tab>
              </v-tabs>
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
            </v-toolbar>

            <!-- <v-card-title :class="$vuetify.breakpoint.mobile ? 'pl-2 pa-0' : null"> -->
            <vue-perfect-scrollbar class="app-drawer__scrollbar">
              <v-data-table
                disable-sort
                :headers="headers"
                height="450"
                fixed-header
                :items="items"
                :items-per-page="itemsPerPage"
                hide-default-footer
                :item-class="itemRowBackground"
                @click:row="handleRowClick"
                :search="search"
              >
              </v-data-table>
            </vue-perfect-scrollbar>
            <!-- </v-card-title> -->
          </v-card>
        </div>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Utils from '@/utils/Utils';
import VuePerfectScrollbar from 'vue-perfect-scrollbar';
import Constants from '@/utils/constants/traffic';
export default {
  components: {
    VuePerfectScrollbar
  },
  props: {
    value: Boolean
  },

  data: () => ({
    loading: false,
    height: 470,
    legendY: 15,
    search: null,
    searchModel: null,
    icon: 'mdi-format-list-bulleted-square',
    title: '',
    itemsPerPage: 200,
    type: 0,
    tab: 0,
    sensorData: {},
    items: [],
    headers: [],
    searchItems: [],
    scrollSettings: {
      maxScrollbarLength: 160
    },
    categories: [
      { title: Constants.DEVICE_TRAFFIC, icon: Constants.DEVICE_TRAFFIC_ICON, id: 0 },
      { title: Constants.DEVICE_BLUETOOTH, icon: Constants.DEVICE_BLUETOOTH_ICON, id: 1 },
      { title: Constants.DEVICE_WEATHER, icon: Constants.DEVICE_WEATHER_ICON, id: 2 },
      { title: Constants.DEVICE_RESTRICTIONS, icon: Constants.DEVICE_RESTRICTIONS_ICON, id: 3 },
      { title: Constants.DEVICE_SEGMENTS, icon: Constants.DEVICE_SEGMENTS_ICON, id: 4 }
    ]
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
    init(data) {
      this.sensorData = data;
      console.log(this.sensorData);
      this.tab = 0;
      this.prepareData(this.tab);
    },

    cleanUp() {},

    prepareData(type) {
      const c = this.categories[type];
      this.icon = c.icon;
      this.type = c.id;
      this.title = c.title;
      this.search = '';
      switch (this.type) {
        case 0:
          this.prepareTrafficDetectors(this.sensorData.flowDetectors);
          break;
        case 1:
          this.prepareBluetoothData(this.sensorData.bluetoothSensors);
          break;
        case 2:
          this.prepareWeatherStations(this.sensorData.weatherStations);
          break;
        case 3:
          this.prepareTravelRestrictions(this.sensorData.restrictions);
          break;
        case 4:
          this.prepareAnomalySegments(this.sensorData.anomalySegments);
          break;
      }
    },

    tabChanged() {
      this.prepareData(this.tab);
    },

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

    getArea(zone) {
      if (zone == 1) {
        return 'Urban Freeway';
      } else if (zone == 3) {
        return 'CAV Area Freeway';
      } else if (zone == 4) {
        return 'CAV Area Arterial';
      } else if (zone == 7) {
        return 'Beach Area';
      } else {
        return '';
      }
    },

    prepareTrafficDetectors(deviceLocations) {
      this.headers = [
        { text: 'Device', value: 'device' },
        { text: 'Area', value: 'area' },
        { text: 'Status', value: 'state' }
      ];
      this.items = deviceLocations.map(d => ({
        id: d.id,
        device: d.name,
        status: d.status,
        area: this.getArea(d.zone),
        state: d.status === 0 ? 'Normal' : 'Anomaly'
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
      this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id, type });
    }
  }
};
</script>

<style lang="scss">
.highlighted {
  background-color: rgb(215, 215, 44);
}
</style>

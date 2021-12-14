<template>
  <div>
    <!-- Left map panel -->
    <SelectionPanel name="travelTimeDataBarWidth">
      <div class="d-flex justify-space-between">
        <v-combobox
          multiple
          small-chips
          class="mx-2"
          dense
          hide-details
          single-line
          :items="items"
          item-text="name"
          v-model="valuesSelected"
          @input="valueSelectHandler"
          label="SELECT SEGMENTS TO SHOW"
          return-object
        >
          <template v-slot:append-outer>
            <v-btn icon @click="clear">
              <v-icon>mdi-backspace</v-icon>
            </v-btn>
            <!-- Region selection menu -->
            <v-menu bottom right offset-y min-width="250" :close-on-content-click="true">
              <template v-slot:activator="{ on: menu, attrs }">
                <v-tooltip bottom>
                  <template v-slot:activator="{ on: tooltip }">
                    <v-btn icon class="mx-1" v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                      <v-icon dark>mdi-dots-vertical</v-icon>
                    </v-btn>
                  </template>
                  <span>Route Selection</span>
                </v-tooltip>
              </template>

              <v-list dense>
                <v-list-item
                  v-for="item in route_menu_items"
                  :key="item.value"
                  @click="routeMenuItemClicked(item.value)"
                >
                  <v-list-item-title :class="{ 'font-weight-black': item.value === selectedRoute }">
                    <v-icon class="mr-1" v-if="item.value === selectedRoute">mdi-check</v-icon>
                    <span :class="{ 'ml-8': item.value !== selectedRoute }"> {{ item.text }} </span>
                  </v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </template>
        </v-combobox>
      </div>
      <MapMultigraphSelect ref="mapSelect" :markers="markers" :icons="icons" @click="markerClicked" />
    </SelectionPanel>

    <!-- Title bar on the top -->
    <TitleBar :isMultigraph="true" :showId="false" :refresh="refreshData">
      <div class="d-flex justify-center align-center">
        <div class="mt-1 mr-6" style="width: 200px">
          <v-select
            dark
            dense
            v-model="selectedVal"
            :items="valItems"
            item-text="text"
            item-value="value"
            hide-details
            single-line
          />
        </div>
        <div style="width: 120px; margin-top: 5px">
          <v-select
            dark
            dense
            v-model="interval"
            :items="intervalItems"
            item-text="text"
            item-value="value"
            @input="intervalSelected"
            hide-details
            prepend-icon="mdi-clock-outline"
            single-line
          />
        </div>
      </div>
    </TitleBar>

    <!-- Charts -->
    <v-container>
      <!-- {{valuesSelected}} -->
      <v-card class="mb-8" v-if="selectedVal == valItems[0]">
        <v-row>
          <v-col cols="12" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
            <div v-if="i.data && i.data.travelTime" class="graph-container">
              <v-btn icon @click="removeItem(i.name)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <BasicChart :data="i.data.travelTime" :height="height" />
            </div>
            <div v-else-if="i.data == -1" class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.name)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{i.name}}</h2>
              <h3>Data is Unavailable.</h3>
            </div>
            <div v-else class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.name)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{i.name}}</h2>
              <h3>Loading Data...</h3>
            </div>
          </v-col>
        </v-row>
      </v-card>

      <v-card class="mb-8" v-if="selectedVal == valItems[1]">
        <v-row>
          <v-col cols="12" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
            <div v-if="i.data && i.data.travelSpeed" class="graph-container">
              <v-btn icon @click="removeItem(i.name)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <BasicChart :data="i.data.travelSpeed" :height="height" />
            </div>
            <div v-else-if="i.data == -1" class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.name)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{i.name}}</h2>
              <h3>Data is Unavailable.</h3>
            </div>
            <div v-else class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.name)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{i.name}}</h2>
              <h3>Loading Data...</h3>
            </div>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState, mapActions } from 'vuex';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapMultigraphSelect from '@/components/modules/traffic/map/MapMultigraphSelect';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import BasicChart from '@/components/modules/traffic/common/BasicChart';

export default {
  components: {
    SelectionPanel,
    MapMultigraphSelect,
    TitleBar,
    BasicChart,
  },
  data: () => ({
    category: null,
    selectedVal: 'Travel Time',
    valItems: ['Travel Time', 'Travel Speed'],

    loading: false,
    height: 480,
    title: 'Travel Time Data',

    interval: 300000,

    intervalItems: [
      { text: '1 min', value: 60000 },
      { text: '5 mins', value: 300000 },
      { text: '15 mins', value: 900000 },
      { text: '30 mins', value: 1800000 },
      { text: '1 Hour', value: 3600000 },
    ],

    icons: [
      {
        path: 0,
        scale: 10.0,
        fillColor: '#0580FF',
        fillOpacity: 0.8,
        strokeWeight: 1.0,
        strokeColor: 'white',
      },
      {
        path: 0,
        scale: 10.0,
        fillColor: '#FF7F00',
        fillOpacity: 0.8,
        strokeWeight: 1.0,
        strokeColor: 'white',
      },
    ],
    valuesSelected: [],

    route_menu_items: [
      { text: 'All Segments', value: '' },
      { text: 'US-13: N DUPONT PKWY', value: 'US13', directions: ['NB', 'SB'] },
      { text: 'DE-1: KOREAN WAR VETERANS MEM HWY', value: 'DE1', directions: ['NB', 'SB'] },
      { text: 'US-113: DUPONT BLVD', value: 'US113', directions: ['NB', 'SB'] },
      { text: 'I-95: INTERSTATE 95', value: 'I-95', directions: ['NB', 'SB'] },
      { text: 'DE-273: CHRISTIANA RD', value: 'DE273', directions: ['WB', 'EB'] },
      { text: 'US-301: MIDDLETOWN WARWICK RD', value: 'US301', directions: ['NB', 'SB'] },
      { text: 'DE-896: SUMMIT BRIDGE RD', value: 'DE896', directions: ['NB', 'SB'] },
      { text: 'DE-7: BEAR CORBITT RD', value: 'DE7', directions: ['NB', 'SB'] },
      { text: 'DE-4: CHRISTINA PKWY', value: 'DE4', directions: ['WB', 'EB'] },
      { text: 'DE-24: JOHN WILLIAMS HWY', value: 'DE24', directions: ['WB', 'EB'] },
      { text: 'DE-2: KIRKWOOD HWY', value: 'DE2', directions: ['WB', 'EB'] },
      { text: 'DE-141: BARLEY MILL RD', value: 'DE141', directions: ['NB', 'SB'] },
      { text: 'US-202: CONCORD PIKE', value: 'US202', directions: ['NB', 'SB'] },
      { text: 'US-9: MARKET ST', value: 'US9', directions: ['WB', 'EB'] },
      { text: 'DE-8: LITTLE CREEK RD', value: 'DE8', directions: ['WB', 'EB'] },
    ],

    selectedRoute: '',
  }),
  computed: {
    markers() {
      if (!this.selectedRoute) {
        return this.bluetoothSegments;
      } else {
        return this.bluetoothSegments.filter((segment) => segment.route === this.selectedRoute);
      }
    },

    items() {
      return this.markers.map((item) => {
        return { id: item.id, name: item.name, data: null };
      });
    },

    multigraphModeSelect: {
      get() {
        return this.$store.state.traffic.multigraphModeSelect;
      },
      set(val) {
        this.$store.commit('traffic/SET_MULTIGRAPH_MODE_SELECT', val);
      },
    },
    ...mapState(['currentDate']),
    ...mapState('traffic', ['bluetoothSegments', 'multigraphModes']),
  },

  mounted() {
    if (this.bluetoothSegments.length === 0) {
      this.fetchBluetoothSegments();
    }

    // Load first selected data in case of no data showing
    /* setTimeout(() => {
      this.showDataIfEmpty();
    }, 500); */
  },

  watch: {
    currentDate() {
      this.refreshData();
    },
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  methods: {
    clear() {
      this.valuesSelected = [];
      this.$bus.$emit('NAME_SELECTED', []);
    },

    removeItem(name) {
      this.valuesSelected = this.valuesSelected.filter(x => x.name && x.name != name)
      this.$bus.$emit('NAME_SELECTED', this.valuesSelected);
    },

    valueSelectHandler(value) {
      if (value && value.length > 0 && value[value.length - 1]) {
        let marker = this.markers.find(m => m.name === value[value.length - 1].name);
        const time = this.currentDate.getTime();
        this.fetchTravelTimeData(marker.id, this.interval, time, marker.name);
        this.$bus.$emit('NAME_SELECTED', value);
      }
    },

    markerClicked(marker, action, fromMap = true) {
      if (fromMap) {
        if (action == "remove") {
          this.valuesSelected = this.valuesSelected.filter(x => x.name && x.name != marker.name)
        } else {
          this.valuesSelected.push({ id: marker.id, name: marker.name, data: null });
          const time = this.currentDate.getTime();
          this.fetchTravelTimeData(marker.id, this.interval, time, marker.name);
        }
      }
    },

    routeMenuIcon(item) {
      return item.value === this.selectedRoute ? 'mdi-check' : '';
    },

    routeMenuItemClicked(value) {
      setTimeout(() => {
        this.selectedRoute = value;
        this.valuesSelected = [];
      }, 100);
    },

    intervalSelected() {
      this.fetchData();
    },

    refreshData() {
      this.fetchData();
    },

    showDataIfEmpty() {
      const any = Object.values(this.availability).some((item) => item);
      if (!any) {
        this.$bus.$emit('CENTER_MAP');
        this.$bus.$emit('SELECT_FIRST');
      }
    },

    fetchData() {
      const time = this.currentDate.getTime();
      
      this.valuesSelected.forEach(x => {
        let marker = this.markers.find(m => m.name === x.name);
        this.fetchTravelTimeData(marker.id, this.interval, time, marker.name);
      });
    },

    async fetchTravelTimeData(id, interval, time, title = null) {
      this.loading = true;

      try {
        // Now we await for both results, whose async processes have already been started
        const response = await Api.fetchTravelTimeData(id, interval, time);

        let travelTimeList = this.getResponseData(response);
        let data = -1
        if (travelTimeList) {
          data = {
            travelTime: this.formTravelTimeData(travelTimeList, title),
            travelSpeed: this.formSpeedData(travelTimeList, title)
          }
        }
        this.valuesSelected.forEach(val => {
          if (val.id == id) {
            val.data = data
          }
        });
      } catch (error) {
        this.valuesSelected.forEach(val => {
          if (val.id == id) {
            val.data = -1
          }
        });
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    getResponseData(response) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'warning' });
      }
      return result;
    },

    formTravelTimeData(travelTimeList, title = null) {
      let subtitle = 'Travel Time';
      let xAxis = 'Time of day';
      let yAxis = 'Travel Time (s)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.travelTime });
      data.push({ name: 'Baseline', color: '#50B432', data: travelTimeList.baselineTravelTime });

      return { data, xAxis, yAxis, title, subtitle };
    },

    formSpeedData(travelTimeList, title = null) {
      let subtitle = 'Travel Speed';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.speed });
      data.push({ name: 'Baseline', color: '#50B432', data: travelTimeList.baselineSpeed });

      return { data, xAxis, yAxis, title, subtitle };
    },

    ...mapActions('traffic', ['fetchBluetoothSegments']),
  },
};
</script>

<style lang="scss" scoped>
.graph-container {
  position: relative;
}
.graph-close-button {
  position: absolute; 
  right: 0px;
  z-index: 99;
}
.basic-chart {
  height: 500px;
}
.option-right {
  margin-left: auto;
  margin-right: 0;
}
</style>

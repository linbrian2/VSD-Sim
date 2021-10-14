<template>
  <div>
    <!-- Left map panel -->
    <SelectionPanel name="travelTimeDataBarWidth">
      <div class="d-flex justify-space-between">
        <v-combobox
          class="mx-2"
          dense
          hide-details
          single-line
          :items="items"
          :value="valueSelected"
          @input="valueSelectHandler"
          label="CHOOSE A SEGMENT TO SHOW"
        />
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
            <v-list-item v-for="item in route_menu_items" :key="item.value" @click="routeMenuItemClicked(item.value)">
              <v-list-item-title :class="{ 'font-weight-black': item.value === selectedRoute }">
                <v-icon class="mr-1" v-if="item.value === selectedRoute">mdi-check</v-icon>
                <span :class="{ 'ml-8': item.value !== selectedRoute }"> {{ item.text }} </span>
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
      <MapSelect ref="mapSelect" :markers="markers" :icons="icons" @click="markerClicked" />
    </SelectionPanel>

    <!-- Title bar on the top -->
    <TitleBar :title="title" :showId="false" showUid :loading="loading" :refresh="refreshData">
      <div class="d-flex justify-center align-center">
        <div style="width:140px; margin-top: 5px">
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
      <v-card class="mb-8" v-if="availability.travelTime">
        <BasicChart :data="travelTimeData" :height="height" />
      </v-card>

      <v-card class="mb-8" v-if="availability.travelSpeed">
        <BasicChart :data="travelSpeedData" :height="height" />
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState, mapActions } from 'vuex';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapSelect from '@/components/modules/traffic/map/MapSelect';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import BasicChart from '@/components/modules/traffic/common/BasicChart';

export default {
  components: {
    SelectionPanel,
    MapSelect,
    TitleBar,
    BasicChart
  },
  data: () => ({
    loading: false,
    height: 480,
    title: 'Travel Time Data',

    interval: 300000,

    intervalItems: [
      { text: '1 min', value: 60000 },
      { text: '5 mins', value: 300000 },
      { text: '15 mins', value: 900000 },
      { text: '30 mins', value: 1800000 },
      { text: '1 Hour', value: 3600000 }
    ],

    icons: [
      {
        path: 0,
        scale: 10.0,
        fillColor: '#0580FF',
        fillOpacity: 0.8,
        strokeWeight: 1.0,
        strokeColor: 'white'
      },
      {
        path: 0,
        scale: 10.0,
        fillColor: '#FF7F00',
        fillOpacity: 0.8,
        strokeWeight: 1.0,
        strokeColor: 'white'
      }
    ],
    valueSelected: '',

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
      { text: 'DE-8: LITTLE CREEK RD', value: 'DE8', directions: ['WB', 'EB'] }
    ],

    selectedRoute: '',

    travelTimeData: {},
    travelSpeedData: {},
    availability: {
      travelTime: false,
      travelSpeed: false
    }
  }),
  computed: {
    markers() {
      if (!this.selectedRoute) {
        return this.bluetoothSegments;
      } else {
        return this.bluetoothSegments.filter(segment => segment.route === this.selectedRoute);
      }
    },

    items() {
      return this.markers.map(item => item.name);
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['activeMarker', 'bluetoothSegments'])
  },

  mounted() {
    if (this.bluetoothSegments.length === 0) {
      this.fetchBluetoothSegments();
    }

    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.showDataIfEmpty();
    }, 500);
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  methods: {
    valueSelectHandler(value) {
      this.$bus.$emit('NAME_SELECTED', value);
    },

    markerClicked(marker) {
      this.valueSelected = marker.name;
      const time = this.currentDate.getTime();
      this.fetchTravelTimeData(marker.id, this.interval, time);
    },

    routeMenuIcon(item) {
      return item.value === this.selectedRoute ? 'mdi-check' : '';
    },

    routeMenuItemClicked(value) {
      setTimeout(() => {
        this.selectedRoute = value;
        this.valueSelected = '';
      }, 100);
    },

    intervalSelected() {
      this.fetchData();
    },

    refreshData() {
      this.fetchData();
    },

    showDataIfEmpty() {
      const any = Object.values(this.availability).some(item => item);
      if (!any) {
        this.$bus.$emit('CENTER_MAP');
        this.$bus.$emit('SELECT_FIRST');
      }
    },

    fetchData() {
      let marker = this.activeMarker;
      let time = this.currentDate.getTime();
      if (marker != null) {
        this.fetchTravelTimeData(marker.id, this.interval, time);
      }
    },

    async fetchTravelTimeData(id, interval, time) {
      this.loading = true;

      try {
        // Now we await for both results, whose async processes have already been started
        const response = await Api.fetchTravelTimeData(id, interval, time);

        let travelTimeList = this.getResponseData(response);
        if (travelTimeList) {
          this.travelTimeData = this.formTravelTimeData(travelTimeList);
          this.travelSpeedData = this.formSpeedData(travelTimeList);
          this.availability.travelTime = travelTimeList.speed ? true : false;
          this.availability.travelSpeed = travelTimeList.travelTime ? true : false;
        } else {
          this.availability.travelTime = false;
          this.availability.travelSpeed = false;
        }
      } catch (error) {
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

    formTravelTimeData(travelTimeList) {
      let title = 'Travel Time';
      let xAxis = 'Time of day';
      let yAxis = 'Travel Time (s)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.travelTime });
      data.push({ name: 'Baseline', color: '#50B432', data: travelTimeList.baselineTravelTime });

      return { data, xAxis, yAxis, title };
    },

    formSpeedData(travelTimeList) {
      let title = 'Travel Speed';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.speed });
      data.push({ name: 'Baseline', color: '#50B432', data: travelTimeList.baselineSpeed });

      return { data, xAxis, yAxis, title };
    },

    ...mapActions('traffic', ['fetchBluetoothSegments'])
  }
};
</script>

<style lang="scss" scoped>
.basic-chart {
  height: 500px;
}
</style>

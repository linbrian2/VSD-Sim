<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
    <SelectionPanel name="travelTimeMapBarWidth" ref="panel">
      <v-select
        dense
        v-model="routeDir"
        :items="routeDirections"
        item-text="text"
        item-value="value"
        hide-details
        prepend-icon="mdi-directions"
        single-line
      />
      <MapSegments ref="mapSegments" :segments="segments" @detector-clicked="detectorClicked" />
    </SelectionPanel>

    <TitleBar title="Travel Time Map" :loading="loading" :refresh="refreshData">
      <div class="d-flex justify-center align-center">
        <div class="d-flex justify-center mt-n3" style="width: 360px;">
          <v-select
            dark
            style="font-size: 14px"
            v-model="route"
            :items="routeItems"
            item-text="text"
            item-value="value"
            @input="routeSelected"
            hide-details
            prepend-icon="mdi-directions"
            single-line
          />
        </div>
        <div class="d-flex justify-center mt-n3" style="width:150px; margin-left: 250px">
          <v-select
            dark
            style="font-size: 14px"
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

    <v-container>
      <v-card class="mb-10">
        <v-container v-if="isData0Available">
          <TravelTimeHeatMapChart
            :data="heatMapData0"
            :height="mapHeight(heatMapData0)"
            :yLabelClicked="labelClicked"
            :titleClicked="titleClicked"
          />
        </v-container>
      </v-card>
      <v-card class="mb-10">
        <v-container v-if="isData1Available">
          <TravelTimeHeatMapChart
            :data="heatMapData1"
            :height="mapHeight(heatMapData1)"
            :yLabelClicked="labelClicked"
            :titleClicked="titleClicked"
          />
        </v-container>
      </v-card>
    </v-container>

    <!-- Popup Dialogs -->
    <TrafficFlowChartsDialog v-model="showDialog" ref="trafficFlowCharts" />
  </div>

  <div class="mobile" v-else>
    <!-- Title Bar -->
    <!-- <TitleBar :showMap="false" :showRefresh="false" /> -->

    <!-- Input & Map -->
    <v-select
      dense
      v-model="routeDir"
      :items="routeDirections"
      item-text="text"
      item-value="value"
      hide-details
      prepend-icon="mdi-directions"
      single-line
    />
    <MapSegments ref="mapSegments" :segments="segments" @detector-clicked="detectorClicked" :smallMap="true" />

    <!-- Container -->
    <v-container>
      <div class="d-flex justify-center align-center">
        <div class="d-flex justify-center mt-n3" style="width: 150px;">
          <v-select
            style="font-size: 14px"
            v-model="route"
            :items="routeItems"
            item-text="text"
            item-value="value"
            @input="routeSelected"
            hide-details
            prepend-icon="mdi-directions"
            single-line
          />
        </div>
        <div class="d-flex justify-center mt-n3" style="width: 150px; margin-left: 10px">
          <v-select
            style="font-size: 14px"
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
      <v-card class="mb-10">
        <v-container v-if="isData0Available">
          <TravelTimeHeatMapChart
            :data="heatMapData0"
            :height="mapHeight(heatMapData0)"
            :yLabelClicked="labelClicked"
            :titleClicked="titleClicked"
          />
        </v-container>
      </v-card>
      <v-card class="mb-10">
        <v-container v-if="isData1Available">
          <TravelTimeHeatMapChart
            :data="heatMapData1"
            :height="mapHeight(heatMapData1)"
            :yLabelClicked="labelClicked"
            :titleClicked="titleClicked"
          />
        </v-container>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import TravelTimeHeatMapChart from '@/components/modules/traffic/TravelTime/TravelTimeHeatMapChart';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapSegments from '@/components/modules/traffic/map/MapSegments';
import TrafficFlowChartsDialog from '@/components/modules/traffic/TravelTime/TrafficFlowChartsDialog';

export default {
  components: {
    TitleBar,
    SelectionPanel,
    MapSegments,
    TravelTimeHeatMapChart,
    TrafficFlowChartsDialog
  },
  data: () => ({
    loading: false,

    showDialog: false,

    routeDir: '',
    routeDirections: [],

    route: 'I-95',
    routeItems: [
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

    interval: 600,
    intervalItems: [
      { text: '1 HOUR', value: 3600 },
      { text: '30 MINs', value: 1800 },
      { text: '15 MINs', value: 900 },
      { text: '10 MINs', value: 600 },
      { text: '5 MINs', value: 300 }
    ],
    height: 650,
    allSegments: [],
    heatMapData0: {},
    heatMapData1: {}
  }),
  computed: {
    isData0Available() {
      return !Utils.isEmpty(this.heatMapData0);
    },

    isData1Available() {
      return !Utils.isEmpty(this.heatMapData1);
    },

    segments() {
      const index = this.routeDirections.findIndex(r => r.value === this.routeDir);
      return index == 0 ? this.allSegments[0] : this.allSegments[1];
    },
    ...mapState(['currentDate'])
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', false);
  },

  mounted() {
    this.fetchData();
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  methods: {
    refreshData() {
      this.fetchData();
    },

    fetchData() {
      this.fetchTravelTimeSeriesData(this.currentDate, this.interval);
    },

    routeSelected() {
      this.fetchData();
    },

    intervalSelected() {
      this.fetchData();
    },

    labelClicked(label) {
      const segment = this.segments.find(s => s.name === label);
      if (segment == null) {
        // Switch the segments
        const index = this.routeDirections.findIndex(r => r.value === this.routeDir);
        this.routeDir = this.routeDirections[(index + 1) % 2].value;
      }

      this.$refs.mapSegments.selectSegmentByShortName(label);
      this.$refs.panel.showPanelIfNot();
    },

    titleClicked(title) {
      // Extract direction
      const arr = title.split('-');
      const direction = arr[arr.length - 1].trim();
      if (this.routeDir !== direction) {
        this.routeDir = direction;
        this.$refs.panel.showPanelIfNot();
      }
    },

    detectorClicked(marker) {
      const direction = this.routeDir;
      const name = `${marker.id}-${direction}-${marker.uid}-${marker.title}`;
      const deviceId = marker.id;

      this.$refs.trafficFlowCharts.init(name, deviceId, direction, this.currentDate);
      this.showDialog = true;
    },

    mapHeight(data) {
      if (data.ycategories) {
        const size = data.ycategories.length;
        return Math.round(120 + 50 * size);
      } else {
        return this.height;
      }
    },

    async fetchTravelTimeSeriesData(startTime, interval) {
      this.loading = true;
      try {
        const start = startTime.getTime();
        const route = this.route;
        const routeItem = this.routeItems.find(r => r.value === route);
        const directions = routeItem.directions;

        // Now we await for both results, whose async processes have already been started
        const [resSegments0, resSegments1, res0, res1] = await Promise.all([
          Api.fetchTravelTimeSegments(route, directions[0]),
          Api.fetchTravelTimeSegments(route, directions[1]),
          Api.fetchTravelTimeHeatMapData(route, directions[0], start, interval),
          Api.fetchTravelTimeHeatMapData(route, directions[1], start, interval)
        ]);

        const segments0 = this.getResponseData(resSegments0);
        const segments1 = this.getResponseData(resSegments1);
        const data0 = this.getResponseData(res0);
        const data1 = this.getResponseData(res1);

        this.routeDir = directions[0];
        this.routeDirections = directions.map(direction => ({
          text: `${routeItem.text} - ${direction}`,
          value: direction
        }));

        this.allSegments = [];
        this.allSegments.push(segments0 ? segments0 : []);
        this.allSegments.push(segments1 ? segments1 : []);

        if (data0) {
          this.heatMapData0 = this.formHeatmapData(route, directions[0], startTime, interval, data0, segments0);
          this.heatMapData0.index = 0;
        }

        if (data1) {
          this.heatMapData1 = this.formHeatmapData(route, directions[1], startTime, interval, data1, segments1);
          this.heatMapData1.index = 1;
        }

        setTimeout(() => {
          this.$refs.mapSegments.setCenterMap();
        }, 1000);
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
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },

    formHeatmapData(route, direction, startTime, interval, data, segments) {
      // Find attached devices for each segment listed in the y-categories
      const devices = {};
      data.ycategories.forEach(shortName => {
        const segment = segments.find(s => s.name === shortName);
        const value = segment && segment.devices ? true : false;
        devices[shortName] = value;
      });

      // Compose result
      let result = {};
      result.title = `${route} - ${direction}`;
      result.xAxis = 'Time of day (hour)';
      result.yAxis = '';
      result.xcategories = data.xcategories;
      result.ycategories = data.ycategories;
      result.descriptions = data.descriptions;
      result.devices = devices;
      result.data = data.series;
      result.startTime = Utils.getStartOfDay(startTime);
      result.interval = interval;
      return result;
    }
  }
};
</script>

<style lang="scss" scoped>
.basic-chart {
  height: 500px;
}
.link-btn {
  margin-top: -30px;
  margin-right: 20px;
  z-index: 10;
}
.col1 {
  width: 150px;
  margin-left: 40px;
}
.col1 {
  flex: 0 0 auto;
}

.flex-container {
  width: 100%;
  min-height: 400px;
  margin: 0 auto;
  display: flex;
}

.flex-container .column {
  padding: 10px;
  background: #dbdfe5;
  flex: 1;
}
</style>

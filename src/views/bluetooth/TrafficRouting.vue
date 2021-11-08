<template>
  <div id="historical-mode">
    <!-- Toggle Button -->
    <v-tooltip bottom>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          small
          v-bind="attrs"
          v-on="on"
          @click="toggleTrafficRouting()"
          style="position: absolute; top: 10px; left: 382px; height: 40px"
        >
          <v-icon>mdi-vector-radius</v-icon>
        </v-btn>
      </template>
      <span>Toggle Traffic Routing</span>
    </v-tooltip>
    <!-- Card -->
    <v-scale-transition>
      <v-card
        style="position: absolute; top: 60px; left: 10px"
        class="pa-2"
        v-show="
          mapLayerSelection &&
          $store.state.bluetooth.map &&
          $store.state.bluetooth.modes.trafficRouting &&
          !$store.state.bluetooth.modes.addFromMap
        "
      >
        <v-card-title> Traffic Routing </v-card-title>
        <v-col>
          <v-row>
            <v-col class="py-0">
              <v-text-field
                outlined
                dense
                label="Start Coordinate"
                append-icon="mdi-close"
                @click:append="doClearSource"
                v-model="sourceString"
                hint="Use bounding box to set coordinate!"
                class="coordInput"
                style="width: 220px"
              >
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col class="py-0">
              <v-text-field
                outlined
                dense
                label="End Coordinate"
                append-icon="mdi-close"
                @click:append="doClearDest"
                v-model="destString"
                hint="Use bounding box to set coordinate!"
                id="coordInput"
                style="width: 220px"
                >>
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col class="py-0">
              <v-btn
                class="mr-5"
                color="orange"
                :loading="loading"
                :disabled="!(sourceString && destString)"
                @click="getShortestPath"
              >
                <span slot="loader">locating...</span>
                <span slot="loader" class="custom-loader">
                  <v-icon dark>cached</v-icon>
                </span>
                Get Route
              </v-btn>
              <v-btn color="orange" @click="clearData"> Clear </v-btn>
            </v-col>
          </v-row>
          <v-row v-if="ett && ett.str" class="pt-5 grid-center">
            <h3>{{ ett.str }}</h3>
          </v-row>
        </v-col>
      </v-card>
    </v-scale-transition>
  </div>
</template>

<script>
import { eventBus } from '@/utils/EventBusBT.js';
import axios from 'axios';

export default {
  name: 'RouteDetour',
  data() {
    return {
      sourceString: '',
      destString: '',
      sourceCoordinate: [39.5799, -75.6971],
      destCoordinate: [39.69659, -75.55035],
      shortestPath: null,
      endMarker: null,
      coords: null,
      loader: null,
      loading: false,
      RoutingUrl: 'http://10.5.71.38:23902/graph',
      ett: null,
      /* Map Data */
      textbubble: require('@/assets/textbubble.png'),
      clickIndex: 0,
      detourPoly: null,
      traveltimePopup: null,
      endLatLng: null,
      sourceMarker: null,
      destMarker: null,
    };
  },

  mounted: function () {
    eventBus.$on('newSource', (sourceCoordinate) => {
      this.updateSource(sourceCoordinate);
    });
    eventBus.$on('newDest', (destCoordinate) => {
      this.updateDest(destCoordinate);
      this.getShortestPath();
    });
    this.$bus.$on('UPDATE_ETT_STR', (ett) => {
      this.ett = ett;
    });
    eventBus.$on('launchShortestPath', (payload) => {
      this.plotShortestPath(payload);
    });
    eventBus.$on('clearRouting', () => {
      this.clearRoutingComponent();
    });
  },

  methods: {
    toggleTrafficRouting() {
      if (this.$store.state.bluetooth.modes.trafficRouting) {
        this.$bus.$emit('CHANGE_LAYER', 5, 'remove');
      } else {
        this.$bus.$emit('CHANGE_LAYER', 5, 'add');
      }
      this.$store.state.bluetooth.modes.trafficRouting = !this.$store.state.bluetooth.modes.trafficRouting;
    },
    clearData() {
      eventBus.$emit('clearRouting');
      this.sourceString = '';
      this.destString = '';
    },
    updateSource(sourceString) {
      let coordsArr = sourceString.split(',').map((x) => parseFloat(x).toFixed(5));
      this.sourceString = `${coordsArr[0]}°, ${coordsArr[1]}°`;
      this.sourceCoordinate = this.parseCoordinateString(this.sourceString);
    },
    updateDest(destString) {
      let coordsArr = destString.split(',').map((x) => parseFloat(x).toFixed(5));
      this.destString = `${coordsArr[0]}°, ${coordsArr[1]}°`;
      this.destCoordinate = this.parseCoordinateString(this.destString);
    },
    getShortestPath() {
      let startCoordinate = this.parseCoordinateString(this.sourceString);
      let endCoordinate = this.parseCoordinateString(this.destString);
      this.loading = true;
      if (startCoordinate.length != 2 || endCoordinate.length != 2) return;
      let params = {
        coords1: startCoordinate,
        coords2: endCoordinate,
        id: 'test',
      };
      axios.post(this.RoutingUrl, params).then((response) => {
        this.coords = response.data.paths;
        this.travelTime = response.data.traveltime;
        eventBus.$emit('launchShortestPath', {
          coords: this.coords,
          travelTime: this.travelTime,
        });
        let notifText = 'Successfully computed shortest path.';
        this.$store.commit('bluetooth/SET_NOTIFICATION', { show: true, text: notifText, timeout: 2500, color: 'info' });
      });
      this.loading = false;
    },
    parseCoordinateString(string) {
      string = string.replace('°', '');
      let coordinateArray = string.split(',');
      coordinateArray = coordinateArray.map(toFloat);
      function toFloat(stringValue) {
        let trimmed = stringValue.trim();
        return parseFloat(trimmed);
      }
      return coordinateArray;
    },
    doClearSource() {
      this.sourceString = '';
      this.sourceCoordinate = this.parseCoordinateString(this.sourceString);
    },
    doClearDest() {
      this.destString = '';
      this.destCoordinate = this.parseCoordinateString(this.destString);
    },
    // Map Methods
    plotShortestPath(payload) {
      if (this.detourPoly != null) {
        this.detourPoly.setMap(null);
      }
      if (this.traveltimePopup != null) {
        this.traveltimePopup.setMap(null);
      }
      this.detourPoly = new google.maps.Polyline({
        path: payload.coords,
        geodesic: true,
        strokeColor: '#9BE1FF',
        strokeOpacity: 1.0,
        strokeWeight: 7,
        zIndex: 15,
      });
      this.traveltimePopup = this.createPopup(`Estimate travel time: ${payload.travelTime} hours`);
      this.detourPoly.setMap(this.$store.state.bluetooth.map);
      let recenter = payload.coords[Math.round((payload.coords.length - 1) / 2)];

      this.updateETT(payload.travelTime);
      this.updateDestMarker(payload.coords.slice(-1)[0]);
      this.updateSourceMarker(payload.coords.slice(0)[0]);
      this.$store.state.bluetooth.map.panTo(recenter);
      this.$store.state.bluetooth.map.setZoom(12);
    },
    createPopup(contentString) {
      let infowindow = new google.maps.InfoWindow({
        content: contentString,
      });
      return infowindow;
    },
    createMarker(position) {
      let marker = new google.maps.Marker({
        position: position,
        map: this.$store.state.bluetooth.map,
        title: `Estimated travel time: ${this.travelTime} hours`,
      });
      return marker;
    },
    updateETT(traveltime) {
      let ett = {
        hours: traveltime.toFixed(0),
        minutes: ((traveltime * 60) % 60).toFixed(0),
        seconds: ((traveltime * 3600) % 60).toFixed(0),
        str: 'N/A',
      };
      if (ett.hours > 0) {
        ett.str = `ETT: ${ett.hours} hr ${ett.minutes} min`;
      } else if (ett.minutes > 0) {
        ett.str = `ETT: ${ett.minutes} min`;
      } else {
        ett.str = `ETT: ${ett.seconds} sec`;
      }
      this.$bus.$emit('UPDATE_ETT_STR', ett);
    },
    updateSourceMarker(latLng) {
      if (this.sourceMarker != null) {
        this.sourceMarker.setMap(null);
      }
      this.sourceMarker = new google.maps.Marker({
        map: this.$store.state.bluetooth.map,
        icon: this.textbubble,
        label: {
          color: 'blue',
          fontWeight: 'bold',
          fontSize: '13px',
          text: 'Source',
        },
      });
      this.sourceMarker.setPosition(latLng);
    },
    updateDestMarker(latLng) {
      if (this.destMarker != null) {
        this.destMarker.setMap(null);
      }
      this.destMarker = new google.maps.Marker({
        map: this.$store.state.bluetooth.map,
        icon: this.textbubble,
        label: {
          color: 'blue',
          fontWeight: 'bold',
          fontSize: '13px',
          text: 'Destination',
        },
      });
      this.destMarker.setPosition(latLng);
    },
    clearRoutingComponent() {
      if (this.detourPoly != null) {
        this.detourPoly.setMap(null);
      }
      if (this.traveltimePopup != null) {
        this.traveltimePopup.setMap(null);
      }
      if (this.endLatLng != null) {
        this.endLatLng = null;
      }
      if (this.sourceMarker != null) {
        this.sourceMarker.setMap(null);
      }
      if (this.destMarker != null) {
        this.destMarker.setMap(null);
      }
      this.$bus.$emit('UPDATE_ETT_STR', null);
      this.clickIndex = 0;
    },
  },

  computed: {
    mapLayerSelection() {
      return this.$store.state.bluetooth.mapLayerSelection;
    },
  },
};
</script>
<style scoped>
h1 {
  padding-bottom: 20px;
}
</style>

<template>
  <div id="historical-mode">
    <!-- Card -->
    <v-scale-transition>
      <v-card
        style="position: absolute; top: 10px; left: 10px"
        class="pa-2"
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
import axios from 'axios';
import { mapState } from 'vuex';

/* eslint-disable no-undef */

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
      detourPoly: null,
      traveltimePopup: null,
      endLatLng: null,
      sourceMarker: null,
      destMarker: null,
      /* Dash */
      bbox: null,
      bbox1: [39.74, -75.79],
      bbox2: [39.6, -75.525],
      clickIndex: 0
    };
  },

  mounted: function() {
    setTimeout(() => {
      this.addBB();
    }, 1000);
    this.$bus.$on('newSource', sourceCoordinate => {
      this.updateSource(sourceCoordinate);
    });
    this.$bus.$on('newDest', destCoordinate => {
      this.updateDest(destCoordinate);
      this.getShortestPath();
    });
    this.$bus.$on('launchShortestPath', payload => {
      this.plotShortestPath(payload);
    });
    this.$bus.$on('clearRouting', () => {
      this.clearRoutingComponent();
    });
  },

  methods: {
    clearData() {
      this.$bus.$emit('clearRouting');
      this.sourceString = '';
      this.destString = '';
    },
    initMapBB() {
      this.geocoder = new google.maps.Geocoder();
      this.bbox = new google.maps.Rectangle({
        strokeColor: 'orange',
        strokeOpacity: 1,
        strokeWeight: 4,
        fillColor: 'orange',
        fillOpacity: 0,
        zIndex: 99,
        bounds: {
          north: this.bbox2[0],
          south: this.bbox1[0],
          east: this.bbox2[1],
          west: this.bbox1[1]
        }
      });
      google.maps.event.addListener(this.bbox, 'click', event => {
        let Lat = event.latLng.lat();
        let Lng = event.latLng.lng();
        let LatLngStr = `${Lat},${Lng}`;
        if (this.clickIndex == 0) {
          this.clickIndex = 1;
          this.updateSourceMarker(event.latLng);
          this.$bus.$emit('newSource', LatLngStr);
        } else if (this.clickIndex == 1) {
          this.clickIndex = 0;
          this.endLatLng = [Lat + 0.004, Lng];
          this.updateDestMarker(event.latLng);
          this.$bus.$emit('newDest', LatLngStr);
        }
      });
    },
    addBB() {
      this.initMapBB();
      if (this.bbox) {
        let poi = this.bbox.bounds.getCenter().toJSON();
        this.map.setCenter(poi);
        this.map.setZoom(12);
        this.bbox.setMap(this.map);
      }
    },
    removeBB() {
      if (this.bbox) {
        this.bbox.setMap(null);
      }
      this.$bus.$emit('clearRouting');
    },
    updateSource(sourceString) {
      let coordsArr = sourceString.split(',').map(x => parseFloat(x).toFixed(5));
      this.sourceString = `${coordsArr[0]}°, ${coordsArr[1]}°`;
      this.sourceCoordinate = this.parseCoordinateString(this.sourceString);
    },
    updateDest(destString) {
      let coordsArr = destString.split(',').map(x => parseFloat(x).toFixed(5));
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
        id: 'test'
      };
      axios
        .post(this.RoutingUrl, params)
        .then(response => {
          this.coords = response.data.paths;
          this.travelTime = response.data.traveltime;
          this.$bus.$emit('launchShortestPath', { coords: this.coords, travelTime: this.travelTime });
          let notifText = 'Successfully computed shortest path.';
          this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
        })
        .catch(err => {
          let notifText = 'Unable to compute shortest path.';
          this.$store.dispatch('setSystemStatus', { text: notifText, color: 'error', timeout: 2500 });
          console.log(err.message);
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
        zIndex: 15
      });
      this.traveltimePopup = this.createPopup(`Estimate travel time: ${payload.travelTime} hours`);
      this.detourPoly.setMap(this.map);
      let recenter = payload.coords[Math.round((payload.coords.length - 1) / 2)];

      this.updateETT(payload.travelTime);
      this.updateDestMarker(payload.coords.slice(-1)[0]);
      this.updateSourceMarker(payload.coords.slice(0)[0]);
      this.map.panTo(recenter);
      this.map.setZoom(12);
    },
    createPopup(contentString) {
      let infowindow = new google.maps.InfoWindow({
        content: contentString
      });
      return infowindow;
    },
    createMarker(position) {
      let marker = new google.maps.Marker({
        position: position,
        map: this.map,
        title: `Estimated travel time: ${this.travelTime} hours`
      });
      return marker;
    },
    updateETT(traveltime) {
      let ett = {
        hours: traveltime.toFixed(0),
        minutes: ((traveltime * 60) % 60).toFixed(0),
        seconds: ((traveltime * 3600) % 60).toFixed(0),
        str: 'N/A'
      };
      if (ett.hours > 0) {
        ett.str = `ETT: ${ett.hours} hr ${ett.minutes} min`;
      } else if (ett.minutes > 0) {
        ett.str = `ETT: ${ett.minutes} min`;
      } else {
        ett.str = `ETT: ${ett.seconds} sec`;
      }
      this.ett = ett;
      this.clickIndex = 0;
    },
    updateSourceMarker(latLng) {
      if (this.sourceMarker != null) {
        this.sourceMarker.setMap(null);
      }
      this.sourceMarker = new google.maps.Marker({
        map: this.map,
        icon: this.textbubble,
        label: {
          color: 'blue',
          fontWeight: 'bold',
          fontSize: '13px',
          text: 'Source'
        }
      });
      this.sourceMarker.setPosition(latLng);
    },
    updateDestMarker(latLng) {
      if (this.destMarker != null) {
        this.destMarker.setMap(null);
      }
      this.destMarker = new google.maps.Marker({
        map: this.map,
        icon: this.textbubble,
        label: {
          color: 'blue',
          fontWeight: 'bold',
          fontSize: '13px',
          text: 'Destination'
        }
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
      this.ett = null;
      this.clickIndex = 0;
    }
  },

  computed: {
    showTrafficRouting: {
      get() {
        return this.$store.state.bluetooth.showTrafficRouting;
      },
      set(val) {
        this.$store.commit('bluetooth/SET_TRAFFIC_ROUTING', val);
      }
    },
    ...mapState('bluetooth', ['map'])
  }
};
</script>
<style scoped>
h1 {
  padding-bottom: 20px;
}
</style>

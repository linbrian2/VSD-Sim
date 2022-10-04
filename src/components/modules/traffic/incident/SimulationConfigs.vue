<template>
  <v-row justify="center">
    <v-dialog v-model="show" fullscreen hide-overlay transition="dialog-bottom-transition">
      <v-card>
        <v-toolbar dark color="primary" dense flat fixed overflow extension-height="0">
          <v-toolbar-title>
            <v-btn icon class="ml-n2" @click="goBack">
              <v-icon dark>mdi-cog-outline</v-icon>
            </v-btn>
            Simulation Settings
          </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn dark small color="error" @click="startSimulation" class="mr-10">
            Start Simulation
          </v-btn>
          <v-toolbar-items>
            <v-btn icon dark @click="show = false"><v-icon>mdi-close</v-icon></v-btn>
          </v-toolbar-items>
        </v-toolbar>

        <v-container fluid>
          <v-row>
            <v-col cols="6">
              <v-container>
                <GmapMap
                  ref="mapRef"
                  :options="options"
                  :center="position"
                  :zoom="11"
                  map-type-id="roadmap"
                  class="map-select"
                >
                  <GmapMarker
                    :position="incidentLocation"
                    title="Incident Location"
                    :icon="starIcon"
                    v-if="incidentLocation"
                  />
                  <GmapRectangle :bounds="bounds" :options="rectOptions" @click="mapClicked" />
                </GmapMap>
              </v-container>
            </v-col>
            <v-col cols="5">
              <v-row>
                <v-col cols="12">
                  <v-subheader class="pl-0 text-overline green--text"><h3>Incident Location</h3></v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col cols="4">
                      <v-text-field
                        v-model="latitude"
                        name="latitude"
                        label="Latitude"
                        placeholder="Latitude"
                        type="text"
                        required
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="longitude"
                        name="longitude"
                        label="Longitude"
                        placeholder="Longitude"
                        type="text"
                        required
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-select
                        v-model="incidentDirection"
                        :items="directionItems"
                        item-text="text"
                        item-value="value"
                        label="Incident Direction"
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-subheader class="pl-0 text-overline green--text"><h3>Blockage Settings</h3></v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col cols="4">
                      <v-select
                        v-model="blockedLanes"
                        :items="blockedLaneItems"
                        item-text="text"
                        item-value="value"
                        label="Blocked lanes"
                      ></v-select>
                    </v-col>
                    <v-col cols="4">
                      <v-select
                        v-model="blockageDelayTime"
                        :items="delayTimeItems"
                        item-text="text"
                        item-value="value"
                        label="Delay Time"
                      ></v-select>
                    </v-col>
                    <v-col cols="4">
                      <v-select
                        v-model="duration"
                        :items="durationItems"
                        item-text="text"
                        item-value="value"
                        label="Duration"
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-subheader class="pl-0 text-overline green--text"><h3>Detour Settings</h3></v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col cols="4">
                      <v-select
                        v-model="detourFlag"
                        :items="booleanItems"
                        item-text="text"
                        item-value="value"
                        label="Detour"
                      ></v-select>
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="detourRatio"
                        name="detourRatio"
                        label="Detour Ratio"
                        placeholder="Detour Ratio"
                        type="text"
                        required
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-select
                        v-model="detourDuration"
                        :items="durationItems"
                        item-text="text"
                        item-value="value"
                        label="Duration"
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-subheader class="pl-0 text-overline green--text"><h3>Simulation Settings</h3></v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col cols="4">
                      <v-select
                        v-model="simuSpeed"
                        :items="simuSpeedItems"
                        item-text="text"
                        item-value="value"
                        label="Simulation Speed"
                      ></v-select>
                    </v-col>
                    <v-col cols="4">
                      <v-select
                        v-model="simuDuration"
                        :items="simuDurationItems"
                        item-text="text"
                        item-value="value"
                        label="Simulation Duration"
                      ></v-select>
                    </v-col>
                    <v-col cols="4">
                      <v-select
                        v-model="simuStartTime"
                        :items="simuStartTimeItems"
                        item-text="text"
                        item-value="value"
                        label="Start Time"
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import { mapState } from 'vuex';
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
export default {
  props: {
    value: Boolean
  },

  data() {
    return {
      starIcon: {
        url: require('@/assets/star.png'),
        size: { width: 40, height: 40, f: 'px', b: 'px' },
        anchor: { x: 20, y: 20 }
      },
      baseURL: process.env.VUE_APP_SIMU_API_URL,
      loading: false,
      theme: 'Dark',
      latitude: null,
      longitude: null,
      map: null,
      position: { lat: 39.280027, lng: -75.58068 },
      bounds: { north: 39.071394, south: 39.488708, east: -75.401717, west: -75.759644 },
      rectOptions: {
        strokeColor: '#666',
        strokeOpacity: 1,
        strokeWeight: 2,
        fillColor: 'orange',
        fillOpacity: 0
      },

      incidentLocation: null,

      incidentDirection: 'NB',
      directionItems: [
        { text: 'NB', value: 'NB' },
        { text: 'SB', value: 'SB' }
      ],

      blockedLanes: 1,
      blockedLaneItems: [
        { text: '1 lane', value: 1 },
        { text: '2 lanes', value: 2 },
        { text: 'All lines', value: -1 }
      ],

      blockageDelayTime: 300,
      delayTimeItems: [
        { text: 'Immediately', value: 0 },
        { text: '5 minutes', value: 300 },
        { text: '10 minutes', value: 600 },
        { text: '20 minutes', value: 1200 },
        { text: '30 minutes', value: 1800 },
        { text: '45 minutes', value: 2700 },
        { text: '60 minutes', value: 3600 }
      ],

      blockageFlag: true,
      detourFlag: true,
      detourDuration: 1800,
      detourRatio: 0.3,
      duration: 1800,
      durationItems: [
        { text: '20 minutes', value: 1200 },
        { text: '30 minutes', value: 1800 },
        { text: '40 minutes', value: 2400 },
        { text: '50 minutes', value: 3000 },
        { text: '60 minutes', value: 3600 },
        { text: '90 minutes', value: 5400 },
        { text: '120 minutes', value: 7200 }
      ],

      simuSpeed: 1,
      simuSpeedItems: [
        { text: '1x', value: 1 },
        { text: '2x', value: 2 },
        { text: '5x', value: 5 },
        { text: '10x', value: 10 }
      ],

      simuDuration: 3600,
      simuDurationItems: [
        { text: '1 hour', value: 3600 },
        { text: '2 hours', value: 7200 },
        { text: '3 hours', value: 10800 },
        { text: '4 hours', value: 14400 },
        { text: '5 hours', value: 18000 }
      ],

      simuStartTime: 0,
      simuStartTimeItems: [
        { text: 'Immediately', value: 0 },
        { text: '5 minutes later', value: 300 },
        { text: '10 minutes later', value: 600 },
        { text: '20 minutes later', value: 1200 },
        { text: '30 minutes later', value: 1800 },
        { text: '45 minutes later', value: 2700 },
        { text: '60 minutes later', value: 3600 }
      ],

      booleanItems: [
        { text: 'Yes', value: true },
        { text: 'No', value: false }
      ],

      options: {
        mapTypeControl: true,
        mapTypeControlOptions: {
          mapTypeIds: ['roadmap', 'satellite']
        },

        // streetViewControl: false,
        zoomControl: true,
        zoomControlOptions: {
          position: 7
        },

        styles: DarkMapStyle
      }
    };
  },

  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },
    ...mapState(['currentDate'])
  },

  methods: {
    goBack() {
      this.$router.go(-1);
    },

    mapClicked(event) {
      console.log(event);
      this.latitude = event.latLng.lat();
      this.longitude = event.latLng.lng();
      this.incidentLocation = event.latLng;
    },

    startSimulation() {
      this.show = false;
      this.startSimulationTask();
    },

    async startSimulationTask() {
      let startTime = new Date();
      if (this.simuStartTime > 0) {
        startTime = new Date(startTime.getTime() + this.simuStartTime * 1000);
      }
      const endTime = new Date(startTime.getTime() + this.simuDuration * 1000);

      const params = {
        startTime: Utils.formatDateTime(startTime),
        endTime: Utils.formatDateTime(endTime),
        incidentLocation: [this.latitude, this.longitude],
        incidentDirection: this.incidentDirection,
        blockageFlag: this.blockageFlag,
        detourFlag: this.detourFlag,
        detourRatio: this.detourRatio,
        blockageOffset: this.blockageDelayTime,
        duration: this.detourDuration
      };

      try {
        const response = await Api.startSimulation(this.baseURL, params);
        console.log(response);
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    saveData() {}
  }
};
</script>

<style lang="scss" scoped>
.title-name {
  background-color: hsl(200, 18%, 46%);
  padding: 5px 10px;
  margin-top: -8px;
  color: white;
  font-size: normal;
  font-weight: bold;
}

.non-selection {
  -moz-user-select: none;
  -khtml-user-select: none;
  -webkit-user-select: none;
  user-select: none;
}

.map-select {
  width: 100%;
  height: calc(100vh - 88px);
}

.gm-style div {
  -webkit-transform: scale(1.005);
}
</style>

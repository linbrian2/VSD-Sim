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
                  @click="mapClicked"
                >
                  <GmapMarker
                    :position="incidentLocation"
                    title="Incident Location"
                    :icon="starIcon"
                    v-if="incidentLocation"
                  />
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
                  </v-row>
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-subheader class="pl-0 text-overline green--text"><h3>Incident Settings</h3></v-subheader>
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
                        v-model="delayTime"
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
      loading: false,
      theme: 'Dark',
      latitude: null,
      longitude: null,
      map: null,
      position: { lat: 39.306027, lng: -75.606789 },
      incidentLocation: null,
      blockedLanes: 1,
      blockedLaneItems: [
        { text: '1 lane', value: 1 },
        { text: '2 lanes', value: 2 },
        { text: 'All lines', value: -1 }
      ],

      delayTime: 0,
      delayTimeItems: [
        { text: 'Immediately', value: 0 },
        { text: '5 minutes', value: 300 },
        { text: '10 minutes', value: 600 },
        { text: '20 minutes', value: 1200 },
        { text: '30 minutes', value: 1800 },
        { text: '45 minutes', value: 2700 },
        { text: '60 minutes', value: 3600 }
      ],

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

      options: {
        mapTypeControl: true,
        mapTypeControlOptions: {
          mapTypeIds: ['roadmap', 'satellite']
        },

        // streetViewControl: false,
        // zoomControl: true,
        // // zoomControlOptions: {
        // //   position: google.maps.ControlPosition.RIGHT_BOTTOM
        // // },

        // fullscreenControl: true,
        // // fullscreenControlOptions: {
        // //   position: google.maps.ControlPosition.TOP_CENTER
        // // },

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
    }
  },

  methods: {
    goBack() {
      this.$router.go(-1);
    },

    mapClicked(event) {
      this.latitude = event.latLng.lat();
      this.longitude = event.latLng.lng();
      this.incidentLocation = event.latLng;
    },

    startSimulation() {
      this.show = false;
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

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
                  <v-subheader class="pl-0 text-overline green--text"><h3>Location</h3></v-subheader>
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
                    <!-- <v-col cols="4">
                      <v-select
                        v-model="incidentDirection"
                        :items="directionItems"
                        item-text="text"
                        item-value="value"
                        label="Incident Direction"
                      ></v-select>
                    </v-col> -->
                  </v-row>
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-subheader class="pl-0 text-overline green--text"><h3>Vehicles/Network Settings</h3></v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col cols="4">
                      <v-text-field
                        v-model="num_vehicles"
                        name="num_vehicles"
                        label="Number of Target Vehicles"
                        type="number"
                        min="1"
                        required
                        hint="At least 1"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="max_accel"
                        name="max_accel "
                        label="Maximum Acceleration"
                        type="number"
                        min="0"
                        max="3"
                        step="0.01"
                        suffix="m/s^2"
                        required
                        hint="Between 0 and 3"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="max_decel"
                        name="max_decel"
                        label="Maximum Deceleration"
                        type="number"
                        min="0"
                        max="3"
                        step="0.01"
                        suffix="m/s^2"
                        required
                        hint="Between 0 and 3"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="target_velocity"
                        name="target_velocity"
                        label="Desired Velocity"
                        type="number"
                        min="0"
                        max="120"
                        suffix="m/s"
                        required
                        hint="Between 0 and 120"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="max_speed"
                        name="max_speed"
                        label="Max Allowed Velocity"
                        type="number"
                        min="0"
                        max="120"
                        suffix="m/s"
                        required
                        hint="Between 0 and 120"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="max_distance"
                        name="max_distance"
                        label="Max Route Length"
                        type="number"
                        min="0"
                        suffix="m"
                        required
                        hint="Greater than 0"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="h_d"
                        name="h_d"
                        label="Desired Time Headway"
                        type="number"
                        min="1"
                        max="2"
                        suffix="s"
                        required
                        hint="Between 1 and 2"
                      />
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-subheader class="pl-0 text-overline green--text"><h3>RL TRAINING SETTINGS</h3></v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col cols="4">
                      <v-text-field
                        v-model="horizon"
                        name="horizon"
                        label="Number of Horizons"
                        type="number"
                        min="1"
                        required
                        hint="At least 1"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="sim_step"
                        name="sim_step"
                        label="Simulation Step Size"
                        type="number"
                        min="0"
                        required
                        hint="Greater than 0"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="n_rollouts"
                        name="n_rollouts"
                        label="Number of Rollouts"
                        type="number"
                        min="1"
                        required
                        hint="At least 1"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="n_cpus"
                        name="n_cpus"
                        label="Number of CPUs"
                        type="number"
                        min="1"
                        required
                        hint="At least 1"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="checkpoint_freq"
                        name="checkpoint_freq"
                        label="Checkpoint Frequency"
                        type="number"
                        min="1"
                        required
                        hint="At least 1"
                      />
                    </v-col>
                    <v-col cols="4">
                      <v-text-field
                        v-model="training_iteration"
                        name="training_iteration"
                        label="Number of Iterations"
                        type="number"
                        min="1"
                        required
                        hint="At least 1"
                      />
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
      num_vehicles: 1,
      max_accel: 3,
      max_decel: 3,
      target_velocity: 30,
      max_speed: 50,
      max_distance: 21679.33,
      h_d: 1,
      horizon: 1000,
      sim_step: 1,
      n_rollouts: 20,
      n_cpus: 20,
      checkpoint_freq: 20,
      training_iteration: 200,
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
      duration: 3600,
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
      // this.show = false;
      this.startSimulationTask();
    },

    async startSimulationTask() {
      let startTime = new Date();
      if (this.simuStartTime > 0) {
        startTime = new Date(startTime.getTime() + this.simuStartTime * 1000);
      }
      const endTime = new Date(startTime.getTime() + this.simuDuration * 1000);

      const payload = {
        startTime: Utils.formatDateTime(startTime),
        endTime: Utils.formatDateTime(endTime),
        num_vehicles: this.num_vehicles,
        max_accel: this.max_accel,
        max_decel: this.max_decel,
        target_velocity: this.target_velocity,
        max_speed: this.max_speed,
        max_distance: this.max_distance,
        h_d: this.h_d,
        horizon: this.horizon,
        sim_step: this.sim_step,
        n_rollouts: this.n_rollouts,
        n_cpus: this.n_cpus,
        checkpoint_freq: this.checkpoint_freq,
        training_iteration: this.training_iteration
      };
      console.log('Payload: %o', payload);

      try {
        // const response = await Api.startSimulation(this.baseURL, params);
        const response = 'Response';
        console.log('Response: %o', response);
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async startSimulationTaskOld() {
      let startTime = new Date();
      if (this.simuStartTime > 0) {
        startTime = new Date(startTime.getTime() + this.simuStartTime * 1000);
      }
      const endTime = new Date(startTime.getTime() + this.simuDuration * 1000);

      const params = {
        startTime: Utils.formatDateTime(startTime),
        endTime: Utils.formatDateTime(endTime),
        incidentLocation: [parseFloat(this.latitude), parseFloat(this.longitude)],
        incidentDirection: this.incidentDirection,
        blockageFlag: this.blockageFlag,
        detourFlag: this.detourFlag,
        detourRatio: parseFloat(this.detourRatio),
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

<template>
  <v-card>
    <v-toolbar dark color="primary" dense flat fixed overflow extension-height="0">
      <v-toolbar-title>
        <v-btn icon class="ml-n2">
          <v-icon dark>mdi-cog-outline</v-icon>
        </v-btn>
        Simulation Settings
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-icon :disabled="page == 1" @click="page = 1">mdi-arrow-left</v-icon>
      <v-icon :disabled="page == 2 || !apiData" @click="page = 2" class="ml-2 mr-5">
        mdi-arrow-right
      </v-icon>
      <v-btn
        dark
        small
        color="error"
        @click="startSimulationTask"
        class="mr-4"
        :loading="loading"
        :disabled="page == 2 || !serviceUp"
      >
        Start Simulation
      </v-btn>
      <div v-if="serviceUp">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon v-on="on" color="#006600">mdi-check-network-outline</v-icon>
          </template>
          <span>Service is available</span>
        </v-tooltip>
      </div>
      <div v-else>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon v-on="on" color="red">mdi-close-network-outline</v-icon>
          </template>
          <span>Service is unavailable</span>
        </v-tooltip>
      </div>
    </v-toolbar>

    <v-row>
      <template v-if="page == 1">
        <v-col cols="6" class="pr-0 py-0">
          <v-container class="pa-0">
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
        <v-col cols="6" class="py-0">
          <v-card class="map-select pl-2 pr-5" style="overflow-y: auto;">
            <v-col>
              <v-row>
                <v-col cols="12">
                  <v-subheader class="pl-0 text-overline green--text">
                    <h3>Location</h3>
                  </v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col lg="3" md="4" sm="6">
                      <v-text-field
                        v-model="latitude"
                        name="latitude"
                        label="Latitude"
                        placeholder="Latitude"
                        type="text"
                        required
                      />
                    </v-col>
                    <v-col lg="3" md="4" sm="6">
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
                  <v-subheader class="pl-0 text-overline green--text">
                    <h3>Vehicles/Network Settings</h3>
                  </v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col lg="3" md="4" sm="6">
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
                    <v-col lg="3" md="4" sm="6">
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
                    <v-col lg="3" md="4" sm="6">
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
                    <v-col lg="3" md="4" sm="6">
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
                    <v-col lg="3" md="4" sm="6">
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
                    <v-col lg="3" md="4" sm="6">
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
                    <v-col lg="3" md="4" sm="6">
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
                  <v-subheader class="pl-0 text-overline green--text">
                    <h3>RL TRAINING SETTINGS</h3>
                  </v-subheader>
                  <v-divider />
                </v-col>
                <v-col cols="12">
                  <v-row>
                    <v-col lg="3" md="4" sm="6">
                      <v-select
                        :items="[10, 50, 100, 200, 250]"
                        v-model="training_iteration"
                        label="Number of Iterations"
                      ></v-select>
                    </v-col>
                    <v-col lg="3" md="4" sm="6">
                      <v-text-field
                        v-model="checkpoint_freq"
                        name="checkpoint_freq"
                        label="Checkpoint Frequency"
                        type="number"
                        required
                        outlined
                        disabled
                      />
                    </v-col>
                    <v-col lg="3" md="4" sm="6">
                      <v-text-field
                        v-model="horizon"
                        name="horizon"
                        label="Number of Timesteps"
                        type="number"
                        min="1"
                        required
                        hint="At least 1"
                      />
                    </v-col>
                    <v-col lg="3" md="4" sm="6">
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
                    <v-col lg="3" md="4" sm="6">
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
                  </v-row>
                </v-col>
              </v-row>
            </v-col>
          </v-card>
        </v-col>
      </template>
      <v-col cols="12" v-else-if="page == 2 && apiData">
        <EmissionAndProgressData :apiData="apiData" :payload="payload" :pathData="pathData" />
      </v-col>
    </v-row>
  </v-card>
</template>

<script>
import { mapState } from 'vuex';
import Api from '@/utils/api/traffic';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import EmissionAndProgressData from '@/components/modules/traffic/incident/EmissionAndProgressData.vue';

export default {
  components: {
    EmissionAndProgressData
  },
  props: {
    value: Boolean
  },

  data() {
    return {
      timer: null,
      serviceUp: true,
      payload: null,
      loading: false,
      page: 1,
      apiData: null,
      num_vehicles: 1,
      max_accel: 3,
      max_decel: 3,
      target_velocity: 30,
      max_speed: 50,
      max_distance: 21679.33,
      h_d: 1,
      horizon: 1000,
      sim_step: 1,
      n_cpus: 20,
      checkpoint_freq: 20,
      training_iteration: 200,
      starIcon: {
        url: require('@/assets/star.png'),
        size: { width: 40, height: 40, f: 'px', b: 'px' },
        anchor: { x: 20, y: 20 }
      },
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
      options: {
        mapTypeControl: true,
        mapTypeControlOptions: {
          mapTypeIds: ['roadmap', 'satellite']
        },
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

  watch: {
    training_iteration(data) {
      switch (data) {
        case 10:
          this.checkpoint_freq = 1;
          break;
        case 50:
          this.checkpoint_freq = 5;
          break;
        case 100:
          this.checkpoint_freq = 10;
          break;
        case 200:
          this.checkpoint_freq = 20;
          break;
        case 250:
          this.checkpoint_freq = 25;
          break;
        default:
          break;
      }
    }
  },

  mounted() {
    this.checkIfUp();
    this.startTimer();
  },

  destroyed() {
    this.stopTimer();
  },

  methods: {
    startTimer() {
      this.timer = setInterval(() => {
        this.checkIfUp();
      }, 60 * 1000);
    },
    stopTimer() {
      clearInterval(this.timer);
    },
    mapClicked(event) {
      this.latitude = event.latLng.lat();
      this.longitude = event.latLng.lng();
      this.incidentLocation = event.latLng;
    },

    async checkIfUp() {
      // try {
      //   this.serviceUp = await Api.checkIfUp(this.payload);
      // } catch (error) {
      //   this.serviceUp = !error.message.includes('Network Error');
      // }
    },

    async startSimulationTask() {
      this.payload = {
        num_vehicles: this.num_vehicles,
        max_accel: this.max_accel,
        max_decel: this.max_decel,
        target_velocity: this.target_velocity,
        max_speed: this.max_speed,
        max_distance: this.max_distance,
        h_d: this.h_d,
        horizon: this.horizon,
        sim_step: this.sim_step,
        n_cpus: this.n_cpus,
        checkpoint_freq: this.checkpoint_freq,
        training_iteration: this.training_iteration
      };

      this.loading = true;

      try {
        /* No Simulation made */
        // this.pathData = {
        //   path:
        //     '/home/vms_public/ray_results/template_ZM/PPO_MultiAgentHighwayPOEnv-v0_cf44823a_2023-05-10_16-11-1268rq39i9'
        // };

        /* New Fetch */
        // this.addPathToBackend(this.pathData);

        /* Old Fetch */
        this.fetchVMSData(this.pathData, true);
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async addPathToBackend(pathData) {
      console.log('addPathToBackend: %o', pathData);
      try {
        const response = await Api.addPathToBackend(pathData);
        console.log(response);
        this.apiData = response.data.data;
        console.log('apiData: %o', this.apiData);
        this.loading = false;
        this.page = 2;
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
        this.loading = false;
      }
    },

    async fetchVMSData(pathData, useSampleData = false) {
      console.log('fetchData: %o', { pathData, useSampleData });
      try {
        const response = await Api.fetchVMSData({ pathData, useSampleData });
        this.apiData = response.data.data;
        console.log('apiData: %o', this.apiData);
        this.loading = false;
        this.page = 2;
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
        this.loading = false;
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
  height: calc(100vh - 96px);
}

.gm-style div {
  -webkit-transform: scale(1.005);
}
</style>

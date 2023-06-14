<template>
  <v-card>
    <v-toolbar dark color="#3A3A5A" dense flat fixed overflow extension-height="0">
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
            <v-icon v-on="on" color="green">mdi-check-network-outline</v-icon>
          </template>
          <span>Simulation initialization service is available as of {{ formatDate(serviceUp.date) }}</span>
        </v-tooltip>
      </div>
      <div v-else>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon v-on="on" color="red">mdi-close-network-outline</v-icon>
          </template>
          <span>Simulation initialization service is unavailable</span>
        </v-tooltip>
      </div>
      <v-menu bottom right offset-y>
        <template v-slot:activator="{ on, attrs }">
          <v-btn class="ml-1" :disabled="page != 1" icon v-bind="attrs" v-on="on">
            <v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
        </template>

        <v-list>
          <template v-for="(item, index) in items">
            <v-divider v-if="item.divider" :key="index"></v-divider>
            <v-list-item v-else :key="`e-${index}`" @click="menuItemClicked(item.action)">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </template>
        </v-list>
      </v-menu>
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
import Utils from '@/utils/Utils';
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
      samplePath:
        '/home/blin/Simulation App/samples/PPO_MultiAgentHighwayPOEnv-v0_b815bb28_2023-05-19_16-14-11meehmeiu', // Linux
      // samplePath: '../samples/PPO_MultiAgentHighwayPOEnv-v0_b815bb28_2023-05-19_16-14-11meehmeiu', // Windows
      items: [
        { action: 1, title: 'Start Sample Simulation' },
        { action: 2, title: 'Load Sample Simulation Results' }
      ],
      fileTimer: null,
      serviceUp: null,
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
      checkpoint_freq: 20, // Default is 20
      training_iteration: 200, // Default is 200
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
    apiData: {
      deep: true,
      handler: function() {
        if (!this.apiData
            || (this.apiData.checkpoint && this.apiData.checkpoint >= 10)
            || this.apiData.errorGenerated) {
          this.stopFileChecker();
        }
      }
    },
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
    setInterval(() => {
      this.checkIfUp();
    }, 60000);
  },

  destroyed() {
    this.stopFileChecker;
  },

  methods: {
    menuItemClicked(action) {
      switch (action) {
        case 1:
          this.useSampleData(1);
          break;
        case 2:
          this.useSampleData(2);
          break;
      }
    },
    formatDate(date) {
      return Utils.formatAMPMTime(date);
    },
    mapClicked(event) {
      this.latitude = event.latLng.lat();
      this.longitude = event.latLng.lng();
      this.incidentLocation = event.latLng;
    },

    async checkIfUp() {
      try {
        let resp = await Api.checkIfUp();
        this.serviceUp = { ...resp.data, date: new Date() };
      } catch (error) {
        this.serviceUp = null;
      }
    },

    async useSampleData(mode) {
      this.payload = {
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
        training_iteration: 200
      };

      this.loading = true;

      try {
        this.apiData = null;
        this.pathData = {
          path: this.samplePath
        };
        if (mode == 1) {
          // Incrementing Data
          this.addPathToBackend(this.pathData);
        } else {
          // Instant Data
          const instantData = true;
          const emulateSim = false;
          this.fetchVMSData(this.pathData, instantData, emulateSim);
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
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
        /* Run Simulation, return files path */
        const response = await Api.startSimulationNew(this.payload);
        this.pathData = response.data;

        /* New Fetch, start file checker */
        this.addPathToBackend(this.pathData);
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async addPathToBackend(pathData) {
      // console.log('addPathToBackend: %o', pathData);
      try {
        const response = await Api.addPathToBackend(pathData);
        // console.log(response);
        this.apiData = response.data.data;
        // console.log('apiData: %o', this.apiData);
        this.loading = false;
        this.page = 2;
        this.startFileChecker(pathData);
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
        this.loading = false;
      }
    },

    async fetchVMSData(pathData, instantData, emulateSim) {
      // console.log({ pathData, instantData, emulateSim });
      if (!instantData) {
        try {
          const response = await Api.fetchVMSData({
            ...pathData,
            getFile: 'progress',
            itSize: this.checkpoint_freq,
            emulateSim
          });
          this.apiData = { ...this.apiData, ...response.data.data, proUpdatedTime: new Date() };
          // console.log(response.data.data);
          console.log('apiDataP: %o', this.apiData);
          this.loading = false;
          this.page = 2;
        } catch (error) {
          this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
          this.loading = false;
        }
        try {
          const response = await Api.fetchVMSData({
            ...pathData,
            getFile: 'emission',
            itSize: this.checkpoint_freq,
            emulateSim
          });
          // console.log(response.data.data);
          this.apiData = { ...this.apiData, ...response.data.data, emmUpdatedTime: new Date() };
          console.log('apiDataE: %o', this.apiData);
          this.loading = false;
          this.page = 2;
        } catch (error) {
          this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
          this.loading = false;
        }
      } else {
        try {
          const response = await Api.fetchVMSData({
            ...pathData,
            getFile: 'both',
            itSize: this.checkpoint_freq,
            emulateSim
          });
          this.apiData = response.data.data;
          this.apiData = { ...this.apiData, proUpdatedTime: new Date(), emmUpdatedTime: new Date() };
          console.log('apiData: %o', this.apiData);
          this.loading = false;
          this.page = 2;
        } catch (error) {
          this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
          this.loading = false;
        }
      }
    },

    startFileChecker(pathData) {
      this.fetchVMSDataInit()
      this.fileTimer = setInterval(() => {
        this.fetchVMSDataInit(pathData);
      }, 15000);
    },

    fetchVMSDataInit(pathData) {
      let instantData = false;
      let emulateSim = false;
      if (this.pathData.path == this.samplePath) {
        emulateSim = true;
      }
      this.fetchVMSData(pathData, instantData, emulateSim);
    },

    stopFileChecker() {
      clearInterval(this.fileTimer);
    }
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

<template>
  <v-col>
    <v-row>
      <!-- 
        Add Progress Bar
        Params = hover
        We want to update progress values as soon as updated
        Progress is iteration by iteration, emission is every checkpoint (20)
        Every 20, replot the 5 plots
       -->
      <v-col cols="6">
        <v-card color="#333333" class="px-10 pt-4 pb-6 ml-10">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
            <v-row>
              <v-col class="py-0" cols="6">
                <h3>Simulation Progress</h3>
              </v-col>
              <v-col class="py-0 px-0 grid-right" cols="6">
                <h3>{{ path }}</h3>
              </v-col>
            </v-row>
            <div class="grid-right"></div>
          </v-subheader>
          <v-divider class="mb-3" />
          <v-row class="pl-4">
            <v-col cols="4" class="py-0">
              Estimated Time: <b style="color: rgb(61, 201, 40)">{{ estimatedTime }}</b>
            </v-col>
            <v-col cols="4" class="py-0">
              Elapsed Time: <b style="color: rgb(61, 201, 40)">{{ fancyTimeFormat(elapsedTime) }}</b>
            </v-col>
            <v-col cols="4" class="py-0"> Simulation: <b style="color: rgb(61, 201, 40)"></b> </v-col>
            <v-col cols="4" class="py-0">
              Iteration Count:
              <b style="color: rgb(61, 201, 40)">{{ iterationCount }} / {{ payload.training_iteration }}</b>
            </v-col>
            <v-col cols="4" class="py-0">
              Checkpoint: <b style="color: rgb(61, 201, 40)">{{ checkpoint }}</b>
            </v-col>
            <v-col cols="4" class="py-0">
              Status: <b style="color: rgb(61, 201, 40)">{{ status }}</b>
            </v-col>
          </v-row>
        </v-card>
        <v-card color="#333333" class="px-10 mt-4 pt-4 pb-6 ml-10">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
            <h3>Vehicles/Network</h3>
          </v-subheader>
          <v-divider class="mb-3" />
          <v-row class="pl-4">
            <v-col cols="4" class="py-0">
              Number of Target Vehicles: <b style="color: rgb(61, 201, 40)">{{ payload.num_vehicles }}</b>
            </v-col>
            <v-col cols="4" class="py-0">
              Maximum Acceleration: <b style="color: rgb(61, 201, 40)"> {{ payload.max_accel }} m/s^2 </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Maximum Deceleration: <b style="color: rgb(61, 201, 40)"> {{ payload.max_decel }} m/s^2 </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Desired Velocity: <b style="color: rgb(61, 201, 40)"> {{ payload.target_velocity }} m/s </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Max Allowed Velocity: <b style="color: rgb(61, 201, 40)"> {{ payload.max_speed }} m/s </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Max Route Length: <b style="color: rgb(61, 201, 40)"> {{ payload.max_distance }} m </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Desired Time Headway: <b style="color: rgb(61, 201, 40)"> {{ payload.h_d }} s </b>
            </v-col>
          </v-row>

          <v-subheader class="mt-4 pl-0 mx-4 font-weight-bold text-overline blue--text">
            <h3>RL Training Settings</h3>
          </v-subheader>
          <v-divider class="mb-3" />
          <v-row class="pl-4">
            <v-col cols="4" class="py-0">
              Number of Timesteps: <b style="color: rgb(61, 201, 40)"> {{ payload.horizon }} </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Simulation Step Size: <b style="color: rgb(61, 201, 40)"> {{ payload.sim_step }} </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Number of CPUs: <b style="color: rgb(61, 201, 40)"> {{ payload.n_cpus }} </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Checkpoint Frequency: <b style="color: rgb(61, 201, 40)"> {{ payload.checkpoint_freq }} </b>
            </v-col>
            <v-col cols="4" class="py-0">
              Number of Iterations: <b style="color: rgb(61, 201, 40)"> {{ payload.training_iteration }} </b>
            </v-col>
          </v-row>

          <v-subheader class="mt-4 pl-0 mx-4 font-weight-bold text-overline blue--text">
            <h3>Simulation Results Summary</h3>
          </v-subheader>
          <v-divider class="mb-3" />
          <v-row class="pl-4">
            <v-col cols="4" class="pa-0">
              Emission Entries Count: <b style="color: rgb(61, 201, 40)"> {{ apiData.emission.length }} </b>
            </v-col>
            <v-col cols="4" class="pa-0">
              Progress Entries Count: <b style="color: rgb(61, 201, 40)"> {{ apiData.progress.length }} </b>
            </v-col>
          </v-row>
        </v-card>
        <v-card color="#333333" class="px-10 mt-3 py-4 ml-10">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
            <h3>Simulation Results</h3>
          </v-subheader>
          <v-divider class="mb-3" />
          <v-col>
            <v-row>
              <v-col cols="4" class="pt-0">
                <v-select :items="idList" v-model="selectedId" label="Id">
                  <template v-slot:prepend>
                    <v-btn @click="resetAll()" icon x-small>
                      <v-icon>mdi-close</v-icon>
                    </v-btn>
                  </template>
                </v-select>
              </v-col>
              <!-- <v-col cols="4" class="pt-0">
                <v-btn-toggle dense v-model="emissionMode" class="mt-4" color="green">
                  <v-btn>
                    Leader
                  </v-btn>
                  <v-btn>
                    Follower
                  </v-btn>
                </v-btn-toggle>
              </v-col> -->
              <!-- <v-col cols="4" class="pt-0" v-if="emissionMode == 0">
                <v-select :items="leaderList" v-model="selectedLeader" label="Leader Id" :disabled="!selectedId">
                  <template v-slot:prepend>
                    <v-btn @click="selectedLeader = null" icon x-small>
                      <v-icon>mdi-close</v-icon>
                    </v-btn>
                  </template>
                </v-select>
              </v-col>
              <v-col cols="4" class="pt-0" v-else-if="emissionMode == 1">
                <v-select :items="followerList" v-model="selectedFollower" label="Follower Id" :disabled="!selectedId">
                  <template v-slot:prepend>
                    <v-btn @click="selectedFollower = null" icon x-small>
                      <v-icon>mdi-close</v-icon>
                    </v-btn>
                  </template>
                </v-select>
              </v-col> -->
            </v-row>
            <v-row> </v-row>
            <v-row> </v-row>
          </v-col>
        </v-card>
      </v-col>
      <v-col cols="6">
        <v-card v-if="progressData" color="#343434">
          <!-- <v-select
            class="px-10 py-4"
            hide-details
            dense
            outlined
            v-model="selectedProgressVar"
            label="Select"
            :items="progressValList"
          ></v-select>
          <v-divider /> -->
          <BasicChart ref="progressChart" :data="progressData" :height="height" :xType="'linear'" />
        </v-card>
        <v-card v-if="emissionsData" class="mt-3" color="#343434">
          <BasicChart ref="emissionChart" :data="emissionsData" :height="height" :xType="'linear'" />
        </v-card>
      </v-col>
    </v-row>
  </v-col>
</template>

<script>
import BasicChart from '@/components/modules/traffic/common/BasicChart';

export default {
  components: {
    BasicChart
  },
  props: {
    payload: Object,
    apiData: Object,
    pathData: Object
  },
  data() {
    return {
      estimatedTime: 'XX:XX',
      elapsedTime: 0,
      iterationCount: 0,
      checkpoint: '0 (0)',
      status: 'Ongoing',
      leader: true,
      follower: false,
      height: 400,
      selectedId: null,
      selectedLeader: null,
      selectedFollower: null,
      value: false,
      emissionMode: 0,
      timer: null,
      selectedProgressVar: null
    };
  },
  computed: {
    progressValList() {
      if (this.apiData && this.apiData.progress) {
        return Object.keys(this.apiData.progress[0]);
      } else {
        return [];
      }
    },
    path() {
      if (this.pathData) {
        let splits = this.pathData.path.split('_');
        let len = splits.length;
        return `${splits[len - 2]}_${splits[len - 1]}`;
      } else {
        return '-';
      }
    },
    idList() {
      if (this.apiData) {
        return this.apiData.emission.filter(x => x.id).map(x => x.id);
      } else {
        return [];
      }
    },
    leaderList() {
      if (this.apiData && this.selectedId) {
        return this.apiData.emission.filter(x => x.id && x.leaderId && x.id == this.selectedId).map(x => x.leaderId);
      } else {
        return [];
      }
    },
    followerList() {
      if (this.apiData) {
        return this.apiData.emission
          .filter(x => x.id && x.followerId && x.id == this.selectedId)
          .map(x => x.followerId);
      } else {
        return [];
      }
    },
    speedData() {
      let title = 'Speed';
      let xAxis = 'Time';
      let yAxis = 'Speed (mph)';
      let data = [];
      data.push({ name: 'Speed', color: '#058DC7', data: this.cardData.speedData });
      return { data, xAxis, yAxis, title };
    },
    progressData() {
      let title = 'Episode Reward Mean';
      let xAxis = 'Iteration';
      let yAxis = 'Episode Reward Mean';
      let data = [];
      data.push({
        name: 'Episode Reward Mean',
        color: '#058DC7',
        data: this.apiData.progress.map(x => [x.trainingIteration, x.episodeRewardMean])
      });
      return { data, xAxis, yAxis, title };
    },
    emissionsData() {
      let xAxis = 'Iteration';
      let yAxis = 'Count';
      let data = [];
      let graphData = this.apiData.emission;
      if (this.selectedId) {
        graphData = graphData.filter(x => x.id == this.selectedId);
      }
      /* if (this.selectedLeader && this.emissionMode == 0) {
        graphData = graphData.filter(x => x.leaderId == this.selectedLeader);
      } else if (this.selectedFollower && this.emissionMode == 1) {
        graphData = graphData.filter(x => x.followerId == this.selectedFollower);
      } */
      graphData = graphData.map(x => [x.x, x.y]);
      data.push({
        name: `Simulation Data`,
        color: 'red',
        data: graphData
      });
      let title = `Simulation Data (${graphData.length} Entries)`;
      return { data, xAxis, yAxis, title };
    }
  },
  mounted() {
    this.initEmissionParams();
  },
  beforeDestroy() {
    this.stopTimer();
  },
  methods: {
    fancyTimeFormat(duration) {
      // Hours, minutes and seconds
      const hrs = ~~(duration / 3600);
      const mins = ~~((duration % 3600) / 60);
      const secs = ~~duration % 60;

      // Output like "1:01" or "4:03:59" or "123:03:59"
      let ret = '';

      if (hrs > 0) {
        ret += '' + hrs + ':' + (mins < 10 ? '0' : '');
      }

      ret += '' + mins + ':' + (secs < 10 ? '0' : '');
      ret += '' + secs;

      return ret;
    },
    startTimer() {
      this.timer = setInterval(() => {
        this.elapsedTime++;
      }, 1000);
    },
    stopTimer() {
      clearInterval(this.timer);
    },
    resetAll() {
      this.selectedId = null;
      this.selectedLeader = null;
      this.selectedFollower = null;
    },
    initEmissionParams() {
      if (this.apiData) {
        this.selectedId = this.idList.length > 0 ? this.idList[0] : null;
        this.selectedLeader = this.leaderList.length > 0 ? this.leaderList[0] : null;
        this.selectedFollower = this.followerList.length > 0 ? this.followerList[0] : null;
        if (this.timer == null) {
          this.elapsedTime = 0;
          this.startTimer();
        }
      }
    }
  },
  watch: {
    progressValList(data) {
      if (this.data) {
        this.selectedProgressVar = data[0];
      }
    },
    apiData() {
      this.initEmissionParams();
    }
  }
};
</script>

<style></style>

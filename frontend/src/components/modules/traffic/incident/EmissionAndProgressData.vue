<template>
  <v-container fluid :style="{ 'max-width': '85%' }">
    <v-card class="overview mt-1" color="rgb(37, 37, 37)">
      <v-progress-linear :value="progressValue" color="primary" height="6"></v-progress-linear>
      <div class="px-10 mt-1 pb-4">
        <v-subheader class="pl-0 ml-4 font-weight-bold text-overline blue--text">
          <v-row>
            <v-col class="py-0" cols="6">
              <h3>Overview</h3>
            </v-col>
            <v-col class="py-0 px-0 grid-right" cols="6">
              <h3>
                {{ path }}
                <v-tooltip bottom>
                  <template v-slot:activator="{ on }">
                    <v-icon v-on="on">mdi-information-outline</v-icon>
                  </template>
                  <v-card class="px-10 pt-2 pb-7 my-2" max-width="800">
                    <v-card-title>Input Parameters</v-card-title>
                    <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
                      <h3>Vehicles/Network</h3>
                    </v-subheader>
                    <v-divider class="mb-3" />
                    <v-row>
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
                    <v-row>
                      <v-col cols="4" class="py-0">
                        Number of Iterations:
                        <b style="color: rgb(61, 201, 40)"> {{ payload.training_iteration }} </b>
                      </v-col>
                      <v-col cols="4" class="py-0">
                        Checkpoint Frequency: <b style="color: rgb(61, 201, 40)"> {{ payload.checkpoint_freq }} </b>
                      </v-col>
                      <v-col cols="4" class="py-0">
                        Number of Timesteps: <b style="color: rgb(61, 201, 40)"> {{ payload.horizon }} </b>
                      </v-col>
                      <v-col cols="4" class="py-0">
                        Simulation Step Size: <b style="color: rgb(61, 201, 40)"> {{ payload.sim_step }} </b>
                      </v-col>
                      <v-col cols="4" class="py-0">
                        Number of CPUs: <b style="color: rgb(61, 201, 40)"> {{ payload.n_cpus }} </b>
                      </v-col>
                    </v-row>

                    <v-subheader class="mt-4 pl-0 mx-4 font-weight-bold text-overline blue--text">
                      <h3>Simulation Results Summary</h3>
                    </v-subheader>
                    <v-divider class="mb-3" />
                    <v-row class="pl-4">
                      <v-col cols="4" class="pa-0">
                        Emission Entries Count:
                        <b style="color: rgb(61, 201, 40)"> {{ apiData.emission.length }} </b>
                      </v-col>
                      <v-col cols="4" class="pa-0">
                        Progress Entries Count:
                        <b style="color: rgb(61, 201, 40)"> {{ apiData.progress.length }} </b>
                      </v-col>
                    </v-row>
                  </v-card>
                </v-tooltip>
              </h3>
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
          <v-col cols="4" class="py-0">
            Start Time: <b style="color: rgb(61, 201, 40)">{{ startTime }}</b>
          </v-col>
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
      </div>
    </v-card>

    <v-card class="sim-progress mt-6" v-if="apiData.progress" color="rgb(37, 37, 37)">
      <v-card-title :class="panelStyle">
        <v-icon class="mr-2">mdi-information-outline</v-icon>
        <span class="title font-weight-light">Simulation Progress</span>
        <v-btn x-small icon class="refresh-btn" @click.stop="fetchData()" :loading="loading">
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
      </v-card-title>
      <v-row>
        <v-col cols="6">
          <v-card class="ml-3">
            <BasicChart
              ref="episodeRewardMeanChart"
              :data="episodeRewardMeanData()"
              :height="height"
              :xType="'linear'"
            />
          </v-card>
        </v-col>
        <v-col cols="6" style="color:rgb(170, 136, 184)">
          <v-card class="mr-3">
            <BasicChart ref="totalSimTimeChart" :data="totalSimTimeData()" :height="height" :xType="'linear'" />
          </v-card>
        </v-col>
      </v-row>
    </v-card>

    <v-card class="vehicle-select mt-6" v-if="apiData.emission" color="rgb(37, 37, 37)">
      <v-card-title :class="panelStyle">
        <v-icon class="mr-2">mdi-information-outline</v-icon>
        <span class="title font-weight-light">Choose a Vehicle</span>
      </v-card-title>
      <v-row class="pt-5 pb-2">
        <v-col offset-lg="2" lg="4" class="pt-0">
          <v-select dense hide-details outlined :items="idList" v-model="selectedId" label="Id">
            <template v-slot:prepend>
              <v-btn @click="resetAll()" icon x-small>
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </template>
          </v-select>
        </v-col>
        <v-col lg="4" class="pt-1" v-if="apiData.emission && selectedId">
          <v-chip color="blue">
            <h3>{{ apiData.emission.filter(x => x.id == selectedId).length }} Emission Entries</h3>
          </v-chip>
        </v-col>
      </v-row>
    </v-card>

    <v-card class="speed-evo mt-6" v-if="apiData.emission" color="rgb(37, 37, 37)">
      <v-card-title :class="panelStyle">
        <v-icon class="mr-2">mdi-information-outline</v-icon>
        <span class="title font-weight-light">
          Evolution of Speed for <b style="color: lightgreen">{{ selectedId }}</b>
        </span>
      </v-card-title>
      <v-row class="px-10">
        <v-col class="pa-0" v-for="i in 10" :offset-lg="i % 5 == 1 ? 1 : 0" lg="2" :key="i.id">
          <v-card>
            <SmallChart :data="speedData(i, true)" :height="200" :xType="'linear'" />
          </v-card>
        </v-col>
      </v-row>
    </v-card>

    <v-card class="speed-evo mt-6" v-if="apiData.emission" color="rgb(37, 37, 37)">
      <v-card-title :class="panelStyle">
        <v-icon class="mr-2">mdi-information-outline</v-icon>
        <span class="title font-weight-light">
          Evolution of Speed for <b style="color: lightgreen">{{ selectedId }}</b>
        </span>
      </v-card-title>
      <v-row class="px-10">
        <v-col class="pa-0" v-for="i in 10" :offset-lg="i % 5 == 1 ? 1 : 0" lg="2" :key="i.id">
          <v-card>
            <SmallChart :data="accelData(i, true)" :height="200" :xType="'linear'" />
          </v-card>
        </v-col>
      </v-row>
    </v-card>

    <v-card class="emission mt-6" v-if="apiData.emission" color="rgb(37, 37, 37)">
      <v-card-title :class="panelStyle">
        <v-icon class="mr-2">mdi-information-outline</v-icon>
        <span class="title font-weight-light">
          Other Variables at the Last Iteration for <b style="color: lightgreen">{{ selectedId }}</b>
        </span>
      </v-card-title>
      <v-row class="px-4">
        <v-col cols="6" class="py-1 px-1">
          <BasicChart ref="trajectoryChart" :data="trajectoryData()" :height="height" :xType="'linear'" />
        </v-col>
        <v-col cols="6" class="py-1 px-1">
          <BasicChart ref="distanceChart" :data="emissionData('distance')" :height="height" :xType="'linear'" />
        </v-col>
        <v-col cols="6" class="py-1 px-1">
          <BasicChart ref="headwayChart" :data="emissionData('headway')" :height="height" :xType="'linear'" />
        </v-col>
        <v-col cols="6" class="py-1 px-1">
          <BasicChart ref="speedChart" :data="speedData()" :height="height" :xType="'linear'" />
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
import SmallChart from '@/components/modules/traffic/incident/SmallChart';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import Utils from '@/utils/Utils';

export default {
  components: {
    SmallChart,
    BasicChart
  },
  props: {
    payload: Object,
    apiData: Object,
    pathData: Object
  },
  data() {
    return {
      startTime: null,
      loading: false,
      progressValue: 64,
      estimatedTime: '40:00',
      elapsedTime: 0,
      iterationCount: 128,
      checkpoint: '6 (120)',
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
    panelStyle() {
      return 'pa-1 pl-3 grey darken-3';
    },
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
    }
  },
  mounted() {
    this.initEmissionParams();
  },
  beforeDestroy() {
    this.stopTimer();
  },
  methods: {
    fetchData() {
      console.log('Fetch');
    },
    episodeRewardMeanData() {
      let title = 'Episode Reward Mean';
      let xAxis = 'Iteration';
      let yAxis = 'Episode Reward Mean';
      let data = [];
      data.push({
        name: 'Episode Reward Mean',
        color: '#058DC7',
        data: this.apiData.progress
          .filter(x => x.episodeRewardMean != 0)
          .map(x => [x.trainingIteration, Math.round(x.episodeRewardMean * 100) / 100])
      });
      return { data, xAxis, yAxis, title };
    },
    totalSimTimeData() {
      let title = 'Total Simulation Time';
      let xAxis = 'Iteration';
      let yAxis = 'Total Simulation Time (minutes)';
      let data = [];
      data.push({
        name: 'Total Simulation Time',
        color: 'rgb(170, 136, 184)',
        data: this.apiData.progress.map(x => [x.trainingIteration, Math.round((x.timeTotalS / 60) * 100) / 100])
      });
      return { data, xAxis, yAxis, title };
    },
    trajectoryData() {
      let xAxis = 'X (m)';
      let yAxis = 'Y (m)';
      let data = [];
      let graphData = this.apiData.emission;
      if (this.selectedId) {
        graphData = graphData.filter(x => x.id == this.selectedId);
      }
      graphData = graphData.map(x => [Math.round(x.x * 100) / 100, Math.round(x.y * 100) / 100]);
      data.push({
        name: `Trajectory`,
        color: 'red',
        data: graphData
      });
      let title = `Trajectory`;
      return { data, xAxis, yAxis, title };
    },
    speedData(i = null, evo = false) {
      let xAxis = 'Iteration';
      let yAxis = 'Speed (m/s)';
      let data = [];
      let graphData = this.apiData.emission;
      if (this.selectedId) {
        graphData = graphData.filter(x => x.id == this.selectedId);
      }
      if (evo) {
        let cutOffIdx = Math.floor(graphData.length * (i / 10));
        graphData = graphData.slice(0, cutOffIdx);
      }
      let speedData = graphData.map(x => [x.time, Math.round(x.speed * 100) / 100]);

      let relSpeedData = graphData
        .filter(x => x.leaderRelSpeed > -1000)
        .map(x => [x.time, Math.round(x.leaderRelSpeed * 100) / 100]);
      data.push({ name: `Speed`, color: 'lightgreen', data: speedData });
      data.push({ name: `Relative Leader Speed`, color: 'lightblue', data: relSpeedData });
      let title = i ? `${i * this.payload.checkpoint_freq}` : `Speed`;
      return { data, xAxis, yAxis, title };
    },
    accelData(i = null, evo = false) {
      let xAxis = 'Iteration';
      let yAxis = 'Acceleration (m/s)';
      let data = [];
      let graphData = this.apiData.emission;
      if (this.selectedId) {
        graphData = graphData.filter(x => x.id == this.selectedId);
      }
      if (evo) {
        let cutOffIdx = Math.floor(graphData.length * (i / 10));
        graphData = graphData.slice(0, cutOffIdx);
      }
      let accel = graphData.map(x => [x.time, Math.round(x.accel * 100) / 100]);
      data.push({ name: `Acceleration`, color: 'orange', data: accel });
      let title = i ? `${i * this.payload.checkpoint_freq}` : `Acceleration`;
      return { data, xAxis, yAxis, title };
    },
    emissionData(param) {
      let xAxis = 'Iteration';
      let yAxis =
        param == 'leaderRelSpeed'
          ? 'Leader Relative Speed (m/s)'
          : param == 'speed'
          ? 'Speed (m/s)'
          : `${param.charAt(0).toUpperCase() + param.slice(1)} (m)`;
      let data = [];
      let graphData = this.apiData.emission;
      if (this.selectedId) {
        graphData = graphData.filter(x => x.id == this.selectedId);
      }
      if (param == 'headway') {
        graphData = graphData.filter(x => x.headway < 1000).map(x => [x.time, Math.round(x.headway * 100) / 100]);
      } else if (param == 'leaderRelSpeed') {
        graphData = graphData
          .filter(x => x.leaderRelSpeed > -1000)
          .map(x => [x.time, Math.round(x.leaderRelSpeed * 100) / 100]);
      } else {
        graphData = graphData.map(x => [x.time, Math.round(x[param] * 100) / 100]);
      }

      data.push({
        name: param == 'leaderRelSpeed' ? 'Leader Relative Speed' : param.charAt(0).toUpperCase() + param.slice(1),
        color: param == 'distance' ? 'cyan' : param == 'headway' ? 'yellow' : 'orange',
        data: graphData
      });
      let title = param == 'leaderRelSpeed' ? 'Leader Relative Speed' : param.charAt(0).toUpperCase() + param.slice(1);
      return { data, xAxis, yAxis, title };
    },
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
        this.startTime = Utils.formatDateTime(new Date());
        this.selectedId = this.idList.length > 0 ? this.idList[0] : null;
        this.selectedLeader = this.leaderList.length > 0 ? this.leaderList[0] : null;
        this.selectedFollower = this.followerList.length > 0 ? this.followerList[0] : null;
        if (this.timer == null) {
          // this.elapsedTime = 0;
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

<style>
.refresh-btn {
  margin-left: auto;
  margin-right: 5px;
}
</style>

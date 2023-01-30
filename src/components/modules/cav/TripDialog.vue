<template>
  <v-dialog v-model="dialog" max-width="100%" transition="scale-transition" v-if="!$vuetify.breakpoint.mobile && data">
    <v-card class="pa-4">
      <v-card-actions v-if="!$vuetify.breakpoint.mobile">
        <v-card-title class="title"> Trip {{ cardData.tripId }} Info</v-card-title>
        <div class="middle-header"></div>
        <v-btn icon class="close-button mr-4" @click="closeDialog">
          <v-icon>mdi-close</v-icon>
        </v-btn> </v-card-actions
      ><br />
      <v-row v-if="cardData">
        <v-col cols="2">
          <v-card color="#292929" class="grid-center pb-6">
            <v-card-title>Group</v-card-title>
            <h1>{{ cardData.group ? cardData.group : '-' }}</h1>
          </v-card>
        </v-col>
        <v-col cols="2">
          <v-card color="#292929" class="grid-center pb-6">
            <v-card-title>Device Count</v-card-title>
            <h1>{{ cardData.stops ? cardData.stops : '-' }}</h1>
          </v-card>
        </v-col>
        <v-col cols="2">
          <v-card color="#292929" class="grid-center pb-6">
            <v-card-title>Start Time</v-card-title>
            <h1>{{ cardData.startTS ? getTimeStr(cardData.startTS) : '-' }}</h1>
          </v-card>
        </v-col>
        <v-col cols="2">
          <v-card color="#292929" class="grid-center pb-6">
            <v-card-title>End Time</v-card-title>
            <h1>{{ cardData.endTS ? getTimeStr(cardData.endTS) : '-' }}</h1>
          </v-card>
        </v-col>
        <v-col cols="2">
          <v-card color="#292929" class="grid-center pb-6">
            <v-card-title>Distance</v-card-title>
            <h1>{{ cardData.totalDist ? `${cardData.totalDist.toFixed(2)} mi` : '-' }}</h1>
          </v-card>
        </v-col>
        <v-col cols="2">
          <v-card color="#292929" class="grid-center pb-6">
            <v-card-title>Red to Green</v-card-title>
            <h1>
              R: {{ cardData.redStops != null ? cardData.redStops : '-' }}, G:
              {{ cardData.greenStops != null ? cardData.greenStops : '-' }}
            </h1>
          </v-card>
        </v-col>
      </v-row>
      <v-row v-if="cardData">
        <v-col cols="4">
          <v-card color="#292929">
            <v-card-title>
              Corridor Speed
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon class="ml-2" v-on="on">mdi-information-outline</v-icon>
                </template>
                <span>
                  Distance traveled divided by the average travel time.<br /><br />
                  Major Segment Speed = Total Distance / Total Time.
                </span>
              </v-tooltip>
            </v-card-title>
            <v-card-text>
              <BasicChart ref="speedChart" :data="speedData" :height="350" />
              <v-row class>
                <v-col cols="6" class="mt-2 pr-1">
                  <v-card color="#333333" class="grid-center pb-6">
                    <v-card-title>Average Speed</v-card-title>
                    <h1>{{ cardData.avgSpd != null ? `${cardData.avgSpd} mph` : '-' }}</h1>
                  </v-card>
                </v-col>
                <v-col cols="6" class="mt-2 pl-1">
                  <v-card color="#333333" class="grid-center pb-6">
                    <v-card-title>Score</v-card-title>
                    <h1>{{ cardData.speedScore != null ? cardData.speedScore : '-' }} / 36</h1>
                  </v-card>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
        <v-col cols="4">
          <v-card color="#292929" height="326px" class="grid-center pb-6 mb-6">
            <v-card-title>Total Score</v-card-title>
            <h1 :style="`color: ${color}`">{{ cardData.score ? cardData.score : '-' }} / 109</h1>
            <v-progress-circular
              button
              :color="color"
              rotate="180"
              size="180"
              :value="cardData.score ? cardData.score / 1.09 : 0"
              width="30"
            ></v-progress-circular>
          </v-card>
          <v-card color="#292929">
            <v-card-title>
              Green to Red Ratio
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon class="ml-2" v-on="on">mdi-information-outline</v-icon>
                </template>
                <span>
                  A vehicle is considered to have "arrived on red" if a stop is<br />
                  detected in a "box" starting 300 feet before the stop bar.<br /><br />
                  Major Segment G:R ratio = Total greens / Total reds.
                </span>
              </v-tooltip>
            </v-card-title>
            <v-card-text>
              <!-- <BasicChart ref="grChart" :data="grData" :height="350" /> -->
              <v-row class>
                <v-col cols="6" class="mt-2 pr-1">
                  <v-card color="#333333" class="grid-center pb-6">
                    <v-card-title>G:R Ratio</v-card-title>
                    <h1>{{ cardData.gr != null ? cardData.gr.toFixed(2) : '-' }}</h1>
                  </v-card>
                </v-col>
                <v-col cols="6" class="mt-2 pl-1">
                  <v-card color="#333333" class="grid-center pb-6">
                    <v-card-title>Score</v-card-title>
                    <h1>{{ cardData.grScore != null ? cardData.grScore : '-' }} / 40</h1>
                  </v-card>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
        <v-col cols="4">
          <v-card color="#292929">
            <v-card-title>
              Stops per Mile
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon class="ml-2" v-on="on">mdi-information-outline</v-icon>
                </template>
                <span>
                  A stop is when a vehicle slows to below 5 mph while<br />
                  previously being above 15 mph. It is considered "stopped" until<br />
                  the vehicle regains speed to above 15 mph.<br /><br />
                  Major Segment Stops per Mile = Total stops / Total distance
                </span>
              </v-tooltip>
            </v-card-title>
            <v-card-text>
              <BasicChart ref="stopsChart" :data="stopsData" :height="350" />
              <v-row class>
                <v-col cols="6" class="mt-2 pr-1">
                  <v-card color="#333333" class="grid-center pb-6">
                    <v-card-title>Stops per Mile</v-card-title>
                    <h1>{{ cardData.spm != null ? cardData.spm.toFixed(2) : '-' }}</h1>
                  </v-card>
                </v-col>
                <v-col cols="6" class="mt-2 pl-1">
                  <v-card color="#333333" class="grid-center pb-6">
                    <v-card-title>Score</v-card-title>
                    <h1>{{ cardData.spmScore != null ? cardData.spmScore : '-' }} / 33</h1>
                  </v-card>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-card>
  </v-dialog>
</template>

<script>
import Colors from '@/utils/Colors.js';
import Utils from '@/utils/Utils';
import BasicChart from '@/components/modules/traffic/common/BasicChart.vue';

export default {
  components: { BasicChart },
  props: {
    data: {
      type: Object,
      default: null
    },
    dialog: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    getTimeStr(ts) {
      return Utils.formatTime(new Date(ts));
    },
    getColorByScore(score) {
      return Colors.getRedGreenColor((score - 33) / 76);
    },
    closeDialog() {
      this.$emit('closeDialog');
    }
  },
  computed: {
    color() {
      if (this.cardData && this.cardData.score) {
        return this.getColorByScore(this.cardData.score);
      } else {
        return 'gray';
      }
    },
    speedData() {
      console.log(this.cardData);
      let title = 'Speed';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';
      let data = [];
      data.push({ name: 'Speed', color: '#058DC7', data: this.cardData.speedData });
      return { data, xAxis, yAxis, title };
    },
    grData() {
      let title = 'Red Count';
      let xAxis = 'Time of day';
      let yAxis = 'Red Count';
      let data = [];
      data.push({ name: 'Red Count', color: '#00FF00', data: this.cardData.reds });
      return { data, xAxis, yAxis, title };
    },
    stopsData() {
      let title = 'Stops';
      let xAxis = 'Time of day';
      let yAxis = 'Stops';
      let data = [];
      data.push({ name: 'Stops', color: '#FFFF00', data: this.cardData.stopsInTrip });
      if (this.cardData.redsInTrip) {
        data.push({ name: 'Red Light Stops', color: '#ED561B', data: this.cardData.redsInTrip });
      }
      return { data, xAxis, yAxis, title };
    },
    graphData() {
      return this.data.data;
    },
    cardData() {
      let cardData = JSON.parse(JSON.stringify(this.data));
      delete cardData.data;
      return cardData;
    }
  },
  watch: {
    dialog(val) {
      if (val) {
        setTimeout(() => {
          this.$bus.$emit('CHART_RELOAD');
        }, 10);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
@media only screen and (max-width: 1000px) {
  .interval-slider {
    display: none;
  }
}
.middle-header {
  text-align: center;
  margin: 0 auto;
  padding: 10px;
}
.title {
  position: absolute;
  left: 20px;
  padding: 10px;
}
.close-button {
  position: absolute;
  right: 20px;
  padding: 10px;
}
.grid-left {
  display: grid;
  place-items: start;
}
.grid-right {
  display: grid;
  place-items: end;
}
.grid-center {
  display: grid;
  place-items: center;
}
</style>

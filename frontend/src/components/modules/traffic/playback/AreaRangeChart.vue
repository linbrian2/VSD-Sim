<template>
  <div :id="this.containerName" v-if="!reload"></div>
</template>
<script>
import Highcharts from 'highcharts';
import { mapState } from 'vuex';
import * as HighchartsMore from 'highcharts/highcharts-more';
import ChartStyles from '@/utils/ChartStyles.js';

//magic
HighchartsMore(Highcharts);

export default {
  props: [
    'historicData',
    'currData',
    'interval',
    'minWidth',
    'height',
    'title',
    'subtitle',
    'containerName',
    'legendX',
    'legendY',
    'multiGraph',
    'visibility',
    'legendWidth'
  ],
  data: () => ({
    reload: false,
    baseMinMax: [],
    baseQuart: [],
    baseMean: [],
    currMinMax: [],
    currQuart: [],
    currMean: [],
    currMeanAbove75Pctl: []
  }),
  mounted() {
    this.init();
  },
  watch: {
    historicData() {
      this.init();
    },
    '$vuetify.theme.dark'() {
      this.addChart();
    }
  },
  computed: {
    ...mapState(['currentDate'])
  },
  methods: {
    init() {
      if (this.multiGraph) {
        this.addDataMultiGraph();
      } else {
        this.addData();
      }
      this.addChart();
    },
    addDataMultiGraph() {
      let selectedDT = this.currentDate;
      var twoHoursBefore = new Date(selectedDT.toISOString());
      twoHoursBefore.setHours(twoHoursBefore.getHours() - 6);
      var oneHourAfter = new Date(selectedDT.toISOString());
      oneHourAfter.setHours(oneHourAfter.getHours() - 3);

      var time = new Date(selectedDT.toISOString());
      var index = 0;
      if (this.historicData) {
        for (var i = 0; i < this.historicData.length; i++) {
          var tod = this.historicData[i].timeOfDay;
          var minutes = parseInt(tod.slice(0, 2)) * 60 + parseInt(tod.slice(3, 5));
          time.setUTCHours(0, minutes, 0, 0);
          if (minutes % this.interval == 0 && time.getTime() >= twoHoursBefore && time.getTime() <= oneHourAfter) {
            this.baseMean[index] = [time.getTime(), this.historicData[i].meanTravelTime];
            this.baseMinMax[index] = [
              time.getTime(),
              this.historicData[i].minTravelTime,
              this.historicData[i].maxTravelTime
            ];
            this.baseQuart[index] = [
              time.getTime(),
              this.historicData[i].pc25TravelTime,
              this.historicData[i].pc75TravelTime
            ];
            index++;
          }
        }
      }
      if (this.currData) {
        index = 0;
        for (var j = 0; j < this.currData.length; j++) {
          tod = this.currData[j].timeOfDay;
          minutes = parseInt(tod.slice(0, 2)) * 60 + parseInt(tod.slice(3, 5));
          time.setUTCHours(0, minutes, 0, 0);
          if (minutes % this.interval == 0 && time >= twoHoursBefore && time <= oneHourAfter) {
            this.currMean[index] = [time.getTime(), this.currData[j].meanTT];
            this.currMinMax[index] = [time.getTime(), this.currData[j].minTT, this.currData[j].maxTT];
            this.currQuart[index] = [time.getTime(), this.currData[j].pctl25TT, this.currData[j].pctl75TT];
            index++;
          }
        }
      }
    },
    addData() {
      let selectedDT = this.currentDate;
      var time = new Date(selectedDT.toISOString());
      var index = 0;
      if (this.historicData) {
        for (var i = 0; i < this.historicData.length; i++) {
          var tod = this.historicData[i].timeOfDay;
          var minutes = parseInt(tod.slice(0, 2)) * 60 + parseInt(tod.slice(3, 5));
          if (minutes % this.interval == 0) {
            time.setUTCHours(0, minutes, 0, 0);
            this.baseMean[index] = [time.getTime(), this.historicData[i].meanTravelTime];
            this.baseMinMax[index] = [
              time.getTime(),
              this.historicData[i].minTravelTime,
              this.historicData[i].maxTravelTime
            ];
            this.baseQuart[index] = [
              time.getTime(),
              this.historicData[i].pc25TravelTime,
              this.historicData[i].pc75TravelTime
            ];
            index++;
          }
        }
      }
      if (this.currData) {
        index = 0;
        var baseIndex = 0;
        for (var j = 0; j < this.currData.length; j++) {
          tod = this.currData[j].timeOfDay;
          minutes = parseInt(tod.slice(0, 2)) * 60 + parseInt(tod.slice(3, 5));
          if (minutes % this.interval == 0) {
            time.setUTCHours(0, minutes, 0, 0);
            this.currMean[index] = [time.getTime(), this.currData[j].meanTT];
            this.currMeanAbove75Pctl[index] = [time.getTime(), this.currData[j].meanTT];
            for (; baseIndex < this.baseQuart.length; baseIndex++) {
              if (this.baseQuart[baseIndex][0] == time.getTime()) {
                var pctl75 = this.baseQuart[baseIndex][2];
                if (this.currData[j].meanTT > pctl75) {
                  this.currMeanAbove75Pctl[index] = [time.getTime(), this.currData[j].meanTT];
                  baseIndex++;
                  break;
                } else {
                  this.currMeanAbove75Pctl[index] = [time.getTime(), null];
                  baseIndex++;
                  break;
                }
              }
              if (this.baseQuart[baseIndex][0] > time.getTime()) {
                break;
              }
            }
            this.currMinMax[index] = [time.getTime(), this.currData[j].minTT, this.currData[j].maxTT];
            this.currQuart[index] = [time.getTime(), this.currData[j].pctl25TT, this.currData[j].pctl75TT];
            index++;
          }
        }
      }
    },
    addChart() {
      this.addTheme();
      let height = this.$vuetify.breakpoint.mobile ? 300 : 600;
      Highcharts.chart(this.containerName, {
        chart: {
          marginBottom: 85,
          height: height,
          zoomType: this.multiGraph ? null : 'x',
          scrollablePlotArea: {
            minWidth: this.minWidth ? this.minWidth : 600,
            scrollPositionX: 1
          }
        },

        legend: {
          enabled: !this.$vuetify.breakpoint.mobile,
          align: 'center',
          verticalAlign: 'bottom',
          y: 0
        },

        exporting: {
          enabled: true
        },

        time: {
          timezoneOffset: 0
        },

        title: {
          text: this.title
        },

        subtitle: {
          text: this.subtitle
        },

        xAxis: {
          type: 'datetime',
          crosshair: true,
          accessibility: {
            rangeDescription: 'Range: 12:00AM to 11:59PM'
          }
        },

        yAxis: {
          title: {
            text: 'Travel Time (s)'
          }
        },

        tooltip: {
          crosshairs: true,
          shared: true,
          valueSuffix: 's'
        },

        series: [
          {
            name: 'Baseline Mean',
            data: this.baseMean,
            zIndex: 1,
            color: Highcharts.getOptions().colors[1],
            marker: {
              enabled: false
            },
            showInLegend: true,
            visible: this.visibility ? this.visibility[0] : true
          },
          {
            name: 'Baseline 25%/75%',
            data: this.baseQuart,
            type: 'arearange',
            lineWidth: 0,
            color: Highcharts.getOptions().colors[4],
            fillOpacity: 0.3,
            zIndex: 3,
            marker: {
              enabled: false
            },
            showInLegend: true,
            visible: this.visibility ? this.visibility[2] : true
          },
          {
            name: 'Baseline Min/Max',
            data: this.baseMinMax,
            type: 'arearange',
            lineWidth: 0,
            color: Highcharts.getOptions().colors[7],
            fillOpacity: 0.3,
            zIndex: 2,
            marker: {
              enabled: false
            },
            showInLegend: true,
            visible: this.visibility ? this.visibility[1] : true
          },
          {
            name: 'Current Mean',
            data: this.currMean,
            zIndex: 1,
            color: Highcharts.getOptions().colors[2],
            marker: {
              enabled: false
            },
            showInLegend: true,
            visible: this.visibility ? this.visibility[3] : true
          },
          {
            name: 'Current Mean (>75%)',
            data: this.currMeanAbove75Pctl,
            zIndex: 1,
            lineWidth: 6,
            linkedTo: ':previous',
            color: Highcharts.getOptions().colors[5],
            fillOpacity: 0.3,
            marker: {
              enabled: false
            },
            visible: this.visibility ? this.visibility[7] : true,
            enableMouseTracking: false
          },
          {
            name: 'Current 25%/75%',
            data: this.currQuart,
            type: 'arearange',
            lineWidth: 0,
            color: Highcharts.getOptions().colors[6],
            fillOpacity: 0.3,
            zIndex: 0,
            marker: {
              enabled: false
            },
            showInLegend: true,
            visible: this.visibility ? this.visibility[5] : false
          },
          {
            name: 'Current Min/Max',
            data: this.currMinMax,
            type: 'arearange',
            lineWidth: 0,
            color: Highcharts.getOptions().colors[5],
            fillOpacity: 0.3,
            zIndex: 0,
            marker: {
              enabled: false
            },
            showInLegend: true,
            visible: this.visibility ? this.visibility[4] : false
          }
        ],

        credits: {
          enabled: false
        }
      });
    },
    addTheme() {
      if (this.$vuetify.theme.dark) {
        Highcharts.theme = ChartStyles.getDarkTheme();
      } else {
        Highcharts.theme = ChartStyles.getLightTheme();
      }
      Highcharts.setOptions(Highcharts.theme);
    }
  }
};
</script>

<style scoped>
g.highcharts-series.highcharts-series-4.highcharts-line-series {
  animation-duration: 0.5s;
  animation-name: blinking;
  animation-iteration-count: 99999999;
  animation-timing-function: ease-in-out;
}

@keyframes blinking {
  0% {
    fill: red;
  }
  50% {
    fill: green;
  }
  100% {
    fill: red;
  }
}
</style>

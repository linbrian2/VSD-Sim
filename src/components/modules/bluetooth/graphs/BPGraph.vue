<template>
  <div :id="this.options.containerName"></div>
</template>
<script>
import Highcharts from 'highcharts';
import ChartStyles from '@/utils/ChartStyles.js';

export default {
  props: ['title', 'bpInfo', 'options'],
  data: () => ({
    volume: [],
    bp: []
  }),
  mounted() {
    if (this.bpInfo) {
      this.addData();
    }
    this.addGraph();
  },
  watch: {
    bpInfo() {
      this.addData();
      this.addGraph();
    },
    '$vuetify.theme.dark'() {
      this.addGraph();
    }
  },
  methods: {
    addData() {
      this.volume = [];
      this.bp = [];
      for (var i = 0; i < this.bpInfo.length; i++) {
        if (this.options.smallChart && i == 24) {
          break;
        }
        this.addDataPoint(i, -3 * 60 * 60 * 1000);
      }
    },
    addDataPoint(i, offset) {
      this.volume[i] = [this.bpInfo[i].createdTime - offset, this.bpInfo[i].volume];
      this.bp[i] = [this.bpInfo[i].createdTime - offset, parseFloat(this.bpInfo[i].bp.toFixed(3))];
    },
    addGraph() {
      this.addTheme();
      Highcharts.chart(this.options.containerName, {
        chart: {
          type: 'line',
          height: this.options.height ? this.options.height : 475,
          zoomType: 'x',
          marginRight: this.options.marginRight ? this.options.marginRight : undefined,
          scrollablePlotArea: {
            minWidth: 600,
            scrollPositionX: 1
          }
        },
        title: {
          text: this.title,
          align: 'center',
          x: this.options.titleOffset ? this.options.titleOffset : 0
        },
        yAxis: [
          {
            title: {
              text: 'Breakdown Probability'
            },
            max: 1,
            min: 0,
            tickPixelInterval: 40
          },
          {
            title: {
              text: 'Volume'
            },
            tickPixelInterval: 40,
            opposite: true
          }
        ],
        tooltip: {
          crosshairs: true,
          shared: true
        },
        xAxis: {
          title: {
            text: 'Time'
          },
          type: 'datetime',
          crosshair: true,
          accessibility: {
            rangeDescription: 'Range: 12:00AM to 11:59PM'
          }
        },
        series: [
          {
            name: 'Breakdown Probability',
            yAxis: 0,
            data: this.bp,
            color: Highcharts.getOptions().colors[2],
            lineWidth: 3
          },
          {
            name: 'Volume',
            yAxis: 1,
            data: this.volume,
            color: Highcharts.getOptions().colors[4],
            lineWidth: 3
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

<style scoped></style>

<template>
  <div>
    <highcharts ref="highcharts" class="chart" :options="chartOptions" v-show="phaseTiming != null"></highcharts>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
import { mapState } from 'vuex';
export default {
  computed: {
    height() {
      if (this.phaseTiming === null || this.phaseTiming === undefined) {
        return 300;
      }
      let len = Object.keys(this.phaseTiming).length;
      return 45 * len;
    },
    chartOptions() {
      if (this.phaseTiming == null) {
        return {
          credits: {
            enabled: false
          }
        };
      }
      let d = this.prepareData(this.phaseTiming);
      let height = this.$vuetify.breakpoint.mobile ? 300 : this.height;
      return this.makeChart(height, d.series, d.categories, d.ticks);
    },
    ...mapState('hr', ['phaseTiming', 'currentSignalTime'])
  },
  watch: {
    currentSignalTime(value) {
      this.updateChartVerticalLine(value);
    }
  },
  methods: {
    prepareData(data) {
      // Add data to series
      let series = [];
      let categories = [];
      let ticks = [];

      if (data) {
        Object.keys(data).forEach((phaseId, i) => {
          ticks.push(i);
          categories.push('P' + phaseId);
          let h = i;
          let signal = data[phaseId];
          if (signal.signals) {
            series = series.concat(this.composeCycleSeries(h, signal.signals));
          }
          if (signal.detectors) {
            series.push(this.composeDetectorSeries(h, signal.detectors));
          }
        });
      }

      return { series, categories, ticks };
    },

    composeCycleSeries(h, cycles) {
      let h1 = h - 0.25;
      let h2 = h + 0.25;

      let series = [];
      for (let i = 0; i < cycles.length; i++) {
        let t0 = cycles[i][0];
        let t1 = cycles[i][1];
        let t2 = cycles[i][2];
        let t3 = cycles[i][3];

        if (!t0 || !t1 || !t2 || !t3) {
          console.log(t0, t1, t2, t3);
        }

        // Red
        if (t0 > 0 && t1 > 0) {
          series.push({
            type: 'polygon',
            name: 'R' + i,
            color: '#dd0000',
            enableMouseTracking: false,
            data: [
              [t0, h1],
              [t1, h1],
              [t1, h2],
              [t0, h2]
            ]
          });
        }

        // Green
        if (t1 > 0 && t2 > 0) {
          series.push({
            type: 'polygon',
            name: 'G' + i,
            color: '#00dd00',
            enableMouseTracking: false,
            data: [
              [t1, h1],
              [t2, h1],
              [t2, h2],
              [t1, h2]
            ]
          });
        }

        // Yellow
        if (t2 > 0 && t3 > 0) {
          series.push({
            type: 'polygon',
            name: 'Y' + i,
            color: '#FFFF00',
            enableMouseTracking: false,
            data: [
              [t2, h1],
              [t3, h1],
              [t3, h2],
              [t2, h2]
            ]
          });
        }
      }

      return series;
    },

    composeDetectorSeries(h, detectors) {
      let points = [];
      detectors.forEach(t => {
        points.push([t, h + 0.32]);
      });
      return {
        type: 'scatter',
        name: 'vehicles',
        color: 'white',
        marker: {
          radius: 1.0,
          symbol: 'circle'
        },
        data: points
      };
    },

    updateChartVerticalLine(time) {
      let chart = this.$refs.highcharts.chart;
      if (chart != null) {
        const lineId = 'running-line';
        chart.xAxis[0].removePlotLine(lineId);
        chart.xAxis[0].addPlotLine({
          value: time,
          color: 'white',
          width: 1,
          id: lineId,
          zIndex: 30
        });
      }
    },

    makeChart(chartHeight, series, categories, ticks) {
      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          spacingTop: 5,
          spacingBottom: 5,
          marginRight: 5,
          type: 'line',
          zoomType: 'xy',
          plotBorderColor: '#888',
          plotBorderWidth: 1,
          backgroundColor: '#455A64'
        },
        plotOptions: {
          series: {
            enableMouseTracking: false,
            shadow: false,
            states: {
              hover: {
                lineWidth: 1
              }
            }
          }
        },
        title: {
          text: null
        },
        xAxis: {
          type: 'datetime',
          labels: {
            style: {
              color: '#fff',
              fontSize: 10,
              fontWeight: 'normal'
            }
          }
        },
        yAxis: {
          reversed: true,
          allowDecimals: false,
          min: 0,
          tickPositions: ticks,
          categories: categories,
          labels: {
            style: {
              color: '#fff',
              font: '11px Arial',
              fontWeight: 'normal'
            }
          },
          title: {
            text: null
          }
        },
        tooltip: {
          formatter: function() {
            let s = 'Time: ' + Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', new Date(this.x));
            return s;
          }
        },
        legend: {
          enabled: false
        },
        exporting: {
          enabled: false
        },
        series: series
      };
      return chart;
    }
  }
};
</script>

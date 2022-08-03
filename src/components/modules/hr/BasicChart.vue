<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts
        ref="highcharts"
        class="chart"
        :options="chartOptions"
        v-show="data != null"
        v-if="!reload"
      ></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: ['data', 'height'],
  data() {
    return {
      reload: false
    };
  },
  computed: {
    chartOptions() {
      if (this.data == null) {
        return {
          credits: {
            enabled: false
          }
        };
      }

      return this.makeChart(this.height, this.data);
    }
  },

  methods: {
    setYAxisRange(min, max) {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        const chart = this.$refs.highcharts.chart;
        chart.yAxis[0].setExtremes(min, max);
      }
    },

    prepareData(data, color) {
      let chartColor = color || '#0000ff';

      // Add data to series
      let series = [];
      series.push({
        type: 'line',
        name: 'data',
        color: chartColor,
        data: data,
        lineWidth: 1.5
      });
      return series;
    },

    makeChart(chartHeight, data) {
      const title = data.title;
      const ylabel = data.ylabel;
      const series = this.prepareData(data.data, data.color);

      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          spacingTop: 20,
          spacingBottom: 15,
          marginLeft: 90,
          marginRight: 30,
          type: 'line',
          zoomType: 'xy',
          plotBorderColor: '#DEDEDE',
          plotBorderWidth: 1
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
          style: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          text: title
        },
        xAxis: {
          type: 'datetime',
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'normal'
            },
            text: 'Time of day'
          }
        },
        yAxis: {
          allowDecimals: false,
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'normal'
            },
            text: ylabel
          },
          labels: {
            format: '{value}'
          },
          min: data.min,
          max: data.max,
          plotLines: data.plotLines
        },
        tooltip: {
          shared: true,
          formatter: () => {
            let s = 'Time: ' + Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', new Date(this.x));
            this.points.forEach(function() {
              s += '<br/>' + this.series.name + ': ' + this.y + 's';
            });
            return s;
          }
        },
        legend: {
          enabled: false,
          floating: true,
          verticalAlign: 'top',
          align: 'left',
          x: 65
        },
        exporting: {
          enabled: false,
          buttons: {
            contextButton: {
              align: 'left',
              verticalAlign: 'bottom'
            },
            exportButton: {
              enabled: true
            },
            printButton: {
              enabled: false
            }
          }
        },
        series: series
      };
      return chart;
    },
    refresh() {
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, 1);
    }
  },
  watch: {
    '$store.state.navigationWidth'() {
      this.refresh();
    },
    '$store.state.darkMode'() {
      this.refresh();
    }
  }
};
</script>

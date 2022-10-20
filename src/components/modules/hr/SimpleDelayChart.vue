<template>
  <div>
    <!-- <div class="chart-title" v-if="title">{{ title }}</div> -->
    <div class="my-0 mx-0">
      <highcharts class="chart" :options="chartOptions" v-show="data != null"></highcharts>
    </div>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: ['data', 'title', 'height'],
  computed: {
    chartOptions() {
      if (this.data == null) {
        return {
          credits: {
            enabled: false
          }
        };
      }
      let series = this.prepareData(this.data);
      let height = this.$vuetify.breakpoint.mobile ? 300 : this.height;
      return this.makeChart(height, this.title, series);
    }
  },
  methods: {
    prepareData(data) {
      // Add data to series
      let series = [];
      series.push({
        type: 'line',
        name: 'Northbound',
        color: '#FF0000',
        data: data[0],
        lineWidth: 1.5
      });
      series.push({
        type: 'line',
        name: 'Southbound',
        color: '#0000FF',
        data: data[1],
        lineWidth: 1.5
      });
      return series;
    },

    makeChart(chartHeight, title, series) {
      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          spacingTop: 30,
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
            text: 'Cycle Delay (seconds)'
          }
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
          enabled: true,
          floating: true,
          verticalAlign: 'top',
          align: 'left',
          x: 90,
          y: 45
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
    }
  }
};
</script>

<template>
  <div>
    <!-- <div class="chart-title" v-if="title">{{ title }}</div> -->
    <div class="my-0 mx-0">
      <highcharts class="chart" :options="chartOptions" v-show="data != null" v-if="!reload"></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: ['data', 'title', 'phase', 'height'],
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
      let series = this.prepareData(this.data);
      return this.makeChart(this.height, this.title, series);
    }
  },
  methods: {
    prepareData(data) {
      // Add data to series
      let series = [];
      series.push({
        type: 'line',
        name: 'Green',
        color: '#00FF00',
        data: data.green,
        lineWidth: 1.5
      });
      series.push({
        type: 'line',
        name: 'Yellow',
        color: '#FFA500',
        data: data.yellow,
        lineWidth: 1.5
      });

      series.push({
        type: 'line',
        name: 'Red',
        color: '#FF0000',
        data: data.red,
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
            text: 'Cycle Duration (seconds)'
          }
        },
        tooltip: {
          shared: true,
          crosshairs: true,
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
          x: 80,
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
    },
    refresh(ms = 1000) {
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, ms);
    }
  },
  watch: {
    '$store.state.navigationWidth'() {
      this.refresh();
    },
    '$store.state.darkMode'() {
      this.refresh(1);
    }
  }
};
</script>

<template>
  <div>
    <div class="chart-title" v-if="title">{{ title }}</div>
    <div class="my-0 mx-0">
      <highcharts class="chart" :options="chartOptions" v-if="!reload"></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: ['data', 'title', 'height'],
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
    prepareSeries(data) {
      let series = [];
      data.forEach(item => {
        if (item.data !== null) {
          series.push({
            type: 'line',
            name: item.name,
            color: item.color,
            data: item.data,
            lineWidth: 1.5
          });
        }
      });
      return series;
    },
    makeChart(chartHeight, data) {
      // Add data to series
      let series = this.prepareSeries(data.data);
      let title = data.title;
      let xAxis = data.xAxis;
      let yAxis = data.yAxis;

      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          spacingTop: 30,
          spacingBottom: 25,
          marginLeft: 90,
          marginRight: 40,
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
              fontWeight: 'bold'
            },
            text: xAxis
          }
        },
        yAxis: {
          allowDecimals: false,
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'bold'
            },
            text: yAxis
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
          enabled: false
        },
        series: series
      };
      return chart;
    },
    refresh(ms = 250) {
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
      this.refresh();
    }
  }
};
</script>

<style scoped>
.chart-title {
  width: 100%;
  text-align: center;
  font-size: 1rem;
  font-weight: bold;
  margin-top: 10px;
  margin-top: 5px;
}
</style>

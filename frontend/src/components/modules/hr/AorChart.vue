<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts class="chart" :options="chartOptions" v-show="data != null" v-if="!reload"></highcharts>
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
        type: 'area',
        name: 'AoR',
        color: '#EF9A9A',
        data: data
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

          spacingTop: this.$vuetify.breakpoint.mobile ? 10 : 30,
          spacingBottom: this.$vuetify.breakpoint.mobile ? 10 : 15,
          marginLeft: this.$vuetify.breakpoint.mobile ? 70 : 90,
          marginRight: this.$vuetify.breakpoint.mobile ? 20 : 30,

          type: 'line',
          zoomType: 'xy',
          plotBorderColor: '#DEDEDE',
          plotBorderWidth: 1,
          plotBackgroundColor: 'green'
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
          },
          area: {
            fillColor: 'red',
            marker: {
              radius: 2
            },
            lineWidth: 1,
            states: {
              hover: {
                lineWidth: 1
              }
            },
            threshold: null
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
          min: 0,
          max: 100,
          allowDecimals: false,
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'normal'
            },
            text: 'Percentage'
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
          enabled: false,
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

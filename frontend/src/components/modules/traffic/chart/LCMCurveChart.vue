<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts ref="highcharts" class="chart" :options="chartOptions" v-if="!reload"></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: {
    data: Object,
    height: Number,
    direction: String
  },

  computed: {
    chartOptions() {
      if (!this.data) {
        return {
          credits: {
            enabled: false
          }
        };
      }
      let height = this.$vuetify.breakpoint.mobile ? 400 : this.height;
      return this.makeChart(height, this.data);
    }
  },

  data: () => ({
    selectedTime: null,
    reload: false
  }),

  mounted() {
    this.$bus.$on('CHART_RELOAD', () => {
      this.refresh(500);
    });
  },

  methods: {
    selectPoints(chart, time) {
      //const ctx = this;

      // Unselect
      Highcharts.each(chart.getSelectedPoints(), function(p) {
        if (p.time !== time) {
          p.select(false);
          //  p.update({ dataLabels: ctx.dataLabels(false) }, false);
        }
      });

      // Select
      const data = chart.series[1].data;
      const length = data.length;
      Highcharts.each(data, function(p, idx) {
        if (p.time === time) {
          for (let i = Math.max(0, idx - 1); i <= Math.min(idx + 1, length - 1); i++) {
            data[i].select(true, true);
            //data[i].update({ dataLabels: ctx.dataLabels(true) }, false);
          }
        }
      });

      //chart.redraw();
    },

    dataLabels(enable) {
      if (enable) {
        return {
          enabled: true,
          formatter: function() {
            return Highcharts.dateFormat('%H:%M', this.point.time);
          },
          y: -10
        };
      } else {
        return {
          enabled: false
        };
      }
    },

    flowPointClicked(time) {
      this.$emit('click', { direction: this.direction, time });
    },

    makeChart(chartHeight, data) {
      let ctx = this;

      // Add data to series
      let series = data.series;
      let title = data.title || '';
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
          spacingBottom: 20,
          marginLeft: 90,
          marginRight: 35,
          plotBorderColor: '#DEDEDE',
          plotBorderWidth: 1,
          zoomType: 'xy'
        },

        title: {
          style: {
            fontSize: '18px',
            fontWeight: 'bold'
          },
          text: title
        },

        xAxis: {
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'bold'
            },
            text: xAxis
          },
          plotLines: data.xplotlines,
          min: data.xmin,
          max: data.xmax
        },

        yAxis: {
          allowDecimals: false,
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'bold'
            },
            text: yAxis
          },
          plotLines: data.yplotlines,
          min: data.ymin,
          max: data.ymax
        },

        tooltip: {
          // positioner: function() {
          //   return { x: 120, y: 50 };
          // },
          // shadow: false,
          // borderWidth: 1,
          // backgroundColor: 'rgba(255,255,255,0.8)'
        },

        legend: {
          enabled: series && series.length > 0 && !this.$vuetify.breakpoint.mobile,
          floating: true,
          verticalAlign: 'top',
          align: 'left',
          x: 90,
          y: 50
        },

        navigation: {
          buttonOptions: {
            align: 'left',
            verticalAlign: 'bottom'
          }
        },

        exporting: {
          enabled: this.exporting,
          buttons: {
            contextButton: {
              menuItems: ['viewFullscreen', 'separator', 'downloadPNG', 'downloadJPEG', 'downloadPDF']
            }
          }
        },

        //https://stackoverflow.com/questions/55595123/highcharts-disable-fading-series-on-marker-hover/55595271
        plotOptions: {
          series: {
            allowPointSelect: true,
            marker: {
              states: {
                select: {
                  fillColor: 'orange',
                  radius: 12
                }
              }
            },
            states: {
              inactive: {
                opacity: 1
              }
            },

            point: {
              events: {
                mouseOver: function() {
                  const chart = this.series.chart;
                  ctx.selectPoints(chart, this.time);
                },
                click: function() {
                  ctx.flowPointClicked(this.time);
                }
              }
            },
            events: {
              mouseOut: function() {}
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

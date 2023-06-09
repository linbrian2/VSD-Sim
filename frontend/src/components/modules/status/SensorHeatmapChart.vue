<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts :options="chartOptions" v-if="!reload"></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
export default {
  props: {
    data: Object,
    height: Number,
    legend: { type: Boolean, default: true }
  },

  data() {
    return {
      reload: false,
      dataClasses: []
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
      let chart = this.makeChart(this.height, this.data);
      return chart;
    }
  },
  methods: {
    tranformSeries(data) {
      if (data) {
        return data.map(item => {
          let x = item[0];
          let y = item[1];
          let value = item[2] >= 0 ? item[2] : null;
          return { x, y, value };
        });
      } else {
        return null;
      }
    },

    getXAxisProps(label, categories, format = '{value:%H}') {
      return {
        categories: categories,
        title: {
          y: 0,
          style: {
            fontSize: 13,
            fontWeight: 'normal'
          },
          text: label
        },
        type: 'datetime',

        labels: {
          align: 'center',
          y: 14,
          style: {
            fontSize: 11,
            fontWeight: 'bold'
          },
          format: format
        },
        tickLength: 24
      };
    },

    getYAxisProps(label, categories) {
      return {
        categories: categories,
        reversed: false,
        tickWidth: 1,
        title: {
          style: {
            fontSize: 13,
            fontWeight: 'normal'
          },
          text: label
        },
        labels: {
          style: {
            fontSize: 11,
            fontWeight: 'bold'
          },
          events: {
            click: function() {
              console.log(this);
            }
          }
        }
      };
    },

    getChartTitle(title) {
      return {
        style: {
          fontSize: '15px',
          fontWeight: 'bold'
        },
        margin: 5,
        text: title
      };
    },

    getTooltip(point) {
      let hh = point.x;
      if (hh < 10) {
        hh = '0' + hh;
      }
      let mm = point.y * 5;
      if (mm < 10) {
        mm = '0' + mm;
      }
      let value = point.value;
      if (value == null || value < 0) {
        return `Time: ${hh}:${mm}`;
      } else {
        if (this.dataClasses.length > 0 && value < this.dataClasses.length) {
          value = this.dataClasses[value].name;
        }
      }

      return `Time: ${hh}:${mm}, Value: ${value}`;
    },

    makeChart(chartHeight, data) {
      let ctx = this;

      // Add data to series
      let series = this.tranformSeries(data.data);
      let title = this.getChartTitle(data.title || '');
      let xAxis = this.getXAxisProps(data.xAxis, data.xcategories);
      let yAxis = this.getYAxisProps(data.yAxis, data.ycategories);
      let colorAxis = data.colorAxis;
      this.dataClasses = colorAxis['dataClasses'];

      // Create chart instance
      return {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          spacingTop: 15,
          spacingBottom: 5,
          marginLeft: this.$vuetify.breakpoint.mobile ? 60 : 75,
          marginRight: !this.$vuetify.breakpoint.mobile && this.legend ? 270 : 20,
          marginBottom: 50,
          type: 'heatmap',
          plotBorderColor: '#0000ff',
          plotBorderWidth: 2
        },

        title: title,
        xAxis: xAxis,
        yAxis: yAxis,

        colorAxis: colorAxis,

        legend: {
          enabled: !this.$vuetify.breakpoint.mobile && this.legend,
          title: {
            text: 'Error Types'
          },
          align: 'right',
          verticalAlign: 'top',
          floating: true,
          layout: 'vertical',
          x: -22,
          y: 115,
          valueDecimals: 0,
          backgroundColor: 'rgba(255,255,255,0.4)',
          borderColor: '#C98657',
          borderWidth: 1,
          symbolRadius: 0,
          symbolHeight: 13
        },

        tooltip: {
          enabled: true,
          formatter: function() {
            return ctx.getTooltip(this.point);
          }
        },

        navigation: {
          buttonOptions: {
            align: 'left',
            verticalAlign: 'bottom'
          }
        },
        exporting: {
          enabled: false,
          buttons: {
            contextButton: {
              menuItems: ['viewFullscreen', 'separator', 'downloadPNG', 'downloadJPEG', 'downloadPDF']
            }
          }
        },

        series: [
          {
            name: 'Heatmap',
            borderWidth: 1,
            turboThreshold: 0, //larger threshold or set to 0 to disable
            data: series,
            dataLabels: {
              enabled: false
            },
            nullColor: '#708090',
            tooltip: {
              pointFormat: 'Time: {point.x}:{point.y} <b>Volume: {point.value}</b>'
            },
            events: {
              click: function(e) {
                if (e.point && e.point.value && e.point.value >= 0) {
                  ctx.$emit('cell-click', e.point);
                }
              }
            }
          }
        ]
      };
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

<template>
  <div>
    <div class="my-0 mx-4">
      <highcharts ref="highcharts" :options="chartOptions" v-if="!reload"></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
export default {
  props: ['data', 'height'],
  data: () => ({
    reload: false,
    selectedIds: []
  }),
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
    redraw() {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        this.$refs.highcharts.chart.redraw();
      }
    },

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

    highlightCells(ids) {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        const chart = this.$refs.highcharts.chart;
        if (this.selectedIds) {
          this.selectedIds.forEach(item => {
            chart.series[0].data[item.id].update({ color: item.color }, false);
          });
          this.selectedIds = [];
        }

        ids.forEach(idx => {
          this.selectedIds.push({ id: idx, color: chart.series[0].data[idx].color });
          chart.series[0].data[idx].update({ color: 'blue' }, false);
        });

        chart.redraw();
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
            click: function() {}
          }
        }
      };
    },

    getChartTitle(title) {
      return {
        style: {
          fontSize: '14px',
          fontWeight: 'normal'
        },
        margin: 5,
        text: title
      };
    },

    getMinMax(data) {
      let values = [];
      data.forEach(item => {
        let value = item[2];
        if (value && value >= 0) {
          values.push(value);
        }
      });
      let min = Math.min(...values);
      let max = Math.max(...values);
      return { min, max };
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

      // Create chart instance
      return {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          spacingTop: 0,
          spacingBottom: 5,
          marginLeft: 75,
          marginRight: 60,
          marginBottom: 45,
          type: 'heatmap',
          plotBorderColor: '#0000ff',
          plotBorderWidth: 0
        },

        title: title,
        xAxis: xAxis,
        yAxis: yAxis,

        colorAxis: colorAxis,

        legend: {
          enabled: false,
          title: {
            text: ''
          },
          align: 'right',
          verticalAlign: 'middle',
          floating: true,
          layout: 'vertical',
          x: 0,
          y: 0,
          reversed: true,
          valueDecimals: 0,
          backgroundColor: 'rgba(255,255,255,0.9)',
          borderColor: '#C98657',
          borderWidth: 1,
          symbolRadius: 0
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
          enabled: false
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

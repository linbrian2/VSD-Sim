<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts :options="chartOptions"></highcharts>
    </div>
  </div>
</template>

<script>
export default {
  props: ['data', 'height'],
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

    getXAxisProps(label, categories, format = '{value:%H}') {
      return {
        categories: categories,
        title: {
          y: 10,
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
        tickPositions: [0, 10, 20, 30, 40, 50, 60],
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
      let mm = point.y;
      if (mm < 10) {
        mm = '0' + mm;
      }
      let volume = point.value;
      if (volume == null || volume < 0) {
        return `Time: ${hh}:${mm}`;
      }
      return `Time: ${hh}:${mm}, Volume: ${volume}`;
    },

    makeChart(chartHeight, data) {
      let ctx = this;

      // Add data to series
      let minmax = this.getMinMax(data.data);
      let series = this.tranformSeries(data.data);
      let title = this.getChartTitle(data.title || '');
      let xAxis = this.getXAxisProps(data.xAxis, data.xcategories);
      let yAxis = this.getYAxisProps(data.yAxis, data.ycategories);

      // Create chart instance
      return {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          spacingTop: 10,
          spacingBottom: 5,
          marginLeft: 75,
          marginRight: 65,
          marginBottom: 55,
          type: 'heatmap',
          plotBorderColor: '#0000ff',
          plotBorderWidth: 2
        },

        title: title,
        xAxis: xAxis,
        yAxis: yAxis,

        colorAxis: {
          min: minmax.min,
          max: minmax.max,
          endOnTick: false,
          startOnTick: false,
          tickAmount: 6,
          reversed: false,
          stops: [
            [0.2, '#7EAB55'],
            [0.4, '#FFFE55'],
            [0.6, '#F5C142'],
            [0.8, '#DF8244'],
            [1.0, '#B02418']
          ]
        },

        legend: {
          align: 'right',
          layout: 'vertical',
          margin: 0,
          verticalAlign: 'top',
          x: 15,
          y: 130,
          symbolHeight: 150
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
    }
  }
};
</script>

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

  data: () => ({
    customObjects: []
  }),

  computed: {
    chartOptions() {
      if (!this.data) {
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

    getXAxisProps(label, categories, interval) {
      return {
        type: 'datetime',
        categories: categories,
        title: {
          y: 0,
          style: {
            fontSize: 15,
            fontWeight: 'bold'
          },
          text: label
        },
        labels: {
          align: 'center',
          y: 14,
          style: {
            fontSize: 12,
            fontWeight: 'bold'
          },
          format: '{value:%H}',
          step: 3600 / interval
        }
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
        },
        gridZIndex: 10,
        gridLineWidth: 0.5,
        gridLineColor: 'black'
      };
    },

    getTooltip(point, startTime, interval) {
      const time = new Date(startTime.getTime() + point.x * interval * 1000);
      const timeStr = this.formatTime(time);
      let value = point.value;
      if (value == null || value < 0) {
        return `Time: ${timeStr}`;
      }
      return `Time: ${timeStr}, Value: ${value}`;
    },

    formatTime(d) {
      const h = d.getHours();
      const m = d.getMinutes();
      let str = ('0' + h).slice(-2) + ':' + ('0' + m).slice(-2);
      return str;
    },

    makeChart(chartHeight, data) {
      let ctx = this;

      let startTime = data.startTime;
      let interval = data.interval;

      // Add data to series
      let series = this.tranformSeries(data.data);
      let xAxis = this.getXAxisProps(data.xAxis, data.xcategories, interval);
      let yAxis = this.getYAxisProps(data.yAxis, data.ycategories);
      let colorAxis = data.colorAxis;
      let title = data.title;

      // Create chart instance
      return {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          spacingTop: 10,
          spacingBottom: 5,
          marginLeft: 165,
          marginRight: 85,
          marginBottom: 55,
          type: 'heatmap',
          plotBorderColor: '#000000',
          plotBorderWidth: 2
        },

        title: {
          style: {
            fontSize: '20px',
            fontWeight: 'bold'
          },
          margin: 10,
          text: title
        },
        xAxis: xAxis,
        yAxis: yAxis,

        colorAxis: { dataClasses: colorAxis },

        legend: {
          title: {
            text: 'Level'
          },
          align: 'right',
          verticalAlign: 'middle',
          floating: true,
          layout: 'vertical',
          x: -10,
          reversed: true,
          valueDecimals: 0,
          backgroundColor: 'rgba(255,255,255,0.9)',
          borderColor: '#C98657',
          borderWidth: 1,
          itemMarginBottom: 5,
          symbolRadius: 0,
          symbolHeight: 13
        },

        tooltip: {
          enabled: true,
          outside: true,
          formatter: function() {
            return ctx.getTooltip(this.point, startTime, interval);
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
            borderColor: 'rgba(51, 153, 0, 0.50)',
            turboThreshold: 0, //larger threshold or set to 0 to disable
            data: series,
            dataLabels: {
              enabled: false
            },
            nullColor: '#708090',
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

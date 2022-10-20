<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts :options="chartOptions" :callback="drawRowText" v-if="!reload"></highcharts>
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
    yLabelClicked: Function,
    titleClicked: Function
  },

  data: () => ({
    reload: false,
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
      let height = this.$vuetify.breakpoint.mobile ? 300 : this.height;
      let chart = this.makeChart(height, this.data, this.drawRowText);
      return chart;
    }
  },
  mounted() {
    this.$bus.$on('CHART_RELOAD', () => {
      this.refresh(500);
    });
  },
  methods: {
    tranformSeries(data) {
      if (data) {
        return data.map(item => {
          let x = item[0];
          let y = item[1];
          let value = item[2] > 0 ? item[2] : null;
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

    getYAxisProps(label, categories, devices) {
      const ctx = this;
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
            fontWeight: 'bold',
            cursor: 'pointer'
          },

          formatter: function() {
            if (devices[this.value]) {
              return `<div style="text-decoration: underline;">${this.value}</div>`;
            } else {
              return this.value;
            }
          },
          useHTML: true,
          events: {
            click: function() {
              const label = this.axis.categories[this.pos];
              ctx.yLabelClicked(label);
            }
          }
        },
        gridZIndex: 10,
        gridLineWidth: 0.5,
        gridLineColor: 'black'
      };
    },

    getChartTitle(title) {
      const ctx = this;
      return {
        style: {
          fontSize: '20px',
          fontWeight: 'bold',
          cursor: 'pointer'
        },
        margin: 10,
        text: title,
        events: {
          click: function() {
            if (ctx.titleClicked) {
              ctx.titleClicked(title);
            }
          },
          dblclick: () => {},
          contextmenu: () => {}
        }
      };
    },

    addYAxisClickHandler(categories) {
      let ctx = this;
      const elements = document.querySelectorAll('.highcharts-yaxis-labels');
      elements.forEach(element => {
        if (element.childElementCount > 0) {
          element.childNodes.forEach(label => {
            if (categories.includes(label.textContent)) {
              label.onclick = function() {
                ctx.yLabelClicked(this.textContent);
              };
            }
          });
        }
      });
    },

    getColorAxis() {
      return [
        { from: 1, to: 1, color: '#1B5E20', name: '1' },
        { from: 2, to: 2, color: '#81C784', name: '2' },
        { from: 3, to: 3, color: '#FDD835', name: '3' },
        { from: 4, to: 4, color: '#FFCC80', name: '4' },
        { from: 5, to: 5, color: '#FF9800', name: '5' },
        { from: 6, to: 6, color: '#EF9A9A', name: '6' },
        { from: 7, to: 7, color: '#F44336', name: '7' },
        { from: 8, to: 8, color: '#B71C1C', name: '8' }
      ];
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

    makeChart(chartHeight, data, redrawFunc) {
      let ctx = this;

      let startTime = data.startTime;
      let interval = data.interval;

      // Add data to series
      let series = this.tranformSeries(data.data);
      let title = this.getChartTitle(data.title || '');
      let xAxis = this.getXAxisProps(data.xAxis, data.xcategories, interval);
      let yAxis = this.getYAxisProps(data.yAxis, data.ycategories, data.devices);
      let colorAxis = this.getColorAxis();

      // Setup the y-axis click handler
      setTimeout(() => {
        this.addYAxisClickHandler(data.ycategories);
      }, 3000);

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
          plotBorderWidth: 2,
          events: {
            redraw: function() {
              redrawFunc(this);
            }
          }
        },

        title: title,
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
          backgroundColor: 'rgba(55,55,55,0.9)',
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
            borderWidth: 0.1,
            borderColor: '#64a4c1',
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
    },

    drawRowText(chart) {
      if (this.$vuetify.breakpoint.mobile) {
        return;
      }
      if (this.customObjects.length > 0) {
        Highcharts.each(this.customObjects, function(e) {
          e.destroy();
        });
        this.customObjects = [];
      }

      const startX = chart.xAxis[0].toPixels(0);
      this.data.descriptions.forEach((row, index) => {
        const x = startX + 20;
        const y = chart.yAxis[0].toPixels(index) + 5;
        const t = chart.renderer
          .text(row, x, y)
          .attr({ zIndex: 5 })
          .css({ color: 'white', opacity: 0.6, fontSize: '10px', fontWeight: 'normal' })
          .add();
        this.customObjects.push(t);
      });
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

<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts :options="chartOptions" v-if="!reload" :callback="drawGridLines"></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
//https://jsfiddle.net/BlackLabel/7hgr51sp/
//https://jsfiddle.net/gh/get/library/pure/highcharts/highcharts/tree/master/samples/highcharts/drilldown/async/
import Highcharts from 'highcharts';
export default {
  props: ['data', 'height', 'deviceClicked'],
  data: () => ({
    reload: false,
    flowChartParams: {},
    customObjects: []
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
      let height = this.$vuetify.breakpoint.mobile ? 300 : this.height;
      let chart = this.makeChart(height, this.data, this.drawGridLines);
      return chart;
    }
  },
  mounted() {
    this.$bus.$on('CHART_RELOAD', () => {
      this.refresh();
    });
  },
  methods: {
    async showHourlyBaseline(point) {
      if (point) {
        const hour = Math.floor(point.x / 12);
        const deviceId = this.data.ycategories[point.y];
        const direction = this.data.direction;
        const start = this.data.start;
        this.$emit('baseline', { deviceId, direction, start, hour });
      }
    },

    getPointCategoryName(point, dimension) {
      let series = point.series,
        isY = dimension === 'y',
        axis = series[isY ? 'yAxis' : 'xAxis'];
      return axis.categories[point[isY ? 'y' : 'x']];
    },

    tranformSeries(data) {
      if (data) {
        return data.map(item => {
          const x = item[0];
          const y = item[1];
          const value = item[2] >= 0 ? item[2] : null;
          return { x, y, value };
        });
      } else {
        return null;
      }
    },

    getXAxisProps(label, categories) {
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
          step: 12
        }
      };
    },

    getYAxisProps(label, categories, names) {
      return {
        categories: categories,
        reversed: true,
        title: {
          style: {
            fontSize: 13,
            fontWeight: 'bold'
          },
          text: label
        },
        labels: {
          enabled: true,
          formatter: function() {
            const name = names[this.pos];
            return '<span title="' + name + '">' + this.value + '</span>';
          },
          useHTML: true,
          style: {
            fontSize: 11,
            fontWeight: 'bold'
          }
        }
      };
    },

    getChartTitle(title) {
      return {
        style: {
          fontSize: '18px',
          fontWeight: 'bold'
        },
        text: title
      };
    },

    getColorAxis() {
      return [
        { from: 0, to: 0, color: '#196f3d', name: '0' },
        { from: 1, to: 1, color: '#B71C1C', name: '1' },
        { from: 3, to: 3, color: '#E57373', name: '3' },
        { from: 4, to: 4, color: '#FF9800', name: '4' },
        { from: 5, to: 5, color: '#FFCC00', name: '5' },
        { from: 6, to: 6, color: '#FDD835', name: '6' }
      ];
    },

    getTooltip(point, startTime) {
      const interval = 300;
      const time = new Date(startTime.getTime() + point.x * interval * 1000);
      const timeStr = this.formatTime(time);
      const permit = this.data.permitNumbers[point.y];
      let value = point.value;
      if (value == null || value < 0) {
        return `Time: ${timeStr}`;
      }
      return `Device: ${permit}, Time: ${timeStr}, Status: ${value}`;
    },

    formatTime(d) {
      const h = d.getHours();
      const m = d.getMinutes();
      let str = ('0' + h).slice(-2) + ':' + ('0' + m).slice(-2);
      return str;
    },

    getYAxisLabels() {
      let result = null;
      if (this.data) {
        let idx = this.data.index;
        let elements = document.querySelectorAll('.highcharts-yaxis-labels');
        if (idx < elements.length) {
          result = elements[idx].childNodes;
        }
        return result;
      }
    },

    addAxisClickHandler() {
      let labels = this.getYAxisLabels();
      if (labels) {
        let ctx = this;
        labels.forEach(label => {
          label.style.cursor = 'pointer';
          label.onclick = function() {
            ctx.deviceClicked(this.textContent);
          };
        });
      }
    },

    removeAxisClickHandler() {
      let labels = this.getYAxisLabels();
      if (labels) {
        labels.forEach(label => {
          label.style.cursor = 'none';
          label.onclick = '';
        });
      }
    },

    drawGridLines(chart) {
      if (this.customObjects.length > 0) {
        Highcharts.each(this.customObjects, function(e) {
          e.destroy();
        });
        this.customObjects = [];
      }

      // Draw horizontal grid lines
      const y0 = chart.yAxis[0].toPixels(-0.5);
      const y1 = chart.yAxis[0].toPixels(this.data.ycategories.length - 0.5);
      for (let i = 1; i < 24; i++) {
        const x = chart.xAxis[0].toPixels(i * 12);
        const t = chart.renderer
          .path(['M', x, y0, 'L', x, y1])
          .attr({ 'stroke-width': 2, stroke: '#64a4c1', zIndex: 1000 })
          .add();
        this.customObjects.push(t);
      }

      // Draw vertical grid lines
      const x0 = chart.xAxis[0].toPixels(-0.5);
      const x1 = chart.xAxis[0].toPixels(288 - 0.5);
      for (let i = 1; i < this.data.ycategories.length; i++) {
        const y = chart.yAxis[0].toPixels(i - 0.5);
        const t = chart.renderer
          .path(['M', x0, y, 'L', x1, y])
          .attr({ 'stroke-width': 2, stroke: '#64a4c1', zIndex: 1000 })
          .add();
        this.customObjects.push(t);
      }
    },

    makeChart(chartHeight, data, redrawFunc) {
      let ctx = this;

      const startTime = data.startTime;

      // Add data to series
      let series = this.tranformSeries(data.data);
      let title = this.getChartTitle(data.title || '');
      let xAxis = this.getXAxisProps(data.xAxis, data.xcategories);
      let yAxis = this.getYAxisProps(data.yAxis, data.permitNumbers, data.names);
      let colorAxis = this.getColorAxis();

      // Setup the y-axis click handler
      setTimeout(() => {
        this.addAxisClickHandler();
      }, 3000);

      // Create chart instance
      return {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          spacingTop: 20,
          spacingBottom: 15,
          marginLeft: this.$vuetify.breakpoint.mobile ? 60 : 110,
          marginRight: this.$vuetify.breakpoint.mobile ? 20 : 80,
          marginBottom: this.$vuetify.breakpoint.mobile ? 50 : 65,
          type: 'heatmap',
          plotBorderColor: '#DEDEDE',
          plotBorderWidth: 1,
          events: {
            redraw: function() {
              redrawFunc(this);
            }
          }
        },

        plotOptions: {
          series: {
            events: {
              click: function(e) {
                ctx.showHourlyBaseline(e.point);
              }
            }
          }
        },

        title: title,
        xAxis: xAxis,
        yAxis: yAxis,

        colorAxis: {
          dataClasses: colorAxis
        },

        legend: {
          enabled: this.$vuetify.breakpoint.mobile ? false : true,
          title: {
            text: 'Status'
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
            return ctx.getTooltip(this.point, startTime);
          }
        },

        navigation: {
          buttonOptions: {
            align: 'right',
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
            borderColor: '#196f3d',
            nullColor: '#708090',
            name: 'Heatmap',
            borderWidth: 0.1,
            turboThreshold: 10000, //larger threshold or set to 0 to disable
            data: series,
            dataLabels: {
              enabled: false
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

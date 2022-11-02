<template>
  <div>
    <div class="chart-title" v-if="title">
      {{ title }}
    </div>
    <div class="my-0 mx-0">
      <highcharts
        ref="highcharts"
        class="chart"
        :options="chartOptions"
        :callback="drawText"
        v-if="!reload"
      ></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: {
    data: Object,
    title: String,
    height: Number,
    left: { type: Number, default: 60 },
    legendy: Number,
    exporting: Boolean
  },

  data() {
    return {
      reload: false,
      signalPatterns: [],
      bands: [],
      customObjects: []
    };
  },

  computed: {
    chartOptions() {
      if (this.data === null || this.data === undefined) {
        return {
          credits: {
            enabled: false
          }
        };
      }
      let height = this.$vuetify.breakpoint.mobile ? 360 : this.height;
      return this.makeChart(height, this.data, this.drawText);
    }
  },

  mounted() {
    this.$bus.$on('CHART_RELOAD', () => {
      this.resize();
    });
  },

  methods: {
    refresh(ms = 1000) {
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, ms);
    },

    resize() {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        const chart = this.$refs.highcharts.chart;
        chart.reflow();
      }
    },

    drawText(chart) {
      if (this.customObjects.length > 0) {
        Highcharts.each(this.customObjects, function(e) {
          e.destroy();
        });
        this.customObjects = [];
      }

      if (this.signalPatterns) {
        const n = Math.floor(this.signalPatterns.length / 2);
        for (let i = 0; i < n; i++) {
          const p0 = this.signalPatterns[2 * i + 0];
          const p1 = this.signalPatterns[2 * i + 1];

          const x0 = chart.xAxis[0].toPixels(p0[0]);
          const x1 = chart.xAxis[0].toPixels(p1[0]);

          if (Math.abs(x1 - x0) > 30) {
            const x = Math.floor((x0 + x1) / 2) - 20;
            const y = chart.yAxis[1].toPixels(p0[1]) - 5;
            const text = `${p0[3]} - ${p0[1]} ${p0[2]}`;

            const t = chart.renderer
              .text(text, x, y)
              .attr({ zIndex: 20 })
              .css({ color: '#aaa', opacity: 1.0, fontSize: '13px', fontWeight: 'bold' })
              .add();
            this.customObjects.push(t);
          }
        }
      }

      if (this.bands) {
        const xx = chart.xAxis[0].toPixels(this.signalPatterns[0][0]) + 15;
        const n = this.bands.length;
        for (let i = 0; i < n; i++) {
          const y0 = chart.yAxis[0].toPixels(this.bands[i].from) - 3;
          const y1 = chart.yAxis[0].toPixels(this.bands[i].to) + 10;

          if (Math.abs(y1 - y0) > 5) {
            const text0 = this.bands[i].from + '';
            const text1 = this.bands[i].to + '';

            const t0 = chart.renderer
              .text(text0, xx, y0)
              .attr({ zIndex: 20 })
              .css({ color: '#fff', opacity: 1.0, fontSize: '10px', fontWeight: 'bold' })
              .add();
            this.customObjects.push(t0);

            const t1 = chart.renderer
              .text(text1, xx, y1)
              .attr({ zIndex: 20 })
              .css({ color: '#fff', opacity: 1.0, fontSize: '10px', fontWeight: 'bold' })
              .add();
            this.customObjects.push(t1);
          }
        }
      }
    },

    prepareSeries(data) {
      let series = [];
      if (data) {
        data.forEach(item => {
          if (item.data !== null) {
            series.push({
              type: 'line',
              name: item.name,
              color: item.color,
              data: item.data,
              lineWidth: 1.5,
              enableMouseTracking: item.tracking
            });
          }
        });

        if (this.signalPatterns) {
          series[2]['yAxis'] = 1;
        }
      }
      return series;
    },

    makeChart(chartHeight, data, redrawFunc) {
      this.signalPatterns = data.patterns;
      this.bands = data.bands;

      // Add data to series
      let series = data.series ? data.series : this.prepareSeries(data.data);
      let title = data.title || '';
      let subtitle = data.subtitle || null;
      let xAxis = data.xAxis;
      // let yAxis = data.yAxis;
      let ly = this.legendy || 50;
      let exporting = this.exporting === undefined ? false : this.exporting;
      let marginLeft = this.left;

      let plotLines =
        data.timeSlots &&
        data.timeSlots.map(time => ({
          color: '#AAAAAA',
          width: 2,
          value: time,
          dashStyle: 'Dot'
        }));

      let plotBands = data.bands;

      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          spacingTop: 30,
          spacingBottom: 20,
          marginLeft,
          marginRight: 40,
          type: 'line',
          zoomType: 'xy',
          plotBorderColor: '#DEDEDE',
          plotBorderWidth: 1,
          events: {
            redraw: function() {
              redrawFunc(this);
            }
          }
        },

        title: {
          style: {
            fontSize: '18px',
            fontWeight: 'bold'
          },
          text: title
        },
        subtitle: {
          text: subtitle
        },
        xAxis: {
          type: 'datetime',
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'bold'
            },
            text: xAxis
          },
          min: data.xmin,
          max: data.xmax,
          plotLines: plotLines,
          plotBands: plotBands
        },
        yAxis: [
          {
            // left y axis
            title: {
              text: 'V + O (%)',
              style: {
                fontSize: 13,
                fontWeight: 'bold'
              },
              x: -10
            },
            labels: {
              align: 'left',
              // x: 3,
              // y: 16,
              format: '{value:.,0f}'
            },
            // min: 0,
            // max: 80,
            maxPadding: 0.2,
            plotBands: plotBands,
            showFirstLabel: false,
            allowDecimals: false
          },
          {
            opposite: true,
            title: {
              text: 'Cycle Length (seconds)',
              style: {
                fontSize: 13,
                fontWeight: 'bold'
              }
            },
            labels: {
              align: 'right',
              x: -3,
              y: 16,
              format: '{value:.,0f}'
            },
            // min: 90,
            // max: 180,
            maxPadding: 0.2,
            showFirstLabel: false,
            allowDecimals: false
          }
        ],
        tooltip: {
          shared: true,
          crosshairs: true
        },
        legend: {
          enabled: series.length > 0,
          floating: true,
          verticalAlign: 'top',
          align: 'left',
          x: 80,
          y: ly
        },

        navigation: {
          buttonOptions: {
            align: 'left',
            verticalAlign: 'bottom'
          }
        },
        exporting: {
          enabled: exporting,
          buttons: {
            contextButton: {
              menuItems: ['viewFullscreen', 'separator', 'downloadPNG', 'downloadJPEG', 'downloadPDF']
            }
          }
        },
        series: series
      };
      return chart;
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

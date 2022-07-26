<template>
  <div>
    <div class="chart-title" v-if="title">
      {{ title }}
    </div>
    <div class="my-0 mx-0">
      <highcharts ref="highcharts" class="chart" :options="chartOptions" v-if="!reload"></highcharts>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    data: Object,
    title: String,
    height: Number,
    left: { type: Number, default: 90 },
    legendy: Number,
    exporting: Boolean
  },

  data() {
    return {
      reload: false
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
      return this.makeChart(this.height, this.data);
    }
  },

  mounted() {
    this.$bus.$on('CHART_RELOAD', () => {
      this.resize();
    });
  },

  methods: {
    refresh() {
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, 100);
    },

    resize() {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        const chart = this.$refs.highcharts.chart;
        chart.reflow();
      }
    },

    setYAxisRange(min, max) {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        const chart = this.$refs.highcharts.chart;
        chart.yAxis[0].setExtremes(min, max);
      }
    },

    updateScaling() {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        this.$refs.highcharts.chart.updateContainerScaling();
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
              lineWidth: 1.5
            });
          }
        });
      }
      return series;
    },

    makeChart(chartHeight, data) {
      // Add data to series
      let series = data.series ? data.series : this.prepareSeries(data.data);
      let title = data.title || '';
      let subtitle = data.subtitle || null;
      let xAxis = data.xAxis;
      let yAxis = data.yAxis;
      let ly = this.legendy || 45;
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
          plotBorderWidth: 1
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
        yAxis: {
          allowDecimals: false,
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'bold'
            },
            text: yAxis
          },
          min: data.ymin,
          max: data.ymax
        },
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

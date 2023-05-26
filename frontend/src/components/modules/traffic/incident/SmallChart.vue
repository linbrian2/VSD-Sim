<template>
  <div>
    <div class="chart-title" v-if="title">
      {{ title }}
    </div>
    <div class="my-0 mx-0">
      <highcharts ref="highcharts" class="chart" :options="chartOptions" v-if="!reload"></highcharts>
      <v-card v-else loading :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
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
    exporting: Boolean,
    xType: String
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
      let height = this.$vuetify.breakpoint.mobile ? 300 : this.height;
      return this.makeChart(height, this.data);
    }
  },

  mounted() {
    this.$bus.$on('CHART_RELOAD', () => {
      this.resize();
      this.refresh(500);
    });
  },

  methods: {
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
      let ly = this.legendy || 45;
      let exporting = this.exporting === undefined ? false : this.exporting;
      // let marginLeft = this.left;

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
          spacingTop: -25,
          spacingBottom: 10,
          marginLeft: 40,
          marginRight: 10,
          type: 'line',
          zoomType: 'xy',
          plotBorderColor: '#DEDEDE',
          plotBorderWidth: 1
        },

        title: {
          style: {
            fontSize: '20px',
            fontWeight: 'bold'
          },
          text: title,
          align: 'center',
          y: 60,
          x: 15
        },
        subtitle: {
          text: subtitle
        },
        xAxis: {
          gridLineWidth: 0,
          type: this.xType ? this.xType : 'datetime',
          // title: {
          //   style: {
          //     fontSize: 13,
          //     fontWeight: 'bold'
          //   },
          //   text: xAxis
          // },
          min: data.xmin,
          max: data.xmax,
          plotLines: plotLines,
          plotBands: plotBands,
          tickInterval: 100
        },
        yAxis: {
          gridLineWidth: 0,
          allowDecimals: false,
          // title: {
          //   style: {
          //     fontSize: 13,
          //     fontWeight: 'bold'
          //   },
          //   text: yAxis
          // },
          min: data.ymin,
          max: data.ymax
        },
        tooltip: {
          shared: true,
          crosshairs: true
        },
        legend: {
          enabled: false,
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
    },
    refresh(ms = 1000) {
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, ms);
    }
  },
  /*       
  <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
  */

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

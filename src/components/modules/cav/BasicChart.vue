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
  props: ['data', 'title', 'height', 'legendy', 'exporting'],
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
  methods: {
    resize(width) {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        this.$refs.highcharts.chart.setSize(width, null);
      }
    },

    updateChartVerticalLine(time) {
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        const chart = this.$refs.highcharts.chart;
        chart.series[0].xAxis.options.plotLines[0].value = time;
        chart.series[0].xAxis.update();
      }
    },

    updateChartData(data) {
      // https://stackoverflow.com/questions/36075039/how-to-use-highcharts-to-implement-this-dynamically-shifting-from-right-to-left
      if (this.$refs.highcharts && this.$refs.highcharts.chart) {
        const chart = this.$refs.highcharts.chart;
        if (chart && data.length > 0) {
          const series = chart.series[0];
          for (var i = 0; i < data.length; i++) {
            const redraw = i > data.length - 2; // only redraw when the last point was added
            const shift = series.data.length > 500; // shift if the series is longer than 20
            series.addPoint(data[i], redraw, shift);
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
              lineWidth: 1.5
            });
          }
        });
      }
      return series;
    },

    makeChart(chartHeight, data) {
      // Add data to series
      let series = this.prepareSeries(data.data);
      let title = data.title || '';
      let xAxis = data.xAxis;
      let yAxis = data.yAxis;
      let ly = this.legendy || 45;
      let exporting = this.exporting === undefined ? false : this.exporting;

      // let plotLines =
      //   data.timeSlots &&
      //   data.timeSlots.map(time => ({
      //     color: '#AAAAAA',
      //     width: 2,
      //     value: time,
      //     dashStyle: 'Dot'
      //   }));

      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          spacingTop: 30,
          spacingBottom: 25,
          marginLeft: 80,
          marginRight: 35,
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
        xAxis: {
          type: 'datetime',
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'bold'
            },
            text: xAxis
          },
          plotLines: [
            {
              color: '#FF0000',
              width: 2,
              value: 0
            }
          ]
        },

        yAxis: {
          allowDecimals: true,
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'bold'
            },
            text: yAxis
          }
        },
        tooltip: {
          shared: true,
          crosshairs: true
        },
        legend: {
          enabled: series.length > 1,
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
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, 1);
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

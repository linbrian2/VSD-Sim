<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts :options="chartOptions" v-if="!reload"></highcharts>
    </div>
  </div>
</template>

<script>
export default {
  props: ['data', 'height', 'width'],
  data() {
    return {
      reload: false
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
    makeChart(chartHeight, data) {
      const series = data.series;
      const title = data.title;

      // Create chart instance
      return {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          width: this.width || 440,
          spacingTop: 10,
          spacingBottom: 15,
          plotBackgroundColor: null,
          plotShadow: false,
          type: 'pie'
        },

        title: {
          style: {
            fontSize: '15px',
            fontWeight: 'bold'
          },
          margin: 5,
          text: title
        },

        tooltip: {
          pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },

        accessibility: {
          point: {
            valueSuffix: '%'
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

        plotOptions: {
          pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
              enabled: true,
              format: '<b>{point.name}</b>: {point.percentage:.1f} %'
            }
          }
        },

        series: [
          {
            name: 'Errors',
            colorByPoint: true,
            data: series
          }
        ]
      };
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

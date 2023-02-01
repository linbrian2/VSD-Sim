<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts :options="chartOptions" v-if="!reload"></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
export default {
  props: ['data', 'height'],
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
          spacingTop: 25,
          spacingBottom: 25,
          marginLeft: 25,
          marginRight: 25,
          plotBorderColor: 'grey',
          plotBorderWidth: 2,
          plotBackgroundColor: null,
          plotShadow: false,
          type: 'pie'
        },

        title: {
          style: {
            fontSize: '15px',
            fontWeight: 'bold'
          },
          margin: 15,
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
            size: '70%',
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

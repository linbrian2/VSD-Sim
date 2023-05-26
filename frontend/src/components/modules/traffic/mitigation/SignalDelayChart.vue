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
  props: {
    data: Object,
    height: Number
  },

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

  watch: {
    '$store.state.navigationWidth'() {
      this.refresh();
    },
    '$store.state.darkMode'() {
      this.refresh(1);
    }
  },

  methods: {
    refresh(ms = 1000) {
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, ms);
    },
    makeChart(chartHeight, data) {
      const series = data.series;
      const title = data.title;
      const categories = data.categories;
      const xAxis = data.xAxis;
      const yAxis = data.yAxis;
      const colors = data.colors;

      // Create chart instance
      return {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          spacingTop: 20,
          spacingBottom: 15,
          marginRight: 30,
          plotBorderColor: '#DEDEDE',
          plotBorderWidth: 1,
          plotBackgroundColor: null,
          plotShadow: false,
          type: 'column'
        },

        title: {
          style: {
            fontSize: '16px',
            fontWeight: 'bold'
          },
          margin: 5,
          text: title
        },

        xAxis: {
          title: {
            style: {
              fontSize: 13,
              fontWeight: 'bold'
            },
            text: xAxis
          },
          categories,
          crosshair: true
        },

        yAxis: {
          title: {
            style: {
              fontSize: 12,
              fontWeight: 'normal'
            },
            text: yAxis
          }
        },

        tooltip: {
          headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
          pointFormat:
            '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:.1f} s</b></td></tr>',
          footerFormat: '</table>',
          shared: true,
          useHTML: true
        },

        legend: {
          enabled: true,
          floating: true,
          verticalAlign: 'top',
          align: 'left',
          x: 60,
          y: 30
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
          column: {
            pointPadding: 0.2,
            borderWidth: 0.5
          }
        },

        colors,
        series
      };
    }
  }
};
</script>

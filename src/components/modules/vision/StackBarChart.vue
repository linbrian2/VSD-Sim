<template>
  <div>
    <div class="chart-title" v-if="title">{{ title }}</div>
    <div class="my-0 mx-0">
      <highcharts
        class="chart"
        :options="chartOptions"
        v-if="data !== null && data.data !== undefined && !reload"
      ></highcharts>
      <v-card v-else :height="height" :color="$store.state.darkMode ? 'rgb(51,51,51)' : 'rgb(255,255,255)'" />
    </div>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: ['data', 'interval', 'title', 'height'],
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
      let height = this.$vuetify.breakpoint.mobile ? 300 : this.height;
      return this.makeChart(height, this.data, this.interval);
    }
  },
  methods: {
    prepareSeries(data) {
      let perShapeGradient = { x1: 0, y1: 0, x2: 1, y2: 0 };
      let colors = Highcharts.getOptions().colors;
      colors = [
        {
          linearGradient: Highcharts.merge(perShapeGradient),
          stops: [
            [0, 'rgb(136, 219, 5)'],
            [1, 'rgb(112, 180, 5)']
          ]
        },
        {
          linearGradient: Highcharts.merge(perShapeGradient),
          stops: [
            [0, 'rgb(120, 202, 248)'],
            [1, 'rgb(46, 150, 208)']
          ]
        },
        {
          linearGradient: perShapeGradient,
          stops: [
            [0, 'rgb(247, 111, 111)'],
            [1, 'rgb(220, 54, 54)']
          ]
        },
        {
          linearGradient: perShapeGradient,
          stops: [
            [0, 'rgb(125, 60, 152)'],
            [1, 'rgb(100, 55, 100)']
          ]
        }
      ];

      let series = [];
      data.forEach((item, idx) => {
        if (item.data !== null) {
          series.push({
            name: item.name,
            color: colors[idx],
            data: item.data,
            lineWidth: 1.5
          });
        }
      });
      return series;
    },

    makeChart(chartHeight, data, offset) {
      // Add data to series
      let series = this.prepareSeries(data.data);
      let title = data.title;
      let xAxis = data.xAxis;
      let yAxis = data.yAxis;

      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          spacingTop: 30,
          spacingBottom: 25,
          marginLeft: 90,
          marginRight: 40,
          alignTicks: false,
          panning: true,
          panKey: 'shift',
          type: 'column',
          zoomType: 'xy',
          plotBorderColor: '#DEDEDE',
          plotBorderWidth: 1
        },
        plotOptions: {
          column: {
            stacking: 'normal',
            dataLabels: {
              enabled: false,
              color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
            }
          }
        },
        title: {
          style: {
            fontSize: 16,
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
          }
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
          min: 0,
          stackLabels: {
            enabled: true,
            style: {
              fontWeight: 'bold',
              color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
            }
          }
        },
        tooltip: {
          shared: true,
          useHTML: true,
          formatter: function() {
            var year = Highcharts.dateFormat('%Y-%m-%d', new Date(this.x));
            var startTime = Highcharts.dateFormat('%H:%M', new Date(this.x));
            var endTime = Highcharts.dateFormat('%H:%M', new Date(this.x + offset));

            var tooltip = '<table class="tip"><tbody>';
            tooltip +=
              '<tr><th style="color: ' +
              '#000' +
              '">' +
              year +
              '</th>' +
              '<td style="text-align: right">' +
              startTime +
              ' - ' +
              endTime +
              '</td></tr>';

            this.points.forEach(function(point) {
              tooltip +=
                '<tr><td style="font-weight:bold; color:' +
                point.series.color +
                '">' +
                point.series.name +
                ':</td><td style="text-align: right"> ' +
                point.y +
                '</td></tr>';
            });

            tooltip += '<tr><th>Total: </th><td style="text-align:right"><b>' + this.points[0].total + '</b></td></tr>';
            tooltip += '</tbody></table>';

            return tooltip;
          }
        },
        legend: {
          enabled: true,
          floating: true,
          verticalAlign: 'top',
          align: 'left',
          x: 80,
          y: 10
        },
        exporting: {
          enabled: false
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

<template>
  <div>
    <div class="my-0 mx-0">
      <highcharts ref="highcharts" :options="chartOptions"></highcharts>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    data: Object,
    height: Number
  },

  computed: {
    chartOptions() {
      if (!this.data) {
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
        series[1]['yAxis'] = 1;
      }
      return series;
    },

    makeChart(chartHeight, data) {
      // Add data to series
      let series = this.prepareSeries(data.data);
      let title = data.title || '';
      let xAxis = data.xAxis;

      let plotLines =
        data.timeSlots &&
        data.timeSlots.map(time => ({
          color: '#AAAAAA',
          width: 2,
          value: time,
          dashStyle: 'Dot'
        }));

      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },

        chart: {
          height: chartHeight,
          spacingTop: 15,
          spacingBottom: 15,
          marginLeft: 50,
          marginRight: 45,
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
          plotLines: plotLines
        },

        yAxis: [
          {
            // left y axis
            title: {
              text: 'Speed (MPH)',
              style: {
                fontSize: 13,
                fontWeight: 'bold'
              }
            },
            labels: {
              align: 'left',
              x: 3,
              y: 16,
              format: '{value:.,0f}'
            },
            showFirstLabel: false
          },
          {
            opposite: true,
            title: {
              text: 'Volume (veh./5m)',
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
            showFirstLabel: false
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
          x: 50,
          y: 10
        },

        exporting: {
          enabled: false
        },

        series: series
      };
      return chart;
    }
  }
};
</script>

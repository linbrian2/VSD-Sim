<template>
  <div>
    <div class="chart-title" v-if="title">{{ title }}</div>
    <div class="my-0 mx-0" v-if="!$vuetify.breakpoint.mobile">
      <highcharts class="chart" :options="chartOptions" :callback="drawPlan" v-show="info != null"></highcharts>
    </div>
    <div class="my-0 mx-0" v-else>
      <highcharts class="chart" :options="chartOptions" v-show="info != null"></highcharts>
    </div>
  </div>
</template>

<script>
import Utils from '@/utils/Utils.js';
import Highcharts from 'highcharts';
export default {
  props: ['info', 'date', 'title', 'phase', 'height'],
  data: () => ({
    isReady: false,
    customObjects: []
  }),
  computed: {
    formatedDate() {
      return Utils.formatDate(this.date);
    },
    chartOptions() {
      if (this.info == null) {
        return {
          credits: {
            enabled: false
          }
        };
      }
      let color = this.darkColorScheme();
      let series = this.prepareData(this.info, color.detectorColor);
      return this.makeChart(this.height, series, color, this.drawPlan);
    }
  },
  filters: {
    format: function(d) {
      return Utils.formatDate(d);
    }
  },
  methods: {
    prepareData(data, detectorColor) {
      // Add data to series
      let series = [];
      series.push({
        type: 'scatter',
        name: 'Detector Activation',
        color: detectorColor,
        data: data.pointSeries,
        marker: { radius: 1.0, symbol: 'circle' }
      });
      series.push({
        type: 'line',
        name: 'Change to green',
        color: '#008000',
        data: data.greenSeries,
        lineWidth: 1.5
      });
      series.push({
        type: 'line',
        name: 'Change to yellow',
        color: '#FFFF00',
        data: data.yellowSeries,
        lineWidth: 1.5
      });

      series.push({
        type: 'line',
        name: 'Change to red',
        color: '#FF0000',
        data: data.redSeries,
        lineWidth: 1.5
      });
      return series;
    },

    normalColorScheme() {
      return {
        backgroundColor: '#eee',
        plotBorderColor: '#000',
        titleColor: '#000',
        labelColor: '#333',
        gridLineColor: '#888',
        legendBackgroundColor: '#FFFFFF',
        detectorColor: '#000'
      };
    },

    darkColorScheme() {
      return {
        backgroundColor: '#000',
        plotBorderColor: '#fff',
        titleColor: '#fff',
        labelColor: '#fff',
        gridLineColor: '#888',
        legendBackgroundColor: '#333',
        detectorColor: '#ccc'
      };
    },

    makeChart(chartHeight, series, colorScheme, redrawFunc) {
      // Create chart instance
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          marginTop: this.$vuetify.breakpoint.mobile ? 40 : 120,
          marginLeft: 90,
          marginRight: 50,
          spacingTop: 5,
          spacingBottom: 10,
          zoomType: 'xy',
          plotBorderColor: colorScheme.plotBorderColor,
          plotBorderWidth: 1,
          backgroundColor: colorScheme.backgroundColor,
          plotBackgroundColor: '#000',
          events: {
            redraw: function() {
              redrawFunc(this);
            }
          }
        },
        plotOptions: {
          series: {
            enableMouseTracking: false,
            shadow: false,
            states: {
              hover: {
                lineWidth: 1
              }
            }
          }
        },
        title: {
          text: null
        },
        xAxis: [
          {
            type: 'datetime',
            tickInterval: 3600 * 1000,
            pointPlacement: 'on',
            gridLineWidth: 1,
            gridLineColor: colorScheme.gridLineColor,
            gridLineDashStyle: 'Dot',
            title: {
              style: {
                color: colorScheme.titleColor,
                fontSize: 12,
                fontWeight: 'bold'
              },
              text: 'Time (Hour of Day)'
            },
            labels: {
              style: {
                color: colorScheme.labelColor,
                fontSize: 12
              },
              formatter: function() {
                return Highcharts.dateFormat('%H', this.value);
              }
            }
          },
          {
            type: 'datetime',
            tickInterval: 3600 * 1000,
            labels: {
              style: {
                color: colorScheme.labelColor,
                fontSize: 12
              },
              formatter: function() {
                return Highcharts.dateFormat('%H', this.value);
              }
            },
            linkedTo: 0,
            opposite: true
          }
        ],
        yAxis: [
          {
            allowDecimals: false,
            gridLineColor: colorScheme.gridLineColor,
            gridLineDashStyle: 'Dot',
            max: 200,
            min: 0,
            tickInterval: 30,
            labels: {
              style: {
                color: colorScheme.labelColor,
                fontSize: 12
              }
            },
            title: {
              style: {
                color: colorScheme.titleColor,
                fontSize: 12,
                fontWeight: 'bold'
              },
              text: 'Cycle Time (s)'
            }
          }
        ],
        tooltip: {
          shared: true,
          crosshairs: true,
          formatter: () => {
            let s = 'Time: ' + Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', new Date(this.x));
            this.points.forEach(function() {
              s += '<br/>' + this.series.name + ': ' + this.y + 's';
            });
            return s;
          }
        },
        legend: {
          enabled: true,
          layout: 'vertical',
          align: 'right',
          verticalAlign: 'top',
          floating: true,
          itemMarginBottom: 4,
          borderColor: colorScheme.plotBorderColor,
          borderWidth: 1,
          backgroundColor: colorScheme.legendBackgroundColor,
          x: -50,
          y: 155,
          itemStyle: {
            color: colorScheme.labelColor,
            fontSize: '10px'
          }
        },
        exporting: {
          enabled: false,
          buttons: {
            contextButton: {
              align: 'left',
              verticalAlign: 'bottom'
            },
            exportButton: {
              enabled: true
            },
            printButton: {
              enabled: false
            }
          }
        },
        series: series
      };
      return chart;
    },

    drawPlan(chart) {
      let plans = this.info.planSeries;

      if (this.customObjects.length > 0) {
        Highcharts.each(this.customObjects, function(e) {
          e.destroy();
        });
        this.customObjects = [];
      }

      plans.forEach(plan => {
        let ss = (plan[2] - plan[1]) / 1000;
        if (ss > 3600) {
          let x0 = chart.xAxis[0].toPixels(plan[1]);
          let y0 = 10;
          let x1 = chart.xAxis[0].toPixels(plan[2]);
          let y1 = 85;
          let r = chart.renderer
            .rect(x0, y0, x1 - x0, y1 - y0, 1)
            .attr({
              'stroke-width': 1,
              stroke: 'black',
              fill: '#444',
              zIndex: 4
            })
            .add();
          this.customObjects.push(r);

          let tm = 0;
          let bm = 5;
          let th = (y1 - y0 - tm - bm) / 4;
          const labels = [plan[0], plan[3] + '% AoG', plan[4] + '% GT', plan[5].toFixed(2) + ' PR'];
          for (let i = 0; i < 4; i++) {
            let t = this.addCenteredText(
              chart,
              x0,
              x1,
              y0 + tm + (i + 1) * th,
              labels[i],
              i === 0 ? '14px' : '10px',
              'white'
            );
            this.customObjects.push(t);
          }
        }
      });

      this.drawDateAndPhase(chart);
    },

    drawDateAndPhase(chart) {
      let x0 = 0;
      let x1 = 90;
      let r = chart.renderer
        .rect(x0, 10, x1, 75, 1)
        .attr({
          'stroke-width': 1,
          stroke: 'black',
          zIndex: 4
        })
        .add();
      this.customObjects.push(r);

      let d = this.formatedDate;
      let t1 = this.addCenteredText(chart, x0, x1, 30, d, '9px', 'white');
      this.customObjects.push(t1);

      let p = 'P' + this.phase;
      let t2 = this.addCenteredText(chart, x0, x1, 50, p, '14px', 'white');
      this.customObjects.push(t2);
    },

    addCenteredText(chart, x0, x1, y, text, fontSize, color = 'black') {
      let t = chart.renderer
        .text(text)
        .attr({ zIndex: 5 })
        .css({ color: color, fontSize: fontSize, fontWeight: 'bold' })
        .add();
      let textBBox = t.getBBox();
      let x = x0 + (x1 - x0) * 0.5 - textBBox.width * 0.5;
      t.attr({ x: x, y: y });
      return t;
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
.phase-label {
  font-size: 1rem;
  font-weight: normal;
  margin-right: 10px;
}
</style>

<template>
  <div>
    <highcharts ref="highcharts" class="chart" :options="chartOptions" v-show="dataAvailable"></highcharts>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: {
    data: Array,
    speed: { type: Number, default: 45 },
    title: { type: String, default: 'Coordinate Group' }
  },

  data: () => ({
    barHeight: 0.04,
    loading: false
  }),

  computed: {
    dataAvailable() {
      return this.data && this.data.length > 0 && !this.loading;
    },

    height() {
      return 120 * this.data.length;
    },

    chartOptions() {
      if (!this.data || this.data.length === 0) {
        return {
          credits: {
            enabled: false
          }
        };
      }
      let d = this.prepareData(this.data);
      return this.makeChart(this.height, d.series, d.categories, d.ticks, this.title, d.start);
    }
  },

  methods: {
    prepareData(data) {
      this.loading = true;

      // Add data to series
      let series = [];
      let categories = [];
      let ticks = [];
      let start = Number.MAX_SAFE_INTEGER;

      if (data) {
        // Compute total distance
        const total = data.reduce((sum, v) => (sum += v.distance), 0);
        const length = data.length;

        const P2s = [];
        const P6s = [];
        const dists = [];

        let h = 0;
        data.forEach(item => {
          h += (item.distance * length) / total;
          ticks.push(h);
          categories.push(item.name);

          if (item.P2 && item.P2.length > 0) {
            const ts = item.P2[0].filter(item => item > 0);
            start = Math.min(start, ...ts);
          }

          const offset = this.barHeight;
          const P2 = this.composeCycleSeries(h, item.P2, -offset);
          const P6 = this.composeCycleSeries(h, item.P6, offset);

          P2s.push(P2);
          P6s.push(P6);
          dists.push(item.distance);

          series = series.concat(P2.concat(P6));
        });

        const startIdx = 0;
        const SPEED = this.speed / 2.237; // (m/s)

        // P6 - South Bound
        for (let i = startIdx + 1; i < P6s.length; i++) {
          const t = Math.round((dists[i] * 1000) / SPEED);
          const B = this.findThroughputBand(P6s[i - 1], P6s[i], t, 'SB');
          if (B.length > 0) {
            series = series.concat(B);
          }
        }

        // P2 - North Bound
        for (let i = P2s.length - 1; i > startIdx; i--) {
          const t = Math.round((dists[i] * 1000) / SPEED);
          const B = this.findThroughputBand(P2s[i], P2s[i - 1], t, 'NB');
          if (B.length > 0) {
            series = series.concat(B);
          }
        }
      }

      this.loading = false;

      return { series, categories, ticks, start };
    },

    findThroughputBand(startPolygonList, endPolygonList, dt, direction) {
      const result = [];

      if (startPolygonList.length > 0 && endPolygonList.length > 0) {
        const color = direction === 'NB' ? 'rgba(0,118,230,0.5)' : 'rgba(0,230,118,0.5)';

        // For all green bands in the polygon list
        const v0 = startPolygonList.filter(polygon => polygon.name.startsWith('G')).map(polygon => polygon.data);
        const v1 = endPolygonList.filter(polygon => polygon.name.startsWith('G')).map(polygon => polygon.data);
        const segments = v1.map(item => [item[0][0], item[1][0]]);

        let h0 = direction === 'NB' ? v0[0][0][1] + 2 * this.barHeight : v0[0][0][1];
        let h1 = direction === 'NB' ? v0[0][1][1] + 2 * this.barHeight : v0[0][1][1];
        let h2 = direction === 'NB' ? v1[0][0][1] + 2 * this.barHeight : v1[0][0][1];
        let h3 = direction === 'NB' ? v1[0][1][1] + 2 * this.barHeight : v1[0][1][1];

        for (let i = 0; i < v0.length; i++) {
          let startT = v0[i][0][0];
          let endT = v0[i][1][0];

          const band = this.createBand(startT, endT, dt, segments);
          if (band.length === 4) {
            result.push(
              this.createPolygon('B' + i, color, 1, [
                [band[0], h0],
                [band[1], h1],
                [band[3], h3],
                [band[2], h2]
              ])
            );
          }
        }
      }

      return result;
    },

    createBand(startT, endT, dt, segments) {
      // Compose band
      // ---- t0 ----- t1 -----
      //       |        |
      //       |        |
      //------t2 ----- t3 -----
      //
      let t0 = startT;
      let t1 = endT;
      let t2 = t0 + dt;
      let t3 = t1 + dt;

      // Initially set all number to be valid
      const values = [1, 1, 1, 1];

      // Find segment index from the green band segments for t2
      let segmentIdx = this.getSegmentIndex(t2, segments);
      if (segmentIdx < 0) {
        // No direct green band found, we need go to next consecutive band
        const { idx, t } = this.closestSegmentPoint(t2, segments, 1);
        if (t === null) {
          // No any band found, set the number to be invalid
          values[2] = 0;
        } else {
          segmentIdx = idx;

          // Modify the t0 and t2 accordingly
          t0 = t - dt;
          t2 = t;

          // Make sure the modified t0 should be within the original band [startT, endT]
          if (t0 > endT) {
            values[0] = 0;
          }
        }
      }

      // Find segment index from the green band segments for t3
      const segmentIdx1 = this.getSegmentIndex(t3, segments);
      if (segmentIdx1 < 0) {
        const point = this.closestSegmentPoint(t3, segments, 0);
        const t = point.t;
        if (t === null) {
          // No any band found, set the number to be invalid
          values[3] = 0;
        } else {
          // Modify the t1 and t3 accordingly
          t1 = t - dt;
          t3 = t;

          // Make sure the modified t1 should be within the original band [startT, endT]
          if (t1 > endT) {
            values[1] = 0;
          }
        }
      } else if (segmentIdx1 != segmentIdx && segmentIdx >= 0) {
        // Make sure we use the same band
        t3 = segments[segmentIdx][1];
        t1 = t3 - dt;
        if (t1 > endT || t1 < startT) {
          values[1] = 0;
        }
      }

      if (values.every(value => value > 0)) {
        return [t0, t1, t2, t3];
      } else {
        return [];
      }
    },

    getSegmentIndex(x, segments) {
      let index = -1;
      for (let i = 0; i < segments.length; i++) {
        if (x >= segments[i][0] && x <= segments[i][1]) {
          index = i;
          break;
        }
      }
      return index;
    },

    closestSegmentPoint(x, segments, offset) {
      let idx = null;
      let t = null;
      for (let i = 0; i < segments.length - 1; i++) {
        if (segments[i][1] < x && x < segments[i + 1][0]) {
          idx = offset > 0 ? i + 1 : i;
          t = offset > 0 ? segments[idx][0] : segments[idx][1];
          break;
        }
      }
      return { idx, t };
    },

    composeCycleSeries(h, cycles, offset) {
      let series = [];
      if (cycles) {
        let h1 = h + offset - this.barHeight;
        let h2 = h + offset + this.barHeight;

        for (let i = 0; i < cycles.length; i++) {
          let t0 = cycles[i][0];
          let t1 = cycles[i][1];
          let t2 = cycles[i][2];
          let t3 = cycles[i][3];

          if (!t0 || !t1 || !t2 || !t3) {
            console.log(t0, t1, t2, t3);
          }

          // Red
          if (t0 > 0 && t1 > 0) {
            series.push(
              this.createPolygon('R' + i, '#dd0000', 10, [
                [t0, h1],
                [t1, h1],
                [t1, h2],
                [t0, h2]
              ])
            );
          }

          // Green
          if (t1 > 0 && t2 > 0) {
            series.push(
              this.createPolygon('G' + i, '#00dd00', 10, [
                [t1, h1],
                [t2, h1],
                [t2, h2],
                [t1, h2]
              ])
            );
          }

          // Yellow
          if (t2 > 0 && t3 > 0) {
            series.push(
              this.createPolygon('Y' + i, '#FFFF00', 10, [
                [t2, h1],
                [t3, h1],
                [t3, h2],
                [t2, h2]
              ])
            );
          }
        }
      }

      return series;
    },

    createPolygon(name, color, zIndex, data) {
      return {
        type: 'polygon',
        name,
        color,
        zIndex,
        data
      };
    },

    makeChart(chartHeight, series, categories, ticks, title, start) {
      // Create chart instance
      let idx = 0;
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          spacingTop: 20,
          spacingBottom: 10,
          marginRight: 20,
          type: 'line',
          zoomType: 'xy',
          plotBorderColor: '#bbb',
          plotBorderWidth: 1,
          backgroundColor: '#455A64'
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
          text: title
        },
        xAxis: {
          gridLineWidth: 1,
          gridLineColor: '#aaa',
          gridLineDashStyle: 'Dot',
          type: 'datetime',
          tickmarkPlacement: 'on',
          min: start,
          labels: {
            style: {
              color: '#fff',
              fontSize: 10,
              fontWeight: 'normal'
            }
          },
          title: {
            style: {
              fontSize: 14,
              fontWeight: 'bold'
            },
            text: 'Time of day'
          }
        },
        yAxis: {
          reversed: true,
          tickColor: '#333',
          tickWidth: 4,
          tickPositions: ticks,
          startOnTick: false,
          endOnTick: false,
          //categories: categories,
          labels: {
            style: {
              color: '#fff',
              font: '12px Helvetica',
              fontWeight: 'bold'
            },
            formatter: function() {
              idx++;
              if (this.isFirst) {
                idx = 0;
              }
              return categories[idx];
            }
          },
          title: {
            text: null
          }
        },
        tooltip: {
          formatter: function() {
            let s = 'Time: ' + Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', new Date(this.x));
            return s;
          }
        },
        legend: {
          enabled: false
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

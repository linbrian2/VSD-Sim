<template>
  <div>
    <highcharts
      ref="highcharts"
      class="chart"
      :options="chartOptions"
      :callback="drawLineText"
      v-show="dataAvailable"
    ></highcharts>
  </div>
</template>

<script>
import Highcharts from 'highcharts';
export default {
  props: {
    data: Array,
    speed: { type: Number, default: 45 },
    title: { type: String, default: '' }
  },

  data: () => ({
    barHeight: 0.04,
    loading: false,
    overlayLines: {},
    customObjects: []
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
      return this.makeChart(this.height, d.series, d.categories, d.ticks, this.title, d.start, this.drawLineText);
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
        const ratio = length / total;

        const P2s = [];
        const P6s = [];
        const dists = [];

        let P2Points = [];
        let P6Points = [];

        let h = 0;
        data.forEach(item => {
          h += (item.distance * length) / total;
          ticks.push(h);
          categories.push(`${item.name} (G${item.group})`);

          if (item.P2 && item.P2.length > 0) {
            const ts = item.P2[0].filter(item => item > 0);
            start = Math.min(start, ...ts);
          }

          const offset = this.barHeight;

          // North bound
          const P2 = this.composeCycleSeries(h, item.P2, -offset, 'rgba(0,118,230,0.9)');

          // South bound
          const P6 = this.composeCycleSeries(h, item.P6, offset, '#76FF03');

          P2s.push(P2);
          P6s.push(P6);
          dists.push(item.distance);

          // North bound points (up direction)
          if (item.P2Points && item.P2Points.length > 0) {
            const P2P = item.P2Points.map(pt => {
              const x = pt.time;
              const y = h - offset - pt.dist * ratio;
              const d = pt.dist;
              return Object.assign({}, pt, { x, y, d });
            });
            P2Points = P2Points.concat(P2P);
          }

          // South bound points (down direction)
          if (item.P6Points && item.P6Points.length > 0) {
            const P6P = item.P6Points.map(pt => {
              const x = pt.time;
              const y = h + offset + pt.dist * ratio;
              const d = pt.dist;
              return Object.assign({}, pt, { x, y, d });
            });
            P6Points = P6Points.concat(P6P);
          }

          series = series.concat(P2.concat(P6));
        });

        const startIdx = 0;
        const SPEED = this.speed / 2.237; // (m/s)

        // P6 - South Bound (green-ish)
        for (let i = startIdx + 1; i < P6s.length; i++) {
          const t = Math.round((dists[i] * 1000) / SPEED);
          const B = this.findThroughputBand(P6s[i - 1], P6s[i], t, 'SB', 'rgba(0,230,118,0.5)');
          if (B.length > 0) {
            series = series.concat(B);
          }
        }

        // P2 - North Bound (blue-ish)
        for (let i = P2s.length - 1; i > startIdx; i--) {
          // Calculate the time used to travel from one intersection to another intersection in milliseconds
          const t = Math.round((dists[i] * 1000) / SPEED);
          const B = this.findThroughputBand(P2s[i], P2s[i - 1], t, 'NB', 'rgba(0,118,230,0.5)');
          if (B.length > 0) {
            series = series.concat(B);
          }
        }

        // Add overlay points
        this.addOverlays(P2Points, P6Points, series);
      }

      this.loading = false;

      return { series, categories, ticks, start };
    },

    addOverlays(P2Points, P6Points, series) {
      // Create overlay point tooltip
      const tooltip = {
        pointFormatter: function() {
          const time = Highcharts.dateFormat('%H:%M:%S', this.x);
          return ` 
            Vid: <b>${this.vid}</b><br>
            Time:  <b>${time}</b><br>
            Speed:  <b>${this.speed.toFixed(1)} mph</b><br>
            Dist: <b>${this.d} m</b><br>
            Int: <b>${this.device}</b><br>`;
        }
      };

      let overlayLines = {};

      // P2 points - North bounds
      if (P2Points.length > 0) {
        //console.log(P2Points);
        const points = this.createOverlayPoints('Overlay', P2Points, 'rgb(0,118,230)', tooltip);
        series.push(points);

        // Create lines
        const lines = this.groupLines(P2Points);
        Object.keys(lines).forEach(name => {
          const data = lines[name];
          const line = this.createOverlayLines(name, data, 'rgb(255,255,255)');
          series.push(line);
        });

        Object.assign(overlayLines, lines);
      }

      // P6 points - South bounds
      if (P6Points.length > 0) {
        const points = this.createOverlayPoints('Overlay', P6Points, 'rgb(0,230,118)', tooltip);
        series.push(points);

        // Create lines
        const lines = this.groupLines(P6Points);
        Object.keys(lines).forEach(name => {
          const data = lines[name];
          const line = this.createOverlayLines(name, data, 'rgb(255,255,255)');
          series.push(line);
        });

        Object.assign(overlayLines, lines);
      }

      this.overlayLines = overlayLines;
    },

    groupLines(overlayPoints) {
      // Group the data by vehicle id
      const groups = overlayPoints.reduce((r, item) => {
        r[item.vid] = r[item.vid] || [];
        r[item.vid].push(item);
        return r;
      }, {});

      // Eliminate isolated points
      for (const vid in groups) {
        if (groups.hasOwnProperty(vid)) {
          const data = groups[vid];
          if (data.length < 2) {
            delete groups[vid];
          }
        }
      }

      // Rerange and sort the points by time
      return Object.keys(groups).reduce((acc, key) => {
        const items = groups[key].map(item => [item.x, item.y]).sort((a, b) => (a[0] > b[0] ? 1 : -1));
        acc[key] = items;
        return acc;
      }, {});
    },

    findThroughputBand(startPolygonList, endPolygonList, dt, direction, color) {
      const result = [];

      if (startPolygonList.length > 0 && endPolygonList.length > 0) {
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

          const bands = this.createBands(startT, endT, dt, segments);
          bands.forEach(band => {
            if (band.length === 4) {
              result.push(
                this.createPolygon('B' + i, color, 1, [
                  { x: band[0], y: h0, id: '1' },
                  { x: band[1], y: h1, id: '2' },
                  { x: band[3], y: h3, id: '3' },
                  { x: band[2], y: h2, id: '4' }
                ])
              );
            }
          });
        }
      }

      return result;
    },

    createBands(startT, endT, dt, segments) {
      // Compose band
      // ---- t0 ----- t1 -----
      //       |        |
      //       |        |
      //------t2 ----- t3 -----
      //
      const affectedSegments = this.getSegments(startT + dt, endT + dt, segments);
      if (affectedSegments.length === 0) {
        return [];
      }

      if (affectedSegments.length === 1) {
        const band = this.createSingleBand(startT, endT, dt, affectedSegments[0]);
        if (band.length === 4) {
          return [band];
        } else {
          return [];
        }
      }

      return this.createMultipleBands(startT, endT, dt, affectedSegments);
    },

    createSingleBand(startT, endT, dt, segment) {
      // Compose band
      // ---- t0 ----- t1 -----
      //       |        |
      //       |        |
      //------t2 ----- t3 -----
      //
      let t2 = segment[0];
      let t3 = segment[1];
      let t0 = t2 - dt;
      let t1 = t3 - dt;

      if (t0 < startT) {
        t0 = startT;
        t2 = t0 + dt;
      }

      if (t1 > endT) {
        t1 = endT;
        t3 = t1 + dt;
      }

      return t1 - t0 > 3000 ? [t0, t1, t2, t3] : [];
    },

    createMultipleBands(startT, endT, dt, affectedSegments) {
      // Compose band
      // ---- t0 ----- t1 -----
      //       |        |
      //       |        |
      //------t2 ----- t3 -----
      //
      const result = [];
      for (let i = 0; i < affectedSegments.length; i++) {
        let t2 = affectedSegments[i][0];
        let t3 = affectedSegments[i][1];
        let t0 = t2 - dt;
        let t1 = t3 - dt;
        if (t0 < startT) {
          t0 = startT;
          t2 = t0 + dt;
        }
        if (t1 > endT) {
          t1 = endT;
          t3 = t1 + dt;
        }

        if (t1 - t0 > 3000) {
          result.push([t0, t1, t2, t3]);
        }
      }

      return result;
    },

    getSegments(t0, t1, segments) {
      const result = [];
      for (let i = 0; i < segments.length; i++) {
        const s0 = segments[i][0];
        const s1 = segments[i][1];

        // Check if two ranges [t0, t1] and [s0, s1] overlap
        if (t0 <= s1 && s0 <= t1) {
          result.push(segments[i]);
        }
      }

      return result;
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

    composeCycleSeries(h, cycles, offset, greenColor) {
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
              this.createPolygon('G' + i, greenColor, 10, [
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

    createArrow(color, data) {
      return {
        type: 'line',
        color,
        data
      };
    },

    createOverlayPoints(name, data, color, tooltip) {
      return {
        type: 'scatter',
        zoomType: 'xy',
        name,
        data,
        color,
        zIndex: 21,
        marker: {
          symbol: 'circle',
          radius: 4.0
        },
        states: {
          hover: {
            halo: {
              size: 10,
              attributes: {
                fill: 'red',
                'stroke-width': 2,
                stroke: 'black'
              }
            },
            marker: {
              enabled: false
            }
          }
        },
        tooltip
      };
    },

    createOverlayLines(name, data, color) {
      return {
        type: 'line',
        zoomType: 'xy',
        dashStyle: 'dash',
        name,
        color,
        data,
        lineWidth: 0.75,
        zIndex: 19,
        marker: {
          enabled: false
        }
      };
    },

    drawLineText(chart) {
      if (this.customObjects.length > 0) {
        Highcharts.each(this.customObjects, function(e) {
          e.destroy();
        });
        this.customObjects = [];
      }

      Object.keys(this.overlayLines).forEach(name => {
        const items = this.overlayLines[name];
        if (items.length > 2) {
          const idx = Math.floor(items.length / 2);
          const x = chart.xAxis[0].toPixels(items[idx][0]) + 20;
          const y = chart.yAxis[0].toPixels(items[idx][1]) + 15;
          const t = chart.renderer
            .text(name, x, y)
            .attr({ zIndex: 20 })
            .css({ color: 'white', opacity: 1.0, fontSize: '13px', fontWeight: 'bold' })
            .add();
          this.customObjects.push(t);
        }
      });
    },

    makeChart(chartHeight, series, categories, ticks, title, start, redrawFunc) {
      // Create chart instance
      let idx = 0;
      let chart = {
        credits: {
          enabled: false
        },
        chart: {
          height: chartHeight,
          spacingTop: 20,
          spacingBottom: 30,
          marginRight: 30,
          type: 'line',
          zoomType: 'xy',
          plotBorderColor: '#bbb',
          plotBorderWidth: 1,
          backgroundColor: '#455A64',
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
              inactive: {
                opacity: 1
              }
            }
          },
          scatter: {
            enableMouseTracking: true,
            marker: {
              enabled: true,
              lineColor: 'red',
              lineWidth: 1.0
            }
          }
        },
        title: {
          text: title,
          style: {
            fontSize: 16,
            fontWeight: 'bold'
          }
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
            text: ''
          }
        },
        yAxis: {
          reversed: true,
          tickColor: '#333',
          tickWidth: 4,
          tickPositions: ticks,
          startOnTick: false,
          endOnTick: false,
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
          // formatter: function() {
          //   let s = 'Time: ' + Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', new Date(this.x));
          //   return s;
          // }
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

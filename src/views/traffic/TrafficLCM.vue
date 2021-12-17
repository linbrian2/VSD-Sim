<template>
  <div>
    <MapSelectionPanel
      ref="mapSelectPanel"
      :markers="markers"
      :items="items"
      name="lcmSideBarWidth"
      :onMarkerClick="markerClicked"
    >
      <!-- Region selection menu -->
      <v-menu bottom right offset-y min-width="250" :close-on-content-click="true">
        <template v-slot:activator="{ on: menu, attrs }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on: tooltip }">
              <v-btn icon class="mx-1" v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                <v-icon dark>mdi-dots-vertical</v-icon>
              </v-btn>
            </template>
            <span>Region Selection</span>
          </v-tooltip>
        </template>

        <v-list>
          <v-list-item v-for="item in region_menu_items" :key="item.value" @click="regionMenuItemClicked(item.value)">
            <v-list-item-title :class="{ 'font-weight-bold': item.value === selectedRegionId }">
              <v-icon class="mr-1" v-if="item.value === selectedRegionId">mdi-check</v-icon>
              <span :class="{ 'ml-8': item.value !== selectedRegionId }"> {{ item.title }} </span>
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </MapSelectionPanel>

    <TitleBar showId showUid title="Traffic Flow LCM" :loading="loading" :refresh="refreshData" />

    <v-container fluid>
      <v-card tile class="mb-8" elevation="24" v-if="isVisible('NB')">
        <LCMCurveChart :data="chartDataNB" direction="NB" :height="height" :exporting="false" @click="pointClicked" />
      </v-card>

      <v-card tile elevation="24" v-if="isVisible('SB')">
        <LCMCurveChart :data="chartDataSB" direction="SB" :height="height" :exporting="false" @click="pointClicked" />
      </v-card>
    </v-container>

    <!-- Popup Dialogs -->
    <SpeedVolumeChartDialog v-model="showDialog" ref="speedVolumeChartDialog" />
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState, mapActions } from 'vuex';
import MapSelectionPanel from '@/components/modules/traffic/map/MapSelectionPanel';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import LCMCurveChart from '@/components/modules/traffic/chart/LCMCurveChart';
import Highcharts from 'highcharts';
import SpeedVolumeChartDialog from '@/components/modules/traffic/lcm/SpeedVolumeChartDialog';

export default {
  components: {
    MapSelectionPanel,
    TitleBar,
    LCMCurveChart,
    SpeedVolumeChartDialog
  },

  data: () => ({
    loading: false,
    defaultHeight: 650,
    showDialog: false,

    directions: [],

    chartDataNB: {},
    chartDataSB: {},
    availabilityNB: false,
    availabilitySB: false,

    LCMDevices: null,

    region_menu_items: [
      { title: 'All Detectors', value: -1 },
      { title: 'Urban Freeway Detectors', value: 1 },
      { title: 'CAV Area Freeway Detectors', value: 3 }
    ],
    selectedRegionId: -1
  }),

  computed: {
    markers() {
      if (this.selectedRegionId < 0) {
        return this.anomalyDevices.filter(item => this.isLCMAvialble(item));
      } else {
        return this.anomalyDevices.filter(item => item.zone === this.selectedRegionId && this.isLCMAvialble(item));
      }
    },

    items() {
      let names = [];
      this.anomalyDevices.forEach(location => {
        names.push(location.name.trimRight());
      });
      return names;
    },

    height() {
      return this.availabilityNB && this.availabilitySB ? this.defaultHeight : 750;
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['activeMarker', 'anomalyDevices'])
  },

  mounted() {
    if (this.anomalyDevices.length === 0) {
      this.fetchAnomalyDevices();
    }
    if (!this.LCMDevices) {
      this.fetchLCMDevices();
    }
    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.showDataIfEmpty();
    }, 500);
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  methods: {
    isVisible(name) {
      switch (name) {
        case 'NB':
          return this.availabilityNB;
        case 'SB':
          return this.availabilitySB;
      }
      return true;
    },

    showDataIfEmpty() {
      this.$bus.$emit('CENTER_MAP');
      this.$bus.$emit('SELECT_FIRST');
    },

    pointClicked({ direction, time }) {
      let marker = this.activeMarker;
      const name = `Traffic Speed & Volume (${marker.id}-${direction})`;
      const deviceId = marker.id;
      this.$refs.speedVolumeChartDialog.init(name, deviceId, direction, time);
      this.showDialog = true;
    },

    regionMenuItemClicked(value) {
      setTimeout(() => {
        this.selectedRegionId = value;
        this.valueSelected = '';
      }, 100);
    },

    markerClicked(marker) {
      this.fetchData(marker);
    },

    refreshData() {
      let marker = this.activeMarker;
      this.fetchData(marker);
    },

    fetchData(marker) {
      let start = this.currentDate.getTime();
      this.directions = marker.directions.map(item => item);
      this.fetchLCMData(marker.id, marker.directions, start);
    },

    async fetchLCMDevices() {
      try {
        const response = await Api.fetchLCMDevices();
        this.LCMDevices = response.data;
      } catch (error) {
        this.showWarningMessage(error);
      }
    },

    async fetchLCMData(deviceId, directions, startDate) {
      this.loading = true;
      try {
        if (directions.includes('NB')) {
          const direction = 'NB';
          const [res1, res2, res3] = await Promise.all([
            Api.fetchTrafficFlowVolumeSpeed(deviceId, direction, startDate),
            Api.fetchTrafficFlowAnomalyVolumeSpeed(deviceId, direction, startDate),
            Api.fetchLCMCurve(deviceId, direction, 200)
          ]);
          const flowData = this.parseResponse(res1, false);
          const anomalyData = this.parseResponse(res2, false);
          const lcmData = this.parseResponse(res3, false);
          const valid = flowData && lcmData ? true : false;
          this.chartDataNB = valid ? this.composeData(flowData, anomalyData, lcmData, direction) : {};
          this.availabilityNB = valid;
        } else {
          this.availabilityNB = false;
        }

        if (directions.includes('SB')) {
          const direction = 'SB';
          const [res1, res2, res3] = await Promise.all([
            Api.fetchTrafficFlowVolumeSpeed(deviceId, direction, startDate),
            Api.fetchTrafficFlowAnomalyVolumeSpeed(deviceId, direction, startDate),
            Api.fetchLCMCurve(deviceId, direction, 200)
          ]);

          const flowData = this.parseResponse(res1, false);
          const anomalyData = this.parseResponse(res2, false);
          const lcmData = this.parseResponse(res3, false);
          const valid = flowData && lcmData ? true : false;
          this.chartDataSB = valid ? this.composeData(flowData, anomalyData, lcmData, direction) : {};
          this.availabilitySB = valid;
        } else {
          this.availabilitySB = false;
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    parseResponse(response, showWarning) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        if (showWarning) {
          this.showWarningMessage(response.data.message);
        }
      }
      return result;
    },

    showWarningMessage(message) {
      this.$store.dispatch('setSystemStatus', { text: message, color: 'warning', timeout: 1500 });
    },

    composeData(flowData, anomalyData, lcmData, direction) {
      const tooltip = {
        pointFormatter: function() {
          const time = Highcharts.dateFormat('%H:%M', this.time);
          return `Time:  <b>${time}</b><br>Volume: <b>${this.x}</b><br>Speed: <b>${this.y}</b>`;
        }
      };

      const lcm = this.createLCMCurve(lcmData.curve);
      const flow = this.createTrafficFlowPoints('Flow Data', flowData, 'rgba(0, 200, 0, 0.5)', tooltip);
      const criticalPoint = this.createCriticalPoint([lcmData.Vc, lcmData.vc]);
      const anchorPoint = this.createAnchorPoint([lcmData.Va, lcmData.va]);

      const series = [lcm, flow, criticalPoint, anchorPoint];
      if (anomalyData) {
        const tooltip = {
          pointFormatter: function() {
            const time = Highcharts.dateFormat('%H:%M', this.time);
            return `Time:  <b>${time}</b><br>Volume: <b>${this.x}</b><br>Speed: <b>${this.y}</b><br>Status: <b>${this.status}</b><br>`;
          }
        };
        const anomaly = this.createTrafficFlowPoints('Anomaly Data', anomalyData, 'rgba(255, 0, 0, .8)', tooltip);
        series.push(anomaly);
      }

      const xplotlines = this.createXPlotLines(lcmData.Vc, `Critical Volume (${lcmData.Vc})`, 'grey');
      const titles = [
        `Free Flow Speed (${Math.floor(lcmData.vf * 10) / 10} mph)`,
        `Critical Speed (${lcmData.vc} mph)`
      ];
      const yplotlines = this.createYPlotLines([lcmData.vf, lcmData.vc], titles, 'grey');

      const title = `LCM ( ${direction} )`;
      const xAxis = 'Volume (vehicle count/5 min)';
      const yAxis = 'Speed (mph)';
      const xmax = Math.floor(lcmData.Vc * 1.35);
      return { series, xAxis, yAxis, title, xplotlines, yplotlines, xmin: 0, xmax: xmax, ymin: 0, ymax: 100 };
    },

    createLCMCurve(data) {
      return {
        findNearestPointBy: 'xy',
        type: 'scatter',
        name: 'LCM Line',
        data,
        marker: {
          enabled: true
        },
        lineWidth: 3,
        shadow: false,
        color: 'rgba(0, 200, 255, 0.5)',
        enableMouseTracking: false
      };
    },

    createCriticalPoint(point) {
      return {
        type: 'scatter',
        zoomType: 'xy',
        name: 'Critical Point',
        data: [point],
        color: 'purple',
        marker: {
          symbol: 'circle',
          radius: 10
        }
      };
    },

    createAnchorPoint(point) {
      return {
        type: 'scatter',
        zoomType: 'xy',
        name: 'Anchor Point',
        data: [point],
        color: 'orange',
        marker: {
          symbol: 'circle',
          radius: 8
        }
      };
    },

    createTrafficFlowPoints(name, data, color, tooltip) {
      return {
        type: 'scatter',
        zoomType: 'xy',
        name,
        data,
        color,
        marker: {
          symbol: 'circle',
          radius: 4
        },
        states: {
          hover: {
            halo: {
              size: 12,
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

    createXPlotLines(value, title, color) {
      return [
        {
          color: 'blue',
          width: 2,
          value,
          dashStyle: 'Dot',
          label: {
            text: title,
            verticalAlign: 'middle',
            textAlign: 'center',
            x: 10,
            y: 150,
            style: {
              color,
              fontWeight: 'normal'
            }
          }
        }
      ];
    },

    createYPlotLines(values, titles, color) {
      const result = [];
      for (let i = 0; i < values.length; i++) {
        const item = {
          color: 'blue',
          width: 2,
          value: values[i],
          dashStyle: 'Dot',
          label: {
            text: titles[i],
            align: 'right',
            x: -20,
            y: -10,
            style: {
              color,
              fontWeight: 'normal'
            }
          }
        };
        result.push(item);
      }
      return result;
    },

    isLCMAvialble(device) {
      if (!this.LCMDevices) {
        return true;
      }
      const foundItem = this.LCMDevices.find(item => item.deviceId === device.id);
      return foundItem !== undefined;
    },

    ...mapActions('traffic', ['fetchAnomalyDevices'])
  }
};
</script>

<style lang="scss" scoped>
.basic-chart {
  height: 500px;
}
</style>

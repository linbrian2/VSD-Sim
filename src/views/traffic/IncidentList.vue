<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
    <!-- Left map panel -->
    <SelectionPanel :width="width" name="incidentSideBarWidth">
      <MapSegment
        ref="mapSegmentRef"
        :segments="segmentLinks"
        :markers="markers"
        @select="onSegmentSelected"
        @click="onMarkerClicked"
      />
    </SelectionPanel>

    <TitleBar title="INCIDENT LIST" :loading="loading" :refresh="refreshData">
      <div class="d-flex justify-space-between">
        <div class="d-flex justify-space-between ml-15 mt-2 mr-15">
          <div class="font-weight-bold overline ml-3">totally {{ totalIncidents }} incidents</div>
          <v-btn small icon @click.stop="showIncidentTable = !showIncidentTable">
            <v-icon color="green" v-if="showIncidentTable">mdi-chevron-up</v-icon>
            <v-icon color="green" v-else>mdi-chevron-down</v-icon>
          </v-btn>
        </div>

        <div class="d-flex justify-space-between" style="width: 200px;">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn class="mt-1" icon v-on="on" @click.stop="displaySearchSettings">
                <v-icon medium color="white">mdi-magnify</v-icon>
              </v-btn>
            </template>
            <span>Global Search</span>
          </v-tooltip>

          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn class="mt-1" icon v-on="on" @click.stop="showIncidentTimeline = !showIncidentTimeline">
                <v-icon medium>mdi-timetable</v-icon>
              </v-btn>
            </template>
            <span>Timeline</span>
          </v-tooltip>

          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn class="mt-1" icon v-on="on" @click.stop="showSettings = !showSettings">
                <v-icon medium color="white">mdi-cog-outline</v-icon>
              </v-btn>
            </template>
            <span>Settings</span>
          </v-tooltip>

          <v-divider vertical />

          <v-tooltip bottom v-if="isSimulation">
            <template v-slot:activator="{ on }">
              <v-btn class="mt-1" icon v-on="on" @click.stop="showSimulationConfig = !showSimulationConfig">
                <v-icon medium color="white">mdi-traffic-light-outline </v-icon>
              </v-btn>
            </template>
            <span>Simulation Config</span>
          </v-tooltip>
        </div>
      </div>
    </TitleBar>

    <v-container>
      <v-card tile class="mb-4 mx-4" v-show="showIncidentTimeline">
        <v-card-title class="d-flex justify-space-between">
          <h4 class="ml-4">Incident Timeline</h4>
          <v-btn icon @click="showIncidentTimeline = false"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <div>
          <IncidentHeatMapChart
            ref="heatMapChart"
            :data="incidentsByTime"
            :height="400"
            @cell-click="timeSlotClicked"
          />
        </div>
      </v-card>

      <v-card tile class="mx-3 mb-2" v-show="showIncidentTable">
        <div>
          <IncidentTable :height="tableHeight" :incidents="incidents" @click="handleRowClick" />
        </div>
      </v-card>

      <div v-if="incidentItem">
        <EvidenceListDisplay :incident="incidentItem" @select="singleSegmentSelected" ref="anomalySegmentDisplay" />
      </div>
    </v-container>

    <IncidentSettings v-model="showSettings" ref="settings" />
    <SimulationConfigs v-model="showSimulationConfig" ref="simu" v-if="isSimulation" />
    <GlobalSearchDialog v-model="showSearch" ref="search" @handler="searchStarted" />
  </div>

  <div class="mobile" v-else>
    <!-- Title Bar -->
    <!-- <TitleBar :showMap="false" :showRefresh="false" /> -->

    <!-- Input & Map -->
    <MapSegment
      ref="mapSegmentRef"
      :segments="segmentLinks"
      :markers="markers"
      @select="onSegmentSelected"
      @click="onMarkerClicked"
      :smallMap="true"
    />

    <!-- Container -->
    <v-container>
      <v-card tile class="mb-4 mx-4" v-show="showIncidentTimeline">
        <v-card-title class="d-flex justify-space-between">
          <h4 class="ml-4">Incident Timeline</h4>
          <v-btn icon @click="showIncidentTimeline = false"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <div>
          <IncidentHeatMapChart
            ref="heatMapChart"
            :data="incidentsByTime"
            :height="400"
            @cell-click="timeSlotClicked"
          />
        </div>
      </v-card>

      <v-card tile class="mx-3 mb-2" v-show="showIncidentTable">
        <div>
          <IncidentTable :height="500" :incidents="incidents" @click="handleRowClick" />
        </div>
      </v-card>

      <div v-if="incidentItem">
        <EvidenceListDisplay :incident="incidentItem" @select="singleSegmentSelected" ref="anomalySegmentDisplay" />
      </div>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import { mapState, mapActions } from 'vuex';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import MapSegment from '@/components/modules/traffic/incident/MapSegment';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import IncidentTable from '@/components/modules/traffic/incident/IncidentTable';
import IncidentSettings from '@/components/modules/traffic/incident/IncidentSettings';
import SimulationConfigs from '@/components/modules/traffic/incident/SimulationConfigs';
import GlobalSearchDialog from '@/components/modules/traffic/incident/GlobalSearchDialog';
import IncidentHeatMapChart from '@/components/modules/traffic/incident/IncidentHeatMapChart';
import EvidenceListDisplay from '@/components/modules/traffic/incident/EvidenceListDisplay';

export default {
  components: {
    TitleBar,
    MapSegment,
    SelectionPanel,
    IncidentTable,
    IncidentSettings,
    SimulationConfigs,
    GlobalSearchDialog,
    IncidentHeatMapChart,
    EvidenceListDisplay
  },

  data: () => ({
    width: 600,
    loading: false,
    showIncidentTable: true,
    showIncidentTimeline: false,
    showSettings: false,
    showSearch: false,
    showSimulationConfig: false,

    segments: [],
    markers: [],
    incidents: [],

    incidentsByTime: {},
    segmentLinks: [],
    incidentItem: null
  }),

  computed: {
    totalIncidents() {
      return this.incidents ? this.incidents.length : 0;
    },

    startTimestamp() {
      return Utils.getStartOfDay(this.currentDate).getTime();
    },

    maxWidth() {
      return this.showPanel ? '100%' : '85%';
    },

    tableHeight() {
      return Math.max(100, Math.min(this.incidents.length * 65, 400));
    },

    isSimulation() {
      return process.env.VUE_APP_MITIGATION_MODE === 'simulation';
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['anomalyDevices', 'weatherStations', 'bluetoothSegments', 'incidentSettings', 'showPanel'])
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  mounted() {
    if (this.anomalyDevices.length === 0) {
      this.fetchAnomalyDevices();
    }
    if (this.bluetoothSegments.length === 0) {
      this.fetchBluetoothSegments();
    }

    this.refreshData();
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  methods: {
    onSegmentSelected(segmentId) {
      if (this.$refs.anomalySegmentDisplay) {
        this.$refs.anomalySegmentDisplay.selectSegment(segmentId);
      }
    },

    onMarkerClicked(marker) {
      console.log(marker);
      const type = marker.type;
      if (type) {
        const item = marker.item;
        this.gotoSection(`#${type}`);
        if (this.$refs.anomalySegmentDisplay) {
          this.$refs.anomalySegmentDisplay.selectEvidenceItem(type, item);
          if (type == 'waze') {
            this.$refs.anomalySegmentDisplay.selectWazeAlert(marker.id);
          }
        }
      }
    },

    displaySearchSettings() {
      this.$refs.search.init();
      this.showSearch = true;
    },

    searchStarted(data) {
      this.searchIncidentData(data);
    },

    singleSegmentSelected(linkId) {
      if (this.$refs.mapSegmentRef) {
        this.$refs.mapSegmentRef.selectLink(linkId);
      }
    },

    handleRowClick(item) {
      this.incidentItem = Object.assign({}, item);
      if (this.$refs.anomalySegmentDisplay) {
        this.$refs.anomalySegmentDisplay.init(item);
      }

      // Compose segment
      const segments = [];
      item.segments.forEach(linkId => {
        const segment = this.bluetoothSegments.find(bs => bs.id === linkId);
        if (segment) {
          segments.push(segment);
        }
      });

      if (segments.length > 0) {
        this.markers = this.createMarkers(this.incidentItem);
        this.segmentLinks = segments;
        setTimeout(() => {
          this.updateMap(segments);
        }, 50);
      }

      setTimeout(() => {
        this.updateIncidentTimeline(item);
      }, 200);
    },

    timeSlotClicked(e) {
      const hourIdx = e.x;
      const minuteIdx = e.y;
      const idx = hourIdx * 12 + minuteIdx;
      const items = this.findAllIncidentsByTimeSlot(idx);

      if (items.length > 0) {
        this.handleRowClick(items[0]);
      }
    },

    gotoSection(target) {
      this.$vuetify.goTo(target);
    },

    updateIncidentTimeline(item) {
      if (this.$refs.heatMapChart) {
        const startIdx = Utils.get5MinIndexOf288(item.startTime);
        const endIdx = Utils.get5MinIndexOf288(item.endTime);
        if (startIdx < endIdx) {
          const ids = Utils.range(startIdx, endIdx + 1);
          this.$refs.heatMapChart.highlightCells(ids);
        }
      }
    },

    updateMap(segments) {
      if (this.$refs.mapSegmentRef) {
        this.$refs.mapSegmentRef.centerMapAndZoom(segments, true);
      }
    },

    refreshData() {
      this.fetchIncidentData(this.currentDate);
    },

    findAllIncidentsByTimeSlot(idx) {
      return this.incidents.filter(incident => {
        const idx0 = Utils.get5MinIndex(this.startTimestamp, incident.startTime);
        const idx1 = Utils.get5MinIndex(this.startTimestamp, incident.endTime);
        return idx0 <= idx && idx <= idx1;
      });
    },

    async fetchIncidentData(startTime) {
      this.loading = true;
      const { severity, duration } = this.incidentSettings;
      try {
        const start = startTime.getTime();
        const response = await Api.fetchIncidentData(start, 1, severity, duration);
        const data = this.getResponseData(response);
        if (data) {
          this.incidents = data;
          this.incidentsByTime = this.composeIncidentHeatMapData(this.incidents);
          this.segments = this.createTotalSegments();
        } else {
          this.incidents = [];
          this.incidentsByTime = this.composeIncidentHeatMapData(this.incidents);
          this.segments = [];
        }
        this.incidentItem = null;
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    async searchIncidentData(queryInput) {
      this.loading = true;
      try {
        const response = await Api.searchIncidentData(queryInput);
        const data = this.getResponseData(response);
        if (data) {
          this.incidents = data;
          this.incidentsByTime = this.composeIncidentHeatMapData(this.incidents);
          this.segments = this.createTotalSegments();
        } else {
          this.incidents = [];
          this.incidentsByTime = this.composeIncidentHeatMapData(this.incidents);
          this.segments = [];
        }
        this.incidentItem = null;
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    getResponseData(response) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },

    createTotalSegments() {
      const linkIds = new Set();
      this.incidents.forEach(item => {
        linkIds.add(item.linkId);
      });

      const segments = [];
      linkIds.forEach(linkId => {
        const segment = this.bluetoothSegments.find(item => item.id === linkId);
        if (segment) {
          segments.push(segment);
        }
      });

      return segments;
    },

    composeIncidentHeatMapData(incidents) {
      let xcategories = [];
      let ycategories = [];

      let startTime = Utils.getStartOfDay(this.currentDate).getTime();

      const rowCount = 12;
      const colCount = 24;

      for (let i = 0; i < rowCount; i++) {
        ycategories.push(i * 5);
      }
      for (let i = 0; i < colCount; i++) {
        xcategories.push(startTime + 3600000 * i);
      }

      const counts = [];
      for (let i = 0; i < rowCount * colCount; i++) {
        counts.push(0);
      }

      if (incidents.length > 0) {
        incidents.forEach(incident => {
          const start = incident.startTime;
          const end = incident.endTime;
          const idx0 = Utils.get5MinIndex(startTime, start);
          const idx1 = Utils.get5MinIndex(startTime, end);
          for (let i = idx0; i <= idx1; i++) {
            counts[i] = counts[i] + 1;
          }
        });
      }

      // Create a series
      let series = [];
      for (let x = 0; x < colCount; x++) {
        for (let y = 0; y < rowCount; y++) {
          series.push([x, y, counts[x * rowCount + y]]);
        }
      }

      const dataClasses = [
        { from: 0, to: 0, color: '#43A047', name: '0' },
        { from: 1, to: 1, color: '#F44336', name: '1' },
        { from: 2, to: 2, color: '#D32F2F', name: '2' },
        { from: 3, to: 50, color: '#B71C1C', name: '3+' }
      ];

      const colorAxis = {
        dataClasses
      };

      let result = {};
      result.title = '';
      result.xAxis = 'Time of day (hour)';
      result.yAxis = 'Time of hour (min)';
      result.xcategories = xcategories;
      result.ycategories = ycategories;
      result.data = series;
      result.colorAxis = colorAxis;

      return result;
    },

    createMarkers(incident) {
      const markers = [];

      // Flow detectors
      const flows = incident.items.filter(item => item.type === Constants.DATA_TRAFFIC_FLOW);
      if (flows.length > 0) {
        Utils.uniqueBy(flows, item => item.data.deviceId + item.data.direction).forEach(item => {
          markers.push({
            id: 'F' + item.data.id,
            name: item.data.name,
            type: 'flow',
            item: item.data.deviceId + item.data.direction,
            position: item.data.position,
            linkId: item.data.linkId,
            icon: 'redDotIcon'
          });
        });
      }

      // Bluetooth detectors
      const travelTimes = incident.items.filter(item => item.type === Constants.DATA_TRAVEL_TIME);
      if (travelTimes.length > 0) {
        Utils.uniqueBy(travelTimes, item => item.data.shortName).forEach(item => {
          markers.push({
            id: 'B' + item.data.id,
            name: item.data.name,
            type: 'travelTime',
            item: item.data.shortName,
            position: item.data.position,
            linkId: item.data.linkId,
            icon: 'redBluetoothIcon',
            zIndex: 9999
          });
        });
      }

      // Waze detectors
      const wazes = incident.items.filter(item => item.type === Constants.DATA_WAZE_ALERTS);
      if (wazes.length > 0) {
        const alerts = [];
        wazes.forEach(item => {
          const linkId = item.data.linkId;
          const details = JSON.parse(item.data.details);
          details.forEach(d => {
            const id = d.id;
            const desc = d.desc;
            const position = { lat: d.lat, lng: d.lon };
            alerts.push({ id, desc, position, linkId });
          });
        });

        Utils.uniqueBy(alerts, item => item.id).forEach(item => {
          markers.push({
            id: 'W' + item.id,
            name: item.desc,
            type: 'waze',
            item: item.id,
            position: item.position,
            linkId: item.linkId,
            icon: 'wazeIcon'
          });
        });
      }

      // Restrictions
      incident.items
        .filter(item => item.type === Constants.DATA_RESTRICTION)
        .forEach(item => {
          markers.push({
            id: 'R' + item.data.restrictionId,
            name: item.data.description,
            type: 'restrictions',
            item: item.data.restrictionId,
            position: item.data.position,
            linkId: item.data.linkId,
            icon: 'restrictionIcon'
          });
        });

      const alerts = incident.items.filter(item => item.type === Constants.DATA_TRAFFIC_ALERT);
      if (alerts.length > 0) {
        Utils.uniqueBy(alerts, item => item.data.position.lat + '' + item.data.position.lng).forEach(item => {
          markers.push({
            id: 'A' + item.data.id,
            name: item.data.details,
            position: item.data.position,
            linkId: item.data.linkId,
            icon: 'cautionIcon'
          });
        });
      }

      // Video cameras
      const videos = incident.items.filter(item => item.type === Constants.DATA_TRAFFIC_VIDEO);
      if (videos.length > 0) {
        Utils.uniqueBy(videos, item => item.data.camera).forEach(item => {
          markers.push({
            id: 'V' + item.data.camera,
            name: item.data.camera,
            type: 'video',
            item: item.data.camera,
            position: item.data.position,
            linkId: item.data.linkId,
            icon: 'cameraIcon'
          });
        });
      }

      // Weather station
      if (this.incidentItem && this.incidentItem.info.stationId) {
        const stationId = this.incidentItem.info.stationId;
        const weatherStation = this.weatherStations.find(w => w.id === stationId);
        if (weatherStation) {
          markers.push({
            id: 'S' + weatherStation.id,
            name: weatherStation.name,
            type: 'weather',
            item: weatherStation.id,
            position: weatherStation.position,
            icon: 'weatherIcon'
          });
        }
      }

      // Incident location
      if (incident.location) {
        markers.push({
          id: 'L1',
          name: incident.address,
          position: incident.location,
          icon: 'alertIcon',
          zIndex: 99999
        });
      }

      return markers;
    },

    ...mapActions('traffic', ['fetchAnomalyDevices', 'fetchBluetoothSegments'])
  }
};
</script>

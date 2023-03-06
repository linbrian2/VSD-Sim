<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
    <!-- Left map panel -->
    <SelectionPanel :width="width" name="incidentSideBarWidth">
      <TitleBar title="INCIDENT LIST" :showRefresh="false" :showMap="false" :loading="loading" :refresh="refreshData">
        <template v-slot:right>
          <ThreeDotMenu :items="menuItems" @menuItemclick="menuItemClicked" class="float-right mt-1" />
        </template>

        <div class="d-flex justify-space-between">
          <v-spacer></v-spacer>
          <div class="ml-2">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-btn class="mt-1" icon v-on="on" @click.stop="setupPin">
                  <v-icon medium color="white">{{ pinPane ? 'mdi-pin' : 'mdi-pin-off' }}</v-icon>
                </v-btn>
              </template>
              <span>Pin the incident list pane</span>
            </v-tooltip>
          </div>
        </div>
      </TitleBar>

      <v-container>
        <v-layout column style="height: 80vh">
          <div v-for="(incident, i) in incidents" :key="i">
            <IncidentInfo
              class="mx-1 mb-1"
              :incident="incident"
              @click="handleRowClick"
              @mitigation="handleMitigationClick"
            />
          </div>
        </v-layout>
      </v-container>
    </SelectionPanel>

    <v-app-bar absolute fixed dense dark color="blue-grey darken-1">
      <v-app-bar-nav-icon color="teal lighten-2" @click.stop="showIncidentPanel" />
      <v-toolbar-title class="overline">{{ incidentTitle }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <div class="d-flex justify-end mt-n2">
        <v-btn-toggle v-model="btnSelection" color="primary" dense group>
          <div v-for="item in actionItems" :key="item.id">
            <v-tooltip bottom>
              <template v-slot:activator="{ on: tooltip }">
                <v-btn
                  small
                  outlined
                  color="grey"
                  class="mx-1 mt-2"
                  v-on="{ ...tooltip }"
                  @click.stop="showSection(item.action)"
                >
                  <v-icon small color="white">{{ item.icon }}</v-icon>
                </v-btn>
              </template>
              <span>{{ item.tooltip }}</span>
            </v-tooltip>
          </div>
        </v-btn-toggle>
      </div>
    </v-app-bar>

    <v-layout v-if="incidentItem">
      <v-navigation-drawer v-model="drawer" left :width="width">
        <MapSegment
          ref="mapSegmentRef"
          :segments="segmentLinks"
          :markers="markers"
          @select="onSegmentSelected"
          @click="onMarkerClicked"
          :smallMap="false"
        />
      </v-navigation-drawer>

      <v-sheet class="overflow-y-auto mt-10" max-height="89vh">
        <EvidenceListDisplay
          :showHeader="false"
          :incident="incidentItem"
          @select="singleSegmentSelected"
          ref="anomalySegmentDisplay"
        />
      </v-sheet>
    </v-layout>

    <IncidentSettings v-model="showSettings" ref="settings" />
    <SimulationConfigs v-model="showSimulationConfig" ref="simu" v-if="isSimulation" />
    <GlobalSearchDialog v-model="showSearch" ref="search" @handler="searchStarted" />
    <MitigationSolutionDialog v-model="showMitigation" ref="mitigationRef" />
  </div>

  <div class="mobile" v-else>
    <v-container>
      <div class="my-3">
        <div v-for="(incident, i) in incidents" :key="i">
          <IncidentInfo class="mx-1 mb-1" :incident="incident" @click="handleRowClick" />
        </div>
      </div>

      <div v-if="incidentItem">
        <v-card tile id="mapSegment">
          <div class="mt-4">
            <MapSegment
              ref="mapSegmentRef"
              :segments="segmentLinks"
              :markers="markers"
              @select="onSegmentSelected"
              @click="onMarkerClicked"
              :smallMap="true"
            />
          </div>
        </v-card>

        <EvidenceListDisplay
          :showHeader="false"
          :incident="incidentItem"
          @select="singleSegmentSelected"
          ref="anomalySegmentDisplay"
        />
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
import ThreeDotMenu from '@/components/common/ThreeDotMenu';
import MapSegment from '@/components/modules/traffic/incident/MapSegment';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import IncidentInfo from '@/components/modules/traffic/common/IncidentInfo';
import IncidentSettings from '@/components/modules/traffic/incident/IncidentSettings';
import SimulationConfigs from '@/components/modules/traffic/incident/SimulationConfigs';
import MitigationSolutionDialog from '@/components/modules/traffic/incident/MitigationSolutionDialog';
import GlobalSearchDialog from '@/components/modules/traffic/incident/GlobalSearchDialog';
import EvidenceListDisplay from '@/components/modules/traffic/incident/EvidenceListDisplay';

export default {
  components: {
    TitleBar,
    ThreeDotMenu,
    MapSegment,
    SelectionPanel,
    IncidentInfo,
    IncidentSettings,
    SimulationConfigs,
    GlobalSearchDialog,
    EvidenceListDisplay,
    MitigationSolutionDialog
  },

  data: () => ({
    width: 600,
    loading: false,
    showIncidentTable: true,
    showIncidentTimeline: false,
    showSettings: false,
    showSearch: false,
    showSimulationConfig: false,
    showMitigation: false,
    pinPane: false,

    segments: [],
    markers: [],
    incidents: [],

    incidentsByTime: {},
    segmentLinks: [],
    incidentItem: null,

    btnSelection: 0,

    menuItems: [
      { title: 'Global Search ...', icon: 'mdi-magnify', action: 0 },
      { title: 'Incident Settings ...', icon: 'mdi-cog-outline', action: 1 },
      { divider: true },
      { title: 'Start Simulation ...', icon: 'mdi-traffic-light-outline', action: 2 }
    ],

    DEFAULT_ACTION_ITEMS: [
      { id: 1, icon: 'mdi-information-outline', tooltip: 'Basic Info', action: 'info' },
      { id: 2, icon: 'mdi-timeline-clock-outline', tooltip: 'Timeline', action: 'timeline' },
      { id: 3, icon: 'mdi-car-multiple', tooltip: 'Traffic Flow', action: 'flow' },
      { id: 4, icon: 'mdi-bluetooth-connect', tooltip: 'Travel Time', action: 'travelTime' },
      { id: 5, icon: 'mdi-movie', tooltip: 'Video', action: 'video' },
      { id: 6, icon: 'mdi-waze', tooltip: 'Wazes', action: 'waze' },
      { id: 7, icon: 'mdi-traffic-cone', tooltip: 'Restrictions', action: 'restrictions' },
      { id: 8, icon: 'mdi-alert-decagram-outline', tooltip: 'Traffic Alerts', action: 'alerts' },
      { id: 9, icon: 'mdi-weather-cloudy', tooltip: 'Weather', action: 'weather' }
    ],
    actionItems: []
  }),

  computed: {
    drawer: {
      get() {
        return true;
      },
      set(show) {
        this.$store.commit('traffic/SHOW_PANEL', show);
      }
    },

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
      return Math.max(150, Math.min(this.incidents.length * 65 + 50, 400));
    },

    isSimulation() {
      return process.env.VUE_APP_MITIGATION_MODE === 'simulation';
    },

    incidentTitle() {
      return this.incidentItem ? `INCIDENT ${this.incidentItem.id}` : 'Select incident to show';
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

      if (!this.showPanel) {
        setTimeout(() => {
          this.$store.commit('traffic/SHOW_PANEL', true);
        }, 1000);
      }
    }
  },

  methods: {
    showIncidentPanel() {
      this.$store.commit('traffic/TOGGLE_SHOW_PANEL');
    },

    onSegmentSelected(segmentId) {
      if (this.$refs.anomalySegmentDisplay) {
        this.$refs.anomalySegmentDisplay.selectSegment(segmentId);
      }
    },

    onMarkerClicked(marker) {
      const type = marker.type;
      if (type) {
        const item = marker.item;
        this.gotoSection(type);
        if (this.$refs.anomalySegmentDisplay) {
          this.$refs.anomalySegmentDisplay.selectEvidenceItem(type, item);
          if (type == 'waze') {
            this.$refs.anomalySegmentDisplay.selectWazeAlert(marker.id);
          }
        }
      }
    },

    setupPin() {
      this.pinPane = !this.pinPane;
      if (!this.pinPane) {
        this.$store.commit('traffic/SHOW_PANEL', false);
      }
    },

    menuItemClicked(action) {
      switch (action) {
        case 0:
          this.displaySearchSettings();
          break;
        case 1:
          this.showSettings = true;
          break;
        case 2:
          this.showSimulationConfig = true;
          break;
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

    handleMitigationClick(id) {
      this.$refs.mitigationRef.init(id);
      this.showMitigation = true;
    },

    handleRowClick(item) {
      this.btnSelection = 0;

      if (this.incidentItem) {
        const prevIncident = this.incidents.find(item => item.id === this.incidentItem.id);
        if (prevIncident != null) {
          prevIncident.selected = false;
        }
      }
      item.selected = true;

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

      // Hide the incident list
      if (!this.pinPane) {
        setTimeout(() => {
          this.$store.commit('traffic/SHOW_PANEL', false);
        }, 100);
      }

      // Move the incident display to the top
      setTimeout(() => {
        this.gotoSection(this.DEFAULT_ACTION_ITEMS[0].action);
        this.actionItems = this.createActionItems();
      }, 200);
    },

    showSection(name) {
      setTimeout(() => {
        this.gotoSection(name);
      }, 200);
    },

    createActionItems() {
      if (!this.incidentItem || !this.$refs.anomalySegmentDisplay) {
        return this.DEFAULT_ACTION_ITEMS;
      } else {
        const display = this.$refs.anomalySegmentDisplay;
        const items = [];
        this.DEFAULT_ACTION_ITEMS.forEach(item => {
          if (display.isVisible(item.action)) {
            items.push(item);
          }
        });
        return items;
      }
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
      try {
        const elm = document.getElementById(target);
        if (elm != null) {
          elm.scrollIntoView({ behavior: 'smooth' });
        }
      } catch (error) {
        console.log(error);
      }
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
      this.actionItems = [];
      const { severity, duration } = this.incidentSettings;
      try {
        const start = startTime.getTime();
        const response = await Api.fetchIncidentData(start, 1, severity, duration);
        const data = this.getResponseData(response);
        if (data) {
          this.incidents = data.map(d => ({ ...d, selected: false }));

          if (Utils.isToday(startTime)) {
            this.incidents.sort((a, b) => a.status - b.status);
          }
          this.segments = this.createTotalSegments();
        } else {
          this.incidents = [];
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
          this.incidents = data.map(d => ({ ...d, selected: false }));
          //this.incidents.sort((a, b) => a.endTime - b.endTime);

          this.segments = this.createTotalSegments();
        } else {
          this.incidents = [];
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

<style lang="scss" scoped>
.card-scroll {
  overflow-x: hidden;
  overflow-y: auto;
  margin: 8px 4px;
}
</style>

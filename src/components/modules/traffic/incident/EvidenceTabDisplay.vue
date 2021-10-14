<template>
  <v-card tile class="mb-2">
    <div class="whole-box">
      <div class="grey darken-2 py-2">
        <SegmentHeader :incident="incident" :selectedSegmentId="selectedSegmentId" @select="segmentMenuItemClicked" />
      </div>
      <v-tabs color="teal accent-4" v-model="tab">
        <v-tab v-for="item in items" :key="item.key" :href="`#${item.key}`">
          {{ item.value }}
        </v-tab>
      </v-tabs>
      <div>
        <v-tabs-items v-model="tab">
          <v-tab-item value="flow" v-if="isTabVisible('flow')">
            <div class="mx-4 mt-4">
              <TrafficFlowData
                :infoList="flowInfoList"
                :devices="flowDevices"
                :selectedSegmentId="selectedSegmentId"
                :time="time"
                :timeSlots="timeSlots"
                :interval="interval"
              />
            </div>
          </v-tab-item>

          <v-tab-item value="travelTime" v-if="isTabVisible('travelTime')">
            <div class="mx-4 mt-4">
              <TravelTimeData
                :infoList="travelTimeInfoList"
                :segments="travelTimeSegments"
                :selectedSegmentId="selectedSegmentId"
                :time="time"
                :timeSlots="timeSlots"
                :interval="interval"
              />
            </div>
          </v-tab-item>

          <v-tab-item value="waze" v-if="isTabVisible('waze')">
            <div class="card-scroll">
              <v-row class="mx-4">
                <v-col cols="4" v-for="(waze, i) in filteredWazeAlerts" :key="i">
                  <WazeInfo class="mx-1" :waze="waze" />
                </v-col>
              </v-row>
            </div>
          </v-tab-item>

          <v-tab-item value="restrictions" v-if="isTabVisible('restrictions')">
            <div class="card-scroll">
              <v-row class="mx-4">
                <v-col cols="12" v-for="(res, i) in filteredRestrictions" :key="i">
                  <RestrictionInfo class="mx-1" :restriction="res" color="brown lighten-1" />
                </v-col>
              </v-row>
            </div>
          </v-tab-item>

          <v-tab-item value="alerts" v-if="isTabVisible('alerts')">
            <div class="card-scroll">
              <v-row class="mx-4">
                <v-col cols="12" v-for="(alert, i) in filteredAlerts" :key="i">
                  <TrafficAlertInfo class="mx-1" :alert="alert" />
                </v-col>
              </v-row>
            </div>
          </v-tab-item>

          <v-tab-item value="video" v-if="isTabVisible('video')">
            <div class="mx-4 mt-4">
              <IncidentVideoList :items="videos" :cameras="videoCameras" :selectedSegmentId="selectedSegmentId" />
            </div>
          </v-tab-item>

          <v-tab-item value="weather" v-if="isTabVisible('weather')">
            <div class="my-2 mx-4" min-height="470px" v-if="validWeatherInfo">
              <WeatherInfo :weather="weatherInfo" color="teal darken-2" />
            </div>
          </v-tab-item>
        </v-tabs-items>
      </div>
    </div>
  </v-card>
</template>

<script>
import Utils from '@/utils/Utils';
import WazeAlertTypes from '@/utils/WazeAlertTypes';
import Constants from '@/utils/constants/traffic';
import WeatherInfo from '@/components/modules/traffic/common/WeatherInfo';
import WazeInfo from '@/components/modules/traffic/common/WazeInfo';
import SegmentHeader from '@/components/modules/traffic/incident/SegmentHeader';
import RestrictionInfo from '@/components/modules/traffic/common/RestrictionInfo';
import TrafficAlertInfo from '@/components/modules/traffic/incident/TrafficAlertInfo';
import TrafficFlowData from '@/components/modules/traffic/incident/TrafficFlowData';
import TravelTimeData from '@/components/modules/traffic/incident/TravelTimeData';
import IncidentVideoList from '@/components/modules/traffic/incident/IncidentVideoList';
import { mapState } from 'vuex';

export default {
  props: {
    incident: Object
  },
  components: {
    WeatherInfo,
    WazeInfo,
    TrafficFlowData,
    TravelTimeData,
    RestrictionInfo,
    TrafficAlertInfo,
    IncidentVideoList,
    SegmentHeader
  },
  data: () => ({
    defaultItems: [
      { key: 'flow', value: 'Traffic Flow' },
      { key: 'travelTime', value: 'Travel Time' },
      { key: 'waze', value: 'Waze Alerts' },
      { key: 'restrictions', value: 'Restrictions' },
      { key: 'alerts', value: 'Traffic Alerts' },
      { key: 'video', value: 'Traffic Videos' },
      { key: 'weather', value: 'Weather' }
    ],
    items: [],

    tab: null,

    flowInfoList: [],
    travelTimeInfoList: [],
    restrictions: [],
    alerts: [],
    wazeAlerts: [],
    videos: [],
    weatherInfo: null,
    markers: [],

    flowDevices: [],
    travelTimeSegments: [],
    videoCameras: [],

    interval: 300000,
    time: null,
    timeSlots: [],
    linkId: null,
    selectedSegmentId: null
  }),

  computed: {
    validWeatherInfo() {
      return this.weatherInfo && Object.keys(this.weatherInfo).length > 0;
    },

    filteredRestrictions() {
      if (this.selectedSegmentId === null) {
        return this.restrictions;
      } else {
        return this.restrictions.filter(item => item.linkId === this.selectedSegmentId);
      }
    },

    filteredWazeAlerts() {
      if (this.selectedSegmentId === null) {
        return this.wazeAlerts;
      } else {
        return this.wazeAlerts.filter(item => item.linkId === this.selectedSegmentId);
      }
    },

    filteredAlerts() {
      if (this.selectedSegmentId === null) {
        return this.alerts;
      } else {
        return this.alerts.filter(item => item.linkId === this.selectedSegmentId);
      }
    },

    ...mapState('traffic', ['weatherStations'])
  },

  mounted() {
    this.init(this.incident);
  },

  methods: {
    init(incident) {
      Object.assign(this.items, this.defaultItems);

      if (this.incident !== incident) {
        Object.assign(this.incident, incident);
      }

      this.selectedSegmentId = null;
      this.currentTrvelTimeEvidenceIdx = 0;

      // Setup data tabs
      this.prepareDataForDisplay(this.incident);
    },

    segmentMenuItemClicked(segmentId) {
      this.selectedSegmentId = segmentId;
      this.$emit('select', segmentId);
    },

    formatDateTime(t) {
      const d = new Date(t);
      return Utils.formatDateTime(d);
    },

    isTabVisible(name) {
      const ii = this.items.find(i => i.key === name);
      return ii !== undefined;
    },

    removeTabByName(name) {
      const e = this.items.findIndex(item => item.key === name);
      if (e >= 0) {
        this.items.splice(e, 1);
      }
    },

    onMapClick(marker) {
      if (marker.id.startsWith('F')) {
        this.tab = 'flow';
      } else if (marker.id.startsWith('B')) {
        this.tab = 'travelTime';
      } else if (marker.id.startsWith('R')) {
        this.tab = 'restrictions';
      } else if (marker.id.startsWith('A')) {
        this.tab = 'waze';
      } else if (marker.id.startsWith('W')) {
        this.tab = 'weather';
      }
    },

    prepareDataForDisplay(incident) {
      const flows = incident.items.filter(item => item.type === Constants.DATA_TRAFFIC_FLOW);
      this.flowInfoList = flows.map(d => ({
        time: d.data.time,
        device: d.data.deviceId + d.data.direction,
        data: this.composeFlowInfo(d.data)
      }));

      // Obtain unique devices
      this.flowDevices = Utils.uniqueBy(flows, item => item.data.deviceId + item.data.direction)
        .map(d => ({
          id: d.data.deviceId,
          dir: d.data.direction,
          name: d.data.deviceId + d.data.direction,
          linkId: d.data.linkId
        }))
        .sort((a, b) => a.name.localeCompare(b.name));

      const travelTimes = incident.items.filter(item => item.type === Constants.DATA_TRAVEL_TIME);
      this.travelTimeInfoList = travelTimes.map(d => ({
        time: d.data.time,
        linkId: d.data.linkId,
        data: this.composeTravelTimeInfo(d.data)
      }));
      this.travelTimeSegments = Utils.uniqueBy(travelTimes, item => item.data.linkId).map(d => ({
        linkId: d.data.linkId,
        name: d.data.shortName
      }));

      const wazeAlerts = incident.items.filter(item => item.type === Constants.DATA_WAZE_ALERTS);
      this.wazeAlerts = this.composeWazeAlerts(wazeAlerts);

      const restrictions = incident.items.filter(item => item.type === Constants.DATA_RESTRICTION);
      this.restrictions = this.composeRestrictions(restrictions);

      const alerts = incident.items.filter(item => item.type === Constants.DATA_TRAFFIC_ALERT);
      this.alerts = alerts.map(item => item.data);

      // Videos
      const videos = incident.items.filter(item => item.type === Constants.DATA_TRAFFIC_VIDEO);
      this.videos = videos.map(item => item.data);
      this.videoCameras = Utils.uniqueBy(videos, item => item.data.camera).map(d => ({
        linkId: d.data.linkId,
        name: d.data.camera
      }));

      // Weather
      const weather = incident.items.find(item => item.type === Constants.DATA_WEATHER);

      // Update tab visibility
      if (this.flowInfoList.length === 0) {
        this.removeTabByName('flow');
      } else {
        // Update chart display
        this.time = new Date(this.incident.startTime);
        this.timeSlots = [this.incident.startTime, this.incident.endTime];
      }

      if (this.travelTimeInfoList.length === 0) {
        this.removeTabByName('travelTime');
      } else {
        // Update chart display
        const i = travelTimes[0].data;
        this.time = new Date(i.time);
        this.timeSlots = [incident.startTime, incident.endTime];
      }

      if (this.wazeAlerts.length === 0) {
        this.removeTabByName('waze');
      }

      if (this.restrictions.length === 0) {
        this.removeTabByName('restrictions');
      }

      if (this.alerts.length === 0) {
        this.removeTabByName('alerts');
      }

      if (this.videos.length === 0) {
        this.removeTabByName('video');
      }

      // Setup weather tab
      if (weather) {
        this.weatherInfo = this.formWeatherInfoData(weather.data);
      } else {
        this.removeTabByName('weather');
      }

      if (this.items.length > 0) {
        this.tab = this.items[0].key;
      }

      this.currentTrvelTimeEvidenceIdx = 0;
    },

    showFlowChart(params) {
      this.$store.commit('traffic/SET_CURRENT_FLOW_CHART_PARAMS', params);
      this.$store.commit('traffic/SHOW_FLOW_CHART', true);
    },

    formFlowChartParamInfo(i) {
      return {
        deviceId: i.deviceId,
        direction: i.direction,
        speed: i.speed,
        volume: i.volume,
        occupancy: i.occupancy,
        mdist: i.mdist,
        status: i.status,
        time: i.time
      };
    },

    composeFlowInfo(i) {
      const color = Constants.INFO_BAR_DEFAULT_COLOR;
      //  const detector = { 'Detector Id': i.deviceId, Direction: i.direction };
      const currentFlow = { speed: i.speed + ' mph', volume: i.volume, occupancy: i.occupancy + '%' };
      const historicFlow = {
        'avg Speed': i.avgSpd + ' mph',
        'avg Volume': i.avgVol,
        'avg Occupancy': i.avgOcc + '%'
      };

      const anomalyDetect = { mdist: i.mdist.toFixed(2), severity: i.severity };
      const statusName = `Status ${i.status}`;
      const text = i.status > 0 ? 'Anomaly' : 'Normal';
      anomalyDetect[statusName] = i.status > 0 ? { text } : text;

      // Flow status
      const flowStatus = {};
      const name = `Flow Status ${i.breakdown ? i.flowLevel : ''}`;
      flowStatus[name] = i.flowStatus;
      flowStatus['vol + occ'] = i.vo + '%';
      if (i.breakdown) {
        flowStatus['Breakdown Probability'] = i.breakdown + '%';
      } else {
        flowStatus['Flow Level'] = i.flowLevel;
      }

      const result = [];
      // result.push({ title: 'Sensor Info', color: color, items: Utils.obj2Arr(detector) });

      if (!Utils.isValueAllEmpty(currentFlow)) {
        result.push({ title: 'Current Flow', color: color, items: Utils.obj2Arr(currentFlow) });
      }

      if (!Utils.isValueAllEmpty(historicFlow)) {
        result.push({ title: 'Historic Flow', color: color, items: Utils.obj2Arr(historicFlow) });
      }

      if (!Utils.isValueAllEmpty(flowStatus)) {
        let currentColor = `level-${i.flowLevel}`;
        result.push({ title: 'Flow Status', color: currentColor, items: Utils.obj2Arr(flowStatus) });
      }

      if (!Utils.isValueAllEmpty(anomalyDetect)) {
        const params = this.formFlowChartParamInfo(i);
        const anomalyButton = {
          color: '#ff8080',
          icon: 'mdi-help-circle-outline',
          handler: this.showFlowChart,
          params: params
        };

        let currentColor = i.status > 0 ? 'red lighten-2' : color;
        result.push({
          title: 'Anomaly',
          button: anomalyButton,
          color: currentColor,
          items: Utils.obj2Arr(anomalyDetect)
        });
      }

      return result;
    },

    composeTravelTimeInfo(info) {
      const current = {
        'Travel Time': info.travelTime ? this.formatDuration(info.travelTime) : null,
        'Average Speed': info.avgSpeed ? Math.round(info.avgSpeed) + ' mph' : null
      };

      const baseline = {
        'Travel Time': info.meanTravelTime ? this.formatDuration(info.meanTravelTime) : null,
        'Average Speed': info.avgLinkSpeed ? info.avgLinkSpeed + ' mph' : null
      };

      let status = {
        'Max Travel Time': this.formatDuration(info.max),
        'Actual Wait Time': this.formatDuration(info.waitTime)
      };

      const text = info.status === 7 ? 'Anomaly' : 'Normal';
      const anomaly = {
        status: info.status ? (info.status === 7 ? { text } : text) : null,
        updated: info.updated ? Utils.formatTime(new Date(info.updated)) : null
      };

      const result = [];
      const color = Constants.INFO_BAR_DEFAULT_COLOR;

      if (!Utils.isValueAllEmpty(current)) {
        result.push({ title: 'Current', color: color, items: Utils.obj2Arr(current) });
      }

      if (!Utils.isValueAllEmpty(baseline)) {
        result.push({ title: 'Baseline', color: color, items: Utils.obj2Arr(baseline) });
      }

      if (!Utils.isValueAllEmpty(status)) {
        result.push({ title: 'Status', color: color, items: Utils.obj2Arr(status) });
      }

      if (!Utils.isValueAllEmpty(anomaly)) {
        result.push({ title: 'Detection', color: 'red lighten-2', items: Utils.obj2Arr(anomaly) });
      }

      return result;
    },

    composeRestrictions(data) {
      return data.map(d => ({
        id: d.data.restrictionId,
        name: d.data.impactType,
        time: d.time,
        dist: this.formatDistance(d.data.dist),
        loc: d.data.location,
        position: d.data.position,
        desc: d.data.description,
        linkId: d.data.linkId
      }));
    },

    composeWazeAlerts(data) {
      const WazeTypes = {};
      Object.keys(WazeAlertTypes).forEach(key => {
        WazeTypes[WazeAlertTypes[key]] = key;
      });

      const wazeIds = new Set();
      const result = [];
      data.forEach(item => {
        const linkId = item.data.linkId;
        const type = this.getWazeAlertType(item.data.alertType, null, WazeTypes);
        const details = JSON.parse(item.data.details);

        details.forEach(d => {
          const id = d.id;
          const desc = d.desc;
          if (!Utils.isStringEmpty(desc) && !wazeIds.has(id)) {
            wazeIds.add(id);
            const tt = d.time;
            const time = Utils.formatAMPMTime(new Date(d.time));
            const dist = this.formatDistance(d.dist);
            const position = { lat: d.lat, lng: d.lon };
            const [reportRating, confidence, reliability, thumbsup] = d.val.split(',').map(Number);
            result.push({
              id,
              tt,
              type,
              desc,
              time,
              position,
              dist,
              reportRating,
              confidence,
              reliability,
              thumbsup,
              linkId
            });
          }
        });
      });

      result.sort((a, b) => a.tt - b.tt);

      return result;
    },

    getWazeAlertType(type, subtype, WazeTypes) {
      const wazeType = subtype ? subtype : type;
      const typeName = WazeTypes[wazeType];
      return Utils.toTitleCase(typeName.replaceAll('_', ' '));
    },

    formWeatherInfoData(info) {
      const result = {};
      Object.assign(result, info);
      return result;
    },

    formatDuration(seconds) {
      let minutes = seconds / 60;
      return `${minutes.toFixed(1)} min`;
    },

    formatDistance(dist) {
      const value = (dist / 1609).toFixed(2);
      return `${value} miles`;
    }
  }
};
</script>

<style lang="scss" scoped>
.whole-box {
  min-height: 550px;
  padding-bottom: 20px;
}

.card-scroll {
  overflow-x: hidden;
  overflow-y: auto;
  max-height: 470px;
  margin: 8px 10px;
}
</style>

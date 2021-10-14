<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="900px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-car</v-icon>
          <v-toolbar-title
            >{{ title }} <small>{{ subtitle }} </small></v-toolbar-title
          >
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container style="height:620px">
          <v-tabs color="teal accent-4" v-model="tab">
            <v-tab v-for="item in items" :key="item.key" :href="`#${item.key}`">
              {{ item.value }}
            </v-tab>
          </v-tabs>
          <div>
            <v-tabs-items v-model="tab">
              <v-tab-item value="map">
                <v-container>
                  <v-card class="elevation-5 map-segment">
                    <AnomalyMap
                      ref="mapSegmentRef"
                      :segment="segment"
                      :markers="markers"
                      @click="onMapClick"
                      v-if="isMapTab"
                    />
                  </v-card>
                </v-container>
              </v-tab-item>

              <v-tab-item value="flow" v-if="isTabVisible('flow')">
                <div class="mx-3 my-0">
                  <ScrollableCards :infoList="flowInfoList" />
                </div>
              </v-tab-item>

              <v-tab-item value="travelTime" v-if="isTabVisible('travelTime')">
                <div class="mx-3 my-0">
                  <ScrollableCards :infoList="travelTimeInfoList" />
                </div>
              </v-tab-item>

              <v-tab-item value="waze" v-if="isTabVisible('waze')">
                <div class="card-scroll">
                  <v-row>
                    <v-col cols="6" v-for="(waze, i) in wazeAlerts" :key="i">
                      <WazeInfo class="mx-1" :waze="waze" />
                    </v-col>
                  </v-row>
                </div>
              </v-tab-item>

              <v-tab-item value="restrictions" v-if="isTabVisible('restrictions')">
                <div class="card-scroll">
                  <v-row>
                    <v-col cols="12" v-for="(res, i) in restrictions" :key="i">
                      <RestrictionInfo class="mx-1" :restriction="res" />
                    </v-col>
                  </v-row>
                </div>
              </v-tab-item>

              <v-tab-item value="weather" v-if="isTabVisible('weather')">
                <div class="ma-2" min-height="490px" v-if="validWeatherInfo">
                  <WeatherInfo :weather="weatherInfo" />
                </div>
              </v-tab-item>

              <v-tab-item value="cameras">
                <div v-if="cameras">
                  <div class="mx-16 mb-n2 mt-n1">
                    <v-radio-group v-model="cameraSelect" dense row @change="videoUrlChanged">
                      <v-radio v-for="(id, i) in cameraIds" :label="id" :value="id" :key="i"></v-radio>
                    </v-radio-group>
                  </div>
                  <div class="mx-16">
                    <VideoPlayer ref="videoPlayer" :options="playerOptions" v-if="isCameraTab" />
                  </div>
                </div>
              </v-tab-item>
            </v-tabs-items>
          </div>
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Utils from '@/utils/Utils';
import WazeAlertTypes from '@/utils/WazeAlertTypes';
import Constants from '@/utils/constants/traffic';
import ScrollableCards from '@/components/modules/traffic/dashboard/ScrollableCards';
import WeatherInfo from '@/components/modules/traffic/common/WeatherInfo';
import WazeInfo from '@/components/modules/traffic/common/WazeInfo';
import RestrictionInfo from '@/components/modules/traffic/common/RestrictionInfo';
import AnomalyMap from '@/components/modules/traffic/dashboard/AnomalyMap';
import VideoPlayer from '@/components/modules/traffic/common/VideoPlayer';
import { mapState } from 'vuex';
export default {
  props: {
    value: Boolean
  },
  components: {
    WeatherInfo,
    WazeInfo,
    AnomalyMap,
    ScrollableCards,
    VideoPlayer,
    RestrictionInfo
  },
  data: () => ({
    defaultItems: [
      { key: 'map', value: 'Map' },
      { key: 'flow', value: 'Traffic Flow' },
      { key: 'travelTime', value: 'Travel Time' },
      { key: 'waze', value: 'Waze Alerts' },
      { key: 'restrictions', value: 'Restrictions' },
      { key: 'weather', value: 'Weather' },
      { key: 'cameras', value: 'Cameras' }
    ],
    items: [],

    title: '',
    subtitle: '',
    tab: null,
    segment: null,

    cameraSelect: null,
    cameraIds: [],

    flowInfoList: [],
    travelTimeInfoList: [],
    restrictions: [],
    wazeAlerts: [],
    weatherInfo: null,
    markers: []
  }),
  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },

    cameras() {
      return this.cameraIds.length > 0;
    },

    validWeatherInfo() {
      return this.weatherInfo && Object.keys(this.weatherInfo).length > 0;
    },

    isMapTab() {
      return this.tab === 'map';
    },

    isCameraTab() {
      return this.tab === 'cameras';
    },

    playerOptions() {
      return {
        // videojs options
        muted: true,
        language: 'en',
        playbackRates: [0.5, 1.0, 2.0, 3.0],
        controlBar: {
          fullscreenToggle: true,
          playbackRateMenuButton: true,
          pictureInPictureToggle: false,
          volumePanel: false
        },
        sources: [
          {
            withCredentials: false,
            type: 'application/x-mpegURL',
            src: this.url
          }
        ],
        poster: require('@/assets/loading.gif')
      };
    },

    ...mapState('traffic', ['weatherStations'])
  },

  filters: {
    dateAndTime(t) {
      const d = new Date(t);
      return Utils.formatDateTime(d);
    }
  },

  methods: {
    init(segment) {
      this.tab = 'map';
      Object.assign(this.items, this.defaultItems);

      if (segment) {
        this.segment = segment;

        // Setup title
        this.title = 'Anomaly Segment: ';
        this.subtitle = (segment.shortName + '').toUpperCase();

        // Setup data tabs
        this.prepareDataForDisplay(segment);
        //this.$refs.mapSegmentRef.centerMapAndZoom();
      }
    },

    cleanUp() {
      this.tab = 'weather';
    },

    hideDialog() {
      this.show = false;
      this.cleanUp();
    },

    isTabVisible(name) {
      const ii = this.items.find(i => i.key === name);
      return ii !== undefined;
    },

    videoUrlChanged() {
      this.changeVideoSource(this.getVideoUrl(this.cameraSelect));
    },

    changeVideoSource(url) {
      this.url = url;
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.setUrl(url);
      }
    },

    removeTabByName(name) {
      const e = this.items.findIndex(item => item.key === name);
      if (e >= 0) {
        this.items.splice(e, 1);
      }
    },

    getVideoUrl(cameraId) {
      return `http://167.21.72.35:1935/live/${cameraId}.stream/playlist.m3u8`;
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
      } else if (marker.id.startsWith('C')) {
        this.tab = 'cameras';
        const cameraId = marker.id.substring(1);
        const selectedId = this.cameraIds.find(id => id === cameraId);
        if (selectedId) {
          this.cameraSelect = selectedId;
          this.changeVideoSource(this.getVideoUrl(this.cameraSelect));
        }
      }
    },

    prepareDataForDisplay(segment) {
      const flows = segment.items.filter(item => item.type === Constants.DATA_TRAFFIC_FLOW);
      this.flowInfoList = flows.map(d => ({
        time: d.data.time,
        device: d.data.deviceId + d.data.direction,
        data: this.composeFlowInfo(d.data)
      }));

      const travelTimes = segment.items.filter(item => item.type === Constants.DATA_TRAVEL_TIME);
      this.travelTimeInfoList = travelTimes.map(d => ({
        time: d.data.time,
        data: this.composeTravelTimeInfo(d.data)
      }));

      const wazeAlerts = segment.items.filter(item => item.type === Constants.DATA_WAZE_ALERTS);
      this.wazeAlerts = this.composeWazeAlerts(wazeAlerts);

      const restrictions = segment.items.filter(item => item.type === Constants.DATA_RESTRICTION);
      this.restrictions = this.composeRestrictions(restrictions);

      const weather = segment.items.find(item => item.type === Constants.DATA_WEATHER);

      // Update tab visibility
      if (this.flowInfoList.length === 0) {
        this.removeTabByName('flow');
      }

      if (this.travelTimeInfoList.length === 0) {
        this.removeTabByName('travelTime');
      }

      if (this.wazeAlerts.length === 0) {
        this.removeTabByName('waze');
      }

      if (this.restrictions.length === 0) {
        this.removeTabByName('restrictions');
      }

      // Setup weather tab
      if (weather) {
        this.weatherInfo = this.formWeatherInfoData(weather.data);
      } else {
        this.removeTabByName('weather');
      }

      // Setup traffic camera tab
      if (segment.info.cameras) {
        this.cameraIds = segment.info.cameras.map(c => c.id);
        if (this.cameraIds.length > 0) {
          this.cameraSelect = this.cameraIds[0];
          this.changeVideoSource(this.getVideoUrl(this.cameraSelect));
        }
      } else {
        this.removeTabByName('cameras');
      }

      this.markers = this.createMarkers(flows, travelTimes);
    },

    createMarkers(flows, travelTimes) {
      const markers = [];
      if (flows.length > 0) {
        flows.forEach(item => {
          markers.push({
            id: 'F' + item.data.id,
            name: item.data.name,
            position: item.data.position,
            icon: 'mapMarkerActive'
          });
        });
      }

      if (travelTimes.length > 0) {
        travelTimes.forEach(item => {
          markers.push({
            id: 'B' + item.data.id,
            name: item.data.name,
            position: item.data.position,
            icon: 'redBluetoothIcon'
          });
        });
      }

      if (this.wazeAlerts.length > 0) {
        this.wazeAlerts.forEach(item => {
          markers.push({
            id: 'A' + item.id,
            name: item.desc,
            position: item.position,
            icon: 'wazeIcon'
          });
        });
      }

      if (this.restrictions.length > 0) {
        this.restrictions.forEach(item => {
          markers.push({
            id: 'R' + item.id,
            name: item.desc,
            position: item.position,
            icon: 'restrictionIcon'
          });
        });
      }

      if (this.segment.info.stationId) {
        const stationId = this.segment.info.stationId;
        const weatherStation = this.weatherStations.find(w => w.id === stationId);
        if (weatherStation) {
          markers.push({
            id: 'W' + weatherStation.id,
            name: weatherStation.name,
            position: weatherStation.position,
            icon: 'weatherIcon'
          });
        }
      }

      if (this.segment.info.cameras) {
        this.segment.info.cameras.forEach(item => {
          markers.push({
            id: 'C' + item.id,
            name: item.name,
            position: item.position,
            icon: 'cameraIcon'
          });
        });
      }

      return markers;
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
      const detector = { 'Detector Id': i.deviceId, Direction: i.direction };
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
        flowStatus['Breakdown Prob.'] = i.breakdown + '%';
      } else {
        flowStatus['Flow Level'] = i.flowLevel;
      }

      const result = [];
      result.push({ title: 'Sensor Info', color: color, items: Utils.obj2Arr(detector) });

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
        desc: d.data.description
      }));
    },

    composeWazeAlerts(data) {
      const WazeTypes = {};
      Object.keys(WazeAlertTypes).forEach(key => {
        WazeTypes[WazeAlertTypes[key]] = key;
      });

      const wazeIds = new Set();
      const result = [];
      data.forEach(anomaly => {
        const type = this.getWazeAlertType(anomaly.data.alertType, null, WazeTypes);
        const details = JSON.parse(anomaly.data.details);

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
            result.push({ id, tt, type, desc, time, position, dist, reportRating, confidence, reliability, thumbsup });
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
.map-segment {
  height: 450px;
  margin-bottom: 20px;
  border-bottom: 2px solid black;
}

.card-scroll {
  overflow-x: hidden;
  overflow-y: auto;
  max-height: 470px;
  margin: 8px 32px;
}
</style>

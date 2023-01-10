<template>
  <div>
    <v-container>
      <v-alert prominent outlined border="left" type="error" v-if="timingPlanChangeNeeded">
        <v-row align="center">
          <v-col class="grow">
            Detour is needed. Timing plan changes can improve the performance. Do you want to proceed?
          </v-col>
          <v-col class="shrink">
            <div class="d-flex flex-column align-content-stretch">
              <v-btn color="error">PROCEED</v-btn>
              <v-btn class="mt-3" color="primary" @click="toggleShowTiming">VIEW OPTIONS</v-btn>
            </div>
          </v-col>
        </v-row>
      </v-alert>

      <v-row id="solution" v-if="showTimingPlan">
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
            <h3>Timing Plans</h3>
          </v-subheader>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <TimingPlan :planId="planId" />
          </div>
        </v-col>
      </v-row>

      <v-row id="info">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Current Info</h3></v-subheader>
            <div class="mt-4 mr-3">
              <div class="d-flex align-center mr-1 mt-n2">
                <v-chip label outlined @click.stop="centerSegment">
                  <span>Incident {{ segment.id }}</span>
                </v-chip>
              </div>
            </div>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-row>
              <v-col cols="6">
                <DataCard title="Time" :items="incidentTime" />
              </v-col>
              <v-col cols="6" v-if="incidentLoc.length > 0">
                <DataCard title="Location" :items="incidentLoc" />
              </v-col>
              <v-col cols="6">
                <IncidentTypeCard title="Type" :item="incidentType" />
              </v-col>
              <v-col cols="6" v-if="incidentConditions.length > 0">
                <DataCard title="Conditions" :items="incidentConditions" />
              </v-col>
            </v-row>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="incidentTimelineSeries.length">
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Timeline</h3></v-subheader>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div :class="$vuetify.breakpoint.mobile ? null : 'mx-4'">
            <v-card tile :elevation="10">
              <IncidentTimeline :items="incidentTimelineSeries" :simple="true" ref="incidentTimeline" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="flowInfoList.length">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Traffic Flow</h3></v-subheader>
          </div>
          <v-divider />
          <div class="mx-4 mt-4">
            <TrafficFlowData
              :infoList="flowInfoList"
              :devices="flowDevices"
              :selectedSegmentId="selectedSegmentId"
              :time="time"
              :timeSlots="timeSlots"
              :interval="interval"
              :includePerLane="false"
              :showEvidenceText="false"
              ref="trafficFlowData"
            />
          </div>
        </v-col>
      </v-row>

      <v-row v-if="travelTimeInfoList.length">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Travel Time</h3></v-subheader>
          </div>
          <v-divider />
          <div class="mx-4 mt-4">
            <TravelTimeData
              :infoList="travelTimeInfoList"
              :segments="travelTimeSegments"
              :selectedSegmentId="selectedSegmentId"
              :time="time"
              :timeSlots="timeSlots"
              :interval="interval"
              :showEvidenceText="false"
              ref="travelTimeData"
            />
          </div>
        </v-col>
      </v-row>

      <v-row v-if="wazeAlerts.length">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Waze Alerts</h3></v-subheader>
          </div>
          <v-divider />
          <div class="card-scroll">
            <v-row>
              <v-col cols="6" v-for="(waze, i) in wazeAlerts" :key="i">
                <WazeInfo class="mx-1" :waze="waze" />
              </v-col>
            </v-row>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="restrictions.length">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Restrictions</h3></v-subheader>
          </div>
          <v-divider />
          <div class="mx-0">
            <div class="card-scroll">
              <v-row>
                <v-col cols="12" v-for="(res, i) in restrictions" :key="i">
                  <RestrictionInfo class="mx-1" :restriction="res" />
                </v-col>
              </v-row>
            </div>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="camerasAvaliable">
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Nearby Cameras</h3></v-subheader>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-btn outlined v-for="id in cameraIds" :key="id" @click.stop="playVideo(id)" class="mr-5">
              <v-icon left color="warning">
                mdi-camera
              </v-icon>
              {{ id }}
            </v-btn>
          </div>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Miscellaneous</h3></v-subheader>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <DataCard :items="incidentMisc" />
          </div>
        </v-col>
      </v-row>
    </v-container>
    <ChartDialog ref="chartDialog" v-model="showChartDialog" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import WazeAlertTypes from '@/utils/WazeAlertTypes';
import Constants from '@/utils/constants/traffic';
import DataCard from '@/components/modules/traffic/common/DataCard';
import IncidentTypeCard from '@/components/modules/traffic/incident/IncidentTypeCard';
import WazeInfo from '@/components/modules/traffic/common/WazeInfo';
import RestrictionInfo from '@/components/modules/traffic/common/RestrictionInfo';
import ChartDialog from '@/components/modules/traffic/common/ChartDialog';
import TimingPlan from '@/components/modules/traffic/dashboard/TimingPlan';
import IncidentTimeline from '@/components/modules/traffic/incident/IncidentTimeline';
import TrafficFlowData from '@/components/modules/traffic/incident/TrafficFlowData';
import TravelTimeData from '@/components/modules/traffic/incident/TravelTimeData';

export default {
  props: {
    segment: Object
  },

  components: {
    DataCard,
    IncidentTypeCard,
    WazeInfo,
    TimingPlan,
    RestrictionInfo,
    ChartDialog,
    TrafficFlowData,
    IncidentTimeline,
    TravelTimeData
  },

  data: () => ({
    loading: false,
    showChartDialog: false,
    showTimingPlan: false,

    height: 300,
    legendY: 5,
    marginLeft: 80,
    cameraIds: [],
    flowDevices: [],
    incidentTimelineSeries: [],
    travelTimeSegments: [],
    flowInfoList: [],
    travelTimeInfoList: [],
    restrictions: [],
    wazeAlerts: [],
    incidentTime: [],
    incidentLoc: [],
    incidentType: {},
    incidentConditions: [],
    incidentMisc: [],
    planId: '',
    simuType: '',

    interval: 300000,
    time: null,
    timeSlots: [],
    selectedSegmentId: null
  }),

  computed: {
    camerasAvaliable() {
      return this.cameraIds.length > 0;
    },
    timingPlanChangeNeeded() {
      return this.segment.incidentState && this.segment.incidentState.timingPlan === 2;
    }
  },

  mounted() {
    this.init(this.segment);
  },

  methods: {
    init(segment) {
      if (segment) {
        // Setup data tabs
        this.prepareDataForDisplay(segment);
      }
    },

    toggleShowTiming() {
      this.planId = 'OPTCYCLE-220';
      this.simuType = 'A';
      this.showTimingPlan = !this.showTimingPlan;
    },

    showSpeedChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Speed', this.speed);
    },

    showVolumeChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Volume', this.volume);
    },

    showOccupancyChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Occupancy', this.occupancy);
    },

    playVideo(id) {
      this.$bus.$emit('PLAY_POPUP_VIDEO', id);
    },

    centerSegment() {
      this.$bus.$emit('MAP_CENTER_SEGMENT', { segment: this.segment });
    },

    prepareDataForDisplay(incident) {
      this.time = new Date(incident.startTime);
      this.timeSlots = [incident.startTime, incident.endTime];

      const flows = incident.items.filter(item => item.type === Constants.DATA_TRAFFIC_FLOW);
      this.flowInfoList = flows.map(d => ({
        time: d.data.time,
        device: d.data.deviceId + d.data.direction,
        data: this.composeFlowInfo(d.data)
      }));

      // Obtain unique devices
      this.flowDevices = Utils.uniqueBy(flows, item => item.data.deviceId + item.data.direction).map(d => ({
        id: d.data.deviceId,
        dir: d.data.direction,
        name: d.data.deviceId + d.data.direction,
        linkId: d.data.linkId
      }));

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

      this.incidentTimelineSeries = incident.timeline;

      // Setup traffic camera tab
      if (incident.info.cameras) {
        this.cameraIds = incident.info.cameras.map(c => c.id);
      }

      this.composeInfoCards(incident);
    },

    composeInfoCards(incident) {
      this.incidentTime = [
        { name: 'Start Time:', value: Utils.formatAMPMTime(incident.startTime) },
        { name: 'End Time:', value: incident.status > 0 ? Utils.formatAMPMTime(incident.endTime) : 'Ongoing' },
        { name: 'Duration:', value: Utils.toHHMM(incident.duration) }
      ];

      if (incident.address) {
        this.incidentLoc = [
          { name: 'Route:', value: `${incident.route} ${incident.direction}` },
          { name: 'Address:', value: incident.address },
          { name: 'Detectors', value: incident.detectors ? incident.detectors.join() : '' }
        ];
      } else {
        this.incidentLoc = [];
      }

      this.incidentType = {
        type: incident.type,
        reason: incident.reason,
        severity: incident.severity,
        severityColor: incident.severityColor,
        evidenceCounts: incident.evidenceCounts
      };

      if (incident.incidentState) {
        let lanes = 'NO';
        if (incident.incidentState.laneBlockage) {
          lanes = Object.keys(incident.incidentState.laneBlockage).join('|');
        }

        // let capacityReduction = '';
        // if (incident.incidentState.capacityReduction && incident.incidentState.capacityReduction > 0) {
        //   capacityReduction = incident.incidentState.capacityReduction + '';
        // }

        this.incidentConditions = [
          { name: 'Blockages:', value: lanes },
          { name: 'Detour Needed:', value: incident.incidentState.detour ? 'YES' : 'NO' },
          { name: 'Timing Plan:', value: this.getTimingPlanChangeFlag(incident.incidentState.timingPlan) }
        ];
      } else {
        this.incidentConditions = [];
      }

      this.incidentMisc = [
        { name: 'Incident Id:', value: incident.id },
        { name: 'Date Created:', value: Utils.formatDateTime(new Date(incident.dateCreated)) },
        { name: 'Last Updated:', value: Utils.formatDateTime(new Date(incident.lastUpdated)) }
      ];
    },

    getTimingPlanChangeFlag(flag) {
      let result = 'NO NEED';
      if (flag) {
        if (flag === 1) {
          result = 'FLAGGED';
        } else if (flag === 2) {
          result = 'TRIGGER';
        }
      }
      return result;
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
      //const detector = { 'Detector Id': i.deviceId, Direction: i.direction, Title: i.title };
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
      //result.push({ title: 'Sensor', color: color, items: Utils.obj2Arr(detector) });

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
  max-height: 200px;
  margin: 4px 12px;
}

.score-value {
  font-size: 65px;
  font-weight: bold;
}

table {
  border-collapse: collapse;
}
th,
td {
  border: 1px solid orange;
  padding: 10px;
  text-align: left;
}
</style>

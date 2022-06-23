<template>
  <div v-if="!reload">
    <v-card tile class="mb-2" v-show="showIncidentTable">
      <IncidentTable :incidents="$store.state.dashboard.trafficIncidents" @click="handleRowClick" />
    </v-card>
    <v-row class="mt-3 ml-1 mr-7" v-if="incidentItem">
      <v-col :offset-lg="singleCol ? 1 : 0" :cols="singleCol ? 10 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :height="cardHeight"
          :flex="singleCol"
          :valueFontSize="singleCol ? 38 : 28"
          :name="'End Time'"
          :value="getTimeStr(incidentItem.endTime)"
        />
      </v-col>
      <v-col :offset-lg="singleCol ? 1 : 0" :cols="singleCol ? 10 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-chart-bar-stacked'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Severity'"
          :value="incidentItem.severity"
          :valueColor="incidentItem.severityColor"
        />
      </v-col>
      <v-col :offset-lg="singleCol ? 1 : 0" :cols="singleCol ? 10 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-map-marker-outline'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Total Evidence'"
          :value="incidentItem.evidenceCount"
        />
      </v-col>
      <v-col :offset-lg="singleCol ? 1 : 0" :cols="singleCol ? 10 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-alert-circle-outline'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Type'"
          :value="incidentItem.type"
        />
      </v-col>
      <v-col :offset-lg="singleCol ? 1 : 0" :cols="singleCol ? 10 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-shield-check-outline'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Reason'"
          :value="incidentItem.reason.split('.')[0] ? incidentItem.reason.split('.')[0] : incidentItem.reason"
          :wide="true"
          :valueFontSize="22"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import InfoCard from '@/components/modules/dashboard/InfoCard';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import { mapState } from 'vuex';
import IncidentTable from '@/components/modules/dashboard/incidents/IncidentTable';

export default {
  props: {
    maxItems: Number,
    infoColumnCount: Number
  },
  components: {
    InfoCard,
    IncidentTable
  },

  data: () => ({
    reload: false,
    width: 600,
    loading: false,
    showIncidentTable: true,

    segments: [],

    incidentItem: null
  }),

  computed: {
    singleCol() {
      return this.infoColumnCount == 1;
    },
    cardHeight() {
      return this.singleCol ? '11vh' : undefined;
    },
    startTimestamp() {
      return Utils.getStartOfDay(this.currentDate).getTime();
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['anomalyDevices', 'weatherStations', 'bluetoothSegments', 'incidentSettings', 'showPanel'])
  },

  methods: {
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)} (${Utils.fromNow(time)})`;
    },
    getTime(ts) {
      if (ts) {
        let date = new Date(ts);
        return Utils.formatTimeAsMinute(date);
      } else {
        return '-';
      }
    },
    getStrokeColor(level) {
      return Utils.getStrokeColor(level);
    },

    handleRowClick(item) {
      console.log(item);
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
        this.$store.state.dashboard.incidentMarkers = this.createMarkers(this.incidentItem);
        this.$store.state.dashboard.incidentSegmentLinks = segments;
        setTimeout(() => {
          this.updateMap(segments);
        }, 50);
      }
    },

    updateMap(segments) {
      if (this.$refs.mapSegmentRef) {
        this.$refs.mapSegmentRef.centerMapAndZoom(segments, true);
      }
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
    }
  }
};
</script>

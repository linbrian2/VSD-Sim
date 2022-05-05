<template>
  <div v-if="!reload">
    <v-card tile class="mb-2" v-show="showIncidentTable">
      <IncidentTable :incidents="$store.state.dashboard.incidents" @click="handleRowClick" />
    </v-card>

    <v-row class="mt-3 ml-1 mr-7" v-if="incidentItem">
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :valueFontSize="28"
          :name="'End Time'"
          :value="getTimeStr(incidentItem.endTime)"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-chart-bar-stacked'"
          :name="'Severity'"
          :value="incidentItem.severity"
          :valueColor="incidentItem.severityColor"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-map-marker-outline'" :name="'Total Evidence'" :value="incidentItem.evidenceCount" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-alert-circle-outline'" :name="'Type'" :value="incidentItem.type" />
      </v-col>
      <v-col cols="12" class="pa-1">
        <InfoCard
          :icon="'mdi-shield-check-outline'"
          :name="'Reason'"
          :value="incidentItem.reason"
          :wide="true"
          :valueFontSize="32"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import InfoCard from '@/components/modules/dashboard/InfoCard';
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import { mapState } from 'vuex';
import IncidentTable from '@/components/modules/dashboard/incidents/IncidentTable';

export default {
  props: {
    maxItems: Number
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
    startTimestamp() {
      return Utils.getStartOfDay(this.currentDate).getTime();
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['anomalyDevices', 'weatherStations', 'bluetoothSegments', 'incidentSettings', 'showPanel'])
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  mounted() {
    if (this.$store.state.dashboard.incidents.length == 0) {
      this.fetchIncidentData(this.currentDate);
    }
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
    onSegmentSelected(segmentId) {
      if (this.$refs.anomalySegmentDisplay) {
        this.$refs.anomalySegmentDisplay.selectSegment(segmentId);
      }
    },

    onMarkerClicked(marker) {
      const type = marker.type;
      if (type) {
        const item = marker.item;
        this.gotoSection(`#${type}`);
        if (this.$refs.anomalySegmentDisplay) {
          this.$refs.anomalySegmentDisplay.selectEvidenceItem(type, item);
        }
      }
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
        this.$store.state.dashboard.incidentMarkers = this.createMarkers(this.incidentItem);
        this.$store.state.dashboard.incidentSegmentLinks = segments;
        setTimeout(() => {
          this.updateMap(segments);
        }, 50);
      }
    },

    gotoSection(target) {
      this.$vuetify.goTo(target);
    },

    updateMap(segments) {
      if (this.$refs.mapSegmentRef) {
        this.$refs.mapSegmentRef.centerMapAndZoom(segments, true);
      }
    },

    async fetchIncidentData(startTime) {
      this.loading = true;
      const { severity, duration } = this.incidentSettings;
      try {
        const start = startTime.getTime();
        const response = await Api.fetchIncidentData(start, 1, severity, duration);
        const data = this.getResponseData(response);
        if (data) {
          this.$store.state.dashboard.incidents = data;
          this.segments = this.createTotalSegments();
        } else {
          this.$store.state.dashboard.incidents = [];
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
      this.$store.state.dashboard.incidents.forEach(item => {
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
    }
  }

  /* watch: {
    bluetoothSegments(segments) {
      if (segments) {
        this.handleRowClick(0);
      }
    }
  } */
};
</script>

<template>
  <div>
    <TitleBar :showMap="false" :showRefresh="false" :title="title" :loading="loading" :refresh="refreshData">
      <template v-slot:right>
        <v-btn small icon @click.stop="goHome" class="float-right mt-2" :loading="loading">
          <v-icon color="white">mdi-home</v-icon>
        </v-btn>
      </template>
    </TitleBar>

    <v-container>
      <v-card tile class="mx-3">
        <MapSegment
          ref="mapSegmentRef"
          :smallMap="$vuetify.breakpoint.mobile"
          :segments="segmentLinks"
          :markers="markers"
          @select="onSegmentSelected"
          @click="onMarkerClicked"
        />
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
import EvidenceListDisplay from '@/components/modules/traffic/incident/EvidenceListDisplay';

export default {
  components: {
    TitleBar,
    MapSegment,
    EvidenceListDisplay
  },

  data: () => ({
    loading: false,
    segments: [],
    markers: [],
    segmentLinks: [],
    incidentItem: null,
    incidentId: -1
  }),

  computed: {
    title() {
      if (this.$vuetify.breakpoint.mobile) {
        return `INCIDENT ${this.incidentId}`;
      }
      return `TRAFFIC INCIDENT - ${this.incidentId}`;
    },
    ...mapState('traffic', ['weatherStations', 'bluetoothSegments'])
  },

  async mounted() {
    if (this.bluetoothSegments.length === 0) {
      await this.fetchBluetoothSegments();
    }

    const id = this.$route.params.id;
    if (id && Utils.isPositiveInteger(id)) {
      this.incidentId = parseInt(id);
      await this.fetchIncident(this.incidentId);
    }
  },

  methods: {
    goHome() {
      this.$router.push('/', () => {});
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
          if (type == 'waze') {
            this.$refs.anomalySegmentDisplay.selectWazeAlert(marker.id);
          }
        }
      }
    },

    singleSegmentSelected(linkId) {
      if (this.$refs.mapSegmentRef) {
        this.$refs.mapSegmentRef.selectLink(linkId);
      }
    },

    setIncidentContent() {
      if (this.$refs.anomalySegmentDisplay) {
        this.$refs.anomalySegmentDisplay.init(this.incidentItem);
      }

      // Compose segment
      const segments = [];
      this.incidentItem.segments.forEach(linkId => {
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
    },

    gotoSection(target) {
      this.$vuetify.goTo(target);
    },

    updateMap(segments) {
      if (this.$refs.mapSegmentRef) {
        this.$refs.mapSegmentRef.centerMapAndZoom(segments, true);
      }
    },

    refreshData() {
      if (this.incidentId > 0) {
        this.fetchIncident(this.incidentId);
      }
    },

    async fetchIncident(id) {
      this.loading = true;
      try {
        const response = await Api.fetchIncident(id, true, true);
        const data = this.getResponseData(response);
        this.incidentItem = data;
        this.setIncidentContent();
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

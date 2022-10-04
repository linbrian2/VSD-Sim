<template>
  <div class="'playback-data-display'">
    <div id="congested-routes" v-if="currentTitle == 'Segment'">
      <v-row class="mt-3 ml-1 mr-7" v-if="selectedSegment">
        <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedSegment.desc">
          <InfoCard
            :icon="'mdi-vector-line'"
            :colDisplay="singleCol"
            :flex="singleCol"
            :height="cardHeight"
            :name="'Name'"
            :titleFontSize="singleCol ? undefined : 20"
            :valueFontSize="singleCol ? undefined : 28"
            :value="selectedSegment.desc"
          />
        </v-col>
        <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedSegment.calculationTimestamp">
          <InfoCard
            :icon="'mdi-clock-outline'"
            :colDisplay="singleCol"
            :flex="singleCol"
            :height="cardHeight"
            :name="'Time'"
            :titleFontSize="singleCol ? undefined : 20"
            :valueFontSize="singleCol ? undefined : 28"
            :value="getTimeStr(selectedSegment.calculationTimestamp.value)"
          />
        </v-col>
        <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedSegment.level">
          <InfoCard
            :icon="'mdi-chart-bar-stacked'"
            :colDisplay="singleCol"
            :flex="singleCol"
            :height="cardHeight"
            :name="'Severity'"
            :valueColor="getStrokeColor(selectedSegment.level)"
            :value="selectedSegment.level"
          />
        </v-col>
        <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedSegment.distance">
          <InfoCard
            :icon="'mdi-vector-line'"
            :colDisplay="singleCol"
            :flex="singleCol"
            :height="cardHeight"
            :name="'Distance'"
            :titleFontSize="singleCol ? undefined : 20"
            :valueFontSize="singleCol ? 38 : 28"
            :value="`${selectedSegment.distance} mi`"
          />
        </v-col>
        <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedSegment.speedMph">
          <InfoCard
            :icon="'mdi-speedometer'"
            :colDisplay="singleCol"
            :flex="singleCol"
            :height="cardHeight"
            :name="'Speed'"
            :titleFontSize="singleCol ? undefined : 20"
            :valueFontSize="singleCol ? 38 : 28"
            :value="`${selectedSegment.speedMph.toFixed(2)} mph`"
          />
        </v-col>
        <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedSegment.freeFlowRunTimeSecs">
          <InfoCard
            :icon="'mdi-timer-outline'"
            :colDisplay="singleCol"
            :flex="singleCol"
            :height="cardHeight"
            :name="'Free Flow Run TIme'"
            :titleFontSize="singleCol ? undefined : 20"
            :valueFontSize="singleCol ? 38 : 28"
            :value="getDurStr(selectedSegment.freeFlowRunTimeSecs)"
          />
        </v-col>
        <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedSegment.meanTravelTimeSecs">
          <InfoCard
            :icon="'mdi-timer-outline'"
            :colDisplay="singleCol"
            :flex="singleCol"
            :height="cardHeight"
            :name="'Travel Time Mean'"
            :titleFontSize="singleCol ? undefined : 20"
            :valueFontSize="singleCol ? 38 : 28"
            :value="getDurStr(selectedSegment.meanTravelTimeSecs)"
          />
        </v-col>
      </v-row>
    </div>
    <div id="waze-alerts" v-if="currentTitle == 'Waze Alert'">
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedWaze.description">
        <InfoCard
          :icon="'mdi-note'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Name'"
          :valueFontSize="singleCol ? undefined : 28"
          :value="selectedWaze.description"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedWaze.alertTimeTS">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Time'"
          :valueFontSize="singleCol ? undefined : 28"
          :value="getTimeStr(selectedWaze.alertTimeTS)"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedWaze.confidence">
        <InfoCard
          :icon="'mdi-account-multiple-check'"
          :valueColor="getStrokeColor(selectedWaze.confidence)"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Score'"
          :value="selectedWaze.confidence"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedWaze.alertType">
        <InfoCard
          :icon="'mdi-alert-circle-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Alert Type'"
          :value="selectedWaze.alertType.name"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedWaze.roadType">
        <InfoCard
          :icon="'mdi-map-marker-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Address'"
          :value="selectedWaze.roadType.name"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedWaze.reliability">
        <InfoCard
          :icon="'mdi-check'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Reliability'"
          :value="selectedWaze.reliability"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedWaze.thumbsUp">
        <InfoCard
          :icon="'mdi-thumb-up-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Thumbs Up'"
          :value="selectedWaze.thumbsUp"
        />
      </v-col>
    </div>
    <div id="devices" v-if="currentTitle == 'Device'">
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedDevice.desc">
        <InfoCard
          :icon="'mdi-note'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Name'"
          :valueFontSize="singleCol ? undefined : 28"
          :value="selectedDevice.desc"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedDevice.createdTime">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Time'"
          :valueFontSize="singleCol ? undefined : 28"
          :value="getTimeStr(selectedDevice.createdTime)"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedDevice.level">
        <InfoCard
          :icon="'mdi-chart-bar-stacked'"
          :valueColor="getDeviceColor(selectedDevice.level)"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Level'"
          :value="selectedDevice.level"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedDevice.direction">
        <InfoCard
          :icon="'mdi-compass-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Direction'"
          :value="selectedDevice.direction"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedDevice.occupancy">
        <InfoCard
          :icon="'mdi-account-multiple'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Occupancy'"
          :value="selectedDevice.occupancy"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedDevice.volume">
        <InfoCard
          :icon="'mdi-car-multiple'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Volume'"
          :value="selectedDevice.volume"
        />
      </v-col>
    </div>
    <div id="incidents" v-if="currentTitle == 'Traffic Incident'">
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident">
        <InfoCard
          :icon="'mdi-note-outline'"
          :height="cardHeight"
          :colDisplay="singleCol"
          :flex="singleCol"
          :valueFontSize="singleCol ? undefined : 28"
          :name="'Incident Id'"
          :value="selectedIncident.id"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident">
        <InfoCard
          :icon="'mdi-vector-line'"
          :height="cardHeight"
          :colDisplay="singleCol"
          :flex="singleCol"
          :valueFontSize="singleCol ? undefined : 28"
          :name="'Route'"
          :value="selectedIncident.route"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :height="cardHeight"
          :colDisplay="singleCol"
          :flex="singleCol"
          :valueFontSize="singleCol ? undefined : 28"
          :name="'Start Time'"
          :value="getTimeStr(selectedIncident.startTime)"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :height="cardHeight"
          :colDisplay="singleCol"
          :flex="singleCol"
          :valueFontSize="singleCol ? undefined : 28"
          :name="'End Time'"
          :value="getTimeStr(selectedIncident.endTime)"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident">
        <InfoCard
          :icon="'mdi-timer-outline'"
          :height="cardHeight"
          :colDisplay="singleCol"
          :flex="singleCol"
          :valueFontSize="singleCol ? undefined : 28"
          :name="'Duration'"
          :value="getDurStr(selectedIncident.duration * 60)"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident">
        <InfoCard
          :icon="'mdi-note-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Severity'"
          :value="selectedIncident.severity"
          :valueColor="selectedIncident.severityColor"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident">
        <InfoCard
          :icon="'mdi-note-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Evidence Counts'"
          :value="selectedIncident.evidenceCounts"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident">
        <InfoCard
          :icon="'mdi-alert-circle-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Type'"
          :value="selectedIncident.type"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1" v-if="selectedIncident && selectedIncident.reason">
        <InfoCard
          :icon="'mdi-note-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Reason'"
          :value="
            selectedIncident.reason.split('.')[0] ? selectedIncident.reason.split('.')[0] : selectedIncident.reason
          "
          :wide="true"
          :valueFontSize="singleCol ? 22 : 22"
        />
      </v-col>
    </div>
  </div>
</template>

<script>
import InfoCard from '@/components/modules/dashboard/InfoCard';
import Utils from '@/utils/Utils.js';
import { mapGetters } from 'vuex';

export default {
  components: { InfoCard },
  props: {
    currentTitle: {
      type: String,
      default: ''
    },
    selectedSegment: {
      type: Object,
      default: null
    },
    selectedWaze: {
      type: Object,
      default: null
    },
    selectedDevice: {
      type: Object,
      default: null
    },
    selectedIncident: {
      type: Object,
      default: null
    }
  },
  computed: {
    infoColumnCount() {
      return this.getSetting('dashboard', 'infoColumnCount');
    },
    singleCol() {
      return this.infoColumnCount == 1;
    },
    cardHeight() {
      return this.singleCol ? '11vh' : undefined;
    },
    ...mapGetters(['getSetting'])
  },
  methods: {
    getDurStr(dur) {
      return Utils.durationToTimeStr(dur);
    },
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)}`;
    },
    getStrokeColor(level) {
      return Utils.getStrokeColor(level);
    },
    getDeviceColor(level) {
      let deviceIcons = ['#339900', '#00FF33', '#FFFF00', '#FFC031', '#FA7A2A', '#FF0000', '#C00000'];
      if (level >= 0 && level < deviceIcons.length) {
        return deviceIcons[level];
      } else {
        return deviceIcons[0];
      }
    }
  }
};
</script>

<style></style>

<template>
  <div>
    <!-- Left map panel -->
    <SelectionPanel :width="width" name="incidentSideBarWidth">
      <MapSegment
        ref="mapSegmentRef"
        :segments="segmentLinks"
        :markers="markers"
        :detourPath="detourRoute"
        @select="onSegmentSelected"
        @click="onMarkerClicked"
      />
    </SelectionPanel>

    <TitleBar title="Mitigation Solution" :loading="loading" :refresh="refreshData">
      <div class="d-flex justify-space-between">
        <div class="d-flex justify-space-between ml-15 mt-2 mr-15">
          <div class="font-weight-bold overline ml-3">Incident {{ $route.params.id }}</div>
        </div>
      </div>
    </TitleBar>

    <v-container>
      <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
        ><h3>Recommended Timing Plan</h3>
      </v-subheader>
      <v-divider />

      <v-card class="ma-4">
        <div class="d-flex justify-space-between">
          <div>
            <v-tabs color="blue accent-4" v-model="solutionTab" show-arrows>
              <v-tab v-for="(solution, time, i) in solutions" :key="i">
                <v-chip color="purple" label>{{ formatTimestamp(time, true) }}</v-chip>
              </v-tab>
            </v-tabs>
          </div>

          <div class="mr-4">
            <MenuSelector
              title="Time Offset"
              unit="min"
              :items="timeOffsetItems"
              :selectedItem="selectedTimeOffset"
              @click="timeOffsetSelected"
            />
          </div>
        </div>

        <div>
          <div class="ma-4">
            <v-card tile>
              <v-card-title class="d-flex justify-space-between">
                <div class="subtitle-1 ml-n4">Current Plan & MOE</div>
                <div class="caption grey--text"></div>
              </v-card-title>
            </v-card>
            <SignalGroupTable :items="currentSolution" color="green" href="currentGroupTable" />
          </div>

          <div class="ma-4">
            <v-card tile>
              <v-card-title class="d-flex justify-space-between">
                <div class="subtitle-1 ml-n4">Recommended Plan & MOE</div>
                <div class="d-flex justify-space-between mr-n4">
                  <MenuSelector
                    title="Cycle Length"
                    unit="s"
                    :items="cycleLengthItems"
                    :selectedItem="selectedCycleLength"
                    :highlightedItem="optimalCycleLength"
                    @click="cycleLengthSelected"
                  />
                  <v-btn small color="blue" class="mt-3 ml-12" @click="handleRejected">
                    <v-icon small left>mdi-check</v-icon> Implemented
                  </v-btn>
                </div>
              </v-card-title>
            </v-card>
            <SignalGroupTable :items="recommendedSolution" color="cyan darken-3" href="recommendedGroupTable" />
          </div>
        </div>
      </v-card>

      <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
        <h3>Travel Delay Comparison</h3>
      </v-subheader>
      <v-divider />

      <div class="ma-4">
        <v-card>
          <SignalDelayChart :data="signalDelays" :height="350" />
        </v-card>
      </div>

      <v-subheader class="pl-0  d-flex justify-space-between">
        <h3 class="mx-4 font-weight-bold text-overline blue--text">Traffic Flow Evolution</h3>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn icon dark v-on="on" @click="loadMoreInfo"><v-icon>mdi-dots-vertical</v-icon></v-btn>
          </template>
          <span>More Info</span>
        </v-tooltip>
      </v-subheader>
      <v-divider />

      <div class="mx-4">
        <v-row>
          <v-col :cols="6">
            <v-card>
              <BasicChart :data="offRampData" :height="height" />
            </v-card>
          </v-col>

          <v-col :cols="6">
            <v-card>
              <BasicChart :data="travelTimeData" :height="height" />
            </v-card>
          </v-col>

          <v-col :cols="6">
            <v-card>
              <BasicChart :data="onRampData" :height="height" />
            </v-card>
          </v-col>

          <v-col :cols="6">
            <v-card>
              <BasicChart :data="demandData" :height="height" />
            </v-card>
          </v-col>
        </v-row>
      </div>

      <div id="volumes" v-if="rampTrafficVolumesData.length > 0">
        <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
          ><h3>Ramp Traffic Volumes</h3>
        </v-subheader>
        <v-divider />

        <div class="mx-4">
          <div v-for="(volumeData, i) in rampTrafficVolumesData" :key="i">
            <v-card class="mb-6">
              <BasicChart :data="volumeData" :height="height" />
            </v-card>
          </div>
        </div>
      </div>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import { mapState, mapActions } from 'vuex';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import MapSegment from '@/components/modules/traffic/mitigation/MapSegment';
import SignalGroupTable from '@/components/modules/traffic/mitigation/SignalGroupTable';
import SignalDelayChart from '@/components/modules/traffic/mitigation/SignalDelayChart';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import MenuSelector from '@/components/modules/traffic/common/MenuSelector';

export default {
  components: {
    TitleBar,
    MapSegment,
    BasicChart,
    MenuSelector,
    SelectionPanel,
    SignalGroupTable,
    SignalDelayChart
  },

  data: () => ({
    width: 600,
    height: 320,
    bandColor: 'rgba(68, 70, 213, 0.2)',
    loading: false,
    incidentId: null,
    solutionTab: null,
    segmentLinks: [],
    incidentItem: null,
    signalDevices: null,
    baseline: {},
    solutions: {},
    markers: [],
    detourRoute: [],
    onRampData: {},
    offRampData: {},
    travelTimeData: {},
    demandData: {},
    rampTrafficVolumesData: {},
    currentSolution: [],
    recommendedSolution: [],
    currentSolutionId: -1,
    selectedCycleLength: 120,
    optimalCycleLength: -1,
    cycleLengthItems: [],
    defaultCycleLengthItems: [120, 130, 140, 150, 160, 170, 180, 190, 200, 210],
    selectedTimeOffset: 0,
    timeOffsetItems: [0, 15, 30, 45],
    signalDelays: {}
  }),

  computed: {
    startTimestamp() {
      return Utils.getStartOfDay(this.currentDate).getTime();
    },

    maxWidth() {
      return this.showPanel ? '100%' : '85%';
    },

    hintText() {
      const keys = Object.keys(this.solutions);
      if (this.solutionTab >= 0 && this.solutionTab < keys.length) {
        const timestamp = keys[this.solutionTab];
        const evalTime = this.formatTimestamp(timestamp, false);
        return `Evaluation Time: <u>${evalTime}</u>, Time Offset: <u>${this.selectedTimeOffset}min</u>, Cycle Length: <u>${this.selectedCycleLength}s</u>`;
      }
      return '';
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['bluetoothSegments', 'showPanel'])
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  mounted() {
    if (this.bluetoothSegments.length === 0) {
      this.fetchBluetoothSegments();
    }

    const id = this.$route.params.id;
    if (id && this.isPositiveInteger(id)) {
      const incidentId = parseInt(id);
      this.incidentId = incidentId;
      this.fetchMitigationSolution(incidentId);
    }
  },

  watch: {
    solutionTab() {
      this.selectedTimeOffset = this.timeOffsetItems[0];
      this.updateCycleLengths(this.solutionTab, this.selectedTimeOffset);
      this.selectedCycleLength =
        this.optimalCycleLength > 0 ? this.optimalCycleLength : parseInt(this.cycleLengthItems[0]);
      this.updateCurrentSolution();
    },

    selectedTimeOffset() {
      this.updateCycleLengths(this.solutionTab, this.selectedTimeOffset);
      this.updateCurrentSolution();
    },

    selectedCycleLength() {
      this.updateCurrentSolution();
    }
  },

  methods: {
    isPositiveInteger(str) {
      if (typeof str !== 'string') {
        return false;
      }

      const num = Number(str);
      if (Number.isInteger(num) && num > 0) {
        return true;
      }

      return false;
    },

    formatTimestamp(timestamp, timeOnly) {
      if (timeOnly) {
        return timestamp ? Utils.formatTimeAsMinute(new Date(parseInt(timestamp))) : '';
      } else {
        return timestamp ? Utils.formatDateTime(new Date(parseInt(timestamp))) : '';
      }
    },

    timeOffsetSelected(timeOffset) {
      this.selectedTimeOffset = timeOffset;
    },

    cycleLengthSelected(cycleLength) {
      this.selectedCycleLength = parseInt(cycleLength);
    },

    updateCurrentSolution() {
      if (this.solutionTab >= 0 && this.selectedTimeOffset >= 0 && this.selectedCycleLength >= 0) {
        const keys = Object.keys(this.baseline);
        const key = keys[this.solutionTab];

        this.currentSolution = this.baseline[key][this.selectedTimeOffset];

        const cycles = this.solutions[key][this.selectedTimeOffset];
        this.cycleLengthItems = Object.keys(cycles);
        this.recommendedSolution = cycles[this.selectedCycleLength];

        this.signalDelays = this.composeSignalDelays();
      }
    },

    updateCycleLengths(solutionTab, timeOffset) {
      const keys = Object.keys(this.solutions);
      const key = keys[solutionTab];
      const cycles = this.solutions[key][timeOffset];
      this.cycleLengthItems = Object.keys(cycles);
    },

    onSegmentSelected(segmentId) {
      if (this.$refs.anomalySegmentDisplay) {
        this.$refs.anomalySegmentDisplay.selectSegment(segmentId);
      }
    },

    loadMoreInfo() {
      if (this.incidentId != null) {
        this.fetchMitigationRampVolumes(this.incidentId);
      }
    },

    onMarkerClicked(marker) {
      if (marker.id.startsWith('S')) {
        this.$refs.solutionTable.expandRowByPermit(marker.name);
      }
    },

    singleSegmentSelected(linkId) {
      if (this.$refs.mapSegmentRef) {
        this.$refs.mapSegmentRef.selectLink(linkId);
      }
    },

    composeSignalDelays() {
      const title = 'Travel Time Delay Comparison';
      const xAxis = 'Traffic Signals';
      const yAxis = 'Time Delay (s)';
      const colors = ['#43A047', '#0097A7'];

      const currentDelays = {};
      this.currentSolution.forEach(s => {
        currentDelays[s.permitNumber] = [s.avgDelay];
      });
      this.recommendedSolution.forEach(s => {
        const items = currentDelays[s.permitNumber];
        if (items && items.length > 0) {
          items.push(s.avgDelay);
        }
      });

      const categories = [];
      const data1 = [];
      const data2 = [];
      for (const [key, value] of Object.entries(currentDelays)) {
        categories.push(key);
        data1.push(value.length > 0 ? value[0] : null);
        data2.push(value.length > 1 ? value[1] : null);
      }

      const series = [
        {
          name: 'Current Plan',
          data: data1
        },
        {
          name: 'Recommended Plan',
          data: data2
        }
      ];

      return { series, xAxis, yAxis, title, categories, colors };
    },

    prepareIncidentData(item) {
      this.incidentItem = Object.assign({}, item);

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
    },

    updateMap(segments) {
      if (this.$refs.mapSegmentRef) {
        this.$refs.mapSegmentRef.centerMapAndZoom(segments, true);
      }
    },

    refreshData() {
      //this.fetchIncidentData(this.currentDate);
    },

    async fetchMitigationSolution(id) {
      this.loading = true;
      try {
        const response = await Api.fetchMitigationSolution(id);
        const data = this.getResponseData(response);
        if (data) {
          console.log(data);
          this.signalDevices = data.signals;
          this.prepareIncidentData(data.incident);
          this.detourRoute = data.detourRoute;

          this.onRampData = this.formOnRampData(data.mitigation);
          this.offRampData = this.formOffRampData(data.mitigation);
          this.travelTimeData = this.formTravelTimeData(data.mitigation);
          this.demandData = this.formDemandData(data.mitigation);

          this.baseline = data.baseline;
          this.solutions = data.solutions;
          this.optimalCycleLength = data.optimalCycleLength;
        } else {
          this.incidentItem = null;
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    async fetchMitigationRampVolumes(id) {
      this.loading = true;
      try {
        const response = await Api.fetchMitigationRampVolumes(id);
        const data = this.getResponseData(response);
        if (data) {
          console.log(data);
          this.rampTrafficVolumesData = this.formRampVolumeDataList(data);

          // Go to the view
          setTimeout(() => {
            this.$vuetify.goTo('#volumes');
          }, 200);
        } else {
          this.rampTrafficVolumesData = [];
        }
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

    formOnRampData(mitigationData) {
      const title = `On Ramp Volume (${mitigationData.onRampDetectors})`;
      const xAxis = '';
      const yAxis = 'Traffic Volume';

      const data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: mitigationData.onRampActual });
      data.push({ name: 'Baseline', color: '#50B432', data: mitigationData.onRampBaseline });

      let bands = [];
      if (mitigationData.onRampBands) {
        bands = mitigationData.onRampBands.map(b => ({
          from: b[0],
          to: b[1],
          color: this.bandColor
        }));
      }

      return { data, xAxis, yAxis, title, bands };
    },

    formOffRampData(mitigationData) {
      const title = `Off Ramp Volume (${mitigationData.offRampDetectors})`;
      const xAxis = '';
      const yAxis = 'Traffic Volume';

      const data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: mitigationData.offRampActual });
      data.push({ name: 'Baseline', color: '#50B432', data: mitigationData.offRampBaseline });

      let bands = [];
      if (mitigationData.offRampBands) {
        bands = mitigationData.offRampBands.map(b => ({
          from: b[0],
          to: b[1],
          color: this.bandColor
        }));
      }

      return { data, xAxis, yAxis, title, bands };
    },

    formTravelTimeData(mitigationData) {
      const title = 'Arterial Travel Time';
      const xAxis = '';
      const yAxis = 'Travel Time (s)';

      const data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: mitigationData.travelTimeActual });
      data.push({ name: 'Baseline', color: '#50B432', data: mitigationData.travelTimeBaseline });

      let bands = [];
      if (mitigationData.travelTimeBands) {
        bands = mitigationData.travelTimeBands.map(b => ({
          from: b[0],
          to: b[1],
          color: this.bandColor
        }));
      }

      return { data, xAxis, yAxis, title, bands };
    },

    formDemandData(mitigationData) {
      const title = 'Demand  vs. Capacity';
      const xAxis = '';
      const yAxis = 'Value';

      const data = [];
      data.push({ name: 'Demand', color: '#ED561B', data: mitigationData.demand });
      data.push({ name: 'Capacity', color: '#50B432', data: mitigationData.capacity });

      let bands = [];
      if (mitigationData.signalChangeBands) {
        bands = mitigationData.signalChangeBands.map(b => ({
          from: b[0],
          to: b[1],
          color: this.bandColor
        }));
      }

      return { data, xAxis, yAxis, title, bands };
    },

    formRampVolumeDataList(volumes) {
      return volumes.map(item => this.formRampVolumeData(item));
    },

    formRampVolumeData(rampData) {
      const title = `${rampData.type} ${rampData.deviceId}-${rampData.direction}`;
      const xAxis = 'Time of day';
      const yAxis = 'Volume (vehicles/5min)';

      const data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: rampData.data.volume });
      data.push({ name: 'Baseline', color: '#50B432', data: rampData.data.baselineVolume });

      const timeSlots = [rampData.startTime, rampData.endTime];

      return { data, xAxis, yAxis, title, timeSlots };
    },

    createMarkers(incident) {
      const markers = [];

      // Incident location
      if (incident.location) {
        markers.push({
          id: 'L1',
          name: incident.address,
          position: incident.location,
          icon: 'alertIcon',
          zIndex: 999
        });
      }

      if (this.signalDevices) {
        this.signalDevices.forEach(item => {
          markers.push({
            id: 'S' + item.permit,
            name: item.permit,
            title: item.title,
            position: item.location,
            icon: 'trafficLightIcon',
            zIndex: 9999
          });
        });
      }

      return markers;
    },

    ...mapActions('traffic', ['fetchAnomalyDevices', 'fetchBluetoothSegments'])
  }
};
</script>

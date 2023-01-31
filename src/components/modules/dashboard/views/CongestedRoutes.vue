<template>
  <div class="congested-routes">
    <v-data-table
      :height="tableHeight"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemPerPage"
      disable-sort
      :hide-default-header="!showTable"
      :item-class="itemRowBackground"
      hide-default-footer
      @click:row="handleRowClick"
      class="elevation-1 mx-2"
    >
      <template v-slot:[`item.level`]="{ item }">
        <v-chip small :color="getStrokeColor(item.level)">
          <strong class="black--text">{{ item.level }}</strong>
        </v-chip>
      </template>
      <template v-slot:[`item.lastUpdated`]="{ item }">
        {{ item.lastUpdated.split(' ')[1].slice(0, 5) }}
      </template>
    </v-data-table>

    <v-row v-if="selectedCongestedSegment">
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Basic Info</h3></v-subheader>

          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showTravelTimeChart" class="mr-4 mt-2" :loading="loading">
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <div class="mx-4">
          <RouteInfo :route="selectedCongestedSegment" />
        </div>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Travel Time</h3></v-subheader>

          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showTravelTimeChart" class="mr-4 mt-2" :loading="loading">
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <div class="mx-4">
          <v-card tile class="basic-chart" elevation="4">
            <BasicChart :data="travelTime" :height="height" :left="marginLeft" :legendy="legendY" />
          </v-card>
        </div>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <div class="d-flex justify-space-between">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Traffic Speed</h3></v-subheader>
          <v-tooltip left>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="showTrafficSpeedChart" class="mr-4 mt-2" :loading="loading">
                <v-icon small>mdi-arrow-expand</v-icon>
              </v-btn>
            </template>
            <span>Expand</span>
          </v-tooltip>
        </div>
        <v-divider />
      </v-col>
      <v-col cols="12">
        <div class="mx-4">
          <v-card tile class="basic-chart" elevation="4">
            <BasicChart :data="speed" :height="height" :left="marginLeft" :legendy="legendY" />
          </v-card>
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
          <v-btn outlined v-for="camera in cameraIds" :key="camera.id" @click.stop="playVideo(camera.id)" class="mr-5">
            <v-icon left color="warning">
              mdi-camera
            </v-icon>
            {{ camera.id }}
          </v-btn>
        </div>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
import RouteInfo from '@/components/modules/dashboard/views/RouteInfo';
import Utils from '@/utils/Utils.js';
import Api from '@/utils/api/traffic';
import BasicChart from '@/components/modules/traffic/common/BasicChart';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: {
    RouteInfo,
    BasicChart
  },
  data: () => ({
    loading: false,
    itemsPerPage: 0,
    reload: false,
    items: [],
    headers: [],
    height: 300,
    legendY: 5,
    marginLeft: 80,
    speed: {},
    travelTime: {}
  }),
  computed: {
    infoColumnCount() {
      return this.getSetting('dashboard', 'infoColumnCount');
    },
    tableHeight() {
      if (this.showTable && this.maxItems > 12) {
        return 'calc(80vh - 48px)';
      } else {
        return null;
      }
    },
    currSegment() {
      return this.formatData(this.selectedCongestedSegment);
    },

    cameraIds() {
      return this.selectedCongestedSegment ? this.selectedCongestedSegment.cameras : [];
    },

    camerasAvaliable() {
      return !Utils.isEmpty(this.cameraIds);
    },

    listLimit() {
      return this.getSetting ? this.getSetting('dashboard', 'limitListings') : 0;
    },
    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },

    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['congestedSegments', 'selectedCongestedSegment', 'showTable'])
  },

  watch: {
    congestedSegments() {
      if (this.congestedSegments) {
        this.prepareHighCongestionRoutes(this.congestedSegments);
        this.handleRowClick(this.items[0]);
      }
    },

    selectedCongestedSegment(segment) {
      if (segment) {
        let time = new Date().getTime();
        this.fetchTravelTimeData(segment.id, 300000, time);
      }
    }
  },

  mounted() {
    if (this.congestedSegments) {
      this.prepareHighCongestionRoutes(this.congestedSegments);
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)}`;
    },

    getStrokeColor(level) {
      return Utils.getStrokeColor(level);
    },

    prepareHighCongestionRoutes(data) {
      this.headers = [
        { text: 'Time', value: 'lastUpdated' },
        { text: 'Segment', value: 'desc' },
        { text: 'Travel Time', value: 'travelTime' },
        { text: 'Level', value: 'level' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        lastUpdated: d.travelTime.calculationTimestamp.text,
        travelTime: Utils.durationToTimeStr(d.travelTime.data.meanTravelTimeSecs),
        desc: d.desc,
        level: d.travelTime.level
      }));
    },

    itemRowBackground(item) {
      return item.id == this.selectedCongestedSegment.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      const data = this.congestedSegments.find(x => x.id == item.id);
      if (data) {
        this.$store.commit('dashboard/SET_SELECTED_CONGESTED_SEGMENT', data);
      }
    },

    playVideo(id) {
      this.$bus.$emit('PLAY_POPUP_VIDEO', id);
    },

    showTrafficSpeedChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Traffic Speed', data: this.speed });
    },

    showTravelTimeChart() {
      this.$bus.$emit('SHOW_CHART_DATA', { title: 'Travel Time', data: this.travelTime });
    },

    async fetchTravelTimeData(linkId, interval, time) {
      this.loading = true;
      try {
        const travelTimeRes = await Api.fetchTravelTimeData(linkId, interval, time);
        let travelTimeList = this.parseResponseData(travelTimeRes);
        if (travelTimeList) {
          this.travelTime = this.formTravelTimeData(travelTimeList);
          this.speed = this.formSpeedData(travelTimeList);
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    parseResponseData(response) {
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

    formSpeedData(travelTimeList) {
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Speed (mph)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.speed });
      data.push({ name: 'Baseline', color: '#058DC7', data: travelTimeList.baselineSpeed });

      return { data, xAxis, yAxis, title };
    },

    formTravelTimeData(travelTimeList) {
      let title = '';
      let xAxis = 'Time of day';
      let yAxis = 'Travel Time (s)';
      let data = [];
      data.push({ name: 'Actual', color: '#ED561B', data: travelTimeList.travelTime });
      data.push({ name: 'Baseline', color: '#058DC7', data: travelTimeList.baselineTravelTime });

      return { data, xAxis, yAxis, title };
    },

    formatData(item) {
      if (!item) {
        return [];
      }
      const signalIssueItems = [
        { icon: 'mdi-note-outline', name: 'Name' },
        { icon: 'mdi-vector-line', name: 'Route' },
        { icon: 'mdi-clock-outline', name: 'Time' },
        { icon: 'mdi-chart-bar-stacked', name: 'Severity' },
        { icon: 'mdi-speedometer', name: 'Speed' },
        { icon: 'mdi-timer-outline', name: 'Travel Time' },
        { icon: 'mdi-timer-outline', name: 'Mean Travel Time' },
        { icon: 'mdi-vector-line', name: 'Distance' }
      ];

      for (let i = 0; i < signalIssueItems.length; i++) {
        signalIssueItems[i]['colDisplay'] = this.infoColumnCount == 1;
        signalIssueItems[i]['flex'] = this.infoColumnCount == 1;
        signalIssueItems[i]['height'] = this.infoColumnCount == 1 ? '11vh' : undefined;
        signalIssueItems[i]['titleFontSize'] = this.infoColumnCount == 1 ? undefined : 20;
        signalIssueItems[i]['valueFontSize'] = this.infoColumnCount == 1 ? 38 : 28;
      }

      signalIssueItems[0]['value'] = item.short;
      signalIssueItems[1]['value'] = item.desc;
      signalIssueItems[2]['value'] = this.getTimeStr(item.travelTime.calculationTimestamp.value);
      signalIssueItems[3]['value'] = `${item.travelTime.level} / 7`;
      signalIssueItems[4]['value'] = `${item.travelTime.data.speedMph.toFixed(2)} mph`;
      signalIssueItems[5]['value'] = Utils.durationToTimeStr(item.travelTime.data.meanTravelTimeSecs);
      signalIssueItems[5]['valueColor'] = 'red';

      signalIssueItems[6]['value'] = Utils.durationToTimeStr(item.travelTime.data.freeFlowRunTimeSecs);
      signalIssueItems[7]['value'] = `${item.distance} mi`;

      return signalIssueItems;
    }
  }
};
</script>

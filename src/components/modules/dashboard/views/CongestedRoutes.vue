<template>
  <div class="congested-routes">
    <v-data-table
      :height="height"
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

    <RouteInfo :route="selectedCongestedSegment" class="mt-3 mx-2" v-if="selectedCongestedSegment" />

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

    <!-- <v-row class="mt-3 mx-1" v-if="currSegment">
      <v-col :cols="12 / infoColumnCount" v-for="x in currSegment" :key="x.name" class="pa-1">
        <InfoCard
          :icon="x.icon"
          :colDisplay="x.colDisplay"
          :flex="x.flex"
          :height="x.height"
          :name="x.name"
          :value="x.value"
          :titleFontSize="x.titleFontSize"
          :valueFontSize="x.valueFontSize"
          :valueColor="x.valueColor"
        />
      </v-col>
    </v-row> -->
  </div>
</template>

<script>
import RouteInfo from '@/components/modules/dashboard/views/RouteInfo';
import Utils from '@/utils/Utils.js';
import { mapGetters, mapState } from 'vuex';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: {
    RouteInfo
  },
  data: () => ({
    itemsPerPage: 0,
    reload: false,
    items: [],
    headers: []
  }),
  computed: {
    infoColumnCount() {
      return this.getSetting('dashboard', 'infoColumnCount');
    },
    height() {
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

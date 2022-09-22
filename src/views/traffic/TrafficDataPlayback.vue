<template>
  <div class="traffic-data-playback">
    <!-- Right display panel -->
    <RightPanel name="dashboardSideBarWidth" :title="currentTitle">
      <PlaybackDataDisplay
        :currentTitle="currentTitle"
        :selectedSegment="selectedSegment"
        :selectedWaze="selectedWaze"
        :selectedDevice="selectedDevice"
        :selectedIncident="selectedIncident"
      />
    </RightPanel>

    <PlaybackMap
      :segments="currSegments"
      :waze="currWaze"
      :btDevices="currDevices"
      :trafficIncidents="currIncidents"
      :mapLayers="mapLayers"
    />

    <PlaybackBar :date="currentDate" :loading="!dataIsAvailable" @processTime="processTime" :duration="duration" />

    <div>
      <PlaybackLayers />
    </div>

    <div class="debug">
      <v-card
        outlined
        color="transparent"
        class="pa-4"
        dense
        floating
        :style="`position: absolute; top: 50px; left: 20px;`"
      >
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-badge
              class="mr-5"
              :content="waze ? numFormatter(waze.length) : 0"
              :value="waze ? numFormatter(waze.length) : 0"
              overlap
            >
              <v-icon v-on="on" :color="'green'" :loading="waze" :disabled="!waze" large>
                mdi-waze
              </v-icon>
            </v-badge>
          </template>
          <span>Total waze alerts: {{ waze ? waze.length : 0 }}</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-badge
              class="mr-5"
              :content="segmentsLength ? numFormatter(segmentsLength) : 0"
              :value="segmentsLength ? numFormatter(segmentsLength) : 0"
              overlap
            >
              <v-icon v-on="on" :color="'green'" :loading="segments" :disabled="!segments" large>
                mdi-vector-line
              </v-icon>
            </v-badge>
          </template>
          <span>
            Total segments: {{ segments ? segments.length : 0 }}<br />
            Total segment instances: {{ !!segmentsLength ? segmentsLength : 0 }}
          </span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-badge
              class="mr-5"
              :content="devicesLength ? numFormatter(devicesLength) : 0"
              :value="devicesLength ? numFormatter(devicesLength) : 0"
              overlap
            >
              <v-icon v-on="on" :color="'green'" :loading="btDevices" :disabled="!btDevices" large>
                mdi-leak
              </v-icon>
            </v-badge>
          </template>
          <span>
            Total devices: {{ btDevices ? btDevices.length : 0 }}<br />
            Total device instances: {{ !!devicesLength ? devicesLength : 0 }}
          </span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-2" @click="fetchApiData" v-on="on" fab x-small :loading="loading">
              <v-icon>
                mdi-refresh
              </v-icon>
            </v-btn>
          </template>
          <span>
            Refresh
          </span>
        </v-tooltip>
      </v-card>
    </div>

    <DialogTT :selectedLinkId="selectedLinkId" :selectedSegment="selectedSegment" />
  </div>
</template>

<script>
import RightPanel from '@/components/modules/traffic/common/RightPanel';
import PlaybackMap from '@/components/modules/traffic/playback/PlaybackMap.vue';
import PlaybackBar from '@/components/modules/traffic/playback/PlaybackBar.vue';
import PlaybackLayers from '@/components/modules/traffic/playback/PlaybackLayers.vue';
import DialogTT from '@/components/modules/traffic/playback/DialogTT.vue';
import Utils from '@/utils/Utils';
import { mapActions, mapState } from 'vuex';
import PlaybackDataDisplay from '@/components/modules/traffic/playback/PlaybackDataDisplay.vue';

export default {
  components: { RightPanel, PlaybackMap, PlaybackBar, PlaybackLayers, DialogTT, PlaybackDataDisplay },
  data() {
    return {
      selectedSegmentId: null,
      selectedWazeId: null,
      selectedDeviceId: null,
      selectedIncidentId: null,
      currentTitle: '',
      duration: 1439,
      startTime: null,
      endTime: null,
      mapLayers: [0, 1, 3],
      layerItems: [
        { title: 'Routes', id: 0 },
        { title: 'Waze Alerts', id: 1 },
        { title: 'Grouped Waze Alerts', id: 2 },
        { title: 'Devices', id: 3 }
      ]
    };
  },
  computed: {
    selectedLinkId() {
      if (this.selectedSegment) {
        return this.selectedSegment.linkId;
      } else {
        return null;
      }
    },
    selectedSegment() {
      if (this.selectedSegmentId) {
        let val = null;
        val = this.currSegments.filter(x => x.id == this.selectedSegmentId);
        return val[0] ? val[0] : null;
      } else {
        return null;
      }
    },
    selectedWaze() {
      if (this.selectedWazeId) {
        let val = null;
        val = this.waze.filter(x => x.id == this.selectedWazeId);
        return val[0] ? val[0] : null;
      } else {
        return null;
      }
    },
    selectedDevice() {
      if (this.selectedDeviceId) {
        let val = null;
        val = this.currDevices.filter(x => x.id == this.selectedDeviceId);
        return val[0] ? val[0] : null;
      } else {
        return null;
      }
    },
    selectedIncident() {
      if (this.selectedIncidentId) {
        let val = null;
        val = this.trafficIncidents.filter(x => x.id == this.selectedIncidentId);
        return val[0] ? val[0] : null;
      } else {
        return null;
      }
    },
    segmentsLength() {
      if (this.segments && this.segments.length > 0) {
        return this.segments.reduce((total, segment) => {
          if (segment.timeline) {
            return total + segment.timeline.length;
          } else {
            return total;
          }
        }, 0);
      } else {
        return 0;
      }
    },
    devicesLength() {
      if (this.btDevices && this.btDevices.length > 0) {
        return this.btDevices.reduce((total, device) => {
          if (device.flowData) {
            return total + device.flowData.length;
          } else {
            return total;
          }
        }, 0);
      } else {
        return 0;
      }
    },
    dataIsAvailable() {
      return !!this.segments || !!this.waze;
    },
    currSegments() {
      if (this.segments && this.endTime) {
        return this.segments.map(x => {
          let instanceData = {
            level: -1,
            calculationTimestamp: null,
            freeFlowRunTimeSecs: null,
            meanTravelTimeSecs: null
          };
          if (x.timeline) {
            let startTime = this.endTime - 30 * 60 * 1000;
            let instance = x.timeline.filter(
              x => x.calculationTimestamp.value >= startTime && x.calculationTimestamp.value < this.endTime
            );
            if (instance.length > 0) {
              let ins = instance[instance.length - 1];
              instanceData = {
                level: ins.level,
                calculationTimestamp: ins.calculationTimestamp,
                freeFlowRunTimeSecs: ins.freeFlowRunTimeSecs,
                meanTravelTimeSecs: ins.meanTravelTimeSecs
              };
            }
          }
          let info = x.info;
          return {
            id: info.id,
            desc: info.description,
            path: info.coordinates,
            linkId: info.linkId,
            short: info.name,
            distance: info.distance,
            ...instanceData
          };
        });
      } else {
        return null;
      }
    },
    currDevices() {
      if (this.btDevices && this.endTime) {
        return this.btDevices.map(x => {
          let instanceData = {
            level: -1,
            direction: null,
            createdTime: null,
            occupancy: null
          };
          if (x.flowData) {
            let startTime = this.endTime - 30 * 60 * 1000;
            let instance = x.flowData.filter(x => {
              return x.createdTime >= startTime && x.createdTime < this.endTime;
            });
            if (instance.length > 0) {
              let ins = instance[instance.length - 1];
              instanceData = {
                level: ins.status.level,
                direction: ins.direction,
                createdTime: ins.createdTime,
                occupancy: ins.occupancy,
                volume: ins.volume
              };
            }
          }
          return { id: x.deviceId, desc: x.title, position: x.position, ...instanceData };
        });
      } else {
        return null;
      }
    },
    currWaze() {
      if (this.waze && this.startTime && this.endTime) {
        return this.waze.filter(x => x.alertTimeTS >= this.startTime && x.alertTimeTS < this.endTime);
      } else {
        return null;
      }
    },
    currIncidents() {
      if (this.trafficIncidents && this.startTime && this.endTime) {
        return this.trafficIncidents.filter(x => x.startTime <= this.endTime && this.endTime <= x.endTime);
      } else {
        return null;
      }
    },
    ...mapState(['currentDate']),
    ...mapState('traffic', ['segments', 'waze', 'btDevices', 'loading']),
    ...mapState('dashboard', ['trafficIncidents'])
  },
  mounted() {
    let date = Utils.getStartOfDay(this.currentDate);
    this.$store.commit('SET_CURRENT_DATE', date);

    this.$bus.$on('DISPLAY_DETAILS', payload => {
      this.cardClicked(payload);
    });
    setTimeout(() => {
      this.$store.commit('traffic/SHOW_PANEL', false);
    }, 50);
  },
  methods: {
    numFormatter(num) {
      if (num < 1000) {
        return num;
      } else if (num < 10000) {
        return (num / 1000).toFixed(2) + 'K';
      } else if (num < 100000) {
        return (num / 1000).toFixed(1) + 'K';
      } else if (num < 100000) {
        return (num / 1000).toFixed(0) + 'K';
      } else if (num < 1000000) {
        return (num / 1000).toFixed(2) + 'M';
      } else {
        return '>1M';
      }
    },
    cardClicked(payload) {
      this.trafficInfoShow = false;
      this.selectedIdx = payload.idx;
      this.showPanelIfNot();
      switch (payload.type) {
        case 0:
          this.currentTitle = 'Segment';
          this.selectedSegmentId = payload.id;
          this.$store.commit('bluetooth/SET_TT_DIALOG', true);
          break;
        case 1:
          this.currentTitle = 'Waze Alert';
          this.selectedWazeId = payload.id;
          break;
        case 3:
          this.currentTitle = 'Device';
          this.selectedDeviceId = payload.id;
          break;
        case 6:
          this.currentTitle = 'Traffic Incident';
          this.selectedIncidentId = payload.id;
          break;
        default:
          this.currentTitle = '';
          break;
      }
    },
    showPanelIfNot() {
      if (!this.$store.state.traffic.showPanel) {
        this.$store.commit('traffic/SHOW_PANEL', true);
      }
    },
    changeSegmentLevels(date) {
      const segmentsFull = this.apiData.segmentsFull;
      if (segmentsFull && segmentsFull.length > 0) {
        segmentsFull.forEach(s => {
          let segment = {
            info: s.info,
            travelTime: null
          };
          if (s.timeline) {
            s.timeline
              .slice()
              .reverse()
              .forEach(t => {
                let timelineTS = t.calculationTimestamp.value;
                if (timelineTS < date.ts && timelineTS > date.ts - 10 * 60 * 1000) {
                  segment.travelTime = t;
                  return;
                }
              });
            // this.segmentPolylines.forEach(p => {
            //   if (p.title == s.info.name) {
            //     let level = segment.travelTime ? segment.travelTime.level : -3;
            //     let clickWindow = new google.maps.InfoWindow(infoWindows.getSegCW(segment));
            //     p.setOptions({ zIndex: level, strokeColor: Utils.getStrokeColor(level), clickWindow: clickWindow });
            //     return;
            //   }
            // });
          }
        });
      }
    },
    layerItemClicked() {
      this.mapLayers.sort();
    },
    processTime(trackingTime) {
      this.startTime = trackingTime.getTime() - 60 * 60 * 1000;
      this.endTime = trackingTime.getTime();
      //   if (this.segments) {
      //     this.currSegments = this.segments.filter;
      //   }
    },
    fetchApiData() {
      this.$store.state.traffic.segments = null;
      this.$store.state.traffic.waze = null;
      this.$store.state.traffic.btDevices = null;
      if (Utils.isToday(this.currentDate)) {
        let d = new Date();
        this.duration = d.getHours() * 60 + d.getMinutes();
      } else {
        this.duration = 1439;
      }
      this.fetchSegments(this.currentDate);
      this.fetchWaze(this.currentDate);
      this.fetchBTDevices(this.currentDate);
      this.fetchBTSensors();
      this.fetchTrafficIncidents(this.currentDate);
    },
    ...mapActions('traffic', ['fetchSegments', 'fetchWaze', 'fetchBTDevices', 'fetchBTSensors']),
    ...mapActions('dashboard', ['fetchTrafficIncidents'])
  },
  watch: {
    currentDate() {
      this.fetchApiData();
    }
  }
};
</script>

<style></style>

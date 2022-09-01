<template>
  <div class="traffic-data-playback">
    <PlaybackMap
      :segments="selectedSegments"
      :waze="selectedWaze"
      :btDevices="selectedDevices"
      :mapLayers="mapLayers"
    />

    <PlaybackBar :date="currentDate" :loading="!dataIsAvailable" @processTime="processTime" :duration="duration" />

    <div>
      <PlaybackLayers />
      <!-- <v-menu light bottom right offset-y :close-on-content-click="false">
        <template v-slot:activator="{ on: menu, attrs }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on: tooltip }">
              <v-btn
                v-bind="attrs"
                v-on="{ ...tooltip, ...menu }"
                :style="`position: absolute; top: 10px; left: 490px; height: 40px`"
              >
                <v-icon>mdi-layers-outline</v-icon>
              </v-btn>
            </template>
            <span>Map Layer</span>
          </v-tooltip>
        </template>

        <v-list dense>
          <v-list-item-group>
            <v-list-item v-for="item in layerItems" :key="item.id" @click="layerItemClicked(item.id)">
              <v-list-item-content class="mt-1 pa-2">
                <v-checkbox hide-details :label="item.title" :value="item.id" class="mt-0" v-model="mapLayers" />
              </v-list-item-content>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-menu> -->
    </div>

    <div class="debug">
      <v-card class="pa-4" dense floating :style="`position: absolute; top: 80px; left: 20px;`">
        Segments: {{ segments ? segments.length : 'Loading...' }}<br />
        Segments TL: {{ !!segmentsLength ? segmentsLength : 'Loading...' }}<br />
        Waze: {{ waze ? waze.length : 'Loading...' }}<br />
        BT Devices: {{ btDevices ? btDevices.length : 'Loading...' }}<br />
        BT Devices TL: {{ !!devicesLength ? devicesLength : 'Loading...' }}
      </v-card>
    </div>
  </div>
</template>

<script>
import PlaybackMap from '@/components/modules/traffic/playback/PlaybackMap.vue';
import PlaybackBar from '@/components/modules/traffic/playback/PlaybackBar.vue';
import PlaybackLayers from '@/components/modules/traffic/playback/PlaybackLayers.vue';
import Utils from '@/utils/Utils';
import { mapActions, mapState } from 'vuex';

export default {
  components: { PlaybackMap, PlaybackBar, PlaybackLayers },
  data() {
    return {
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
    selectedSegments() {
      if (this.segments && this.endTime) {
        return this.segments.map(x => {
          let level = -1;
          if (x.timeline) {
            let startTime = this.endTime - 30 * 60 * 1000;
            let instance = x.timeline.filter(
              x => x.calculationTimestamp.value >= startTime && x.calculationTimestamp.value < this.endTime
            );
            if (instance.length > 0) {
              level = instance[instance.length - 1].level;
            }
          }
          return { id: x.info.id, desc: x.info.description, path: x.info.coordinates, level: level };
        });
      } else {
        return null;
      }
    },
    selectedDevices() {
      if (this.btDevices && this.endTime) {
        return this.btDevices.map(x => {
          let level = -1;
          if (x.flowData) {
            let startTime = this.endTime - 30 * 60 * 1000;
            let instance = x.flowData.filter(x => {
              return x.createdTime >= startTime && x.createdTime < this.endTime;
            });
            if (instance.length > 0) {
              level = instance[instance.length - 1].status.level;
            }
          }
          return { id: x.deviceId, desc: x.title, position: x.position, level: level };
        });
      } else {
        return null;
      }
    },
    selectedWaze() {
      if (this.waze && this.startTime && this.endTime) {
        return this.waze.filter(x => x.alertTimeTS >= this.startTime && x.alertTimeTS < this.endTime);
      } else {
        return null;
      }
    },
    ...mapState(['currentDate']),
    ...mapState('traffic', ['segments', 'waze', 'btDevices'])
  },
  mounted() {
    let date = Utils.getStartOfDay(this.currentDate);
    this.$store.commit('SET_CURRENT_DATE', date);
  },
  methods: {
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
      //     this.selectedSegments = this.segments.filter;
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
    },
    ...mapActions('traffic', ['fetchSegments', 'fetchWaze', 'fetchBTDevices', 'fetchBTSensors'])
  },
  watch: {
    currentDate() {
      this.fetchApiData();
    }
  }
};
</script>

<style></style>

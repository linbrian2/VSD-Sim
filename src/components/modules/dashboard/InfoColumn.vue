<template>
  <v-container class="pa-0 px-2" style="max-height: calc(100vh - 48px); overflow-y: auto;">
    <div id="anomaly-incidents" v-if="selectedIdx == 0">
      <div>
        <IncidentTable :height="200" :incidents="incidents" @click="handleRowClick" />
        <Timeline />
        <v-card class="mt-2">
          <v-col>
            <v-row justify="center">
              <v-img alt="Logo" class="shrink" src="@/assets/collision-example.png" width="458" />
            </v-row>
          </v-col>
        </v-card>
      </div>
    </div>
    <div id="traffic-detector-issues" v-else-if="selectedIdx == 1">
      <TrafficDetector />
      <v-card class="mt-2">
        <v-col>
          <v-row justify="center">
            <v-img alt="Logo" class="shrink" src="@/assets/collision-example.png" width="458" />
          </v-row>
        </v-col>
      </v-card>
    </div>
    <div id="signal-performance-issues" v-else-if="selectedIdx == 2">
      <SignalPerformance />
    </div>
    <div id="detectors-high-congestion" v-else-if="selectedIdx == 3">
      <Detectors />
    </div>
    <div id="routes-high-level" v-else-if="selectedIdx == 4">
      <TravelTimeTableCard :apiInfo="apiInfo" />
      <div class="pt-2">
        <TravelTimeGraphCard :apiInfo="apiInfo" :selection="['i95n', 0]" />
      </div>
      <v-card class="mt-2">
        <v-col>
          <v-row justify="center">
            <v-img alt="Logo" class="shrink" src="@/assets/collision-example.png" width="458" />
          </v-row>
        </v-col>
      </v-card>
    </div>
    <div id="reported-waze-incidents" v-else-if="selectedIdx == 5">
      <Waze />
    </div>
  </v-container>
</template>

<script>
import TravelTimeTableCard from '@/components/modules/dashboard/TravelTimeTableCard.vue';
import TravelTimeGraphCard from '@/components/modules/dashboard/TravelTimeGraphCard.vue';
import IncidentTable from '@/components/modules/dashboard/IncidentTable.vue';
import Timeline from '@/components/modules/dashboard/Timeline.vue';
import TrafficDetector from '@/components/modules/dashboard/TrafficDetector.vue';
import SignalPerformance from '@/components/modules/dashboard/SignalPerformance.vue';
import Detectors from '@/components/modules/dashboard/Detectors.vue';
import Waze from '@/components/modules/dashboard/Waze.vue';

import Incidents from '@/utils/samples/Incidents.js';

export default {
  props: {
    apiInfo: Object,
    selectedIdx: Number,
    cardData: Array
  },
  components: {
    TravelTimeTableCard,
    TravelTimeGraphCard,
    IncidentTable,
    Timeline,
    TrafficDetector,
    SignalPerformance,
    Detectors,
    Waze
  },
  data() {
    return {
      incidents: Incidents
    };
  },
  methods: {
    handleRowClick(/* item */) {
      // console.log(item);
      /* this.incidentItem = Object.assign({}, item);
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
        this.markers = this.createMarkers(this.incidentItem);
        this.segmentLinks = segments;
        setTimeout(() => {
          this.updateMap(segments);
        }, 50);
      }

      setTimeout(() => {
        this.updateIncidentTimeline(item);
      }, 200); */
    }
  }
};
</script>

<style></style>

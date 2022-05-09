<template>
  <div>
    <MapBase @map-ready="mapMounted" @center-map="centerMapHandler" @zoom-select="zoomSelectHandler">
      <GmapPolyline
        v-for="s in segments"
        :key="s.id"
        :title="s.desc"
        :path.sync="s.path"
        :options="segmentOptions(s)"
        @click="segmentClicked(s)"
      />

      <!-- <GmapMarker v-for="s in segments" :key="`M-${s.id}`" :position="s.path[0]" :title="s.desc" :icon="redIcon" /> -->

      <GmapCustomMarker
        alignment="center"
        v-for="s in segments"
        :key="`L-${s.id}`"
        :offsetX="0"
        :offsetY="-50"
        :marker="midPoint(s)"
      >
        <div v-if="s.id == selectedSegmentId">
          <v-chip small :color="getChipColor(s)" @click="segmentClicked(s)">{{ s.short }}</v-chip>
        </div>
      </GmapCustomMarker>
    </MapBase>
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import MapBase from '@/components/modules/traffic/map/MapBase';
export default {
  props: ['segments', 'defaultColors'],
  components: {
    GmapCustomMarker,
    MapBase
  },

  data: () => ({
    map: null,
    selectedSegmentId: '',
    defaultSegmentOptions: {
      strokeColor: 'green',
      strokeOpacity: 1.0,
      strokeWeight: 8,
      zIndex: 100
    },

    redIcon: {
      path: 0,
      scale: 5.0,
      fillColor: 'white',
      fillOpacity: 0.6,
      strokeWeight: 1.0,
      strokeColor: 'white',
      strokeOpacity: 1.0
    },

    redDot: {
      path: 0,
      scale: 9.0,
      fillColor: '#057CFF',
      fillOpacity: 1.0,
      strokeWeight: 2.0,
      strokeColor: 'white',
      strokeOpacity: 1.0
    }
  }),

  computed: {},

  mounted() {
    this.$bus.$on('SELECT_FIRST', () => {
      this.triggerFirstSegmentClick();
    });

    this.$bus.$on('SELECT_SEGMENT_BY_NAME', name => {
      this.selectSegmentByName(name);
    });

    this.$bus.$on('CENTER_SEGMENT', () => {
      setTimeout(() => {
        this.setCenterMap();
      }, 500);
    });
  },

  methods: {
    midPoint(segment) {
      return segment.path[Math.round(segment.path.length / 2)];
    },

    segmentOptions(segment) {
      if (this.defaultColors) {
        const color = segment.id === this.selectedSegmentId ? 'blue' : Utils.getStrokeColor(1);
        return { ...this.defaultSegmentOptions, strokeColor: color };
      } else {
        const color = segment.id === this.selectedSegmentId ? 'blue' : Utils.getStrokeColor(segment.travelTime.level);
        return { ...this.defaultSegmentOptions, strokeColor: color };
      }
    },

    mapMounted(map) {
      this.map = map;
      this.centerMapHandler(map);
    },

    centerMapHandler(map) {
      if (map && this.segments) {
        this.centerMap(map, this.segments);
      }
    },

    zoomSelectHandler(map) {
      if (this.selectedSegmentId) {
        this.segments.forEach(s => {
          if (s.id === this.selectedSegmentId) {
            if (s) {
              map.panTo(this.midPoint(s));
              map.setZoom(12);
            }
          }
        });
      }
    },

    triggerFirstSegmentClick() {
      if (this.segments && this.segments.length > 0) {
        const selectedSegment = this.segments[0];
        this.segmentClicked(selectedSegment);
      }
    },

    segmentClicked(s) {
      this.selectedSegmentId = s.id;
      this.$emit('clicked', s);
    },

    getChipColor(s) {
      return this.selectedSegmentId === s.id ? 'blue' : 'white';
    },

    setCenterMap() {
      if (this.map && this.segments) {
        this.centerMap(this.map, this.segments);
      }
    },

    centerMap(map, segments) {
      if (segments.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        segments.forEach(segment => {
          segment.path.forEach(point => {
            bounds.extend(point);
          });
        });
        map.fitBounds(bounds, 0);
      }
    },

    selectSegmentByName(name) {
      const segment = this.segments.find(m => m.name === name);
      if (segment) {
        this.segmentClicked(segment);
      }
    }
  }
};
</script>

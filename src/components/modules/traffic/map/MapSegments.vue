<template>
  <div>
    <MapBase
      @map-ready="mapMounted"
      @center-map="centerMapHandler"
      @zoom-select="zoomSelectHandler"
      :smallMap="smallMap"
    >
      <GmapPolyline
        v-for="s in segments"
        :key="s.id"
        :title="s.desc"
        :path.sync="s.path"
        :options="segmentOptions(s.id)"
        @click="segmentClicked(s)"
      />

      <GmapMarker v-for="s in segments" :key="`M-${s.id}`" :position="s.path[0]" :title="s.desc" :icon="redIcon" />

      <GmapMarker
        v-for="d in detectors"
        :key="d.id"
        :position="d.position"
        :title="d.name"
        :icon="redDot"
        @click="markerClicked(d)"
      />

      <GmapCustomMarker
        alignment="center"
        v-for="s in segments"
        :key="`L-${s.id}`"
        :offsetX="80"
        :offsetY="0"
        :marker="midPoint(s)"
      >
        <div v-if="s.id == selectedSegmentId">
          <v-chip small :color="getChipColor(s)" @click="segmentClicked(s)">{{ s.name }}</v-chip>
        </div>
      </GmapCustomMarker>
    </MapBase>
  </div>
</template>

<script>
/* global google */
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import MapBase from '@/components/modules/traffic/map/MapBase';
export default {
  props: ['segments', 'smallMap'],
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

  computed: {
    detectors() {
      const devices = [];
      if (this.segments) {
        this.segments
          .filter(s => s.devices)
          .forEach(s => {
            devices.push(...s.devices);
          });
      }
      return devices;
    }
  },

  methods: {
    midPoint(segment) {
      return segment.path[Math.round(segment.path.length / 2)];
    },

    segmentOptions(id) {
      const color = id === this.selectedSegmentId ? 'orange' : 'green';
      return { ...this.defaultSegmentOptions, strokeColor: color };
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

    segmentClicked(s) {
      this.selectedSegmentId = s.id;
    },

    markerClicked(m) {
      this.$emit('detector-clicked', m);
    },

    getChipColor(s) {
      return this.selectedSegmentId === s.id ? 'orange' : 'white';
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

    selectSegmentByShortName(name) {
      const segment = this.segments.find(m => m.name === name);
      if (segment) {
        this.selectedSegmentId = segment.id;
        this.zoomSelectHandler(this.map);
      }
    }
  }
};
</script>

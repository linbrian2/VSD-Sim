<template>
  <div>
    <GmapMap ref="mapRef" :options="options" :center="position" :zoom="zoom" map-type-id="roadmap" class="map-segment">
      <div v-if="segment">
        <!-- Bluetooth Segments -->
        <GmapPolyline :path.sync="segment.path" :options="segmentOptions" />

        <GmapCustomMarker alignment="topright" :offsetX="10" :offsetY="startPointOffsetY" :marker="startPoint">
          <h2 style="color:grey">{{ segment.info.startPoint }}</h2>
        </GmapCustomMarker>

        <GmapCustomMarker alignment="topright" :offsetX="30" :offsetY="0" :marker="midPoint">
          <h2 style="color:blue">{{ segment.info.distance }} miles</h2>
        </GmapCustomMarker>

        <GmapCustomMarker alignment="topright" :offsetX="10" :offsetY="endPointOffsetY" :marker="endPoint">
          <h2 style="color:grey">{{ segment.info.endPoint }}</h2>
        </GmapCustomMarker>

        <GmapMarker
          v-for="m in markers"
          :key="m.id"
          :position="m.position"
          :title="m.name"
          :icon="getIconByName(m.icon)"
          @click="markerClicked(m)"
        />

        <GmapMarker :position="startPoint" :title="segment.startPoint" :icon="startIcon" />
        <GmapMarker :position="endPoint" :title="segment.endPoint" :icon="endIcon" />
      </div>
    </GmapMap>
  </div>
</template>

<script>
/* global google */
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import { weatherCode } from '@/mixins/weatherCode';
import { mapIcons } from '@/mixins/mapIcons';
import MapUtils from '@/utils/MapUtils.js';
export default {
  mixins: [weatherCode, mapIcons],
  props: {
    segment: Object,
    markers: Array
  },

  components: {
    GmapCustomMarker
  },

  data: () => ({
    map: null,
    zoom: 16,

    options: {
      mapTypeControl: false,
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite'],
        position: google.maps.ControlPosition.TOP_CENTER
      },

      streetViewControl: false,

      fullscreenControl: true,
      fullscreenControlOptions: {
        position: google.maps.ControlPosition.LEFT_TOP
      },

      zoomControl: true,
      zoomControlOptions: {
        position: google.maps.ControlPosition.RIGHT_CENTER
      }
    },

    segmentOptions: {
      strokeColor: '#1B5E20',
      strokeOpacity: 0.6,
      strokeWeight: 8
    }
  }),
  computed: {
    position() {
      return this.$store.state.traffic.position;
    },
    center() {
      return this.segment && this.segment.path && this.segment.path[Math.round(this.segment.path.length / 2)];
    },

    startPoint() {
      return this.segment && this.segment.path && this.segment.path[0];
    },

    midPoint() {
      return this.segment && this.segment.path && this.segment.path[Math.round(this.segment.path.length / 2)];
    },

    endPoint() {
      return this.segment && this.segment.path && this.segment.path[this.segment.path.length - 1];
    },

    startPointOffsetY() {
      if (!this.startPoint) {
        return 20;
      }
      return this.startPoint && this.startPoint.lat > this.endPoint.lat ? -10 : 20;
    },

    endPointOffsetY() {
      if (!this.startPoint) {
        return 20;
      }
      return this.startPoint && this.startPoint.lat > this.endPoint.lat ? 20 : -10;
    }
  },

  watch: {
    segment() {
      this.$refs.mapRef &&
        this.$refs.mapRef.$mapPromise.then(map => {
          this.centerMap(map, this.segment.path);
        });
    }
  },

  mounted() {
    this.$refs.mapRef &&
      this.$refs.mapRef.$mapPromise.then(map => {
        this.map = map;
        this.map.setZoom(this.zoom);
        this.addHomeControl(map);
        this.centerMapAndZoom();

        setTimeout(() => {
          this.centerMapAndZoom();
          google.maps.event.trigger(this.map, 'resize');
        }, 1000);
      });
  },

  methods: {
    getMap() {
      return this.$refs.mapRef;
    },

    addHomeControl(map) {
      let options = {
        position: 'top_right',
        content: `<div style="margin:-5px 4px;"><img src="${this.homeIcon}"/></div>`,
        title: 'Click to center the map',
        style: {
          width: '40px',
          height: '40px',
          margin: '10px',
          padding: '12px 3px',
          border: 'solid 1px #717B87',
          background: '#fff'
        },
        events: {
          click: () => {
            this.centerMapAndZoom();
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    centerMapAndZoom() {
      if (this.map && this.segment) {
        this.centerMap(this.map, this.segment.path);
      }
    },

    centerMap(map, path) {
      if (path && path.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < path.length; i++) {
          bounds.extend(path[i]);
        }

        // Don't zoom in too far on only one marker
        if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
          const extendPoint1 = new google.maps.LatLng(
            bounds.getNorthEast().lat() + 0.01,
            bounds.getNorthEast().lng() + 0.01
          );
          const extendPoint2 = new google.maps.LatLng(
            bounds.getNorthEast().lat() - 0.01,
            bounds.getNorthEast().lng() - 0.01
          );
          bounds.extend(extendPoint1);
          bounds.extend(extendPoint2);
        }

        map.fitBounds(bounds);
      }
    },

    refreshMap() {
      this.$gmapDefaultResizeBus.$emit('resize');
    },

    markerClicked(marker) {
      this.$emit('click', marker);
    }
  }
};
</script>
<style lang="scss" scoped>
.map-segment {
  width: 100%;
  height: 500px;
}
</style>

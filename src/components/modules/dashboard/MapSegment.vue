<template>
  <div>
    <MapBase @map-ready="mapMounted" @center-map="centerMapHandler" @zoom-select="zoomSelectHandler">
      <div v-for="segment in filteredSegments" :key="segment.id">
        <GmapPolyline :path.sync="segment.path" :options="segmentOptions" :zIndex="1" />
        <GmapMarker :position="startPoint(segment)" :title="segment.startPoint" :icon="smallGreenIcon" :zIndex="0" />
        <GmapMarker :position="endPoint(segment)" :title="segment.endPoint" :icon="smallRedIcon" :zIndex="0" />
      </div>

      <GmapMarker
        v-for="m in filteredMarkers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :icon="getIcon(m)"
        :zIndex="m.zIndex"
      />
    </MapBase>
  </div>
</template>

<script>
/* global google */
import { weatherCode } from '@/mixins/weatherCode';
import { mapIcons } from '@/mixins/mapIcons';
import MapBase from '@/components/modules/dashboard/MapBase';
// import LayersSelectionTool from '@/components/modules/traffic/incident/LayersSelectionTool';

export default {
  mixins: [weatherCode, mapIcons],
  props: {
    incidentSegmentLinks: Array,
    incidentMarkers: Array
  },
  components: {
    MapBase
    // LayersSelectionTool
  },

  data: () => ({
    map: null,
    zoom: 15,

    segmentOptions: {
      strokeColor: 'orange',
      strokeOpacity: 0.9,
      strokeWeight: 8
    },

    selectedMarkerId: null,
    selectedSegmentId: null,

    layers: [],
    selectedLayers: []
  }),

  computed: {
    position() {
      return this.$store.state.position;
    },

    filteredSegments() {
      if (this.selectedSegmentId === null) {
        return this.incidentSegmentLinks;
      } else {
        return this.incidentSegmentLinks.filter(segment => segment.id === this.selectedSegmentId);
      }
    },

    filteredMarkers() {
      if (this.selectedSegmentId === null) {
        return this.incidentMarkers;
      } else {
        return this.incidentMarkers.filter(marker => marker.linkId === this.selectedSegmentId);
      }
    }
  },

  watch: {
    incidentMarkers(markers) {
      this.layers = this.composeMapLayers(markers);
      this.selectedLayers = this.layers.map(layer => layer.key);
    },
    incidentSegmentLinks() {
      this.centerMapAndZoom(this.incidentSegmentLinks, true);
    }
  },

  mounted() {
    this.$refs.mapRef &&
      this.$refs.mapRef.$mapPromise.then(map => {
        this.map = map;
        this.centerMapAndZoom(this.incidentSegmentLinks, true);
        this.addBoundingBox(this.map);
      });

    this.$bus.$on('INCIDENT_ITEM_SELECTED', item => {
      if (this.markers) {
        const marker = this.markers.find(m => m.item === item.name);
        if (marker) {
          this.selectMarker(marker.id);
        }
      }
    });
  },

  methods: {
    getIcon(marker) {
      const iconName = marker.id === this.selectedMarkerId ? `${marker.icon}Active` : marker.icon;
      return this.getIconByName(iconName);
    },

    startPoint(segment) {
      return segment.path[0];
    },

    endPoint(segment) {
      return segment.path[segment.path.length - 1];
    },

    centerMapAndZoom(segments, resetSelection) {
      if (this.map && segments) {
        if (this.incidentSegmentLinks !== segments) {
          Object.assign(this.incidentSegmentLinks, segments);
        }

        this.map.setZoom(this.zoom);
        this.centerMap(this.map, this.incidentSegmentLinks);
      }

      if (resetSelection) {
        this.selectedSegmentId = null;
      }
    },

    centerMap(map, segments) {
      if (segments.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        segments.forEach(segment => {
          if (segment.path && segment.path.length > 0) {
            for (let i = 0; i < segment.path.length; i++) {
              bounds.extend(segment.path[i]);
            }
          }
        });
        map.fitBounds(bounds, 0);
      }
    },

    zoomAndCenter() {
      this.map.setZoom(this.zoom);
      this.centerMap(this.map, this.filteredSegments);
    },

    markerClicked(marker) {
      this.selectMarker(marker.id);
      this.$emit('click', marker);
    },

    selectMarker(markerId) {
      this.selectedMarkerId = markerId;
    },

    mapMounted(map) {
      this.map = map;
      this.centerMapHandler(map);
    },

    centerMapHandler(map) {
      if (map && this.incidentSegmentLinks) {
        this.centerMap(map, this.incidentSegmentLinks);
      }
    },

    zoomSelectHandler(map) {
      const marker = this.filteredMarkers.find(m => m.id == this.selectedMarkerId);
      if (marker) {
        map.panTo(marker.position);
        map.setZoom(16);
      }
    },

    composeMapLayers(markers) {
      const types = [
        { id: 1, key: 'F', name: 'Flow Detectors' },
        { id: 2, key: 'B', name: 'Bluetooth Detectors' },
        { id: 3, key: 'W', name: 'Waze Alerts' },
        { id: 4, key: 'R', name: 'Restrictions' },
        { id: 5, key: 'V', name: 'Video Cameras' },
        { id: 6, key: 'S', name: 'Weather Station' },
        { id: 7, key: 'L', name: 'Incident Location' }
      ];

      const mapLayers = {};
      markers.forEach(marker => {
        const id = marker.id;
        const key = id.charAt(0);
        if (mapLayers.hasOwnProperty(key)) {
          mapLayers[key].push(id);
        } else {
          mapLayers[key] = [id];
        }
      });

      const layers = [];
      types.forEach(type => {
        const ids = mapLayers[type.key];
        if (ids) {
          layers.push({ id: type.id, key: type.key, name: type.name, count: ids.length });
        }
      });
      return layers;
    }
  }
};
</script>

<style scoped>
.segment-title {
  margin: 7px 0px;
}

#markerLayer img {
  border: 2px solid red !important;
  width: 85% !important;
  height: 90% !important;
  border-radius: 5px;
}
</style>

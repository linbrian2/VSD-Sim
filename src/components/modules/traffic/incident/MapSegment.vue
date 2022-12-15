<template>
  <div>
    <div class="ml-2">
      <div class="segment-title d-flex" v-if="segments.length === 1">
        <div class="mr-4">
          <v-icon>mdi-chart-timeline-variant</v-icon>
        </div>
        <div>
          <h4 class="text-truncate">{{ segmentTitle }}</h4>
        </div>
      </div>

      <div v-else>
        <v-select
          dense
          flat
          solo
          v-model="selectedSegmentId"
          :items="segmentItems"
          item-text="text"
          item-value="value"
          hide-details
          prepend-icon="mdi-chart-timeline-variant"
          @input="segmentSelected"
          single-line
        />
      </div>
    </div>

    <MapBase
      @map-ready="mapMounted"
      @center-map="centerMapHandler"
      @zoom-select="zoomSelectHandler"
      :smallMap="smallMap"
      :mapOptions="mapExtraOptions"
    >
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
        @click="markerClicked(m)"
      />
    </MapBase>
    <LayersSelectionTool :layers="layers" @layer-selected="layerClicked" v-if="layers.length > 0" />
  </div>
</template>

<script>
/* global google */
import { weatherCode } from '@/mixins/weatherCode';
import { mapIcons } from '@/mixins/mapIcons';
import MapBase from '@/components/modules/traffic/map/MapBase';
import LayersSelectionTool from '@/components/modules/traffic/incident/LayersSelectionTool';

export default {
  mixins: [weatherCode, mapIcons],
  props: {
    smallMap: Boolean,
    segments: Array,
    markers: Array
  },
  components: {
    MapBase,
    LayersSelectionTool
  },

  data: () => ({
    map: null,
    zoom: 15,

    mapExtraOptions: { scrollwheel: false },
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

    segmentItems() {
      const items = [{ text: 'ALL SEGMENTS', value: null }];
      this.segments.forEach(item => {
        items.push({ text: item.short, value: item.id });
      });
      return items;
    },

    filteredSegments() {
      if (this.selectedSegmentId === null) {
        return this.segments;
      } else {
        return this.segments.filter(segment => segment.id === this.selectedSegmentId);
      }
    },

    filteredMarkers() {
      if (this.selectedSegmentId === null) {
        return this.filterMarkers(this.markers, this.selectedLayers);
      } else {
        const markers = this.markers.filter(marker => marker.linkId === this.selectedSegmentId);
        return this.filterMarkers(markers, this.selectedLayers);
      }
    },

    segmentTitle() {
      if (this.segments.length === 1) {
        return this.segments[0].short;
      } else {
        let title = 'All Segments';
        if (this.selectedSegmentId) {
          const segment = this.segments.find(s => s.id === this.selectedSegmentId);
          if (segment) {
            title = segment.short;
          }
        }
        return title;
      }
    }
  },

  watch: {
    markers(markers) {
      this.layers = this.composeMapLayers(markers);
      this.selectedLayers = this.layers.map(layer => layer.key);
    }
  },

  mounted() {
    this.$refs.mapRef &&
      this.$refs.mapRef.$mapPromise.then(map => {
        this.map = map;
        this.centerMapAndZoom(this.segments, true);
        this.addBoundingBox(this.map);
      });

    this.$bus.$on('INCIDENT_ITEM_SELECTED', item => {
      const marker = this.markers.find(m => m.item === item.name);
      if (marker) {
        this.selectMarker(marker.id);
      }
    });

    this.$bus.$on('MARKER_SELECTED', id => {
      const marker = this.markers.find(m => m.id === id);
      if (marker) {
        this.selectMarker(marker.id);
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

    midPoint(segment) {
      return segment.path[Math.round(segment.path.length / 3)];
    },

    endPoint(segment) {
      return segment.path[segment.path.length - 1];
    },

    centerMapAndZoom(segments, resetSelection) {
      if (this.map && segments) {
        if (this.segments !== segments) {
          Object.assign(this.segments, segments);
        }

        this.map.setZoom(this.zoom);
        this.centerMap(this.map, this.segments);
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

    selectLink(segmentId) {
      this.selectedSegmentId = segmentId;
      this.zoomAndCenter();
    },

    segmentSelected(segmentId) {
      this.zoomAndCenter();
      this.$emit('select', segmentId);
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
      if (map && this.segments) {
        this.centerMap(map, this.segments);
      }
    },

    zoomSelectHandler(map) {
      const marker = this.filteredMarkers.find(m => m.id == this.selectedMarkerId);
      if (marker) {
        map.panTo(marker.position);
        map.setZoom(16);
      }
    },

    filterMarkers(markers, keys) {
      return markers.filter(marker => keys.includes(marker.id.charAt(0)));
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
    },

    layerClicked(markerKeys) {
      this.selectedLayers = markerKeys;
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

<template>
  <div>
    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="12"
      map-type-id="roadmap"
      :class="$vuetify.breakpoint.mobile && selectedSegmentIds.length > 0 ? 'map-select-mobile' : 'map-select-desktop'"
    >
      <GmapPolyline
        v-for="s in segments"
        :key="s.id"
        :title="s.desc"
        :path.sync="s.path"
        :options="segmentOptions(s)"
        @click="segmentClicked(s)"
      />
    </GmapMap>
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import OutlierRemoval from '@/utils/OutlierRemoval.js';

export default {
  props: ['segments', 'defaultColors'],
  data: () => ({
    mapMarker: {
      url: require('@/assets/green-icon-48.png'),
      size: { width: 30, height: 48, f: 'px', b: 'px' }
    },
    mapMarkerActive: {
      url: require('@/assets/orange-icon-48.png'),
      size: { width: 30, height: 48, f: 'px', b: 'px' }
    },
    homeIcon: require('@/assets/home-24.png'),
    zoomIcon: require('@/assets/zoom-24.png'),
    markerOptions: {
      url: '',
      size: { width: 20, height: 20, f: 'px', b: 'px' },
      anchor: { x: 10, y: 10 }
    },
    map: null,
    selectedSegmentIds: [],
    defaultSegmentOptions: {
      strokeColor: 'green',
      strokeOpacity: 1.0,
      strokeWeight: 8,
      zIndex: 100
    },

    options: {
      mapTypeControl: true,
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite']
      },
      streetViewControl: false,
      fullscreenControl: true,
      zoomControl: true,
      gestureHandling: 'greedy'
    }
  }),
  computed: {
    position() {
      return this.$store.state.position;
    }
  },
  watch: {
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(11);
      });
    },
    markers(markers) {
      this.$refs.mapRef.$mapPromise.then(map => {
        this.centerMap(map, markers);
      });
    }
  },
  mounted() {
    this.loadPage(this.$vuetify.theme.dark);

    this.$bus.$on('SEGMENT_SELECTED', selectedSegments => {
      if (!selectedSegments || (selectedSegments && selectedSegments.length == 0)) {
        this.selectedSegmentIds = [];
      } else {
        this.selectedSegmentIds = selectedSegments.map(marker => marker.id);
      }
    });

    this.$bus.$on('CENTER_SEGMENT', () => {
      setTimeout(() => {
        this.setCenterMap();
      }, 500);
    });

    this.$bus.$on('CENTER_MAP', () => {
      this.setCenterMap();
    });

    this.$bus.$on('ZOOM_SELECTED', () => {
      this.zoomSelectedSegments();
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
    });

    this.addMapControls();
  },
  methods: {
    loadPage(darkMode) {
      if (this.$refs.mapRef == null) {
        return;
      }
      if (darkMode && this.$refs.mapRef) {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: DarkMapStyle });
        });
      } else {
        this.$refs.mapRef.$mapPromise.then(map => {
          map.setOptions({ styles: null });
        });
      }
    },

    getMap() {
      return this.$refs.mapRef;
    },

    addMapControls() {
      this.$refs.mapRef.$mapPromise.then(map => {
        this.setMapIconLocations(map);
        this.addHomeControl(map);
        this.addPointControl(map);
      });
    },

    setMapIconLocations(map) {
      map.setOptions({
        zoomControlOptions: {
          position: google.maps.ControlPosition.RIGHT_CENTER
        }
      });
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
            this.centerMap(map, this.segments);
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    addPointControl(map) {
      let options = {
        position: 'right',
        content: `<div style="margin:-5px 4px;"><img src="${this.zoomIcon}"/></div>`,
        title: 'Click to zoom in the selected marker',
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
            this.zoomSelectedSegments();
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    midPoint(segment) {
      return segment.path[Math.round(segment.path.length / 2)];
    },

    segmentOptions(segment) {
      if (this.defaultColors) {
        const color = this.selectedSegmentIds.includes(segment.id) ? 'blue' : 'green';
        return { ...this.defaultSegmentOptions, strokeColor: color };
      } else {
        const color = this.selectedSegmentIds.includes(segment.id)
          ? 'blue'
          : Utils.getStrokeColor(segment.travelTime.level);
        return { ...this.defaultSegmentOptions, strokeColor: color };
      }
    },

    centerAndZoom(marker, zoomLevel) {
      if (this.map) {
        this.map.panTo(marker.position);
        this.map.setZoom(zoomLevel);
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

    zoomSelectedSegments() {
      const segments = this.segments.filter(s => this.selectedSegmentIds.includes(s.id));
      if (segments.length > 0) {
        this.map.panTo(segments[0].position);
        this.map.setZoom(13);
      }
    },

    segmentClicked(s) {
      let action = 'add';
      if (this.selectedSegmentIds.includes(s.id)) {
        this.selectedSegmentIds = this.selectedSegmentIds.filter(x => x != s.id);
        action = 'remove';
      } else {
        this.selectedSegmentIds.push(s.id);
      }

      this.$emit('click', s, action);
    },

    selectByIds(ids) {
      this.selectedSegmentIds = [];
      Object.assign(this.selectedSegmentIds, ids);

      if (ids.length > 0) {
        let id = ids[Math.floor(ids.length / 2)];
        let marker = this.markers.find(m => m.id == id);
        if (marker != null) {
          this.centerAndZoom(marker, 12);
        }
      }
    },

    triggerFirstSegmentClick() {
      if (this.segments && this.segments.length > 0) {
        const selectedSegment = this.segments[0];
        this.segmentClicked(selectedSegment);
      }
    },

    setCenterMap() {
      if (this.map && this.segments) {
        this.centerMap(this.map, this.segments);
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
<style lang="scss" scoped>
.map-select {
  width: 100%;
  height: calc(100vh - 96px);
}
.gm-style div {
  -webkit-transform: scale(1.007);
}
</style>

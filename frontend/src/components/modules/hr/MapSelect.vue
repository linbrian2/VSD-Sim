<template>
  <div>
    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="11"
      map-type-id="roadmap"
      :class="$vuetify.breakpoint.mobile ? 'map-select-mobile' : 'map-select-desktop'"
    >
      <GmapMarker
        v-for="m in markers"
        :key="m.id"
        :position="m.position"
        :title="m.name"
        :clickable="true"
        :icon="getMarker(m)"
        @click="markerClicked(m)"
      />

      <GmapCustomMarker
        alignment="center"
        v-for="m in markers"
        :key="`LL-${m.id}`"
        :offsetX="45"
        :offsetY="0"
        :marker="m.position"
      >
        <v-chip small :color="getChipColor(m.id)" @click="markerClicked(m)">
          <span class="white--text font-weight-bold">{{ m.permit }}</span>
        </v-chip>
      </GmapCustomMarker>
    </GmapMap>
    <PhaseSelectionTool @click="phaseSelected" v-if="isLive" />
  </div>
  <!-- <div v-else> v-if="$route.name !== 'Signal Timing'"
    <template v-if="showMapForSignal">
      <GmapMap
        ref="mapRef"
        :options="options"
        :center="position"
        :zoom="11"
        map-type-id="roadmap"
        class="map-select-desktop-2"
      >
        <GmapMarker
          v-for="m in markers"
          :key="m.id"
          :position="m.position"
          :title="m.name"
          :clickable="true"
          :icon="getMarker(m)"
          @click="markerClicked(m)"
        />

        <GmapCustomMarker
          alignment="center"
          v-for="m in markers"
          :key="`LL-${m.id}`"
          :offsetX="45"
          :offsetY="0"
          :marker="m.position"
        >
          <v-chip small :color="getChipColor(m.id)" @click="markerClicked(m)">
            <span class="white--text font-weight-bold">{{ m.permit }}</span>
          </v-chip>
        </GmapCustomMarker>
      </GmapMap>
      <PhaseSelectionTool @click="phaseSelected" v-if="isLive" />
    </template>
  </div> -->
</template>

<script>
/* global google */
import { mapState, mapActions } from 'vuex';
import DarkMapStyle from '@/utils/DarkMapStyle.js';
import MapUtils from '@/utils/MapUtils.js';
import Utils from '@/utils/Utils.js';
import { RouterNames } from '@/utils/constants/router';
import { TrafficLightIcons } from '@/mixins/TrafficLightIcons.js';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import PhaseSelectionTool from '@/components/modules/hr/PhaseSelectionTool';

export default {
  mixins: [TrafficLightIcons],
  components: {
    GmapCustomMarker,
    PhaseSelectionTool
  },
  data: () => ({
    homeIcon: require('@/assets/home-24.png'),

    markerOptions: {
      url: '',
      size: { width: 60, height: 90, f: 'px', b: 'px' },
      scaledSize: { width: 30, height: 45, f: 'px', b: 'px' }
    },
    map: null,
    selectedKey: null,
    signals: null,
    curPhaseId: 2,
    options: {
      mapTypeControl: false,
      streetViewControl: false,
      fullscreenControl: false,
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'satellite']
      },
      gestureHandling: 'greedy'
    }
  }),
  computed: {
    markers() {
      return this.$store.state.hr.locations;
    },
    position() {
      return this.$store.state.position;
    },
    isLive() {
      return this.$route.name === RouterNames.HR_SIGNAL_DISPLAY && this.signalTimingMode == 'Live';
    },
    ...mapState('hr', ['signalTimingMode', 'showMapForSignal'])
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

  async mounted() {
    this.$bus.$on('NAME_SELECTED', name => {
      let marker = this.markers.find(m => m.name === name);
      if (marker != null) {
        this.markerClicked(marker);
      }
    });

    this.$bus.$on('CENTER_MAP', () => {
      this.centerMap(this.map, this.markers);
    });
    this.$bus.$on('RELOAD_SIGNAL_MAP', () => {
      this.reload();
    });

    this.$bus.$on('LIVE_UPDATE_SIGNALS', signals => {
      this.signals = signals;
    });

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });

    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
    });

    this.addControls();
    await this.fetchLocations();
    let selected = this.$store.state.hr.activeMarker;
    if (selected !== null) {
      this.selectedKey = selected.id;
    }
    this.loadPage(this.$vuetify.theme.dark);
  },

  methods: {
    async reload() {
      this.addControls();
      await this.fetchLocations();
      let selected = this.$store.state.hr.activeMarker;
      if (selected !== null) {
        this.selectedKey = selected.id;
      }
      this.loadPage(this.$vuetify.theme.dark);
    },
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
    addControls() {
      this.$refs.mapRef.$mapPromise.then(map => {
        let options = {
          position: 'top_right',
          content: `<div style="margin:-5px 4px;"><img src="${this.homeIcon}"/></div>`,
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
              this.centerMap(map, this.markers);
            }
          }
        };
        MapUtils.addControl(map, options);
      });
    },
    centerMap(map, markers) {
      if (markers.length > 2) {
        const bounds = new google.maps.LatLngBounds();
        for (let i = 0; i < markers.length - 1; i++) {
          bounds.extend(markers[i].position);
        }
        map.fitBounds(bounds);
      }
    },

    getTrafficLightIcon(permit) {
      let result = this.trafficLightIcon;
      if (!Utils.isEmpty(this.signals)) {
        const ICONS = {
          G: this.trafficLightGIcon,
          Y: this.trafficLightYIcon,
          R: this.trafficLightRIcon,
          F: this.trafficLightIcon
        };
        if (permit in this.signals) {
          let idx = this.curPhaseId ? this.curPhaseId - 1 : 1;
          if (idx < 0 || idx > 7) {
            idx = 1;
          }
          const m = this.signals[permit];
          const status = m.status[idx];
          if (status !== 'X') {
            result = ICONS[status];
          }
        }
      }
      return result;
    },

    getMarker(marker) {
      if (this.isLive) {
        return this.selectedKey === marker.id ? this.trafficLightIconActive : this.trafficLightIcon;
      } else {
        return this.getTrafficLightIcon(marker.permit);
      }
    },

    getChipColor(key) {
      return this.selectedKey === key ? 'orange' : 'gray';
    },

    markerClicked(marker) {
      this.selectedKey = marker.id;
      this.$store.commit('hr/SET_ACTIVE_MARKER', marker);
      this.$store.commit('hr/SET_SHOW_MAP_FOR_SIGNAL', false);
      this.$emit('click', marker);
    },

    phaseSelected(phaseId) {
      if (this.isLive) {
        this.curPhaseId = phaseId;
      }
    },

    ...mapActions('hr', ['fetchLocations', 'fetchTiming'])
  }
};
</script>
<style lang="scss" scoped>
.my-map {
  width: 100%;
  height: calc(100vh - 84px);
}
.gm-style div {
  -webkit-transform: scale(1.002);
}
</style>

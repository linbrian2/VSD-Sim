<template>
  <div v-if="show">
    <gmap-polygon v-for="signal in signals" :key="signal.id" :options="signal.options" :paths="signal.paths" />

    <!-- Display signal phase -->
    <gmap-custom-marker
      alignment="bottomright"
      v-for="signal in signals"
      :key="`P-${signal.id}`"
      :offsetX="10"
      :offsetY="5"
      :marker="signal.anchor"
    >
      <h3 style="color:white">{{ signal.value }}</h3>
    </gmap-custom-marker>

    <!-- Display countdown timer -->
    <gmap-custom-marker
      alignment="top"
      v-for="signal in signals"
      :key="`C-${signal.id}`"
      :offsetX="10"
      :offsetY="-40"
      :marker="signal.center"
    >
      <div v-if="counterdownAvailable(signal)">
        <v-chip color="purple">
          <span class="font-weight-bold white--text">{{ signal.timeRemaining }}</span>
        </v-chip>
      </div>
    </gmap-custom-marker>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import Constants from '@/utils/constants/hr';
import Utils from '@/utils/Utils.js';
import SignalPolygon from '@/utils/SignalPolygon';

export default {
  props: {
    spat: Object
  },

  components: {
    'gmap-custom-marker': GmapCustomMarker
  },

  data: () => ({
    show: true,
    signals: [],
    flashPhases: [],
    flashHandle: null,
    flashStatus: false,
    lineOptions: {
      strokeColor: '#FA8072',
      strokeOpacity: 0.8,
      strokeWeight: 1
    }
  }),

  computed: {
    changeData() {
      return {
        proj: this.mapProjection,
        marker: this.activeMarker
      };
    },

    ...mapState('hr', ['activeMarker', 'mapProjection'])
  },

  watch: {
    changeData: {
      handler: function(v) {
        if (v.proj !== null && v.marker != null) {
          this.init();
        }
      },
      deep: true
    },

    spat() {
      this.changeSignal(this.spat);
    }
  },

  mounted() {
    // Setup map change event
    let mapRef = this.$parent.map || this.$parent.$parent.getMap();
    mapRef.$mapPromise.then(map => {
      google.maps.event.addListener(map, 'zoom_changed', () => {
        let zoom = map.getZoom();
        this.show = zoom > 17 ? true : false;

        let proj = map.getProjection();
        this.$store.commit('hr/SET_MAP_PROJECTION', proj);
      });

      google.maps.event.addListener(map, 'projection_changed', () => {
        let proj = map.getProjection();
        this.$store.commit('hr/SET_MAP_PROJECTION', proj);
      });
    });

    setTimeout(() => {
      if (this.signals.length === 0) {
        console.log('Initialized');
        this.init();
      }
    }, 2000);
  },

  beforeDestroy() {
    if (this.flashHandle) {
      clearInterval(this.flashHandle);
    }
  },

  methods: {
    init() {
      if (this.activeMarker && this.mapProjection) {
        this.generateSignals(this.activeMarker, this.mapProjection);
      } else {
        console.log(this.activeMarker, this.mapProjection);
      }
    },

    counterdownAvailable(signal) {
      return signal.status === 'Y' && signal.timeRemaining > 0 && signal.timeRemaining < 10;
    },

    generateSignals(marker, projection) {
      if (marker) {
        this.signals = [];
        const locations = this.$store.state.hr.locations || [];
        const location = locations.find(location => location.id === marker.id);
        if (location && 'layout' in location) {
          let signals = location.layout.signals;
          Object.keys(signals).forEach(phaseId => {
            let signal = signals[phaseId];
            this.addSignal(phaseId, projection, signal.id, signal.location, signal.rotation, signal.type);
          });
        }
      }
    },

    addSignal(phaseId, projection, id, center, angle, type) {
      const ratio = 3.5;
      let polygon;
      switch (type) {
        case 0:
          polygon = SignalPolygon.arrow(center, ratio, -angle, projection);
          break;
        case 1:
          polygon = SignalPolygon.leftArrow(center, ratio, -angle, projection);
          break;
        case 2:
          polygon = SignalPolygon.rightArrow(center, ratio, -angle, projection);
          break;
      }

      let signal = {
        id: id,
        type: Constants.SIGNAL,
        phase: phaseId,
        value: 'P' + phaseId,
        options: {
          strokeColor: '#111111',
          strokeOpacity: 0.8,
          strokeWeight: 1,
          fillColor: '#FF0000',
          fillOpacity: 1.0,
          zIndex: 30
        },
        center: center,
        anchor: polygon[0],
        paths: polygon,
        light: 0,
        timeRemaining: 1000,
        status: 'R'
      };

      this.signals.push(signal);
    },

    updateFlashingPhases(values) {
      const phases = [];
      for (let i = 0; i < values.length; i++) {
        if (values[i] === 'F') {
          const phaseId = i + 1;
          phases.push(phaseId);
        }
      }
      this.flashPhases = phases;

      if (this.flashPhases.length > 0) {
        if (this.flashHandle === null) {
          this.flashHandle = setInterval(() => {
            this.flashSignal();
          }, 500);
        }
      } else {
        if (this.flashHandle) {
          clearInterval(this.flashHandle);
          this.flashHandle = null;
        }
      }
    },

    flashSignal() {
      if (this.flashPhases.length > 0) {
        const strokeOpacity = this.flashStatus ? 0.1 : 0.8;
        const fillOpacity = this.flashStatus ? 0.1 : 1.0;

        this.flashPhases.forEach(phaseId => {
          this.signals.forEach(signal => {
            if (signal.phase == phaseId) {
              const options = {
                strokeColor: '#111111',
                strokeOpacity,
                strokeWeight: 1,
                fillColor: '#FFFF00',
                fillOpacity,
                zIndex: 30
              };
              signal.options = options;
            }
          });
        });

        this.flashStatus = !this.flashStatus;
      }
    },

    changeSignal(spat) {
      if (!Utils.isEmpty(spat)) {
        //console.log(spat.status);
        this.updateFlashingPhases(spat.status);

        // Traffic Light: Green, Yellow and Red
        const colors = { G: '#00FF00', Y: '#FFFF00', R: '#FF0000' };
        this.signals.forEach(signal => {
          const idx = signal.phase - 1;
          if (idx >= 0 && idx < spat.status.length) {
            const colorCode = spat.status[idx];
            if (colorCode != null) {
              signal.options.fillColor = colors[colorCode];
              signal.options.strokeOpacity = 0.8;
              signal.options.fillOpacity = 1.0;
              signal.timeRemaining = spat.ttc[idx];
              signal.status = colorCode;
            }
          }
        });
      }
    }
  }
};
</script>

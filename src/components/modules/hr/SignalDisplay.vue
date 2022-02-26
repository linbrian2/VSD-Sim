<template>
  <div v-if="show">
    <gmap-polygon v-for="signal in signals" :key="signal.id" :options="signal.options" :paths="signal.paths" />

    <!-- Display detector counts -->
    <gmap-custom-marker
      alignment="bottomright"
      v-for="(signal, index) in detectors"
      :key="index"
      :offsetX="0"
      :offsetY="12"
      :marker="signal.center"
    >
      <h3 style="color:white">{{ signal.count }}</h3>
    </gmap-custom-marker>

    <!-- Display detector labels -->
    <!-- <gmap-custom-marker
      alignment="bottomright"
      v-for="(signal, index) in detectors"
      :key="index + 150"
      :offsetX="0"
      :offsetY="-12"
      :marker="signal.center"
    >
      <h3 style="color:yellow">{{ signal.id }}</h3>
    </gmap-custom-marker> -->

    <!-- Display signal phase -->
    <gmap-custom-marker
      alignment="bottomright"
      v-for="signal in signalPhases"
      :key="`P-${signal.id}`"
      :offsetX="10"
      :offsetY="5"
      :marker="signal.anchor"
    >
      <h3 style="color:white">{{ signal.value }}</h3>
    </gmap-custom-marker>

    <!-- Display countdown timer -->
    <gmap-custom-marker
      alignment="bottomright"
      v-for="(signal, index) in signalPhases"
      :key="index + 2000"
      :offsetX="40"
      :offsetY="5"
      :marker="signal.center"
    >
      <div v-if="signal.timeRemaining < 20">
        <v-chip color="purple"
          ><span class="font-weight-bold white--text">{{ signal.timeRemaining }}</span></v-chip
        >
      </div>
    </gmap-custom-marker>

    <!-- Display car moving -->
    <GmapMarker v-for="car in cars" :key="car.id + 3000" :position="car.position" :icon="car.icon" />
  </div>
</template>

<script>
/* global google */
import { mapState } from 'vuex';
import GmapCustomMarker from 'vue2-gmap-custom-marker';
import Constants from '@/utils/constants/hr';
import SignalPolygon from '@/utils/SignalPolygon';
import { computeDistanceBetween, computeHeading } from 'spherical-geometry-js';

export default {
  components: {
    'gmap-custom-marker': GmapCustomMarker
  },
  data: () => ({
    handle: null,
    show: false,
    signalUpdateTimerId: null,
    carUpdateTimerId: null,
    startTime: new Date(),
    signals: [],
    signalHandles: [],
    detectorHandles: [],
    detectorValues: [],
    cars: [],
    // Car Icon
    carIcon: {
      path:
        'M17.402,0H5.643C2.526,0,0,3.467,0,6.584v34.804c0,3.116,2.526,5.644,5.643,5.644h11.759c3.116,0,5.644-2.527,5.644-5.644 V6.584C23.044,3.467,20.518,0,17.402,0z M22.057,14.188v11.665l-2.729,0.351v-4.806L22.057,14.188z M20.625,10.773 c-1.016,3.9-2.219,8.51-2.219,8.51H4.638l-2.222-8.51C2.417,10.773,11.3,7.755,20.625,10.773z M3.748,21.713v4.492l-2.73-0.349 V14.502L3.748,21.713z M1.018,37.938V27.579l2.73,0.343v8.196L1.018,37.938z M2.575,40.882l2.218-3.336h13.771l2.219,3.336H2.575z M19.328,35.805v-7.872l2.729-0.355v10.048L19.328,35.805z',
      scale: 0.7,
      strokeColor: 'black',
      strokeWeight: 0.6,
      fillOpacity: 1,
      fillColor: 'red',
      offset: '50%'
    },
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
        timing: this.timing
      };
    },

    signalPhases() {
      return this.signals.filter(s => s.type === Constants.SIGNAL);
    },

    detectors() {
      return this.signals.filter(s => s.type === Constants.DETECTOR);
    },

    routes() {
      return this.signals.filter(s => s.type === Constants.DETECTOR && s.route);
    },
    ...mapState('hr', ['activeMarker', 'timing', 'mapProjection'])
  },
  watch: {
    changeData: {
      handler: function(v) {
        if (v.proj !== null && v.timing != null) {
          this.startPeriodicalTask();
        }
      },
      deep: true
    }
  },

  mounted() {
    // Setup map change event
    let mapRef = this.$parent.map || this.$parent.$parent.getMap();
    mapRef.$mapPromise.then(map => {
      google.maps.event.addListener(map, 'zoom_changed', () => {
        let zoom = map.getZoom();
        this.show = zoom > 17 ? true : false;

        if (this.mapProjection === null) {
          let proj = map.getProjection();
          this.$store.commit('hr/SET_MAP_PROJECTION', proj);
        }
      });

      google.maps.event.addListener(map, 'maptypeid_changed', () => {
        let type = map.getMapTypeId();
        this.changeDetectorColor(type === 'roadmap' ? '#FFFFFF' : '#FFFF00');
      });

      google.maps.event.addListener(map, 'projection_changed', () => {
        if (this.mapProjection === null) {
          let proj = map.getProjection();
          this.$store.commit('hr/SET_MAP_PROJECTION', proj);
        }
      });
    });
  },

  beforeDestroy() {
    if (this.signalUpdateTimerId != null) {
      clearInterval(this.signalUpdateTimerId);
    }
    if (this.carUpdateTimerId != null) {
      clearInterval(this.carUpdateTimerId);
    }
  },

  methods: {
    generateSignals(deviceId, projection) {
      this.signals = [];
      const locations = this.$store.state.hr.locations || [];
      const location = locations.find(location => location.id === deviceId);
      if (location && 'layout' in location) {
        let signals = location.layout.signals;
        Object.keys(signals).forEach(phaseId => {
          let signal = signals[phaseId];
          this.addSignal(phaseId, projection, signal.id, signal.location, signal.rotation, signal.type);
        });

        let detectors = location.layout.detectors;
        Object.keys(detectors).forEach(detectorId => {
          let detector = detectors[detectorId];
          this.addDetector(
            projection,
            detector.id,
            detector.location,
            detector.size,
            detector.rotation,
            detector.route,
            detector.phaseId
          );
        });
      }
    },

    addDetector(projection, id, center, size, angle, route, phaseId) {
      const ratio = 0.65;
      let polygon = SignalPolygon.rectangle(center, ratio, (ratio * size.height) / size.width, -angle, projection);
      let signal = {
        id: id,
        type: Constants.DETECTOR,
        phaseId: phaseId,
        count: 0,
        options: {
          strokeColor: '#FFFFFF',
          strokeOpacity: 0.95,
          strokeWeight: 2,
          fillColor: '#0000FF',
          fillOpacity: 0.1,
          zIndex: 20
        },
        center: center,
        paths: polygon,
        route: route
      };
      this.signals.push(signal);
    },

    addSignal(phaseId, projection, id, center, angle, type) {
      const ratio = 3.0;
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
        duration: 0,
        timeRemaining: 1000
      };

      this.signals.push(signal);
    },

    updateSignalsRemainingTime() {
      this.signals.forEach(signal => {
        if (signal.type === Constants.SIGNAL) {
          this.calcSignalRemainingTime(signal);
        }
      });
    },

    calcSignalRemainingTime(signal) {
      let result = 1000;
      if (signal && signal.duration > 0) {
        const currentTime = new Date();
        const timePast = currentTime.getTime() - this.startTime.getTime();
        const timeLeft = Math.floor((signal.duration - timePast) / 1000);
        if (timeLeft >= 0) {
          result = timeLeft;
        }
      }
      signal.timeRemaining = result;
    },

    changeSignal(id, type, duration) {
      const signal = this.signals.find(signal => signal.id === id);
      if (signal) {
        const colors = ['#00FF00', '#FFFF00', '#FF0000'];
        signal.options.fillColor = colors[type];
        signal.light = type;
        signal.duration = duration;
      }
    },

    changeDetector(id, value) {
      const signal = this.signals.find(signal => signal.id === id);
      if (signal) {
        signal.options.fillOpacity = value > 0 ? 0.9 : 0.0;
        signal.count += value;
      }
    },

    applyDetectorsTiming(timingSeries) {
      if (timingSeries) {
        // Cancel the current timers
        this.detectorHandles.forEach(h => clearTimeout(h));
        this.detectorHandles = [];
        this.detectorValues = [];

        // Clear out counts
        this.signals.forEach(d => {
          if (d.type === Constants.DETECTOR) {
            d.count = 0;
          }
        });

        // Apply the new timers
        timingSeries.forEach(t => {
          let h = setTimeout(
            (id, value) => {
              this.changeDetector(id, value);
            },
            t.time,
            t.id,
            t.value
          );
          this.detectorHandles.push(h);
          this.detectorValues.push({ id: t.id, time: t.time, value: t.value });
        });
      }
    },

    applySignalsTiming(timingSeries) {
      if (timingSeries) {
        // Cancel the current timers
        this.signalHandles.forEach(h => clearTimeout(h));
        this.signalHandles = [];

        // Update start time
        this.startTime = new Date();

        // Initialize the state for all signals
        if (timingSeries.length > 0) {
          this.signals.forEach(signal => {
            if (signal.type === Constants.SIGNAL) {
              let id = signal.id;
              let ss = timingSeries.find(t => t.id === id);
              if (ss) {
                // Init the state with the previous type
                let prevType = ss.type - 1;
                if (prevType < 0) {
                  prevType = 2;
                }
                this.changeSignal(id, prevType, ss.time);
              }
            }
          });
        }

        // Apply the new timers
        for (let i = 0; i < timingSeries.length - 1; i++) {
          let t = timingSeries[i];

          // Find next phase change
          let next = timingSeries.find((ti, idx) => ti.id === t.id && idx > i);
          let nextT = next ? next.time : -1;

          // Set timeout handler
          let h = setTimeout(
            (id, type, nextT) => {
              this.changeSignal(id, type, nextT);
            },
            t.time,
            t.id,
            t.type,
            nextT
          );
          this.signalHandles.push(h);
        }
      }
    },

    changeDetectorColor(color) {
      this.signals.forEach(d => {
        if (d.type === Constants.DETECTOR) {
          d.options.strokeColor = color;
        }
      });
    },

    updateCarsMovement() {
      const TIME_DURATION = 5000; // 6 seconds
      const currentTime = new Date();
      const timePast = currentTime.getTime() - this.startTime.getTime();

      // Add new cars
      this.detectorValues.forEach((d, idx) => {
        if (d.value > 0 && d.time > timePast - TIME_DURATION && d.time < timePast + TIME_DURATION) {
          const car = this.cars.find(c => c.id === idx);
          if (!car) {
            this.addCar(idx, d.id, d.time, timePast);
          }
        }
      });

      // Remove unused cars
      for (let i = this.cars.length - 1; i >= 0; i--) {
        let car = this.cars[i];
        if (car.timeRemaining < -TIME_DURATION) {
          this.cars.splice(i, 1);
        }
      }

      // Update all existing cars
      this.cars.forEach(car => {
        // Update the remaining time
        this.updateCarLifetime(car);

        // Update the car position
        this.updateCarPosition(car);
      });
    },

    addCar(id, detectorId, duration, timePast) {
      const color = this.randomCarColor();

      let icon = Object.assign({}, this.carIcon);
      icon.rotation = 0;
      icon.fillColor = color;
      icon.anchor = new google.maps.Point(10, 25); // orig 10,50 back of car, 10,0 front of car, 10,25 center of car

      const timeRemaining = duration - timePast;
      let car = { id, position: null, duration, timeRemaining, icon, detectorId };
      this.cars.push(car);
    },

    updateCarLifetime(car) {
      const currentTime = new Date();
      const timePast = currentTime.getTime() - this.startTime.getTime();
      car.timeRemaining = car.duration - timePast;
    },

    updateCarPosition(car) {
      let detector = this.signals.find(d => d.id === car.detectorId);
      if (detector) {
        // When the traffic light is RED, stop updating
        const trafficLight = this.getCurrentSignalValue(detector.phaseId);
        if (trafficLight !== 0) {
          car.position = undefined;
          return;
        }

        if (detector.route) {
          const SPEED_MPH = 50;
          const position = this.getLocationFromRoute(car.timeRemaining, detector.route, SPEED_MPH);
          if (position) {
            this.moveCar(car, position.cur, position.last);
          } else {
            car.position = undefined;
          }
        }
      }
    },

    moveCar(car, pos, lastPos) {
      // Check distance
      const distance = computeDistanceBetween(lastPos, pos);
      if (distance > 1) {
        // Calculate the car heading direction
        let heading = computeHeading(lastPos, pos);

        // Create a new icon to enable reactivity of vue rendering
        let icon = Object.assign({}, car.icon);
        icon.rotation = heading;
        car.icon = icon;
        car.position = pos;
      }
    },

    randomCarColor() {
      // Generate color based on popularity distribution
      const VEHICLE_COLORS = [
        { name: 'black', color: '#000000', popularity: 22.25 },
        { name: 'white', color: '#FFFFFF', popularity: 19.34 },
        { name: 'grey', color: '#808080', popularity: 17.63 },
        { name: 'silver', color: '#AAA9AD', popularity: 14.64 },
        { name: 'blue', color: '#0000FF', popularity: 9.62 },
        { name: 'red', color: '#FF0000', popularity: 9.54 },
        { name: 'brown', color: '#AC4313', popularity: 2.16 },
        { name: 'gold', color: '#DAAF37', popularity: 1.44 },
        { name: 'green', color: '#00FF00', popularity: 1.35 },
        { name: 'tan', color: '#D2B48C', popularity: 0.89 },
        { name: 'orange', color: '#DA680F', popularity: 0.56 }
      ];

      // Transform ratios to percentages
      let acc = 0;
      const total = VEHICLE_COLORS.reduce((sum, v) => (sum += v.popularity), 0);
      const accPercentages = VEHICLE_COLORS.map(v => (acc += v.popularity / total));

      // choose biased random(accPercentages)
      const random = Math.random();
      const index = accPercentages.findIndex(acc => random < acc);

      return VEHICLE_COLORS[index].color;
    },

    interpolationCoords(coord1, coord2, alpha) {
      let lat = coord1.lat * alpha + coord2.lat * (1.0 - alpha);
      let lng = coord1.lng * alpha + coord2.lng * (1.0 - alpha);
      return { lat, lng };
    },

    getLocationFromRoute(timeRemaining, route, speedMPH) {
      if (!route) {
        return undefined;
      }

      // The center point index on the route
      let centerIdx = route.centerIdx;

      if (timeRemaining == 0) {
        let last = this.interpolationCoords(route.coords[centerIdx], route.coords[centerIdx - 1], 0.5);
        return { cur: route.coords[centerIdx], last };
      }

      // To m/s
      let speed = speedMPH * 0.44704;
      let expectedDist = Math.abs((speed * timeRemaining) / 1000);
      const range = this.calcRouteDistance(route, timeRemaining);
      //console.log('range=', range, 'expectedDist=', expectedDist);
      if (expectedDist > range) {
        // Beyond the range on the route
        return undefined;
      }

      let total = 0;
      if (timeRemaining > 0) {
        for (let i = centerIdx; i > 0; i--) {
          let curDist = computeDistanceBetween(route.coords[i], route.coords[i - 1]);
          total += curDist;
          if (total >= expectedDist) {
            // Interpolation
            let alpha = (total - expectedDist) / curDist;
            let cur = this.interpolationCoords(route.coords[i], route.coords[i - 1], alpha);
            //console.log('alpha=', alpha, 'a cur=', cur);
            return { cur, last: route.coords[i - 1] };
          }
        }

        // Beyond the first point on the route
        return undefined;
      } else {
        for (let i = centerIdx; i < route.coords.length - 1; i++) {
          let curDist = computeDistanceBetween(route.coords[i], route.coords[i + 1]);
          total += curDist;
          if (total >= expectedDist) {
            // Interpolation
            let alpha = (total - expectedDist) / curDist;
            let cur = this.interpolationCoords(route.coords[i], route.coords[i + 1], alpha);
            //console.log('alpha=', alpha, 'b cur=', cur);
            return { cur, last: route.coords[i] };
          }
        }

        // Beyond the last point on the route
        return undefined;
      }
    },

    calcRouteDistance(route, dir) {
      const centerIdx = route.centerIdx;
      let dist = 0;
      if (dir > 0) {
        for (let i = centerIdx; i > 0; i--) {
          dist += computeDistanceBetween(route.coords[i], route.coords[i - 1]);
        }
      } else {
        for (let i = centerIdx; i < route.coords.length - 1; i++) {
          dist += computeDistanceBetween(route.coords[i], route.coords[i + 1]);
        }
      }
      return dist;
    },

    getCurrentSignalValue(phaseId) {
      if (phaseId && phaseId > 0) {
        const id = `${this.activeMarker.id}-S${phaseId}`;
        const signal = this.signals.find(signal => signal.id === id);
        return signal ? signal.light : 2;
      }
    },

    startCarsMovementUpdate() {
      if (this.carUpdateTimerId != null) {
        clearInterval(this.carUpdateTimerId);
      }

      const interval = 200;
      this.carUpdateTimerId = setInterval(() => {
        this.updateCarsMovement(interval);
      }, interval);
    },

    startSignalRemainingTimeUpdate() {
      if (this.signalUpdateTimerId != null) {
        clearInterval(this.signalUpdateTimerId);
      }
      this.signalUpdateTimerId = setInterval(() => {
        this.updateSignalsRemainingTime();
      }, 250);
    },

    startPeriodicalTask() {
      this.generateSignals(this.activeMarker.id, this.mapProjection);
      this.applySignalsTiming(this.timing.signals);
      this.applyDetectorsTiming(this.timing.detectors);
      this.startSignalRemainingTimeUpdate();
      this.startCarsMovementUpdate();
      this.$emit('startTask', {});
    }
  },
  filters: {
    format: function(name) {
      return name.split('-')[1];
    }
  }
};
</script>

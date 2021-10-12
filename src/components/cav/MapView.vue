<template>
  <div>
    <GmapMap
      ref="mapRef"
      :options="options"
      :center="position"
      :zoom="12"
      map-type-id="roadmap"
      class="my-map"
      style="margin-top:-1px; width: 100%; height:calc(100vh - 48px)"
    >
      <CarMarker
        v-for="item in liveCars"
        :key="item.id"
        :visible="true"
        :info="item"
        :color="item.color"
        :position="item.position"
      />
    </GmapMap>
  </div>
</template>

<script>
/* global google */
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import CarMarker from '@/components/cav/CarMarker';
import MapUtils from '@/utils/MapUtils.js';
import { websocketUrl } from '@/utils/websocketUrl';
import DarkMapStyle from '@/utils/DarkMapStyle.js';

export default {
  components: {
    CarMarker
  },

  data: () => ({
    homeIcon: require('@/assets/home-24.png'),
    infoIcon: require('@/assets/info-24.png'),
    map: null,
    options: {
      mapTypeControl: true,
      streetViewControl: false,
      fullscreenControl: true,
      fullscreenControlOptions: {
        position: google.maps.ControlPosition.TOP_LEFT
      },
      mapTypeControlOptions: {
        mapTypeIds: ['roadmap', 'hybrid'],
        position: 6
      }
      //styles: MapStyles
    },
    inactiveSeconds: 900,
    cleanUpInterval: 60000,
    animateInterval: 250,
    cluenupTimerHandle: null,
    animateTimerHandle: null
  }),

  computed: {
    position() {
      return this.$store.state.cav.position;
    },

    ...mapState(['currentUpdates', 'liveCars']),
    ...mapState('cav', ['socket'])
  },

  watch: {
    position() {
      this.$refs.mapRef.$mapPromise.then(map => {
        map.panTo(this.position);
        map.setZoom(12);
      });
    },

    currentUpdates(updates) {
      this.updateLiveCars(updates);
    }
  },

  mounted() {
    this.$refs.mapRef.$mapPromise.then(map => {
      this.map = map;
      this.addHomeControlToMap(map);
      this.addInfoControl(map);
    });

    this.$bus.$on('SENSOR_SELECTED', deviceId => {
      this.centerSelectedCar(deviceId);
    });

    this.startWebSocket();
    this.startCleanUpTimer();

    this.$bus.$on('UPDATE_DARK_MODE', darkMode => {
      this.loadPage(darkMode);
    });
    this.loadPage(this.$vuetify.theme.dark);
  },

  beforeDestroy() {
    this.stopWebSocket();
    this.stopCleanUpTimer();
    this.stopAnimateCars();
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

    addHomeControlToMap(map) {
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
            this.centerMapByLiveCars();
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    addInfoControl(map) {
      let options = {
        position: 'right',
        content: `<div class="non-selection" style="margin:-5px 4px;"><img src="${this.infoIcon}"/></div>`,
        title: 'Show vehicle list',
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
            this.$store.commit('cav/TOGGLE_SHOW_PANEL');
          }
        }
      };
      MapUtils.addControl(map, options);
    },

    centerMapByLiveCars() {
      const points = this.liveCars.map(item => item.position);
      if (points.length > 0) {
        this.centerMap(this.map, points);
      }
    },

    centerMap(map, points) {
      if (points.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        points.forEach(point => bounds.extend(point));

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

    centerSelectedCar(id) {
      const selected = this.liveCars.find(item => item.id === id);
      if (selected) {
        const position = selected.position;
        this.map.setCenter(position);
      }
    },

    randomCarColor() {
      // Generate color based on popularity distribution
      const VEHICLE_COLORS = [
        // { name: 'black', color: '#000000', popularity: 22.25 },
        // { name: 'white', color: '#FFFFFF', popularity: 19.34 },
        // { name: 'grey', color: '#808080', popularity: 17.63 },
        // { name: 'silver', color: '#AAA9AD', popularity: 14.64 },
        { name: 'blue', color: '#0000FF', popularity: 10 },
        { name: 'red', color: '#FF0000', popularity: 25 },
        { name: 'brown', color: '#AC4313', popularity: 12 },
        { name: 'gold', color: '#DAAF37', popularity: 10 },
        { name: 'green', color: '#00FF00', popularity: 16 },
        { name: 'tan', color: '#D2B48C', popularity: 12 },
        { name: 'orange', color: '#DA680F', popularity: 15 }
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

    //-------------------- Update Live Cars -------------------------------------------

    startWebSocket() {
      const url = `${websocketUrl}/live`;
      this.$store.dispatch('cav/WSConnect', { vm: this, url });
      this.$store.dispatch('cav/WSSendMessage', JSON.stringify({ data: 'Hello from frontend ...' }));
    },

    stopWebSocket() {
      if (this.socket.isConnected) {
        this.$store.dispatch('cav/WSDisconnect', { vm: this });
      }
    },

    startCleanUpTimer() {
      // Runs every minute
      this.cluenupTimerHandle = setInterval(this.cleanupLiveCars, this.cleanUpInterval);
    },

    stopCleanUpTimer() {
      if (this.cluenupTimerHandle) {
        clearInterval(this.cluenupTimerHandle);
        this.cluenupTimerHandle = null;
      }
    },

    updateLiveCars(updates) {
      const updatedLiveCars = [];
      for (const deviceId in updates) {
        const data = updates[deviceId];

        // Set update flag for future animation
        data.updated = true;
        data.index = 0;

        const car = this.liveCars.find(car => car.id.toString() === deviceId);
        if (car) {
          const updatedData = { ...car, ...data };
          updatedLiveCars.push(updatedData);
        } else {
          data.color = this.randomCarColor();
          updatedLiveCars.push(data);
        }
      }

      // Set store
      this.$store.commit('cav/SET_LIVE_CARS', updatedLiveCars);

      // Center map
      const points = updatedLiveCars.map(item => item.position);
      this.centerMap(this.map, points);

      // Start animation
      this.startAnimateCars();
    },

    cleanupLiveCars() {
      if (this.liveCars.length) {
        const updatedLiveCars = this.liveCars.filter(
          car => Utils.getSecondsTillNow(car.lastUpdated) < this.inactiveSeconds
        );
        if (updatedLiveCars.length < this.liveCars.length) {
          this.$store.commit('cav/SET_LIVE_CARS', updatedLiveCars);
        }
      }
    },

    startAnimateCars() {
      this.stopAnimateCars();
      this.animateTimerHandle = setInterval(this.animateCars, this.animateInterval);
    },

    stopAnimateCars() {
      if (this.animateTimerHandle) {
        clearInterval(this.animateTimerHandle);
        this.animateTimerHandle = null;
      }
    },

    animateCars() {
      let updatedAny = false;
      for (let i = 0; i < this.liveCars.length; i++) {
        const car = this.liveCars[i];
        if (car.updated) {
          if (car.index < car.path.length) {
            car.position = car.path[car.index];
            car.index++;
            if (car.index >= car.path.length) {
              car.updated = false;
            }
            const updatedCar = Object.assign({}, car);
            this.$store.commit('cav/UPDATE_LIVE_CAR', { vm: this, index: i, car: updatedCar });
            updatedAny = true;
          }
        }
      }

      if (!updatedAny) {
        this.stopAnimateCars();
      }
    }
  }
};
</script>
<style lang="scss" scoped>
.my-map {
  width: 101.5%;
  height: calc(100vh - 48px);
  margin-top: -12px;
  margin-left: -12px;
  margin-right: 0px;
} 
</style>

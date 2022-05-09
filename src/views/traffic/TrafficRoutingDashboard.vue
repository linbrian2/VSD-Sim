<template>
  <div class="bluetooth-dashboard">
    <BluetoothMap />
    <TrafficRouting />
    <v-tooltip left>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          v-bind="attrs"
          v-on="on"
          @click="resetMapZoom"
          fab
          small
          style="position: absolute; top: 76vh; right: 10px"
        >
          <v-icon>mdi-home-outline</v-icon>
        </v-btn>
      </template>
      <span>Reset Map</span>
    </v-tooltip>
    <v-tooltip left>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          v-bind="attrs"
          v-on="on"
          fab
          small
          style="position: absolute; top: 82vh; right: 10px"
          @click="changeZoom(1)"
        >
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </template>
      <span>Zoom in</span>
    </v-tooltip>
    <v-tooltip left>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          v-bind="attrs"
          v-on="on"
          fab
          small
          style="position: absolute; top: 88vh; right: 10px"
          @click="changeZoom(-1)"
        >
          <v-icon>mdi-minus</v-icon>
        </v-btn>
      </template>
      <span>Zoom out</span>
    </v-tooltip>
  </div>
</template>

<script>
import BluetoothMap from '@/components/modules/bluetooth/ui/BluetoothMap';
import TrafficRouting from '@/views/traffic/TrafficRouting';
import { mapState } from 'vuex';

export default {
  name: 'App',
  components: {
    BluetoothMap,
    TrafficRouting
  },
  methods: {
    resetMapZoom() {
      this.map.setCenter({ lat: 39.67, lng: -75.6575 });
      this.map.setZoom(12);
    },
    changeZoom(val) {
      let map = this.map;
      let newZoom = map.getZoom() + val;
      map.setZoom(newZoom);
    }
  },
  computed: {
    ...mapState('bluetooth', ['map'])
  }
};
</script>

<style lang="scss">
.grid-center {
  display: grid;
  place-items: center;
}
.grid-left {
  display: grid;
  place-items: start;
}
.grid-right {
  display: grid;
  place-items: end;
}
</style>

<style lang="scss">
div#content {
  color: black;
}
</style>

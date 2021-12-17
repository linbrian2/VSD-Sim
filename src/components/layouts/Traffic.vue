<template>
  <div class="traffic">
    <AppBar />
    <v-main>
      <router-view></router-view>
    </v-main>
    <BluetoothModules />
  </div>
</template>

<script>
import AppBar from '@/components/modules/traffic/app/AppBar';
import BluetoothModules from '@/components/modules/bluetooth/BluetoothModules';
import { trafficWebsocketUrl } from '@/utils/websocketUrl';

export default {
  name: 'App',
  components: {
    AppBar,
    BluetoothModules
  },

  created() {
    this.$store.commit('SET_CURRENT_DATE', new Date());
  },

  mounted() {
    setTimeout(() => {
      this.startWebSocket();
    }, 1000);
  },

  beforeDestroy() {
    this.stopWebSocket();
  },

  methods: {
    startWebSocket() {
      this.$store.dispatch('WSConnect', trafficWebsocketUrl);
    },

    stopWebSocket() {
      this.$store.dispatch('WSDisconnect');
    }
  }
};
</script>

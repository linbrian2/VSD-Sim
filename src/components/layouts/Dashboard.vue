<template>
  <div class="dashboard">
    <AppBar />
    <v-main>
      <router-view />
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/dashboard/AppBar';
import { trafficWebsocketUrl } from '@/utils/websocketUrl';

export default {
  name: 'App',
  components: {
    AppBar
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

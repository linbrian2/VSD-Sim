<template>
  <div class="cav">
    <AppBar />
    <v-main class="mt-6">
      <router-view />
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/cav/AppBar';
import { cavWebsocketUrl } from '@/utils/websocketUrl';

export default {
  name: 'App',
  components: {
    AppBar
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
      const url = `${cavWebsocketUrl}/live`;
      this.$store.dispatch('WSConnect', url);
    },

    stopWebSocket() {
      this.$store.dispatch('WSDisconnect');
    }
  }
};
</script>

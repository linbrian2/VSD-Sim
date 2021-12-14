<template>
  <div class="traffic-routing">
    <AppBar />
    <v-main>
      <router-view />
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/trafficRouting/AppBar';

export default {
  name: 'App',
  components: {
    AppBar
  }
};
</script>

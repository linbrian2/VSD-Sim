<template>
  <div class="traffic">
    <AppBar />
    <v-main>
      <router-view :key="$route.fullPath"></router-view>
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/traffic/app/AppBar';

export default {
  name: 'App',
  components: {
    AppBar
  }
};
</script>

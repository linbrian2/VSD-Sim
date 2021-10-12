<template>
  <div class="traffic">
    <AppBar />
    <v-main>
      <router-view></router-view>
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/traffic/app/AppBar';

export default {
  name: 'App',
  components: {
    AppBar
  }
};
</script>

<template>
  <div class="status">
    <AppBar />
    <v-main>
      <router-view />
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/status/AppBar';

export default {
  name: 'App',
  components: {
    AppBar
  }
};
</script>

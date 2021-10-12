<template>
  <div class="cav">
    <AppBar />
    <v-main>
      <router-view />
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/cav/AppBar';

export default {
  name: 'App',
  components: {
    AppBar
  }
};
</script>

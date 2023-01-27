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

export default {
  name: 'App',
  components: {
    AppBar
  }
};
</script>

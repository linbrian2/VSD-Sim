<template>
  <div class="status">
    <AppBar />
    <v-main>
      Main Dashboard
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

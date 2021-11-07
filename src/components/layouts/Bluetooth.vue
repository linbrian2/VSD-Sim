<template>
  <div class="bluetooth">
    <AppBar />
    <v-main>
      <router-view />
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/bluetooth/ui/AppBar';

export default {
  name: 'App',
  components: {
    AppBar
  }
};
</script>

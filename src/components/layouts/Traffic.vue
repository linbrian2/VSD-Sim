<template>
  <div class="traffic">
    <AppBar />
    <v-main>
      <router-view></router-view>
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/traffic/app/AppBar';

export default {
  name: 'App',
  components: {
    AppBar
  },
  created() {
    this.$store.commit('SET_CURRENT_DATE', new Date());
  }
};
</script>

<template>
  <div class="cav">
    <AppBar />
    <v-main>
      <router-view />
    </v-main>
    <SystemInfo />
    <LoadingProgress :loading="progressLoading" color="primary" />
  </div>
</template>

<script>
import AppBar from '@/components/cav/AppBar';
import SystemInfo from '@/components/cav/SystemInfo';
import LoadingProgress from '@/components/cav/LoadingProgress';
import { mapState } from 'vuex';

export default {
  name: 'App',
  components: {
    AppBar,
    SystemInfo,
    LoadingProgress
  },
  computed: {
    ...mapState('cav', ['progressLoading'])
  }
};
</script>
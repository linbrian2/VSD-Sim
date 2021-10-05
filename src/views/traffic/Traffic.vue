<template>
  <div class="traffic">
    <AppBar />
    <v-main>
      <router-view></router-view>
    </v-main>

    <v-snackbar middle rounded="pill" v-model="snackbar.showing" :color="snackbar.color" :timeout="snackbar.timeout">
      <div class="text-center">{{ snackbar.text }}</div>
      <!-- <v-btn text @click="snackbar.showing = false" class="float-right">X</v-btn> -->
    </v-snackbar>

    <FlowChartDialog v-model="showFlowChart" />
  </div>
</template>

<script>
import AppBar from '@/components/traffic/app/AppBar';
import FlowChartDialog from '@/components/traffic/app/FlowChartDialog';
import { mapState } from 'vuex';

export default {
  name: 'App',
  components: {
    AppBar,
    FlowChartDialog
  },

  computed: {
    icon() {
      return this.$store.state.traffic.showPanel ? 'mdi-close' : 'mdi-menu';
    },
    ...mapState('traffic', ['snackbar', 'showFlowChart'])
  },

  created() {
    window.addEventListener('keydown', this.keydownListener);
  },

  destroyed() {
    window.removeEventListener('keydown', this.keydownListener);
  },

  methods: {
    keydownListener(e) {
      if (e.key === 'ArrowLeft') {
        this.$store.dispatch('traffic/incCurrentDate', -1);
      } else if (e.key === 'ArrowRight') {
        this.$store.dispatch('traffic/incCurrentDate', 1);
      }
    }
  }
};
</script>
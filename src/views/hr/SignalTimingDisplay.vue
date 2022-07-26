<template>
  <div>
    <div v-if="signalTimingMode == 'Live'">
      <SignalTimingLive />
    </div>
    <div v-else>
      <SignalTimingPlayback />
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import SignalTimingLive from '@/views/hr/SignalTimingLive.vue';
import SignalTimingPlayback from '@/views/hr/SignalTimingPlayback.vue';

export default {
  components: {
    SignalTimingLive,
    SignalTimingPlayback
  },

  computed: {
    ...mapState('hr', ['signalTimingMode'])
  },
  mounted() {
    console.log(this.signalTimingMode);
  }
};
</script>

<style></style>

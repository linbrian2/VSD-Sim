<template>
  <div class="d-flex justify-space-between mx-2">
    <v-progress-circular :size="120" :width="2" value="100" :color="color">
      <div class="d-flex flex-column align-center">
        <h1 class="mt-10" style="font-size:35px">
          {{ time }}
        </h1>
        <div class="overline mt-2 grey--text">Time</div>
      </div>
    </v-progress-circular>
    <v-progress-circular :size="120" :width="2" value="100" :color="color">
      <div class="d-flex flex-column align-center">
        <h1 class="mt-10" style="font-size:40px">
          {{ state.speed }}
        </h1>
        <div class="overline mt-2 grey--text">MPH</div>
      </div>
    </v-progress-circular>
    <v-progress-circular :size="120" :width="2" value="100" :color="color">
      <div class="d-flex flex-column align-center">
        <h1 class="mt-10" style="font-size:38px">
          {{ state.rpm }}
        </h1>
        <div class="overline mt-2 grey--text">RPM</div>
      </div>
    </v-progress-circular>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    state: Object,
    color: {
      type: String,
      default: 'red'
    }
  },
  computed: {
    time() {
      return Utils.formatTimeAsMinute(new Date(this.state.time));
    }
  }
};
</script>

<style lang="scss" scoped></style>

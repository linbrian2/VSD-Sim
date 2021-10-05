<template>
  <div>
    <div v-if="value < 30">
      <v-chip small outlined color="green lighten-1" style="width:38px;">{{ value }}</v-chip>
    </div>
    <div v-else-if="value < 40">
      <v-chip small color="orange lighten-2" style="width:38px;">{{ value }}</v-chip>
    </div>
    <div v-else-if="value < 50">
      <v-chip small color="red lighten-1" text-color="white" style="width:38px;">{{ value }}</v-chip>
    </div>
    <div v-else>
      <v-chip small color="red darken-3" text-color="white" style="width:38px;">{{ value }}</v-chip>
    </div>
  </div>
</template>

<script>
export default {
  // below 30%: leave the background blank
  // 30~40%: semi-transparent orange
  // 40%~50%:semi-transparent Red color
  // beyond 50% show solid and flashing red background
  props: ['value']
};
</script>

<style lang="scss" scoped>
.blinking {
  animation: blinkingAnim 1.2s infinite;
}
@keyframes blinkingAnim {
  0% {
    color: #000;
  }
  49% {
    color: #000;
  }
  60% {
    color: transparent;
  }
  99% {
    color: transparent;
  }
  100% {
    color: #000;
  }
}
</style>

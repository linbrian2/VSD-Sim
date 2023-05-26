<template>
  <v-progress-linear
    :active="loading"
    :color="color"
    :indeterminate="true"
    class="ma-0"
    height="4"
    style="top: -4px;"
  ></v-progress-linear>
</template>

<style>
.v-progress-linear {
  -moz-transform: scale(1, -1);
  -webkit-transform: scale(1, -1);
  -o-transform: scale(1, -1);
  -ms-transform: scale(1, -1);
  transform: scale(1, -1);
}
</style>

<script>
export default {
  props: {
    loading: Boolean,
    color: String
  }
};
</script>

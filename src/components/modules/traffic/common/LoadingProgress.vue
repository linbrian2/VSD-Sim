<template>
  <v-layout row justify-center>
    <v-dialog v-model="loading" persistent fullscreen content-class="loading-dialog" transition="">
      <v-container fill-height>
        <v-layout row justify-center align-center>
          <v-progress-circular indeterminate :size="100" :width="7" :color="color"></v-progress-circular>
        </v-layout>
      </v-container>
    </v-dialog>
  </v-layout>
</template>

<script>
export default {
  props: {
    loading: Boolean,
    color: String
  }
};
</script>

<style lang="scss" scoped>
.loading-dialog {
  background-color: transparent;
}
</style>

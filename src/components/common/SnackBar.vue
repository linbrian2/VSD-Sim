<template>
  <v-snackbar right v-model="snackbar.showing" :color="snackbar.color" :timeout="snackbar.timeout">
    <div class="text-center">{{ snackbar.text }}</div>
  </v-snackbar>
</template>

<script>
import { mapState } from 'vuex';
export default {
  computed: {
    ...mapState(['snackbar'])
  }
};
</script>

<style scoped>
.v-snack__wrapper {
  max-width: none;
}
</style>

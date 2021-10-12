<template>
  <v-snackbar middle rounded="pill" v-model="snackbar.showing" :color="snackbar.color" :timeout="snackbar.timeout">
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

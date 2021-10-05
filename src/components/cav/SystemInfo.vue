<template>
  <v-snackbar v-model="snackbar.showing" :color="snackbar.color" :timeout="snackbar.timeout">
    {{ snackbar.text }}
    <v-btn text @click="snackbar.showing = false" class="float-right">Close</v-btn>
  </v-snackbar>
</template>

<script>
import { mapState } from 'vuex';
export default {
  computed: {
    ...mapState('cav', ['snackbar'])
  }
};
</script>

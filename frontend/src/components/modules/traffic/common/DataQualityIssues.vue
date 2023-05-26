<template>
  <v-card tile elevation="24" v-if="show">
    <v-alert dense outlined elevation="2" type="error">
      <div class="d-flex align-center">
        <h4 class="mx-0 overline">Data contains errors!</h4>
        <v-chip-group active-class="primary--text" column class="ml-2">
          <v-chip
            small
            v-for="{ color, value, percent, description } in validErrors"
            :key="value"
            :color="color"
            class="black--text mr-3"
          >
            {{ description }} ({{ percent }}%)
          </v-chip>
        </v-chip-group>
      </div>
    </v-alert>
  </v-card>
</template>

<script>
import ErrorTypes from '@/utils/constants/status';
export default {
  props: {
    quality: Object,
    threshold: {
      type: Number,
      default: 10
    }
  },

  computed: {
    show() {
      return this.quality.total > this.threshold;
    },

    validErrors() {
      const result = this.quality.errors
        .map((item, index) => ({ index, count: item }))
        .filter(item => item.count > 0)
        .map(item => ({ percent: item.count, ...ErrorTypes.ERROR_COLOR_TYPES[item.index] }));
      return result;
    }
  }
};
</script>

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
        .map(item => ({ percent: item.count, ...this.sensorErrorTypes[item.index] }));
      return result;
    }
  },

  data: () => ({
    sensorErrorTypes: [
      { value: 0, color: '#90ed7d', description: 'No error' },
      { value: 1, color: '#434348', description: 'Missing data' },
      { value: 2, color: '#f7a35c', description: 'Invalid data' },
      { value: 3, color: '#8085e9', description: 'VOS exceeding threshold' },
      { value: 4, color: '#f15c80', description: 'VOS consecutive constant' },
      { value: 5, color: '#e4d354', description: 'VOS inconsistency' },
      { value: 6, color: '#2b908f', description: 'Volume aggregation issue' },
      { value: 7, color: '#f45b5b', description: 'Timezone mismatch' }
    ]
  })
};
</script>

<template>
  <v-timeline dense>
    <v-timeline-item v-for="(item, idx) in items" :key="idx" :color="item.color" small>
      <v-row align="center">
        <v-col v-if="!isSimplified" cols="2">
          <div class="overline ml-n4">{{ item.time }}</div>
        </v-col>
        <v-col>
          <v-chip v-if="!isSimplified" label :color="item.color" class="font-weight-light">
            <v-icon small left v-text="item.icon" />
            {{ item.event }}
          </v-chip>
          <v-card v-else :color="item.color" class="font-weight-light px-2 py-1 mr-3">
            <v-icon small left v-text="item.icon" />
            {{ item.time }} - {{ item.event }}
          </v-card>
        </v-col>
      </v-row>
    </v-timeline-item>
  </v-timeline>
</template>

<script>
export default {
  props: {
    items: Array,
    simple: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    isSimplified() {
      return this.$vuetify.breakpoint.mobile || this.simple;
    }
  }
};
</script>

<template>
  <v-timeline dense>
    <v-timeline-item v-for="(item, idx) in items" :key="idx" :color="item.color" :icon="item.icon" fill-dot>
      <v-row align="center">
        <v-col cols="2">
          <strong>{{ item.time }}</strong>
        </v-col>
        <v-col>
          <v-alert dense :color="item.color" class="white--text" max-width="500">
            <strong>{{ item.event }}</strong>
          </v-alert>
        </v-col>
      </v-row>
    </v-timeline-item>
  </v-timeline>
</template>

<script>
export default {
  props: {
    items: Array
  }
};
</script>

<template>
  <v-timeline dense>
    <v-timeline-item v-for="(item, idx) in items" :key="idx" :color="item.color" fill-dot small>
      <v-row align="center">
        <v-col :cols="$vuetify.breakpoint.mobile ? 12 : 2">
          <strong>{{ item.time }}</strong>
        </v-col>
        <v-col>
          <v-chip label :color="item.color" class="font-weight-light">
            <v-icon small left v-text="item.icon" />
            {{ item.event }}
          </v-chip>
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

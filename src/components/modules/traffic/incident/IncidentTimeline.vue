<template>
  <v-timeline dense>
    <v-timeline-item v-for="(item, idx) in items" :key="idx" :color="item.color" fill-dot small>
      <v-row align="center">
        <v-col v-if="!$vuetify.breakpoint.mobile" cols="2">
          <strong>{{ item.time }}</strong>
        </v-col>
        <v-col>
          <v-chip v-if="!$vuetify.breakpoint.mobile" label :color="item.color" class="font-weight-light">
            <v-icon small left v-text="item.icon" />
            {{ item.event }}
          </v-chip>
          <v-card v-else :color="item.color" class="font-weight-light px-2 py-1 mr-3">
            <v-icon small left v-text="item.icon" />
            {{ item.time }} -
            {{ item.event }}
          </v-card>
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

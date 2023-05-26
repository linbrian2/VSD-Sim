<template>
  <v-card :color="color" dark>
    <v-card-title class="d-flex justify-space-between">
      <div>{{ alert.type }}</div>
      <div class="caption grey--text">{{ alert.dist }} meters</div>
    </v-card-title>

    <v-card-text>
      <div>{{ alert.details }}</div>
    </v-card-text>

    <v-card-actions class="mt-n3">
      <v-list-item class="grow">
        <v-list-item-content class="ml-n2">
          <v-list-item-title>{{ alert.updatedAt | dateAndTime }}</v-list-item-title>
        </v-list-item-content>

        <v-row align="center" justify="end">
          <div>{{ alert.status }}</div>
        </v-row>
      </v-list-item>
    </v-card-actions>
  </v-card>
</template>
<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    alert: Object,
    color: {
      type: String,
      default: '#385F73'
    }
  },

  filters: {
    dateAndTime(t) {
      const d = new Date(t);
      let dd = Utils.formatDate(d);
      let tt = Utils.formatTimeAsMinute(d);
      return dd + ' ' + tt;
    }
  }
};
</script>

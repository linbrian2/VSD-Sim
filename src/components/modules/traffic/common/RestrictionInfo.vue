<template>
  <v-card tile :color="color" dark>
    <v-card-title class="d-flex justify-space-between my-n3">
      <div>
        <strong>{{ restriction.name }} </strong>
      </div>
      <div class="d-flex">
        <v-tooltip bottom max-width="350" color="orange">
          <template v-slot:activator="{ on, attrs }">
            <v-icon small class="mr-1" v-bind="attrs" v-on="on">
              mdi-map-marker
            </v-icon>
          </template>
          <span>{{ restriction.loc }}</span>
        </v-tooltip>
        <div class="caption">{{ restriction.dist }}</div>
      </div>
    </v-card-title>
    <v-card-text class="font-weight-light">
      <div class="pre-formatted" v-html="restriction.desc" />
    </v-card-text>

    <v-card-actions class="mt-n3 mx-2 d-flex justify-space-between">
      <div class="grey--text overline" v-if="restriction.time">Created at {{ restriction.time | dateAndTime }}</div>
      <div class="grey--text caption">#{{ restriction.id }}</div>
    </v-card-actions>
  </v-card>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    restriction: Object,
    color: {
      type: String,
      default: '#385F73'
    }
  },

  filters: {
    dateAndTime(t) {
      const d = new Date(t);
      return Utils.formatDateTime(d);
    }
  }
};
</script>

<style lang="scss" scoped>
.pre-formatted {
  word-wrap: break-word;
  white-space: pre-wrap;
  -moz-white-space: pre-wrap;
}
</style>

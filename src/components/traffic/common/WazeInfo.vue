<template>
  <v-card tile :color="color" dark>
    <v-card-title class="d-flex justify-space-between">
      <div>{{ waze.type }}</div>
      <div class="caption grey--text">{{ waze.dist }}</div>
    </v-card-title>

    <v-card-text>
      <v-row align="center" class="mx-0 mt-n3">
        <v-rating :value="waze.reportRating" color="amber" dense half-increments readonly size="12"></v-rating>
      </v-row>

      <div>{{ waze.desc }}</div>
    </v-card-text>

    <v-card-actions class="mt-n3">
      <v-list-item class="grow">
        <v-list-item-content class="ml-n2">
          <v-list-item-title>{{ waze.time }}</v-list-item-title>
        </v-list-item-content>

        <v-row align="center" justify="end">
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-icon small color="white" class="mr-1" v-bind="attrs" v-on="on">
                mdi-account-multiple-check
              </v-icon>
            </template>
            <span>Confidence</span>
          </v-tooltip>
          <span class="subheading mr-3">{{ waze.confidence }}</span>

          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-icon small color="white" class="mr-1" v-bind="attrs" v-on="on">
                mdi-shield-check-outline
              </v-icon>
            </template>
            <span>Reliability</span>
          </v-tooltip>
          <span class="subheading mr-3">{{ waze.reliability }}</span>

          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-icon small color="white" class="mr-1" v-bind="attrs" v-on="on">
                mdi-thumb-up-outline
              </v-icon>
            </template>
            <span>Thumbs Up</span>
          </v-tooltip>
          <span class="subheading">{{ waze.thumbsup }}</span>
        </v-row>
      </v-list-item>
    </v-card-actions>
  </v-card>
</template>

<script>
export default {
  props: {
    waze: Object,
    color: {
      type: String,
      default: '#385F73'
    }
  }
};
</script>

<template>
  <v-sheet outlined :color="borderColor">
    <v-card tile :color="color" dark @click="handleClick">
      <v-card-title class="d-flex justify-space-between">
        <div class="d-flex">
          <v-img :src="getIcon(waze)" width="24" height="24" class="mt-1 mr-2" v-if="waze.subType" />
          <div>{{ waze.type }}</div>
        </div>

        <div class="caption grey--text" v-if="waze.dist">{{ waze.dist }}</div>
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
  </v-sheet>
</template>

<script>
import { mapIcons } from '@/mixins/mapIcons';
export default {
  mixins: [mapIcons],
  props: {
    waze: Object,
    color: {
      type: String,
      default: '#385F73'
    }
  },

  computed: {
    borderColor() {
      return this.waze.selected ? 'yellow' : '';
    }
  },

  methods: {
    handleClick() {
      this.waze.selected = true;
      this.$emit('click', this.waze);
    },

    getIcon(waze) {
      return this.getWazeIcon(waze).url;
    }
  }
};
</script>

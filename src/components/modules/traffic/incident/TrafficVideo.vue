<template>
  <v-card tile @click.stop="clicked">
    <v-card-title class="d-flex justify-space-between">
      <div class="body-1">{{ video.camera }}</div>
      <div class="caption grey--text">{{ video.time | dateAndTime }}</div>
    </v-card-title>

    <v-card-text>
      <v-img :src="video.image" :lazy-src="video.image" aspect-ratio="1.78" class="grey lighten-2">
        <template v-slot:placeholder>
          <v-row class="fill-height ma-0" align="center" justify="center">
            <v-progress-circular indeterminate color="grey"></v-progress-circular>
          </v-row>
        </template>
      </v-img>
    </v-card-text>
  </v-card>
</template>
<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    video: Object
  },

  filters: {
    dateAndTime(t) {
      const d = new Date(t);
      let dd = Utils.formatDate(d);
      let tt = Utils.formatTimeAsMinute(d);
      return dd + ' ' + tt;
    }
  },

  methods: {
    clicked() {
      this.$emit('video-click', this.video);
    }
  }
};
</script>

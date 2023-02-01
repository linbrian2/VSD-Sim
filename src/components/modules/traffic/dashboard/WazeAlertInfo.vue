<template>
  <v-container>
    <WazeInfo :waze="info" />
  </v-container>
</template>

<script>
import Utils from '@/utils/Utils';
import WazeInfo from '@/components/modules/traffic/common/WazeInfo';
export default {
  props: {
    waze: Object
  },

  components: {
    WazeInfo
  },

  computed: {
    info() {
      return this.formatDate(this.waze);
    }
  },

  methods: {
    formatDate(waze) {
      if (!waze) {
        return null;
      }

      return {
        id: waze.id,
        time: Utils.formatAMPMTime(new Date(waze.alertTimeTS)),
        type: waze.alertType.name,
        desc: waze.description,
        position: waze.position,
        reportRating: waze.reportRating,
        confidence: waze.confidence,
        reliability: waze.reliability,
        thumbsup: waze.thumbsUp
      };
    }
  }
};
</script>

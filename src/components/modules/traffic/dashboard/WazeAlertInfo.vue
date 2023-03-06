<template>
  <v-container>
    <WazeInfo :waze="info" />
    <NearbyCameras :cameraIds="cameraIds" v-if="camerasAvaliable" />
  </v-container>
</template>

<script>
import Utils from '@/utils/Utils';
import WazeInfo from '@/components/modules/traffic/common/WazeInfo';
import NearbyCameras from '@/components/modules/traffic/common/NearbyCameras';

export default {
  props: {
    waze: Object
  },

  components: {
    WazeInfo,
    NearbyCameras
  },

  computed: {
    info() {
      return this.formatDate(this.waze);
    },

    cameraIds() {
      return this.waze.cameras ? this.waze.cameras : [];
    },

    camerasAvaliable() {
      return !Utils.isEmpty(this.cameraIds);
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
        thumbsup: waze.thumbsUp,
        subType: waze.subType
      };
    }
  }
};
</script>

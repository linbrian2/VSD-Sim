<template>
  <v-container>
    <RestrictionInfo :restriction="info" />
    <NearbyCameras :cameraIds="cameraIds" v-if="camerasAvaliable" />
  </v-container>
</template>

<script>
import Utils from '@/utils/Utils';
import RestrictionInfo from '@/components/modules/traffic/common/RestrictionInfo';
import NearbyCameras from '@/components/modules/traffic/common/NearbyCameras';

export default {
  props: {
    restriction: Object
  },

  components: {
    RestrictionInfo,
    NearbyCameras
  },

  computed: {
    info() {
      return this.formatDate(this.restriction);
    },

    cameraIds() {
      return this.restriction.cameras ? this.restriction.cameras : [];
    },

    camerasAvaliable() {
      return !Utils.isEmpty(this.cameraIds);
    }
  },

  methods: {
    formatDate(res) {
      if (!res) {
        return null;
      }

      return {
        id: res.restrictionId,
        name: res.impactType,
        time: res.time,
        dist: this.formatDistance(res.dist),
        loc: res.location,
        position: res.position,
        desc: res.description
      };
    },

    formatDistance(dist) {
      if (!dist) {
        return '';
      }
      const value = (dist / 1609).toFixed(2);
      return `${value} miles`;
    }
  }
};
</script>

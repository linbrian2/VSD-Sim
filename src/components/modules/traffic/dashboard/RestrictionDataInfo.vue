<template>
  <v-container>
    <RestrictionInfo :restriction="info" />
  </v-container>
</template>

<script>
import RestrictionInfo from '@/components/modules/traffic/common/RestrictionInfo';
export default {
  props: {
    restriction: Object
  },

  components: {
    RestrictionInfo
  },

  computed: {
    info() {
      return this.formatDate(this.restriction);
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

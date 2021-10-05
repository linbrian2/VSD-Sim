<template>
  <div>
    <GmapCustomMarker
      alignment="bottomright"
      v-for="(s, index) in segments"
      :key="index + 100"
      :offsetX="15"
      :offsetY="-5"
      :marker="getSegmentCenter(s.path)"
    >
      <h2 style="color:orange">{{ formatDisplay(s.travelTime) }}</h2>
    </GmapCustomMarker>
  </div>
</template>

<script>
import GmapCustomMarker from 'vue2-gmap-custom-marker';
export default {
  props: ['segments'],
  components: {
    GmapCustomMarker
  },
  methods: {
    formatDisplay(seconds) {
      if (!seconds) {
        return '';
      }
      let minutes = seconds / 60;
      return `${minutes.toFixed(1)} min`;
    },

    getSegmentCenter(path) {
      return path[Math.round(path.length / 4)];
    }
  }
};
</script>

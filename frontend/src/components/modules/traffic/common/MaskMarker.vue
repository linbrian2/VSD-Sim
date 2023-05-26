<template>
  <GmapMarker :position="position" :icon="maskIcon" :options="maskMarkerOptions" />
</template>

<script>
export default {
  props: {
    position: { type: Object, default: () => ({ lat: 0, lng: 0 }) }
  },
  data: () => ({
    maskMarkerOptions: {
      optimized: false,
      zIndex: 101
    },

    maskIcon: {
      path: 'M18 4H6c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2z',
      scale: 2.0,
      fillColor: '#FFFFFF',
      fillOpacity: 0.0,
      strokeWeight: 2.8,
      strokeColor: '#FFFF55',
      strokeOpacity: 1.0,
      anchor: { x: 12, y: 12 }
    }
  })
};
</script>

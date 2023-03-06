<template>
  <v-row>
    <v-col cols="12">
      <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Nearby Cameras</h3></v-subheader>
      <v-divider />
    </v-col>
    <v-col cols="12">
      <div class="mx-4">
        <v-btn outlined v-for="camera in cameraIds" :key="camera.id" @click.stop="playVideo(camera.id)" class="mr-5">
          <v-icon left color="warning">
            mdi-camera
          </v-icon>
          {{ camera.id }}
        </v-btn>
      </div>
    </v-col>
  </v-row>
</template>

<script>
export default {
  props: {
    cameraIds: Array
  },
  methods: {
    playVideo(id) {
      this.$bus.$emit('PLAY_POPUP_VIDEO', id);
    }
  }
};
</script>

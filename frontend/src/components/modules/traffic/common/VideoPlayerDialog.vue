<template>
  <div>
    <v-row justify="center">
      <v-dialog v-if="show" v-model="show" persistent hide-overlay max-width="1000px">
        <v-card>
          <v-toolbar dense flat fixed overflow extension-height="0">
            <v-toolbar-title>{{ title }}</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-toolbar-items class="ml-6">
              <v-btn icon @click="show = false"><v-icon>mdi-close</v-icon></v-btn>
            </v-toolbar-items>
          </v-toolbar>

          <div class="mx-auto">
            <VideoPlayer ref="videoPlayer" :options="playerOptions" :caption="caption" :icon="icon" />
          </div>
        </v-card>
      </v-dialog>
    </v-row>
  </div>
</template>

<script>
import VideoPlayer from '@/components/modules/traffic/common/VideoPlayer';

// IMPORTANT: The v-if flag in the v-dialog is very important to make sure every time when the dialog is closed
// the dialog got destroyed. This will make sure the video player instance was destroyed as well so that it closes the video
// connection to the video server, otherwise the video streaming will continue
export default {
  components: {
    VideoPlayer
  },
  props: {
    value: Boolean,
    videoType: {
      type: String,
      defualt: 'video/mp4'
    }
  },
  data: () => ({
    url: '',
    title: 'Video Player',
    caption: '',
    icon: ''
  }),
  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },
    playerOptions() {
      return {
        // videojs options
        muted: true,
        language: 'en',
        playbackRates: [0.5, 1.0, 2.0, 3.0],
        controlBar: {
          fullscreenToggle: true,
          playbackRateMenuButton: true,
          pictureInPictureToggle: false,
          volumePanel: false
        },
        sources: [
          {
            withCredentials: false,
            type: this.videoType,
            src: this.url
          }
        ],
        poster: require('@/assets/loading.gif')
      };
    }
  },

  mounted() {
    this.changeVideoSource(this.url);
  },

  methods: {
    changeVideoSource(url) {
      this.url = url;
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.setUrl(url);
      }
    },

    changeTitle(title) {
      this.title = title;
    },

    changeCaption(caption) {
      this.caption = caption;
    },

    changeIcon(icon) {
      this.icon = icon;
    },

    stopVideo() {
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.destroy();
      }
    }
  }
};
</script>

<style scoped>
.v-toolbar__extension {
  padding: 0px !important;
}
</style>

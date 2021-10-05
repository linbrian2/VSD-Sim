<template>
  <div>
    <video
      ref="videoPlayer"
      class="video-js vjs-default-skin vjs-16-9 vjs-big-play-centered"
      width="auto"
      height="auto"
      preload="auto"
      controls="controls"
      autoplay="autoplay"
    ></video>
  </div>
</template>

<script>
import 'video.js/dist/video-js.css';
// import videojs from 'video.js';
import videojs from 'video.js/dist/alt/video.novtt.js';

export default {
  name: 'VideoPlayer',
  props: {
    options: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  data() {
    return {
      player: null
    };
  },

  mounted() {
    this.init();
  },

  beforeDestroy() {
    this.destroy();
  },

  methods: {
    init() {
      if (!this.player) {
        this.player = videojs(this.$refs.videoPlayer, this.options, function onPlayerReady() {});
      }
    },

    setUrl(url) {
      if (this.player != null) {
        this.player.src(url);
      }
    },

    destroy() {
      if (this.player) {
        this.player.dispose();
        this.player = null;
      }
    }
  }
};
</script>

<style scoped>
.video {
  display: block;
  width: auto;
  margin: 0 auto;
}
</style>

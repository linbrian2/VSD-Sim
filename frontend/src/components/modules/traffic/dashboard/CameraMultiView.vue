<template>
  <div>
    <v-row>
      <v-col :cols="12" v-if="cameraFeeds.length === 0">
        <p class="mx-10 mt-4">
          <v-icon color="yellow" class="mt-n2 mr-2">mdi-lightbulb-on-outline</v-icon>
          Click camera markers on the map to add add camera views. You can add at most 6 cameras.
        </p>
        <v-btn rounded outlined class="ml-12" @click="showCameraIcons" v-if="!isCameraMarkerVisible">
          <v-icon small left>mdi-camera</v-icon>Show camera markers
        </v-btn>
      </v-col>

      <v-col :cols="cols" v-for="feed in cameraFeeds" :key="feed.id" v-else>
        <v-card class="mx-2">
          <v-app-bar height="32">
            <v-icon small class="mr-1">mdi-camera</v-icon>
            <v-toolbar-title class="overline">{{ feed.id }} </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon @click="removeCamera(feed.id)">
              <v-icon small>mdi-close</v-icon>
            </v-btn>
          </v-app-bar>
          <VideoPlayer ref="liveVideoPlayer" :options="feed.liveOptions" />
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { getVideoUrl } from '@/utils/DeldotVideoUrl';
import { mapState } from 'vuex';
import Constants from '@/utils/constants/traffic';
import VideoPlayer from '@/components/modules/vision/VideoPlayer';

export default {
  components: {
    VideoPlayer
  },

  data: () => ({
    maxCameras: 6,
    cameraFeeds: []
  }),

  computed: {
    cols() {
      return this.cameraFeeds.length <= 2 ? 12 : 6;
    },

    isCameraMarkerVisible() {
      const entry = this.mapLayersSelection.find(m => m === Constants.LAYER_DEVICE_CAMERAS);
      return entry !== undefined;
    },

    ...mapState('traffic', ['mapLayersSelection'])
  },

  mounted() {
    this.$bus.$on('ADD_CAMERA_TO_VIEW', id => {
      this.addCamera(id);
    });
  },

  destroyed() {
    console.log('CameraMultiView destroyed');
  },

  methods: {
    addCamera(id) {
      if (this.cameraFeeds.length < this.maxCameras) {
        const url = getVideoUrl(id);
        if (url) {
          const foundIndex = this.cameraFeeds.findIndex(c => c.id === id);
          if (foundIndex === -1) {
            const liveOptions = this.createLiveVideoFeed(url);
            this.cameraFeeds.push({ id, liveOptions });
          }
        }
      }
    },

    removeCamera(id) {
      const index = this.cameraFeeds.findIndex(c => c.id === id);
      if (index >= 0 && index < this.cameraFeeds.length) {
        this.cameraFeeds.splice(index, 1);
      }
    },

    createLiveVideoFeed(url) {
      return {
        muted: true,
        language: 'en',
        playbackRates: [1.0],
        controlBar: {
          playToggle: false,
          remainingTimeDisplay: false,
          fullscreenToggle: true,
          playbackRateMenuButton: false,
          pictureInPictureToggle: false,
          volumePanel: false
        },
        sources: [
          {
            withCredentials: false,
            type: 'application/x-mpegURL',
            src: url
          }
        ],
        poster: require('@/assets/loading.gif')
      };
    },

    changeLiveVideoSource(url) {
      if (this.$refs.liveVideoPlayer) {
        this.$refs.liveVideoPlayer.setUrl(url);
      }
    },

    showCameraIcons() {
      if (!this.isCameraMarkerVisible) {
        const layers = this.mapLayersSelection;
        layers.push(Constants.LAYER_DEVICE_CAMERAS);
        layers.sort();
        this.$store.dispatch('traffic/saveMapLayers', layers);
      }
    }
  }
};
</script>

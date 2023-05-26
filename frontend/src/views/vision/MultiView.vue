<template>
  <div>
    <TitleBar :loading="loading" :refresh="refreshData"> </TitleBar>
    <div>
      <v-row>
        <v-col :cols="$vuetify.breakpoint.mobile ? 12 : 6" v-for="(feed, i) in cameraFeeds" :key="i">
          <v-card class="mx-2">
            <v-app-bar height="32">
              <v-icon small class="mr-1">mdi-camera</v-icon>
              <v-toolbar-title class="overline">{{ feed.title }} </v-toolbar-title>
              <v-spacer></v-spacer>
              <v-btn icon @click="removeCamera(feed.title)">
                <v-icon small>mdi-close</v-icon>
              </v-btn>
            </v-app-bar>
            <VideoPlayer ref="liveVideoPlayer" :options="feed.liveOptions" />
          </v-card>
        </v-col>
      </v-row>
    </div>
  </div>
</template>

<script>
import Api from '@/utils/api/vision';
import { mapState } from 'vuex';
import TitleBar from '@/components/modules/vision/TitleBar';
import VideoPlayer from '@/components/modules/vision/VideoPlayer';

export default {
  components: {
    TitleBar,
    VideoPlayer
  },
  data: () => ({
    loading: false,
    liveUrl: null,
    cameraFeeds: []
  }),

  computed: {
    ...mapState('vision', ['activeMarker'])
  },

  mounted() {
    this.fetchData();
  },

  watch: {
    activeMarker(marker) {
      if (marker != null) {
        console.log('marker=', marker);
        this.fetchData();
      }
    },

    currentDate() {
      this.fetchData();
    }
  },

  methods: {
    refreshData() {
      this.fetchData();
    },

    fetchData() {
      let camera = this.activeMarker;
      if (camera != null) {
        const { id, type } = camera;
        this.fetchDeviceInfo(id, type);
      }
    },

    removeCamera(title) {
      const index = this.cameraFeeds.findIndex(c => c.title === title);
      console.log(title, index);
      if (index >= 0 && index < this.cameraFeeds.length) {
        this.cameraFeeds.splice(index, 1);
        this.cameraFeeds.forEach(c => {
          console.log(c.title);
        });
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

    async fetchDeviceInfo(id, type) {
      this.loading = true;
      try {
        const response = await Api.fetchInfo(id, type);
        if (response.data.status === 'OK') {
          const info = response.data.data;
          const title = info.title;
          const foundIndex = this.cameraFeeds.findIndex(c => c.title === title);
          if (foundIndex === -1) {
            const liveOptions = this.createLiveVideoFeed(info.uri);
            this.cameraFeeds.push({ title, liveOptions });
          }
        } else {
          this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    changeLiveVideoSource(url) {
      if (this.$refs.liveVideoPlayer) {
        this.$refs.liveVideoPlayer.setUrl(url);
      }
    }
  }
};
</script>

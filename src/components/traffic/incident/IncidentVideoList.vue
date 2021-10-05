<template>
  <div v-if="filteredCameras.length > 0">
    <SlideButtons
      class="mb-4"
      :items="filteredCameras"
      :title="cameraText"
      :callback="cameraClicked"
      :allowExpand="false"
      ref="slideButtons"
    />

    <div class="mt-4" v-if="filteredVideos.length > 0">
      <v-row>
        <v-col cols="4" v-for="(video, i) in filteredVideos" :key="i">
          <TrafficVideo :video="video" @video-click="playVideo" />
        </v-col>
      </v-row>
    </div>
    <VideoPlayerDialog ref="vpRef" v-model="showVideoPlayer" />
  </div>
</template>

<script>
import SlideButtons from '@/components/traffic/incident/SlideButtons';
import TrafficVideo from '@/components/traffic/incident/TrafficVideo';
import VideoPlayerDialog from '@/components/traffic/common/VideoPlayerDialog';

export default {
  props: {
    items: Array,
    cameras: Array,
    selectedSegmentId: null
  },

  components: {
    SlideButtons,
    TrafficVideo,
    VideoPlayerDialog
  },

  data: () => ({
    showVideoPlayer: false,
    selectedCamera: null
  }),

  computed: {
    filteredVideos() {
      if (this.selectedCamera) {
        return this.items.filter(item => item.camera === this.selectedCamera.name);
      } else {
        return this.items;
      }
    },

    filteredCameras() {
      if (this.selectedSegmentId === null) {
        return this.cameras;
      } else {
        return this.cameras.filter(item => item.linkId === this.selectedSegmentId);
      }
    },

    cameraText() {
      const videos = this.filteredVideos;
      return videos ? `${videos.length} Incident Video${videos.length > 1 ? 's' : ''}` : '';
    }
  },

  watch: {
    filteredCameras(cameras) {
      this.selectFirst(cameras);
    }
  },

  mounted() {
    this.selectFirst(this.filteredCameras);
  },

  methods: {
    selectFirst(cameras) {
      const firstItem = cameras.length > 0 ? cameras[0] : null;
      if (firstItem) {
        this.cameraClicked(firstItem);
      }
    },

    selectItem(itemName) {
      const item = this.filteredCameras.find(item => item.name === itemName);
      if (item) {
        this.cameraClicked(item);
        this.$refs.slideButtons.selectItem(itemName);
      }
    },

    cameraClicked(item) {
      this.selectedCamera = item;
      this.$bus.$emit('INCIDENT_ITEM_SELECTED', item);
    },

    playVideo(url) {
      if (url) {
        if (this.$refs.vpRef) {
          this.$refs.vpRef.changeVideoSource(url);
        }
        this.showVideoPlayer = true;
      }
    }
  }
};
</script>

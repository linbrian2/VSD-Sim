<template>
  <div v-if="filteredCameras.length > 0">
    <SlideButtons
      class="mb-4"
      :items="filteredCameras"
      :title="cameraText"
      :callback="cameraClicked"
      :allowExpand="false"
      ref="slideButtons"
    >
      <v-btn outlined small @click.stop="playLiveVideo()" class="mr-5">
        <v-icon left small color="warning">
          mdi-camera
        </v-icon>
        Live
      </v-btn>
    </SlideButtons>

    <div class="mt-4" v-if="filteredVideos.length > 0">
      <v-row>
        <v-col :cols="$vuetify.breakpoint.mobile ? 12 : 4" v-for="(video, i) in filteredVideos" :key="i">
          <TrafficVideo :video="video" @video-click="playVideo" />
        </v-col>
      </v-row>
    </div>
    <VideoPlayerDialog ref="vpRef" v-model="showVideoPlayer" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import SlideButtons from '@/components/modules/traffic/incident/SlideButtons';
import TrafficVideo from '@/components/modules/traffic/incident/TrafficVideo';
import VideoPlayerDialog from '@/components/modules/traffic/common/VideoPlayerDialog';
import { getVideoUrl } from '@/utils/DeldotVideoUrl';

export default {
  props: {
    items: Array,
    cameras: Array,
    selectedSegmentId: null,
    selectedFilter: null
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
        if (!this.selectedFilter || this.selectedFilter < 0) {
          return this.items.filter(item => item.camera === this.selectedCamera.name);
        } else {
          return this.items.filter(
            item => item.camera === this.selectedCamera.name && item.classification === this.selectedFilter - 1
          );
        }
      } else {
        if (!this.selectedFilter || this.selectedFilter < 0) {
          return this.items;
        } else {
          return this.items.filter(item => item.classification === this.selectedFilter - 1);
        }
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

    dateAndTime(t) {
      const d = new Date(t);
      let dd = Utils.formatDate(d);
      let tt = Utils.formatTimeAsMinute(d);
      return dd + ' ' + tt;
    },

    playVideo(video) {
      const url = video.video;
      if (url) {
        if (this.$refs.vpRef) {
          this.$refs.vpRef.changeVideoSource(url);
          this.$refs.vpRef.changeTitle(video.camera);
          this.$refs.vpRef.changeCaption(this.dateAndTime(video.time));
          this.$refs.vpRef.changeIcon(video.classification ? 'mdi-car-emergency' : '');
        }
        this.showVideoPlayer = true;
      }
    },

    playLiveVideo() {
      if (this.selectedCamera) {
        const cameraId = this.selectedCamera.name;
        const url = getVideoUrl(cameraId);
        if (this.$refs.vpRef) {
          this.$refs.vpRef.changeVideoSource(url);
          this.$refs.vpRef.changeTitle(cameraId);
          this.$refs.vpRef.changeCaption('LIVE');
          this.$refs.vpRef.changeIcon('mdi-video');
        }
        this.showVideoPlayer = true;
      }
    }
  }
};
</script>

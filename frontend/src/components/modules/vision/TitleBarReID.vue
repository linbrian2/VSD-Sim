<template>
  <div class="title-name">
    <v-row wrap no-gutters>
      <v-col lg="4" sm="6" xs="12">
        <div>
          <span>{{ loading ? 'Loading ...' : activeTitle }}</span>
          <span class="ml-2" v-if="title === undefined && !loading">
            <v-chip class="mt-n1 white--text" color="green" outlined x-small>
              <span class="white--text">{{ activeSignal.id }}</span>
            </v-chip>
          </span>
        </div>
      </v-col>
      <v-col lg="5" sm="3" xs="12">
        <span><slot></slot></span>
      </v-col>
      <v-col lg="3" sm="3" xs="12">
        <v-btn small icon @click.stop="refreshData()" class="title-btn float-right" :loading="loading">
          <v-icon color="white">mdi-refresh</v-icon>
        </v-btn>
        <v-btn
          small
          icon
          v-if="!$store.state.vision.showReIDVideos"
          @click="$store.state.vision.showReIDVideos = true"
          class="title-btn float-right"
          :disabled="!hasSnippetInfo"
        >
          <v-icon color="white"> mdi-video-off </v-icon>
        </v-btn>
        <v-btn
          small
          icon
          v-if="$store.state.vision.showReIDVideos"
          @click="$store.state.vision.showReIDVideos = false"
          class="title-btn float-right"
          :disabled="!hasSnippetInfo"
        >
          <v-icon color="white"> mdi-video </v-icon>
        </v-btn>
        <v-btn
          small
          icon
          @click="$store.state.vision.dialogMetadata = true"
          class="title-btn float-right"
          :disabled="!hasSnippetInfo"
        >
          <v-icon color="white"> mdi-history </v-icon>
        </v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapState } from 'vuex';
export default {
  props: ['title', 'loading', 'refresh'],
  computed: {
    hasSnippetInfo() {
      return this.$store.state.vision.snippetInfo;
    },
    activeTitle() {
      if (this.title !== undefined) {
        return this.title;
      } else {
        return this.activeSignal.name;
      }
    },
    activeSignal() {
      return (
        this.currentSignal || {
          id: 6,
          name: 'No Signal',
          phases: [],
          detectors: [],
          plans: []
        }
      );
    },
    ...mapState(['currentSignal', 'currentDate'])
  },
  methods: {
    refreshData() {
      this.refresh();
    }
  }
};
</script>

<style lang="scss" scoped>
.title-name {
  background-color: hsl(200, 18%, 46%);
  padding: 8px 10px;
  margin-top: 0px;
  color: white;
  font-size: large;
  font-weight: bold;
}
.title-id {
  color: #888;
  font-size: small;
  font-weight: normal;
  margin-bottom: 30px;
}
.title-btn {
  margin-left: 5px;
  display: inline-block;
}
</style>

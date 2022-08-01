<template>
  <div>
    <TitleBarReID title="Vehicle Re-ID" :loading="loading" :refresh="refreshData" />
    <!-- <TitleBar :title="title" :loading="loading" :refresh="refreshData" /> -->
    <v-container>
      <v-row v-show="$store.state.vision.showReIDVideos" v-if="$store.state.vision.snippetInfo">
        <!-- Upstream Video -->
        <v-col cols="6">
          <v-system-bar color="orange" height="5" dark />
          <!-- <v-img src="@/assets/loading.gif" aspect-ratio="1.78" /> -->
          <img :src="'http://10.51.4.9:25344/streams/PCAM_DOWN1'" width="100%" />
        </v-col>
        <!-- Downstream Video -->
        <v-col cols="6">
          <v-system-bar color="purple" height="5" dark />
          <!-- <v-img src="@/assets/loading.gif" aspect-ratio="1.78" /> -->
          <img :src="'http://10.51.4.9:25344/streams/PCAM_UP2'" width="100%" />
        </v-col>
      </v-row>
      <!-- Snippets -->
      <v-row class="mt-0">
        <v-col cols="12">
          <v-card outlined class="px-8 my-1 overflow" v-if="$store.state.vision.snippetInfo">
            <DetectionBoxes />
          </v-card>
          <v-card outlined height="80vh" class="grid-center" v-else-if="initialFetch">
            <v-col class="grid-center">
              <h2>Re-ID Detections</h2>
              <br />
              <h3>(Loading API...)</h3>
            </v-col>
          </v-card>
          <v-card outlined height="80vh" class="grid-center" v-else-if="!initialFetch">
            <v-col class="grid-center">
              <h2>Re-ID Detections</h2>
              <br />
              <h3 style="color: #FF6666;">(API Inactive)</h3>
            </v-col>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/vision';
import TitleBarReID from '@/components/modules/vision/TitleBarReID';
import DetectionBoxes from '@/components/modules/vision/DetectionBoxes.vue';

export default {
  components: {
    //  TitleBar,
    TitleBarReID,
    DetectionBoxes
  },

  data: () => ({
    title: 'Vehicle Re-ID',
    loading: false,
    initialFetch: true,
    fetchSuccess: false,
    apiConnectInterval: null
  }),

  mounted() {
    this.initialFetch = true;
    this.apiConnectInterval = null;
    this.fetchSnippetInfo();
    this.apiConnectInterval = setInterval(() => {
      if (this.fetchSuccess) {
        this.fetchSnippetInfo();
      }
      this.fetchSnippetInfo();
    }, 5000);
  },

  destroyed() {
    clearInterval(this.apiConnectInterval);
  },

  methods: {
    refreshData() {
      this.initialFetch = true;
      this.fetchSnippetInfo(true);
    },
    fetchSnippetInfo() {
      Api.fetchSnippets().then(
        data => {
          /* this.fetchSuccess = true */
          this.$store.state.vision.snippetInfo = data.meta;
          console.log('Snippet Info %o', this.$store.state.vision.snippetInfo);
          let snippetHistory = this.$store.state.vision.snippetHistory;
          if (snippetHistory.length == 0) {
            snippetHistory.push(data.meta);
          } else {
            if (JSON.stringify(snippetHistory[snippetHistory.length - 1]) != JSON.stringify(data.meta)) {
              if (snippetHistory.length == 20) {
                snippetHistory.shift();
              }
              snippetHistory.push(data.meta);
            }
          }
          if (this.initialFetch) {
            this.$store.dispatch('setSystemStatus', {
              text: 'Successfully connected to Vehicle Re-ID API',
              color: 'info'
            });
            this.initialFetch = false;
          }
        },
        error => {
          console.log(error);
          /* this.fetchSuccess = false */
          if (this.initialFetch) {
            console.log(2);
            this.$store.dispatch('setSystemStatus', {
              text: 'Failed to connect to Vehicle Re-ID API',
              color: 'warning'
            });
            this.initialFetch = false;
          }
        }
      );
    }
  }
};
</script>

<style lang="scss">
.panel-default {
  text-align: center;
  // background-color: #e5f1f3;
  color: #555555;
  background-color: #f5f5f5;
  border-color: #ddd;
}
</style>

<style lang="scss" scoped>
.grid-center {
  display: grid;
  place-items: center;
}
</style>

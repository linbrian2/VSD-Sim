<template>
  <v-col class="pa-0">
    <!-- Buttons -->
    <!-- <v-row class="pt-4">
      <v-col cols="6" class="grid-right">
        <v-btn float @click="$store.state.vision.dialogMetadata = true">
          <v-icon left> mdi-history </v-icon>
          History
        </v-btn>
      </v-col>
      <v-col cols="6" class="grid-left">
        <v-btn float v-if="!$store.state.vision.showReIDVideos" @click="$store.state.vision.showReIDVideos = true">
          <v-icon left> mdi-video </v-icon>
          Show Videos
        </v-btn>
        <v-btn float v-if="$store.state.vision.showReIDVideos" @click="$store.state.vision.showReIDVideos = false">
          <v-icon left> mdi-video-off </v-icon>
          Hide Videos
        </v-btn>
      </v-col>
    </v-row> -->
    <!-- Snippets -->
    <v-row v-for="i in $store.state.vision.snippetInfo" :key="i.name">
      <v-col cols="2" v-for="j in i" :key="j.name" class="pa-1">
        <v-card outlined class="mb-1">
          <v-system-bar :color="getCardColor(j)" height="10" dark />
          <v-card-text class="pa-0 text-center grid-center">
            <v-tooltip top color="rgba(128, 128, 128, 1)">
              <template v-slot:activator="{ on, attrs }">
                <v-img :src="`${$store.state.vision.imageAPI}/${j.cls_uid}`" v-bind="attrs" v-on="on"></v-img>
              </template>
              <span>
                <b>Ingest Time:</b> {{ j.ingest_time }}<br />
                <b>Camera Name:</b> {{ j.cam_name }}<br />
                <b>CLS:</b> {{ j.cls.charAt(0).toUpperCase() + j.cls.slice(1) }}<br />
                <b>CLS Coords:</b> {{ j.coords }}<br />
                <!-- <b>Color:</b> {{ j.color }} -->
              </span>
            </v-tooltip>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <!-- Info Dialog -->
    <v-dialog
      v-model="$store.state.vision.dialogMetadata"
      scrollable
      fullscreen
      persistent
      :overlay="false"
      max-width="500px"
      transition="scroll-x-transition"
    >
      <v-card outlined>
        <v-card-title class="grid-center pb-0">
          Snippets with Metadata
          <v-switch label="Metadata" v-model="showMeta"></v-switch>
          <v-btn class="close-button" icon @click="$store.state.vision.dialogMetadata = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text v-if="$store.state.vision.snippetHistory">
          <v-pagination
            :length="$store.state.vision.snippetHistory.length"
            v-model="page"
            :total-visible="15"
            class="mb-4"
          ></v-pagination>
          <v-row v-for="i in $store.state.vision.snippetHistory[page - 1]" :key="i.name">
            <v-col cols="2" class="pa-0" v-for="j in i" :key="j.name">
              <v-col class="pa-1">
                <v-card outlined class="mb-1">
                  <v-system-bar :color="getCardColor(j)" height="10" dark />
                  <v-card-text class="pa-0 text-center grid-center">
                    <v-img :src="`${$store.state.vision.imageAPI}/${j.cls_uid}`"></v-img>
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col class="pa-1" v-if="showMeta">
                <v-card class="pa-5">
                  <b>Ingest Time:</b> {{ j.ingest_time }}<br />
                  <b>Camera Name:</b> {{ j.cam_name }}<br />
                  <b>CLS:</b> {{ j.cls.charAt(0).toUpperCase() + j.cls.slice(1) }}<br />
                  <b>CLS UID:</b> {{ j.cls_uid }}<br />
                  <b>CLS Conf:</b> {{ j.cls_conf }}<br />
                  <b>CLS Coords:</b> {{ j.coords }}<br />
                  <!-- <b>Color:</b> {{ j.color }} -->
                </v-card>
              </v-col>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-col>
</template>

<script>
export default {
  data() {
    return {
      rows: 10,
      showMeta: true,
      page: 1
    };
  },
  methods: {
    getCardColor(info) {
      if (info.cam_name.includes('DOWN')) return 'orange';
      else if (info.cam_name.includes('UP')) return 'purple';
      else return 'black';
    }
  }
};
</script>

<style lang="scss" scoped>
.grid-left {
  display: grid;
  place-items: start;
}
.grid-right {
  display: grid;
  place-items: end;
}
.grid-center {
  display: grid;
  place-items: center;
}
.close-button {
  position: absolute;
  right: 20px;
  padding: 10px;
}
</style>

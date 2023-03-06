<template>
  <v-row justify="center">
    <v-dialog v-model="show" scrollable max-width="700px">
      <v-card>
        <v-app-bar dense>
          <v-icon class="mt-n1 mr-2">mdi-information-outline</v-icon>
          <v-toolbar-title>Release Notes</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="show = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>

        <div class="d-flex justify-space-between my-2">
          <div class="mx-6 overline blue--text">
            <strong>Version {{ version }}</strong>
          </div>
          <div class="mx-6 overline">Build Time: {{ buildTime }}</div>
        </div>

        <v-divider></v-divider>

        <v-card-text style="height: 350px;">
          <div class="pre-formatted" v-html="text" />
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="show = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>
<script>
//import { mapState } from 'vuex';
import Utils from '@/utils/Utils';
export default {
  props: {
    value: Boolean
  },

  data: () => ({
    title: `Version ${process.env.VUE_APP_VERSION}`,
    text: `<br>* Dashboard UI improvements <br>
* Added real-time message list to dashboard <br>
* Fixed issuses on the detector quality map <br>
* Improved Traffic Anomaly Map<br>
* Improved NTCIP UI<br>`
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

    version() {
      return process.env.VUE_APP_VERSION || '';
    },

    buildTime() {
      const d = process.env.VUE_APP_BUILD_TIME;
      return d ? Utils.formatDateTime(new Date(d)) : '';
    }
  },

  watch: {
    incidentSettings() {
      this.updateSettings();
    }
  },

  mounted() {
    this.updateSettings();
  },

  methods: {
    updateSettings() {},

    saveSettings() {}
  }
};
</script>

<style lang="scss" scoped>
.pre-formatted {
  word-wrap: break-word;
  white-space: pre-wrap;
  -moz-white-space: pre-wrap;
}
.scroll {
  overflow-y: scroll;
}
</style>

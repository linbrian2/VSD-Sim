<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" :max-width="options.width" :style="{ zIndex: options.zIndex }" @keydown.esc="cancel">
      <v-card color="white">
        <v-toolbar dark :color="options.color" dense flat>
          <v-toolbar-title class="text-body-1 font-weight-bold black--text">
            {{ title }}
          </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon small @click="dialog = false">
            <v-icon small color="black">mdi-close</v-icon>
          </v-btn>
        </v-toolbar>
        <v-card-text v-show="!!message" class="pa-4 black--text" v-html="message"></v-card-text>
        <v-card-actions class="pt-3">
          <v-spacer></v-spacer>
          <v-btn v-if="!options.noconfirm" color="grey" outlined class="body-2 font-weight-bold" @click.native="cancel"
            >Cancel</v-btn
          >
          <v-btn color="primary" class="body-2 font-weight-bold" outlined @click.native="agree">OK</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
export default {
  data: () => ({
    dialog: false,
    resolve: null,
    reject: null,
    message: null,
    title: null,
    options: {
      color: 'blue',
      width: 500,
      zIndex: 200,
      noconfirm: false
    }
  }),

  methods: {
    open(title, message, options) {
      this.dialog = true;
      this.title = title;
      this.message = message;
      this.options = Object.assign(this.options, options);
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
    },

    agree() {
      this.resolve(true);
      this.dialog = false;
    },

    cancel() {
      this.resolve(false);
      this.dialog = false;
    }
  }
};
</script>

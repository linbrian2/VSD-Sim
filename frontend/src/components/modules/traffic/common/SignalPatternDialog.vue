<template>
  <v-row justify="center">
    <v-dialog
      persistent
      v-model="dialog"
      :max-width="options.width"
      :style="{ zIndex: options.zIndex }"
      @keydown.esc="cancel"
    >
      <v-card tile>
        <v-toolbar dark :color="options.color" dense flat>
          <v-toolbar-title class="text-body-1 font-weight-bold white--text">
            {{ title }}
          </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon small @click="cancel">
            <v-icon small color="black">mdi-close</v-icon>
          </v-btn>
        </v-toolbar>
        <v-card-text>
          <v-form ref="form" lazy-validation>
            <v-row dense class="mt-4 mb-n4">
              <v-col cols="4">
                <v-text-field
                  outlined
                  label="Pattern Number"
                  v-model="params.pattern"
                  :rules="rules"
                  :disabled="options.constant"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field
                  outlined
                  label="Cycle Length"
                  v-model="params.cycle"
                  :rules="rules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field outlined label="Offset" v-model="params.offset" :rules="rules" required></v-text-field>
              </v-col>

              <v-col cols="3"><v-text-field outlined label="P1" v-model="params.P1" :rules="numberRule"/></v-col>
              <v-col cols="3"><v-text-field outlined label="P2" v-model="params.P2" :rules="numberRule"/></v-col>
              <v-col cols="3"><v-text-field outlined label="P3" v-model="params.P3" :rules="numberRule"/></v-col>
              <v-col cols="3"><v-text-field outlined label="P4" v-model="params.P4" :rules="numberRule"/></v-col>

              <v-col cols="3"><v-text-field outlined label="P5" v-model="params.P5" :rules="numberRule"/></v-col>
              <v-col cols="3"><v-text-field outlined label="P6" v-model="params.P6" :rules="numberRule"/></v-col>
              <v-col cols="3"><v-text-field outlined label="P7" v-model="params.P7" :rules="numberRule"/></v-col>
              <v-col cols="3"><v-text-field outlined label="P8" v-model="params.P8" :rules="numberRule"/></v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions class="pt-0">
          <v-spacer></v-spacer>
          <v-btn v-if="!options.noconfirm" color="grey" outlined class="body-2 font-weight-bold" @click.native="cancel"
            >Cancel</v-btn
          >
          <v-btn color="primary" class="body-2 font-weight-bold mr-4" outlined @click.native="agree">OK</v-btn>
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
    title: null,
    params: {},
    options: {
      color: 'teal',
      width: 560,
      zIndex: 200,
      noconfirm: false,
      constant: false
    },
    rules: [v => !!v || 'Required', v => !isNaN(parseInt(v)) || 'Must be a integer number'],
    numberRule: [
      v => !v || v === '/' || (!isNaN(parseInt(v)) && v >= 0 && v <= 255) || 'Number has to be between 0 and 255'
    ]
  }),

  methods: {
    open(title, params, options) {
      this.dialog = true;
      this.title = title;
      this.params = params;
      this.options = Object.assign(this.options, options);
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
    },

    get() {
      const inp = this.params;
      return {
        patternNumber: parseInt(inp.pattern),
        cycleLength: parseInt(inp.cycle),
        offset: parseInt(inp.offset),
        phaseTime: [
          parseInt(inp.P1),
          parseInt(inp.P2),
          parseInt(inp.P3),
          parseInt(inp.P4),
          parseInt(inp.P5),
          parseInt(inp.P6),
          parseInt(inp.P7),
          parseInt(inp.P8)
        ]
      };
    },

    agree() {
      if (this.$refs.form.validate()) {
        this.resolve(true);
        this.dialog = false;
      }
    },

    cancel() {
      this.resolve(false);
      this.dialog = false;
    }
  }
};
</script>

<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="500px" @keydown.esc="hideDialog">
      <v-card color="black">
        <v-app-bar dark dense>
          <v-icon small color="lime" class="mr-1"> mdi-sign-caution</v-icon>
          <v-toolbar-title class="overline">{{ vms.name }} </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-card-text class="pa-4 black--text">
          <div v-if="vms.message">
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <div
                  class="overline text-h4 font-weight-bold text-center orange--text"
                  v-bind="attrs"
                  v-on="on"
                  v-html="vms.message"
                ></div>
              </template>
              <span>Last Updated: {{ format(vms.lastUpdated) }}</span>
            </v-tooltip>
          </div>
          <div v-else>
            <div class="overline text-h4 font-weight-bold text-center grey--text my-12">
              No Content
            </div>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    value: Boolean
  },

  data: () => ({
    vms: {}
  }),

  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    }
  },

  methods: {
    init(vms) {
      Object.assign(this.vms, vms);
    },

    hideDialog() {
      this.show = false;
    },

    format(d) {
      if (!d) {
        return '';
      }
      return Utils.formatTime(new Date(d));
    }
  }
};
</script>

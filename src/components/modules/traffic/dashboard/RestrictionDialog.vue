<template>
  <v-row justify="center">
    <v-dialog scrollable v-model="show" persistent max-width="900px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-traffic-cone</v-icon>
          <v-toolbar-title
            >{{ title }}<small>{{ subtitle }} </small></v-toolbar-title
          >
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-card-text style="min-height: 250px; max-height: 550px;">
          <v-container>
            <v-card class="my-3" v-for="item in restrictions" :key="item.id">
              <v-card-title>
                <div>
                  <strong>{{ item.name }}</strong>
                </div>
                <v-spacer></v-spacer>
                <div class="overline font-weight-light">Data received at {{ item.time | dateAndTime }}</div>
              </v-card-title>
              <v-card-text class="font-weight-light">
                <div v-html="item.desc" />
              </v-card-text>
            </v-card>
          </v-container>
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
    title: '',
    subtitle: '',
    expanded: null,
    restrictions: []
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

  filters: {
    dateAndTime(t) {
      const d = new Date(t);
      return Utils.formatDateTime(d);
    }
  },

  methods: {
    init(res) {
      if (res) {
        this.title = 'Restrictions';
        this.restrictions = Object.assign([], res.data);
        this.expanded = [...Array(this.restrictions.length).keys()].map((k, i) => i);
      }
    },

    cleanUp() {},

    hideDialog() {
      this.show = false;
      this.cleanUp();
    }
  }
};
</script>

<style lang="scss" scoped></style>

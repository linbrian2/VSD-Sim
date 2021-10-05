<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="1000px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-chart-line</v-icon>
          <v-toolbar-title>{{ title }} </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container style="width: 960px; height:600px">
          <BasicChart :data="data" :height="height" />
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import BasicChart from '@/components/cav/BasicChart';
export default {
  props: {
    value: Boolean
  },

  components: {
    BasicChart
  },

  data: () => ({
    title: '',
    data: {},
    height: 550
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

  mounted() {
    //this.init(this.deviceId, this.direction, this.time, this.timeSlots);
  },

  methods: {
    init(title, data) {
      this.title = title;
      this.data = data;
    },

    hideDialog() {
      this.show = false;
    }
  }
};
</script>

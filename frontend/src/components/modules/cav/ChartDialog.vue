<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="1100px">
      <v-card>
        <v-app-bar dark dense>
          <v-icon class="mt-n1 mr-2">mdi-chart-line</v-icon>
          <v-toolbar-title>{{ title }} </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <div style="width: 1100px; height:600px">
          <BasicChart :data="data" :height="height" />
        </div>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import BasicChart from '@/components/modules/cav/BasicChart';
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
    height: 600
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

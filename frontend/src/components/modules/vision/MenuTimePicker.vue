<template>
  <div style="width:100px">
    <v-menu
      ref="menu"
      v-model="menu2"
      :close-on-content-click="false"
      :nudge-right="10"
      :return-value.sync="time"
      transition="scale-transition"
      offset-y
      max-width="320px"
      min-width="320px"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-text-field dark hide-details v-model="time" readonly v-bind="attrs" v-on="on">
          <template v-slot:prepend>
            <v-icon class="mt-1" color="grey">mdi-clock-time-four-outline</v-icon>
          </template>
        </v-text-field>
      </template>
      <v-time-picker v-if="menu2" v-model="time" scrollable color="green lighten-1">
        <v-spacer></v-spacer>
        <v-btn text color="primary" @click="menu2 = false">
          Cancel
        </v-btn>
        <v-btn text color="primary" @click="timeSelected">
          OK
        </v-btn>
      </v-time-picker>
    </v-menu>
  </div>
</template>

<script>
export default {
  data() {
    return {
      time: '09:00',
      menu2: false
    };
  },
  mounted() {
    this.time = this.$store.state.vision.currentTime;
  },
  methods: {
    timeSelected() {
      this.$refs.menu.save(this.time);
      this.$store.commit('vision/SET_CURRENT_TIME', this.time);
    },
    setTime(time) {
      this.time = time;
    }
  }
};
</script>

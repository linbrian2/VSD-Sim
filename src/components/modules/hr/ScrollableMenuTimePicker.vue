<template>
  <div class="d-flex justify-space-between align-center">
    <v-btn small icon @click.stop="prevTime" class="mr-1 material-bubble">
      <v-icon color="white">mdi-arrow-left-bold</v-icon>
    </v-btn>
    <v-menu
      ref="menu"
      v-model="menu2"
      :close-on-content-click="false"
      :nudge-right="0"
      :return-value.sync="time"
      transition="scale-transition"
      offset-y
      max-width="320px"
      min-width="320px"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-chip class="ma-2" color="primary" label v-bind="attrs" v-on="on">{{ time }}</v-chip>
      </template>
      <v-time-picker v-if="menu2" v-model="time" scrollable color="primary">
        <v-spacer></v-spacer>
        <v-btn text color="primary" @click="menu2 = false">
          Cancel
        </v-btn>
        <v-btn text color="primary" @click="timeSelected">
          OK
        </v-btn>
      </v-time-picker>
    </v-menu>
    <v-btn small icon @click.stop="nextTime" class="ml-1 material-bubble">
      <v-icon color="white">mdi-arrow-right-bold</v-icon>
    </v-btn>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    inc: { type: Number, default: 60 }
  },

  data() {
    return {
      time: '09:00',
      menu2: false
    };
  },
  methods: {
    timeSelected() {
      this.$refs.menu.save(this.time);
      this.$emit('set-time', this.time);
    },

    setTime(time) {
      this.time = time;
    },

    getTime() {
      return this.time;
    },

    prevTime() {
      this.incTime(-this.inc);
      this.$emit('prev', this.time);
    },

    nextTime() {
      this.incTime(this.inc);
      this.$emit('next', this.time);
    },

    incTime(seconds) {
      const date = new Date();
      const d = Utils.dateFromDateAndTimeString(date, this.time);
      d.setSeconds(d.getSeconds() + seconds);
      this.time = Utils.formatTimeToMinutes(d);
    }
  }
};
</script>

<style lang="scss">
.custom.v-text-field > .v-input__control > .v-input__slot:before {
  border-style: none;
}
.custom.v-text-field > .v-input__control > .v-input__slot:after {
  border-style: none;
}
</style>

<template>
  <div>
    <!-- Time Picker -->
    <v-scroll-x-reverse-transition>
      <v-card style="position: absolute; top: 0px; left: 61.1%" v-show="$store.state.bluetooth.timePickerMenu">
        <v-time-picker v-model="time" scrollable color="green lighten-1">
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="cancelTimePicker"> Cancel </v-btn>
          <v-btn text color="primary" @click="timeSelected"> OK </v-btn>
        </v-time-picker>
      </v-card>
    </v-scroll-x-reverse-transition>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  data() {
    return {
      time: null,
    };
  },
  mounted() {
    this.time = new Date();
    if (this.currentDate) {
      this.time = this.currentDate;
    }

    this.$bus.$on('CHANGE_BLUETOOTH_TIME', date => {
      this.setTime(date);
    });
  },
  computed: {
    ...mapState(['currentDate']),
  },
  methods: {
    /* Time Picker */
    setTime(date) {
      this.time = date
    },
    cancelTimePicker() {
      this.time = this.currentDate;
      this.$store.state.bluetooth.timePickerMenu = false;
    },
    timeSelected() {
      if (typeof this.time == 'string') {
        let timeSplit = this.time.split(':');
        let cd = this.currentDate;
        this.$store.state.currentDate = new Date(
          cd.getFullYear(),
          cd.getMonth(),
          cd.getDate(),
          parseInt(timeSplit[0]),
          parseInt(timeSplit[1])
        );
        console.log(this.time);
        this.time = this.currentDate;
        this.$store.state.bluetooth.timePickerMenu = false;
      }
    },
  },
};
</script>

<style>
</style>
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
        <v-text-field hide-details dark readonly v-model="time" v-bind="attrs" v-on="on" class="custom mt-4">
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

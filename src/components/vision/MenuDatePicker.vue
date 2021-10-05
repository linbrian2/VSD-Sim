<template>
  <div class="d-flex justify-space-between align-center">
    <v-btn small icon @click.stop="prevDate" class="mr-2 material-bubble">
      <v-icon color="white">mdi-arrow-left-bold</v-icon>
    </v-btn>
    <v-menu
      ref="menu"
      v-model="menu"
      :close-on-content-click="false"
      :return-value.sync="fromDateVal"
      transition="scale-transition"
      offset-y
      max-width="295px"
      min-width="295px"
    >
      <template v-slot:activator="{ on }">
        <v-text-field
          hide-details
          flat
          solo
          :value="dateFormatted"
          label=""
          readonly
          v-on="on"
          style="width: 270px; color:grey;"
        >
          <template v-slot:prepend>
            <v-icon class="mt-0 mr-n2" color="grey lighten-1">mdi-calendar-month</v-icon>
          </template>
        </v-text-field>
      </template>
      <v-date-picker v-model="fromDateVal" :allowed-dates="allowedDates" no-title scrollable color="green lighten-1">
        <v-spacer></v-spacer>
        <v-btn text color="primary" @click="menu = false">Cancel</v-btn>
        <v-btn text color="primary" @click="dateSelected">OK</v-btn>
      </v-date-picker>
    </v-menu>

    <v-btn small icon @click.stop="nextDate" class="ml-2 material-bubble">
      <v-icon color="white">mdi-arrow-right-bold</v-icon>
    </v-btn>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    date: {
      type: Date,
      default: new Date()
    }
  },

  data: () => ({
    menu: false,
    fromDateVal: null
  }),

  computed: {
    dateFormatted() {
      return this.fromDateVal ? Utils.formatDateLong(Utils.dateFromString(this.fromDateVal)) : '';
    }
  },

  watch: {
    date() {
      this.init();
    }
  },

  mounted() {
    this.init();
  },

  methods: {
    init() {
      this.fromDateVal = Utils.formatDate(this.date);
    },

    allowedDates(val) {
      let date0 = parseInt(val.split('-').join(''));
      let date1 = Utils.getTodayNumber();
      return date0 <= date1;
    },

    dateSelected() {
      this.$refs.menu.save(this.fromDateVal);
      this.$emit('setdate', Utils.dateFromString(this.fromDateVal));
    },

    prevDate() {
      this.$store.dispatch('vision/incCurrentDate', -1);
    },

    nextDate() {
      this.$store.dispatch('vision/incCurrentDate', 1);
    }
  }
};
</script>
<style lang="scss" scoped>
.material-bubble {
  background-color: transparent;
  border: none;
  overflow: hidden;
  box-shadow: none;
  opacity: 0;
  transition: opacity 0.35s ease;

  &:hover {
    color: darken(white, 10%);
    opacity: 1;
  }
}
</style>

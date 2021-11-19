<template>
  <div class="d-flex justify-space-between align-center">
    <v-btn small icon @click.stop="prevDate" class="mr-2 material-bubble" :disabled="disablePicker">
      <v-icon color="white">mdi-arrow-left-bold</v-icon>
    </v-btn>
    <v-menu
      ref="menu"
      v-model="menu"
      :close-on-content-click="false"
      :return-value.sync="fromDateVal"
      transition="scale-transition"
      offset-y
      max-width="290px"
      min-width="290px"
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
          style="width: 190px; color:grey;"
          :disabled="disablePicker"
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

    <v-btn small icon @click.stop="nextDate" class="ml-2 material-bubble" :disabled="disablePicker">
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
      default: new Date(),
    },
    disable: Boolean
  },

  data: () => ({
    menu: false,
    fromDateVal: null
  }),

  computed: {
    dateFormatted() {
      return this.fromDateVal ? Utils.formatDateLong2(Utils.dateFromString(this.fromDateVal)) : '';
    },
    disablePicker() {
      return this.disable ? true : false
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

  created() {
    window.addEventListener('keydown', this.keydownListener);
  },

  destroyed() {
    window.removeEventListener('keydown', this.keydownListener);
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
      const date = Utils.dateFromString(this.fromDateVal);
      this.$store.commit('SET_CURRENT_DATE', date);
      this.$emit('setdate', date);
    },

    prevDate() {
      this.$emit('prev');
      this.$store.dispatch('incCurrentDate', -1);
    },

    nextDate() {
      this.$emit('next');
      this.$store.dispatch('incCurrentDate', 1);
    },

    keydownListener(e) {
      if (e.key === 'ArrowLeft') {
        this.prevDate();
      } else if (e.key === 'ArrowRight') {
        this.nextDate();
      }
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

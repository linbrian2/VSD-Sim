<template>
  <v-menu
    ref="menu"
    v-model="menu"
    :close-on-content-click="false"
    :return-value.sync="date"
    transition="scale-transition"
    offset-y
    max-width="290px"
    min-width="290px"
  >
    <template v-slot:activator="{ on }">
      <v-text-field
        hide-details
        v-model="dateFormatted"
        label=""
        readonly
        v-on="on"
        :style="{ color: 'grey', width: width }"
      >
        <template v-slot:prepend>
          <v-icon class="mt-1" color="">mdi-calendar-month</v-icon>
        </template>
      </v-text-field>
    </template>
    <v-date-picker v-model="date" :allowed-dates="allowedDates" no-title scrollable color="green lighten-1">
      <v-spacer></v-spacer>
      <v-btn text color="primary" @click="menu = false">Cancel</v-btn>
      <v-btn text color="primary" @click="dateSelected">OK</v-btn>
    </v-date-picker>
  </v-menu>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    type: {
      type: String,
      default: 'long'
    }
  },
  data: () => ({
    menu: false,
    date: Utils.formatDate(new Date())
  }),
  computed: {
    dateFormatted() {
      if (!this.date) {
        return '';
      }
      const d = Utils.dateFromString(this.date);
      return this.type === 'long' ? Utils.formatDateLong(d) : Utils.formatDateLong2(d);
    },

    width() {
      return this.type === 'long' ? '250px' : '180px';
    }
  },

  methods: {
    allowedDates(val) {
      let date0 = parseInt(val.split('-').join(''));
      let date1 = Utils.getTodayNumber();
      return date0 <= date1;
    },

    dateSelected() {
      this.$refs.menu.save(this.date);
      const date = Utils.dateFromString(this.date);
      this.$store.commit('cav/SET_CURRENT_DATE', date);
      this.$emit('date', date);
    }
  }
};
</script>

<style lang="scss" scoped></style>

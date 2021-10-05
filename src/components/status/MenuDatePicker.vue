<template>
  <div style="width:290px">
    <v-menu
      ref="menu"
      v-model="menu2"
      :close-on-content-click="false"
      :nudge-right="0"
      :return-value.sync="date"
      transition="scale-transition"
      offset-y
      max-width="290px"
      min-width="290px"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-text-field
          dark
          hide-details
          v-model="dateFormatted"
          label=""
          prepend-inner-icon="mdi-calendar-month"
          readonly
          v-bind="attrs"
          v-on="on"
        >
        </v-text-field>
      </template>

      <v-date-picker v-if="menu2" v-model="date" :allowed-dates="allowedDates" no-title scrollable
                     color="green lighten-1">
        <v-spacer></v-spacer>
        <v-btn text color="primary" @click="menu2 = false">
          Cancel
        </v-btn>
        <v-btn text color="primary" @click="dateSelected">
          OK
        </v-btn>
      </v-date-picker>
    </v-menu>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  data() {
    return {
      date: Utils.formatDate(new Date()),
      menu2: false
    };
  },

  computed: {
    dateFormatted() {
      const df = this.date ? Utils.formatDateLong(Utils.dateFromString(this.date)) : '';
      return df;
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
      this.$store.commit('status/SET_CURRENT_DATE', Utils.dateFromString(this.date));
    }
  }
};
</script>

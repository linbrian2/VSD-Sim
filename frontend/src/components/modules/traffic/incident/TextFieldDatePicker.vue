<template>
  <div class="d-flex justify-space-between align-center">
    <!-- <v-menu
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
          solo
          :value="dateFormatted"
          :label="label"
          readonly
          v-on="on"
          style="width: 190px;"
        >
        </v-text-field>
      </template>
      <v-date-picker v-model="fromDateVal" :allowed-dates="allowedDates" no-title scrollable color="green lighten-1">
        <v-spacer></v-spacer>
        <v-btn text color="primary" @click="menu = false">Cancel</v-btn>
        <v-btn text color="primary" @click="dateSelected">OK</v-btn>
      </v-date-picker>
    </v-menu> -->
     <v-menu
        v-model="menu"
        :close-on-content-click="false"
        :nudge-right="40"
        transition="scale-transition"
        offset-y
        min-width="auto"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="date"
            label="labelText"
            prepend-icon="mdi-calendar"
            readonly
            v-bind="attrs"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker
          v-model="date"
          @input="menu = false"
          scrollable color="green lighten-1"
        ></v-date-picker>
      </v-menu>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    labelText: { type: String, default: 'Label' },
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
      return this.fromDateVal ? Utils.formatDateLong2(Utils.dateFromString(this.fromDateVal)) : '';
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
      const date = Utils.dateFromString(this.fromDateVal);
      this.$emit('setdate', date);
    }
  }
};
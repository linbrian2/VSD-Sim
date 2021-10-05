<template>
  <v-toolbar flat dense floating style="position: absolute; top: 10px; left: 300px; height:44px">
    <!-- <v-img alt="Logo" class="shrink mr-2" contain src="@/assets/ai-logo.png" transition="scale-transition" width="60" /> -->
    <div>
      <v-menu
        ref="menu"
        v-model="menu"
        :close-on-content-click="false"
        :return-value.sync="date"
        transition="scale-transition"
        offset-y
        min-width="290px"
        max-width="290px"
      >
        <template v-slot:activator="{ on }">
          <v-text-field hide-details v-model="date" label="" readonly v-on="on" style="width: 130px;">
            <template v-slot:prepend>
              <v-icon class="mt-1">mdi-calendar-month</v-icon>
            </template>
          </v-text-field>
        </template>
        <v-date-picker v-model="date" :allowed-dates="allowedDates" no-title scrollable color="green lighten-1">
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="menu = false">Cancel</v-btn>
          <v-btn text color="primary" @click="dateSelected">OK</v-btn>
        </v-date-picker>
      </v-menu>
    </div>
    <v-select
      style="width: 180px; margin-left:15px"
      prepend-icon="mdi-traffic-light"
      dense
      hide-details
      single-line
      :items="items"
      :value="selectedItem"
      @input="signalSelected"
      label="signal"
    >
    </v-select>
    <v-divider class="ml-2 mr-0" vertical></v-divider>

    <v-tooltip bottom>
      <template v-slot:activator="{ on }">
        <v-btn icon v-on="on" @click.stop="showInfo()">
          <v-icon>mdi-information-outline</v-icon>
        </v-btn>
      </template>
      <span>Show Plan</span>
    </v-tooltip>

    <v-tooltip bottom>
      <template v-slot:activator="{ on }">
        <v-btn icon v-on="on" @click.stop="showPCD()">
          <v-icon>mdi-chart-areaspline</v-icon>
        </v-btn>
      </template>
      <span>Show PCD</span>
    </v-tooltip>

    <v-tooltip bottom>
      <template v-slot:activator="{ on }">
        <v-btn icon v-on="on" @click.stop="showSignal()">
          <v-icon>mdi-clock</v-icon>
        </v-btn>
      </template>
      <span>Show Signal</span>
    </v-tooltip>

    <v-tooltip bottom>
      <template v-slot:activator="{ on }">
        <v-btn icon v-on="on" @click.stop="goHome()">
          <v-icon>mdi-home</v-icon>
        </v-btn>
      </template>
      <span>Go Home</span>
    </v-tooltip>

    <v-menu bottom right offset-y>
      <template v-slot:activator="{ on, attrs }">
        <v-btn icon v-bind="attrs" v-on="on">
          <v-icon>mdi-dots-vertical</v-icon>
        </v-btn>
      </template>

      <v-list>
        <v-list-item v-for="(item, i) in menu_items" :key="i" @click="menuItemClicked(i)">
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </v-toolbar>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  data: () => ({
    date: Utils.formatDate(new Date()),
    menu: false,
    menu_items: [{ title: 'PCD charts' }]
  }),
  computed: {
    items() {
      let names = [];
      this.$store.state.hr.locations.forEach(location => {
        names.push(location.name.trimRight());
      });
      return names;
    },
    selectedItem() {
      let selected = this.$store.state.hr.activeMarker;
      return selected != null ? selected.name : '';
    },
    activeMarker() {
      return this.$store.state.hr.activeMarker;
    },
    currentDate() {
      return this.$store.state.hr.currentDate;
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
      this.$store.commit('hr/SET_CURRENT_DATE', Utils.dateFromString(this.date));
    },
    signalSelected(value) {
      this.$bus.$emit('NAME_SELECTED', value);
    },
    goHome() {
      this.$bus.$emit('CENTER_MAP', {});
    },
    showInfo() {
      console.log(this.currentDate);
      this.$bus.$emit('GET_SIGNAL', {
        marker: this.activeMarker,
        time: this.currentDate.getTime()
      });
    },
    showPCD() {
      this.$bus.$emit('SHOW_PCD', {
        id: this.activeMarker.id,
        time: this.currentDate,
        phase: -1
      });
    },
    showSignal() {
      this.$bus.$emit('SHOW_SIGNALS', {
        id: this.activeMarker.id,
        position: this.activeMarker.position,
        zoomLevel: 20
      });
    },
    menuItemClicked(idx) {
      if (idx == 0) {
        this.$store.commit('hr/SHOW_SELECTION_DLG', true);
      }
    }
  }
};
</script>

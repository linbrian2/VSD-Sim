<template>
  <div class="hr">
    <v-navigation-drawer app clipped v-model="showPanel" :width="420">
      <v-select
        class="mx-2"
        dense
        hide-details
        single-line
        :items="items"
        :value="selectedItem"
        @input="signalSelected"
        label="signal"
      />

      <MapSelect @click="onMapClick" />
    </v-navigation-drawer>
    <AppBar />
    <v-content>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </v-content>

    <v-snackbar v-model="snackbar.showing" :color="snackbar.color" :timeout="snackbar.timeout">
      {{ snackbar.text }}
      <v-btn text @click="snackbar.showing = false">Close</v-btn>
    </v-snackbar>
  </div>
</template>

<script>
import Constants from '@/utils/constants/hr';
import AppBar from '@/components/hr/AppBar';
import MapSelect from '@/components/hr/MapSelect';
import { mapState } from 'vuex';

export default {
  name: 'App',
  components: {
    AppBar,
    MapSelect
  },
  computed: {
    showPanel: {
      get() {
        return this.$store.state.hr.showPanel;
      },
      set(show) {
        this.$store.commit('hr/SHOW_PANEL', show);
      }
    },
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
    icon() {
      return this.$store.state.hr.showPanel ? 'mdi-close' : 'mdi-menu';
    },
    ...mapState('hr', ['snackbar', 'locations', 'currentSignal', 'currentDate', 'currentAction'])
  },
  methods: {
    hidePanel() {
      this.$store.commit('hr/SHOW_PANEL', false);
    },

    signalSelected(value) {
      this.$bus.$emit('NAME_SELECTED', value);
    },
    onMapClick(marker) {
      let time = this.currentDate.getTime();
      switch (this.currentAction) {
        case Constants.PAGE_INFO:
          this.$bus.$emit('GET_PLANS', { marker, time });
          break;
        case Constants.PAGE_PCD:
          this.$bus.$emit('GET_PCD', { marker, time });
          break;
        case Constants.PAGE_SIGNAL:
          this.$bus.$emit('GET_SIGNALS', { marker, time });
          break;
        case Constants.PAGE_PREF:
          this.$bus.$emit('GET_PREF', { marker, time });
          break;
        case Constants.PAGE_AOR:
          this.$bus.$emit('GET_AOR', { marker, time });
          break;
        case Constants.PAGE_VOLUME:
          this.$bus.$emit('GET_VOLUME', { marker, time });
          break;
        case Constants.PAGE_INTERVAL:
          this.$bus.$emit('GET_INTERVAL', { marker, time });
          break;
        case Constants.PAGE_DELAY:
          this.$bus.$emit('GET_DELAY', { marker, time });
          break;
      }
    }
  }
};
</script>
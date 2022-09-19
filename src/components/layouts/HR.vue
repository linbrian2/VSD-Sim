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

    <v-main>
      <router-view></router-view>
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/hr/AppBar';
import MapSelect from '@/components/modules/hr/MapSelect';
import { RouterNames } from '@/utils/constants/router';
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

    ...mapState(['currentDate']),
    ...mapState('hr', ['locations', 'currentSignal', 'signalTimingMode'])
  },

  // created() {
  //   this.$store.commit('SET_CURRENT_DATE', Utils.yesterday());
  // },

  methods: {
    hidePanel() {
      this.$store.commit('hr/SHOW_PANEL', false);
    },

    signalSelected(value) {
      this.$bus.$emit('NAME_SELECTED', value);
    },

    onMapClick(marker) {
      let time = this.currentDate.getTime();
      switch (this.$route.name) {
        case RouterNames.HR_PCD:
          this.$bus.$emit('GET_PCD', { marker, time });
          break;
        case RouterNames.HR_SIGNAL_DISPLAY:
          if (this.signalTimingMode == 'Playback') {
            this.$bus.$emit('GET_PLAYBACK_SIGNALS', { marker, time });
          } else {
            this.$bus.$emit('GET_LIVE_SIGNALS', { marker, time });
          }
          break;
        case RouterNames.HR_PERFORMANCE_MEASURES:
          this.$bus.$emit('GET_PREF', { marker, time });
          break;
      }
    }
  }
};
</script>

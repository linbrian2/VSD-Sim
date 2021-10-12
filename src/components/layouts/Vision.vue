<template>
  <div>
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
import Constants from '@/utils/constants/vision';
import AppBar from '@/components/vision/AppBar';
import MapSelect from '@/components/vision/MapSelect';
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
        return this.$store.state.vision.showPanel;
      },
      set(show) {
        this.$store.commit('vision/SHOW_PANEL', show);
      }
    },

    items() {
      let names = [];
      this.$store.state.vision.locations.forEach(location => {
        names.push(location.name.trimRight());
      });
      return names;
    },

    selectedItem() {
      let selected = this.$store.state.vision.activeMarker;
      return selected != null ? selected.name : '';
    },

    icon() {
      return this.$store.state.vision.showPanel ? 'mdi-close' : 'mdi-menu';
    },
    ...mapState('vision', ['locations', 'currentAction', 'currentDate'])
  },
  methods: {
    hidePanel() {
      this.$store.commit('vision/SHOW_PANEL', false);
    },

    signalSelected(value) {
      this.$bus.$emit('NAME_SELECTED', value);
    },

    onMapClick(marker) {
      let time = this.currentDate.getTime();
      switch (this.currentAction) {
        case Constants.PAGE_DASHBOARD:
          this.$bus.$emit('GET_INFO', { marker, time });
          break;
        case Constants.PAGE_RE_ID:
          this.$bus.$emit('GET_VIDEO', { marker, time });
          break;
      }
    }
  }
};
</script>

<style></style>

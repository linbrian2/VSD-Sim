<template>
  <div v-if="!$vuetify.breakpoint.mobile">
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
  <div v-else>
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
    <AppBar />
    <v-main class="pt-0">
      <router-view></router-view>
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/vision/AppBar';
import MapSelect from '@/components/modules/vision/MapSelect';
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

    ...mapState(['currentDate']),
    ...mapState('vision', ['locations'])
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
      switch (this.$route.name) {
        case RouterNames.VISION_DASHBOARD:
          this.$bus.$emit('GET_INFO', { marker, time });
          break;
        case RouterNames.VISION_REID:
          this.$bus.$emit('GET_VIDEO', { marker, time });
          break;
      }
    }
  }
};
</script>

<style></style>

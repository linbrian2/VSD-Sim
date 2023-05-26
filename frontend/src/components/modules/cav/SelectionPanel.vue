<template>
  <v-navigation-drawer ref="drawer" app clipped right v-model="showPanel" :width="width">
    <v-toolbar dense flat fixed overflow>
      <v-toolbar-title class="action-title">{{ title }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <slot name="buttons"></slot>
        <v-btn icon small @click="showPanel = false"><v-icon>mdi-close</v-icon></v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <slot></slot>
  </v-navigation-drawer>
</template>

<script>
export default {
  props: {
    title: String,
    width: {
      type: Number,
      default: 600
    }
  },

  computed: {
    showPanel: {
      get() {
        return this.$store.state.cav.showPanel;
      },
      set(show) {
        this.$store.commit('cav/SHOW_PANEL', show);
      }
    }
  },

  methods: {
    showPanelIfNot() {
      if (!this.showPanel) {
        this.$store.commit('cav/SHOW_PANEL', true);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.action-title {
  font-weight: bold;
  font-size: 16px;
  text-transform: uppercase;
}
</style>

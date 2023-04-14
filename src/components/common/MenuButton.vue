<template>
  <v-menu bottom right offset-y>
    <template v-slot:activator="{ on: menu, attrs }">
      <v-btn :disabled="disabled" small color="teal darken-3" v-bind="attrs" v-on="{ ...menu }" v-if="normal">
        <v-icon small left>{{ icon }}</v-icon>
        {{ tooltip }}
      </v-btn>

      <v-tooltip bottom v-else>
        <template v-slot:activator="{ on: tooltip }">
          <v-btn
            :disabled="disabled"
            class="mx-1"
            fab
            :color="color"
            icon
            v-bind="attrs"
            v-on="{ ...tooltip, ...menu }"
          >
            <v-icon>{{ icon }}</v-icon>
          </v-btn>
        </template>
        <span>{{ tooltip }}</span>
      </v-tooltip>
    </template>

    <!-- <v-list>
      <template v-for="(item, index) in items">
        <v-divider v-if="item.divider" :key="index"></v-divider>
        <v-list-item v-else :key="index + 100" @click="menuItemClicked(item.action)">
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>
      </template>
    </v-list> -->
  </v-menu>
</template>

<script>
export default {
  props: {
    items: Array,
    tooltip: String,
    icon: String,
    color: String,
    disabled: Boolean,
    normal: { type: Boolean, default: false }
  },
  methods: {
    menuItemClicked(action) {
      this.$emit('menuItemclick', action);
    }
  }
};
</script>

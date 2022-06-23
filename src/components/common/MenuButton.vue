<template>
  <v-menu bottom right offset-y>
    <template v-slot:activator="{ on: menu, attrs }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on: tooltip }">
          <v-btn class="mx-1" fab :color="color" icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
            <v-icon>{{ icon }}</v-icon>
          </v-btn>
        </template>
        <span>{{ tooltip }}</span>
      </v-tooltip>
    </template>

    <v-list>
      <template v-for="(item, index) in items">
        <v-divider v-if="item.divider" :key="index"></v-divider>
        <v-list-item v-else :key="index" @click="menuItemClicked(item.url)">
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>
      </template>
    </v-list>
  </v-menu>
</template>

<script>
export default {
  props: {
    items: Array,
    tooltip: String,
    icon: String,
    color: String
  },
  methods: {
    menuItemClicked(action) {
      this.$emit('menuItemclick', action);
    }
  }
};
</script>

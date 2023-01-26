<template>
  <div>
    <v-divider vertical class="ml-2" />
    <v-menu bottom right offset-y>
      <template v-slot:activator="{ on, attrs }">
        <v-btn icon v-bind="attrs" v-on="on">
          <v-icon>mdi-dots-vertical</v-icon>
        </v-btn>
      </template>

      <v-list>
        <template v-for="(item, index) in items">
          <v-divider v-if="item.divider" :key="index"></v-divider>
          <v-list-item v-else :key="`e-${index}`" @click="menuItemClicked(item.action)">
            <v-icon small left v-text="item.icon" />
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item>
        </template>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
export default {
  props: {
    items: Array
  },
  methods: {
    menuItemClicked(action) {
      this.$emit('menuItemclick', action);
    }
  }
};
</script>

<style lang="scss" scoped></style>

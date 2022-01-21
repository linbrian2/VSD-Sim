<template>
  <v-menu offset-y>
    <template v-slot:activator="{ on, attrs }">
      <v-btn small outlined color="grey lighten-2" dark v-bind="attrs" v-on="on" class="mt-3 mr-4">
        &nbsp;&nbsp;{{ title }}: {{ selectedItem }} {{ unit }}&nbsp;&nbsp;
      </v-btn>
    </template>

    <v-list>
      <v-list-item v-for="(item, i) in items" :key="i" @click="handler(item)">
        <v-list-item-title :class="{ 'font-weight-bold': item === selectedItem }">
          <v-icon class="mr-1" v-if="item === selectedItem">mdi-check</v-icon>
          <span :class="{ 'ml-8': item !== selectedItem }"> {{ item }} {{ unit }}</span>
        </v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
export default {
  props: {
    title: String,
    unit: String,
    selectedItem: Number,
    items: {
      type: Array,
      default: () => []
    }
  },

  methods: {
    handler(item) {
      this.$emit('click', item);
    }
  }
};
</script>

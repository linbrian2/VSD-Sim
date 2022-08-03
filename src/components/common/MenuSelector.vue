<template>
  <v-menu offset-y>
    <template v-slot:activator="{ on, attrs }">
      <v-btn small plain :color="color" v-bind="attrs" v-on="on" class="mt-3">
        <h4>{{ selectedItem }}</h4>
        <span> <v-icon>mdi-chevron-down</v-icon> </span>
      </v-btn>
    </template>

    <v-list>
      <v-list-item v-for="(item, i) in items" :key="i" @click="handler(item)">
        <v-list-item-title :class="{ 'font-weight-bold': item == selectedItem }">
          <v-icon class="mr-1" v-if="item == selectedItem">mdi-check</v-icon>
          <span :class="{ 'ml-8': item != selectedItem }">
            <span>{{ item }} </span>
          </span>
        </v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
export default {
  props: {
    selectedItem: String,
    color: {
      type: String,
      default: '#567'
    },
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

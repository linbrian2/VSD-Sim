<template>
  <div style="position: absolute; top:50px; left:10px; width: 15px; ">
    <v-menu offset-y>
      <template v-slot:activator="{ on, attrs }">
        <v-btn small outlined color="grey lighten-2" dark v-bind="attrs" v-on="on">
          {{ selectedItem }}
        </v-btn>
      </template>

      <v-list dense>
        <v-list-item v-for="(item, i) in items" :key="i" @click="handler(item)">
          <v-list-item-title :class="{ 'font-weight-bold': item.text == selectedItem }">
            <v-icon small class="mr-1" v-if="item.text == selectedItem">mdi-check</v-icon>
            <span :class="{ 'ml-6': item.text != selectedItem }">
              <span>{{ item.text }} </span>
            </span>
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
export default {
  data: () => ({
    items: [
      { text: 'P2', value: 2 },
      { text: 'P6', value: 6 }
    ],
    selectedItem: 'P2'
  }),

  methods: {
    handler(item) {
      this.selectedItem = item.text;
      this.$emit('click', item.value);
    }
  }
};
</script>

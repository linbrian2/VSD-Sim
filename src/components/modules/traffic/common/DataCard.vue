<template>
  <v-card tile min-width="100%" min-height="200px">
    <v-card-title class="my-n2 pa-1 teal darken-2" v-if="title">
      <h5 class="text-center ml-5 overline">{{ title }}</h5>
    </v-card-title>

    <v-card-text class="mt-2 pb-0">
      <v-simple-table v-if="items && items.length > 0">
        <template v-slot:default>
          <tbody>
            <tr v-for="item in items" :key="item.name">
              <td>
                <strong>{{ item.name }}</strong>
              </td>
              <td>{{ item.value }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      <slot></slot>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  props: {
    title: String,
    items: {
      type: Array,
      default: () => []
    }
  }
};
</script>

<style lang="scss" scoped>
.text-center {
  font-weight: bold;
  text-align: center;
  color: white;
}
</style>

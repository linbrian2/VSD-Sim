<template>
  <v-card tile min-width="100%" min-height="200px">
    <v-card-title class="my-n2 pa-1 teal darken-2" v-if="title">
      <h5 class="text-center ml-5 overline">{{ title }}</h5>
    </v-card-title>

    <div class="mt-2 pb-0 mx-0">
      <v-simple-table v-if="items && items.length > 0">
        <template v-slot:default>
          <tbody>
            <tr v-for="item in items" :key="item.name">
              <td>
                <strong>{{ item.name }}</strong>
              </td>
              <td>
                <div v-if="item.button">
                  <div v-if="item.button.tooltip">
                    <v-tooltip bottom color="primary">
                      <template v-slot:activator="{ on }">
                        <v-btn icon v-on="on" @click="item.button.handler">
                          <v-icon :color="item.button.color" v-text="item.button.icon" />
                          <span v-if="item.button.text"> {{ item.button.text }} </span>
                        </v-btn>
                      </template>
                      <span>{{ item.button.tooltip }}</span>
                    </v-tooltip>
                  </div>
                  <div v-else>
                    <v-btn icon @click="item.button.handler">
                      <v-icon :color="item.button.color" v-text="item.button.icon" />
                      <span v-if="item.button.text"> {{ item.button.text }} </span>
                    </v-btn>
                  </div>
                </div>

                <div v-else>{{ item.value }}</div>
              </td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      <slot></slot>
    </div>
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

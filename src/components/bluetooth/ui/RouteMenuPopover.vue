<template>
  <div>
    <v-menu
      v-model="menu"
      :close-on-content-click="false"
      :nudge-width="200"
      offset-x
    >
      <template v-slot:activator="{ on, attrs }">
        <v-badge
          color="grey"
          :content="$store.state.bluetooth.selectedRoutes.length"
          :value="$store.state.bluetooth.selectedRoutes.length > 0"
        >
          <v-btn icon v-bind="attrs" v-on="on" class="ml-2" small>
            <v-icon>mdi-filter</v-icon>
          </v-btn>
        </v-badge>
      </template>

      <v-card>
        <v-list>
          <v-list-item>
            <v-autocomplete
              v-model="$store.state.bluetooth.selectedRoutes"
              dense
              outlined
              :items="$store.state.bluetooth.apiData.routes"
              :search-input.sync="search"
              color="white"
              hide-no-data
              hide-selected
              label="Filter By Routes"
              return-object
              small-chips
              multiple
              deletable-chips
              class="mt-0 mx-6"
            >
              <template v-slot:append-outer>
                <v-tooltip top>
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn dense icon v-bind="attrs" v-on="on" @click="clearAll()" class="pb-3">
                      <v-icon>mdi-backspace-outline</v-icon>
                    </v-btn>
                  </template>
                  <span>Clear all</span>
                </v-tooltip>
              </template>
            </v-autocomplete>
          </v-list-item>
        </v-list>
      </v-card>
    </v-menu>
  </div>
</template>

<script>
export default {
  data: () => ({
    search: null,
    menu: false,
  }),
  methods: {
    clearAll() {
      this.$store.state.bluetooth.selectedRoutes = []
    },
  },
};
</script>

<style lang="scss" scoped>
</style>
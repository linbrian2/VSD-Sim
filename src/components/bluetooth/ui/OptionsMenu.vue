<template>
  <div>
    <!-- {{options}}<br/>
    {{this.$store.state.bluetooth.prefs}} -->
    <v-menu
      v-model="menu"
      :close-on-content-click="false"
      :nudge-width="200"
      offset-x
    >
      <template v-slot:activator="{ on, attrs }">
        <div v-bind="attrs" v-on="on">
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs" v-on="on" class="mx-1">
                <v-icon>mdi-cog</v-icon>
              </v-btn>
            </template>
            <span>Options</span>
          </v-tooltip>
        </div>
      </template>

      <v-card>
        <v-list>
          <v-list-item>
            <h3>Number of columns:</h3>
          </v-list-item>
          <v-list-item>
            <v-btn-toggle dense mandatory v-model="options.colCount">
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn v-bind="attrs" v-on="on">
                    Auto
                  </v-btn>
                </template>
                <span>Auto-adjusts depending on resolution</span>
              </v-tooltip>
              <v-btn> 1 </v-btn>
              <v-btn> 2 </v-btn>
              <v-btn> 3 </v-btn>
              <v-btn> 4 </v-btn>
            </v-btn-toggle>
          </v-list-item>
          <v-list-item class="pt-10">
             <v-text-field label="Graph Height" v-model="options.graphHeight" outlined filled dense suffix="pixels" 
                           hint="Default: 250 pixels" type="number" />
          </v-list-item>
          <v-list-item>
            <v-switch label="Show Buttons" v-model="options.showButtons"></v-switch>
          </v-list-item>
        </v-list>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn text @click="clear()"> Cancel </v-btn>
          <v-btn color="primary" text @click="submit()"> Submit </v-btn>
        </v-card-actions>
      </v-card>
    </v-menu>
  </div>
</template>

<script>
export default {
  data: () => ({
    menu: false,
    oldOptions: null,
    options: {
      colCount: 0,
      graphHeight: 250,
      showButtons: true,
    }
    /* options: {
      colCount: null,
      graphHeight: null,
      showButtons: null,
    } */
  }),
  mounted() {
    /* if (this.options) {
      if (!this.options.colCount || !this.options.graphHeight || !this.options.showButtons) {
        let mgOps = this.$store.state.bluetooth.prefs.multiGraph
        this.colCount = mgOps.colCountIdx
        this.graphHeight = mgOps.graphHeight
        this.showButtons = mgOps.showButtons
      }
    } */
  },
  methods: {
    clear() {
      this.menu = false
      this.options = this.oldOptions
    },
    submit() {
      this.menu = false;
      this.$bus.$emit("SUBMIT_MULTIGRAPH_OPTIONS", this.options);
    },
  },
  watch: {
    menu(data) {
      if (data) {
        this.oldOptions = JSON.parse(JSON.stringify(this.options))
      }
    }
  }
};
</script>

<style lang="scss" scoped>
</style>
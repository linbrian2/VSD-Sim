<template>
  <v-dialog v-model="openDialog" width="unset" transition="scroll-x-transition" scrollable @click:outside="closeDialog">
    <v-card>
      <v-card-title>
        <v-icon class="mr-2">mdi-cog</v-icon>
        Preferences
        <div class="middle-header">
          <!-- <v-btn small outlined class="ml-10 mt-1" color="white" @click.stop="saveData">
            <v-icon left>mdi-content-save </v-icon>
            <span> Apply </span>
          </v-btn> -->
        </div>
        <v-btn icon class="close-button" @click="closeDialog">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text class="pr-0 pb-0">
        <!-- Title bar on the top -->
        <v-divider />
        <v-container style="width:900px">
          <v-row>
            <v-col class="pa-0" cols="12">
              <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Display</h3></v-subheader>
            </v-col>
            <v-col class="pa-0" cols="6" lg="5">
              <v-container>
                <v-select
                  outlined
                  :items="['Dark Mode', 'Light Mode']"
                  v-model="$store.state.dashboard.pref.theme"
                  @input="processDarkMode"
                  filled
                  label="Theme"
                  hide-details
                ></v-select>
              </v-container>
            </v-col>
          </v-row>

          <v-row>
            <v-col class="pa-0" cols="12">
              <v-divider class="mt-6" />
              <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Updates</h3></v-subheader>
            </v-col>
            <v-col class="pa-0" cols="6" lg="5">
              <v-container>
                <v-checkbox
                  v-model="$store.state.dashboard.pref.dataUpdateEnabled"
                  label="Enable Automatic Data Update"
                ></v-checkbox>
              </v-container>
            </v-col>
            <v-col class="pa-0" cols="6" offset-lg="1" lg="5">
              <v-text-field
                v-model="$store.state.dashboard.pref.dataUpdate"
                :rules="dataUpdateRules"
                label="Data update interval"
                suffix="min"
                type="number"
                dense
                filled
                hide-details
                required
              ></v-text-field>
            </v-col>
            <v-col class="pa-0" cols="6" lg="5">
              <v-container>
                <v-checkbox
                  v-model="$store.state.dashboard.pref.swapEnabled"
                  label="Enable Automatic Page Swaps"
                ></v-checkbox>
              </v-container>
            </v-col>
            <v-col class="pa-0" cols="6" offset-lg="1" lg="5">
              <v-text-field
                v-model="$store.state.dashboard.pref.swap"
                :rules="swapRules"
                label="Swap Interval"
                suffix="sec"
                type="number"
                dense
                filled
                hide-details
                required
              ></v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col class="pa-0" cols="12">
              <v-divider class="mt-6" />
              <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Layout</h3></v-subheader>
            </v-col>
            <v-col class="pa-0" cols="6" lg="5">
              <v-container>
                <v-checkbox
                  v-model="$store.state.dashboard.pref.resizableMap"
                  label="Enable Resizable Map (Overrides layout)"
                ></v-checkbox>
              </v-container>
            </v-col>
            <v-col class="pa-0" cols="6" offset-lg="1" lg="5">
              <v-container>
                <v-select
                  outlined
                  :items="layouts"
                  v-model="$store.state.dashboard.pref.layout"
                  filled
                  label="Page Layout"
                  hide-details
                ></v-select>
              </v-container>
            </v-col>
            <v-col class="pa-0" cols="6" lg="5">
              <v-container>
                <v-checkbox
                  v-model="$store.state.dashboard.pref.limitResults"
                  label="Limit Listings (Top 3 Results)"
                ></v-checkbox>
              </v-container>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    openDialog: {
      type: Boolean,
      dafult: false
    }
  },
  data() {
    return {
      layouts: ['1: Card, 2: Map, 3: Info', '1: Card, 2: Info, 3: Map'],
      search: '',
      menuItems: [{ title: 'Print JSON' }, { title: 'Download JSON' }],
      dataUpdateRules: [v => !!v || 'Interval is required', v => v >= 5 || 'Interval must be equal or larger than 5'],
      swapRules: [v => !!v || 'Interval is required', v => v >= 5 || 'Interval must be equal or larger than 5']
    };
  },
  methods: {
    closeDialog() {
      this.$emit('closeDialog');
    },
    processDarkMode(mode) {
      if (mode == 'Dark Mode') {
        this.$store.dispatch('saveDarkMode', true);
      } else {
        this.$store.dispatch('saveDarkMode', false);
      }
    }
  }
};
</script>

<style>
.middle-header {
  text-align: center;
  margin: 0 auto;
  padding-right: 120px;
}
</style>

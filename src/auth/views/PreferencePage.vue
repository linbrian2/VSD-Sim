<template>
  <div>
    <!-- Title bar on the top -->
    <v-toolbar dark color="#607C8A" dense flat fixed overflow extension-height="0">
      <v-toolbar-title>
        <v-btn icon class="ml-n2" @click="goBack">
          <v-icon dark>mdi-cog-outline</v-icon>
        </v-btn>
        Settings
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn small outlined class="mt-1" color="white" @click.stop="saveData">
        <v-icon left>mdi-content-save </v-icon>
        <span> Save </span>
      </v-btn>
      <!-- <v-btn small outlined class="mt-1 ml-3" color="white" @click.stop="restoreDefault">
        <v-icon left>mdi-lock-reset </v-icon>
        <span> Restore Default Settings </span>
      </v-btn> -->
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <v-btn icon dark @click="goBack"><v-icon>mdi-close</v-icon></v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <v-container style="width:1200px;">
      <v-tabs vertical class="tabs">
        <v-tab class="mt-4">
          <v-icon left>
            mdi-cog
          </v-icon>
          General
        </v-tab>
        <v-tab>
          <v-icon left>
            mdi-view-dashboard
          </v-icon>
          Main Dashboard
        </v-tab>
        <v-tab>
          <v-img class="mr-1" :src="getIcon('flowIcon')" max-width="30" />
          Traffic Flow Data
        </v-tab>
        <v-tab>
          <v-img class="mr-1" :src="getIcon('hrIcon')" max-width="30" />
          High Resolution Data
        </v-tab>
        <v-tab>
          <v-img class="mr-1" :src="getIcon('visionIcon')" max-width="30" />
          Machine Vision
        </v-tab>
        <v-tab>
          <v-img class="mr-1" :src="getIcon('cavIcon')" max-width="30" />
          CAV Data
        </v-tab>
        <v-tab>
          <v-img class="mr-1" :src="getIcon('healthIcon')" max-width="30" />
          System Monitoring
        </v-tab>
        <!-- General -->
        <v-tab-item>
          <v-row>
            <v-col cols="12">
              <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>General</h3></v-subheader>
              <v-divider />
            </v-col>
            <v-col cols="6">
              <v-container>
                <v-select
                  outlined
                  :items="['Dark Mode', 'Light Mode']"
                  v-model="theme"
                  filled
                  hide-details
                  label="Theme"
                ></v-select>
              </v-container>
            </v-col>
          </v-row>
        </v-tab-item>
        <!-- Main Dashboard -->
        <v-tab-item>
          <v-row>
            <v-col cols="12">
              <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Updates</h3></v-subheader>
              <v-divider />
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
            <v-col cols="12">
              <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Layout</h3></v-subheader>
              <v-divider />
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
        </v-tab-item>
        <!-- Traffic Flow Data -->
        <v-tab-item>
          <v-row>
            <v-col cols="12">
              <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
                ><h3>Incident Display</h3></v-subheader
              >
              <v-divider />
            </v-col>
            <v-col cols="6">
              <v-container>
                <v-text-field
                  v-model="severity"
                  :rules="severityRules"
                  label="Minimum severity to show"
                  type="number"
                  required
                ></v-text-field>
              </v-container>
            </v-col>
            <v-col cols="6">
              <v-container>
                <v-text-field
                  v-model="duration"
                  :rules="durationRules"
                  label="Minimum duration (minutes) to show"
                  type="number"
                  required
                ></v-text-field>
              </v-container>
            </v-col>
          </v-row>

          <v-row>
            <v-col cols="12">
              <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>UI</h3></v-subheader>
              <v-divider />
            </v-col>
            <v-col cols="12">
              <v-container
                ><v-checkbox v-model="savePanel" label="Save left map display panel position"></v-checkbox>
              </v-container>
            </v-col>
          </v-row>
        </v-tab-item>
        <!-- High Resolution Data -->
        <v-tab-item></v-tab-item>
        <!-- Machine Vision -->
        <v-tab-item></v-tab-item>
        <!-- CAV Data -->
        <v-tab-item></v-tab-item>
        <!-- System Monitoring -->
        <v-tab-item></v-tab-item>
      </v-tabs>
    </v-container>
  </div>
</template>

<script>
import { appIcons } from '@/mixins/appIcons';

export default {
  mixins: [appIcons],
  data() {
    return {
      loading: false,
      savePanel: false,
      theme: 'Dark',
      severity: 50,
      duration: 30,
      severityRules: [
        v => !!v || 'Severity is required',
        v => (v >= 0 && v < 100) || 'Severity must be between 0 and 100'
      ],
      durationRules: [v => !!v || 'Duration is required', v => v >= 0 || 'Duration must be equal or larger than 0']
    };
  },

  computed: {
    isMdi() {
      return this.icon.startsWith('mdi-');
    }
  },

  methods: {
    goBack() {
      this.$router.go(-1);
    },

    saveData() {
      let notifText = 'Settings saved.';
      this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
    },

    restoreDefault() {
      let notifText = 'Settings restored to default.';
      this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
    },

    getIcon(name) {
      return this.getAppIconByName(name, this.$store.state.darkMode);
    }
  }
};
</script>

<style lang="scss" scoped>
div.tabs [role='tab'] {
  justify-content: flex-start;
}

.title-name {
  background-color: hsl(200, 18%, 46%);
  padding: 5px 10px;
  margin-top: -8px;
  color: white;
  font-size: normal;
  font-weight: bold;
}

.non-selection {
  -moz-user-select: none;
  -khtml-user-select: none;
  -webkit-user-select: none;
  user-select: none;
}
</style>

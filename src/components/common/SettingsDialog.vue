<template>
  <v-dialog v-model="dialog" persistent width="1000px">
    <!-- Title bar on the top -->
    <v-toolbar dark color="indigo" dense flat fixed overflow extension-height="0">
      <v-toolbar-title>
        <v-btn icon class="ml-n2" @click="goBack">
          <v-icon dark>mdi-cog-outline</v-icon>
        </v-btn>
        Settings
      </v-toolbar-title>
      <v-spacer></v-spacer>

      <v-spacer></v-spacer>
      <v-toolbar-items>
        <v-btn icon dark @click="goBack"><v-icon>mdi-close</v-icon></v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <v-card>
      <v-toolbar dense>
        <v-tabs color="teal accent-4" v-model="tab">
          <v-tab v-for="category in settings" :key="category.id">
            <v-icon v-if="category.icon.includes('mdi')" left>{{ category.icon }}</v-icon>
            <v-img v-else class="mr-1" :src="getIcon(category.icon)" max-width="30" />
            {{ category.name }}
          </v-tab>
        </v-tabs>

        <v-spacer></v-spacer>

        <v-btn small outlined @click.stop="saveData">
          <v-icon left color="green">mdi-content-save </v-icon>
          <span> Save </span>
        </v-btn>
      </v-toolbar>

      <v-card-title>
        <v-tabs-items v-model="tab">
          <v-tab-item v-for="(category, key) in settings" :key="category.id" class="pl-10 pb-10">
            <v-row>
              <v-col cols="12" class="py-3" v-if="key == 'general'">
                <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
                  <h3>System</h3>
                </v-subheader>
                <v-divider class="mb-3" />
                <v-row>
                  <v-col cols="6">
                    <v-btn @click="defaultSettingsDialog = true">Restore Settings to Default</v-btn>
                  </v-col>

                  <v-col cols="12">
                    <WebPushSettings />
                  </v-col>
                </v-row>
              </v-col>

              <v-col
                class="py-0"
                :cols="setting.divider ? 12 : 4"
                v-for="setting in category.settings"
                :key="setting.id"
              >
                <template v-if="setting.divider">
                  <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
                    <h3>{{ camelCaseToWords(setting.title) }}</h3>
                  </v-subheader>
                  <v-divider class="mb-1" />
                </template>
                <v-checkbox
                  v-if="setting.type == 'boolean'"
                  v-model="setting.val"
                  :label="setting.label"
                  class="mr-10"
                ></v-checkbox>
                <v-select
                  v-else-if="setting.type == 'select' && setting.items"
                  v-model="setting.val"
                  :label="setting.label"
                  :items="setting.items"
                  class="mr-10 mb-1"
                  dense
                  filled
                  hide-details
                ></v-select>
                <v-text-field
                  v-else-if="setting.type == 'number'"
                  v-model.number="setting.val"
                  :label="setting.label"
                  :min="setting.min"
                  :max="setting.max"
                  class="mr-10 mb-1"
                  type="number"
                  dense
                  filled
                  hide-details
                ></v-text-field>
              </v-col>
            </v-row>
          </v-tab-item>
        </v-tabs-items>
      </v-card-title>
    </v-card>

    <v-dialog v-model="defaultSettingsDialog" width="unset" transition="scroll-x-transition" scrollable>
      <v-card>
        <v-card-title>
          Are you sure you want to restore the settings to default?
        </v-card-title>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="defaultSettingsDialog = false">
            Back
          </v-btn>
          <v-btn color="blue darken-1" text @click="restoreDefault">
            Confirm
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-dialog>
</template>

<script>
import Settings from '@/utils/AppSettings';
import { appIcons } from '@/mixins/appIcons';
import WebPushSettings from '@/components/common/WebPushSettings';

export default {
  mixins: [appIcons],
  components: {
    WebPushSettings
  },
  data() {
    return {
      dialog: false,
      defaultSettingsDialog: false,
      tab: null,
      settings: null,
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
    },
    appSettings() {
      return this.$store.state.settings;
    }
  },

  mounted() {
    if (!this.appSettings) {
      this.settings = Settings.getDefault();
    } else {
      this.settings = this.appSettings;
    }
    this.$bus.$on('TOGGLE_SETTINGS_DIALOG', toggle => {
      this.dialog = toggle;
    });
  },

  methods: {
    camelCaseToWords(str) {
      return str
        .match(/^[a-z]+|[A-Z][a-z]*/g)
        .map(function(x) {
          return x[0].toUpperCase() + x.substr(1).toLowerCase();
        })
        .join(' ');
    },
    goBack() {
      this.dialog = false;
    },

    saveData() {
      this.$store.dispatch('saveSettings', this.settings);
      let notifText = 'Settings saved.';
      this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
      this.dialog = false;
    },

    restoreDefault() {
      this.defaultSettingsDialog = false;
      this.settings = Settings.getDefault();
      this.$store.dispatch('saveSettings', this.settings);
      this.saveData();
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

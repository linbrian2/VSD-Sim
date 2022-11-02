<template>
  <v-dialog v-model="dialog" persistent width="1200px" :fullscreen="$vuetify.breakpoint.mobile">
    <!-- Title bar on the top -->
    <v-toolbar dark color="#607C8A" dense flat fixed overflow extension-height="0">
      <v-toolbar-title>
        <v-btn icon class="ml-n2" @click="goBack">
          <v-icon dark>mdi-cog-outline</v-icon>
        </v-btn>
        Settings
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn small class="mt-1" @click.stop="saveData">
        <v-icon left>mdi-content-save </v-icon>
        <span> Save </span>
      </v-btn>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <v-btn icon dark @click="goBack"><v-icon>mdi-close</v-icon></v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <v-card>
      <v-tabs vertical class="tabs" v-if="settings" style="overflow-x: hidden;">
        <template v-for="(category, key) in settings">
          <v-tab :key="category.id">
            <v-icon v-if="category.icon.includes('mdi')" left>{{ category.icon }}</v-icon>
            <v-img v-else class="mr-1" :src="getIcon(category.icon)" max-width="30" />
            <template v-if="!$vuetify.breakpoint.mobile">{{ category.name }}</template>
          </v-tab>
          <v-tab-item :key="category.id" :class="$vuetify.breakpoint.mobile ? 'pb-10 pr-2' : 'pl-10 pb-10'">
            <v-row>
              <v-col cols="12" class="py-3" v-if="key == 'general'">
                <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
                  <h3>System</h3>
                </v-subheader>
                <v-divider class="mb-3" />
                <v-btn @click="defaultSettingsDialog = true">Restore Settings to Default</v-btn>
              </v-col>
              <!-- <v-divider v-if="item.divider" :key="index"></v-divider> -->
              <v-col
                class="py-0"
                :cols="setting.divider || $vuetify.breakpoint.mobile ? 12 : 6"
                v-for="setting in category.settings"
                :key="setting.id"
              >
                <template v-if="setting.divider">
                  <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text">
                    <h3>{{ camelCaseToWords(setting.title) }}</h3>
                  </v-subheader>
                  <v-divider class="mb-3" />
                </template>
                <v-checkbox
                  v-if="setting.type == 'boolean'"
                  v-model="setting.val"
                  :label="setting.label"
                  :class="$vuetify.breakpoint.mobile ? null : 'mr-10'"
                ></v-checkbox>
                <v-select
                  v-else-if="setting.type == 'select' && setting.items"
                  v-model="setting.val"
                  :label="setting.label"
                  :items="setting.items"
                  :class="$vuetify.breakpoint.mobile ? 'mb-3' : 'mr-10 mb-3'"
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
                  class="mr-10 mb-3"
                  type="number"
                  dense
                  filled
                  hide-details
                ></v-text-field>
              </v-col>
            </v-row>
          </v-tab-item>
        </template>
      </v-tabs>
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

export default {
  mixins: [appIcons],
  data() {
    return {
      dialog: false,
      defaultSettingsDialog: false,
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

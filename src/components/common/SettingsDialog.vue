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
                  <v-col cols="6">
                    <v-btn :disabled="isNotificationBlocked" @click="allowWebPushNotifications">
                      <v-icon left :color="notificationIconColor" v-text="notificationIcon" />
                      {{ notificationLabel }}
                    </v-btn>

                    <v-tooltip bottom v-if="notificationStatus == 1">
                      <template v-slot:activator="{ on }">
                        <v-btn icon class="ml-1" v-on="on" @click.stop="testPushNotifications">
                          <v-icon color="gray">mdi-cursor-default-click-outline</v-icon>
                        </v-btn>
                      </template>
                      <span>Test push notifications</span>
                    </v-tooltip>
                  </v-col>
                  <v-col cols="12" v-show="alert">
                    <v-alert outlined dense color="cyan" border="left" elevation="2">
                      <v-row align="center">
                        <v-col class="grow">
                          Did you receive the web push notification yet?
                        </v-col>
                        <v-col class="shrink d-flex justify-space-between">
                          <v-btn outlined @click="confirmPushSuccess">YES</v-btn>
                          <v-btn outlined @click="confirmPushFailure" class="ml-4">NO</v-btn>
                        </v-col>
                      </v-row>
                    </v-alert>
                  </v-col>

                  <v-col cols="12" v-show="showInstruction">
                    <v-alert outlined dense>
                      <div class="caption grey--text">{{ instruction }}</div>
                    </v-alert>
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
import { mapState } from 'vuex';
import { subscribe, unsubscribe } from '@/utils/Notification';
import Api from '@/utils/api/auth';

export default {
  mixins: [appIcons],
  data() {
    return {
      dialog: false,
      defaultSettingsDialog: false,
      tab: null,
      alert: false,
      showInstruction: false,
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
      durationRules: [v => !!v || 'Duration is required', v => v >= 0 || 'Duration must be equal or larger than 0'],
      instruction: `If you are unable to see the web notifications on Windows, please open Settings > System >
                    Notifications & Actions. Turn on Get notifications from apps and other senders if not already. 
                    Under Get notifications from these senders, make sure your browser
                    (for example: Google Chrome) is on if it's visible.'
                    `
    };
  },

  computed: {
    isMdi() {
      return this.icon.startsWith('mdi-');
    },
    appSettings() {
      return this.$store.state.settings;
    },

    user() {
      return this.$store.state.auth.user;
    },

    isNotificationBlocked() {
      return Notification.permission === 'denied';
    },

    notificationState() {
      return this.notificationStatus == 1 ? true : false;
    },

    notificationIcon() {
      let icon = '';
      switch (this.notificationStatus) {
        case 0:
          icon = 'mdi-checkbox-blank-outline';
          break;
        case 1:
          icon = 'mdi-checkbox-marked-outline';
          break;
        case 2:
          icon = 'mdi-checkbox-blank-off-outline';
          break;
      }
      return icon;
    },

    notificationIconColor() {
      let color = '';
      switch (this.notificationStatus) {
        case 0:
          color = 'gray';
          break;
        case 1:
          color = 'green';
          break;
        case 2:
          color = '';
          break;
      }
      return color;
    },

    notificationLabel() {
      let text = '';
      let type = this.isNotificationBlocked ? 2 : this.notificationStatus;

      switch (type) {
        case 0:
          text = 'Click to subscribe web push notifications';
          break;
        case 1:
          text = 'Web push notifications subscribed';
          break;
        case 2:
          text = 'Web push notifications blocked by user';
          break;
      }

      return text;
    },

    ...mapState(['notificationStatus', 'subscription'])
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

    allowWebPushNotifications() {
      if (this.notificationStatus == 0) {
        subscribe(process.env.VUE_APP_PUBLIC_KEY, this.subscribeCallback);
      } else if (this.notificationStatus == 1) {
        unsubscribe(this.unsubscribeCallback);
      }
    },

    subscribeCallback(subscription) {
      this.$store.commit('SET_NOTIFICATION_SUB', subscription);
      const subscriptionObject = this.composeSubscriptionObject(subscription);
      this.updateSubscriptionOnServer(subscriptionObject, 0);
    },

    unsubscribeCallback(subscription) {
      this.$store.commit('SET_NOTIFICATION_SUB', null);
      const subscriptionObject = this.composeSubscriptionObject(subscription);
      this.updateSubscriptionOnServer(subscriptionObject, 1);
    },

    composeSubscriptionObject(subscription) {
      const sub = JSON.parse(JSON.stringify(subscription));
      return {
        email: this.user.email,
        agent: window.navigator.userAgent,
        key: sub.keys.p256dh,
        auth: sub.keys.auth,
        endpoint: sub.endpoint
      };
    },

    async updateSubscriptionOnServer(subscription, mode) {
      try {
        const res = await (mode === 0 ? Api.addSubscription(subscription) : Api.removeSubscription(subscription));
        if (res.status === 200) {
          console.log(res.data.message);
          this.$store.commit('SET_NOTIFICATION_STATUS', mode == 0 ? 1 : 0);
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async testPushNotifications() {
      if (this.subscription) {
        this.showInstruction = false;

        // Show the confirmation popup after 3 seconds

        const subscriptionObject = this.composeSubscriptionObject(this.subscription);
        try {
          const res = await Api.testSubscription(subscriptionObject);
          if (res.status === 200) {
            console.log(res.data.message);
            this.showMessageAlertWithDelay(3000, 20000);
          }
        } catch (error) {
          this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
        }
      }
    },

    showMessageAlertWithDelay(startDelay, endDelay) {
      setTimeout(() => {
        this.alert = true;
      }, startDelay);

      setTimeout(() => {
        this.alert = false;
      }, endDelay);
    },

    confirmPushSuccess() {
      this.alert = false;
    },

    confirmPushFailure() {
      this.alert = false;
      this.showInstruction = true;
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

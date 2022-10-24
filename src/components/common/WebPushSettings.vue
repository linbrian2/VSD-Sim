<template>
  <v-row>
    <v-col cols="12">
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
      <v-alert outlined dense class="mx-2">
        <div class="caption grey--text">{{ instruction }}</div>
      </v-alert>
    </v-col>
  </v-row>
</template>

<script>
import { mapState } from 'vuex';
import { subscribe, unsubscribe } from '@/utils/Notification';
import Api from '@/utils/api/auth';

export default {
  data() {
    return {
      alert: false,
      showInstruction: false,
      instruction: `If you are unable to see the web notifications on Windows, please open Settings > System >
                    Notifications & Actions. Turn on Get notifications from apps and other senders if not already. 
                    Under Get notifications from these senders, make sure your browser
                    (for example: Google Chrome) is on if it's visible.'
                    `
    };
  },

  computed: {
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

  methods: {
    allowWebPushNotifications() {
      this.alert = false;
      this.showInstruction = false;
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
    }
  }
};
</script>

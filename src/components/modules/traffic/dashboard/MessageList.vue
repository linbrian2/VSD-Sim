<template>
  <v-card tile class="notes" width="550">
    <v-toolbar tile flat dense height="40" color="">
      <v-subheader class="ml-n4">
        <v-icon small left color="white">mdi-message-text-outline</v-icon>
        <h3 class="overline">System Messages</h3>
      </v-subheader>
      <v-spacer />
      <v-btn icon small @click="handleClose"><v-icon>mdi-close</v-icon></v-btn>
    </v-toolbar>
    <v-divider />
    <v-card-text class="pa-0">
      <v-list class="pa-0 notes_list">
        <v-list-item-group>
          <template v-for="(item, index) in items">
            <v-list-item :key="index" @click="handleClick(item)">
              <v-list-item-avatar size="35" color="black">
                <v-avatar :color="item.color">
                  <v-icon dark>{{ item.icon }}</v-icon>
                </v-avatar>
              </v-list-item-avatar>
              <v-list-item-content>
                <v-list-item-title class="text-caption overline">{{ item.title }}</v-list-item-title>
                <v-list-item-subtitle class="font-weight-light overline green--text">{{
                  item.message
                }}</v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-action class="caption">
                <v-tooltip top>
                  <template v-slot:activator="{ on }">
                    <span v-on="on">{{ item.time | fromNow }}</span>
                  </template>
                  <span>{{ item.time | formatTimestamp }}</span>
                </v-tooltip>
              </v-list-item-action>
            </v-list-item>
            <v-divider :key="`${index}-divider`" />
          </template>
        </v-list-item-group>
      </v-list>
      <v-divider></v-divider>
      <div class="d-flex justify-space-between align-center ml-5 mr-5">
        <div class="grey-text text-caption">Total {{ items.length }} messages</div>
        <div>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="handleClearAll">
                <v-icon color="red lighten-3">mdi-notification-clear-all</v-icon>
              </v-btn>
            </template>
            <span>Clear All</span>
          </v-tooltip>

          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-btn small icon v-on="on" @click.stop="handleShowAll" class="ml-3">
                <v-icon color="grey">mdi-eye-arrow-right-outline</v-icon>
              </v-btn>
            </template>
            <span>View All</span>
          </v-tooltip>
        </div>
      </div>
      <v-divider></v-divider>
    </v-card-text>
  </v-card>
</template>

<script>
import Constants from '@/utils/constants/traffic.js';
import { RouterNames } from '@/utils/constants/router';
import Utils from '@/utils/Utils';

export default {
  props: {
    items: {
      type: Array,
      default: () => []
    }
  },

  data() {
    return {};
  },

  filters: {
    formatTimestamp: function(date) {
      if (date) {
        return Utils.formatDateTime(date);
      } else {
        return '';
      }
    },
    fromNow: function(date) {
      return Utils.fromNow(date);
    }
  },

  methods: {
    handleClick(item) {
      if (this.$route.name !== RouterNames.TRAFFIC_DASHBOARD) {
        this.$router.push({ path: '/' }).catch(() => {});
        setTimeout(() => {
          this.showMessage(item);
        }, 500);
      } else {
        this.showMessage(item);
      }
    },

    showMessage(message) {
      let type = 0;
      switch (message.type) {
        case Constants.DATA_TRAFFIC_FLOW:
          type = Constants.LAYER_DEVICE_TRAFFIC;
          break;
        case Constants.DATA_TRAVEL_TIME:
          type = Constants.LAYER_DEVICE_BLUETOOTH;
          break;
        case Constants.DATA_WAZE_ALERTS:
          type = Constants.LAYER_DEVICE_WAZE_ALERTS;
          break;
        case Constants.DATA_RESTRICTION:
          type = Constants.LAYER_DEVICE_RESTRICTIONS;
          break;
        case Constants.DATA_WEATHER:
          type = Constants.LAYER_DEVICE_WEATHER;
          break;
        case Constants.DATA_TRAFFIC_INCIDENT:
          type = Constants.LAYER_DEVICE_INCIDENTS;
          break;
      }

      if (message.id) {
        this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id: message.id, type, trigger: 1 });
      }
    },

    handleClose() {
      this.$emit('close');
    },

    handleClearAll() {
      this.$store.dispatch('traffic/clearMessageQueue');
    },

    handleShowAll() {
      // if (this.$route.name !== RouterNames.TRAFFIC_INCIDENT_LIST) {
      //   this.$router.push({ path: RouterPaths.TRAFFIC_INCIDENT_LIST }).catch(() => {});
      // }
    }
  }
};
</script>
<style lang="scss" scoped>
.notes_list {
  max-height: 360px;
  overflow-y: auto;
}
</style>

<template>
  <v-card tile class="notes" max-width="600">
    <v-toolbar tile flat>
      <v-subheader><h3 class="font-weight-bold overline">Incidents</h3></v-subheader>
      <v-spacer />
      <!-- <v-btn text @click="handleClearNotification">close</v-btn> -->
      <v-btn icon small @click="handleClearNotification" class="mr-2"><v-icon>mdi-close</v-icon></v-btn>
    </v-toolbar>
    <v-divider />
    <v-card-text class="pa-0">
      <v-list dense class="pa-0 notes_list">
        <template v-for="(item, index) in items">
          <v-list-item :key="index" @click="handleClick(item.id)">
            <v-list-item-avatar size="40" color="black">
              <v-progress-circular :size="38" :width="4" :value="100" :color="item.severityColor">
                <span class="score-value"> {{ item.incidentScore }}</span>
              </v-progress-circular>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title color="red" v-html="item.title"></v-list-item-title>
            </v-list-item-content>
            <v-list-item-action class="caption">
              <v-tooltip top>
                <template v-slot:activator="{ on }">
                  <span v-on="on">{{ item.startTime | fromNow }}</span>
                </template>
                <span>{{ item.startTime | fromatTimestamp }}</span>
              </v-tooltip>
            </v-list-item-action>
          </v-list-item>
          <v-divider :key="'d' + index" />
        </template>
      </v-list>
      <v-divider></v-divider>
      <v-btn block text class="ma-0" @click="handleNotificationAll">All</v-btn>
      <v-divider></v-divider>
    </v-card-text>
  </v-card>
</template>

<script>
import Constants from '@/utils/constants/traffic';
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
    fromatTimestamp: function(date) {
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
    handleClick(id) {
      if (this.$store.state.traffic.currentAction !== Constants.PAGE_DASHBOARD) {
        this.$store.commit('traffic/SET_CURRENT_ACTION', Constants.PAGE_DASHBOARD);
        this.$router.push({ path: '/' }).catch(() => {});
        setTimeout(() => {
          this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id, type: 4 });
        }, 500);
      } else {
        this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id, type: 4 });
      }
    },

    handleClearNotification() {
      this.$store.dispatch('traffic/clearNotificaton');
    },

    handleNotificationAll() {
      if (!Utils.isToday(this.$store.state.traffic.currentDate)) {
        this.$store.commit('traffic/SET_CURRENT_DATE', new Date());
      }

      if (this.$store.state.traffic.currentAction !== Constants.PAGE_INCIDENTS) {
        this.$store.commit('traffic/SET_CURRENT_ACTION', Constants.PAGE_INCIDENTS);
        this.$router.push({ path: '/incidents' }).catch(() => {});
      }
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

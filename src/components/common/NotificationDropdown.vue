<template>
  <!-- notification -->
  <v-menu offset-y origin="center center" class="elelvation-1" transition="scale-transition">
    <template #activator="{ on }">
      <v-btn slot="activator" icon text v-on="on">
        <div v-if="getNotification.length > 0">
          <v-badge color="red" overlap>
            <span slot="badge">{{ getNotification.length }}</span>
            <v-icon medium>mdi-bell</v-icon>
          </v-badge>
        </div>
        <div v-else>
          <v-icon medium>mdi-bell-outline</v-icon>
        </div>
      </v-btn>
    </template>
    <IncidentNotificationList v-show="getNotification.length > 0" :items="getNotification" />
  </v-menu>
</template>

<script>
import IncidentNotificationList from '@/components/modules/traffic/dashboard/IncidentNotificationList';
import { mapGetters } from 'vuex';

export default {
  components: {
    IncidentNotificationList
  },

  computed: {
    ...mapGetters('traffic', ['getNotification'])
  }
};
</script>

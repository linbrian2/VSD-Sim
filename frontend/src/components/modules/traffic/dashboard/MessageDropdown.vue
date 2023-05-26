<template>
  <!-- messages -->
  <v-menu offset-y origin="center center" class="elevation-1" transition="scroll-y-transition" v-model="menu">
    <template v-slot:activator="{ on: menu, attrs }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on: tooltip }">
          <v-btn icon text v-bind="attrs" v-on="{ ...tooltip, ...menu }">
            <div v-if="getMessages.length > 0">
              <v-badge color="purple" overlap offset-y="14" max="20">
                <span slot="badge">{{ getMessages.length }}</span>
                <v-icon medium>mdi-message-text-outline</v-icon>
              </v-badge>
            </div>
            <v-icon v-else medium>mdi-message-text-outline</v-icon>
          </v-btn>
        </template>
        <span>{{ getMessages.length }} messages</span>
      </v-tooltip>
    </template>

    <MessageList v-show="getMessages.length > 0" :items="getMessages" @close="menu = false" />
  </v-menu>
</template>

<script>
import MessageList from '@/components/modules/traffic/dashboard/MessageList';
import { mapGetters } from 'vuex';

export default {
  components: {
    MessageList
  },

  data: () => ({
    menu: false
  }),

  computed: {
    ...mapGetters('traffic', ['getMessages'])
  }
};
</script>

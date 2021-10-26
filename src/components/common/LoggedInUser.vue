<template>
  <v-menu>
    <template v-slot:activator="{ on }">
      <v-btn x-small :color="color" depressed fab v-on="on" class="white--text ml-5">
        <v-avatar v-if="user.photoUrl">
          <img :src="user.photoUrl" :alt="`${user.name} avatar`" />
        </v-avatar>
        <template v-else>
          <span class="title">
            {{ firstLetter }}
          </span>
        </template>
      </v-btn>
    </template>

    <v-card>
      <v-list>
        <v-list-item>
          <v-list-item-avatar>
            <v-avatar v-if="user.photoUrl">
              <img :src="user.photoUrl" :alt="`${user.name} avatar`" />
            </v-avatar>
            <template v-else>
              <v-avatar :color="color">
                <span class="white--text headline "> {{ firstLetter }}</span>
              </v-avatar>
            </template>
          </v-list-item-avatar>

          <v-list-item-content>
            <v-list-item-title class="text-capitalize">{{ name }}</v-list-item-title>
            <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>

      <v-divider></v-divider>

      <v-list>
        <v-list-item @click="changePassword">
          <v-list-item-icon>
            <v-icon>mdi-account-box</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Change Password</v-list-item-title>
        </v-list-item>
        <v-list-item @click="preferences">
          <v-list-item-icon>
            <v-icon>mdi-cog-outline</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Preferences</v-list-item-title>
        </v-list-item>
        <v-list-item @click="logout">
          <v-list-item-icon>
            <v-icon>mdi-login-variant</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Sign out</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-card>
  </v-menu>
</template>

<script>
import { RouterPaths } from '@/utils/constants/router';
export default {
  props: {
    user: Object
  },

  computed: {
    name() {
      return `${this.user.firstName} ${this.user.lastName}`;
    },

    firstLetter() {
      return this.user && this.user.firstName && this.user.firstName[0].toUpperCase();
    },

    color() {
      const colors = [
        '#F44336',
        '#E91E63',
        '#9C27B0',
        '#673AB7',
        '#3F51B5',
        '#2196F3',
        '#03A9F4',
        '#00BCD4',
        '#009688',
        '#4CAF50',
        '#8BC34A',
        '#CDDC39',
        '#FFEB3B',
        '#FFC107',
        '#FF9800',
        '#FF5722',
        '#795548',
        '#9E9E9E',
        '#b91d47'
      ];
      const s = this.firstLetter;
      const index = s ? (s.charCodeAt(0) - 65) % colors.length : 0;
      return colors[index];
    }
  },

  methods: {
    changePassword() {
      this.$router.push({ path: RouterPaths.AUTH_CHANGE_PASSWORD }).catch(() => {});
    },

    preferences() {
      this.$router.push({ path: RouterPaths.AUTH_PREFERENCES }).catch(() => {});
    },

    logout() {
      this.$store.dispatch('auth/logout');
    }
  }
};
</script>

<style lang="scss">
.v-list-item__avatar {
  justify-content: center !important;
}
</style>

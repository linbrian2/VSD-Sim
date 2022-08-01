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
        '#9c27b0',
        '#673ab7',
        '#3f51b5',
        '#2196f3',
        '#03a9f4',
        '#00bcd4',
        '#009688',
        '#4caf50',
        '#8bc34a',
        '#cddc39',
        '#ffeb3b',
        '#ffc107',
        '#ff9800',
        '#795548',
        '#9e9e9e',
        '#607d8b',
        '#101010'
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
      this.$bus.$emit('TOGGLE_SETTINGS_DIALOG', true);
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

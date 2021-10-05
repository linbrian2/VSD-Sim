<template>
  <v-menu>
    <template v-slot:activator="{ on }">
      <v-btn x-small color="blue" depressed fab v-on="on" class="white--text ml-5">
        <v-avatar v-if="user.photoUrl">
          <img :src="user.photoUrl" :alt="`${user.name} avatar`" />
        </v-avatar>
        <template v-else>
          <span class="title">
            {{ user.name.split('')[0].toUpperCase() }}
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
              <v-avatar color="blue">
                <span class="white--text headline "> {{ user.name.split('')[0].toUpperCase() }}</span>
              </v-avatar>
            </template>
          </v-list-item-avatar>

          <v-list-item-content>
            <v-list-item-title class="text-capitalize">{{ user.name }}</v-list-item-title>
            <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>

      <v-divider></v-divider>

      <v-list>
        <v-list-item>
          <v-list-item-icon>
            <v-icon>mdi-account-box</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Preferences</v-list-item-title>
        </v-list-item>
        <v-list-item href="/logout">
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
export default {
  props: ['user']
};
</script>

<style lang="scss">
.v-list-item__avatar {
  justify-content: center !important;
}
</style>

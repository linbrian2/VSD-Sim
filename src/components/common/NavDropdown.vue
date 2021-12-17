<template>
  <v-menu bottom right offset-y>
    <template v-slot:activator="{ on, attrs }">
      <v-btn small icon class="mr-1" v-bind="attrs" v-on="on">
        <v-icon color="blue">mdi-chevron-right </v-icon>
      </v-btn>
    </template>

    <v-list>
      <template v-for="(item, i) in menuItems">
        <v-divider v-if="item.divider" :key="i"></v-divider>
        <v-list-item v-else :key="i" @click="changeApp(item.url)">
          <v-list-item-title :class="{ 'font-weight-bold': item.title === title }">
            <v-icon class="mr-1" v-if="item.title === title">mdi-check</v-icon>
            <span :class="{ 'ml-8': item.title !== title }"> {{ item.title }} </span>
          </v-list-item-title>
        </v-list-item>
      </template>
    </v-list>
  </v-menu>
</template>

<script>
import { RouterPaths } from '@/utils/constants/router';
import AppConstants from '@/utils/constants/app';
export default {
  props: {
    title: String,
    items: Array
  },

  data() {
    return {
      app_menu_items: [
        { title: AppConstants.TRAFFIC_APP_TITLE, url: RouterPaths.TRAFFIC_DASHBOARD },
        { title: AppConstants.HR_APP_TITLE, url: RouterPaths.HR_DASHBOARD },
        { title: AppConstants.VISION_APP_TITLE, url: RouterPaths.VISION_DASHBOARD },
        /* { title: AppConstants.BLUETOOTH_APP_TITLE, url: RouterPaths.BLUETOOTH_DASHBOARD }, */
        { title: AppConstants.CAV_APP_TITLE, url: RouterPaths.CAV_DASHBOARD },
        { title: AppConstants.STATUS_APP_TITLE, url: RouterPaths.STATUS_DASHBOARD }
      ]
    };
  },

  computed: {
    menuItems() {
      return this.items ? this.items : this.app_menu_items;
    }
  },

  methods: {
    changeApp(appURL) {
      if (appURL != this.$route.path) {
        this.$router.push({ path: appURL }).catch(() => {});
      }
    }
  }
};
</script>

<style></style>

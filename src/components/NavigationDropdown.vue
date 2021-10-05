<template>
  <v-menu bottom right offset-y>
    <template v-slot:activator="{ on, attrs }">
      <v-btn icon v-bind="attrs" v-on="on">
        <v-icon>mdi-view-grid-outline</v-icon>
      </v-btn>
    </template>

    <v-list>
      <v-list-item v-for="(item, i) in app_menu_items" :key="i" @click="changeApp(item.url)">
        <v-list-item-title :class="{ 'font-weight-bold': item.title === title }"> {{ item.title }}</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
export default {
  props: ['title'],
  data() {
    return {
      app_menu_items: [
        { title: 'Traffic Flow Data', url: '/flow' },
        { title: 'High Resolution Data', url: '/hr' },
        { title: 'Machine Vision on Traffic Cameras', url: '/vision' },
        { title: 'Bluetooth and Waze Data', url: '/bluetooth' },
        { title: 'CAV Data', url: '/cav' },
        { title: 'Health Monitoring', url: '/status' },
      ],
    };
  },
  methods: {
    changeApp(appURL) {
      console.log(appURL);
      if (appURL != this.$route.path)
      this.$router.push({ path: appURL }).catch(() => {});
    },
  },
};
</script>

<style>
</style>
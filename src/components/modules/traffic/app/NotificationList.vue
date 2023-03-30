<template>
  <v-card tile class="notes">
    <v-toolbar tile flat>
      <v-subheader><h3 class="font-weight-bold overline">Incidents</h3></v-subheader>
      <v-spacer />
      <v-btn text @click="handleClearNotification">clear</v-btn>
    </v-toolbar>
    <v-divider />
    <v-card-text class="pa-0">
      <v-list dense class="pa-0 notes_list">
        <template v-for="(item, index) in items">
          <v-list-item :key="index" @click="handleClick(item.id)">
            <v-list-item-avatar size="25" :color="item.color">
              <v-icon dark>{{ item.icon }}</v-icon>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-subtitle v-html="item.title"></v-list-item-subtitle>
            </v-list-item-content>
            <v-list-item-action class="caption">
              {{ item.timeLabel }}
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
  methods: {
    handleClick(e) {
      console.log(e);
    },

    handleClearNotification() {
      this.$store.dispatch('traffic/clearNotificaton');
    },

    handleNotificationAll() {}
  }
};
</script>
<style lang="scss" scoped>
.notes_list {
  max-height: 360px;
  overflow-y: auto;
}
</style>

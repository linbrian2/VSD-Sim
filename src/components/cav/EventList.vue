<template>
  <div class="my-1 mx-3">
    <v-data-table
      disable-sort
      :headers="headers"
      :height="height"
      fixed-header
      :items="events"
      :items-per-page="itemsPerPage"
      hide-default-footer
      :loading="loading"
      loading-text="Loading... Please wait"
    >
      <template v-slot:[`item.time`]="{ item }">
        <div>{{ item.time | fromatTimestamp }}</div>
      </template>
      <template v-slot:[`item.type`]="{ item }">
        <v-icon color="blue-grey">{{ icon(item.type) }}</v-icon>
      </template>
      <template v-slot:[`item.video`]="{ item }">
        <v-btn icon @click="videoClick(item)"><v-icon color="green">mdi-video-box</v-icon></v-btn>
      </template>
    </v-data-table>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: ['header', 'height', 'events', 'func'],
  data: () => ({
    loading: false,
    itemsPerPage: 200,
    defaultHeaders: [
      { text: 'Time', value: 'time' },
      { text: 'Message', value: 'data' },
      { text: 'Type', value: 'type' },
      { text: 'Video', value: 'video' }
    ]
  }),
  computed: {
    headers() {
      return this.header || this.defaultHeaders;
    }
  },
  filters: {
    fromatTimestamp: function(timestamp) {
      if (timestamp) {
        return Utils.formatDateAndTime(new Date(timestamp));
      } else {
        return '';
      }
    }
  },
  methods: {
    icon(type) {
      return type == 1 ? 'mdi-barcode' : 'mdi-bolnisi-cross';
    },
    videoClick(item) {
      if (this.func) {
        this.func(item);
      }
    }
  }
};
</script>

<style lang="scss">
.v-data-table > .v-data-table__wrapper > table > tbody > tr > td {
  font-size: 12px;
}
</style>

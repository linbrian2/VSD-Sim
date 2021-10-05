<template>
  <div class="my-1 mx-3">
    <v-data-table
      dense
      disable-sort
      :headers="headers"
      fixed-header
      :items="items"
      :items-per-page="itemsPerPage"
      hide-default-header
      hide-default-footer
      :loading="loading"
      loading-text="Loading... Please wait"
    >
      <template v-slot:[`item.item`]="{ item }">
        <div>
          <strong>{{ item.item }}:</strong>
        </div>
      </template>
    </v-data-table>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    header: Object,
    data: Object,
    height: Number
  },

  data: () => ({
    loading: false,
    itemsPerPage: 200,
    defaultHeaders: [
      { text: 'Item', value: 'item' },
      { text: 'value', value: 'value' }
    ]
  }),

  computed: {
    headers() {
      return this.header || this.defaultHeaders;
    },

    items() {
      return Object.keys(this.data).map(key => ({ item: Utils.toSentenceCase(key), value: this.data[key] }));
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
  }
};
</script>

<style lang="scss">
.v-data-table > .v-data-table__wrapper > table > tbody > tr > td {
  font-size: 14px !important;
}

.v-data-table > .v-data-table__wrapper > table > tbody > tr > th,
.v-data-table > .v-data-table__wrapper > table > thead > tr > th,
.v-data-table > .v-data-table__wrapper > table > tfoot > tr > th {
  font-size: 14px !important;
}
</style>

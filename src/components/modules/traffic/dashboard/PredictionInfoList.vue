<template>
  <div>
    <v-data-table
      dense
      disable-sort
      :headers="headers"
      fixed-header
      :items="data"
      :items-per-page="itemsPerPage"
      hide-default-footer
      :loading="loading"
      loading-text="Loading..."
    >
    </v-data-table>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    header: Array,
    data: Array,
    height: Number
  },

  data: () => ({
    loading: false,
    itemsPerPage: 4,
    defaultHeaders: [
      { text: 'Flow', value: 'item', class: 'teal white--text' },
      { text: 'Pred5', value: 'pred5', class: 'teal white--text' },
      { text: 'pred10', value: 'pred10', class: 'teal white--text' },
      { text: 'pred15', value: 'pred15', class: 'teal white--text' },
      { text: 'pred30', value: 'pred30', class: 'teal white--text' }
    ]
  }),

  computed: {
    headers() {
      if (this.header) {
        const result = [];
        for (let i = 0; i < this.header.length; i++) {
          const item = { ...this.defaultHeaders[i], ...this.header[i] };
          result.push(item);
        }
        return result;
      } else {
        return this.defaultHeaders;
      }
    }
  },

  filters: {
    formatTimestamp: function(timestamp) {
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
  font-size: 16px !important;
}

.v-data-table > .v-data-table__wrapper > table > tbody > tr > th,
.v-data-table > .v-data-table__wrapper > table > thead > tr > th,
.v-data-table > .v-data-table__wrapper > table > tfoot > tr > th {
  font-size: 14px !important;
}
</style>

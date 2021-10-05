<template>
  <v-data-table
    disable-sort
    :headers="headers"
    :items="summary"
    :items-per-page="15"
    hide-default-footer
    :loading="loading"
    loading-text="Loading... Please wait"
  >
    <template v-slot:[`item.type`]="{ item }">
      <div class="service-type">{{ item.type }}</div>
    </template>
    <template v-slot:[`item.total`]="{ item }">
      <div>{{ item.total | formatNumber }}</div>
    </template>
    <template v-slot:[`item.collected`]="{ item }">
      <div>{{ item.collected | formatNumber }}</div>
    </template>
    <template v-slot:[`item.status`]="{ item }">
      <v-chip outlined small :color="controlColor(item.isLive)" style="width:60px;" class="chip-text">{{
        item.status
      }}</v-chip>
    </template>
    <template v-slot:[`item.lastUpdateTime`]="{ item }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <span v-on="on">{{ item.lastUpdateTime | fromNow }}</span>
        </template>
        <span>{{ item.lastUpdateTime | fromatTimestamp }}</span>
      </v-tooltip>
    </template>
  </v-data-table>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: ['loading', 'summary'],
  data: () => ({
    headers: [
      { text: 'Type', value: 'type' },
      { text: 'Interval', value: 'interval' },
      { text: 'Total collected', align: 'start', value: 'total' },
      { text: 'Today collected', align: 'start', value: 'collected' },
      { text: 'Last update', value: 'lastUpdateTime' },
      { text: 'Status', value: 'status' }
    ]
  }),
  filters: {
    fromatTimestamp: function(timestamp) {
      if (timestamp) {
        return Utils.formatDateAndTime(new Date(timestamp));
      } else {
        return '';
      }
    },
    fromNow: function(date) {
      return Utils.fromNow(date);
    },
    formatNumber: function(num) {
      return Utils.numberWithCommas(num);
    }
  },

  methods: {
    controlColor(isLive) {
      return isLive > 0 ? 'green' : 'red';
    }
  }
};
</script>

<style lang="scss" scoped>
.service-type {
  font-size: 18;
  font-weight: bold;
}

.clip-text {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>

<template>
  <div class="detectors">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemsPerPage"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
    >
      <template v-slot:[`item.status`]="{ item }">
        <v-chip small :color="'red'"
          ><strong class="black--text">{{ item.status }}</strong></v-chip
        >
      </template>
    </v-data-table>
    <!-- Data Display -->
    <v-card class="my-2" min-height="36.9vh">
      <v-container grid-center>
        <BasicChart :data="lineData" :height="400" :width="560" :showTitle="true" />
      </v-container>
    </v-card>
    <v-card min-height="36.9vh">
      <v-container grid-center>
        <BasicChart :data="lineData2" :height="400" :width="560" :showTitle="true" />
      </v-container>
    </v-card>
  </div>
</template>

<script>
import BasicChart from '@/components/modules/dashboard/graphs/BasicChart';
import Utils from '@/utils/Utils';
import lineData from '@/utils/LineChartSampleData2';
import lineData2 from '@/utils/LineChartSampleData3';

export default {
  props: {
    height: { type: Number, default: 200 },
    data: Object
  },
  components: {
    BasicChart
  },
  data: () => ({
    itemsPerPage: 3,
    lineData: lineData,
    lineData2: lineData2,
    headers: [
      { text: 'Device Id', value: 'id' },
      { text: 'Name', value: 'name' },
      { text: 'Level', value: 'level' },
      { text: 'Type', value: 'type' },
      { text: 'County', value: 'county' },
      { text: 'Station Count', value: 'stationCount' }
    ],
    items: [
      {
        id: '18115',
        name: 'DE 92 @ Ridge Rd',
        level: 5,
        type: 'Signal Intersection',
        county: 'New Castle',
        stationCount: 2
      },
      {
        id: '19872',
        name: 'US 113 @ Centerview Dr',
        level: 5,
        type: 'Signal Intersection',
        county: 'Sussex',
        stationCount: 2
      },
      {
        id: '18098',
        name: 'DE 92 @ Foulk Rd',
        level: 5,
        type: 'Signal Intersection',
        county: 'New Castle',
        stationCount: 4
      }
    ],
    selectedId: null
  }),

  filters: {
    time(time) {
      const d = new Date(time);
      const dd = Utils.formatDate(d);
      const tt = Utils.formatTimeAsMinute(d);
      return `${dd} ${tt}`;
    }
  },

  mounted() {
    this.handleRowClick(this.items[0]);
  },

  methods: {
    itemRowBackground(item) {
      return item.id == this.selectedId ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedId = item.id;
      this.$emit('click', item);
    }
  }
};
</script>

<style lang="scss"></style>

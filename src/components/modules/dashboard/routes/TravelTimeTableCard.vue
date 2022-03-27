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
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    height: { type: Number, default: 200 },
    data: Object
  },

  data: () => ({
    itemsPerPage: 3,
    headers: [
      { text: 'Name', value: 'name' },
      { text: 'Current Travel Time (s)', value: 'curr' },
      { text: 'Baseline Travel Time (s)', value: 'baseline' },
      { text: 'Exceed Percentage', value: 'exceed' }
    ],
    items: [
      {
        name: 'I-495',
        curr: 75.5,
        baseline: 105,
        exceed: '+39%'
      },
      {
        name: 'I-95',
        curr: 170.5,
        baseline: 138,
        exceed: '+23%'
      },
      {
        name: 'I-295',
        curr: 200,
        baseline: 164,
        exceed: '+21%'
      }
    ],
    selectedName: null
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
      return item.name == this.selectedName ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedName = item.name;
      this.$emit('click', item);
    }
  }
};
</script>

<style lang="scss"></style>

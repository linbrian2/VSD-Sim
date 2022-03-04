<template>
  <v-data-table
    fixed-header
    :headers="headers"
    :items="items"
    :items-per-page="itemsPerPage"
    hide-default-footer
    class="elevation-1"
  >
    <template v-slot:[`item.permitNumber`]="{ item }">
      <v-chip color="green" outlined small>
        <span class="white--text">{{ item.permitNumber }}</span>
      </v-chip>
    </template>

    <template v-slot:[`item.los`]="{ item }">
      <v-chip :color="getColor(item.los)" label small>
        <span class="black--text">{{ item.los }}</span>
      </v-chip>
    </template>
  </v-data-table>
</template>

<script>
export default {
  props: {
    items: Array
  },

  data: () => ({
    itemsPerPage: 500,
    headers: [
      { text: 'Permit', value: 'permitNumber', sortable: false, class: 'indigo' },
      { text: 'Offset', value: 'offset', sortable: false, class: 'indigo' },
      { text: 'LOS', value: 'los', sortable: false, class: 'indigo' },
      { text: 'Delay', value: 'avgDelay', sortable: false, class: 'indigo', divider: true },
      { text: 'P1', value: 'P1', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P2', value: 'P2', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P3', value: 'P3', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P4', value: 'P4', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P5', value: 'P5', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P6', value: 'P6', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P7', value: 'P7', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P8', value: 'P8', sortable: false, class: 'indigo darken-3', divider: true }
    ]
  }),

  methods: {
    getColor(c) {
      const colors = {
        A: 'rgb(0,255,0)',
        B: 'rgb(102,255,0)',
        C: 'rgb(204,255,0)',
        D: 'rgb(255,204,0)',
        E: 'rgb(255,102,0)',
        F: 'rgb(255,0,0)'
      };
      return c in colors ? colors[c] : 'green';
    }
  }
};
</script>

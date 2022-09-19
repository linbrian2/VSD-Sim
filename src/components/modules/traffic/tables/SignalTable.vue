<template>
  <v-data-table
    fixed-header
    :headers="headers"
    :items="items"
    :items-per-page="itemsPerPage"
    hide-default-footer
    class="elevation-1"
  >
    <template v-slot:[`item.pattern`]="{ item }">
      <v-chip color="orange" label small>
        <span class="font-weight-bold black--text">{{ item.pattern }}</span>
      </v-chip>
    </template>

    <template v-slot:[`item.P1`]="{ item }">
      <span :class="cc(item.M1)">{{ item.P1 }}</span>
    </template>
    <template v-slot:[`item.P2`]="{ item }">
      <span :class="cc(item.M2)">{{ item.P2 }}</span>
    </template>
    <template v-slot:[`item.P3`]="{ item }">
      <span :class="cc(item.M3)">{{ item.P3 }}</span>
    </template>
    <template v-slot:[`item.P4`]="{ item }">
      <span :class="cc(item.M4)">{{ item.P4 }}</span>
    </template>
    <template v-slot:[`item.P5`]="{ item }">
      <span :class="cc(item.M5)">{{ item.P5 }}</span>
    </template>
    <template v-slot:[`item.P6`]="{ item }">
      <span :class="cc(item.M6)">{{ item.P6 }}</span>
    </template>
    <template v-slot:[`item.P7`]="{ item }">
      <span :class="cc(item.M7)">{{ item.P7 }}</span>
    </template>
    <template v-slot:[`item.P8`]="{ item }">
      <span :class="cc(item.M8)">{{ item.P8 }}</span>
    </template>
  </v-data-table>
</template>

<script>
export default {
  props: {
    items: Array
  },

  data: () => ({
    itemsPerPage: 100,
    headers: [
      { text: 'Pattern', value: 'pattern', sortable: false, class: 'indigo' },
      { text: 'Cycle', value: 'cycle', sortable: false, class: 'indigo' },
      { text: 'Offset', value: 'offset', sortable: false, class: 'indigo', divider: true },
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
    cc(mode) {
      return mode && mode > 0 ? 'red--text' : 'white--text';
    }
  }
};
</script>

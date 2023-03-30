<template>
  <v-data-table
    fixed-header
    :headers="headers"
    :items="items"
    :items-per-page="itemsPerPage"
    hide-default-footer
    class="elevation-1"
  >
    <template v-slot:[`item.permit`]="{ item }">
      <div class="d-flex align-center">
        <v-tooltip top color="orange" v-if="isCritical(item)">
          <template v-slot:activator="{ on }">
            <span v-on="on">
              <v-icon color="green" class="mr-2">mdi-star-outline</v-icon>
            </span>
          </template>
          <span>Critical Intersection</span>
        </v-tooltip>
        <div :class="{ 'ml-8': !isCritical(item) }">
          {{ item.permit }}
        </div>
      </div>
    </template>
  </v-data-table>
</template>

<script>
export default {
  props: {
    items: Array
  },

  computed: {
    ctriticalItem() {
      return this.items.reduce((prev, current) => {
        return prev.saturationDegree > current.saturationDegree ? prev : current;
      });
    }
  },

  data: () => ({
    itemsPerPage: 5,
    headers: [
      { text: 'Permit', value: 'permit', align: 'center', sortable: false, class: 'teal darken-1', divider: true },
      {
        text: 'Critical Movement',
        value: 'cms',
        align: 'center',
        sortable: false,
        class: 'teal darken-2',
        divider: true
      },
      { text: 'LOS', value: 'levelOfService', align: 'center', sortable: false, class: 'teal darken-1', divider: true },
      {
        text: 'Progression Ratio',
        value: 'progressionRatio',
        align: 'center',
        sortable: false,
        class: 'teal darken-2',
        divider: true
      },
      {
        text: 'Recommended Pattern',
        value: 'pattern',
        align: 'center',
        sortable: false,
        class: 'teal darken-1',
        divider: true
      },
      {
        text: 'Cycle Length',
        value: 'cycleLength',
        align: 'center',
        sortable: false,
        class: 'teal darken-2',
        divider: true
      },
      {
        text: 'Pattern Id',
        value: 'patternId',
        align: 'center',
        sortable: false,
        class: 'teal darken-1',
        divider: true
      }
    ]
  }),

  methods: {
    isCritical(item) {
      return this.ctriticalItem ? this.ctriticalItem.permit === item.permit : false;
    }
  }
};
</script>

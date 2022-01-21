<template>
  <v-data-table
    :height="height"
    fixed-header
    :headers="headers"
    :items="items"
    :items-per-page="itemsPerPage"
    hide-default-footer
    :expanded.sync="expanded"
    show-expand
    single-expand
    item-key="id"
    class="elevation-1"
    @click:row="clickRow"
  >
    <template v-slot:expanded-item="{ headers, item }">
      <td :colspan="headers.length">
        <div class="mt-2 mb-6 d-flex flex-column align-center" style="min-width:500px;">
          <h3 class="font-weight-bold text-overline">Phase Shifts for {{ item.permitNumber }}</h3>
          <v-data-table
            dense
            fixed-header
            :headers="phaseShiftHeaders"
            :items="getPhaseShifts(item)"
            hide-default-footer
            class="elevation-1 primary"
          />
        </div>
      </td>
    </template>

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
    height: { type: Number, default: 500 },
    items: Array
  },

  data: () => ({
    itemsPerPage: 500,
    expanded: [],
    headers: [
      { text: 'Group', value: 'signalGroup', sortable: false, class: 'green' },
      { text: 'Permit', value: 'permitNumber', sortable: false, class: 'green' },
      { text: 'Offset', value: 'offset', sortable: false, class: 'green' },
      { text: 'LOS', value: 'los', sortable: false, class: 'green' },
      { text: 'Delay', value: 'avgDelay', sortable: false, class: 'green' },
      { text: 'Bottleneck Volume', value: 'bottleneckVolume', sortable: false, class: 'green' },
      { text: 'Travel Time', value: 'totalDetourTravelTime', sortable: false, class: 'green', divider: true },
      { text: '', value: 'data-table-expand', class: 'green' }
    ],
    phaseShiftHeaders: [
      { text: 'Phase', value: 'phase', sortable: false, class: 'indigo darken-3', divider: true },
      { text: 'P1', value: 'P1', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P2', value: 'P2', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P3', value: 'P3', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P4', value: 'P4', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P5', value: 'P5', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P6', value: 'P6', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P7', value: 'P7', sortable: false, class: 'indigo darken-3', divider: false },
      { text: 'P8', value: 'P8', sortable: false, class: 'indigo darken-3', divider: false }
    ]
  }),

  methods: {
    clickRow(item, event) {
      if (event.isExpanded) {
        const index = this.expanded.findIndex(i => i === item);
        this.expanded.splice(index, 1);
      } else {
        this.expanded = [];
        this.expanded.push(item);
      }
    },

    expandRowByPermit(permit) {
      const row = this.items.find(item => item.permitNumber === permit);
      if (row) {
        const currentIdx = this.expanded.findIndex(i => i === row);
        if (currentIdx === -1) {
          // not expanded
          this.expanded = [];
          this.expanded.push(row);
        }
      }
    },

    getPhaseShifts(item) {
      const existing = {};
      Object.assign(existing, item);
      Object.keys(existing).forEach(key => {
        if (existing[key] !== '/') {
          existing[key] += this.randomInt(-8, 8);
        }
      });
      existing['phase'] = 'Existing';

      const newOne = {};
      Object.assign(newOne, item);
      newOne['phase'] = 'New';

      const changes = {};
      Object.keys(existing).forEach(key => {
        const v0 = existing[key];
        const v1 = newOne[key];
        if (v0 === '/') {
          changes[key] = '/';
        } else {
          const d = v0 - v1;
          changes[key] = d > 0 ? '+' + d : d;
        }
      });
      changes['phase'] = 'Changes';

      return [existing, newOne, changes];
    },

    randomInt(min, max) {
      // min and max included
      return Math.floor(Math.random() * (max - min + 1) + min);
    },

    getColor(c) {
      let color = 'green';
      switch (c) {
        case 'A':
          color = this.getGreenToRed(100);
          break;
        case 'B':
          color = this.getGreenToRed(80);
          break;
        case 'C':
          color = this.getGreenToRed(60);
          break;
        case 'D':
          color = this.getGreenToRed(40);
          break;
        case 'E':
          color = this.getGreenToRed(20);
          break;
        case 'F':
          color = this.getGreenToRed(0);
          break;
      }
      return color;
    },

    getGreenToRed(percent) {
      const r = percent < 50 ? 255 : Math.floor(255 - ((percent * 2 - 100) * 255) / 100);
      const g = percent > 50 ? 255 : Math.floor((percent * 2 * 255) / 100);
      return 'rgb(' + r + ',' + g + ',0)';
    }
  }
};
</script>

<template>
  <v-data-table
    fixed-header
    :headers="headers"
    :items="groups"
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
        <div class="ma-8">
          <v-card tile>
            <SignalTable :items="currentSolution(item)" />
          </v-card>
        </div>
      </td>
    </template>

    <template v-slot:[`item.los`]="{ item }">
      <v-chip :color="getColor(item.los)" label small>
        <span class="black--text">{{ item.los }}</span>
      </v-chip>
    </template>
  </v-data-table>
</template>

<script>
import SignalTable from '@/components/modules/traffic/mitigation/SignalTable';
export default {
  components: {
    SignalTable
  },

  props: {
    color: {
      type: String,
      default: 'red'
    },
    items: Array
  },

  data: () => ({
    expanded: [],
    headers: [
      { text: 'Group', value: 'signalGroup', sortable: false, class: 'green' },
      { text: 'Time Plan #', value: 'plan', sortable: false, class: 'green' },
      { text: 'Cycle Length', value: 'cycle', sortable: false, class: 'green' },
      { text: 'LOS', value: 'los', sortable: false, class: 'green' },
      { text: 'Delay', value: 'avgDelay', sortable: false, class: 'green' },
      { text: 'Bottleneck Volume', value: 'bottleneckVolume', sortable: false, class: 'green' },
      { text: 'Travel Time', value: 'totalDetourTravelTime', sortable: false, class: 'green', divider: true },
      { text: '', value: 'data-table-expand', class: 'green' }
    ]
  }),

  computed: {
    groups() {
      return this.createGroupSignal(this.items);
    }
  },

  mounted() {
    this.headers.forEach(item => (item.class = this.color));
  },

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

    currentSolution(groupItem) {
      const group = groupItem.signalGroup;
      return this.items.filter(item => item.signalGroup === group);
    },

    createGroupSignal(items) {
      // Group the data by group
      const groups = items.reduce((r, item) => {
        r[item.signalGroup] = r[item.signalGroup] || [];
        r[item.signalGroup].push(item);
        return r;
      }, {});

      // Create group record
      return Object.keys(groups).map(name => {
        const solutions = groups[name];
        const totalDelay = solutions.reduce((sum, s) => (sum += s.avgDelay), 0);
        const avgDelay = Math.round(totalDelay / solutions.length);

        return {
          id: name,
          signalGroup: name,
          plan: '231',
          cycle: solutions[0].cycleLength,
          los: solutions[0].los,
          avgDelay: avgDelay,
          bottleneckVolume: solutions[0].bottleneckVolume,
          totalDetourTravelTime: solutions[0].totalDetourTravelTime
        };
      });
    },

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

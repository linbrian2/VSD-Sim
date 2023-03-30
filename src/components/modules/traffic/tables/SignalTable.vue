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
      <v-icon color="red" v-if="item.controller">mdi-circle-small</v-icon>
      <v-chip :color="patternColor(item)" label small>
        <span class="font-weight-bold white--text">{{ item.pattern }}</span>
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

    <template v-slot:[`item.action`]="{ item }">
      <div class="d-flex">
        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-2" dark icon v-on="on" color="orange" @click.stop="applyThisPattern(item)">
              <v-icon dark>mdi-arrow-down-bold-circle-outline</v-icon>
            </v-btn>
          </template>
          <span>Apply pattern</span>
        </v-tooltip>

        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-2" dark icon v-on="on" color="lime" @click.stop="modifyThisPattern(item)">
              <v-icon dark>mdi-pencil</v-icon>
            </v-btn>
          </template>
          <span>Modify pattern</span>
        </v-tooltip>

        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn class="mx-2" dark icon v-on="on" color="red" @click.stop="deleteThisPattern(item)">
              <v-icon dark>mdi-delete</v-icon>
            </v-btn>
          </template>
          <span>Delete pattern</span>
        </v-tooltip>
      </div>
    </template>
  </v-data-table>
</template>

<script>
export default {
  props: {
    items: Array,
    current: String,
    controller: { type: Boolean, default: false }
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

  mounted() {
    this.$bus.$on('OPEN_CLOSE_SYNC_CONTROL', show => {
      show ? this.addActionColumn() : this.removeActionColumn();
    });
  },

  methods: {
    cc(mode) {
      return mode && mode > 0 ? 'red--text' : 'white--text';
    },

    patternColor(item) {
      return item.pattern === this.current ? 'orange' : 'green darken-3';
    },

    // <!-- :item-class="itemRowBackground" -->
    itemRowBackground(item) {
      if (item.pattern === this.current) {
        return 'highlighted-tr';
      } else {
        return '';
      }
    },

    applyThisPattern(item) {
      this.$bus.$emit('APPLY_PATTERN', { action: 0, pattern: item });
    },

    modifyThisPattern(item) {
      this.$bus.$emit('APPLY_PATTERN', { action: 1, pattern: item });
    },

    deleteThisPattern(item) {
      this.$bus.$emit('APPLY_PATTERN', { action: 2, pattern: item });
    },

    addActionColumn() {
      const column = this.headers.find(item => item.text === 'Action');
      if (!column) {
        this.headers.push({
          text: 'Action',
          value: 'action',
          align: 'center',
          sortable: false,
          class: 'blue-grey',
          divider: true,
          width: 20
        });
      }
    },

    removeActionColumn() {
      const index = this.headers.findIndex(item => item.text === 'Action');
      if (index > -1) {
        this.headers.splice(index, 1);
      }
    }
  }
};
</script>

<style lang="scss">
.highlighted-tr {
  background-color: teal;
}
</style>

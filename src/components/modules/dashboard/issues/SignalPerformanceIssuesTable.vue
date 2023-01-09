<template>
  <div v-if="summary">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="summary"
      :items-per-page="showTable && maxItems > listLimit ? maxItems : listLimit"
      disable-sort
      :hide-default-header="!showTable"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
      :search="search"
    >
      <template v-slot:[`item.permit`]="{ item }">
        <v-chip color="pink" outlined small style="width:62px;">{{ item.permit }}</v-chip>
      </template>

      <template v-slot:[`item.AoRN`]="{ item }">
        <FormatChip :value="item.AoR[0]" />
      </template>

      <template v-slot:[`item.AoRS`]="{ item }">
        <FormatChip :value="item.AoR[1]" />
      </template>
    </v-data-table>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import FormatChip from '@/components/modules/hr/FormatChip';
import { mapGetters, mapState } from 'vuex';

export default {
  components: {
    FormatChip
  },
  props: {
    preSelect: { type: Boolean, default: true },
    maxItems: Number,
    summary: Array,
    search: String,
    hrSummary: Array
  },
  data: () => ({
    height: null,
    itemsPerPage: 0,
    updatedTime: null,
    initPanelState: true,
    selectedRowId: null,
    headers: [
      { text: 'Permit', value: 'permit' },
      { text: 'Intersection', value: 'intersection' },
      { text: 'AoR (NB)', value: 'AoRN' },
      { text: 'AoR (SB)', value: 'AoRS' }
    ]
  }),
  computed: {
    showTable: {
      get() {
        return this.$store.state.dashboard.showTable;
      },
      set(show) {
        this.$store.commit('dashboard/SHOW_TABLE', show);
      }
    },
    listLimit() {
      if (this.getSetting) {
        return this.getSetting('dashboard', 'limitListings');
      } else {
        return 0;
      }
    },
    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['selectedSignalPerformanceIssue'])
  },
  filters: {
    date: function(date) {
      return 'Updated time: ' + Utils.formatDateAndTime(date);
    }
  },

  mounted() {
    if (this.summary && !this.selectedSignalPerformanceIssue) {
      this.handleRowClick(this.summary[0]);
    }

    this.itemsPerPage = this.listLimit;
    if (this.showTable) {
      this.expandTable();
    }

    this.$bus.$on('SELECT_TABLE_ROW', dir => {
      if (dir > 0) {
        this.selectNext();
      } else {
        this.selectPrev();
      }
    });
  },

  methods: {
    expandTable() {
      if (this.itemsPerPage == this.listLimit) {
        this.$emit('prepareData', this.hrSummary);
        if (this.maxItems > 12) {
          this.height = 'calc(95vh - 48px)';
        }
        if (this.maxItems == 1) {
          this.itemsPerPage = 1.1;
        } else {
          this.itemsPerPage = this.maxItems;
        }
      } else {
        this.$emit('prepareData', [this.selectedSignalPerformanceIssue]);
        this.height = null;
        this.itemsPerPage = this.listLimit;
      }
    },

    selectNext() {
      if (this.summary.length > 0) {
        const currentIndex = this.summary.findIndex(item => item.id === this.selectedRowId);
        const nextIndex = currentIndex === -1 ? 0 : (currentIndex + 1) % this.summary.length;
        this.handleRowClick(this.summary[nextIndex]);
      }
    },

    selectPrev() {
      if (this.summary.length > 0) {
        const currentIndex = this.summary.findIndex(item => item.id === this.selectedRowId);
        let prevIndex = 0;
        if (currentIndex === -1) {
          prevIndex = this.summary.length - 1;
        } else {
          prevIndex = currentIndex - 1;
          if (prevIndex < 0) {
            prevIndex = this.summary.length + prevIndex;
          }
        }
        this.handleRowClick(this.summary[prevIndex]);
      }
    },

    itemRowBackground(item) {
      return item.id == this.selectedRowId ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedRowId = item.id;
      this.$emit('click', item);
    },
    powerColor(power) {
      return power > 0 ? 'green' : 'red';
    },
    controlColor(power) {
      return power > 0 ? 'green' : 'orange';
    },
    controlType(type) {
      return type > 0 ? 'mdi-alpha-a-circle-outline' : 'mdi-alpha-m-circle-outline';
    },
    getPerformanceScore(index) {
      let random = this.getRandomInt(0, 18);
      let score = index + random;
      return score;
    },
    getRandomInt(min, max) {
      min = Math.ceil(min);
      max = Math.floor(max);
      return Math.floor(Math.random() * (max - min + 1)) + min;
    }
  },
  watch: {
    summary() {
      if (this.summary && this.itemsPerPage == this.listLimit) {
        this.handleRowClick(this.summary[0]);
      }
    },
    listLimit(limit) {
      if (!this.showTable) {
        this.itemsPerPage = limit;
      }
    },
    showTable() {
      this.expandTable();
    }
  }
};
</script>

<style lang="scss" scoped>
.update {
  font-size: 11px !important;
  position: absolute;
  right: 80px;
  margin-top: 8px;
}
.tool {
  font-size: 11px;
  float: right;
  margin-top: 10px;
  margin-right: 10px;
}
.borderless tr td {
  border: none !important;
  padding: 0px !important;
}
.no-border-bottom {
  border-bottom: none !important;
}

.text-orange {
  width: 40px;
  height: 40px;
  padding: 5px auto;
  border-style: solid;
  border-color: orange;
  background-color: rgba(255, 152, 0, 0.4);
}

.text-red {
  border-style: solid;
  border-color: red;
  background-color: rgba(255, 0, 0, 0.4);
}
</style>

<template>
  <div v-if="summary">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="summary"
      :items-per-page="itemsPerPage"
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
      <template v-slot:[`footer`]>
        <v-btn :disabled="maxItems == 1" block @click="expandTable">
          <v-icon>{{ itemsPerPage == 1 ? 'mdi-arrow-expand-down' : 'mdi-arrow-expand-up' }}</v-icon>
        </v-btn>
      </template>
    </v-data-table>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import FormatChip from '@/components/modules/hr/FormatChip';
import { mapState } from 'vuex';

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
    itemsPerPage: 1,
    updatedTime: null,
    initPanelState: true,
    selectedRowId: null,
    headers: [
      { text: 'Permit', value: 'permit' },
      { text: 'Intersection', value: 'intersection' },
      { text: 'AoR (NB)', value: 'AoRN' },
      { text: 'AoR (SB)', value: 'AoRS' }
      // { text: 'SimpleDelay (NB)', value: 'simpleDelayN', align: 'center' },
      // { text: 'SimpleDelay (SB)', value: 'simpleDelayS', align: 'center' },
      // { text: 'ApproachVol (NB)', value: 'approachVolumeN', align: 'center' },
      // { text: 'ApproachVol (SB)', value: 'approachVolumeS', align: 'center' }
    ]
  }),
  computed: {
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
  },

  methods: {
    expandTable() {
      if (this.itemsPerPage == 1) {
        this.$emit('prepareData', this.hrSummary);
        if (this.maxItems > 12) {
          this.height = 'calc(95vh - 48px)';
        }
        this.itemsPerPage = this.maxItems;
      } else {
        this.$emit('prepareData', [this.selectedSignalPerformanceIssue]);
        this.height = null;
        this.itemsPerPage = 1;
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
      if (this.summary && this.itemsPerPage == 1) {
        this.handleRowClick(this.summary[0]);
      }
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

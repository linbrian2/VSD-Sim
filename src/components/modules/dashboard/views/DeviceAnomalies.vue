<template>
  <div>
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemPerPage"
      disable-sort
      :hide-default-header="!showTable"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1 mx-2"
    >
      <template v-slot:[`item.id`]="{ item }">
        <v-chip color="green" outlined small style="width:62px;">{{ item.id }}</v-chip>
      </template>
    </v-data-table>

    <SensorErrorInfo :marker="selectedAnomalyDevice" v-if="selectedAnomalyDevice" />
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
import ErrorTypes from '@/utils/constants/status';
import SensorErrorInfo from '@/components/modules/status/SensorErrorInfo';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: {
    SensorErrorInfo
  },

  data: () => ({
    items: [],
    headers: []
  }),

  computed: {
    height() {
      if (this.showTable && this.maxItems > 12) {
        return 'calc(80vh - 48px)';
      } else {
        return null;
      }
    },

    listLimit() {
      if (this.getSetting) {
        return this.getSetting('dashboard', 'limitListings');
      } else {
        return 0;
      }
    },

    itemPerPage() {
      return this.showTable && this.maxItems > this.listLimit ? this.maxItems : this.listLimit;
    },

    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['deviceAnomalies', 'selectedAnomalyDevice', 'showTable'])
  },

  mounted() {
    if (this.deviceAnomalies.length > 0) {
      this.prepareSensorErrorCounts(this.deviceAnomalies);
      this.handleRowClick(this.items[0]);
    }
  },

  watch: {
    deviceAnomalies() {
      if (this.deviceAnomalies.length > 0) {
        this.prepareSensorErrorCounts(this.deviceAnomalies);
        this.handleRowClick(this.items[0]);
      }
    }
  },

  methods: {
    getError(idx) {
      if (idx >= 0 && idx < 7) {
        return ErrorTypes.QUALITY_ERROR_NAMES[idx];
      } else {
        return 'N/A';
      }
    },
    handleRowClick(item) {
      const data = this.deviceAnomalies.find(x => x.id == item.id);
      if (data) {
        this.$store.commit('dashboard/SET_SELECTED_ANOMALY_DEVICE', data);
      }
    },

    itemRowBackground(item) {
      return item.id == this.selectedAnomalyDevice.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    getDominantErrorType(counts, startIdx) {
      let max = counts[startIdx];
      let maxIndex = startIdx;

      for (var i = startIdx + 1; i < counts.length; i++) {
        if (counts[i] > max) {
          maxIndex = i;
          max = counts[i];
        }
      }

      return maxIndex;
    },

    prepareSensorErrorCounts(data) {
      this.headers = [
        { text: 'Sensor Id', value: 'id' },
        { text: 'Permit', value: 'uid' },
        { text: 'Error Percentage', value: 'percent' },
        { text: 'Major Error', value: 'majorError' }
      ];

      this.items = data.map(d => {
        const counts = d.counts;
        const dominantErrorType = this.getDominantErrorType(counts, 1);
        const majorError = this.getError(dominantErrorType);

        const item = {
          id: d.id,
          uid: d.uid,
          percent: d.errorPercentage,
          majorError
        };
        return item;
      });
    }
  }
};
</script>

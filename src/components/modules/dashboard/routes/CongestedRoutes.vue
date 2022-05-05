<template>
  <div class="congested-routes">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemsPerPage"
      :item-class="itemRowBackground"
      hide-default-footer
      @click:row="handleRowClick"
      class="elevation-1"
    >
      <template v-slot:[`item.level`]="{ item }">
        <v-chip small :color="getStrokeColor(item.level)">
          <strong class="black--text">{{ item.level }}</strong>
        </v-chip>
      </template>
      <template v-slot:[`item.lastUpdated`]="{ item }">
        {{ item.lastUpdated.split(' ')[1].slice(0, 5) }}
      </template>
      <template v-slot:[`footer`]>
        <v-btn :disabled="maxItems == 1" block @click="expandTable">
          <v-icon>{{ itemsPerPage == 1 ? 'mdi-arrow-expand-down' : 'mdi-arrow-expand-up' }}</v-icon>
        </v-btn>
      </template>
    </v-data-table>
    <v-row class="mt-3 ml-1 mr-7" v-if="currSegment">
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :name="'Time'"
          :titleFontSize="20"
          :valueFontSize="28"
          :value="getTimeStr(currSegment.travelTime.calculationTimestamp.value)"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-chart-bar-stacked'"
          :name="'Severity'"
          :valueColor="getStrokeColor(currSegment.travelTime.level)"
          :value="currSegment.travelTime.level"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-vector-line'"
          :name="'Distance'"
          :titleFontSize="20"
          :valueFontSize="28"
          :value="currSegment.distance"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-speedometer'"
          :name="'Speed (mph)'"
          :titleFontSize="20"
          :valueFontSize="28"
          :value="currSegment.travelTime.data.speedMph"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-timer-outline'"
          :name="'Free Flow Run TIme'"
          :titleFontSize="20"
          :valueFontSize="28"
          :value="currSegment.travelTime.data.freeFlowRunTimeSecs"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-timer-outline'"
          :name="'Travel Time Mean'"
          :titleFontSize="20"
          :valueFontSize="28"
          :value="currSegment.travelTime.data.meanTravelTimeSecs"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import InfoCard from '@/components/modules/dashboard/InfoCard';
import Utils from '@/utils/Utils.js';
import { mapState } from 'vuex';

export default {
  props: {
    data: Object,
    maxItems: Number
  },
  components: {
    InfoCard
  },
  data: () => ({
    height: null,
    itemsPerPage: 1,
    reload: false,
    items: [],
    headers: []
  }),
  computed: {
    currSegment() {
      if (this.selectedSegment && this.segments) {
        let result = this.segments.filter(x => x.id == this.selectedSegment.id);
        return result.length > 0 ? result[0] : null;
      } else {
        return null;
      }
    },
    selectedSegment: {
      get() {
        return this.$store.state.dashboard.selectedSegment;
      },
      set(val) {
        this.$store.state.dashboard.selectedSegment = val;
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },
    ...mapState('dashboard', ['segments'])
  },
  mounted() {
    if (this.segments) {
      this.prepareHighCongestionRoutes(this.segments);
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)} (${Utils.fromNow(time)})`;
    },
    expandTable() {
      if (this.itemsPerPage == 1) {
        this.prepareHighCongestionRoutes(this.segments);
        if (this.maxItems > 12) {
          this.height = 'calc(95vh - 48px)';
        }
        this.itemsPerPage = this.maxItems;
      } else {
        this.prepareHighCongestionRoutes([this.currSegment]);
        this.height = null;
        this.itemsPerPage = 1;
      }
    },
    getStrokeColor(level) {
      return Utils.getStrokeColor(level);
    },
    prepareHighCongestionRoutes(data) {
      this.headers = [
        { text: 'Description', value: 'desc' },
        { text: 'Last Updated', value: 'lastUpdated' },
        { text: 'Level', value: 'level' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        lastUpdated: d.travelTime.calculationTimestamp.text,
        desc: d.desc,
        level: d.travelTime.level
      }));
    },

    itemRowBackground(item) {
      return item.id == this.selectedSegment.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedSegment = item;
      this.$emit('click', item);
    }
  },
  watch: {
    segments() {
      if (this.segments) {
        this.prepareHighCongestionRoutes(this.segments);
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

<style></style>

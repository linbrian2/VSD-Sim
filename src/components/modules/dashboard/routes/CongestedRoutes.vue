<template>
  <div class="congested-routes">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemsPerPage"
      :item-class="itemRowBackground"
      disable-sort
      :hide-default-header="itemsPerPage == 1"
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
      <template v-slot:[`item.actions`] v-if="itemsPerPage == 1">
        <div class="grid-right pr-6">
          <v-icon small @click="expandTable">
            mdi-arrow-expand-down
          </v-icon>
        </div>
      </template>
      <template v-slot:[`footer`] v-if="itemsPerPage != 1">
        <v-btn block @click="expandTable">
          <v-icon>{{ itemsPerPage == 1 ? 'mdi-arrow-expand-down' : 'mdi-arrow-expand-up' }}</v-icon>
        </v-btn>
      </template>
    </v-data-table>
    <v-row class="mt-3 ml-1 mr-7" v-if="currSegment">
      <v-col :cols="singleCol ? 12 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Time'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? undefined : 28"
          :value="getTimeStr(currSegment.travelTime.calculationTimestamp.value)"
        />
      </v-col>
      <v-col :cols="singleCol ? 12 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-chart-bar-stacked'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Severity'"
          :valueColor="getStrokeColor(currSegment.travelTime.level)"
          :value="currSegment.travelTime.level"
        />
      </v-col>
      <v-col :cols="singleCol ? 12 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-vector-line'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Distance'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? 38 : 28"
          :value="`${currSegment.distance} mi`"
        />
      </v-col>
      <v-col :cols="singleCol ? 12 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-speedometer'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Speed'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? 38 : 28"
          :value="`${currSegment.travelTime.data.speedMph.toFixed(2)} mph`"
        />
      </v-col>
      <v-col :cols="singleCol ? 12 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-timer-outline'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Free Flow Run TIme'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? 38 : 28"
          :value="`${currSegment.travelTime.data.freeFlowRunTimeSecs} s`"
        />
      </v-col>
      <v-col :cols="singleCol ? 12 : 6" class="pa-1">
        <InfoCard
          :icon="'mdi-timer-outline'"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Travel Time Mean'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? 38 : 28"
          :value="`${currSegment.travelTime.data.meanTravelTimeSecs} s`"
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
    maxItems: Number,
    infoColumnCount: Number
  },
  components: {
    InfoCard
  },
  data: () => ({
    itemsPerPage: 1,
    reload: false,
    items: [],
    headers: []
  }),
  computed: {
    singleCol() {
      return this.infoColumnCount == 1;
    },
    cardHeight() {
      return this.singleCol ? '11vh' : undefined;
    },
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
        if (this.maxItems == 1) {
          this.itemsPerPage = 1.1;
        } else {
          this.itemsPerPage = this.maxItems;
        }
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
        { text: 'Level', value: 'level' },
        { text: '', value: 'actions' }
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

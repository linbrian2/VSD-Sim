<template>
  <div class="congested-routes">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="showTable && maxItems > listLimit ? maxItems : listLimit"
      disable-sort
      :hide-default-header="!showTable"
      :item-class="itemRowBackground"
      hide-default-footer
      @click:row="handleRowClick"
      class="elevation-1 mx-2"
    >
      <template v-slot:[`item.level`]="{ item }">
        <v-chip small :color="getStrokeColor(item.level)">
          <strong class="black--text">{{ item.level }}</strong>
        </v-chip>
      </template>
      <template v-slot:[`item.lastUpdated`]="{ item }">
        {{ item.lastUpdated.split(' ')[1].slice(0, 5) }}
      </template>
    </v-data-table>

    <v-row class="mt-3 mx-1" v-if="currSegment">
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-note-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Name'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? undefined : 28"
          :value="currSegment.short"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-vector-line'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Route'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? undefined : 28"
          :value="currSegment.desc"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Time'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? undefined : 28"
          :value="getTimeStr(currSegment.travelTime.calculationTimestamp.value)"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-chart-bar-stacked'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Severity'"
          :valueColor="getStrokeColor(currSegment.travelTime.level)"
          :value="`${currSegment.travelTime.level} / 7`"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-speedometer'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Speed'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? 38 : 28"
          :value="`${currSegment.travelTime.data.speedMph.toFixed(2)} mph`"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-timer-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Travel Time'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? 38 : 28"
          :value="
            `${getDurStr(currSegment.travelTime.data.freeFlowRunTimeSecs)} / ${getDurStr(
              currSegment.travelTime.data.meanTravelTimeSecs
            )} (mean)`
          "
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-vector-line'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Distance'"
          :titleFontSize="singleCol ? undefined : 20"
          :valueFontSize="singleCol ? 38 : 28"
          :value="`${currSegment.distance} mi`"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import InfoCard from '@/components/modules/dashboard/InfoCard';
import Utils from '@/utils/Utils.js';
import { mapGetters, mapState } from 'vuex';

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
    itemsPerPage: 0,
    reload: false,
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
    singleCol() {
      return this.infoColumnCount == 1;
    },
    cardHeight() {
      return this.singleCol ? '11vh' : undefined;
    },
    currSegment() {
      if (this.selectedSegment && this.congestedSegments) {
        let result = this.congestedSegments.filter(x => x.id == this.selectedSegment.id);
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
      }
    },

    listLimit() {
      return this.getSetting ? this.getSetting('dashboard', 'limitListings') : 0;
    },
    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['congestedSegments', 'showTable'])
  },

  mounted() {
    if (this.congestedSegments) {
      this.prepareHighCongestionRoutes(this.congestedSegments);
      this.handleRowClick(this.items[0]);
    }

    this.$bus.$on('SELECT_TABLE_ROW', dir => {
      console.log('SELECT_TABLE_ROW');
      if (dir > 0) {
        this.selectNext();
      } else {
        this.selectPrev();
      }
    });
  },
  methods: {
    getDurStr(dur) {
      return Utils.durationToTimeStr(dur);
    },
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)}`;
    },
    getStrokeColor(level) {
      return Utils.getStrokeColor(level);
    },
    prepareHighCongestionRoutes(data) {
      this.headers = [
        { text: 'Description', value: 'desc' },
        { text: 'Time', value: 'lastUpdated' },
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
    },

    selectNext() {
      if (this.items && this.items.length > 0) {
        const currentIndex = this.items.findIndex(item => item.id == this.selectedSegment.id);
        const nextIndex = currentIndex === -1 ? 0 : (currentIndex + 1) % this.items.length;
        this.handleRowClick(this.items[nextIndex]);
        console.log(currentIndex, nextIndex);
      }
    },

    selectPrev() {
      if (this.items && this.items.length > 0) {
        const currentIndex = this.items.findIndex(item => item.id == this.selectedSegment.id);
        let prevIndex = 0;
        if (currentIndex === -1) {
          prevIndex = 0;
        } else {
          prevIndex = currentIndex - 1;
          if (prevIndex < 0) {
            prevIndex = this.items.length + prevIndex;
          }
        }
        this.handleRowClick(this.items[prevIndex]);
      }
    }
  },
  watch: {
    congestedSegments() {
      if (this.congestedSegments) {
        this.prepareHighCongestionRoutes(this.congestedSegments);
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

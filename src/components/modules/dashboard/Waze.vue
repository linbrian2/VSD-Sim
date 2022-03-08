<template>
  <div class="waze">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemsPerPage"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
    >
      <template v-slot:[`item.status`]="{ item }">
        <v-chip small :color="'red'"
          ><strong class="black--text">{{ item.status }}</strong></v-chip
        >
      </template>
    </v-data-table>
    <!-- Data Display -->
    <v-row>
      <v-col cols="12" class="pb-0">
        <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Current</h3></v-subheader>
        <v-divider />
      </v-col>
      <v-col cols="12" class="pt-0">
        <v-card class="my-2" min-height="24vh" tile :color="'#385F73'">
          <WazeInfo class="mx-1" :waze="waze" :showNearby="true" />
        </v-card>
      </v-col>
      <v-col cols="12" class="py-0">
        <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Nearby Incidents</h3></v-subheader>
        <v-divider />
      </v-col>
      <v-col class="pa-0 px-2" cols="6" v-for="w in relatedWazes" :key="w.id">
        <v-card class="my-2" min-height="16vh" tile :color="'#385F73'">
          <WazeInfo class="mx-1" :waze="w" />
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import WazeInfo from '@/components/modules/dashboard/WazeInfo';
import Utils from '@/utils/Utils';
export default {
  props: {
    height: { type: Number, default: 200 },
    data: Object
  },
  components: {
    WazeInfo
  },
  data: () => ({
    waze: {
      type: 'Hazard',
      dist: 'Freeway',
      reportRating: 4,
      desc: 'Hazard on Shoulder (Car Stopped)',
      time: '2:45 PM',
      confidence: 4,
      reliability: 12,
      thumbsup: 37
    },
    relatedWazes: [
      {
        type: 'Hazard',
        dist: 'Freeway',
        reportRating: 4,
        desc: 'Hazard on Shoulder (Car Stopped)',
        time: '2:45 PM',
        confidence: 4,
        reliability: 12,
        thumbsup: 12
      },
      {
        type: 'Hazard',
        dist: '0.02 mi',
        reportRating: 5,
        desc: 'Hazard on Shoulder (Car Stopped)',
        time: '2:47 PM',
        confidence: 2,
        reliability: 7,
        thumbsup: 11
      },
      {
        type: 'Jam',
        dist: '0.04mi',
        reportRating: 4,
        desc: 'Traffic Jam (Moderate)',
        time: '2:50 PM',
        confidence: 0,
        reliability: 5,
        thumbsup: 6
      },
      {
        type: 'Hazard',
        dist: '0.07 mi',
        reportRating: 4,
        desc: 'Hazard on Shoulder (Car Stopped)',
        time: '2:45 PM',
        confidence: 0,
        reliability: 4,
        thumbsup: 2
      },
      {
        type: 'Hazard',
        dist: '0.07 mi',
        reportRating: 4,
        desc: 'Hazard on Shoulder (Car Stopped)',
        time: '2:45 PM',
        confidence: 0,
        reliability: 4,
        thumbsup: 2
      },
      {
        type: 'Hazard',
        dist: '0.07 mi',
        reportRating: 4,
        desc: 'Hazard on Shoulder (Car Stopped)',
        time: '2:45 PM',
        confidence: 0,
        reliability: 4,
        thumbsup: 2
      },
      {
        type: 'Hazard',
        dist: '0.07 mi',
        reportRating: 4,
        desc: 'Hazard on Shoulder (Car Stopped)',
        time: '2:45 PM',
        confidence: 0,
        reliability: 4,
        thumbsup: 2
      }
    ],
    itemsPerPage: 3,
    headers: [
      { text: 'Alert Time', value: 'time' },
      { text: 'Description', value: 'desc' },
      { text: 'Street', value: 'street' },
      { text: 'Road Type', value: 'type' }
    ],
    items: [
      {
        time: '2022-02-08 14:45:57',
        desc: 'Hazard on Shoulder (Car Stopped)',
        street: 'Coastal Hwy',
        type: 'Primary'
      },
      {
        time: '2022-02-08 14:45:15',
        desc: 'Hazard on Shoulder (Car Stopped)',
        street: 'SR-1 N',
        type: 'Freeways'
      },
      {
        time: '2022-02-08 14:45:13',
        desc: 'Hazard on Road (Construction)',
        street: 'Pilottown Rd',
        type: 'Primary'
      }
    ],
    selectedId: null
  }),

  filters: {
    time(time) {
      const d = new Date(time);
      const dd = Utils.formatDate(d);
      const tt = Utils.formatTimeAsMinute(d);
      return `${dd} ${tt}`;
    }
  },

  mounted() {
    this.handleRowClick(this.items[0]);
  },

  methods: {
    itemRowBackground(item) {
      return item.time == this.selectedId ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedId = item.time;
      this.$emit('click', item);
    }
  }
};
</script>

<style lang="scss"></style>

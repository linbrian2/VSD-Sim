<template>
  <div class="signal-performance">
    <v-data-table
      disable-sort
      :headers="headers"
      :items="summary"
      :loading="loading"
      loading-text="Loading... Please wait"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
    >
      <template v-slot:[`item.permit`]="{ item }">
        <v-chip color="#008900" outlined x-small style="width:45px;">{{ item.permit }}</v-chip>
      </template>
      <template v-slot:[`item.power`]="{ item }">
        <v-icon small :color="powerColor(item.power)">mdi-record</v-icon>
      </template>

      <template v-slot:[`item.control`]="{ item }">
        <v-icon small :color="controlColor(item.control)">{{ controlType(item.control) }}</v-icon>
      </template>

      <template v-slot:[`item.AoRN`]="{ item }">
        <FormatChip :value="item.AoR[0]" />
      </template>

      <template v-slot:[`item.AoRS`]="{ item }">
        <FormatChip :value="item.AoR[1]" />
      </template>

      <template v-slot:[`item.simpleDelayN`]="{ item }">
        <div>{{ item.simpleDelay[0] }}</div>
      </template>

      <template v-slot:[`item.simpleDelayS`]="{ item }">
        <div>{{ item.simpleDelay[1] }}</div>
      </template>

      <template v-slot:[`item.approachVolumeN`]="{ item }">
        <div>{{ item.approachVolume[0] }}</div>
      </template>

      <template v-slot:[`item.approachVolumeS`]="{ item }">
        <div>{{ item.approachVolume[1] }}</div>
      </template>

      <template v-slot:[`item.performanceIndex`]="{ item }">
        <v-chip outlined small color="teal">{{ getPerformanceScore(item.performanceIndex) }}</v-chip>
      </template>
    </v-data-table>
    <v-card class="my-2" min-height="36.9vh">
      <PcdChart :info="pcd.info" :title="pcd.title" :date="pcd.date" :phase="pcd.phase" :height="pcd.height" />
    </v-card>
    <v-card min-height="36.9vh">
      <AorChart :data="aor.data" :title="aor.title" :height="420" />
    </v-card>
  </div>
</template>

<script>
import FormatChip from '@/components/modules/dashboard/FormatChip';
import PcdChart from '@/components/modules/dashboard/graphs/PcdChart';
import AorChart from '@/components/modules/dashboard/graphs/AorChart';

import PCD from '@/utils/samples/PCD.js';
import AOR from '@/utils/samples/AOR.js';

export default {
  components: {
    FormatChip,
    PcdChart,
    AorChart
  },
  data() {
    return {
      pcd: PCD,
      aor: AOR,
      selectedId: null,
      headers: [
        /* { text: 'Id', value: 'id' }, */
        { text: 'Permit', value: 'permit' },
        { text: 'Intersection', value: 'intersection', width: '150px' },
        { text: 'Power', value: 'power' },
        { text: 'Control', value: 'control' },
        { text: 'AoR (NB)', value: 'AoRN' },
        { text: 'AoR (SB)', value: 'AoRS' },
        { text: 'SimpleDelay (NB)', value: 'simpleDelayN', align: 'center' },
        { text: 'SimpleDelay (SB)', value: 'simpleDelayS', align: 'center' },
        { text: 'ApproachVol (NB)', value: 'approachVolumeN', align: 'center' },
        { text: 'ApproachVol (SB)', value: 'approachVolumeS', align: 'center' }
        /* { text: 'Score', value: 'performanceIndex' } */
      ],
      summary: [
        {
          id: 68,
          permit: 'K247',
          intersection: 'US 13 @ BIG OAK RD',
          power: 1,
          control: 1,
          AoR: [51, 71],
          simpleDelay: [21, 40],
          approachVolume: [14, 15],
          performanceIndex: 80
        },
        {
          id: 69,
          permit: 'K278',
          intersection: 'US 13 @ CARTER RD',
          power: 1,
          control: 1,
          AoR: [0, 48],
          simpleDelay: [0, 42],
          approachVolume: [0, 26],
          performanceIndex: 80
        },
        {
          id: 67,
          permit: 'K201',
          intersection: 'US 13 @ S SMYRNA RAMP',
          power: 1,
          control: 1,
          AoR: [28, 47],
          simpleDelay: [41, 31],
          approachVolume: [21, 24],
          performanceIndex: 80
        }
      ]
    };
  },
  mounted() {
    this.handleRowClick(this.summary[0]);
  },
  methods: {
    itemRowBackground(item) {
      return item.id == this.selectedId ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedId = item.id;
      this.$emit('click', item);
    },
    chartHeight(length) {
      return length == 1 ? 560 : 360;
    },
    powerColor(power) {
      return power > 0 ? '#008900' : 'red';
    },
    controlColor(power) {
      return power > 0 ? '#008900' : 'orange';
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
  }
};
</script>

<style></style>

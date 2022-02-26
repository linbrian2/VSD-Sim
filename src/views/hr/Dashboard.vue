<template>
  <div>
    <TitleBar title="Today's Summary" :loading="loading" :refresh="refreshData">
      <div class="update mt-3" v-if="updatedTime">{{ updatedTime | date }}</div>
    </TitleBar>
    <v-container fluid style="max-width: 95%">
      <div class="my-2 mx-2">
        <v-data-table
          disable-sort
          :headers="headers"
          :items="summary"
          hide-default-footer
          :loading="loading"
          loading-text="Loading... Please wait"
        >
          <template v-slot:[`item.permit`]="{ item }">
            <v-chip color="success" outlined x-small style="width:45px;">{{ item.permit }}</v-chip>
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
      </div>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/hr';
import Utils from '@/utils/Utils';
import TitleBar from '@/components/modules/hr/TitleBar';
import FormatChip from '@/components/modules/hr/FormatChip';
export default {
  components: {
    TitleBar,
    FormatChip
  },
  data: () => ({
    loading: false,
    updatedTime: null,
    initPanelState: true,
    summary: [],
    headers: [
      { text: 'Id', value: 'id' },
      { text: 'Permit', value: 'permit' },
      { text: 'Intersection', value: 'intersection', width: '150px' },
      { text: 'Power', value: 'power' },
      { text: 'Control', value: 'control' },
      { text: 'AoR (NB)', value: 'AoRN' },
      { text: 'AoR (SB)', value: 'AoRS' },
      { text: 'SimpleDelay (NB)', value: 'simpleDelayN', align: 'center' },
      { text: 'SimpleDelay (SB)', value: 'simpleDelayS', align: 'center' },
      { text: 'ApproachVol (NB)', value: 'approachVolumeN', align: 'center' },
      { text: 'ApproachVol (SB)', value: 'approachVolumeS', align: 'center' },
      { text: 'Score', value: 'performanceIndex' }
    ]
  }),
  filters: {
    date: function(date) {
      return 'Updated time: ' + Utils.formatDateAndTime(date);
    }
  },

  mounted() {
    setTimeout(() => {
      this.initPanelState = this.$store.state.hr.showPanel;
      this.$store.commit('hr/SHOW_PANEL', false);
    }, 100);
    this.refreshData();
  },

  beforeDestroy() {
    if (this.initPanelState) {
      this.$store.commit('hr/SHOW_PANEL', true);
    }
  },

  methods: {
    powerColor(power) {
      return power > 0 ? 'green' : 'red';
    },
    controlColor(power) {
      return power > 0 ? 'green' : 'orange';
    },
    controlType(type) {
      return type > 0 ? 'mdi-alpha-a-circle-outline' : 'mdi-alpha-m-circle-outline';
    },
    refreshData() {
      this.fetchStatus();
    },
    async fetchStatus() {
      this.loading = true;
      try {
        const response = await Api.fetchStatusOfDevices();
        this.summary = response.data;
        this.updatedTime = new Date();
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
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

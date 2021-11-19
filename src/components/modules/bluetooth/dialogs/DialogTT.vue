<template>
  <v-dialog v-model="ttDialog" max-width="100%" transition="scroll-x-transition">
    <v-card>
      <v-card-actions>
        <v-card-title class="segment-title"> {{ segName }} </v-card-title>
        <!-- Card Button Group -->
        <div class="middle-header">
          <v-btn-toggle dense mandatory>
            <!-- Button: w/ anomaly detection -->
            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <v-btn @click.stop="showAnom = true" v-bind="attrs" v-on="on">
                  <v-icon>mdi-alert-rhombus</v-icon>
                </v-btn>
              </template>
              <span>With Anomalies</span>
            </v-tooltip>
            <!-- Button: w/o anomaly detection -->
            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <v-btn @click.stop="showAnom = false" v-bind="attrs" v-on="on">
                  <v-icon>mdi-alert-rhombus-outline</v-icon>
                </v-btn>
              </template>
              <span>Without Anomalies</span>
            </v-tooltip>
          </v-btn-toggle>
        </div>
        <div class="interval-slider">
          <v-slider
            v-model="graphIntervalInd"
            :tick-labels="ticksLabels"
            :max="4"
            :label="'Interval'"
            step="1"
            ticks="always"
            tick-size="4"
            class="px-1"
          ></v-slider>
        </div>
        <v-btn icon class="close-button mr-4" @click="closeDialog">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-actions>
      <div v-for="i in graphInterval.length" :key="'A' + i" class="px-3">
        <AreaRangeChart
          v-if="showAnom && i - 1 == graphIntervalInd"
          :historicData="selectedSeg.histWInc"
          :currData="selectedSeg.curr"
          :interval="graphInterval[i - 1]"
          :title="'Historical Travel Time'"
          :containerName="'container'"
          :visibility="[true, true, true, true, false, false, true]"
        />
      </div>
      <!-- Graph w/o anomalies -->
      <div v-for="i in graphInterval.length" :key="'B' + i" class="px-3">
        <AreaRangeChart
          v-if="!showAnom && i - 1 == graphIntervalInd"
          :historicData="selectedSeg.histWoInc"
          :currData="selectedSeg.curr"
          :interval="graphInterval[i - 1]"
          :title="'Historical Travel Time'"
          :containerName="'container'"
          :visibility="[true, true, true, true, false, false, true]"
        />
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
import AreaRangeChart from '@/components/modules/bluetooth/graphs/AreaRangeChart';

import Api from '@/utils/api/bluetooth.js';
import { mapState } from 'vuex';

export default {
  components: {
    AreaRangeChart
  },
  data() {
    return {
      graphIntervalInd: 2,
      graphInterval: [1, 2, 5, 15, 60],
      ticksLabels: ['1m', '2m', '5m', '15m', '60m'],
      showAnom: true
    };
  },
  watch: {
    ttDialog(val, oldVal) {
      if (val == true && oldVal == false) {
        this.init();
      }
    }
  },
  methods: {
    init() {
      this.fetchSegmentData(this.selectedSeg.data);
    },
    closeDialog() {
      this.$store.commit('bluetooth/SET_TT_DIALOG', false)
    },
    fetchSegmentData(selectedSeg) {
      this.$store.commit('bluetooth/SET_SELECTED_SEG', {prop: 'curr', data: null})
      let linkId = selectedSeg.info.linkId;
      /* Fetch Historical Data w/ Incidents */
      Api.fetchHistoricalTTWIncidentsByLinkID(linkId).then(
        dataW => {
          this.$store.commit('bluetooth/SET_SELECTED_SEG', {prop: 'histWInc', data: dataW})
          let notifText = 'Successfully fetched Travel Time Data';
          this.$store.dispatch('setSystemStatus', { text: notifText, color: 'info', timeout: 2500 });
        },
        error => {
          console.log(error);
          let notifText = 'Failed to fetch Travel Time Data';
          this.$store.dispatch('setSystemStatus', { text: notifText, color: 'error', timeout: 2500 });
        }
      );
      /* Fetch Historical Data w/o Incidents */
      Api.fetchHistoricalTTWoIncidentsByLinkID(linkId).then(
        dataWo => {
          this.$store.commit('bluetooth/SET_SELECTED_SEG', {prop: 'histWoInc', data: dataWo})
        },
        error => {
          console.log(error);
        }
      );
      /* Fetch Current Day Data */
      console.log(this.currentDate);
      let dt = this.currentDate.valueOf();
      Api.fetchCurrTTByLinkId(linkId, dt).then(
        dataC => {
          this.$store.commit('bluetooth/SET_SELECTED_SEG', {prop: 'curr', data: dataC})
        },
        error => {
          console.log(error);
        }
      );
    }
  },
  computed: {
    segName() {
      if (this.selectedSeg && this.selectedSeg.data && this.selectedSeg.data.info) {
        return this.selectedSeg.data.info.description;
      } else {
        return 'N/A';
      }
    },
    ttDialog: {
      get() {
        return this.$store.state.bluetooth.ttDialog
      },
      set(val) {
        this.$store.commit('bluetooth/SET_TT_DIALOG', val)
      }
    },
    ...mapState(['currentDate']),
    ...mapState('bluetooth', ['selectedSeg'])
  }
};
</script>

<style lang="scss" scoped>
@media only screen and (max-width: 1000px) {
  .interval-slider {
    display: none;
  }
}
.interval-slider {
  width: 30%;
  padding-left: 0;
  right: 10%;
  position: absolute;
  display: inline-flex;
}
.middle-header {
  text-align: center;
  margin: 0 auto;
  padding: 10px;
}
.segment-title {
  position: absolute;
  left: 20px;
  padding: 10px;
}
</style>

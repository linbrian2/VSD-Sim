<template>
  <v-card class="main-card">
    <v-col>
      <v-row>
        <v-col style="display:flex;">
          <v-autocomplete
            v-model="$store.state.bluetooth.segGraph"
            dense
            :items="segmentInfo"
            :loading="isLoading"
            :search-input.sync="search"
            color="white"
            hide-no-data
            hide-selected
            item-text="info.description"
            item-value="API"
            label="Search"
            return-object
            small-chips
            multiple
            deletable-chips
            class="mt-0 mx-6"
            prepend-icon="mdi-chart-areaspline"
          />
          <!-- Add From Map Button -->
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs" v-on="on" @click="addFromMap()" class="mx-1">
                <v-icon>mdi-map-plus</v-icon>
              </v-btn>
            </template>
            <span>Map Add</span>
          </v-tooltip>
          <!-- Add Using Filters -->
          <MenuPopover />
          <!-- Options Button -->
          <OptionsMenu />
          <!-- Clear Button -->
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs" v-on="on" @click="clearAll()" class="mx-1">
                <v-icon>mdi-backspace-outline</v-icon>
              </v-btn>
            </template>
            <span>Clear all</span>
          </v-tooltip>
        </v-col>
      </v-row>
      <v-row>
        <v-col :cols="col[0]" :lg="col[1]" :xl="col[2]" v-for="(s, i) in $store.state.bluetooth.segGraph" :key="s.id">
          <!-- Current & Baseline travel time found -->
          <v-card v-if="s.data && !reload">
            <AreaRangeChart
              :historicData="s.data.histTT"
              :currData="s.data.currTT"
              :interval="5"
              :minWidth="320"
              :height="$store.state.bluetooth.prefs.multiGraph.graphHeight"
              :title="s.info.description"
              :subtitle="null"
              :containerName="`container-${i}`"
              :legendX="60"
              :legendY="20"
              :multiGraph="true"
              :legendWidth="'450px'"
              :visibility="[false, false, true, true, false, false, false]"
            />
            <div id="buttons">
              <v-row v-if="$store.state.bluetooth.prefs.multiGraph.showButtons">
                <v-col cols="6" class="pt-1 pr-1 pb-0">
                  <v-btn @click="viewAdditionalInfo(s)" block dense>Additional Info</v-btn>
                </v-col>
                <v-col cols="6" class="pt-1 pl-1 pb-0">
                  <v-btn @click="goToMapLocation(s)" block dense>Go to location</v-btn>
                </v-col>
              </v-row>
            </div>
          </v-card>
          <v-card :height="$store.state.bluetooth.prefs.multiGraph.graphHeight + 40" loading v-else> </v-card>
        </v-col>
      </v-row>
    </v-col>
  </v-card>
</template>

<script>
import Api from '@/utils/api/bluetooth.js';
import AreaRangeChart from '@/components/modules/bluetooth/graphs/AreaRangeChart';
import MenuPopover from '@/components/modules/bluetooth/ui/MenuPopover.vue';
import OptionsMenu from '@/components/modules/bluetooth/ui/OptionsMenu.vue';

export default {
  props: ['segmentInfo'],
  components: {
    AreaRangeChart,
    MenuPopover,
    OptionsMenu
  },
  data: () => ({
    reload: false,
    isLoading: false,
    search: null,
    colCount: [
      [12, 6, 4] /* Auto */,
      [12, 12, 12] /* 1 */,
      [6, 6, 6] /* 2 */,
      [4, 4, 4] /* 3 */,
      [3, 3, 3] /* 4 */
    ]
  }),
  mounted() {
    this.$bus.$on('SUBMIT_MULTIGRAPH_FILTERS', filters => {
      this.processFilters(filters);
    });
    this.$bus.$on('SUBMIT_MULTIGRAPH_OPTIONS', ops => {
      this.processOptions(ops);
    });
    this.$bus.$on('SUBMIT_SEGMENTS', segsList => {
      this.processSegments(segsList);
    });
    this.$bus.$on('SEGMENTS_UPDATE', () => {
      let segGraphCopy = JSON.parse(JSON.stringify(this.$store.state.bluetooth.segGraph));
      this.clearAll();
      this.$store.state.bluetooth.segGraph = segGraphCopy;
    });
  },
  methods: {
    goToMapLocation(segment) {
      this.$bus.$emit('GO_TO_SEGMENT_LOCATION', segment);
      this.$store.state.bluetooth.map.setZoom(14);
      this.$store.commit('bluetooth/SET_SELECTED_PAGE', 0);
    },
    viewAdditionalInfo(segment) {
      console.log('viewAdditionalInfo:\n%o', segment);
      this.$store.state.bluetooth.selectedSeg.data = segment;
      this.$store.state.bluetooth.dialog.tt = true;
    },
    processSegments(segsList) {
      setTimeout(() => {
        this.$store.state.bluetooth.segGraph = segsList;
        this.$store.state.bluetooth.modes.addFromMap = false;
      }, 1);
    },
    processOptions(ops) {
      this.$store.state.bluetooth.prefs.multiGraph.colCountIdx = ops.colCount;
      this.$store.state.bluetooth.prefs.multiGraph.showButtons = ops.showButtons;
      this.$store.state.bluetooth.prefs.multiGraph.graphHeight = ops.graphHeight;
      this.reload = true;
      setTimeout(() => {
        this.reload = false;
      }, 1);
    },
    processFilters(filters) {
      console.log('processFilters: %o', filters);
      let segmentsFilt = this.segments.filter(s => {
        let isValid = false;
        filters.levels.forEach(level => {
          if (s.travelTime.level == level) {
            isValid = true;
            return;
          }
        });
        return isValid;
      });
      let segments = [];
      segmentsFilt.forEach(s => {
        segments.push(s);
      });
      this.$store.state.bluetooth.segGraph = segments;
    },
    clearAll() {
      this.$store.state.bluetooth.segGraph = [];
    },
    addFromMap() {
      this.$store.state.bluetooth.modes.addFromMap = true;
      this.$store.commit('bluetooth/SET_SELECTED_PAGE', 0);
    },
    fetchTTData(segsToAdd) {
      let seg = segsToAdd.shift();
      let linkId = seg.info.linkId;
      let printInfo = false;
      let dt = this.$store.state.bluetooth.selectedDatetime;
      Api.fetchCurrTTByLinkId(linkId, dt.valueOf(), printInfo).then(
        currData => {
          Api.fetchHistoricalTTWIncidentsByLinkID(linkId, printInfo).then(
            histData => {
              if (seg) {
                this.$set(seg, 'data', {
                  currTT: currData,
                  histTT: histData
                });
              }
              if (segsToAdd.length > 0 && this.$store.state.bluetooth.segGraph.length > 0) {
                this.fetchTTData(segsToAdd);
              }
            },
            error => {
              console.log(error);
            }
          );
        },
        error => {
          console.log(error);
        }
      );
    }
  },
  computed: {
    col() {
      return this.colCount[this.$store.state.bluetooth.prefs.multiGraph.colCountIdx];
    },
    segments() {
      return this.$store.state.bluetooth.apiData.segments;
    }
  },
  watch: {
    '$store.state.bluetooth.segGraph': {
      deep: true,
      handler: function(newVal, oldVal) {
        if (JSON.stringify(newVal) != JSON.stringify(oldVal)) {
          let newSegs = newVal.map(x => x.info.description);
          let oldSegs = oldVal.map(x => x.info.description);
          console.log('segGraph - oldSegs: %o, newSegs: %o', oldSegs, newSegs);
          let segsToAdd = [];
          newSegs.forEach((segName, i) => {
            if (!oldSegs.includes(segName)) {
              segsToAdd.push(newVal[i]);
            }
          });
          if (segsToAdd.length > 0) {
            this.fetchTTData(segsToAdd);
          }
        }
      }
    }
  }
};
</script>

<style scoped>
.button-group {
  position: absolute;
  right: 10px;
}
.middle-header {
  text-align: center;
  margin: 0 auto;
  padding: 10px;
}
.v-input.theme--dark.v-text-field.v-text-field--is-booted.v-select.v-select--chips.v-select--chips--small.v-select--is-multi.v-autocomplete {
  padding-top: 4px;
}
</style>

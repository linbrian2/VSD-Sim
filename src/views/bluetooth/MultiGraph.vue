<template>
  <div class="multigraph">
    <v-container fluid style="max-width: 85%">
      <v-card class="rounded-0 elevation-5" :loading="loadingData" style="height: 90vh; overflow-y: auto;">
        <v-card-title>Multigraph</v-card-title>
        <v-card-text class="mb-10">
          <v-container fluid>
            <div style="display: flex">
              <v-autocomplete
                v-model="segGraph"
                dense
                :items="segments"
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
              <MGFilterMenu />
              <!-- Options Button -->
              <MGOptionsMenu />
              <!-- Clear Button -->
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn v-bind="attrs" v-on="on" @click="clearAll()" class="mx-1">
                    <v-icon>mdi-backspace-outline</v-icon>
                  </v-btn>
                </template>
                <span>Clear all</span>
              </v-tooltip>
            </div>
          </v-container>
          <v-container v-if="segGraph" fluid>
            <v-row>
              <v-col :cols="col[0]" :lg="col[1]" :xl="col[2]" v-for="(s, i) in segGraph" :key="s.id">
                <!-- Current & Baseline travel time found -->
                <v-card v-if="s.data && !reload">
                  <AreaRangeChart
                    :historicData="s.data.histTT"
                    :currData="s.data.currTT"
                    :interval="5"
                    :minWidth="320"
                    :height="prefs.multiGraph.graphHeight"
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
                    <v-row v-if="prefs.multiGraph.showButtons">
                      <v-col cols="6" class="pt-1 pr-1 pb-0">
                        <v-btn @click="viewAdditionalInfo(s)" block dense>Additional Info</v-btn>
                      </v-col>
                      <v-col cols="6" class="pt-1 pl-1 pb-0">
                        <v-btn @click="goToMapLocation(s)" block dense>Go to location</v-btn>
                      </v-col>
                    </v-row>
                  </div>
                </v-card>
                <v-card :height="prefs.multiGraph.graphHeight + 40" loading v-else> </v-card>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import { RouterPaths } from '@/utils/constants/router';
import Api from '@/utils/api/bluetooth.js';
import AreaRangeChart from '@/components/modules/bluetooth/graphs/AreaRangeChart';
import MGFilterMenu from '@/components/modules/bluetooth/ui/MGFilterMenu.vue';
import MGOptionsMenu from '@/components/modules/bluetooth/ui/MGOptionsMenu.vue';

export default {
  components: {
    AreaRangeChart,
    MGFilterMenu,
    MGOptionsMenu
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
    let newSegs = this.segGraph;
    let segsToAdd = [];
    newSegs.forEach(seg => {
      segsToAdd.push(seg);
    });
    if (segsToAdd.length > 0) {
      this.fetchTTData(segsToAdd);
    }

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
      let segGraphCopy = JSON.parse(JSON.stringify(this.segGraph));
      this.clearAll();
      this.segGraph = segGraphCopy;
    });
  },
  methods: {
    goToMapLocation(segment) {
      this.$bus.$emit('GO_TO_SEGMENT_LOCATION', segment);
      this.map.setZoom(14);
      this.$store.commit('bluetooth/SET_SELECTED_PAGE', 0);
    },
    viewAdditionalInfo(segment) {
      this.$store.state.bluetooth.selectedSeg.data = segment;
      this.$store.commit('bluetooth/SET_TT_DIALOG', true);
    },
    processSegments(segsList) {
      setTimeout(() => {
        this.segGraph = segsList;
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
    filterByRoute(segments, filterRoutes) {
      if (filterRoutes && filterRoutes.length > 0) {
        let arr = segments.filter(x => {
          for (let i = 0; i < filterRoutes.length; i++) {
            if (filterRoutes[i] == x.info.route) {
              return true;
            }
          }
          return false;
        });
        return arr;
      } else {
        return segments;
      }
    },
    processFilters(filters) {
      let filteredRouteSegments = this.filterByRoute(this.segments, filters.routes);
      let filteredSegments = filteredRouteSegments.filter(s => {
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
      filteredSegments.forEach(s => {
        segments.push(s);
      });
      this.segGraph = segments;
    },
    clearAll() {
      this.segGraph = [];
    },
    addFromMap() {
      this.$store.state.bluetooth.modes.addFromMap = true;
      let path = RouterPaths.BLUETOOTH_DASHBOARD;
      this.$router.push({ path }).catch(() => {});
    },
    fetchTTData(segsToAdd) {
      let seg = segsToAdd.shift();
      let linkId = seg.info.linkId;
      let dt = this.currentDate;
      Api.fetchCurrTTByLinkId(linkId, dt.valueOf()).then(
        currData => {
          Api.fetchHistoricalTTWIncidentsByLinkID(linkId).then(
            histData => {
              if (seg) {
                this.$set(seg, 'data', {
                  currTT: currData,
                  histTT: histData
                });
              }
              if (segsToAdd.length > 0 && this.segGraph.length > 0) {
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
    loadingData() {
      return this.segments.length == 0;
    },
    col() {
      return this.colCount[this.prefs.multiGraph.colCountIdx];
    },
    segments() {
      return this.$store.state.bluetooth.apiData.segments || [];
    },
    segGraph: {
      get() {
        return this.$store.state.bluetooth.segGraph;
      },
      set(val) {
        this.$store.commit('bluetooth/SET_SEG_GRAPH', val);
      }
    },
    ...mapState(['currentDate']),
    ...mapState('bluetooth', ['map', 'prefs'])
  },
  watch: {
    loadingData(val, oldVal) {
      if (oldVal == true && val == false) {
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },
    segGraph: {
      deep: true,
      handler: function(newVal, oldVal) {
        if (JSON.stringify(newVal) != JSON.stringify(oldVal)) {
          let newSegs = newVal.map(x => x.info.description);
          let oldSegs = oldVal.map(x => x.info.description);
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

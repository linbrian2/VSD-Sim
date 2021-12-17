<template>
  <div>
    <!-- Left map panel -->
    <SelectionPanel name="trafficFlowSideBarWidth" @navigation-width-changed="sideBarWidthChanged">
      <div class="d-flex justify-space-between">
        <v-combobox
          multiple
          small-chips
          class="mx-2"
          dense
          hide-details
          single-line
          :items="items"
          item-text="name"
          v-model="valuesSelected"
          @input="valueSelectHandler"
          label="SELECT DETECTORS TO SHOW"
          return-object
        >
          <!-- Region selection menu -->
          <template v-slot:append-outer>
            <v-btn icon @click="clear">
              <v-icon>mdi-backspace</v-icon>
            </v-btn>
            <!-- Region selection menu -->
            <v-menu bottom right offset-y min-width="250" :close-on-content-click="true">
              <template v-slot:activator="{ on: menu, attrs }">
                <v-tooltip bottom>
                  <template v-slot:activator="{ on: tooltip }">
                    <v-btn icon class="mx-1" v-bind="attrs" v-on="{ ...tooltip, ...menu }">
                      <v-icon dark>mdi-dots-vertical</v-icon>
                    </v-btn>
                  </template>
                  <span>Region Selection</span>
                </v-tooltip>
              </template>

              <v-list>
                <v-list-item
                  v-for="item in region_menu_items"
                  :key="item.value"
                  @click="regionMenuItemClicked(item.value)"
                >
                  <v-list-item-title :class="{ 'font-weight-bold': item.value === selectedRegionId }">
                    <v-icon class="mr-1" v-if="item.value === selectedRegionId">mdi-check</v-icon>
                    <span :class="{ 'ml-8': item.value !== selectedRegionId }"> {{ item.title }} </span>
                  </v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </template>
        </v-combobox>
      </div>
      <MapMultigraphSelect ref="mapSelect" :markers="markers" :icons="icons" @click="markerClicked" />
    </SelectionPanel>

    <!-- Title bar on the top -->
    <TitleBar :isMultigraph="true" :showId="false" :loading="loading" :refresh="refreshData">
      <div class="d-flex align-items justify-space-between align-center">
        <div class="d-flex justify-space-between">
          <div class="mt-1 ml-6 mr-6" style="width: 120px">
            <v-select
              dark
              dense
              v-model="selectedVal"
              :items="valItems"
              item-text="text"
              item-value="value"
              hide-details
              single-line
            />
          </div>

          <div class="mt-1 mr-6" style="width: 128px">
            <v-select
              dark
              dense
              v-model="interval"
              :items="intervalItems"
              item-text="text"
              item-value="value"
              @input="intervalSelected"
              hide-details
              prepend-icon="mdi-clock-outline"
              single-line
            />
          </div>
        </div>
      </div>
    </TitleBar>

    <!-- Charts -->
    <v-container>
      <v-tabs color="teal accent-4" v-model="tab" @change="tabChanged">
        <v-tab v-for="item in tabItems" :key="item.key" :href="`#${item.key}`">
          {{ item.value }}
        </v-tab>
      </v-tabs>
      <div>
        <v-tabs-items v-model="tab">
          <v-tab-item value="bound" v-if="isTabVisible('bound')">
            <MultigraphDataEntries
              :valuesSelected="valuesSelected"
              :param="'boundData'"
              :flowParams="flowParams"
              @removeItem="removeItem"
            />
          </v-tab-item>

          <v-tab-item value="lane" v-if="isTabVisible('lane')">
            <MultigraphDataEntries
              :valuesSelected="valuesSelected"
              :param="'laneData'"
              :flowParams="flowParams"
              @removeItem="removeItem"
            />
          </v-tab-item>

          <v-tab-item value="minute" v-if="isTabVisible('minute')">
            <MultigraphDataEntries
              :valuesSelected="valuesSelected"
              :param="'minuteData'"
              :flowParams="flowParams"
              @removeItem="removeItem"
            />
          </v-tab-item>
        </v-tabs-items>
      </div>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapMultigraphSelect from '@/components/modules/traffic/map/MapMultigraphSelect';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import MultigraphDataEntries from './MultigraphDataEntries.vue';

export default {
  components: {
    SelectionPanel,
    MapMultigraphSelect,
    TitleBar,
    MultigraphDataEntries
  },
  data: () => ({
    startDelay: true,
    selectedVal: 'Volume',
    valItems: ['Speed', 'Volume', 'Occupancy'],

    loading: false,
    showBaseline: true,
    interval: 300000,
    icons: [
      {
        path: 0,
        scale: 10.0,
        fillColor: '#05FF00',
        fillOpacity: 0.8,
        strokeWeight: 0.4
      },
      {
        path: 0,
        scale: 10.0,
        fillColor: '#FF7F00',
        fillOpacity: 0.8,
        strokeWeight: 0.4
      }
    ],

    tabItems: [
      { key: 'bound', value: 'Per Bound' },
      { key: 'lane', value: 'Per Lane' },
      { key: 'minute', value: 'Per Minute' }
    ],
    tab: null,

    valuesSelected: [],
    intervalItems: [
      { text: '1 Hour', value: 3600000 },
      { text: '30 mins', value: 1800000 },
      { text: '15 mins', value: 900000 },
      { text: '5 mins', value: 300000 },
      { text: '1 min', value: 60000 }
    ],
    direction: '',

    region_menu_items: [
      { title: 'All Detectors', value: -1 },
      { title: 'Urban Freeway Detectors', value: 1 },
      { title: 'Urban System Detectors', value: 2 },
      { title: 'CAV Area Freeway Detectors', value: 3 },
      { title: 'CAV Area System Detectors', value: 4 },
      { title: ' Rural Freeway Detectors', value: 5 },
      { title: 'Rural System Detectors', value: 6 },
      { title: 'Outside study area', value: 7 }
    ],

    selectedRegionId: -1,

    devices: []
  }),

  computed: {
    flowParams() {
      return {
        isFlow: true,
        direction: this.direction,
        limitGraph: this.selectedVal
      };
    },

    markers() {
      if (!this.startDelay) {
        if (this.selectedRegionId < 0) {
          return this.devices;
        } else {
          return this.devices.filter(location => location.flags === this.selectedRegionId);
        }
      } else {
        return [];
      }
    },

    items() {
      return this.markers.map(location => {
        return { id: location.id, name: location.name, data: null };
      });
    },

    name() {
      const marker = this.activeMarker;
      return marker && marker.uid;
    },

    multigraphModeSelect: {
      get() {
        return this.$store.state.traffic.multigraphModeSelect;
      },
      set(val) {
        this.$store.commit('traffic/SET_MULTIGRAPH_MODE_SELECT', val);
      }
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['activeMarker', 'multigraphModes'])
  },

  mounted() {
    setTimeout(() => {
      this.startDelay = false;
    }, 100);
    this.fetchDevices();
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  methods: {
    clear() {
      this.valuesSelected = [];
      this.$bus.$emit('NAME_SELECTED', []);
    },

    removeItem(item) {
      this.valuesSelected = this.valuesSelected.filter(x => x.id && x.id != item.id);
      this.$bus.$emit('NAME_SELECTED', this.valuesSelected);
    },

    sideBarWidthChanged() {
      this.$bus.$emit('CHART_RELOAD');
    },

    valueSelectHandler(value) {
      if (value && value.length > 0 && value[value.length - 1]) {
        let marker = this.markers.find(m => m.name === value[value.length - 1].name);
        const time = this.currentDate.getTime();
        this.fetchTrafficFlowData(marker.id, marker.uid, this.interval, time);
        this.$bus.$emit('NAME_SELECTED', value);
      }
    },

    regionMenuItemClicked(value) {
      setTimeout(() => {
        this.selectedRegionId = value;
        this.valuesSelected = [];
      }, 100);
    },

    isTabVisible(name) {
      return this.tabItems.find(i => i.key === name) !== undefined;
    },

    markerClicked(marker, action, fromMap = true) {
      if (fromMap) {
        if (action == 'remove') {
          this.valuesSelected = this.valuesSelected.filter(x => x.name && x.name != marker.name);
        } else {
          this.valuesSelected.push({ id: marker.id, name: marker.name, data: null });
          const time = this.currentDate.getTime();
          this.fetchTrafficFlowData(marker.id, marker.uid, this.interval, time);
        }
      }
    },

    refreshData() {
      this.fetchData();
    },

    tabChanged() {},
    directionSelected() {},

    intervalSelected() {
      this.fetchData();
    },

    fetchData() {
      const time = this.currentDate.getTime();

      this.valuesSelected.forEach(x => {
        let marker = this.markers.find(m => m.name === x.name);
        this.fetchTrafficFlowData(marker.id, marker.uid, this.interval, time);
      });
    },

    async fetchDevices() {
      try {
        const response = await Api.fetchDevices();
        this.devices = response.data;
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async fetchTrafficFlowData(id, uid, interval, time) {
      this.loading = true;
      try {
        // Now we await for all results, whose async processes have already been started
        const [baselineRes, perBoundRes, perLaneRes, perMinRes] = await Promise.all([
          Api.fetchTrafficFlowBaselineData(id, null, interval, time, null),
          Api.fetchTrafficFlowData(id, null, interval, time, null),
          Api.fetchTrafficFlowPerLaneData(id, uid, null, interval, time, null),
          Api.fetchTrafficFlowPerMinuteData(id, null, interval, time, null)
        ]);

        const dataList = [];
        dataList.push(this.getResponseData(perBoundRes, false));
        dataList.push(this.getResponseData(perLaneRes, false));
        dataList.push(this.getResponseData(perMinRes, false));

        let boundData = dataList[0] ? dataList[0] : {};
        let laneData = dataList[1] ? dataList[1] : {};
        let minuteData = dataList[2] ? dataList[2] : {};

        const baselineData = this.getResponseData(baselineRes, false);
        if (baselineData && !Utils.isEmpty(baselineData)) {
          // modify the color and name
          baselineData.speed.forEach((speed, index) => {
            baselineData.speed[index].color = '#50B432'; //Utils.shadeColor(speed.color, 90);
            baselineData.speed[index].name = 'Baseline'; // + speed.name;
          });

          baselineData.volume.forEach((volume, index) => {
            baselineData.volume[index].color = '#50B432'; // Utils.shadeColor(volume.color, 90);
            baselineData.volume[index].name = 'Baseline'; //+ volume.name;
          });

          baselineData.occupancy.forEach((occupancy, index) => {
            baselineData.occupancy[index].color = '#50B432'; //Utils.shadeColor(occupancy.color, 90);
            baselineData.occupancy[index].name = 'Baseline'; // + occupancy.name;
          });

          // Merge with bound data
          boundData.speed.push(...baselineData.speed);
          boundData.volume.push(...baselineData.volume);
          boundData.occupancy.push(...baselineData.occupancy);
        }

        let data = -1;
        if (dataList) {
          data = {
            boundData: boundData,
            laneData: laneData,
            minuteData: minuteData
          };
        }
        this.valuesSelected.forEach(val => {
          if (val.id == id) {
            val.data = data;
          }
        });

        if (dataList.every(item => item === null)) {
          this.showWarningMessage('No data available');
        }
      } catch (error) {
        this.valuesSelected.forEach(val => {
          if (val.id == id) {
            val.data = -1;
          }
        });
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    getResponseData(response, showWarning) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        if (showWarning) {
          this.showWarningMessage(response.data.message);
        }
      }
      return result;
    },

    showWarningMessage(message) {
      this.$store.dispatch('setSystemStatus', { text: message, color: 'info', timeout: 1000 });
    }
  }
};
</script>

<style lang="scss" scoped>
.graph-container {
  position: relative;
}
.graph-close-button {
  position: absolute;
  right: 0px;
  z-index: 99;
}
.basic-chart {
  height: 500px;
}
.active-select {
  background-color: red;
  color: white;
  border-style: solid;
  border-width: 5px;
}
.option-left {
  margin-left: 0;
  margin-right: 0;
}
</style>

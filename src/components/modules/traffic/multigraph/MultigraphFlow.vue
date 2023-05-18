<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
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
          <template v-slot:selection="{ attrs, item, parent, selected }">
            <v-chip v-if="item === Object(item)" v-bind="attrs" :input-value="selected" label small>
              <span class="pr-1">
                {{ item.permit }}
              </span>
              <v-icon small @click="parent.selectItem(item)">
                $delete
              </v-icon>
            </v-chip>
          </template>

          <!-- Region selection menu -->
          <template v-slot:append-outer>
            <v-btn icon @click="clear">
              <v-icon>mdi-backspace</v-icon>
            </v-btn>
            <!-- Region selection menu -->
            <DeviceSelectionMenu icon="mdi-dots-vertical" tooltip="Device Filter" v-model="deviceSelection" />
          </template>
        </v-combobox>
      </div>
      <MapMultigraphSelect ref="mapSelect" :markers="markers" :icons="icons" @click="markerClicked" />
    </SelectionPanel>

    <!-- Title bar on the top -->
    <TitleBar :loading="loading" :refresh="refreshData">
      <div class="d-flex align-items justify-center align-center">
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

          <div class="mt-1 mr-6" style="width: 100px">
            <v-select
              dark
              dense
              v-model="interval"
              :items="intervalItems"
              item-text="text"
              item-value="value"
              @input="intervalSelected"
              hide-details
              single-line
            />
          </div>

          <div class="mt-1" style="width: 110px">
            <v-select
              dark
              dense
              v-model="cols"
              :items="columnItems"
              item-text="text"
              item-value="value"
              hide-details
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
              :cols="cols"
              @removeItem="removeItem"
            />
          </v-tab-item>

          <v-tab-item value="lane" v-if="isTabVisible('lane')">
            <MultigraphDataEntries
              :valuesSelected="valuesSelected"
              :param="'laneData'"
              :flowParams="flowParams"
              :cols="cols"
              @removeItem="removeItem"
            />
          </v-tab-item>

          <v-tab-item value="minute" v-if="isTabVisible('minute')">
            <MultigraphDataEntries
              :valuesSelected="valuesSelected"
              :param="'minuteData'"
              :flowParams="flowParams"
              :cols="cols"
              @removeItem="removeItem"
            />
          </v-tab-item>
        </v-tabs-items>
      </div>
    </v-container>
  </div>

  <div class="mobile" v-else>
    <TitleBar :loading="loading" :refresh="refreshData" :showRefresh="!$vuetify.breakpoint.xs" :showMap="false">
      <div :style="'height: 45px'" />
    </TitleBar>

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
        <template v-slot:selection="{ attrs, item, parent, selected }">
          <v-chip v-if="item === Object(item)" v-bind="attrs" :input-value="selected" label small>
            <span class="pr-1">
              {{ item.permit }}
            </span>
            <v-icon small @click="parent.selectItem(item)">
              $delete
            </v-icon>
          </v-chip>
        </template>

        <!-- Region selection menu -->
        <template v-slot:append-outer>
          <v-btn icon @click="clear">
            <v-icon>mdi-backspace</v-icon>
          </v-btn>
          <!-- Region selection menu -->
          <DeviceSelectionMenu icon="mdi-dots-vertical" tooltip="Device Filter" v-model="deviceSelection" />
        </template>
      </v-combobox>
    </div>

    <MapMultigraphSelect ref="mapSelect" :markers="markers" :icons="icons" @click="markerClicked" />

    <v-container>
      <div class="d-flex align-items justify-center align-center mb-3">
        <div class="d-flex justify-space-between">
          <div class="mt-1 ml-6 mr-6" style="width: 120px">
            <v-select
              filled
              dense
              v-model="selectedVal"
              :items="valItems"
              item-text="text"
              item-value="value"
              hide-details
              single-line
            />
          </div>

          <div class="mt-1 mr-6" style="width: 100px">
            <v-select
              filled
              dense
              v-model="interval"
              :items="intervalItems"
              item-text="text"
              item-value="value"
              @input="intervalSelected"
              hide-details
              single-line
            />
          </div>
        </div>
      </div>
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
              :cols="cols"
              @removeItem="removeItem"
            />
          </v-tab-item>

          <v-tab-item value="lane" v-if="isTabVisible('lane')">
            <MultigraphDataEntries
              :valuesSelected="valuesSelected"
              :param="'laneData'"
              :flowParams="flowParams"
              :cols="cols"
              @removeItem="removeItem"
            />
          </v-tab-item>

          <v-tab-item value="minute" v-if="isTabVisible('minute')">
            <MultigraphDataEntries
              :valuesSelected="valuesSelected"
              :param="'minuteData'"
              :flowParams="flowParams"
              :cols="cols"
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
// import TrafficConstants from '@/utils/constants/traffic';
import { mapState } from 'vuex';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapMultigraphSelect from '@/components/modules/traffic/map/MapMultigraphSelect';
import TitleBar from '@/components/modules/traffic/multigraph/TitleBar';
import MultigraphDataEntries from './MultigraphDataEntries.vue';
import DeviceSelectionMenu from '@/components/modules/traffic/common/DeviceSelectionMenu';

export default {
  components: {
    SelectionPanel,
    MapMultigraphSelect,
    TitleBar,
    DeviceSelectionMenu,
    MultigraphDataEntries
  },
  data: () => ({
    startDelay: true,
    selectedVal: 'Volume',
    valItems: ['Speed', 'Volume', 'Occupancy'],
    cols: 12,

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

    columnItems: [
      { text: '1 column', value: 12 },
      { text: '2 column', value: 6 },
      { text: '3 column', value: 4 }
    ],

    tab: null,

    valuesSelected: [],
    intervalItems: [
      { text: '1 Hour', value: 3600000 },
      { text: '30 min', value: 1800000 },
      { text: '15 min', value: 900000 },
      { text: '5 min', value: 300000 },
      { text: '1 min', value: 60000 }
    ],
    direction: '',

    deviceSelection: {
      regionId: -1,
      typeId: -1
    },
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
        if (this.deviceSelection.regionId > 0 && this.deviceSelection.typeId < 0) {
          return this.devices.filter(location => location.region === this.deviceSelection.regionId);
        } else if (this.deviceSelection.regionId < 0 && this.deviceSelection.typeId > 0) {
          return this.devices.filter(location => location.type === this.deviceSelection.typeId);
        } else if (this.deviceSelection.regionId > 0 && this.deviceSelection.typeId > 0) {
          return this.devices.filter(
            location =>
              location.region === this.deviceSelection.regionId && location.type === this.deviceSelection.typeId
          );
        } else {
          return this.devices;
        }
      } else {
        return [];
      }
    },

    items() {
      return this.markers.map(location => {
        return { id: location.id, permit: location.uid, name: location.name, data: null };
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
    cols() {
      this.$bus.$emit('CHART_RELOAD');
    },
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
      // this.$bus.$emit('CHART_RELOAD');
    },

    valueSelectHandler(value) {
      if (value && value.length > 0 && value[value.length - 1]) {
        let marker = this.markers.find(m => m.name === value[value.length - 1].name);
        const time = this.currentDate.getTime();
        this.fetchTrafficFlowData(marker.id, marker.uid, this.interval, time);
        this.$bus.$emit('NAME_SELECTED', value);
      }
    },

    isTabVisible(name) {
      return this.tabItems.find(i => i.key === name) !== undefined;
    },

    markerClicked(marker, action, fromMap = true) {
      if (fromMap) {
        if (action == 'remove') {
          this.valuesSelected = this.valuesSelected.filter(x => x.name && x.name != marker.name);
        } else {
          this.valuesSelected.push({ id: marker.id, permit: marker.uid, name: marker.name, data: null });
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

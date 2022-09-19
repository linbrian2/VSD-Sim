<template>
  <div>
    <!-- Left map panel -->
    <SelectionPanel name="signalsBarWidth">
      <v-combobox
        class="mx-2"
        dense
        hide-details
        single-line
        item-text="text"
        item-value="value"
        :items="groupItems"
        :value="valueSelected"
        @input="groupSelectionHandler"
        label="CHOOSE A ZONE TO SHOW"
      />
      <MapSelect ref="mapSelect" :markers="markers" :icons="icons" @click="onMapClick" />
    </SelectionPanel>

    <TitleBar :title="title" :loading="loading" :refresh="refreshData" :showMap="true"> </TitleBar>

    <v-container>
      <div class="mx-4">
        <div v-if="infoAvailable">
          <v-row>
            <v-col cols="12">
              <div class="d-flex justify-space-between">
                <v-subheader class="pl-0 text-overline blue--text">
                  <h3>Intersection Info</h3>
                </v-subheader>

                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn icon @click.stop="showInfo = !showInfo" v-bind="attrs" v-on="on">
                      <v-icon small dark>mdi-chevron-down</v-icon>
                    </v-btn>
                  </template>
                  <span>Hide Info</span>
                </v-tooltip>
              </div>

              <v-divider />
            </v-col>
          </v-row>

          <v-row v-show="showInfo">
            <v-col cols="4">
              <DataCard title="Basic" :items="detectorInfo['basic']" />
            </v-col>
            <v-col cols="4">
              <DataCard title="ID" :items="detectorInfo['ids']" />
            </v-col>
            <v-col cols="4">
              <DataCard title="Misc" :items="detectorInfo['misc']" />
            </v-col>
          </v-row>
        </div>

        <v-row>
          <v-col cols="12">
            <v-subheader class="pl-0 text-overline blue--text">
              <h3>Signal Cycle Patterns</h3>
            </v-subheader>
            <v-divider />
          </v-col>

          <v-col cols="12">
            <v-card tile class="mt-n2" elevation="12" v-if="currentSignal">
              <SignalTable :items="currentSignal" />
            </v-card>
          </v-col>
        </v-row>
      </div>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { TrafficLightIcons } from '@/mixins/TrafficLightIcons.js';
import { RouterNames } from '@/utils/constants/router';
import SignalTable from '@/components/modules/traffic/tables/SignalTable';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapSelect from '@/components/modules/traffic/map/MapSelect';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import Utils from '@/utils/Utils';
import DataCard from '@/components/modules/traffic/common/DataCard';

export default {
  mixins: [TrafficLightIcons],
  components: {
    SelectionPanel,
    MapSelect,
    TitleBar,
    SignalTable,
    DataCard
  },

  data: () => ({
    delayComplete: false,
    loading: false,
    defaultHeight: 650,
    title: RouterNames.TRAFFIC_SIGNALS,
    showInfo: true,
    chartData: {},
    currentPermit: null,
    currentGroupId: null,
    currentSignal: null,
    valueSelected: null,
    deviceList: null,
    deviceGroups: null,
    detectorInfo: {},
    icons: [
      {
        url: require('@/assets/traffic_light_xsmall.png'),
        size: { width: 10, height: 24, f: 'px', b: 'px' },
        anchor: { x: 5, y: 12 }
      },
      {
        url: require('@/assets/traffic_light_active.png'),
        size: { width: 15, height: 36, f: 'px', b: 'px' },
        anchor: { x: 8, y: 18 }
      },
      {
        url: require('@/assets/traffic_light_selected.png'),
        size: { width: 15, height: 36, f: 'px', b: 'px' },
        anchor: { x: 8, y: 18 }
      }
    ]
  }),

  computed: {
    markers() {
      return !this.deviceList ? [] : this.deviceList;
    },

    infoAvailable() {
      return !Utils.isEmpty(this.detectorInfo);
    },

    groupItems() {
      if (!this.deviceGroups) {
        return [];
      }

      const items = [];
      Object.entries(this.deviceGroups).forEach(entry => {
        if (entry[0]) {
          const desc = entry[1].length > 0 ? entry[1][0].groupName : 'No Description';
          items.push({
            text: 'Zone: ' + desc,
            value: entry[0]
          });
        }
      });
      return items;
    }
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  mounted() {
    if (!this.deviceList) {
      this.fetchTrafficSignalDeviceList();
    }

    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.delayComplete = true;
      this.showDataIfEmpty();
    }, 500);
  },

  methods: {
    showDataIfEmpty() {
      this.$bus.$emit('CENTER_MAP');
      this.$bus.$emit('SELECT_FIRST');
    },

    onMapClick(marker) {
      const groupId = marker.groupId;
      this.currentGroupId = groupId;
      this.valueSelected = this.groupItems.find(item => item.value == groupId).text;
      this.selectDetectorsByGroupId(groupId, null);
      this.fetchData(marker.uid);
    },

    groupSelectionHandler(value) {
      const groupId = value.value;
      this.currentGroupId = groupId;

      const detector = this.findFirstDetectorByGroupId(groupId);
      if (detector) {
        this.selectDetectorsByGroupId(groupId, detector.id);
        this.fetchData(detector.uid);
      }
    },

    selectDetectorsByGroupId(groupId, id) {
      if (groupId) {
        const ids = this.deviceList.filter(d => d.groupId === groupId).map(d => d.id);
        this.$refs.mapSelect.selectByIds(ids, false);
        if (id != null) {
          this.$refs.mapSelect.selectById(id);
        }
      }
    },

    findFirstDetectorByGroupId(groupId) {
      const detector = this.deviceList.find(d => d.groupId == groupId);
      return detector;
    },

    refreshData() {
      if (this.currentPermit) {
        this.fetchData(this.currentPermit);
      }
    },

    fetchData(permit) {
      this.fetchSignalCycleSplit(permit);
    },

    async fetchTrafficSignalDeviceList() {
      try {
        const response = await Api.trafficSignalDevices();
        this.deviceList = response.data;
        this.deviceGroups = this.groupSignalDevices(this.deviceList);
      } catch (error) {
        this.showWarningMessage(error);
      }
    },

    async fetchSignalCycleSplit(permit) {
      this.loading = true;
      try {
        const response = await Api.getSignalCycleSplit(permit);
        const data = this.parseResponse(response, false);
        if (data != null) {
          this.detectorInfo = this.composeDetectorInfo(data.device);
          this.currentSignal = data.signal;
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
    },

    parseResponse(response, showWarning) {
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
      this.$store.dispatch('setSystemStatus', { text: message, color: 'warning', timeout: 1500 });
    },

    groupSignalDevices(deviceList) {
      // Group the data by group
      return deviceList.reduce((r, item) => {
        r[item.groupId] = r[item.groupId] || [];
        r[item.groupId].push(item);
        return r;
      }, {});
    },

    composeDetectorInfo(info) {
      const basic = [
        { name: 'Permit:', value: info.permit },
        { name: 'Location:', value: info.name },
        { name: 'IP Addr:', value: info.ip }
      ];

      const ids = [
        { name: 'GroupId:', value: info.groupId },
        { name: 'IntersectionId:', value: info.intersectionId },
        { name: 'Parent ZoneId', value: info.parentZoneId }
      ];

      const misc = [
        { name: 'Number of Lanes:', value: info.numLanes },
        { name: 'TR Enabled:', value: info.enabledTr ? 'YES' : 'NO' },
        { name: 'Controller Version', value: info.controllerVersion }
      ];

      return { basic, ids, misc };
    }
  }
};
</script>

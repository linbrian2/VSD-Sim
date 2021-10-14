<template>
  <div>
    <SlideButtons
      class="mb-4"
      :items="filteredDevices"
      :title="evidenceText"
      :show="isEvidenceVisible"
      :callback="deviceClicked"
      :showEvidenceText="showEvidenceText"
      @expand="isEvidenceVisible = !isEvidenceVisible"
      ref="slideButtons"
    />

    <ExpansionItems
      :name="selectDeviceName"
      :infoList="trafficFlowListByDevice"
      :show="isEvidenceVisible"
      @close="hideEvidences"
    />

    <div class="mt-4" v-if="selectedDevice">
      <div v-if="includePerLane">
        <v-tabs color="teal accent-4" v-model="tab">
          <v-tab v-for="item in tabItems" :key="item.key" :href="`#${item.key}`">
            {{ item.value }}
          </v-tab>
        </v-tabs>
        <div>
          <v-tabs-items v-model="tab">
            <v-tab-item value="bound" v-if="isTabVisible('bound')">
              <div class="mt-2">
                <TrafficFlowCharts
                  ref="trafficFlowCharts"
                  mode="list"
                  :height="height"
                  :deviceId="selectedDevice.id"
                  :direction="selectedDevice.dir"
                  :name="selectedDevice.name"
                  :interval="interval"
                  :time="time"
                  :timeSlots="timeSlots"
                />
              </div>
            </v-tab-item>

            <v-tab-item value="lane" v-if="isTabVisible('lane')">
              <div class="mt-2">
                <TrafficFlowPerLaneCharts
                  ref="trafficFlowPerLaneCharts"
                  :height="height"
                  :deviceId="selectedDevice.id"
                  :direction="selectedDevice.dir"
                  :name="selectedDevice.name"
                  :interval="interval"
                  :time="time"
                  :timeSlots="timeSlots"
                />
              </div>
            </v-tab-item>
          </v-tabs-items>
        </div>
      </div>

      <div class="mt-2" v-else>
        <TrafficFlowCharts
          ref="trafficFlowCharts"
          mode="list"
          :height="height"
          :deviceId="selectedDevice.id"
          :direction="selectedDevice.dir"
          :name="selectedDevice.name"
          :interval="interval"
          :time="time"
          :timeSlots="timeSlots"
        />
      </div>
    </div>
  </div>
</template>

<script>
import ExpansionItems from '@/components/modules/traffic/incident/ExpansionItems';
import SlideButtons from '@/components/modules/traffic/incident/SlideButtons';
import TrafficFlowCharts from '@/components/modules/traffic/common/TrafficFlowCharts';
import TrafficFlowPerLaneCharts from '@/components/modules/traffic/incident/TrafficFlowPerLaneCharts';

export default {
  props: {
    infoList: Array,
    devices: Array,
    selectedSegmentId: { type: String, default: null },
    time: Date,
    timeSlots: Array,
    interval: Number,
    height: { type: Number, default: 390 },
    includePerLane: { type: Boolean, default: true },
    showEvidenceText: { type: Boolean, default: true }
  },

  components: {
    SlideButtons,
    ExpansionItems,
    TrafficFlowCharts,
    TrafficFlowPerLaneCharts
  },

  data: () => ({
    tab: null,
    isEvidenceVisible: false,
    selectedDevice: null,
    tabItems: [
      { key: 'bound', value: 'Per Bound' },
      { key: 'lane', value: 'Per Lane' }
    ]
  }),

  computed: {
    selectDeviceName() {
      return this.selectedDevice ? this.selectedDevice.name : null;
    },

    trafficFlowListByDevice() {
      if (this.selectedDevice) {
        return this.infoList.filter(item => item.device === this.selectedDevice.name);
      } else {
        return null;
      }
    },

    evidenceText() {
      const infoList = this.trafficFlowListByDevice;
      return infoList ? `${infoList.length} Evidence${infoList.length > 1 ? 's' : ''}` : '';
    },

    filteredDevices() {
      if (this.selectedSegmentId === null) {
        return this.devices;
      } else {
        return this.devices.filter(item => item.linkId === this.selectedSegmentId);
      }
    }
  },

  watch: {
    filteredDevices(devices) {
      this.selectFirst(devices);
    }
  },

  mounted() {
    this.selectFirst(this.filteredDevices);
  },

  methods: {
    selectFirst(devices) {
      const firstItem = devices.length > 0 ? devices[0] : null;
      if (firstItem) {
        this.deviceClicked(firstItem);
      }
    },

    selectItem(itemName) {
      const item = this.filteredDevices.find(item => item.name === itemName);
      if (item) {
        this.deviceClicked(item);
        this.$refs.slideButtons.selectItem(itemName);
      }
    },

    hideEvidences() {
      this.isEvidenceVisible = false;
    },

    isTabVisible(name) {
      return this.tabItems.find(i => i.key === name) !== undefined;
    },

    deviceClicked(item) {
      this.selectedDevice = item;
      this.tab = this.tabItems[0].key;
      this.$bus.$emit('INCIDENT_ITEM_SELECTED', item);
      this.updateTraficFlowCharts();
    },

    updateTraficFlowCharts() {
      if (this.selectedDevice) {
        const deviceId = this.selectedDevice.id;
        const direction = this.selectedDevice.dir;

        if (this.$refs.trafficFlowCharts) {
          this.$refs.trafficFlowCharts.init(deviceId, direction, this.time, this.timeSlots);
        }

        if (this.$refs.trafficFlowPerLaneCharts) {
          this.$refs.trafficFlowPerLaneCharts.init(deviceId, direction, this.time);
        }
      }
    }
  }
};
</script>

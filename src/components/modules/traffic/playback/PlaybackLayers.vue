<template>
  <v-menu bottom left offset-y max-width="250" :close-on-content-click="false">
    <template v-slot:activator="{ on: menu, attrs }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on: tooltip }">
          <v-btn
            v-bind="attrs"
            v-on="{ ...tooltip, ...menu }"
            :style="`position: absolute; top: 10px; left: 490px; height: 40px`"
          >
            <v-icon>mdi-layers-outline</v-icon>
          </v-btn>
        </template>
        <span>Map Layer</span>
      </v-tooltip>
    </template>

    <template>
      <v-row>
        <v-expansion-panels accordion v-if="layerItems">
          <v-expansion-panel v-for="(item, i) in layerItems" :key="i">
            <v-list-item v-if="!(item.id == 1 || item.id == 3 || item.id == 2)" class="pl-6 py-1">
              <v-checkbox
                @click.native="layerItemClicked($event, item.id)"
                hide-details
                :label="item.title"
                :value="item.id"
                class="mt-0"
                v-model="mapLayers"
              />
            </v-list-item>
            <v-expansion-panel-header v-if="item.id == 1 || item.id == 3">
              <v-checkbox
                @click.native="layerItemClicked($event, item.id)"
                hide-details
                :label="item.title"
                :value="item.id"
                class="mt-0"
                v-model="mapLayers"
              />
            </v-expansion-panel-header>
            <v-expansion-panel-content v-if="item.id == 1 || item.id == 3">
              <template v-if="item.id == 1">
                <v-switch
                  dense
                  label="Grouped"
                  v-model="mapLayers"
                  class="px-3 py-0 my-1"
                  @click.native="layerItemClicked($event, 2)"
                  hide-details
                  :value="2"
                ></v-switch>
                <div v-for="subtoggle in wazeSubtoggles" :key="subtoggle.id">
                  <v-checkbox
                    @click.native="layerItemClicked($event, item.id)"
                    v-model="wazeLayers"
                    :label="subtoggle.title"
                    :value="subtoggle.id"
                    class="pt-0 mt-0 px-3"
                    hide-details
                    dense
                  />
                </div>
              </template>
              <template v-if="item.id == 3">
                <v-checkbox
                  v-for="subtoggle in deviceSubtoggles"
                  :key="subtoggle.id"
                  @click.native="layerItemClicked($event, item.id)"
                  v-model="deviceLayers"
                  :label="subtoggle.title"
                  :value="subtoggle.id"
                  class="pt-0 mt-0 px-3"
                  hide-details
                  dense
                />
              </template>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-row>
    </template>
  </v-menu>
</template>

<script>
import Constants from '@/utils/constants/bluetooth';
import { mapState } from 'vuex';

export default {
  data: () => ({
    mapLayers: [],
    wazeLayers: [],
    deviceLayers: [],
    layerItems: [
      { title: 'Congestion', id: Constants.LAYER_CONGESTION },
      { title: 'Waze Alerts', id: Constants.LAYER_WAZE },
      { title: 'Grouped Waze Alerts', id: Constants.LAYER_GROUPED_WAZE },
      { title: 'Traffic Flow Detectors', id: Constants.LAYER_DEVICES },
      { title: 'Traffic Incidents', id: Constants.LAYER_TRAFFIC_INCIDENTS },
      { title: 'Bluetooth Sensors', id: Constants.LAYER_BLUETOOTH_SENSORS },
      { title: 'Labels', id: Constants.LAYER_LABELS }
    ],
    wazeSubtoggles: [
      { title: 'Hazard', id: Constants.WAZE_HAZARD },
      { title: 'Traffic Jam', id: Constants.WAZE_TRAFFIC_JAM },
      { title: 'Road Closed', id: Constants.WAZE_ROAD_CLOSED },
      { title: 'Accident', id: Constants.WAZE_ACCIDENT }
    ],
    deviceSubtoggles: [
      { title: 'Low Traffic', id: Constants.DEVICES_LOW_TRAFFIC },
      { title: 'Medium Traffic', id: Constants.DEVICES_MEDIUM_TRAFFIC },
      { title: 'High Traffic', id: Constants.DEVICES_HIGH_TRAFFIC }
    ]
  }),
  mounted() {
    this.mapLayers = this.mapLayerSelection;
    this.wazeLayers = this.wazeLayerSelection;
    this.deviceLayers = this.deviceLayerSelection;
    this.$bus.$on('CHANGE_LAYER', (id, op) => {
      this.changeLayer(id, op);
    });
  },
  computed: {
    ...mapState('bluetooth', ['mapLayerSelection', 'wazeLayerSelection', 'deviceLayerSelection'])
  },
  methods: {
    changeLayer(id, op) {
      if (op == 'add') {
        if (!this.mapLayers.includes(id)) {
          let newMapLayers = JSON.parse(JSON.stringify(this.mapLayers));
          newMapLayers.push(id);
          this.mapLayers = newMapLayers;
          this.layerItemClicked(null, id);
        }
      } else if (op == 'remove') {
        if (this.mapLayers.includes(id)) {
          this.mapLayers = this.mapLayers.filter(x => x != id);
          this.layerItemClicked(null, id);
        }
      }
    },
    layerItemClicked(e, id) {
      console.log(`Layer clicked (ID: ${id})`);
      if (e) {
        e.cancelBubble = true;
      }
      this.mapLayers.sort();
      this.wazeLayers.sort();
      this.deviceLayers.sort();
      let layers = {
        map: this.mapLayers,
        waze: this.wazeLayers,
        device: this.deviceLayers
      };
      this.$store.dispatch('bluetooth/saveMapLayers', layers);
    }
  }
};
</script>

<style></style>

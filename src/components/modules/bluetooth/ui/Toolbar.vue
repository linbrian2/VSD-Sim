<template>
  <v-toolbar dense floating height="40" style="position: absolute; top: 10px; left:10px; ">
    <!-- Menu Items -->
    <v-menu bottom left offset-y min-width="350">
      <template v-slot:activator="{ on, attrs }">
        <v-btn icon v-bind="attrs" v-on="on">
          <v-app-bar-nav-icon></v-app-bar-nav-icon>
        </v-btn>
      </template>
      <v-list nav dense>
        <v-list-item-group color="primary">
          <v-list-item class="my-2" v-for="item in mapMenuItems" :key="item.id" @click="menuItemClicked(item.id)">
            <v-list-item-icon>
              <v-icon v-text="item.icon"></v-icon>
            </v-list-item-icon>
            <v-list-item-content class="ml-n4 pa-3">
              <v-list-item-title class="text-md-body-2">{{ item.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-menu>
    <!-- Search Bar -->
    <v-autocomplete
      dense
      flat
      solo
      clearable
      v-model="select"
      single-line
      hide-details
      append-icon=""
      prepend-inner-icon="mdi-magnify"
      placeholder="Search Anything"
      hide-no-data
      :search-input.sync="search"
      return-object
      :items="searchItems"
      :item-text="getFieldText"
      item-value="id"
      @change="onSearchChange"
      :disabled="!fetchDone"
    />
    <!-- Map Layer Selection -->
    <v-menu bottom left offset-y max-width="250" :close-on-content-click="false">
      <template v-slot:activator="{ on: menu, attrs }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on: tooltip }">
            <v-btn icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
              <v-icon>mdi-layers-outline</v-icon>
            </v-btn>
          </template>
          <span>Map Layer Selection</span>
        </v-tooltip>
      </template>

      <template>
        <v-row>
          <v-expansion-panels accordion>
            <v-expansion-panel v-for="(item, i) in layerItems" :key="i">
              <v-list-item v-if="!(item.id == 3 || item.id == 2 || item.id == 4)" class="pl-6 py-1">
                <v-checkbox
                  @click.native="layerItemClicked($event, item.id)"
                  hide-details
                  :label="item.title"
                  :value="item.id"
                  class="mt-0"
                  v-model="mapLayers"
                />
              </v-list-item>
              <v-expansion-panel-header v-if="item.id == 3 || item.id == 2">
                <v-checkbox
                  @click.native="layerItemClicked($event, item.id)"
                  hide-details
                  :label="item.title"
                  :value="item.id"
                  class="mt-0"
                  v-model="mapLayers"
                />
              </v-expansion-panel-header>
              <v-expansion-panel-content v-if="item.id == 3 || item.id == 2">
                <template v-if="item.id == 3">
                  <v-switch
                    dense
                    label="Grouped"
                    v-model="mapLayers"
                    class="px-3 py-0 my-1"
                    @click.native="layerItemClicked($event, 4)"
                    hide-details
                    :value="4"
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
                <template v-if="item.id == 2">
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
  </v-toolbar>
</template>

<script>
export default {
  props: ['searchItems', 'fetchDone'],
  data: () => ({
    select: null,
    search: null,
    mapLayers: [],
    wazeLayers: [],
    deviceLayers: [],
    maxWidth: 40,
    mapMenuItems: [
      { title: 'Congestion', icon: 'mdi-car-multiple', id: 0 },
      { title: 'Traffic Flow Detectors', icon: 'mdi-leak', id: 2 },
      { title: 'Bluetooth Sensors', icon: 'mdi-bluetooth-connect', id: 1 },
      { title: 'Waze Alerts', icon: 'mdi-waze', id: 3 }
    ],
    layerItems: [
      { title: 'Congestion', id: 0 },
      { title: 'Waze Alerts', id: 3 },
      { title: 'Grouped Waze Alerts', id: 4 },
      { title: 'Traffic Flow Detectors', id: 2 },
      { title: 'Bluetooth Sensors', id: 1 },
      { title: 'Labels', id: 6 }
    ],
    wazeSubtoggles: [
      { title: 'Hazard', id: 0 },
      { title: 'Traffic Jam', id: 1 },
      { title: 'Road Closed', id: 2 },
      { title: 'Accident', id: 3 }
      /* {title: "Grouped", id: 4}, */
    ],
    deviceSubtoggles: [
      { title: 'Low Traffic', id: 0 },
      { title: 'Medium Traffic', id: 1 },
      { title: 'High Traffic', id: 2 }
    ]
  }),
  watch: {
    select() {
      this.$nextTick(() => {
        this.select = null;
      });
    }
  },
  mounted() {
    this.mapLayers = this.$store.state.bluetooth.mapLayerSelection;
    this.wazeLayers = this.$store.state.bluetooth.wazeLayerSelection;
    this.deviceLayers = this.$store.state.bluetooth.deviceLayerSelection;
    this.$bus.$on('CHANGE_LAYER', (id, op) => {
      this.changeLayer(id, op);
    });
  },
  methods: {
    changeLayer(id, op) {
      if (op == 'add') {
        console.log(this.mapLayers);
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
    getFieldText(item) {
      return `${item.info.name} - (${item.info.description})`;
    },
    onSearchChange(item) {
      if (item) {
        /* console.log(`onSearchChange(item): ${item}`); */
        this.$bus.$emit('DISPLAY_MARKER_DETAILS', item);
      }
    },
    menuItemClicked(type) {
      /* console.log(`menuItemClicked(type): ${type}`); */
      this.$bus.$emit('SHOW_SELECTION_POPUP', type);
    },
    layerItemClicked(e, id) {
      console.log(e);
      console.log(id);
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

<style lang="scss" scoped></style>

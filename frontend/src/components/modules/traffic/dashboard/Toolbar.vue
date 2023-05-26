<template>
  <v-toolbar light dense floating height="40" style="position: absolute; top: 10px; left:10px; ">
    <v-menu light bottom left offset-y min-width="350">
      <template v-slot:activator="{ on, attrs }">
        <v-btn icon v-bind="attrs" v-on="on">
          <v-app-bar-nav-icon></v-app-bar-nav-icon>
        </v-btn>
      </template>

      <v-list nav dense>
        <v-list-item-group color="primary">
          <v-list-item class="my-1" v-for="item in map_menu_items" :key="item.id" @click="menuItemClicked(item.id)">
            <v-list-item-icon>
              <v-icon v-text="item.icon"></v-icon>
            </v-list-item-icon>
            <v-list-item-content class="ml-n4 pa-2">
              <v-list-item-title class="text-md-body-2">{{ item.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-menu>
    <v-divider vertical />

    <ExpandableSearch :entities="entities" @search="onSearchChange" />

    <v-divider vertical class="ml-4" />

    <v-menu light bottom right offset-y min-width="250" :close-on-content-click="true">
      <template v-slot:activator="{ on: menu, attrs }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on: tooltip }">
            <v-btn text color="grey darken-1" v-bind="attrs" v-on="{ ...tooltip, ...menu }">
              <div class="d-flex align-center">
                <v-icon>mdi-map-outline</v-icon>
                <span>{{ currentRegionShortName }}</span>
              </div>
            </v-btn>
          </template>
          <span>Map Region</span>
        </v-tooltip>
      </template>

      <v-list>
        <v-list-item v-for="item in region_menu_items" :key="item.value" @click="regionMenuItemClicked(item.value)">
          <v-list-item-title :class="{ 'font-weight-bold': item.value === mapRegionSelection }">
            <v-icon class="mr-1" v-if="item.value === mapRegionSelection">mdi-check</v-icon>
            <span :class="{ 'ml-8': item.value !== mapRegionSelection }"> {{ item.title }}</span>
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>

    <v-divider vertical />

    <v-menu light bottom right offset-y min-width="250" :close-on-content-click="false">
      <template v-slot:activator="{ on: menu, attrs }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on: tooltip }">
            <v-btn icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
              <v-icon>mdi-layers-outline</v-icon>
            </v-btn>
          </template>
          <span>Map Layer</span>
        </v-tooltip>
      </template>

      <v-list dense>
        <v-list-item-group>
          <v-list-item v-for="item in layer_items" :key="item.id" @click="layerItemClicked(item.id)">
            <v-list-item-content class="mt-1 pa-2">
              <v-checkbox hide-details :label="item.title" :value="item.id" class="mt-0" v-model="mapLayers" />
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-menu>

    <v-divider vertical />

    <MessageDropdown />
  </v-toolbar>
</template>

<script>
import Constants from '@/utils/constants/traffic';
import { mapState } from 'vuex';
import ExpandableSearch from '@/components/common/ExpandableSearch';
import MessageDropdown from '@/components/modules/traffic/dashboard/MessageDropdown';
export default {
  props: ['entities'],
  components: {
    ExpandableSearch,
    MessageDropdown
  },
  data: () => ({
    loading: false,
    search: null,

    mapLayers: [],

    map_menu_items: [
      { title: 'Traffic Sensors', icon: Constants.DEVICE_TRAFFIC_ICON, id: 0 },
      { title: 'Show multiple cameras', icon: Constants.DEVICE_TRAFFIC_CAMERA, id: 1 }
    ],

    tool_menu_items: [{ title: 'Map Layer Selection', icon: 'mdi-layers-triple', id: 5 }],

    layer_items: [
      { title: Constants.DEVICE_TRAFFIC, id: Constants.LAYER_DEVICE_TRAFFIC },
      { title: Constants.DEVICE_BLUETOOTH, id: Constants.LAYER_DEVICE_BLUETOOTH },
      { title: Constants.DEVICE_WEATHER, id: Constants.LAYER_DEVICE_WEATHER },
      { title: Constants.DEVICE_CAMERAS, id: Constants.LAYER_DEVICE_CAMERAS },
      { title: Constants.DEVICE_SIGNALS, id: Constants.LAYER_DEVICE_SIGNALS },
      { title: Constants.DEVICE_RESTRICTIONS, id: Constants.LAYER_DEVICE_RESTRICTIONS },
      { title: Constants.DEVICE_WAZE_ALERTS, id: Constants.LAYER_DEVICE_WAZE_ALERTS },
      { title: Constants.DEVICE_INCIDENTS, id: Constants.LAYER_DEVICE_INCIDENTS },
      { title: Constants.DEVICE_CONGESTED_SEGMENTS, id: Constants.LAYER_DEVICE_CONGESTED_SEGMENTS },
      { title: Constants.DEVICE_ANOMALY_DEVICES, id: Constants.LAYER_DEVICE_ANOMALY_DEVICES }
    ],

    region_menu_items: [
      { title: 'All Regions', name: 'ALL', value: -1 },
      { title: 'Urban Freeway', name: 'URBAN', value: 10 },
      { title: 'CAV Area', name: 'CAV', value: 20 },
      { title: 'Beach Area', name: 'BEACH', value: 30 }
    ]
  }),

  computed: {
    currentRegionShortName() {
      const current = this.region_menu_items.find(item => item.value === this.mapRegionSelection);
      return current ? current.name : this.region_menu_items[0].name;
    },
    ...mapState('traffic', ['mapRegionSelection'])
  },

  mounted() {
    this.mapLayers = this.$store.state.traffic.mapLayersSelection;
  },

  methods: {
    onSearchChange(item) {
      if (item) {
        this.$bus.$emit('DISPLAY_MARKER_DETAILS', { id: item.id, type: item.type });
      }
    },

    menuItemClicked(type) {
      this.$bus.$emit('SHOW_SELECTION_POPUP', type);
    },

    layerItemClicked() {
      this.mapLayers.sort();
      this.$store.dispatch('traffic/saveMapLayers', this.mapLayers);
    },

    regionMenuItemClicked(value) {
      setTimeout(() => {
        this.$store.commit('traffic/SET_MAP_REGION', value);
      }, 100);
    }
  }
};
</script>

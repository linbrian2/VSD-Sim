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
          <v-list-item class="my-2" v-for="item in map_menu_items" :key="item.id" @click="menuItemClicked(item.id)">
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

    <div style="width:200px">
      <v-autocomplete
        light
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
        item-text="desc"
        item-value="id"
        @change="onSearchChange"
      />
    </div>

    <v-divider vertical />

    <v-menu light bottom right offset-y min-width="250" :close-on-content-click="true">
      <template v-slot:activator="{ on: menu, attrs }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on: tooltip }">
            <v-btn icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
              <v-icon>mdi-map-outline</v-icon>
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
  </v-toolbar>
</template>

<script>
import Constants from '@/utils/constants/traffic';
import { mapState } from 'vuex';
export default {
  props: ['entities'],
  data: () => ({
    loading: false,
    select: null,
    search: null,
    searchItems: [],

    mapLayers: [],

    map_menu_items: [
      { title: Constants.DEVICE_TRAFFIC, icon: Constants.DEVICE_TRAFFIC_ICON, id: 0 },
      { title: Constants.DEVICE_BLUETOOTH, icon: Constants.DEVICE_BLUETOOTH_ICON, id: 1 },
      { title: Constants.DEVICE_WEATHER, icon: Constants.DEVICE_WEATHER_ICON, id: 2 },
      { title: Constants.DEVICE_RESTRICTIONS, icon: Constants.DEVICE_RESTRICTIONS_ICON, id: 3 },
      { title: Constants.DEVICE_SEGMENTS, icon: Constants.DEVICE_SEGMENTS_ICON, id: 4 }
    ],

    tool_menu_items: [{ title: 'Map Layer Selection', icon: 'mdi-layers-triple', id: 5 }],

    layer_items: [
      { title: Constants.DEVICE_TRAFFIC, id: Constants.LAYER_DEVICE_TRAFFIC },
      { title: Constants.DEVICE_BLUETOOTH, id: Constants.LAYER_DEVICE_BLUETOOTH },
      { title: Constants.DEVICE_RESTRICTIONS, id: Constants.LAYER_DEVICE_RESTRICTIONS },
      { title: Constants.DEVICE_SEGMENTS, id: Constants.LAYER_DEVICE_SEGMENTS },
      { title: Constants.DEVICE_WEATHER, id: Constants.LAYER_DEVICE_WEATHER },
      { title: 'Signals', id: 5 },
      { title: 'Devices', id: 6 },
      { title: 'Routes', id: 7 },
      { title: 'Waze Alerts', id: 8 }
    ],

    region_menu_items: [
      { title: 'All Region', value: -1 },
      { title: 'Urban Freeway', value: 1 },
      { title: 'CAV Area Freeway', value: 3 },
      { title: 'CAV Area Arterial', value: 4 }
    ]
  }),

  watch: {
    select() {
      this.$nextTick(() => {
        this.select = null;
      });
    },

    search(val) {
      if (val) {
        if (!this.isLoading) {
          this.isLoading = true;
          const key = val.toLowerCase();
          this.searchItems = this.entities.filter(item => item.desc.toLowerCase().includes(key));
          this.loading = false;
        }
      }
    }
  },

  computed: {
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

<style lang="scss" scoped></style>

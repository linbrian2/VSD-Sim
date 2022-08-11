<template>
  <v-toolbar light dense floating height="40" style="position: absolute; top: 10px; left:10px; ">
    <v-chip-group>
      <div v-for="{ color, name, from } in dataClasses" :key="from">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-chip filter small label :color="color" v-on="on" @click="classClicked(from)">
              {{ from }}
            </v-chip>
          </template>
          <span>{{ name }}</span>
        </v-tooltip>
      </div>
    </v-chip-group>

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
          <v-list-item-title :class="{ 'font-weight-bold': item.value === selectedRegionId }">
            <v-icon class="mr-1" v-if="item.value === selectedRegionId">mdi-check</v-icon>
            <span :class="{ 'ml-8': item.value !== selectedRegionId }"> {{ item.title }}</span>
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </v-toolbar>
</template>

<script>
import StatusConstants from '@/utils/constants/status';
import TrafficConstants from '@/utils/constants/traffic';
import { mapState } from 'vuex';
export default {
  props: ['entities'],
  data: () => ({
    loading: false,
    dataClasses: [],
    selectedErrorType: -1,
    region_menu_items: TrafficConstants.TRAFFIC_DEVICE_CATEGORIES,
    selectedRegionId: -1
  }),

  mounted() {
    this.mapLayers = this.$store.state.traffic.mapLayersSelection;
    this.dataClasses = this.createDataClasses();
  },

  methods: {
    createDataClasses() {
      const dataClasses = [];
      for (let i = 0; i < 8; i++) {
        dataClasses.push({
          from: i,
          to: i,
          color: StatusConstants.DISTINCT_COLORS[i],
          name: StatusConstants.QUALITY_ERROR_NAMES[i]
        });
      }
      return dataClasses;
    },

    menuItemClicked(type) {
      this.$bus.$emit('SHOW_SELECTION_POPUP', type);
    },

    layerItemClicked() {
      this.mapLayers.sort();
      this.$store.dispatch('traffic/saveMapLayers', this.mapLayers);
    },

    classClicked(classIdx) {
      if (classIdx == this.selectedErrorType) {
        this.selectedErrorType = -1;
      } else {
        this.selectedErrorType = classIdx;
      }

      setTimeout(() => {
        this.$emit('type-select', this.selectedErrorType);
      }, 100);
    },

    regionMenuItemClicked(value) {
      setTimeout(() => {
        this.selectedRegionId = value;
        this.$emit('region-select', value);
      }, 100);
    }
  }
};
</script>

<style lang="scss" scoped></style>

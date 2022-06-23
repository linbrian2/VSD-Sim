<template>
  <SelectionPanel :name="name">
    <v-combobox
      class="mx-2"
      dense
      hide-details
      single-line
      :items="items"
      :value="valueSelected"
      @input="valueSelectHandler"
      label="Select ..."
    />
    <MapSelect ref="mapSelect" :markers="markers" :icons="markerIcons" @click="onMapClick" />
  </SelectionPanel>
</template>

<script>
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapSelect from '@/components/modules/traffic/map/MapSelect';

export default {
  props: {
    markers: Array,
    items: Array,
    icons: Array,
    name: {
      type: String,
      default: ''
    },
    onMarkerClick: Function
  },

  components: {
    SelectionPanel,
    MapSelect
  },

  data: () => ({
    valueSelected: '',
    defaultIcons: [
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
    ]
  }),

  computed: {
    markerIcons() {
      return this.icons ? this.icons : this.defaultIcons;
    }
  },

  methods: {
    triggerFirstMarkerClick() {
      this.$refs.mapSelect.triggerFirstMarkerClick();
    },

    getSelectedMarker() {
      return this.$refs.mapSelect.getSelectedMarker();
    },

    valueSelectHandler(value) {
      this.$bus.$emit('NAME_SELECTED', value);
    },

    onMapClick(marker) {
      this.valueSelected = marker.name;
      this.onMarkerClick(marker);
    },

    selectDeviceById(deviceId) {
      this.$bus.$emit('ID_SELECTED', deviceId);
      if (!this.showPanel) {
        this.$store.commit('traffic/SHOW_PANEL', true);
      }
    },

    selectDevicesByIds(ids) {
      this.$refs.mapSelect.selectByIds(ids);
      if (!this.showPanel) {
        this.$store.commit('traffic/SHOW_PANEL', true);
      }
    }
  }
};
</script>

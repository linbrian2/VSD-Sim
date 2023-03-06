<template>
  <v-card class="mx-auto" elevation="0" style="position: absolute; top: 9px; left:10px; " color="rgb(0, 0, 0, 0.0)">
    <v-toolbar light dense floating height="40">
      <v-chip-group>
        <div v-for="{ color, name, from } in dataClasses" :key="from">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-chip
                :class="$vuetify.breakpoint.mobile ? 'cell-mobile' : null"
                filter
                small
                label
                v-bind="{ disabled: counts[from] === 0 }"
                :color="color"
                :input-value="selectedErrorType === from"
                v-on="on"
                @click="classClicked(from)"
              >
                {{ counts.length == 8 ? counts[from] : 0 }}
              </v-chip>
            </template>
            <span>{{ name }}</span>
          </v-tooltip>
        </div>
      </v-chip-group>

      <v-divider :class="$vuetify.breakpoint.mobile ? 'ml-3' : null" vertical />

      <DeviceSelectionMenu icon="mdi-map-outline" tooltip="Device Filter" v-model="deviceSelection" />

      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" @click.stop="showSystemQuality">
            <v-icon>mdi-check-decagram</v-icon>
          </v-btn>
        </template>
        <span>System Quality</span>
      </v-tooltip>

      <ExpandableSearch :entities="entities" @search="onSearchChange" />
    </v-toolbar>

    <v-chip-group>
      <v-chip
        small
        filter
        v-for="item in selections"
        :key="item.id"
        :color="item.color"
        class="black--text"
        close
        @click:close="chipClosed(item)"
      >
        {{ item.name }}
      </v-chip>

      <v-chip small :color="dataClasses[selectedErrorType].color" class="black--text" v-if="selectedErrorType >= 0">
        {{ currentTag }}
      </v-chip>
    </v-chip-group>
  </v-card>
</template>

<script>
import StatusConstants from '@/utils/constants/status';
import Constants from '@/utils/constants/traffic';
import ExpandableSearch from '@/components/common/ExpandableSearch';
import DeviceSelectionMenu from '@/components/modules/traffic/common/DeviceSelectionMenu';

export default {
  props: {
    entities: Array,
    counts: Array
  },

  components: {
    ExpandableSearch,
    DeviceSelectionMenu
  },

  data: () => ({
    loading: false,
    dataClasses: [],
    selectedErrorType: -1,
    region_menu_items: Constants.TRAFFIC_DEVICE_REGIONS,
    type_menu_items: Constants.TRAFFIC_DEVICE_TYPES,
    deviceSelection: {
      regionId: -1,
      typeId: -1
    },
    select: null,
    search: null,
    searchItems: []
  }),

  mounted() {
    this.mapLayers = this.$store.state.traffic.mapLayersSelection;
    this.dataClasses = this.createDataClasses();
  },

  computed: {
    currentTag() {
      if (this.selectedErrorType >= 0) {
        const name = this.dataClasses[this.selectedErrorType].name;
        const count = this.selectedErrorType < this.counts.length ? this.counts[this.selectedErrorType] : 0;
        return `${name} - ${count}`;
      } else {
        return '';
      }
    },

    selections() {
      const result = [];
      if (this.deviceSelection.regionId >= 0) {
        const item = this.region_menu_items.find(item => item.value == this.deviceSelection.regionId);
        if (item) {
          result.push({
            id: 100 + this.deviceSelection.regionId,
            type: 0,
            value: this.deviceSelection.regionId,
            name: item.title,
            color: 'orange'
          });
        }
      }

      if (this.deviceSelection.typeId >= 0) {
        const item = this.type_menu_items.find(item => item.value == this.deviceSelection.typeId);
        if (item) {
          result.push({
            id: 200 + this.deviceSelection.typeId,
            type: 1,
            value: this.deviceSelection.typeId,
            name: item.title,
            color: 'teal'
          });
        }
      }

      return result;
    }
  },

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
    },

    deviceSelection() {
      setTimeout(() => {
        this.$emit('filter', this.deviceSelection);
      }, 250);
    }
  },

  methods: {
    onSearchChange(item) {
      this.$emit('search', item);
    },

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

    classClicked(classIdx) {
      if (classIdx == this.selectedErrorType) {
        this.selectedErrorType = -1;
      } else {
        this.selectedErrorType = classIdx;
      }

      setTimeout(() => {
        this.$emit('type', this.selectedErrorType);
      }, 10);
    },

    showSystemQuality() {
      this.$emit('system');
    },

    chipClosed(item) {
      if (item.type == 0) {
        this.deviceSelection.regionId = -1;
        this.$emit('filter', { regionId: -1, typeId: this.deviceSelection.typeId });
      } else if (item.type == 1) {
        this.deviceSelection.typeId = -1;
        this.$emit('filter', { regionId: this.deviceSelection.regionId, typeId: -1 });
      } else {
        this.classClicked(-1);
      }
    },

    errorTypeChipClosed() {}
  }
};
</script>

<style lang="scss" scoped>
.cell-mobile {
  padding-left: 9px;
  padding-right: 9px;
  margin-left: 1px !important;
  margin-right: 1px !important;
}
</style>

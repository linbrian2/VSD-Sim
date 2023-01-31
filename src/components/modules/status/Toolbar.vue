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
                :color="color"
                v-on="on"
                @click="classClicked(from)"
              >
                {{ from }}
              </v-chip>
            </template>
            <span>{{ name }}</span>
          </v-tooltip>
        </div>
      </v-chip-group>

      <v-divider :class="$vuetify.breakpoint.mobile ? 'ml-3' : null" vertical />

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
        v-for="{ name, color, id } in selections"
        :key="id"
        :color="color"
        class="black--text"
        close
        @click:close="chipClosed(id)"
      >
        {{ name }}
      </v-chip>
    </v-chip-group>
  </v-card>
</template>

<script>
import StatusConstants from '@/utils/constants/status';
import TrafficConstants from '@/utils/constants/traffic';
import ExpandableSearch from '@/components/common/ExpandableSearch';

export default {
  props: {
    entities: Array
  },

  components: {
    ExpandableSearch
  },

  data: () => ({
    loading: false,
    dataClasses: [],
    selectedErrorType: -1,

    region_menu_items: TrafficConstants.TRAFFIC_DEVICE_CATEGORIES,
    selectedRegionId: -1,

    select: null,
    search: null,
    searchItems: []
  }),

  mounted() {
    this.mapLayers = this.$store.state.traffic.mapLayersSelection;
    this.dataClasses = this.createDataClasses();
  },

  computed: {
    selections() {
      const result = [];
      if (this.selectedRegionId >= 0) {
        const item = this.region_menu_items.find(item => item.value == this.selectedRegionId);
        if (item) {
          result.push({
            id: 10 + this.selectedRegionId,
            type: 0,
            name: item.title,
            color: 'white'
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
      }, 100);
    },

    showSystemQuality() {
      this.$emit('system');
    },

    regionMenuItemClicked(value) {
      this.selectedRegionId = value;
      setTimeout(() => {
        this.$emit('region', this.selectedRegionId);
      }, 100);
    },

    chipClosed(id) {
      if (id < 20) {
        this.regionMenuItemClicked(-1);
      } else {
        this.classClicked(-1);
      }
    }
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

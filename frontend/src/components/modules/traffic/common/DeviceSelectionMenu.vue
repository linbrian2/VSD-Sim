<template>
  <v-menu :close-on-content-click="false" bottom right offset-y min-width="250">
    <template v-slot:activator="{ on: menu, attrs }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on: tooltip }">
          <v-btn icon class="mx-1" v-bind="attrs" v-on="{ ...tooltip, ...menu }">
            <v-icon dark>{{ icon }}</v-icon>
          </v-btn>
        </template>
        <span>{{ tooltip }}</span>
      </v-tooltip>
    </template>

    <v-list>
      <v-subheader>DEVICE FILTER</v-subheader>

      <v-list-group v-for="item in items" :key="item.title" v-model="item.active" no-action>
        <template v-slot:activator>
          <v-list-item-icon>
            <v-icon>{{ item.action }}</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title v-text="getSelection(item.type)"></v-list-item-title>
          </v-list-item-content>
        </template>

        <v-list-item v-for="child in item.items" :key="child.title" @click="menuSelection(item.type, child.value)">
          <v-list-item-content>
            <v-list-item-title>
              <v-icon class="mr-1" v-if="isSelected(item.type, child.value)">mdi-check</v-icon>
              <span :class="{ 'ml-8': !isSelected(item.type, child.value) }"> {{ child.title }} </span>
            </v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list-group>

      <v-divider />
      <v-list-item @click="resetAll">
        <v-list-item-icon>
          <v-icon>mdi-check-all</v-icon>
        </v-list-item-icon>
        <v-list-item-title>Clear All</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
import Constants from '@/utils/constants/traffic';

export default {
  props: {
    icon: String,
    tooltip: String,
    value: Object
  },

  data: () => ({
    items: [
      {
        action: 'mdi-map-outline',
        title: 'Region Selection',
        active: false,
        type: 1,
        items: Constants.TRAFFIC_DEVICE_REGIONS
      },
      {
        action: 'mdi-radar',
        title: 'Sensor Type',
        active: false,
        type: 2,
        items: Constants.TRAFFIC_DEVICE_TYPES
      }
    ]
  }),

  methods: {
    getSelection(type) {
      const parentItem = this.items.find(item => item.type === type);
      if (!parentItem) {
        return '';
      }

      const value = type === 1 ? this.value.regionId : this.value.typeId;
      const item = parentItem.items.find(item => item.value === value);
      return item ? item.title : parentItem.title;
    },

    menuSelection(type, value) {
      // Close the parent submenu item
      const parentItem = this.items.find(item => item.type === type);
      if (parentItem) {
        parentItem.active = false;
      }

      if (type === 1) {
        this.value.regionId = value;
        this.$emit('input', { regionId: value, typeId: this.value.typeId });
      } else if (type === 2) {
        this.value.typeId = value;
        this.$emit('input', { regionId: this.value.regionId, typeId: value });
      }
    },

    isSelected(type, value) {
      if (type === 1) {
        return value === this.value.regionId;
      } else if (type === 2) {
        return value === this.value.typeId;
      } else {
        return false;
      }
    },

    resetAll() {
      this.value.regionId = -1;
      this.value.typeId = -1;
      this.$emit('input', { regionId: -1, typeId: -1 });
    }
  }
};
</script>

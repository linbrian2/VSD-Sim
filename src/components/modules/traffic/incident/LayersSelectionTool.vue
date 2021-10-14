<template>
  <v-toolbar dense floating height="40" style="position: absolute; top: 49px; left:10px; width: 55px ">
    <v-menu bottom right offset-y min-width="250" :close-on-content-click="false">
      <template v-slot:activator="{ on: menu, attrs }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on: tooltip }">
            <v-btn icon v-bind="attrs" v-on="{ ...tooltip, ...menu }">
              <v-icon>mdi-layers-outline</v-icon>
            </v-btn>
          </template>
          <span>Map Layers</span>
        </v-tooltip>
      </template>

      <v-list dense>
        <v-list-item-group>
          <v-list-item v-for="item in layers" :key="item.id" @click="layerItemClicked(item.id)">
            <v-list-item-content class="mt-0 pa-2">
              <v-checkbox hide-details :value="item.key" class="mt-0" v-model="mapLayers">
                <template v-slot:label>
                  {{ item.name }}
                  <v-badge class="ml-2 mt-3" color="green" :content="item.count" bordered> </v-badge>
                </template>
              </v-checkbox>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-menu>
  </v-toolbar>
</template>

<script>
export default {
  props: {
    layers: Array
  },

  data: () => ({
    mapLayers: []
  }),

  watch: {
    layers(layers) {
      this.setMapLayers(layers);
    }
  },

  mounted() {
    this.setMapLayers(this.layers);
  },

  methods: {
    setMapLayers(layers) {
      this.mapLayers = layers.map(item => item.key);
    },

    layerItemClicked() {
      this.$emit('layer-selected', this.mapLayers);
    }
  }
};
</script>

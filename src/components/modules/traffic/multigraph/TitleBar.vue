<template>
  <div class="title-name non-selection">
    <v-row wrap no-gutters>
      <v-col lg="6" sm="6" xs="12">
        <div class="d-flex mt-1">
          <span v-if="showMap">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-btn outlined class="mb-1 mr-2" color="teal" icon v-on="on" @click.stop="showPanel">
                  <v-icon color="teal accent-3">mdi-map</v-icon>
                </v-btn>
              </template>
              <span>Show/Hide Map</span>
            </v-tooltip>
          </span>

          <div class="mt-n2 mb-n2">
            <MenuSelector :items="multigraphModes" :selectedItem="multigraphModeSelect" @click="setMultiGraphMode" />
          </div>

          <div class="ml-3 mt-n2 mb-n2">
            <MenuSelector
              color="default"
              :items="selectItems"
              :selectedItem="singleSelect"
              @click="setSingleSelectMode"
            />
          </div>
        </div>
      </v-col>
      <v-col lg="5" sm="4" xs="12">
        <span><slot></slot></span>
      </v-col>
      <v-col lg="1" sm="2" xs="12">
        <v-btn
          small
          icon
          @click.stop="refreshData"
          class="title-btn float-right mt-2"
          :loading="loading"
          v-if="showRefresh"
        >
          <v-icon color="white">mdi-refresh</v-icon>
        </v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import MenuSelector from '@/components/common/MenuSelector';
export default {
  components: {
    MenuSelector
  },

  props: {
    title: String,
    loading: Boolean,
    refresh: Function,
    showMap: {
      type: Boolean,
      default: true
    },
    showRefresh: {
      type: Boolean,
      default: true
    },
    subtitle: {
      type: String,
      default: ''
    }
  },

  data: () => ({
    selectItems: ['Single Select', 'Multi Select']
  }),

  computed: {
    activeSignal() {
      return (
        this.activeMarker || {
          id: 6,
          name: 'No Signal'
        }
      );
    },

    ...mapState('traffic', ['activeMarker', 'multigraphModes', 'multigraphModeSelect', 'singleSelect'])
  },
  methods: {
    refreshData() {
      this.refresh();
    },

    showPanel() {
      this.$store.commit('traffic/TOGGLE_SHOW_PANEL');
    },

    setMultiGraphMode(mode) {
      this.$store.commit('traffic/SET_MULTIGRAPH_MODE_SELECT', mode);
    },

    setSingleSelectMode(mode) {
      this.$store.commit('traffic/SET_SINGLE_SELECT', mode);
    }
  }
};
</script>

<style lang="scss" scoped>
.title-name {
  background-color: hsl(200, 18%, 46%);
  padding: 3px 10px;
  color: white;
  font-size: normal;
  font-weight: bold;
}
.title-id {
  color: #888;
  font-size: small;
  font-weight: normal;
  margin-bottom: 30px;
}
.title-btn {
  margin-left: 5px;
  display: inline-block;
}

.non-selection {
  -moz-user-select: none;
  -khtml-user-select: none;
  -webkit-user-select: none;
  user-select: none;
}
</style>

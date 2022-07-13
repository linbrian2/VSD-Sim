<template>
  <div class="title-name non-selection" style="height: 48px">
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

          <v-overflow-btn
            dense
            outlined
            class="ml-n1 mt-n1 mb-n2"
            v-model="multigraphModeSelect"
            :items="multigraphModes"
            auto-select-first
          />

          <v-overflow-btn
            class="ml-2 mr-2 mt-n1 mb-n2"
            v-model="singleMode"
            :items="selectItems"
            dense
            auto-select-first
            single-line
            prepend-icon="mdi-gesture-tap-button "
          />
          <v-divider vertical class="ml-2 mt-n2 mb-4" />
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
export default {
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
    selectItems: [
      { text: 'Single Select', value: true },
      { text: 'Multi Select', value: false }
    ]
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

    multigraphModeSelect: {
      get() {
        return this.$store.state.traffic.multigraphModeSelect;
      },
      set(val) {
        this.$store.commit('traffic/SET_MULTIGRAPH_MODE_SELECT', val);
      }
    },

    singleMode: {
      get() {
        return this.$store.state.traffic.singleSelect;
      },
      set(val) {
        this.$store.commit('traffic/SET_SINGLE_SELECT', val);
      }
    },

    ...mapState('traffic', ['activeMarker', 'multigraphModes'])
  },
  methods: {
    refreshData() {
      this.refresh();
    },

    showPanel() {
      this.$store.commit('traffic/TOGGLE_SHOW_PANEL');
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

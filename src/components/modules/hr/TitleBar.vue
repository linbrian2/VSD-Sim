<template>
  <div class="title-name">
    <v-row wrap no-gutters>
      <v-col lg="4" sm="6" xs="6">
        <div class="d-flex mt-2">
          <span v-if="showMap">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-btn outlined class="mb-1 mr-2" color="teal" icon v-on="on" @click.stop="showPanel()">
                  <v-icon color="teal accent-4">mdi-map</v-icon>
                </v-btn>
              </template>
              <span>Show/Hide Map</span>
            </v-tooltip>
          </span>
          <div class="mt-1">
            <span>{{ loading ? 'Loading ...' : activeTitle }}</span>
            <span class="ml-2" v-if="title === undefined && !loading">
              <v-chip class="mt-n1 white--text" color="green" outlined x-small>
                <span class="white--text">{{ activeSignal.id }}</span>
              </v-chip>
            </span>
          </div>
        </div>
      </v-col>
      <v-col lg="7" sm="4" xs="12">
        <span><slot></slot></span>
      </v-col>
      <v-col lg="1" sm="2" xs="12" v-if="!$vuetify.breakpoint.mobile"">
        <v-btn
          small
          icon
          @click.stop="refreshData()"
          class="title-btn float-right"
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
    showRefresh: {
      type: Boolean,
      default: true
    },
    showMap: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    activeTitle() {
      if (this.title !== undefined) {
        return this.title;
      } else {
        return this.activeSignal.name;
      }
    },
    activeSignal() {
      return (
        this.currentSignal || {
          id: 6,
          name: 'No Signal',
          phases: [],
          detectors: [],
          plans: []
        }
      );
    },
    ...mapState('hr', ['currentSignal'])
  },
  methods: {
    refreshData() {
      this.refresh();
    },

    showPanel() {
      this.$store.commit('hr/TOGGLE_SHOW_PANEL');
    }
  }
};
</script>

<style lang="scss" scoped>
.title-name {
  background-color: hsl(200, 18%, 46%);
  padding: 5px 10px;
  margin-top: 0px;
  color: white;
  font-size: large;
  font-weight: bold;
}
.title-id {
  color: #888;
  font-size: small;
  font-weight: normal;
  margin-bottom: 30px;
}
.title-btn {
  margin: 7px 5px;
  display: inline-block;
}
</style>

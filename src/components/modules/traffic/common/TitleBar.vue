<template>
  <div class="title-name non-selection">
    <v-row wrap no-gutters>
      <v-col lg="4" sm="6" xs="12">
        <div class="d-flex mt-1">
          <span v-if="showMap">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-btn outlined class="mb-1 mr-2" color="teal" icon v-on="on" @click.stop="showPanel()">
                  <v-icon color="teal accent-3">mdi-map</v-icon>
                </v-btn>
              </template>
              <span>Show/Hide Map</span>
            </v-tooltip>
          </span>

          <div class="mt-2">
            <span>{{ loading ? 'Loading ...' : activeTitle }}</span>
            <span class="ml-2 mt-0" v-if="!loading">
              <v-chip color="green" outlined small v-if="showId || showUid">
                <span class="white--text">{{ label }}</span>
              </v-chip>
            </span>
          </div>
        </div>
      </v-col>
      <v-col lg="7" sm="4" xs="12">
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
    showId: {
      type: Boolean,
      default: false
    },
    showUid: {
      type: Boolean,
      default: false
    },
    showMap: {
      type: Boolean,
      default: true
    },
    showRefresh: {
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
        this.activeMarker || {
          id: 6,
          name: 'No Signal'
        }
      );
    },

    label() {
      const u1 = this.showId && this.activeSignal.id ? this.activeSignal.id + '' : '';
      const u2 = this.showUid && this.activeSignal.uid ? this.activeSignal.uid + '' : '';
      let result = u1;
      if (u1 && u2) {
        result = u1 + ' / ' + u2;
      } else {
        result = u1 + u2;
      }

      return result;
    },
    ...mapState('traffic', ['activeMarker'])
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
  padding: 5px 10px;
  margin-top: -8px;
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

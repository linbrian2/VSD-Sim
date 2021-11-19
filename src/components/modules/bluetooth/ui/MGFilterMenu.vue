<template>
  <div>
    <v-menu v-model="menu" :close-on-content-click="false" left :nudge-width="200" offset-x>
      <template v-slot:activator="{ on, attrs }">
        <div v-bind="attrs" v-on="on">
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs" v-on="on" class="mx-1">
                <v-icon>mdi-filter-plus</v-icon>
              </v-btn>
            </template>
            <span>Filter Add</span>
          </v-tooltip>
        </div>
      </template>

      <v-card>
        <v-list>
          <v-list-item>
            <h3>Severity Levels:</h3>
          </v-list-item>
          <v-list-item v-if="severity">
            <v-tooltip bottom v-for="s in severity" :key="s.id">
              <template v-slot:activator="{ on, attrs }">
                <div v-bind="attrs" v-on="on">
                  <v-badge top class="pl-5" overlap :color="s.color" :content="s.count" :value="true">
                    <v-checkbox dense :label="s.name" v-model="s.flag" value="value" class="pr-5"></v-checkbox>
                  </v-badge>
                </div>
              </template>
              <span>{{ s.desc }}<br />Occurrences: {{ s.count }}</span>
            </v-tooltip>
          </v-list-item>
        </v-list>

        <v-list-item>
          <v-autocomplete
            v-model="selectedRoutes"
            dense
            outlined
            :items="apiData.routes"
            :search-input.sync="search"
            color="white"
            hide-no-data
            hide-selected
            label="Filter By Routes"
            return-object
            small-chips
            multiple
            deletable-chips
            class="mt-0 mx-6"
          >
            <template v-slot:append-outer>
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn dense icon v-bind="attrs" v-on="on" @click="clearAll()" class="pb-3">
                    <v-icon>mdi-backspace-outline</v-icon>
                  </v-btn>
                </template>
                <span>Clear all</span>
              </v-tooltip>
            </template>
          </v-autocomplete>
        </v-list-item>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn text @click="clear()"> Cancel </v-btn>
          <v-btn color="primary" text @click="submit()"> Submit </v-btn>
        </v-card-actions>
      </v-card>
    </v-menu>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  data: () => ({
    menu: false,
    search: null,
    severity: [
      { id: 1, name: 'Light', flag: false, desc: 'Severity Level: 1 to 2', count: 0, color: '#32A83C' },
      { id: 2, name: 'Moderate', flag: false, desc: 'Severity Level: 3 to 4', count: 0, color: '#95A832' },
      { id: 3, name: 'Heavy', flag: false, desc: 'Severity Level: 5', count: 0, color: '#FAA700' },
      { id: 4, name: 'Severe', flag: false, desc: 'Severity Level: 6', count: 0, color: '#FF0000' }
    ],
    oldSeverity: null
  }),
  mounted() {
    this.init();
  },
  methods: {
    filterByRoute(segments, filterRoutes) {
      if (filterRoutes && filterRoutes.length > 0) {
        let arr = segments.filter(x => {
          for (let i = 0; i < filterRoutes.length; i++) {
            if (filterRoutes[i] == x.info.route) {
              return true;
            }
          }
          return false;
        });
        return arr;
      } else {
        return segments;
      }
    },
    init() {
      if (this.segments) {
        let filteredSegments = this.filterByRoute(this.segments, this.selectedRoutes);
        this.severity[0].count = 0;
        this.severity[1].count = 0;
        this.severity[2].count = 0;
        this.severity[3].count = 0;
        filteredSegments.forEach(s => {
          if (s.travelTime.level == 1 || s.travelTime.level == 2) this.severity[0].count++;
          else if (s.travelTime.level == 3 || s.travelTime.level == 4) this.severity[1].count++;
          else if (s.travelTime.level == 5) this.severity[2].count++;
          else if (s.travelTime.level == 6) this.severity[3].count++;
        });
      }
    },
    clear() {
      this.menu = false;
      this.severity = this.oldSeverity;
    },
    submit() {
      this.menu = false;
      let filters = {
        levels: this.selectedLevels,
        routes: this.selectedRoutes
      };
      this.$bus.$emit('SUBMIT_MULTIGRAPH_FILTERS', filters);
    },
    clearAll() {
      this.selectedRoutes = [];
    }
  },
  watch: {
    menu(data) {
      if (data) {
        this.oldSeverity = JSON.parse(JSON.stringify(this.severity));
      }
    },
    selectedRoutes() {
      this.init();
    },
    segments() {
      this.init();
    }
  },
  computed: {
    segments() {
      return this.$store.state.bluetooth.apiData.segments;
    },
    selectedLevels() {
      let levels = [];
      if (this.severity[0].flag) {
        levels.push(1);
        levels.push(2);
      }
      if (this.severity[1].flag) {
        levels.push(3);
        levels.push(4);
      }
      if (this.severity[2].flag) {
        levels.push(5);
      }
      if (this.severity[3].flag) {
        levels.push(6);
      }
      return levels;
    },
    selectedRoutes: {
      get() {
        return this.$store.state.bluetooth.selectedRoutes;
      },
      set(routes) {
        this.$store.commit('bluetooth/SET_SELECTED_ROUTES', routes);
      }
    },
    ...mapState('bluetooth', ['apiData'])
  }
};
</script>

<style lang="scss" scoped></style>

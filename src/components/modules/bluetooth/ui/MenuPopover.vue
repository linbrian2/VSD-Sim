<template>
  <div>
    <v-menu v-model="menu" :close-on-content-click="false" :nudge-width="200" offset-x>
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
          <v-list-item>
            <v-tooltip bottom v-for="s in severity" :key="s.id">
              <template v-slot:activator="{ on, attrs }">
                <div v-bind="attrs" v-on="on">
                  <v-checkbox dense :label="s.name" v-model="s.flag" value="value" class="pr-5"> </v-checkbox>
                </div>
              </template>
              <span>{{ s.desc }}<br />Occurrences: {{ s.count }}</span>
            </v-tooltip>
          </v-list-item>
        </v-list>

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
export default {
  data: () => ({
    menu: false,
    severity: [
      { id: 1, name: 'Light', flag: false, desc: 'Severity Level: 1 to 2', count: 0 },
      { id: 2, name: 'Moderate', flag: false, desc: 'Severity Level: 3 to 4', count: 0 },
      { id: 3, name: 'Heavy', flag: false, desc: 'Severity Level: 5', count: 0 },
      { id: 4, name: 'Severe', flag: false, desc: 'Severity Level: 6', count: 0 }
    ],
    oldSeverity: null
  }),
  mounted() {
    this.init();
  },
  methods: {
    init() {
      /* console.log("Segments: %o", this.segments) */
      this.segments.forEach(s => {
        if (s.travelTime.level == 1 || s.travelTime.level == 2) this.severity[0].count++;
        else if (s.travelTime.level == 3 || s.travelTime.level == 4) this.severity[1].count++;
        else if (s.travelTime.level == 5) this.severity[2].count++;
        else if (s.travelTime.level == 6) this.severity[3].count++;
      });
    },
    clear() {
      this.menu = false;
      this.severity = this.oldSeverity;
    },
    submit() {
      this.menu = false;
      let filters = {
        levels: this.selectedLevels
      };
      this.$bus.$emit('SUBMIT_MULTIGRAPH_FILTERS', filters);
    }
  },
  watch: {
    menu(data) {
      if (data) {
        this.oldSeverity = JSON.parse(JSON.stringify(this.severity));
      }
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
    }
  }
};
</script>

<style lang="scss" scoped></style>

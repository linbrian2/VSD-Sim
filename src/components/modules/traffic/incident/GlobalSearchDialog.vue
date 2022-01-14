<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="760px">
      <v-card tile color="#263238">
        <v-app-bar color="#607D8B" dense>
          <v-icon class="mr-2">mdi-magnify</v-icon>
          <v-toolbar-title>Incident Search</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-card-text class="mb-n2">
          <v-container fluid>
            <v-row>
              <v-col cols="6">
                <v-menu
                  v-model="menu1"
                  :close-on-content-click="false"
                  transition="scale-transition"
                  offset-y
                  min-width="auto"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-text-field
                      v-model="startDate"
                      label="Start Date"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                    ></v-text-field>
                  </template>
                  <v-date-picker
                    v-model="startDate"
                    @input="menu1 = false"
                    no-title
                    scrollable
                    color="green lighten-1"
                  ></v-date-picker>
                </v-menu>
              </v-col>
              <v-col cols="6">
                <v-menu
                  v-model="menu2"
                  :close-on-content-click="false"
                  transition="scale-transition"
                  offset-y
                  min-width="auto"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-text-field v-model="endDate" label="End Date" readonly v-bind="attrs" v-on="on"></v-text-field>
                  </template>
                  <v-date-picker
                    v-model="endDate"
                    @input="menu2 = false"
                    no-title
                    scrollable
                    color="green lighten-1"
                  ></v-date-picker>
                </v-menu>
              </v-col>
              <v-col cols="6">
                <v-select
                  label="Region"
                  style="font-size: 14px"
                  v-model="region"
                  :items="regionItems"
                  item-text="text"
                  item-value="value"
                />
              </v-col>
              <v-col cols="6">
                <v-select
                  label="Route"
                  style="font-size: 14px"
                  v-model="route"
                  :items="routeItems"
                  item-text="text"
                  item-value="value"
                />
              </v-col>
              <v-col cols="4">
                <v-select
                  label="Detour Needed"
                  style="font-size: 14px"
                  v-model="detourNeeded"
                  :items="detourItems"
                  item-text="text"
                  item-value="value"
                />
              </v-col>
              <v-col cols="4">
                <v-select
                  label="Lane Blockage"
                  style="font-size: 14px"
                  v-model="blockage"
                  :items="blockageItems"
                  item-text="text"
                  item-value="value"
                />
              </v-col>
              <v-col cols="4">
                <v-select
                  label="Mitigation Solution"
                  style="font-size: 14px"
                  v-model="mitigation"
                  :items="mitigationItems"
                  item-text="text"
                  item-value="value"
                />
              </v-col>
              <v-col cols="4">
                <v-subheader class="ml-n4 d-flex justify-space-between">
                  <div>Severity</div>
                  <v-chip color="green" outlined small>{{ severity[0] }} - {{ severity[1] }}</v-chip>
                </v-subheader>
                <v-range-slider
                  v-model="severity"
                  max="100"
                  min="0"
                  step="5"
                  thumb-color="red"
                  dark
                  hide-details
                  class="align-center"
                />
              </v-col>
              <v-col cols="4">
                <v-subheader class="ml-n4 d-flex justify-space-between">
                  <div>Duration</div>
                  <v-chip color="blue" outlined small>{{ duration[0] }} - {{ duration[1] }} minutes</v-chip>
                </v-subheader>
                <v-range-slider
                  v-model="duration"
                  max="1200"
                  min="0"
                  step="5"
                  thumb-color="red"
                  hide-details
                  class="align-center"
                />
              </v-col>
              <v-col cols="4">
                <v-subheader class="ml-n4">
                  <div>Maximum Count</div>
                </v-subheader>
                <v-select
                  class="mt-n7"
                  label=""
                  style="font-size: 14px"
                  v-model="count"
                  :items="countItems"
                  item-text="text"
                  item-value="value"
                />
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>

        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="cancelDialog">Cancel</v-btn>
          <v-btn color="blue darken-1" text @click="startSearching">OK</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Utils from '@/utils/Utils';

export default {
  props: {
    value: Boolean
  },
  data: () => ({
    height: 470,
    legendY: 15,
    menu1: false,
    menu2: false,
    startDate: '',
    endDate: '',
    route: '',
    routeItems: [
      { text: 'All', value: '' },
      { text: 'DE-1', value: 'DE1' },
      { text: 'DE-8', value: 'DE8' },
      { text: 'SR-1', value: 'SR-1' },
      { text: 'I-495', value: 'I-495' },
      { text: 'I-95', value: 'I-95' }
    ],
    region: -1,
    regionItems: [
      { text: 'All', value: -1 },
      { text: 'Urban', value: 1 },
      { text: 'CAV', value: 3 }
    ],
    count: 100,
    countItems: [
      { text: '50', value: 50 },
      { text: '100', value: 100 },
      { text: '200', value: 200 },
      { text: '500', value: 500 }
    ],
    blockage: -1,
    blockageItems: [
      { text: "Don't care", value: -1 },
      { text: 'Yes', value: 1 },
      { text: 'No', value: 0 }
    ],
    detourNeeded: -1,
    detourItems: [
      { text: "Don't care", value: -1 },
      { text: 'Yes', value: 1 },
      { text: 'No', value: 0 }
    ],
    mitigation: -1,
    mitigationItems: [
      { text: "Don't care", value: -1 },
      { text: 'Yes', value: 1 },
      { text: 'No', value: 0 }
    ],
    severity: [0, 100],
    duration: [0, 1200]
  }),
  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    }
  },

  methods: {
    init() {
      const now = new Date();
      this.startDate = Utils.formatDate(Utils.addDay(now, -30));
      this.endDate = Utils.formatDate(now);
      this.severity = [0, 100];
      this.duration = [0, 1200];
      this.route = '';
      this.region = -1;
      this.blockage = -1;
      this.detourNeeded = -1;
      this.mitigation = -1;
      this.count = 100;
    },

    hideDialog() {
      this.show = false;
    },

    cancelDialog() {
      this.show = false;
    },

    startSearching() {
      this.show = false;
      const startDate = Utils.dateFromString(this.startDate);
      const endDate = Utils.dateFromString(this.endDate);

      const data = {
        startDate: startDate.getTime(),
        endDate: endDate.getTime(),
        route: this.route,
        region: this.region,
        severity: this.severity,
        duration: this.duration,
        blockage: this.blockage,
        detourNeeded: this.detourNeeded,
        mitigation: this.mitigation,
        limit: this.count
      };
      this.$emit('handler', data);
    }
  }
};
</script>

<style lang="scss" scoped></style>

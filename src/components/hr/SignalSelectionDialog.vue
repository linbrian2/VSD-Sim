<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" persistent max-width="900px">
      <v-card>
        <v-app-bar :color="color" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-car</v-icon>
          <v-toolbar-title>Signal Selection</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="cancelDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container class="my-n4">
          <v-row>
            <v-col cols="5">
              <MapMultiSelect ref="mapRef" @click="onMapClick" />
            </v-col>
            <v-col cols="7">
              <v-data-table
                disable-sort
                hide-default-footer
                :headers="headers"
                :items="signals"
                item-key="signal"
                class="elevation-2"
              >
                <template v-slot:body="{ items, headers }">
                  <tbody v-if="items.length > 0">
                    <tr v-for="(item, idx) in items" :key="idx">
                      <td v-for="(header, key) in headers" :key="key">
                        <v-edit-dialog :return-value.sync="item[header.value]" large>
                          {{ item[header.value] }}
                          <template v-slot:input>
                            <div v-if="key == 0">
                              <v-select
                                style="max-width: 300px;"
                                dense
                                hide-details
                                single-line
                                :items="signalItems"
                                v-model="item[header.value]"
                              />
                            </div>
                            <div v-else>
                              <v-select
                                dense
                                hide-details
                                single-line
                                :items="signalPhases(item['signal'])"
                                v-model="item[header.value]"
                              />
                            </div>
                          </template>
                        </v-edit-dialog>
                      </td>
                    </tr>
                  </tbody>
                  <tbody v-else>
                    <tr>
                      <td :colspan="headers.length" style="text-align: center">
                        Please click markers on the map to select/unselect intersections.
                      </td>
                    </tr>
                  </tbody>
                </template>
              </v-data-table>
            </v-col>
          </v-row>
        </v-container>

        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="cancelDialog">Cancel</v-btn>
          <v-btn color="blue darken-1" text @click="confirmDialog">OK</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Utils from '@/utils/Utils.js';
import MapMultiSelect from '@/components/hr/MapMultiSelect';
export default {
  props: {
    value: Boolean,
    callback: Function
  },
  components: {
    MapMultiSelect
  },
  data: () => ({
    height: 460,
    headers: [
      { text: '#', value: 'id' },
      { text: 'Intersection', value: 'signal' }
      // { text: 'Left', value: 'left' },
      // { text: 'Right', value: 'right' }
    ],
    signals: []
  }),
  computed: {
    dialog: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },

    color() {
      return this.$store.state.darkMode ? '' : '#009688';
    },

    locations() {
      return this.$store.state.hr.locations;
    },
    signalItems() {
      let names = [];
      this.locations.forEach(location => {
        names.push(location.name.trimRight());
      });
      return names;
    },
    localStorageAvailable() {
      return localStorage.getItem('multiSignals') !== null;
    }
  },
  methods: {
    init() {
      this.signals = [];
      if (this.$refs.mapRef) {
        this.$refs.mapRef.clearSelection();
        this.$refs.mapRef.resize();
      }
    },

    onMapClick(data) {
      let location = data.marker;
      let action = data.action;

      if (action === 0) {
        if (this.signals.length < 8) {
          this.insertSignal(location);
        }
      } else {
        let index = this.signals.findIndex(s => s.signal === location.name);
        if (index >= 0) {
          this.signals.splice(index, 1);
        }
      }
    },

    insertSignal(location) {
      let leftP = location.phases.find(p => p == 2) || location.phases[0];
      let rightP = location.phases.find(p => p == 6) || location.phases[1];
      let item = { id: location.id, signal: location.name, left: leftP, right: rightP, lat: location.position.lat };

      if (this.signals.length == 0) {
        this.signals.push(item);
      } else {
        let arr = this.signals.map(s => s.lat);
        let index = Utils.getInsertionIndex(arr, item.lat, (a, b) => b - a);
        this.signals.splice(index, 0, item);
      }
    },

    signalPhases(name) {
      let location = this.locations.find(location => location.name === name);
      if (location === undefined) {
        return [];
      }
      return location.phases;
    },

    loadFromLocalStorage() {
      this.signals = JSON.parse(localStorage.getItem('multiSignals'));
    },

    saveToLocalStorage() {
      localStorage.setItem('multiSignals', JSON.stringify(this.signals));
    },

    addSignal() {
      if (this.locations.length > 1 && this.signals.length < 8) {
        let location = this.locations[0];
        let leftPhase = location.phases.find(p => p == 2) || location.phases[0];
        let rightPhase = location.phases.find(p => p == 6) || location.phases[1];
        this.signals.push({ signal: location.name, left: leftPhase, right: rightPhase });
      }
    },

    cancelDialog() {
      this.signals = [];
      this.dialog = false;
    },

    confirmDialog() {
      if (this.signals.length > 0) {
        // Compose data
        let signals = [];
        this.signals.forEach(s => {
          let location = this.locations.find(loc => loc.name === s.signal);
          if (location !== undefined) {
            let deviceId = parseInt(location.id);
            signals.push([deviceId, parseInt(s.left)]);
            // signals.push([deviceId, parseInt(s.right)]);
          }
        });

        setTimeout(() => {
          this.callback(signals);
        }, 200);

        // Close dialog
        this.dialog = false;
      }
    }
  }
};
</script>

<style lang="scss" scoped></style>

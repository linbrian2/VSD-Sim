<template>
  <v-dialog v-model="dialog" hide-overlay max-width="1200px" scrollable persistent transition="scale-transition">
    <v-card color="gray">
      <v-card-title>
        Simulations List
        <v-spacer />
        <v-text-field
          v-model="search"
          class="mx-10"
          :disabled="!simulationList"
          dense
          outlined
          append-icon="mdi-magnify"
          label="Search"
          single-line
          hide-details
        ></v-text-field>
        <v-spacer />
        <v-btn icon @click="dialog = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-data-table
          :loading="loading"
          :headers="headers"
          :items="simulationList"
          :items-per-page="15"
          :search="search"
          multi-sort
          class="elevation-1"
        >
          <template v-slot:[`item.emissionFileCount`]="{ item }">
            <v-chip
              small
              :color="getColor(item.checkpointCount ? item.emissionFileCount / item.checkpointCount : 0, 0)"
            >
              <h4
                :style="
                  `color: ${
                    (item.checkpointCount ? item.emissionFileCount / item.checkpointCount : 0) > 0.3 ? 'black' : 'white'
                  }`
                "
              >
                {{ item.emissionFileCount }}
              </h4>
            </v-chip>
          </template>
          <template v-slot:[`item.checkpointCount`]="{ item }">
            <v-chip small :color="getColor(item.checkpointCount > 10 ? 1 : item.checkpointCount / 10, 0)">
              <h4
                :style="
                  `color: ${(item.checkpointCount > 10 ? 1 : item.checkpointCount / 10) > 0.3 ? 'black' : 'white'}`
                "
              >
                {{ item.checkpointCount }}
              </h4>
            </v-chip>
          </template>
          <template v-slot:[`item.date`]="{ item }">
            {{ getDate(item.date) }}
          </template>
          <template v-slot:[`item.errorGenerated`]="{ item }">
            <v-icon color="green" v-if="!item.errorGenerated">mdi-check</v-icon>
            <v-icon color="red" v-else>mdi-close</v-icon>
          </template>
          <template v-slot:[`item.actions`]="{ item }">
            <v-tooltip bottom max-width="1000px">
              <template v-slot:activator="{ on }">
                <v-icon v-on="on" small class="mr-3" @click="viewItem(item)">
                  mdi-eye
                </v-icon>
              </template>
              <span>{{ JSON.stringify(item.params.env_config.flow_params, null, '\t') }}</span>
            </v-tooltip>
            <v-icon small class="mr-3" @click="open(item)">
              mdi-open-in-new
            </v-icon>
            <v-icon small @click="deleteItem(item)">
              mdi-delete
            </v-icon>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import Colors from '@/utils/Colors.js';
import Utils from '@/utils/Utils';
import Api from '@/utils/api/traffic';

export default {
  data() {
    return {
      updateTime: null,
      loading: false,
      simulationList: [],
      dialog: false,
      headers: [
        { text: 'Date', value: 'dateStr' },
        { text: 'Type', value: 'type' },
        { text: 'Checkpoint Folder Count', value: 'checkpointCount' },
        { text: 'Emmission File Count', value: 'emissionFileCount' },
        { text: 'Iteration Size', value: 'itSize' },
        { text: 'No Error Produced', value: 'errorGenerated' },
        { text: '', value: 'actions' }
      ],
      search: ''
    };
  },
  methods: {
    getColor(percent, mode) {
      if (mode == 0) {
        return Colors.getRedGreenColor(percent);
      } else {
        return Colors.getRedBlueColor(percent);
      }
    },
    async getSimulations() {
      this.loading = true;
      try {
        let resp = await Api.fetchSimulations();
        let data = resp.data.map(x => {
          let newVal = x;
          newVal.params.env_config.flow_params = JSON.parse(
            x.params.env_config.flow_params.replace('Infinity', '"Infinity"')
          );
          return newVal;
        });
        console.log(data);
        this.simulationList = data;
        this.loading = false;
        this.updateTime = new Date();
        // console.log('this.simulationList', this.simulationList);
      } catch (error) {
        this.loading = false;
        console.log(error);
      }
    },
    getDate(ts) {
      return Utils.formatDateTime(new Date(ts));
    },
    init() {
      this.dialog = true;
      if (!this.updateTime || (new Date().getTime() - this.updateTime.getTime()) / 1000 > 30) {
        this.getSimulations();
      }
    },
    closeDialog() {
      this.dialog = false;
    },
    viewItem(item) {
      console.log(item);
      //   console.log(JSON.parse(item.params.replace('Infinity', '"Infinity"')));
    },
    editItem(item) {
      console.log(item);
      //   this.page = 4;
      //   this.action = 'edit';
      //   this.item = item;
    },
    deleteItem(item) {
      alert(`Elevated permission required to delete simulation: ${item.name}`);
      //   this.showDialog = true;
      //   this.action = 'delete';
      //   this.item = item;
    },
    open(item) {
      console.log(item);
      this.$emit('openSimulation', item);
      this.dialog = false;
    }
  }
};
</script>

<style></style>

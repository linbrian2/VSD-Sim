<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="1200px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-grid</v-icon>
          <v-toolbar-title>{{ title }} </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container fluid style="width: 1200px; height:550px">
          <v-simple-table class="ma-3 mytable">
            <template v-slot:default>
              <thead>
                <tr>
                  <th>Time</th>
                  <th class="text-left" v-for="time in xcategories" :key="time">
                    {{ formatTime(time) }}
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, i) in matrix" :key="`R${i}`">
                  <th>{{ ycategories[i] }}</th>

                  <td v-for="(col, j) in row" :key="`R${i}C${j}`">
                    <div v-if="i < 8">
                      {{ col }}
                    </div>
                    <v-chip
                      v-else
                      :color="statusColor(col)"
                      text-color="black"
                      label
                      small
                      @click="showDecisionTree(j)"
                    >
                      <strong>{{ col }}</strong>
                    </v-chip>
                  </td>
                </tr>
              </tbody>
            </template>
          </v-simple-table>
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
export default {
  props: {
    value: Boolean
  },

  data: () => ({
    title: '',
    xcategories: [],
    ycategories: [],
    matrix: [],
    treeParams: {}
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
    init(deviceId, direction, start, hour) {
      this.fetchAnomalyHourlyResults(deviceId, direction, start, hour);
    },

    hideDialog() {
      this.show = false;
    },

    showDecisionTree(idx) {
      if (this.treeParams) {
        const params = this.treeParams[idx];
        this.$store.commit('traffic/SET_CURRENT_FLOW_CHART_PARAMS', params);
        this.$store.commit('traffic/SHOW_FLOW_CHART', true);
      }
    },

    formatTime(t) {
      return Utils.formatTimeAsMinute(new Date(t));
    },

    statusColor(status) {
      const colors = {
        '0': '#00FF00',
        '1': '#FF0000',
        '2': '#B71C1C',
        '3': '#E57373',
        '4': '#FF9800',
        '5': '#FFCC00',
        '6': '#FDD835'
      };
      if (status <= 0) {
        return colors['0'];
      } else {
        return colors[status + ''];
      }
    },

    async fetchAnomalyHourlyResults(deviceId, direction, start, hour) {
      this.loading = true;
      try {
        const response = await Api.fetchAnomalyHourlyResults(deviceId, direction, start, hour);
        this.loading = false;
        if (response.data.status === 'OK') {
          if (response.data.data) {
            const data = response.data.data;
            this.title = `${deviceId} - ${direction} ${hour}:00`;
            this.xcategories = data.xcategories;
            this.ycategories = data.ycategories;
            this.matrix = data.matrix;

            // create tree params
            this.treeParams = this.composeDecisionTreeChartParams(deviceId, direction, data.matrix, data.xcategories);
          }
        } else {
          this.$store.dispatch('traffic/setSystemStatus', { text: response.data.message, color: 'error' });
        }
      } catch (error) {
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;

      return {};
    },

    composeDecisionTreeChartParams(deviceId, direction, data, xcategories) {
      const result = {};
      for (let col = 0; col < 12; col++) {
        result[col] = {
          deviceId,
          direction,
          speed: data[3][col],
          volume: data[4][col],
          occupancy: data[5][col],
          mdist: data[7][col],
          status: data[8][col],
          time: xcategories[col]
        };
      }
      return result;
    }
  }
};
</script>

<style lang="scss" scoped>
.mytable table tr {
  background-color: lightgoldenrodyellow;
  border-bottom: none !important;
}
</style>

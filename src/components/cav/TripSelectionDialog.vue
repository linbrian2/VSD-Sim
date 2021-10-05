<template>
  <v-row>
    <v-dialog scrollable v-model="show" persistent max-width="800px">
      <v-card>
        <v-app-bar dark color="#009688" dense>
          <v-icon small class="mr-2">mdi-format-list-bulleted</v-icon>
          <v-toolbar-title>Trip List</v-toolbar-title>

          <v-spacer></v-spacer>

          <div class="mr-15">
            <DatePicker @date="dateSelected" />
          </div>

          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-card-text style="height: 450px;">
          <TripList :headers="headers" :items="items" @click="rowClicked" />
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Api from '@/utils/api/cav';
import DatePicker from '@/components/cav/DatePicker';
import TripList from '@/components/cav/TripList';
export default {
  props: {
    value: Boolean
  },

  components: {
    TripList,
    DatePicker
  },

  data: () => ({
    items: [],
    headers: [
      { text: 'TripId', value: 'id' },
      { text: 'Start Time', value: 'startTime' },
      { text: 'Duration', value: 'duration' },
      { text: 'Distance', value: 'distance' },
      { text: 'Device', value: 'deviceId' },
      { text: 'Status', value: 'status' }
    ]
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
      const time = new Date().getTime();
      this.fetchTripList(time);
    },

    dateSelected(date) {
      this.fetchTripList(date.getTime());
    },

    cleanUp() {},

    hideDialog() {
      this.cleanUp();
      this.show = false;
    },

    rowClicked() {
      this.hideDialog();
    },

    async fetchTripList(time) {
      try {
        const response = await Api.fetchTrips(time, 50);
        if (response.status === 200) {
          const trips = response.data;
          if (trips.length > 0) {
            this.items = trips;
          }
        }
      } catch (error) {
        console.log(error);
      }
    }
  }
};
</script>

<style lang="scss">
.highlighted {
  background-color: rgb(215, 215, 44);
}
</style>

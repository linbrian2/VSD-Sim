<template>
  <div>
    <v-container fluid>
      <TripList :headers="headers" :items="items" @click="rowClicked" />
    </v-container>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import TripList from '@/components/modules/cav/TripList';
export default {
  components: {
    TripList
  },

  data: () => ({
    events: [],
    headers: [
      { text: 'Device', value: 'deviceId' },
      { text: 'StartTime', value: 'startTime' },
      { text: 'Duration', value: 'duration' },
      { text: 'Distance', value: 'distance' },
      { text: 'Speed', value: 'speed' },
      { text: 'LastUpdated', value: 'lastUpdated' }
    ],
    items: []
  }),

  computed: {
    ...mapState('cav', ['liveCars'])
  },

  watch: {
    liveCars(value) {
      this.items = value;
    }
  },

  methods: {
    rowClicked(row) {
      this.$bus.$emit('SENSOR_SELECTED', row.id);
    }
  }
};
</script>

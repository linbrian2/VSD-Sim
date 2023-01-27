<template>
  <v-card tile color="teal">
    <v-card-title class="d-flex justify-space-between">
      <div>{{ signal.intersection }}</div>
      <div>
        <v-chip small outlined>
          <strong>{{ signal.permit }}</strong>
        </v-chip>
      </div>
    </v-card-title>

    <v-card-text class="mt-2">
      <v-row wrap no-gutters>
        <v-col class="mr-3"><PropertyCard :entry="getEntry('AoR', signal.AoR)" color=""/></v-col>
        <v-col class="mr-3"><PropertyCard :entry="getEntry('Approach Vol', signal.approachVolume)"/></v-col>
        <v-col class="mr-3"><PropertyCard :entry="getEntry('Simple Delay', signal.simpleDelay)"/></v-col>
      </v-row>
      <v-row class="mt-2 ml-2">
        <div class="overline">{{ formatDate(signal) }}</div>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '@/utils/Utils';
import PropertyCard from '@/components/modules/traffic/common/PropertyCard';
export default {
  props: {
    signal: Object
  },

  components: {
    PropertyCard
  },

  computed: {},

  methods: {
    getEntry(name, values) {
      return {
        name,
        items: [
          { name: 'NB', value: values[0] },
          { name: 'SB', value: values[1] }
        ]
      };
    },
    formatDate(signal) {
      const d1 = new Date(signal.startTime);
      const d2 = new Date(signal.endTime);

      const mm1 = Utils.formatDateWOYear(d1);
      const tt1 = Utils.formatTimeAsMinute(d1);
      const mm2 = Utils.formatDateWOYear(d2);
      const tt2 = Utils.formatTimeAsMinute(d2);

      let endTime = mm1 === mm2 ? tt2 : `${mm2} ${tt2}`;

      return `${mm1} ${tt1} - ${endTime}`;
    }
  }
};
</script>

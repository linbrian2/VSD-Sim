<template>
  <div class="d-flex justify-space-between">
    <div class="d-flex align-center">
      <div class="white--text ml-4">
        <strong>{{ segmentTitle }}</strong>
      </div>

      <!-- Segment selection menu -->
      <v-menu v-if="incident.segmentCount > 1" bottom right offset-y min-width="250" :close-on-content-click="true">
        <template v-slot:activator="{ on, attrs }">
          <v-btn icon class="mx-1" v-bind="attrs" v-on="on">
            <v-icon dark color="white">mdi-dots-vertical</v-icon>
          </v-btn>
        </template>

        <v-list>
          <v-list-item v-for="item in segmentItems" :key="item.value" @click="menuItemClicked(item.id)">
            <v-list-item-title :class="{ 'font-weight-bold': item.id === selectedSegmentId }">
              <v-icon class="mr-1" v-if="item.id === selectedSegmentId">mdi-check</v-icon>
              <span :class="{ 'ml-8': item.id !== selectedSegmentId }"> {{ item.value }} </span>
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </div>

    <!-- <div class="d-flex align-center">
      <v-icon dark medium class="mr-1">mdi-clock-time-three-outline </v-icon>
      <div class="body-1 white--text mr-8">{{ incident.startTime | time }} - {{ incident.endTime | time }}</div>
    </div> -->

    <div class="d-flex align-center">
      <v-chip small outlined class="mx-4">
        {{ incident.direction }}
      </v-chip>
    </div>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    incident: Object,
    selectedSegmentId: null
  },

  computed: {
    segmentTitle() {
      if (this.incident.segmentCount === 1) {
        return this.incident.shortName;
      }

      const selected = this.segmentItems.find(item => item.id === this.selectedSegmentId);
      return selected ? selected.value : this.incident.shortName;
    },

    segmentItems() {
      const items = [{ value: 'ALL SEGMENTS', id: null }];
      const n = this.incident.segmentCount;
      for (let i = 0; i < n; i++) {
        items.push({
          value: this.incident.shortNames[i],
          id: this.incident.segments[i]
        });
      }
      return items;
    }
  },

  filters: {
    time(time) {
      const d = new Date(time);
      const tt = Utils.formatTimeAsMinute(d);
      return tt;
    }
  },

  methods: {
    menuItemClicked(segmentId) {
      this.$emit('select', segmentId);
    }
  }
};
</script>

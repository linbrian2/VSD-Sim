<template>
  <div class="traffic-incidents">
    <IncidentTable :height="200" :incidents="incidents" @click="handleRowClick" />
    <Timeline />
    <v-card class="mt-2">
      <v-col>
        <v-row justify="center">
          <v-img alt="Logo" class="shrink" src="@/assets/collision-example.png" width="458" />
        </v-row>
      </v-col>
    </v-card>
  </div>
</template>

<script>
import IncidentTable from '@/components/modules/dashboard/IncidentTable.vue';
import Timeline from '@/components/modules/dashboard/Timeline.vue';
import { mapState } from 'vuex';

export default {
  props: {
    handleRowClick: Function
  },
  components: {
    IncidentTable,
    Timeline
  },
  data() {
    return {
      incidents: []
    };
  },
  computed: {
    ...mapState('dashboard', ['trafficIncidents'])
  },
  mounted() {
    if (this.trafficIncidents) {
      this.prepareTrafficIncidents(this.trafficIncidents);
    }
  },
  methods: {
    prepareTrafficIncidents(data) {
      this.incidents = data;
    }
  },
  watch: {
    trafficIncidents() {
      this.prepareTrafficIncidents(this.trafficIncidents);
    }
  }
};
</script>

<style></style>

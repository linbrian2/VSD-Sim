<template>
  <v-row>
    <v-col>
      <div class="ml-2">
        <v-tabs color="deep-purple accent-4" v-model="activeTab" show-arrows>
          <v-tab v-for="(phase, i) in phases" :key="i">
            <v-chip color="teal" small>P{{ phase }}</v-chip>
          </v-tab>
          <v-tab-item v-for="(plan, i) in plans" :key="i">
            <v-container fluid>
              <v-data-table disable-sort mobile-breakpoint="0" :headers="headers" :items="plan" hide-default-footer>
                <template v-slot:[`item.name`]="{ item }">
                  <v-chip color="success" outlined x-small style="width:35px;">{{ item.name | simplify }}</v-chip>
                </template>
                <template v-slot:[`item.start`]="{ item }">
                  {{ item.start | time }}
                </template>
                <template v-slot:[`item.end`]="{ item }">
                  {{ item.end | time }}
                </template>
                <template v-slot:[`item.AoG`]="{ item }"> {{ item.AoG }}% </template>
                <template v-slot:[`item.GT`]="{ item }"> {{ item.GT }}% </template>
                <template v-slot:[`item.PR`]="{ item }">
                  {{ item.PR.toFixed(2) }}
                </template>
              </v-data-table>
            </v-container>
          </v-tab-item>
        </v-tabs>
      </div>
    </v-col>
    <!-- <v-col cols="3" v-if="!$vuetify.breakpoint.mobile">
      <div class="phase-image">
        <v-img src="@/assets/phase.png" aspect-ratio="1.0" />
      </div>
      <h5 class="text-center font-italic font-weight-thin">General Nema Diagram</h5>
    </v-col> -->
  </v-row>
</template>

<script>
export default {
  props: ['phases', 'plans'],
  data: () => ({
    headers: [
      { text: 'Plan#', value: 'name' },
      { text: 'Start', value: 'start' },
      { text: 'End', value: 'end' },
      { text: 'AoG', value: 'AoG' },
      { text: 'GT', value: 'GT' },
      { text: 'PR', value: 'PR' }
    ],
    activeTab: null
  }),
  filters: {
    simplify: function(name) {
      return name.split(' ')[1];
    },
    time: function(timestamp) {
      const date = new Date(timestamp);
      const time = ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2);
      return time;
    }
  }
};
</script>
<style lang="scss" scoped>
table.v-table tbody td {
  font-size: 8px !important;
}

.v-tab {
  padding: 0px !important;
}

.phase-image {
  display: flex;
  justify-content: center;
  margin: 40px 20px 20px 20px;
}
</style>

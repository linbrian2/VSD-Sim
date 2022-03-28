<template>
  <div class="congested-routes">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      hide-default-footer
      :items-per-page="itemsPerPage"
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
    >
    </v-data-table>
    <v-card class="mt-2">
      <v-col>
        <v-row justify="center">
          <v-img alt="Logo" class="shrink" src="@/assets/tt-graph-sample.png" contain />
        </v-row>
      </v-col>
    </v-card>
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
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';

export default {
  props: {
    height: { type: Number, default: 190 },
    itemsPerPage: {type: Number, default: 3},
    data: Object
  },
  data: () => ({
    reload: false,
    items: [],
    headers: []
  }),
  computed: {
    selectedSegment: {
      get() {
        return this.$store.state.dashboard.selectedSegment;
      },
      set(val) {
        this.$store.state.dashboard.selectedSegment = val;
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },
    ...mapState('dashboard', ['segments'])
  },
  mounted() {
    if (this.segments) {
      this.prepareHighCongestionRoutes(this.segments);
    }
    if (!this.selectedSegment) {
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    prepareHighCongestionRoutes(data) {
      console.log(data);
      this.headers = [
        { text: 'Last Updated', value: 'lastUpdated' },
        { text: 'Description', value: 'desc' },
        { text: 'Level', value: 'level' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        lastUpdated: d.travelTime.calculationTimestamp.text,
        desc: d.desc,
        level: d.travelTime.level
      }));
    },

    itemRowBackground(item) {
      return item.id == this.selectedSegment.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedSegment = item;
      this.$emit('click', item);
    },

    fetchInfo(device) {
      console.log('Fetch Device Info: %o', device);
    }
  },
  watch: {
    selectedSegment(data) {
      this.fetchInfo(data);
    },
    segments() {
      if (this.segments) {
        this.prepareHighCongestionRoutes(this.segments);
      }
      if (!this.selectedSegment) {
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

<style></style>

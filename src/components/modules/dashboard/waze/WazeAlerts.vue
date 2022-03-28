<template>
  <div class="waze">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemsPerPage"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
    >
    </v-data-table>
    <!-- Data Display -->
    <!-- {{ selectedWazeAlert }} -->
    <v-row class="mt-3 ml-1 mr-7" v-if="currWaze">
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-information-outline'" :name="'Time'" :value="currWazeTime" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-information-outline'" :name="'Alert Type'" :value="currWaze.alertType.name" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-information-outline'" :name="'Address'" :value="currWaze.address.street" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-information-outline'" :name="'Score'" :value="currWaze.confidence" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-information-outline'" :name="'Reliability'" :value="currWaze.reliability" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-information-outline'" :name="'Thumbs Up'" :value="currWaze.thumbsUp" />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import InfoCard from '@/components/modules/dashboard/InfoCard';
import Utils from '@/utils/Utils';
import { mapState } from 'vuex';

export default {
  props: {
    height: { type: Number, default: 190 },
    itemsPerPage: { type: Number, default: 3 },
    data: Object
  },
  components: { InfoCard },
  data: () => ({
    reload: false,
    items: [],
    headers: []
  }),
  computed: {
    currWazeTime() {
      if (this.currWaze) {
        return Utils.formatTimeAsMinute(new Date(this.currWaze.alertTimeTS))
      } else {
        return '-'
      }
    },
    currWaze() {
      if (this.waze) {
        return this.waze[0]
      } else {
        return null
      }
    },
    selectedWazeAlert: {
      get() {
        return this.$store.state.dashboard.selectedWazeAlert;
      },
      set(val) {
        this.$store.state.dashboard.selectedWazeAlert = val;
        this.reload = true;
        setTimeout(() => {
          this.reload = false;
        }, 1);
      }
    },
    ...mapState('dashboard', ['waze'])
  },
  mounted() {
    if (this.waze) {
      this.prepareReportedWazeAlerts(this.waze);
    }
    if (!this.selectedWazeAlert) {
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    prepareReportedWazeAlerts(data) {
      this.headers = [
        { text: 'Time', value: 'time' },
        { text: 'Description', value: 'desc' },
        { text: 'Street', value: 'street' },
        { text: 'Road Type', value: 'roadType' },
        { text: 'Score', value: 'score' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        time: d.alertTime,
        desc: d.description,
        street: d.address.street,
        roadType: d.roadType.name,
        score: d.confidence
      }));
    },

    itemRowBackground(item) {
      return item.id == this.selectedWazeAlert.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedWazeAlert = item;
      this.$emit('click', item);
    },

    fetchInfo(device) {
      console.log('Fetch Device Info: %o', device);
    }
  },
  watch: {
    selectedWazeAlert(data) {
      this.fetchInfo(data);
    },
    waze() {
      if (this.waze) {
        this.prepareReportedWazeAlerts(this.waze);
      }
      if (!this.selectedWazeAlert) {
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

<style lang="scss"></style>

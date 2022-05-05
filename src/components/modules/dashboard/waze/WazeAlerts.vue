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
      <template v-slot:[`item.score`]="{ item }">
        <v-chip small :color="getStrokeColor(item.score)">
          <strong class="black--text">{{ item.score }}</strong>
        </v-chip>
      </template>
      <template v-slot:[`item.time`]="{ item }">
        {{ item.time.split(' ')[1].slice(0, 5) }}
      </template>
      <template v-slot:[`footer`]>
        <v-btn :disabled="maxItems == 1" block @click="expandTable">
          <v-icon>{{ itemsPerPage == 1 ? 'mdi-arrow-expand-down' : 'mdi-arrow-expand-up' }}</v-icon>
        </v-btn>
      </template>
    </v-data-table>
    <v-row class="mt-3 ml-1 mr-7" v-if="currWaze">
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :name="'Time'"
          :valueFontSize="28"
          :value="getTimeStr(currWaze.alertTimeTS)"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard
          :icon="'mdi-account-multiple-check'"
          :valueColor="getStrokeColor(currWaze.confidence)"
          :name="'Score'"
          :value="currWaze.confidence"
        />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-alert-circle-outline'" :name="'Alert Type'" :value="currWaze.alertType.name" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-map-marker-outline'" :name="'Address'" :value="currWaze.roadType.name" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-shield-check-outline'" :name="'Reliability'" :value="currWaze.reliability" />
      </v-col>
      <v-col cols="6" class="pa-1">
        <InfoCard :icon="'mdi-thumb-up-outline'" :name="'Thumbs Up'" :value="currWaze.thumbsUp" />
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
    data: Object,
    maxItems: Number
  },
  components: { InfoCard },
  data: () => ({
    height: null,
    itemsPerPage: 1,
    reload: false,
    items: [],
    headers: []
  }),
  computed: {
    currWaze() {
      if (this.selectedWazeAlert && this.waze) {
        let result = this.waze.filter(x => x.id == this.selectedWazeAlert.id);
        return result.length > 0 ? result[0] : null;
      } else {
        return null;
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
      this.handleRowClick(this.items[0]);
    }
  },
  methods: {
    getTimeStr(ts) {
      let time = new Date(ts);
      return `${Utils.formatTimeAsMinute(time)} (${Utils.fromNow(time)})`;
    },
    expandTable() {
      if (this.itemsPerPage == 1) {
        this.prepareReportedWazeAlerts(this.waze);
        if (this.maxItems > 12) {
          this.height = 'calc(95vh - 48px)';
        }
        this.itemsPerPage = this.maxItems;
      } else {
        this.prepareReportedWazeAlerts([this.selectedWazeAlert]);
        this.height = null;
        this.itemsPerPage = 1;
      }
    },
    getStrokeColor(level) {
      if (level == 5) return Utils.getStrokeColor(3);
      else if (level == 6 || level == 7) return Utils.getStrokeColor(4);
      else if (level == 10 || level == 9) return Utils.getStrokeColor(5);
      else return Utils.getStrokeColor(6);
    },
    prepareReportedWazeAlerts(data) {
      this.headers = [
        { text: 'Description', value: 'desc' },
        { text: 'Time', value: 'time' },
        { text: 'Street', value: 'street' },
        { text: 'Score', value: 'score' }
      ];
      this.items = data.map(d => ({
        id: d.id,
        time: d.alertTime || d.time,
        desc: d.description || d.desc,
        street: d.address ? d.address.street : d.street,
        // roadType: d.roadType.name,
        score: d.confidence || d.score
      }));
    },

    itemRowBackground(item) {
      return item.id == this.selectedWazeAlert.id ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedWazeAlert = item;
      this.$emit('click', item);
    }
  },
  watch: {
    waze() {
      if (this.waze) {
        this.prepareReportedWazeAlerts(this.waze);
        this.handleRowClick(this.items[0]);
      }
    }
  }
};
</script>

<style lang="scss"></style>

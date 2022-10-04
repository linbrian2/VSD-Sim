<template>
  <div class="waze">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="showTable && maxItems > listLimit ? maxItems : listLimit"
      disable-sort
      :hide-default-header="!showTable"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1 mx-2"
    >
      <template v-slot:[`item.score`]="{ item }">
        <v-chip small :color="getStrokeColor(item.score)">
          <strong class="black--text">{{ item.score }}</strong>
        </v-chip>
      </template>
      <template v-slot:[`item.time`]="{ item }">
        {{ item.time.split(' ')[1].slice(0, 5) }}
      </template>
    </v-data-table>
    <v-row class="mt-3 mx-1" v-if="currWaze">
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-note'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Name'"
          :valueFontSize="singleCol ? undefined : 28"
          :value="currWaze.description"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-clock-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Time'"
          :valueFontSize="singleCol ? undefined : 28"
          :value="getTimeStr(currWaze.alertTimeTS)"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-account-multiple-check'"
          :valueColor="getStrokeColor(currWaze.confidence)"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Score'"
          :value="currWaze.confidence"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-alert-circle-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Alert Type'"
          :value="currWaze.alertType.name"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-map-marker-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Address'"
          :value="currWaze.roadType.name"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-check'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Reliability'"
          :value="currWaze.reliability"
        />
      </v-col>
      <v-col :cols="12 / infoColumnCount" class="pa-1">
        <InfoCard
          :icon="'mdi-thumb-up-outline'"
          :colDisplay="singleCol"
          :flex="singleCol"
          :height="cardHeight"
          :name="'Thumbs Up'"
          :value="currWaze.thumbsUp"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import InfoCard from '@/components/modules/dashboard/InfoCard';
import Utils from '@/utils/Utils';
import { mapGetters, mapState } from 'vuex';

export default {
  props: {
    data: Object,
    maxItems: Number,
    infoColumnCount: Number
  },
  components: { InfoCard },
  data: () => ({
    reload: false,
    items: [],
    headers: []
  }),
  computed: {
    height() {
      if (this.showTable && this.maxItems > 12) {
        return 'calc(80vh - 48px)';
      } else {
        return null;
      }
    },
    singleCol() {
      return this.infoColumnCount == 1;
    },
    cardHeight() {
      return this.singleCol ? '11vh' : undefined;
    },
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
    showTable: {
      get() {
        return this.$store.state.dashboard.showTable;
      },
      set(show) {
        this.$store.commit('dashboard/SHOW_TABLE', show);
      }
    },
    listLimit() {
      if (this.getSetting) {
        return this.getSetting('dashboard', 'limitListings');
      } else {
        return 0;
      }
    },
    ...mapGetters(['getSetting']),
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
      return `${Utils.formatTimeAsMinute(time)}`;
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
        { text: 'Score', value: 'score' },
        { text: '', value: 'actions' }
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

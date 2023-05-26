<template>
  <v-card tile v-if="currentInfo">
    <div v-if="infoList.length > 1">
      <v-card-title class="d-flex justify-space-between">
        <div v-if="name">
          <v-chip label outlined class="font-weight-bold overline">{{ name }} </v-chip>
        </div>
        <div class="d-flex">
          <v-select
            style="width:210px;"
            dense
            item-text="text"
            item-value="value"
            v-model="currentIdx"
            :items="timeItems"
            @click.native.stop=""
            hide-details
            prepend-icon="mdi-clock-outline"
            single-line
          />
          <div class="ml-3">
            <v-btn icon @click.native.stop="prevClicked"><v-icon>mdi-menu-left</v-icon></v-btn>
            <v-chip outlined> {{ currentIdx + 1 }} / {{ infoList.length }} </v-chip>
            <v-btn icon @click.native.stop="nextClicked"><v-icon>mdi-menu-right</v-icon></v-btn>
          </div>
        </div>
      </v-card-title>
    </div>

    <div v-else>
      <v-card-title class="d-flex justify-space-between">
        <div v-if="name">
          <v-chip label outlined class="font-weight-bold overline">{{ name }} </v-chip>
        </div>
        <div>
          <v-icon class="mt-n1 mr-2">mdi-clock-outline</v-icon>
          <span class="text-sm-body-1">{{ currentInfo.time | dateAndTime }}</span>
        </div>
      </v-card-title>
    </div>

    <v-card-text>
      <v-row class="mt-n4">
        <v-col cols="6" lg="6" md="6" sm="12" xs="12" v-for="(item, j) in currentInfo.data" :key="j">
          <ListInfoCard class="ma-1" :info="item" />
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '@/utils/Utils';
import ListInfoCard from '@/components/modules/traffic/common/ListInfoCard';
export default {
  props: {
    infoList: Array
  },

  components: {
    ListInfoCard
  },

  data: () => ({
    currentIdx: 0
  }),

  watch: {
    infoList() {
      this.currentIdx = 0;
    }
  },

  computed: {
    currentInfo() {
      return this.infoList && this.infoList.length > 0 ? this.infoList[this.currentIdx] : null;
    },

    timeItems() {
      return this.infoList.map((item, index) => ({ text: this.formatDateTime(item.time), value: index }));
    },

    name() {
      return this.currentInfo ? this.currentInfo.device : '';
    }
  },

  filters: {
    dateAndTime(t) {
      return Utils.formatDateTime(new Date(t));
    }
  },

  methods: {
    nextClicked() {
      if (this.currentIdx < this.infoList.length - 1) {
        this.currentIdx++;
      }
    },

    prevClicked() {
      if (this.currentIdx > 0) {
        this.currentIdx--;
      }
    },

    formatDateTime(t) {
      const d = new Date(t);
      const dd = Utils.formatDate(d);
      const tt = Utils.formatTimeAsMinute(d);
      return `${dd} ${tt}`;
    }
  }
};
</script>

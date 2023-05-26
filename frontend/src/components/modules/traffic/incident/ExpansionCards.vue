<template>
  <v-expansion-panels v-if="currentInfo">
    <v-expansion-panel>
      <v-expansion-panel-header>
        <template v-slot:default="{ open }">
          <div v-if="open">
            <div v-if="infoList.length > 1">
              <div class="d-flex justify-space-between mr-15">
                <div style="width:230px;">
                  <v-select
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
                </div>

                <v-chip outlined> {{ currentIdx + 1 }} / {{ infoList.length }} </v-chip>

                <div>
                  <v-btn icon @click.native.stop="prevClicked"><v-icon>mdi-chevron-left</v-icon></v-btn>
                  <v-btn icon @click.native.stop="nextClicked"><v-icon>mdi-chevron-right</v-icon></v-btn>
                </div>
              </div>
            </div>
            <div v-else>
              <v-icon class="mt-n1 mr-2">mdi-clock-outline</v-icon>
              <span class="text-sm-body-1">{{ currentInfo.time | dateAndTime }}</span>
            </div>
          </div>
          <div v-else>
            <div class="font-weight-bold overline">{{ title }}</div>
          </div>
        </template>
      </v-expansion-panel-header>
      <v-expansion-panel-content>
        <v-container fluid class="mt-n3">
          <v-row>
            <v-col cols="3" lg="3" md="3" sm="6" xs="12" v-for="(item, j) in currentInfo.data" :key="j">
              <InfoCard class="ma-1" :info="item" />
            </v-col>
          </v-row>
        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script>
import Utils from '@/utils/Utils';
import InfoCard from '@/components/modules/traffic/common/InfoCard';
export default {
  props: {
    infoList: Array,
    name: String
  },

  components: {
    InfoCard
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

    title() {
      return `${this.infoList.length} ${this.name} Evidence${this.infoList.length > 1 ? 's' : ''}`;
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
      return Utils.formatDateTime(d);
    }
  }
};
</script>

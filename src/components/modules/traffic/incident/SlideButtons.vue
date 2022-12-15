<template>
  <v-card tile>
    <v-row no-gutters>
      <v-col md="9">
        <v-slide-group center-active show-arrows v-model="model">
          <v-slide-item
            class="my-2"
            v-for="item in items"
            :key="item.name"
            :value="item.name"
            v-slot="{ active, toggle }"
          >
            <v-btn
              class="mx-3"
              :input-value="active"
              active-class="purple white--text"
              depressed
              rounded
              @click="toggle"
              v-on:click="itemClicked(item)"
              ref="btns"
            >
              {{ item.name }}
            </v-btn>
          </v-slide-item>
        </v-slide-group>
      </v-col>
      <v-col md="3">
        <div class="d-flex justify-end mt-3 mr-2">
          <slot></slot>
          <v-btn text color="primary" @click.stop="btnClicked" v-if="showEvidenceText">
            <div class="font-weight-bold overline ml-3 mr-1">{{ title }}</div>
          </v-btn>

          <div v-if="!$vuetify.breakpoint.mobile">
            <div v-if="showEvidenceText">
              <v-btn icon @click.stop="btnClicked" v-if="allowExpand">
                <v-icon color="orange darken-2" v-if="show">mdi-chevron-up</v-icon>
                <v-icon color="orange darken-2" v-else>mdi-chevron-down</v-icon>
              </v-btn>
            </div>
            <div v-else>
              <v-tooltip left>
                <template v-slot:activator="{ on }">
                  <v-btn icon v-on="on" @click.stop="btnClicked" v-if="allowExpand">
                    <v-icon color="orange darken-2" v-if="show">mdi-chevron-up</v-icon>
                    <v-icon color="orange darken-2" v-else>mdi-chevron-down</v-icon>
                  </v-btn>
                </template>
                <span>{{ title }}</span>
              </v-tooltip>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<script>
export default {
  props: {
    title: String,
    items: Array,
    show: Boolean,
    callback: Function,
    showEvidenceText: { type: Boolean, default: true },
    allowExpand: {
      type: Boolean,
      default: true
    }
  },

  data: () => ({
    model: ''
  }),

  watch: {
    items() {
      this.selectFirst();
    }
  },

  mounted() {
    this.selectFirst();
  },

  methods: {
    selectFirst() {
      this.model = this.items.length > 0 ? this.items[0].name : '';
    },

    selectItem(name) {
      const item = this.items.find(item => item.name === name);
      this.model = item ? item.name : '';
    },

    itemClicked(item) {
      if (this.callback) {
        this.callback(item);
      }
    },

    btnClicked() {
      this.$emit('expand', this.show);
    }
  }
};
</script>

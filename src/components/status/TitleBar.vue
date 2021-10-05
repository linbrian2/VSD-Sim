<template>
  <div class="title-name">
    <v-row wrap no-gutters>
      <v-col lg="4" sm="6" xs="12">
        <div class="mt-2 ml-5">
          {{ loading ? 'Loading ...' : activeTitle }}
        </div>
      </v-col>
      <v-col lg="5" sm="4" xs="12">
        <span><slot></slot></span>
      </v-col>
      <v-col lg="3" sm="2" xs="12">
        <v-btn small icon @click.stop="refreshData()" class="title-btn float-right" :loading="loading">
          <v-icon color="white">mdi-refresh</v-icon>
        </v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import Keycloak from 'keycloak-js';
import { mapState } from 'vuex';
export default {
  props: ['title', 'loading', 'refresh'],
  computed: {
    activeTitle() {
      if (this.title !== undefined) {
        return this.title;
      } else {
        return this.activeSignal.name;
      }
    },
    activeSignal() {
      return (
        this.currentSignal || {
          id: 6,
          name: 'No Signal',
          phases: [],
          detectors: [],
          plans: []
        }
      );
    },
    ...mapState('status', ['currentSignal', 'currentDate'])
  },

  methods: {
    refreshData() {
      this.refresh();
    }
  }
};
</script>

<style lang="scss" scoped>
.title-name {
  background-color: hsl(200, 18%, 46%);
  padding: 8px 10px;
  margin-top: -8px;
  color: white;
  font-size: large;
  font-weight: bold;
}
.title-id {
  color: #888;
  font-size: small;
  font-weight: normal;
  margin-bottom: 30px;
}
.title-btn {
  margin-left: 5px;
  margin-top: 8px;
  display: inline-block;
}
</style>

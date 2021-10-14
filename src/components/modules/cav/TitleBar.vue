<template>
  <div class="title-name">
    <v-row wrap no-gutters>
      <v-col lg="4" sm="6" xs="12">
        <div>
          <span>{{ loading ? 'Loading ...' : activeTitle }}</span>
          <span class="ml-2" v-if="title === undefined && !loading">
            <v-chip class="mt-n1 white--text" color="green" outlined x-small>
              <span class="white--text">{{ activeSignal.id }}</span>
            </v-chip>
          </span>
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
    ...mapState('cav', ['currentSignal'])
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
  background-color: #5c6bc0;
  padding: 5px 10px;
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
  display: inline-block;
}
</style>

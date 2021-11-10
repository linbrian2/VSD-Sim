<template>
  <!-- Menu Items -->
  <v-list style="position: absolute; top: 60px; left:10px; min-width:300px;">
    <div>
      <v-list-item>
        <b>Segments to add:</b>
      </v-list-item>
    </div>
    <div v-if="!selectionMade">
      <v-list-item>
        None selected
      </v-list-item>
    </div>
    <div v-for="(item, i) in multigraphSegs" :key="item.id">
      <v-list-item v-if="selectionMade && i < 6">{{ item.info.description }}</v-list-item>
      <v-list-item v-if="selectionMade && i == 6">And {{ multigraphSegs.length - 8 }} more...</v-list-item>
    </div>
    <v-row>
      <v-col class="grid-right" cols="6">
        <v-btn @click="$store.state.bluetooth.modes.addFromMap = false">Cancel</v-btn>
      </v-col>
      <v-col class="grid-left" cols="6">
        <v-btn @click="submit()">Submit</v-btn>
      </v-col>
    </v-row>
  </v-list>
</template>

<script>
import { mapState } from 'vuex';
import { RouterPaths } from '@/utils/constants/router';

export default {
  methods: {
    submit() {
      this.$bus.$emit('SUBMIT_SEGMENTS', this.multigraphSegs.slice());
      this.$store.state.bluetooth.modes.addFromMap = false;
      setTimeout(() => {
        let path = RouterPaths.BLUETOOTH_MULTIGRAPH;
        this.$router.push({ path }).catch(() => {});
      }, 1);
    }
  },
  computed: {
    selectionMade() {
      return this.multigraphSegs && this.multigraphSegs.length > 0;
    },
    ...mapState('bluetooth', ['multigraphSegs'])
  }
};
</script>

<style lang="scss" scoped></style>

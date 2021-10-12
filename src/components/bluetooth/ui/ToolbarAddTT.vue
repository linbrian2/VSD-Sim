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
export default {
  props: ['multigraphSegs'],
  methods: {
    submit() {
      console.log('Submit');
      console.log(this.multigraphSegs);
      this.$store.commit('bluetooth/SET_SELECTED_PAGE', 1);
      this.$bus.$emit('SUBMIT_SEGMENTS', this.multigraphSegs);
    }
  },
  computed: {
    selectionMade() {
      return this.multigraphSegs && this.multigraphSegs.length > 0;
    }
  }
};
</script>

<style lang="scss" scoped></style>

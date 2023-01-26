<template>
  <div>
    <!-- <SelectionPanel title="Trip Data" v-if="!$vuetify.breakpoint.mobile"> -->
    <SelectionPanel title="Trip Data">
      <template v-slot:buttons>
        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn small icon @click.stop="chooseTrip" class="mr-6" v-on="on">
              <v-icon color="orange">mdi-car-arrow-right </v-icon>
            </v-btn>
          </template>
          <span>Choose Trip</span>
        </v-tooltip>
      </template>
      <TripDataCSPI />
    </SelectionPanel>
    <MapDisplayCSPI />
    <TripSelectionDialogCSPI v-model="showSelection" ref="dialog" />
  </div>
</template>

<script>
import TripDataCSPI from '@/components/modules/cav/TripDataCSPI';
import MapDisplayCSPI from '@/components/modules/cav/MapDisplayCSPI';
import SelectionPanel from '@/components/modules/cav/SelectionPanel';
import TripSelectionDialogCSPI from '@/components/modules/cav/TripSelectionDialogCSPI';

export default {
  components: {
    SelectionPanel,
    MapDisplayCSPI,
    TripDataCSPI,
    TripSelectionDialogCSPI
  },

  computed: {
    showPanel: {
      get() {
        return this.$store.state.cav.showPanel;
      },
      set(show) {
        this.$store.commit('cav/SHOW_PANEL', show);
      }
    }
  },

  data: () => ({
    showSelection: false
  }),

  methods: {
    chooseTrip() {
      if (this.$refs.dialog) {
        this.$refs.dialog.init();
        this.showSelection = true;
      }
    }
  }
};
</script>

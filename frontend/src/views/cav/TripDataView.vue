<template>
  <div>
    <!-- <SelectionPanel title="Trip Data" v-if="!$vuetify.breakpoint.mobile"> -->
    <SelectionPanel title="Trip Data">
      <!-- <template v-slot:buttons>
        <v-tooltip left>
          <template v-slot:activator="{ on }">
            <v-btn small icon @click.stop="chooseTrip" class="mr-6" v-on="on">
              <v-icon color="orange">mdi-car-arrow-right </v-icon>
            </v-btn>
          </template>
          <span>Choose Trip</span>
        </v-tooltip>
      </template> -->
      <TripData />
    </SelectionPanel>
    <MapDisplay />
    <TripSelectionDialog v-model="showSelection" ref="dialog" />
    <CSPIFullDialog :dialog="cspiFullDialog" @closeDialog="cspiFullDialog = false" />
  </div>
</template>

<script>
import TripData from '@/components/modules/cav/TripData';
import MapDisplay from '@/components/modules/cav/MapDisplay';
import SelectionPanel from '@/components/modules/cav/SelectionPanel';
import TripSelectionDialog from '@/components/modules/cav/TripSelectionDialog';
import CSPIFullDialog from '@/components/modules/cav/CSPIFullDialog';

export default {
  components: {
    SelectionPanel,
    MapDisplay,
    TripData,
    TripSelectionDialog,
    CSPIFullDialog
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
    showSelection: false,
    cspiFullDialog: false
  }),

  mounted() {
    this.$bus.$on('TOGGLE_CSPI_FULL', toggle => {
      this.cspiFullDialog = toggle;
    });
  },

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

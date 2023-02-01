<template>
  <div>
    <v-tooltip bottom open-delay="1000">
      <template v-slot:activator="{ on }">
        <v-btn small v-on="on" @click.stop="showReleaseNotes">
          <v-icon left small color="primary">mdi-alpha-v-box-outline</v-icon>
          <div class="grey--text">{{ version }}</div>
        </v-btn>
      </template>
      <span>Built on {{ buildTime }}</span>
    </v-tooltip>
    <ReleaseNotesDialog v-model="showDialog" ref="dialog" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import ReleaseNotesDialog from '@/components/nav/ReleaseNotesDialog';

export default {
  components: {
    ReleaseNotesDialog
  },
  data: () => ({
    showDialog: false
  }),
  computed: {
    version() {
      return process.env.VUE_APP_VERSION || '';
    },
    buildTime() {
      const d = process.env.VUE_APP_BUILD_TIME;
      return d ? Utils.formatDateTime(new Date(d)) : '';
    }
  },

  methods: {
    showReleaseNotes() {
      this.showDialog = true;
    }
  }
};
</script>

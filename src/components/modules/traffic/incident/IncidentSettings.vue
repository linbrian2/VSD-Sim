<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="500px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mr-1">mdi-cog-outline</v-icon>
          <v-toolbar-title>Settings</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="show = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>

        <v-card-text>
          <v-container fluid>
            <v-form ref="form" class="mx-2" lazy-validation>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="severity"
                    :rules="severityRules"
                    label="Minimum severity you want to show*"
                    type="number"
                    required
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    v-model="duration"
                    :rules="durationRules"
                    label="Minimum duration (minutes) you want to show*"
                    type="number"
                    required
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-form>
          </v-container>
        </v-card-text>
        <v-card-actions class="justify-end">
          <v-btn text @click="saveSettings">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>
<script>
import { mapState } from 'vuex';
export default {
  props: {
    value: Boolean
  },

  data: () => ({
    height: 470,
    severity: 50,
    duration: 30,
    severityRules: [
      v => !!v || 'Severity is required',
      v => (v >= 0 && v < 100) || 'Severity must be between 0 and 100'
    ],
    durationRules: [v => !!v || 'Duration is required', v => v >= 0 || 'Duration must be equal or larger than 0']
  }),

  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },
    ...mapState('traffic', ['incidentSettings'])
  },

  watch: {
    incidentSettings() {
      this.updateSettings();
    }
  },

  mounted() {
    this.updateSettings();
  },

  methods: {
    updateSettings() {
      const settings = this.incidentSettings;
      this.severity = settings.severity;
      this.duration = settings.duration;
    },

    saveSettings() {
      if (this.$refs.form.validate()) {
        const severity = parseInt(this.severity);
        const duration = parseInt(this.duration);
        this.$store.dispatch('traffic/saveIncidentSettings', { severity, duration });
        this.show = false;
      }
    }
  }
};
</script>

<style lang="scss" scoped></style>

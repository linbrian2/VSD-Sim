<template>
  <div>
    <!-- Title bar on the top -->
    <v-toolbar dark color="#607C8A" dense flat fixed overflow extension-height="0">
      <v-toolbar-title>
        <v-btn icon class="ml-n2" @click="goBack">
          <v-icon dark>mdi-cog-outline</v-icon>
        </v-btn>
        Preferences
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn small outlined class="mt-1" color="white" @click.stop="saveData">
        <v-icon left color="indigo darken-2">mdi-content-save </v-icon>
        <span> Save </span>
      </v-btn>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <v-btn icon dark @click="goBack"><v-icon>mdi-close</v-icon></v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <v-container style="width:900px">
      <v-row>
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>General</h3></v-subheader>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <v-container>
            <v-select outlined :items="['Dark Mode', 'Light Mode']" v-model="theme" filled label="Theme"></v-select>
          </v-container>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
            ><h3>Incident Display</h3></v-subheader
          >
          <v-divider />
        </v-col>
        <v-col cols="12">
          <v-container>
            <v-text-field
              v-model="severity"
              :rules="severityRules"
              label="Minimum severity to show"
              type="number"
              required
            ></v-text-field>
          </v-container>
        </v-col>
        <v-col cols="12">
          <v-container>
            <v-text-field
              v-model="duration"
              :rules="durationRules"
              label="Minimum duration (minutes) to show"
              type="number"
              required
            ></v-text-field>
          </v-container>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>UI</h3></v-subheader>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <v-container
            ><v-checkbox v-model="savePanel" label="Save left map display panel position"></v-checkbox>
          </v-container>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      savePanel: false,
      theme: 'Dark',
      severity: 50,
      duration: 30,
      severityRules: [
        v => !!v || 'Severity is required',
        v => (v >= 0 && v < 100) || 'Severity must be between 0 and 100'
      ],
      durationRules: [v => !!v || 'Duration is required', v => v >= 0 || 'Duration must be equal or larger than 0']
    };
  },

  methods: {
    goBack() {
      this.$router.go(-1);
    },

    saveData() {}
  }
};
</script>

<style lang="scss" scoped>
.title-name {
  background-color: hsl(200, 18%, 46%);
  padding: 5px 10px;
  margin-top: -8px;
  color: white;
  font-size: normal;
  font-weight: bold;
}

.non-selection {
  -moz-user-select: none;
  -khtml-user-select: none;
  -webkit-user-select: none;
  user-select: none;
}
</style>

<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" persistent max-width="290">
      <v-card>
        <v-card-title class="text-h5">
          {{ title }}
        </v-card-title>
        <v-card-text>
          {{ text }}
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" text @click="dialog = false">
            OK
          </v-btn>
          <v-btn color="green darken-1" text @click="dialog = false">
            CANCEL
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
export default {
  props: {
    title: String,
    text: String,
    width: Number
  }
};
</script>

<style lang="scss" scoped></style>

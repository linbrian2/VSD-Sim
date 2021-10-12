<template>
  <v-snackbar
    v-model="$store.state.bluetooth.notification.show"
    :timeout="$store.state.bluetooth.notification.timeout"
    :color="$store.state.bluetooth.notification.color"
  >
    {{ $store.state.bluetooth.notification.text }}
    <template v-slot:action="{ attrs }">
      <v-btn small fab text v-bind="attrs" @click="$store.state.bluetooth.notification.show = false">
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script>
export default {};
</script>

<style></style>

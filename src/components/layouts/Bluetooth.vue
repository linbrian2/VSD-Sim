<template>
  <div class="bluetooth">
    <AppBar />
    <v-main>
      <router-view />
      <BluetoothModules />
    </v-main>
  </div>
</template>

<script>
import AppBar from '@/components/modules/bluetooth/ui/AppBar';
import BluetoothModules from '@/components/modules/bluetooth/BluetoothModules'

export default {
  name: 'App',
  components: {
    AppBar,
    BluetoothModules
  }
};
</script>

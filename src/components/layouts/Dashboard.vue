<template>
  <div class="dashboard">
    <AppBar />
    <v-main>
      <router-view />
    </v-main>
    <BluetoothModules />
  </div>
</template>

<script>
import AppBar from '@/components/modules/dashboard/AppBar';
import BluetoothModules from '@/components/modules/bluetooth/BluetoothModules';

export default {
  name: 'App',
  components: {
    AppBar,
    BluetoothModules
  }
};
</script>

<template>
  <v-card class="mx-auto" elevation="0" style="position: absolute; bottom: 5px; right:0px;" color="rgb(0, 0, 0, 0.0)">
    <TimingChart />
  </v-card>
</template>

<script>
import TimingChart from '@/components/modules/hr/TimingChart';
export default {
  components: { TimingChart }
};
</script>

<template>
  <div id="dialogs">
    <DialogBTSensors />
    <DialogCongestion />
    <DialogDevices />
    <DialogTT />
    <DialogWaze />
  </div>
</template>

<script>
import DialogBTSensors from './DialogBTSensors.vue';
import DialogCongestion from './DialogCongestion.vue';
import DialogDevices from './DialogDevices.vue';
import DialogTT from './DialogTT.vue';
import DialogWaze from './DialogWaze.vue';

export default {
  components: {
    DialogBTSensors,
    DialogCongestion,
    DialogDevices,
    DialogTT,
    DialogWaze
  }
};
</script>

<style></style>
DialogBP

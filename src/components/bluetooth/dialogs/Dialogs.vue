<template>
  <div id="dialogs">
    <DialogBP />
    <DialogBTSensors />
    <DialogCongestion />
    <DialogDevices />
    <DialogTT />
    <DialogWaze />
  </div>
</template>

<script>
import DialogBP from "./DialogBP.vue";
import DialogBTSensors from "./DialogBTSensors.vue";
import DialogCongestion from "./DialogCongestion.vue";
import DialogDevices from "./DialogDevices.vue";
import DialogTT from "./DialogTT.vue";
import DialogWaze from "./DialogWaze.vue";

export default {
  components: {
    DialogBP,
    DialogBTSensors,
    DialogCongestion,
    DialogDevices,
    DialogTT,
    DialogWaze,
  },
};
</script>

<style>
</style>
DialogBP
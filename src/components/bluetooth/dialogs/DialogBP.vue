<template>
  <v-dialog v-model="$store.state.bluetooth.dialog.bp" max-width="100%" transition="scroll-x-transition">
    <v-card>
      <v-card-actions>
        <v-card-title>{{ $store.state.bluetooth.selectedDevice }}</v-card-title>
        <!-- Card Button Group -->
        <div class="middle-header">
          <v-list-item-content v-if="$store.state.bluetooth.selectedDevice" class="headline pt-1">
            <DateTimePicker @updateDate="updateDate" @updateTime="updateTime" />
          </v-list-item-content>
        </div>
        <v-btn icon class="close-button mr-4" @click="$store.state.bluetooth.dialog.bp = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-actions>
      <BPGraph :bpInfo="getBPInfo" :options="bpGraphOptions" />
    </v-card>
  </v-dialog>
</template>

<script>
import DateTimePicker from '@/components/bluetooth/ui/DateTimePicker';
import BPGraph from '@/components/bluetooth/graphs/BPGraph';

export default {
  components: {
    DateTimePicker,
    BPGraph
  },
  data() {
    return {
      bpGraphOptions: {
        containerName: 'window-bp-container'
      }
    };
  },
  computed: {
    getBPInfo() {
      return this.$store.state.bluetooth.bpInfo;
    }
  }
};
</script>

<style></style>

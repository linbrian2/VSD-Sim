<template>
  <v-row>
    <v-col cols="12">
      <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Nearby VMSs</h3></v-subheader>
      <v-divider />
    </v-col>
    <v-col cols="12">
      <div class="mx-4">
        <v-btn outlined v-for="vms in VMSs" :key="vms.id" @click.stop="showVMS(vms)" class="mr-5">
          <v-icon left color="teal">
            mdi-sign-caution
          </v-icon>
          {{ vms.id }}
        </v-btn>
      </div>
    </v-col>
  </v-row>
</template>

<script>
export default {
  props: {
    VMSs: Array
  },
  methods: {
    showVMS(vms) {
      this.$bus.$emit('SHOW_POPUP_VMS', vms);
    }
  }
};
</script>

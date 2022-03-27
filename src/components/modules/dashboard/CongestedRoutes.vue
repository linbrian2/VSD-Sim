<template>
  <div class="congested-routes">
    <TravelTimeTableCard :apiInfo="apiInfo" />
    <div class="pt-2">
      <TravelTimeGraphCard :apiInfo="apiInfo" :selection="['i95n', 0]" />
    </div>
    <v-card class="mt-2">
      <v-col>
        <v-row justify="center">
          <v-img alt="Logo" class="shrink" src="@/assets/tt-graph-sample.png" contain />
        </v-row>
      </v-col>
    </v-card>
    <v-card class="mt-2">
      <v-col>
        <v-row justify="center">
          <v-img alt="Logo" class="shrink" src="@/assets/collision-example.png" width="458" />
        </v-row>
      </v-col>
    </v-card>
  </div>
</template>

<script>
import TravelTimeTableCard from '@/components/modules/dashboard/TravelTimeTableCard.vue';
import TravelTimeGraphCard from '@/components/modules/dashboard/TravelTimeGraphCard.vue';

export default {
  props: {
    apiInfo: Object
  },
  components: {
    TravelTimeTableCard,
    TravelTimeGraphCard
  }
};
</script>

<style></style>

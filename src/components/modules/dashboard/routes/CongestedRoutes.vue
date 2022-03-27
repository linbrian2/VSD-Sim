<template>
  <div class="congested-routes">
    <TravelTimeTableCard :apiInfo="apiInfo" />
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
import TravelTimeTableCard from '@/components/modules/dashboard/routes/TravelTimeTableCard.vue';

export default {
  props: {
    apiInfo: Object
  },
  components: {
    TravelTimeTableCard
  }
};
</script>

<style></style>

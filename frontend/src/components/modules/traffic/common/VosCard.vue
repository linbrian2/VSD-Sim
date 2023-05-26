<template>
  <div>
    <v-card min-width="100%">
      <v-card-title
        class="justify-space-between my-n2 pa-1 grey darken-3"
        :class="$vuetify.breakpoint.mobile ? 'pt-2' : ''"
      >
        <v-chip class="mt-n1 ml-2" :color="color" small>
          <span>{{ vos.type }}</span>
        </v-chip>
        <h4 class="mr-2">{{ vos.time }}</h4>
      </v-card-title>

      <v-card-text class="mt-10">
        <v-row wrap no-gutters>
          <v-col>
            <div class="text-center">
              <h2 class="extra-bold text-success">
                <strong>{{ vos.speed }}</strong>
              </h2>
              <h5>Speed</h5>
            </div>
          </v-col>
          <v-col>
            <div class="text-center">
              <h2 class="extra-bold text-success">
                <strong>{{ vos.volume }}</strong>
              </h2>
              <h5>Vol</h5>
            </div>
          </v-col>
          <v-col>
            <div class="text-center">
              <h2 class="extra-bold text-success">
                <strong>{{ vos.occupancy }}</strong>
              </h2>
              <h5>Occ</h5>
            </div>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  props: ['vos', 'color']
};
</script>

<style lang="scss" scoped>
.text-center {
  text-align: center;
}
.text-success {
  color: #3c763d;
}
.extra-bold {
  font-size: 26px;
  font-weight: 500;
}
</style>

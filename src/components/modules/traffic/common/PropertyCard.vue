<template>
  <div>
    <v-card min-width="100%" :color="color">
      <v-card-title class="justify-center my-n2 pa-1 teal darken-4" :class="$vuetify.breakpoint.mobile ? 'pt-2' : ''">
        <div class="overline">{{ entry.name }}</div>
      </v-card-title>

      <v-card-text class="mt-6">
        <v-row wrap>
          <v-col :cols="cols" v-for="(item, index) in entry.items" :key="index">
            <div class="text-center">
              <h1 class="extra-bold" :style="`color: ${item.color ? item.color : '#3c763d'};`">
                <strong>{{ item.value }}</strong>
              </h1>
              <h5>{{ item.name }}</h5>
            </div>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  props: {
    entry: Object,
    cols: { type: Number, default: 6 },
    color: { type: String, default: '' }
  }
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

<template>
  <div>
    <v-card min-width="100%">
      <v-card-title class="justify-center my-n2 pa-1 grey darken-3">
        <v-chip class="mt-n1 mr-5" :color="item.color" outlined small>
          <span>{{ item.type }}</span>
        </v-chip>
        <h4>{{ item.time }}</h4>
      </v-card-title>

      <v-card-text class="mt-6">
        <div class="text-center">
          <h2 class="extra-bold text-success">
            <strong>{{ item.volume }}</strong>
          </h2>
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  props: ['item', 'color']
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

<template>
  <v-card tile min-width="100%" min-height="120px">
    <v-card-title class="d-flex justify-space-between my-n2 pa-1 teal darken-2">
      <h5 class="text-center ml-5">{{ info.title }}</h5>
      <div v-if="info.value !== undefined">
        <v-chip small outlined>{{ info.value }}</v-chip>
      </div>

      <v-btn icon @click="info.button.handler(info.button.params)" v-if="info.button">
        <v-icon dense :color="info.button.color" v-text="info.button.icon" />
      </v-btn>
    </v-card-title>

    <v-card-text class="mt-1 pb-0 mb-n2">
      <v-container>
        <div v-for="(item, index) in info.items" :key="index">
          <div class="d-flex justify-space-between my-2">
            <h4>{{ item.text }}</h4>
            <div>
              <h3 class="text-success" v-if="!isObject(item.value)">
                <strong class="text-truncate">{{ item.value }}</strong>
              </h3>
              <h3 class="text-danger" v-else>
                <strong>{{ item.value.text }}</strong>
              </h3>
            </div>
          </div>
        </div>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  props: {
    info: Object
  },
  methods: {
    isObject(value) {
      return typeof value === 'object' && value !== null;
    }
  }
};
</script>

<style lang="scss" scoped>
.text-center {
  text-align: center;
}
.text-success {
  color: #00c853;
}
.text-danger {
  color: #ff8080;
}
.text-truncate {
  text-overflow: ellipsis;
}
.extra-bold {
  font-size: 22px;
  font-weight: 500;
}
.label-text {
  text-transform: capitalize;
  font-size: 14;
}
</style>

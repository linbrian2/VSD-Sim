<template>
  <v-card shaped class="rounded-0 elevation-2" min-width="100%" min-height="220px">
    <v-card-title class="d-flex justify-space-between my-n2 pa-1">
      <!-- :class="['d-flex my-n2 pa-1', info.color]" -->
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
        <v-row :no-gutters="info.items.length > 2">
          <v-col cols="12" v-for="(item, index) in info.items" :key="index">
            <div class="text-center mb-2">
              <h2 class="text-success" v-if="!isObject(item.value)">
                <strong>{{ item.value }}</strong>
              </h2>
              <h2 class="text-danger" v-else>
                <strong>{{ item.value.text }}</strong>
              </h2>
              <h5 class="font-weight-thin label-text">{{ item.text }}</h5>
            </div>
          </v-col>
        </v-row>
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
  color: #3c763d;
}
.text-danger {
  color: #ff8080;
}
.extra-bold {
  font-size: 22px;
  font-weight: 500;
}
.label-text {
  text-transform: capitalize;
  font-size: 14;
}
.level-0 {
  background-color: #339900;
}
.level-1 {
  background-color: #00ff33;
}
.level-2 {
  background-color: #ffff00;
}
.level-3 {
  background-color: #ffc031;
}
.level-4 {
  background-color: #fa7a28;
}
.level-5 {
  background-color: #ff0000;
}
.level-6 {
  background-color: #c00000;
}
</style>

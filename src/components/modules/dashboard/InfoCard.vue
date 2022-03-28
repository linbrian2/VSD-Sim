<template>
  <v-card :min-height="height" class="grid-center">
    <v-col>
      <v-row>
        <v-col cols="5" class="grid-right pb-8">
          <v-icon :size="iconSize">{{ icon }}</v-icon>
        </v-col>
        <v-col cols="5" class="grid-center">
          <h3 style="font-size:24px">{{ name }}</h3>
          <h1 style="color: yellow; font-size:42px">{{ value }}</h1>
        </v-col>
      </v-row>
    </v-col>
  </v-card>
</template>

<script>
export default {
  props: {
    height: { type: String, default: '22vh' },
    iconSize: { type: Number, default: 75 },
    icon: { type: String, default: 'mdi-cog' },
    name: { type: String, default: 'Placeholder' },
    value: { type: String, default: '-' }
  }
};
</script>

<style lang="scss"></style>

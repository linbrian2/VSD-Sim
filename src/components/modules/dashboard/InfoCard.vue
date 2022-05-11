<template>
  <v-card :min-height="height" :color="cardColor" class="grid-center">
    <v-col>
      <v-row>
        <v-col :lg="wide ? 3 : 4" :class="`${wide ? 'grid-center' : 'grid-right'} pb-8`">
          <v-icon class="pr-1" :color="iconColor" :size="iconSize">{{ icon }}</v-icon>
        </v-col>
        <v-col :lg="wide ? 9 : 8" class="grid-left">
          <h3 :style="`color: ${color}; font-size:${titleFontSize}px`">{{ name }}</h3>
          <h1 :style="`color: ${color !== 'undefined' ? color : valueColor}; font-size:${valueFontSize}px`">
            <b>{{ value }}</b>
          </h1>
        </v-col>
      </v-row>
    </v-col>
  </v-card>
</template>

<script>
export default {
  props: {
    wide: { type: Boolean, default: false },
    titleFontSize: { type: Number, default: 24 },
    valueFontSize: { type: Number, default: 42 },
    cardColor: { type: String, default: undefined },
    color: { type: String, default: 'undefined' },
    iconColor: { type: String, default: 'amber' },
    height: { type: String, default: '22vh' },
    iconSize: { type: Number, default: 50 },
    icon: { type: String, default: 'mdi-cog' },
    name: { type: String, default: 'Placeholder' },
    value: { type: [Number, String], default: '-' },
    valueColor: { type: String, default: 'undefined' }
  }
};
</script>

<style lang="scss"></style>

<template>
  <v-card :height="height" :color="cardColor" :class="flex ? 'py-2' : 'grid-center'">
    <v-col>
      <v-row v-if="!flex">
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
      <v-row v-if="flex">
        <div style="display: flex">
          <v-icon class="pl-8 pr-3" :color="iconColor" :size="iconSize">{{ icon }}</v-icon>
          <h3 class="pt-3 pr-3" :style="`color: ${titleColor}; font-size:${titleFontSize}px`">{{ name }}:</h3>
          <h1 :style="`color: ${color !== 'undefined' ? color : valueColor}; font-size:${valueFontSize}px`">
            <b>{{ value }}</b>
          </h1>
        </div>
      </v-row>
    </v-col>
  </v-card>
</template>

<script>
export default {
  props: {
    wide: { type: Boolean, default: false },
    flex: { type: Boolean, default: false },
    titleFontSize: { type: Number, default: 24 },
    valueFontSize: { type: Number, default: 42 },
    cardColor: { type: String, default: undefined },
    color: { type: String, default: 'undefined' },
    titleColor: { type: String, default: '#FFC107' },
    iconColor: { type: String, default: '#FFC107' },
    height: { type: String, default: '21vh' },
    iconSize: { type: Number, default: 50 },
    icon: { type: String, default: 'mdi-cog' },
    name: { type: String, default: 'Placeholder' },
    value: { type: [Number, String], default: '-' },
    valueColor: { type: String, default: '#E0E0E0' }
  }
};
</script>

<style lang="scss"></style>

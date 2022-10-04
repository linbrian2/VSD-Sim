<template>
  <v-card :color="cardColor" :class="flex ? 'py-2' : 'grid-center'">
    <v-row v-if="colDisplay" class="mx-3">
      <v-col :cols="navigationWidth < 420 ? 12 : 5">
        <div style="display: flex;">
          <v-icon v-if="!hideIcons" class="pr-1" :color="iconColor" :size="30">{{ icon }}</v-icon>
          <h3 v-if="!(navigationWidth < 420)" :style="`color: ${titleColor}`">{{ name }}</h3>
          <h3 v-else class="pl-2" :style="`color: ${color !== 'undefined' ? color : valueColor};`">
            <b>{{ value }}</b>
          </h3>
        </div>
      </v-col>
      <v-col cols="7" v-if="!(navigationWidth < 420)">
        <h3 :style="`color: ${color !== 'undefined' ? color : valueColor};`">
          <div v-if="name == 'Evidence Counts' && value">
            <v-badge
              class="mr-8"
              v-for="(count, name, index) in value"
              :color="getEvidenceColor(name)"
              :key="index"
              :content="count"
              offset-x="5"
              offset-y="22"
              bordered
            >
              <v-icon v-text="getEvidenceIcon(name)"></v-icon>
            </v-badge>
          </div>
          <div v-else>
            <b>{{ value }}</b>
            <v-icon class="ml-2" v-if="name == 'Incident Id'" color="blue" @click="openIncidentId(value)">
              mdi-link
            </v-icon>
          </div>
        </h3>
      </v-col>
    </v-row>
    <v-col v-else>
      <v-row v-if="!flex">
        <v-col :lg="wide ? 3 : 4" :class="`${wide ? 'grid-center' : 'grid-right'} pb-8`">
          <v-icon v-if="!hideIcons" class="pr-1" :color="iconColor" :size="iconSize">{{ icon }}</v-icon>
        </v-col>
        <v-col :lg="wide ? 9 : 8" class="grid-left">
          <h3 :style="`color: ${color}; font-size:${titleFontSize}px`">{{ name }}</h3>
          <h3 :style="`color: ${color !== 'undefined' ? color : valueColor}; font-size:${valueFontSize}px`">
            <h3 :style="`color: ${color !== 'undefined' ? color : valueColor};`">
              <div v-if="name == 'Evidence Counts' && value">
                <v-badge
                  class="mr-8"
                  v-for="(count, name, index) in value"
                  :color="getEvidenceColor(name)"
                  :key="index"
                  :content="count"
                  offset-x="5"
                  offset-y="22"
                  bordered
                >
                  <v-icon v-text="getEvidenceIcon(name)"></v-icon>
                </v-badge>
              </div>
              <div v-else>
                <b>{{ value }}</b>
              </div>
            </h3>
          </h3>
        </v-col>
      </v-row>
      <v-row v-if="flex">
        <div style="display: flex">
          <v-icon v-if="!hideIcons" class="pl-8 pr-3" :color="iconColor" :size="iconSize">{{ icon }}</v-icon>
          <h3 class="pt-3 pr-3" :style="`color: ${titleColor}; font-size:${titleFontSize}px`">{{ name }}:</h3>
          <h3 :style="`color: ${color !== 'undefined' ? color : valueColor}; font-size:${valueFontSize}px`">
            <div v-if="name == 'Evidence Counts'">
              <v-badge
                class="mr-8"
                v-for="(count, name, index) in value"
                :color="getEvidenceColor(name)"
                :key="index"
                :content="count"
                offset-x="5"
                offset-y="22"
                bordered
              >
                <v-icon v-text="getEvidenceIcon(name)"></v-icon>
              </v-badge>
            </div>
            <div v-else>
              <b>{{ value }}</b>
            </div>
          </h3>
        </div>
      </v-row>
    </v-col>
  </v-card>
</template>

<script>
import Constants from '@/utils/constants/traffic';
import { RouterNames } from '@/utils/constants/router';
import { mapGetters } from 'vuex';

export default {
  props: {
    colDisplay: { type: Boolean, default: true },
    wide: { type: Boolean, default: false },
    flex: { type: Boolean, default: false },
    titleFontSize: { type: Number, default: 24 },
    valueFontSize: { type: Number, default: 32 },
    cardColor: { type: String, default: undefined },
    color: { type: String, default: 'undefined' },
    titleColor: { type: String, default: '#FFC107' },
    iconColor: { type: String, default: '#FFC107' },
    height: { type: String, default: '21vh' },
    iconSize: { type: Number, default: 50 },
    icon: { type: String, default: 'mdi-cog' },
    name: { type: String, default: 'Placeholder' },
    value: { type: [Number, String, Object], default: '-' },
    valueColor: { type: String }
  },
  computed: {
    valueColorA() {
      if (this.valueColor) {
        return this.valueColor;
      } else {
        if (this.$vuetify.theme.dark) {
          return '#E0E0E0';
        } else {
          return '#303030';
        }
      }
    },
    hideIcons() {
      return this.getSetting('dashboard', 'hideIconsRightPanel');
    },
    navigationWidth() {
      let navigationWidth = this.$store.state.navigationWidth;
      if (typeof navigationWidth === 'string') {
        return parseInt(this.$store.state.navigationWidth.split('px')[0]);
      } else {
        return this.$store.state.navigationWidth;
      }
    },
    ...mapGetters(['getSetting'])
  },

  methods: {
    openIncidentId(id) {
      const r = this.$router.resolve({ name: RouterNames.TRAFFIC_INCIDENT_PATH, params: { id } });
      console.log(r.href);
      window.open(r.href, '_blank');
    },

    getEvidenceIcon(name) {
      const ICONS = {
        flow: Constants.DEVICE_TRAFFIC_ICON,
        bluetooth: Constants.DEVICE_BLUETOOTH_ICON,
        waze: Constants.DEVICE_WAZE_ICON,
        restriction: Constants.DEVICE_RESTRICTIONS_ICON,
        weather: Constants.DEVICE_WEATHER_ICON,
        alert: Constants.DEVICE_ALERT_ICON
      };
      return ICONS[name];
    },

    getEvidenceColor(name) {
      const ICONS = {
        flow: 'purple',
        bluetooth: 'blue darken-4',
        waze: 'cyan accent-4',
        restriction: 'orange',
        weather: 'grey',
        alert: 'red'
      };
      return ICONS[name];
    }
  }
};
</script>

<style lang="scss"></style>

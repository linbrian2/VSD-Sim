<template>
  <div v-if="showWeatherBar">
    <v-card color="rgba(0, 0, 0, .5)" style="display:flex;" class="px-2">
      <!-- Increase font size -->
      <b v-if="showCurrentTime">
        <div :style="`color: ${color};`" class="py-2">
          {{ dateStr }}
        </div>
      </b>
      <template v-if="weather">
        <v-divider vertical class="mx-6" v-if="showCurrentTime" />
        <v-img class="mt-1 weather-icon" width="30px" height="30px" :src="weatherIcon(weather.conditionCode)" />
        <!-- <v-divider class="pl-3" vertical /> -->
        <v-icon v-if="weather.airTemp && showTemp" :color="`${color}`" class="pl-3">mdi-thermometer</v-icon>
        <div v-if="weather.airTemp && showTemp" :style="`color: ${color};`" class="pt-2">
          {{ formatTemperature(weather.airTemp, true) }}<sup>°F</sup>
        </div>
        <v-icon v-if="weather.precip && showPrecip" :color="`${color}`" class="pl-3">mdi-water</v-icon>
        <div v-if="weather.precip && showPrecip" :style="`color: ${color};`" class="pt-2">
          {{ toPercentage(weather.precip) }}%
        </div>
        <v-icon v-if="weather.windAvg && weather.windAvgHeading && showWind" :color="`${color}`" class="pl-3 pr-1"
          >mdi-weather-windy</v-icon
        >
        <div v-if="weather.windAvg && weather.windAvgHeading && showWind" :style="`color: ${color};`" class="pt-2">
          {{ Math.round(weather.windAvg) }}mph {{ weather.windAvgHeading }}
        </div>
        <v-icon v-if="weather.visibility && showVisibility" :color="`${color}`" class="pl-3 pr-1">mdi-eye</v-icon>
        <div v-if="weather.visibility && showVisibility" :style="`color: ${color};`" class="pt-2">
          {{ weather.visibility }}m
        </div></template
      >
    </v-card>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Api from '@/utils/api/traffic';
import { mapGetters, mapState } from 'vuex';

export default {
  props: {
    center: Object,
    showCurrentTime: Boolean
  },
  data() {
    return {
      date: new Date(),
      color: 'white',
      closestStation: null,
      weather: null
    };
  },
  computed: {
    dateStr() {
      if (this.date) {
        return `${Utils.formatDate(this.date)} ${Utils.formatTimeAsMinute(this.date)}`;
      } else {
        return null;
      }
    },
    showWeatherBar() {
      return this.getSetting('general', 'showWeatherBar');
    },
    showTemp() {
      return this.getSetting('general', 'showTemp');
    },
    showWind() {
      return this.getSetting('general', 'showWind');
    },
    showVisibility() {
      return this.getSetting('general', 'showVisibility');
    },
    showPrecip() {
      return this.getSetting('general', 'showPrecip');
    },
    ...mapGetters(['getSetting']),
    ...mapState('dashboard', ['weatherStations'])
  },
  mounted() {
    this.startUpdateInterval();
    if (this.weatherStations) {
      this.getWeatherInfo();
    }
  },
  methods: {
    startUpdateInterval() {
      this.updateInterval = setInterval(this.updateDate, 1000);
    },
    stopUpdateInterval() {
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.updateInterval = null;
      }
    },
    updateDate() {
      this.date = new Date();
    },
    async getWeatherInfo() {
      let idx = 0;
      let min = 999999;
      let center = this.center;
      this.weatherStations.forEach((x, i) => {
        let pos = x.position;
        let dist = Math.hypot(center.lat - pos.lat, center.lng - pos.lng);
        if (dist < min) {
          min = dist;
          idx = i;
        }
      });
      this.closestStation = this.weatherStations[idx];
      if (this.closestStation && this.closestStation.id) {
        let stationId = this.closestStation.id;
        const [weatherInfoRes] = await Promise.all([Api.fetchLatestWeatherDataInfo(stationId, 600)]);
        let weatherInfo = this.parseResponseData(weatherInfoRes);
        if (weatherInfo) {
          this.weather = this.formWeatherInfoData(weatherInfo);
        } else {
          this.weather = null;
        }
      }
    },
    parseResponseData(response) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          let data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      }
      return result;
    },
    formWeatherInfoData(info) {
      const result = {};
      Object.assign(result, info);
      return result;
    },
    weatherIcon(code) {
      if (code) {
        return require(`@/assets/weather/${code}.png`);
      }
    },
    toPercentage(value) {
      return Math.round(value * 100);
    },
    formatDate(time) {
      const d = new Date(time);
      return Utils.formatDateTime(d);
    },
    formatTemperature(temp, noUnit) {
      if (temp) {
        return Math.round(temp) + (noUnit ? '' : '°');
      } else {
        return '';
      }
    }
  },
  watch: {
    center() {
      if (this.weatherStations) {
        this.getWeatherInfo();
      }
    },
    weatherStations() {
      this.getWeatherInfo();
    }
  }
};
</script>

<style lang="scss">
.weather-overlay {
  display: flex;
}

.weather-icon {
  height: 40px;
  width: 40px;
}

.small-weather-icon {
  height: 40px;
  width: 40px;
}

.bullet-icon {
  font-size: 24px;
  fill: #aaa;
  &:hover {
    fill: #666;
  }
}

.temperature {
  display: flex;
  margin-left: 8px;
  margin-top: -8px;
  div {
    font-size: 64px;
    font-weight: 600;
  }
  sup {
    padding-right: 4px;
    font-weight: bold;
    font-size: 24px;
  }
}

.details {
  display: flex;
  flex-wrap: wrap;
  font-size: 16px;
  line-height: 3;
  padding: 13px 0px;
  li {
    margin-top: 10px;
    text-align: left;
    width: 50%;
    list-style: none;
  }
}

.condition-text {
  font-size: 28px;
  font-weight: bold;
  text-transform: uppercase;
  margin-top: 5px;
}

.updated-text {
  color: rgb(206, 194, 194);
  font-size: 10px;
}

.flex-view {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  width: 100%;
  padding: 10px 5px;
}

.time-weather-record {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  width: 100%;
}
</style>

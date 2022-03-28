<template>
  <v-card tile>
    <div class="row">
      <div class="col-4">
        <div class="d-flex flex-column align-center py-2 ml-4 mt-4">
          <div class="condition-text">{{ weather.conditionText }}</div>
          <div>
            <v-img class="weather-icon" :src="weatherIcon(weather.conditionCode)" />
            <div class="temperature" v-if="weather.airTemp">
              <div>{{ formatTemperature(weather.airTemp, true) }}<sup>°F</sup></div>
            </div>
          </div>
        </div>
      </div>
      <v-divider class="mx-1" vertical dark></v-divider>
      <div class="col">
        <ul class="details ml-n5">
          <li>
            <v-icon color="orange lighten-3" class="mr-1">mdi-thermometer</v-icon>
            Temp: <strong>{{ formatTemperature(weather.minTemp) }} / {{ formatTemperature(weather.maxTemp) }}</strong>
          </li>
          <li>
            <v-icon color="orange lighten-3" class="mr-1">mdi-weather-pouring</v-icon>
            Precipitation: <strong v-if="weather.precip">{{ toPercentage(weather.precip) }}%</strong>
          </li>
          <li>
            <v-icon color="orange lighten-3" class="mr-1">mdi-water</v-icon>
            Humidity: <strong>{{ weather.relHumidity }}%</strong>
          </li>
          <li v-if="weather.roadSurface">
            <v-icon color="orange lighten-3" class="mr-1">mdi-waves</v-icon>
            Surface: <strong>{{ weather.roadSurface }}</strong>
          </li>
          <li>
            <v-icon color="orange lighten-3" class="mr-1">mdi-weather-windy</v-icon>
            Wind: <strong>{{ Math.round(weather.windAvg) }}mph {{ weather.windAvgHeading }}</strong>
          </li>
          <li>
            <v-icon color="orange lighten-3" class="mr-1">mdi-eye-outline</v-icon>
            Visibility: <strong v-if="weather.visibility">{{ weather.visibility }}m</strong>
          </li>
          <li>
            <v-icon color="orange lighten-3" class="mr-1">mdi-weather-sunset-up</v-icon>
            Sunrise:
            <strong>{{ weather.sunrise }}</strong>
          </li>
          <li>
            <v-icon color="orange lighten-3" class="mr-1">mdi-weather-sunset-down</v-icon>
            Sunset:
            <strong>{{ weather.sunset }}</strong>
          </li>
        </ul>
      </div>
    </div>
    <v-divider class="mx-4" dark></v-divider>

    <div class="row">
      <div class="col mx-10">
        <div class="flex-view">
          <div class="time-weather-record" v-for="w in weather.hourlyWeather" :key="w.time">
            <span>{{ formatTime(w.time) }}</span>
            <v-img class="small-weather-icon my-2" :src="weatherIcon(w.code)" />
            <span>{{ formatTemperature(w.temp) }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="float-right mt-1 mb-3 mr-2">
      <div class="updated-text font-italic">Updated: {{ formatDate(weather.dataReceived) }}</div>
    </div>
  </v-card>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    weather: Object,
    color: {
      type: String,
      default: '#000'
    }
  },
  data: () => ({
    hourlyWeather: []
  }),
  methods: {
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

    formatTime(time) {
      const d = new Date(time);
      let h = d.getHours();
      let m = Math.floor(d.getMinutes() / 5) * 5;
      const ampm = h >= 12 ? 'PM' : 'AM';

      h = h % 12;
      h = h ? h : 12; // the hour '0' should be '12'
      m = m < 10 ? '0' + m : m;
      const strTime = h + ':' + m + ' ' + ampm;

      return strTime;
    },

    formatTemperature(temp, noUnit) {
      if (temp) {
        return Math.round(temp) + (noUnit ? '' : '°');
      } else {
        return '';
      }
    }
  }
};
</script>

<style lang="scss">
.weather-icon {
  height: 100px;
  width: 100px;
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
    font-size: 54px;
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
  font-size: 14px;
  line-height: 3;
  padding: 13px 0px;
  margin: 0px;
  li {
    margin-top: 10px;
    text-align: left;
    width: 50%;
    list-style: none;
  }
}

.condition-text {
  font-size: 20px;
  font-weight: bold;
  text-transform: uppercase;
  margin-top: 5px;
  inline-size: 150px;
  overflow-wrap: break-word;
  text-align: center;
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

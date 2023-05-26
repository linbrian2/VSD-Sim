<template>
  <v-row justify="center">
    <v-dialog v-model="show" persistent max-width="900px">
      <v-card>
        <v-app-bar color="#009688" dark dense>
          <v-icon class="mt-n1 mr-2">mdi-weather-cloudy</v-icon>
          <v-toolbar-title
            >{{ title }}<small>{{ subtitle }} </small></v-toolbar-title
          >
          <v-spacer></v-spacer>
          <v-btn icon @click="hideDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-app-bar>
        <v-container style="height:570px">
          <v-tabs color="teal accent-4" v-model="tab">
            <v-tab key="0">Current Info</v-tab>
            <v-tab key="1">Temperature</v-tab>
            <v-tab key="2">Wind Speed</v-tab>
            <v-tab key="3">Humidity</v-tab>
            <v-tab key="4">Visibility</v-tab>
            <v-tab key="5">Precipitation</v-tab>
          </v-tabs>
          <div>
            <v-tabs-items v-model="tab">
              <v-tab-item key="0">
                <div class="ma-4" min-height="470px" v-if="validInfo">
                  <WeatherInfo :weather="info" />
                </div>
                <div class="ma-2" min-height="470px" v-else>
                  <div class="text-center pt-15">
                    <h3>No current information available</h3>
                  </div>
                </div>
              </v-tab-item>
              <v-tab-item key="1">
                <BasicChart :data="temp" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="2">
                <BasicChart :data="wind" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="3">
                <BasicChart :data="humidity" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="4">
                <BasicChart :data="visibility" :height="height" :legendy="legendY" />
              </v-tab-item>
              <v-tab-item key="5">
                <BasicChart :data="precipitation" :height="height" :legendy="legendY" />
              </v-tab-item>
            </v-tabs-items>
          </div>
        </v-container>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Api from '@/utils/api/traffic';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import WeatherInfo from '@/components/modules/traffic/common/WeatherInfo';
export default {
  props: {
    value: Boolean
  },
  components: {
    BasicChart,
    WeatherInfo
  },
  data: () => ({
    height: 470,
    legendY: 15,
    title: '',
    subtitle: '',
    tab: null,
    temp: {},
    wind: {},
    humidity: {},
    visibility: {},
    precipitation: {},
    info: {},
    availability: {
      temp: false,
      relHumidity: false,
      windAvg: false,
      visibility: false,
      precip: false
    }
  }),
  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    },

    validInfo() {
      return this.isEmpty(this.info) === false;
    }
  },

  methods: {
    init(station) {
      this.tab = 0;
      if (station) {
        this.title = station.name;
        const time = new Date().getTime();
        this.fetchWeatherData(station.id, time);
      }
    },

    cleanUp() {
      this.tab = 0;
    },

    hideDialog() {
      this.show = false;
      this.cleanUp();
    },

    isEmpty(obj) {
      return Object.keys(obj).length === 0;
    },

    async fetchWeatherData(stationId, time) {
      this.loading = true;
      try {
        // Now we await for both results, whose async processes have already been started
        const [weatherDataRes, weatherInfoRes] = await Promise.all([
          Api.fetchWeatherDataOneDay(stationId, time),
          Api.fetchLatestWeatherDataInfo(stationId, 600)
        ]);

        let dataList = this.parseResponseData(weatherDataRes);
        if (dataList) {
          this.temp = this.formTemperatureData(dataList);
          this.wind = this.formWindSpeedData(dataList);
          this.humidity = this.formHumidityData(dataList);
          this.visibility = this.formVisibilityData(dataList);
          this.precipitation = this.formPrecipitationData(dataList);
        } else {
          this.temp = {};
          this.wind = {};
          this.humidity = {};
          this.visibility = {};
          this.precipitation = {};
        }

        let weatherInfo = this.parseResponseData(weatherInfoRes);
        if (weatherInfo) {
          this.info = this.formWeatherInfoData(weatherInfo);
        } else {
          this.info = {};
        }
      } catch (error) {
        //this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.loading = false;
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
      // } else {
      //   this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
      // }
      return result;
    },

    formTemperatureData(resData) {
      const title = 'Air Temperature';
      const xAxis = 'Time of day';
      const yAxis = 'Temperature (\u00B0F)';
      const data = [];
      if (resData.airTemp) {
        data.push({ name: 'Actual', color: '#ED561B', data: resData.airTemp });
      }
      return { data, xAxis, yAxis, title };
    },

    formWindSpeedData(resData) {
      const title = 'Average Wind Speed';
      const xAxis = 'Time of day';
      const yAxis = 'Wind Speed (km/h)';
      const data = [];
      if (resData.windAvg) {
        data.push({ name: 'Actual', color: '#50B432', data: resData.windAvg });
      }
      return { data, xAxis, yAxis, title };
    },

    formHumidityData(resData) {
      const title = 'Humidity Percentage';
      const xAxis = 'Time of day';
      const yAxis = 'Humidity Percentage (%)';
      const data = [];
      if (resData.relHumidity) {
        data.push({ name: 'Actual', color: '#058DC7', data: resData.relHumidity });
      }
      return { data, xAxis, yAxis, title };
    },

    formVisibilityData(resData) {
      const title = 'Visibility';
      const xAxis = 'Time of day';
      const yAxis = 'Visibility';
      const data = [];
      if (resData.visibility) {
        data.push({ name: 'Actual', color: '#FF7F00', data: resData.visibility });
      }
      return { data, xAxis, yAxis, title };
    },

    formPrecipitationData(resData) {
      const title = 'Precipitation';
      const xAxis = 'Time of day';
      const yAxis = 'Precipitation (%)';
      const data = [];
      if (resData.precip) {
        data.push({ name: 'Actual', color: '#24CBE5', data: resData.precip });
      }
      return { data, xAxis, yAxis, title };
    },

    formWeatherInfoData(info) {
      const result = {};
      Object.assign(result, info);
      return result;
    }
  }
};
</script>

<style lang="scss" scoped>
.text-center {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>

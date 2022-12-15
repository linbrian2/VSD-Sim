<template>
  <div>
    <v-container>
      <v-row id="info">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Current Info</h3></v-subheader>
            <div class="mt-4 mr-3">
              <v-chip class="ml-2 mt-n1" outlined small>
                <span>{{ station.id }}</span>
              </v-chip>
            </div>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <div min-height="470px" v-if="validInfo">
              <WeatherInfo :weather="info" />
            </div>
            <div class="ma-2" min-height="470px" v-else>
              <div class="text-center pt-15">
                <h3>No current information available</h3>
              </div>
            </div>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="isTemperature">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Temperature</h3></v-subheader>

            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showTempChart" class="mr-4 mt-2" :loading="loading">
                  <v-icon small>mdi-arrow-expand</v-icon>
                </v-btn>
              </template>
              <span>Expand</span>
            </v-tooltip>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="12">
              <BasicChart :data="temp" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="isWindSpeed">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Wind Speed</h3></v-subheader>
            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showWindChart" class="mr-4 mt-2" :loading="loading">
                  <v-icon small>mdi-arrow-expand</v-icon>
                </v-btn>
              </template>
              <span>Expand</span>
            </v-tooltip>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="12">
              <BasicChart :data="wind" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="isHumidity">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Humidity</h3></v-subheader>

            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showHumidityChart" class="mr-4 mt-2" :loading="loading">
                  <v-icon small>mdi-arrow-expand</v-icon>
                </v-btn>
              </template>
              <span>Expand</span>
            </v-tooltip>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="12">
              <BasicChart :data="humidity" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="isVisibility">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"><h3>Visibility</h3></v-subheader>

            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showVisibilityChart" class="mr-4 mt-2" :loading="loading">
                  <v-icon small>mdi-arrow-expand</v-icon>
                </v-btn>
              </template>
              <span>Expand</span>
            </v-tooltip>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="12">
              <BasicChart :data="visibility" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>

      <v-row v-if="isPrecipitation">
        <v-col cols="12">
          <div class="d-flex justify-space-between">
            <v-subheader class="pl-0 mx-4 font-weight-bold text-overline blue--text"
              ><h3>Precipitation</h3></v-subheader
            >

            <v-tooltip left>
              <template v-slot:activator="{ on }">
                <v-btn small icon v-on="on" @click.stop="showPrecipitationChart" class="mr-4 mt-2" :loading="loading">
                  <v-icon small>mdi-arrow-expand</v-icon>
                </v-btn>
              </template>
              <span>Expand</span>
            </v-tooltip>
          </div>
          <v-divider />
        </v-col>
        <v-col cols="12">
          <div class="mx-4">
            <v-card tile class="basic-chart" elevation="12">
              <BasicChart :data="precipitation" :height="height" :left="marginLeft" :legendy="legendY" />
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>
    <ChartDialog ref="chartDialog" v-model="showChartDialog" />
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import BasicChart from '@/components/modules/traffic/common/BasicChart';
import WeatherInfo from '@/components/modules/traffic/common/WeatherInfoSm';
import ChartDialog from '@/components/modules/traffic/common/ChartDialog';
export default {
  props: {
    station: Object
  },
  components: {
    BasicChart,
    WeatherInfo,
    ChartDialog
  },
  data: () => ({
    loading: false,
    showChartDialog: false,
    height: 300,
    legendY: 5,
    marginLeft: 80,
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
    validInfo() {
      return Object.keys(this.info).length > 0;
    },

    isTemperature() {
      return this.temp.data && this.temp.data.length > 0;
    },

    isWindSpeed() {
      return this.wind.data && this.wind.data.length > 0;
    },

    isHumidity() {
      return this.humidity.data && this.humidity.data.length > 0;
    },

    isVisibility() {
      return this.visibility.data && this.visibility.data.length > 0;
    },

    isPrecipitation() {
      return this.precipitation.data && this.precipitation.data.length > 0;
    }
  },

  mounted() {
    this.init(this.station);
  },

  methods: {
    init(station) {
      if (station) {
        const time = new Date().getTime();
        this.fetchWeatherData(station.id, time);
      }
    },

    showTempChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Temperature', this.temp);
    },

    showWindChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Wind speed', this.wind);
    },

    showHumidityChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Humidity', this.humidity);
    },

    showVisibilityChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('visibility', this.visibility);
    },

    showPrecipitationChart() {
      this.showChartDialog = true;
      this.$refs.chartDialog.init('Precipitation', this.precipitation);
    },

    async fetchWeatherData(stationId, time) {
      this.loading = true;
      try {
        // Now we await for both results, whose async processes have already been started
        const [weatherDataRes, weatherInfoRes] = await Promise.all([
          Api.fetchWeatherDataOneDay(stationId, time),
          Api.fetchLatestWeatherDataInfo(stationId, 1800)
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
      const title = '';
      const xAxis = 'Time of day';
      const yAxis = 'Temperature (\u00B0F)';
      const data = [];
      if (resData.airTemp) {
        data.push({ name: 'Actual', color: '#ED561B', data: resData.airTemp });
      }
      return { data, xAxis, yAxis, title };
    },

    formWindSpeedData(resData) {
      const title = '';
      const xAxis = 'Time of day';
      const yAxis = 'Wind Speed (km/h)';
      const data = [];
      if (resData.windAvg) {
        data.push({ name: 'Actual', color: '#50B432', data: resData.windAvg });
      }
      return { data, xAxis, yAxis, title };
    },

    formHumidityData(resData) {
      const title = '';
      const xAxis = 'Time of day';
      const yAxis = 'Humidity Percentage (%)';
      const data = [];
      if (resData.relHumidity) {
        data.push({ name: 'Actual', color: '#058DC7', data: resData.relHumidity });
      }
      return { data, xAxis, yAxis, title };
    },

    formVisibilityData(resData) {
      const title = '';
      const xAxis = 'Time of day';
      const yAxis = 'Visibility';
      const data = [];
      if (resData.visibility) {
        data.push({ name: 'Actual', color: '#FF7F00', data: resData.visibility });
      }
      return { data, xAxis, yAxis, title };
    },

    formPrecipitationData(resData) {
      const title = '';
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

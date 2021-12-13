<template>
  <div>
    <MapMultigraphSelectionPanel
      ref="mapSelectPanel"
      :markers="markers"
      :items="stationItems"
      :icons="icons"
      name="weatherSideBarWidth"
      :onMarkerClick="markerClicked"
    />
    <TitleBar :isMultigraph="true" :showId="false" :loading="loading" :refresh="refreshData">
      <div class="d-flex align-items justify-space-between align-center">
        <div class="d-flex justify-space-between">
          <div class="mt-1 mr-6" style="width: 200px">
            <v-select
              dark
              dense
              v-model="selectedVal"
              :items="valItems"
              item-text="text"
              item-value="value"
              hide-details
              single-line
            />
          </div>

          <div class="mt-1 mr-6" style="width: 120px">
            <v-select
              dark
              style="width: 140px; font-size: 14px"
              dense
              v-model="interval"
              :items="intervalItems"
              item-text="text"
              item-value="value"
              @input="intervalSelected"
              hide-details
              prepend-icon="mdi-clock-outline"
              single-line
            />
          </div>
        </div>
      </div>
    </TitleBar>
    <v-container ref="myDiv">
      <v-card class="mb-8" v-if="availability.temp && selectedVal == valItems[0]">
        <v-row>
          <v-col cols="12" xl="6" v-for="i in 7" :key="i.id">
            <BasicChart :data="weather.temp" :height="height" />
          </v-col>
        </v-row>
      </v-card>

      <v-card class="mb-8" v-if="availability.relHumidity && selectedVal == valItems[1]">
        <v-row>
          <v-col cols="12" xl="6" v-for="i in 7" :key="i.id">
            <BasicChart :data="weather.relHumidity" :height="height" />
          </v-col>
        </v-row>
      </v-card>

      <v-card class="mb-8" v-if="availability.windAvg && selectedVal == valItems[2]">
        <v-row>
          <v-col cols="12" xl="6" v-for="i in 7" :key="i.id">
            <BasicChart :data="weather.windAvg" :height="height" />
          </v-col>
        </v-row>
      </v-card>

      <v-card class="mb-8" v-if="availability.visibility && selectedVal == valItems[3]">
        <v-row>
          <v-col cols="12" xl="6" v-for="i in 7" :key="i.id">
            <BasicChart :data="weather.visibility" :height="height" />
          </v-col>
        </v-row>
      </v-card>

      <v-card class="mb-8" v-if="availability.precip && selectedVal == valItems[4]">
        <v-row>
          <v-col cols="12" xl="6" v-for="i in 7" :key="i.id">
            <BasicChart :data="weather.precip" :height="height" />
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState, mapActions } from 'vuex';
import MapMultigraphSelectionPanel from '@/components/modules/traffic/map/MapMultigraphSelectionPanel';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import BasicChart from '@/components/modules/traffic/common/BasicChart';

export default {
  components: {
    MapMultigraphSelectionPanel,
    TitleBar,
    BasicChart,
  },
  data: () => ({
    selectedVal: 'Air Temperature',
    valItems: ['Air Temperature', 'Humidity Percentage', 'Average Wind Speed', 'Visibility', 'Precipitation'],

    loading: false,
    height: 420,
    interval: 300000,
    title: 'Weather Data',
    intervalItems: [
      { text: '1 Hour', value: 3600000 },
      { text: '30 mins', value: 1800000 },
      { text: '15 mins', value: 900000 },
      { text: '5 mins', value: 300000 },
    ],
    icons: [
      {
        url: require('@/assets/icon30.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' },
      },
      {
        url: require('@/assets/icon30-select.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' },
      },
    ],
    weather: {},
    availability: {
      temp: false,
      relHumidity: false,
      windAvg: false,
      visibility: false,
      precip: false,
    },
  }),

  computed: {
    markers() {
      return this.weatherStations;
    },

    stationItems() {
      let names = [];
      this.weatherStations.forEach((location) => {
        names.push(location.name.trimRight());
      });
      return names;
    },

    multigraphModeSelect: {
      get() {
        return this.$store.state.traffic.multigraphModeSelect;
      },
      set(val) {
        this.$store.commit('traffic/SET_MULTIGRAPH_MODE_SELECT', val);
      },
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['weatherStations', 'multigraphModes']),
  },

  mounted() {
    if (this.weatherStations.length === 0) {
      this.fetchWeatherStations();
    }

    // Load first selected data in case of no data showing
    setTimeout(() => {
      this.showDataIfEmpty();
    }, 500);
  },

  watch: {
    currentDate() {
      this.refreshData();
    },
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  methods: {
    markerClicked(marker) {
      const time = this.currentDate.getTime();
      this.fetchWeatherData(marker.id, this.interval, time);
    },

    intervalSelected() {
      this.fetchData();
    },

    refreshData() {
      this.fetchData();
    },

    showDataIfEmpty() {
      const any = Object.values(this.availability).some((item) => item);
      if (!any) {
        this.$bus.$emit('CENTER_MAP');
        this.$refs.mapSelectPanel.triggerFirstMarkerClick();
      }
    },

    fetchData() {
      const marker = this.$refs.mapSelectPanel.getSelectedMarker();
      const time = this.currentDate.getTime();
      if (marker != null) {
        this.fetchWeatherData(marker.id, this.interval, time);
      }
    },

    async fetchWeatherData(id, interval, time) {
      this.loading = true;

      try {
        const response = await Api.fetchWeatherData(id, interval, time);
        const data = this.getResponseData(response);
        if (data != null) {
          this.availability.temp = data.airTemp ? true : false;
          this.availability.relHumidity = data.relHumidity ? true : false;
          this.availability.windAvg = data.windAvg ? true : false;
          this.availability.visibility = data.visibility ? true : false;
          this.availability.precip = data.precip ? true : false;

          this.weather.temp = this.formTempData(data);
          this.weather.relHumidity = this.formHumidityData(data);
          this.weather.windAvg = this.formWindData(data);
          this.weather.visibility = this.formVisibilityData(data);
          this.weather.precip = this.formPrecipData(data);
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }

      this.loading = false;
    },

    getResponseData(response) {
      let result = null;
      if (response.data.status === 'OK') {
        if (response.data.data !== undefined) {
          const data = response.data.data;
          if (Object.keys(data).length > 0) {
            result = data;
          }
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'info' });
      }
      return result;
    },

    formTempData(resData) {
      const title = 'NWMS07 - DE 7 @ VALLEY RD';
      const subtitle = 'Air Temperature';
      const xAxis = 'Time of day';
      const yAxis = 'Temperature (\u00B0F)';
      const data = [];
      if (resData.airTemp) {
        data.push({ name: 'Actual', color: '#ED561B', data: resData.airTemp });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    formHumidityData(resData) {
      const title = 'NWMS07 - DE 7 @ VALLEY RD';
      const subtitle = 'Humidity Percentage';
      const xAxis = 'Time of day';
      const yAxis = 'Humidity Percentage (%)';
      const data = [];
      if (resData.relHumidity) {
        data.push({ name: 'Actual', color: '#058DC7', data: resData.relHumidity });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    formWindData(resData) {
      const title = 'NWMS07 - DE 7 @ VALLEY RD';
      const subtitle = 'Average Wind Speed';
      const xAxis = 'Time of day';
      const yAxis = 'Wind Speed (km/h)';
      const data = [];
      if (resData.windAvg) {
        data.push({ name: 'Actual', color: '#50B432', data: resData.windAvg });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    formVisibilityData(resData) {
      const title = 'NWMS07 - DE 7 @ VALLEY RD';
      const subtitle = 'Visibility';
      const xAxis = 'Time of day';
      const yAxis = 'Visibility';
      const data = [];
      if (resData.visibility) {
        data.push({ name: 'Actual', color: '#FF7F00', data: resData.visibility });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    formPrecipData(resData) {
      const title = 'NWMS07 - DE 7 @ VALLEY RD';
      const subtitle = 'Precipitation';
      const xAxis = 'Time of day';
      const yAxis = 'Precipitation (%)';
      const data = [];
      if (resData.precip) {
        data.push({ name: 'Actual', color: '#24CBE5', data: resData.precip });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    ...mapActions('traffic', ['fetchWeatherStations']),
  },
};
</script>

<style lang="scss" scoped>
.active-select {
  background-color: red;
  color: white;
  border-style: solid;
  border-width: 5px;
}
.option-right {
  margin-left: auto;
  margin-right: 0;
}
</style>
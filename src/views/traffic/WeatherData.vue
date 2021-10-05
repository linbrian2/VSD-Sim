<template>
  <div>
    <MapSelectionPanel
      ref="mapSelectPanel"
      :markers="markers"
      :items="stationItems"
      :icons="icons"
      name="weatherSideBarWidth"
      :onMarkerClick="markerClicked"
    />
    <TitleBar :title="title" :showId="true" :loading="loading" :refresh="refreshData">
      <div style="margin-left:50px; margin-top:-3px">
        <v-select
          dark
          style="width:140px; font-size: 14px"
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
    </TitleBar>

    <v-container>
      <v-card class="mb-8" v-if="availability.temp">
        <BasicChart :data="weather.temp" :height="height" />
      </v-card>

      <v-card class="mb-8" v-if="availability.relHumidity">
        <BasicChart :data="weather.relHumidity" :height="height" />
      </v-card>

      <v-card class="mb-8" v-if="availability.windAvg">
        <BasicChart :data="weather.windAvg" :height="height" />
      </v-card>

      <v-card class="mb-8" v-if="availability.visibility">
        <BasicChart :data="weather.visibility" :height="height" />
      </v-card>

      <v-card class="mb-8" v-if="availability.precip">
        <BasicChart :data="weather.precip" :height="height" />
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState, mapActions } from 'vuex';
import MapSelectionPanel from '@/components/traffic/map/MapSelectionPanel';
import TitleBar from '@/components/traffic/common/TitleBar';
import BasicChart from '@/components/traffic/common/BasicChart';

export default {
  components: {
    MapSelectionPanel,
    TitleBar,
    BasicChart
  },
  data: () => ({
    loading: false,
    height: 420,
    interval: 300000,
    title: 'Weather Data',
    intervalItems: [
      { text: '1 Hour', value: 3600000 },
      { text: '30 mins', value: 1800000 },
      { text: '15 mins', value: 900000 },
      { text: '5 mins', value: 300000 }
    ],
    icons: [
      {
        url: require('@/assets/icon30.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' }
      },
      {
        url: require('@/assets/icon30-select.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' }
      }
    ],
    weather: {},
    availability: {
      temp: false,
      relHumidity: false,
      windAvg: false,
      visibility: false,
      precip: false
    }
  }),

  computed: {
    markers() {
      return this.weatherStations;
    },

    stationItems() {
      let names = [];
      this.weatherStations.forEach(location => {
        names.push(location.name.trimRight());
      });
      return names;
    },
    
    ...mapState('traffic', ['weatherStations', 'currentDate'])
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
    }
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
      const any = Object.values(this.availability).some(item => item);
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
        this.$store.dispatch('traffic/setSystemStatus', { text: error, color: 'error' });
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
        this.$store.dispatch('traffic/setSystemStatus', { text: response.data.message, color: 'error' });
      }
      return result;
    },

    formTempData(resData) {
      const title = 'Air Temperature';
      const xAxis = 'Time of day';
      const yAxis = 'Temperature (\u00B0F)';
      const data = [];
      if (resData.airTemp) {
        data.push({ name: 'Actual', color: '#ED561B', data: resData.airTemp });
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

    formWindData(resData) {
      const title = 'Average Wind Speed';
      const xAxis = 'Time of day';
      const yAxis = 'Wind Speed (km/h)';
      const data = [];
      if (resData.windAvg) {
        data.push({ name: 'Actual', color: '#50B432', data: resData.windAvg });
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

    formPrecipData(resData) {
      const title = 'Precipitation';
      const xAxis = 'Time of day';
      const yAxis = 'Precipitation (%)';
      const data = [];
      if (resData.precip) {
        data.push({ name: 'Actual', color: '#24CBE5', data: resData.precip });
      }
      return { data, xAxis, yAxis, title };
    },

    ...mapActions('traffic', ['fetchWeatherStations'])
  }
};
</script>

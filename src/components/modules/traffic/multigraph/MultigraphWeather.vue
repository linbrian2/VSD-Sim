<template>
  <div>
    <SelectionPanel name="weatherDataBarWidth">
      <v-combobox
        multiple
        small-chips
        class="mx-2"
        dense
        hide-details
        single-line
        :items="items"
        item-text="name"
        v-model="valuesSelected"
        @input="valueSelectHandler"
        label="SELECT WEATHER LOCATIONS"
        return-object
      >
        <template v-slot:append-outer>
          <v-btn icon @click="clear">
            <v-icon>mdi-backspace</v-icon>
          </v-btn>
        </template>
      </v-combobox>
      <MapMultigraphSelect ref="mapSelect" :markers="markers" :icons="markerIcons" @click="markerClicked" />
    </SelectionPanel>
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
      <v-card class="mb-8" v-if="selectedVal == valItems[0]">
        <v-row>
          <v-col cols="12" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
            <div v-if="i.data && i.data.temp" class="graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <BasicChart :data="i.data.temp" :height="height" />
            </div>
            <div v-else-if="i.data == -1" class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Data is Unavailable.</h3>
            </div>
            <div v-else class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Loading Data...</h3>
            </div>
          </v-col>
        </v-row>
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[1]">
        <v-row>
          <v-col cols="12" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
            <div v-if="i.data && i.data.relHumidity" class="graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <BasicChart :data="i.data.relHumidity" :height="height" />
            </div>
            <div v-else-if="i.data == -1" class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Data is Unavailable.</h3>
            </div>
            <div v-else class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Loading Data...</h3>
            </div>
          </v-col>
        </v-row>
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[2]">
        <v-row>
          <v-col cols="12" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
            <div v-if="i.data && i.data.windAvg" class="graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <BasicChart :data="i.data.windAvg" :height="height" />
            </div>
            <div v-else-if="i.data == -1" class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Data is Unavailable.</h3>
            </div>
            <div v-else class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Loading Data...</h3>
            </div>
          </v-col>
        </v-row>
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[3]">
        <v-row>
          <v-col cols="12" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
            <div v-if="i.data && i.data.visibility" class="graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <BasicChart :data="i.data.visibility" :height="height" />
            </div>
            <div v-else-if="i.data == -1" class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Data is Unavailable.</h3>
            </div>
            <div v-else class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Loading Data...</h3>
            </div>
          </v-col>
        </v-row>
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[4]">
        <v-row>
          <v-col cols="12" xl="6" class="pt-0" v-for="i in valuesSelected" :key="i.id">
            <div v-if="i.data && i.data.precip" class="graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <BasicChart :data="i.data.precip" :height="height" />
            </div>
            <div v-else-if="i.data == -1" class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Data is Unavailable.</h3>
            </div>
            <div v-else class="grid-center graph-container">
              <v-btn icon @click="removeItem(i.id)" class="graph-close-button">
                <v-icon>mdi-close</v-icon>
              </v-btn>
              <h2>{{ i.name }}</h2>
              <h3>Loading Data...</h3>
            </div>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState, mapActions } from 'vuex';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapMultigraphSelect from '@/components/modules/traffic/map/MapMultigraphSelect';
import TitleBar from '@/components/modules/traffic/common/TitleBar';
import BasicChart from '@/components/modules/traffic/common/BasicChart';

export default {
  components: {
    SelectionPanel,
    MapMultigraphSelect,
    TitleBar,
    BasicChart,
  },
  data: () => ({
    valuesSelected: [],
    defaultIcons: [
      {
        path: 0,
        scale: 10.0,
        fillColor: '#05FF00',
        fillOpacity: 0.8,
        strokeWeight: 0.4,
      },
      {
        path: 0,
        scale: 10.0,
        fillColor: '#FF7F00',
        fillOpacity: 0.8,
        strokeWeight: 0.4,
      },
    ],

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
  }),

  computed: {
    markerIcons() {
      return this.icons ? this.icons : this.defaultIcons;
    },

    markers() {
      return this.weatherStations;
    },

    items() {
      return this.weatherStations.map((location) => {
        return { id: location.id, name: location.name.trimRight(), data: null };
      });
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
    clear() {
      this.valuesSelected = [];
      this.$bus.$emit('NAME_SELECTED', []);
    },

    removeItem(id) {
      this.valuesSelected = this.valuesSelected.filter((x) => x.id && x.id != id);
      this.$bus.$emit('NAME_SELECTED', this.valuesSelected);
    },

    triggerFirstMarkerClick() {
      this.$refs.mapSelect.triggerFirstMarkerClick();
    },

    getSelectedMarker() {
      return this.$refs.mapSelect.getSelectedMarker();
    },

    valueSelectHandler(value) {
      console.log(value);
      if (value && value.length > 0 && value[value.length - 1]) {
        let marker = this.markers.find((m) => m.id === value[value.length - 1].id);
        const time = this.currentDate.getTime();
        this.fetchWeatherData(marker.id, this.interval, time, marker.name);
        this.$bus.$emit('NAME_SELECTED', value);
      }
    },

    selectDeviceById(deviceId) {
      this.$bus.$emit('ID_SELECTED', deviceId);
      if (!this.showPanel) {
        this.$store.commit('traffic/SHOW_PANEL', true);
      }
    },

    selectDevicesByIds(ids) {
      this.$refs.mapSelect.selectByIds(ids);
      if (!this.showPanel) {
        this.$store.commit('traffic/SHOW_PANEL', true);
      }
    },

    markerClicked(marker, action, fromMap = true) {
      if (fromMap) {
        if (action == 'remove') {
          this.valuesSelected = this.valuesSelected.filter((x) => x.name && x.name != marker.name);
        } else {
          this.valuesSelected.push({ id: marker.id, name: marker.name, data: null });
          const time = this.currentDate.getTime();
          this.fetchWeatherData(marker.id, this.interval, time, marker.name);
        }
      }
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
      const time = this.currentDate.getTime();
      this.valuesSelected.forEach(x => {
        let marker = this.markers.find(m => m.id === x.id);
        this.fetchWeatherData(marker.id, this.interval, time, marker.name);
      });
    },

    async fetchWeatherData(id, interval, time, name) {
      this.loading = true;

      try {
        const response = await Api.fetchWeatherData(id, interval, time);
        const dataList = this.getResponseData(response);
        let data = -1;
        if (dataList) {
          data = {
            temp: this.formTempData(dataList, name),
            relHumidity: this.formHumidityData(dataList, name),
            windAvg: this.formWindData(dataList, name),
            visibility: this.formVisibilityData(dataList, name),
            precip: this.formPrecipData(dataList, name),
          };
        }
        this.valuesSelected.forEach((val) => {
          if (val.id == id) {
            val.data = data;
          }
        });
      } catch (error) {
        this.valuesSelected.forEach((val) => {
          if (val.id == id) {
            val.data = -1;
          }
        });
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

    formTempData(resData, title = null) {
      const subtitle = 'Air Temperature';
      const xAxis = 'Time of day';
      const yAxis = 'Temperature (\u00B0F)';
      const data = [];
      if (resData.airTemp) {
        data.push({ name: 'Actual', color: '#ED561B', data: resData.airTemp });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    formHumidityData(resData, title = null) {
      const subtitle = 'Humidity Percentage';
      const xAxis = 'Time of day';
      const yAxis = 'Humidity Percentage (%)';
      const data = [];
      if (resData.relHumidity) {
        data.push({ name: 'Actual', color: '#058DC7', data: resData.relHumidity });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    formWindData(resData, title = null) {
      const subtitle = 'Average Wind Speed';
      const xAxis = 'Time of day';
      const yAxis = 'Wind Speed (km/h)';
      const data = [];
      if (resData.windAvg) {
        data.push({ name: 'Actual', color: '#50B432', data: resData.windAvg });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    formVisibilityData(resData, title = null) {
      const subtitle = 'Visibility';
      const xAxis = 'Time of day';
      const yAxis = 'Visibility';
      const data = [];
      if (resData.visibility) {
        data.push({ name: 'Actual', color: '#FF7F00', data: resData.visibility });
      }
      return { data, xAxis, yAxis, title, subtitle };
    },

    formPrecipData(resData, title = null) {
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
.graph-container {
  position: relative;
}
.graph-close-button {
  position: absolute; 
  right: 0px;
  z-index: 99;
}
.basic-chart {
  height: 500px;
}
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
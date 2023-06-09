<template>
  <div class="desktop" v-if="!$vuetify.breakpoint.mobile">
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
        <template v-slot:selection="{ attrs, item, parent, selected }">
          <v-chip v-if="item === Object(item)" v-bind="attrs" :input-value="selected" label small>
            <span class="pr-1">
              {{ item.id }}
            </span>
            <v-icon small @click="parent.selectItem(item)">
              $delete
            </v-icon>
          </v-chip>
        </template>
      </v-combobox>
      <MapMultigraphSelect ref="mapSelect" :markers="markers" :icons="markerIcons" @click="markerClicked" />
    </SelectionPanel>
    <TitleBar :loading="loading" :refresh="refreshData">
      <div class="d-flex align-items justify-center align-center">
        <div class="d-flex justify-space-between">
          <div class="mt-1 mr-6" style="width: 140px">
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

          <div class="mt-1 mr-5" style="width: 100px">
            <v-select
              dark
              dense
              v-model="interval"
              :items="intervalItems"
              item-text="text"
              item-value="value"
              @input="intervalSelected"
              hide-details
              single-line
            />
          </div>

          <div class="mt-1" style="width: 110px">
            <v-select
              dark
              dense
              v-model="cols"
              :items="columnItems"
              item-text="text"
              item-value="value"
              hide-details
              single-line
            />
          </div>
        </div>
      </div>
    </TitleBar>
    <v-container ref="myDiv">
      <v-card class="mb-8" v-if="selectedVal == valItems[0]">
        <MultigraphDataEntries :valuesSelected="valuesSelected" :param="'temp'" :cols="cols" @removeItem="removeItem" />
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[1]">
        <MultigraphDataEntries
          :valuesSelected="valuesSelected"
          :param="'relHumidity'"
          :cols="cols"
          @removeItem="removeItem"
        />
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[2]">
        <MultigraphDataEntries
          :valuesSelected="valuesSelected"
          :param="'windAvg'"
          :cols="cols"
          @removeItem="removeItem"
        />
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[3]">
        <MultigraphDataEntries
          :valuesSelected="valuesSelected"
          :param="'visibility'"
          :cols="cols"
          @removeItem="removeItem"
        />
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[4]">
        <MultigraphDataEntries
          :valuesSelected="valuesSelected"
          :param="'precip'"
          :cols="cols"
          @removeItem="removeItem"
        />
      </v-card>
    </v-container>
  </div>

  <div class="mobile" v-else>
    <TitleBar :loading="loading" :refresh="refreshData" :showRefresh="!$vuetify.breakpoint.xs" :showMap="false">
      <div :style="'height: 45px'" />
    </TitleBar>

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
      <template v-slot:selection="{ attrs, item, parent, selected }">
        <v-chip v-if="item === Object(item)" v-bind="attrs" :input-value="selected" label small>
          <span class="pr-1">
            {{ item.id }}
          </span>
          <v-icon small @click="parent.selectItem(item)">
            $delete
          </v-icon>
        </v-chip>
      </template>
    </v-combobox>

    <MapMultigraphSelect ref="mapSelect" :markers="markers" :icons="markerIcons" @click="markerClicked" />

    <v-container ref="myDiv">
      <div class="d-flex align-items justify-center align-center mb-3">
        <div class="d-flex justify-space-between">
          <div class="mt-1 mr-6" style="width: 140px">
            <v-select
              filled
              dense
              v-model="selectedVal"
              :items="valItems"
              item-text="text"
              item-value="value"
              hide-details
              single-line
            />
          </div>

          <div class="mt-1 mr-5" style="width: 100px">
            <v-select
              filled
              dense
              v-model="interval"
              :items="intervalItems"
              item-text="text"
              item-value="value"
              @input="intervalSelected"
              hide-details
              single-line
            />
          </div>
        </div>
      </div>
      <v-card class="mb-8" v-if="selectedVal == valItems[0]">
        <MultigraphDataEntries :valuesSelected="valuesSelected" :param="'temp'" :cols="cols" @removeItem="removeItem" />
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[1]">
        <MultigraphDataEntries
          :valuesSelected="valuesSelected"
          :param="'relHumidity'"
          :cols="cols"
          @removeItem="removeItem"
        />
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[2]">
        <MultigraphDataEntries
          :valuesSelected="valuesSelected"
          :param="'windAvg'"
          :cols="cols"
          @removeItem="removeItem"
        />
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[3]">
        <MultigraphDataEntries
          :valuesSelected="valuesSelected"
          :param="'visibility'"
          :cols="cols"
          @removeItem="removeItem"
        />
      </v-card>
      <v-card class="mb-8" v-if="selectedVal == valItems[4]">
        <MultigraphDataEntries
          :valuesSelected="valuesSelected"
          :param="'precip'"
          :cols="cols"
          @removeItem="removeItem"
        />
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/traffic';
import { mapState, mapActions } from 'vuex';
import SelectionPanel from '@/components/modules/traffic/common/SelectionPanel';
import MapMultigraphSelect from '@/components/modules/traffic/map/MapMultigraphSelect';
import MultigraphDataEntries from './MultigraphDataEntries.vue';
import TitleBar from '@/components/modules/traffic/multigraph/TitleBar';

export default {
  components: {
    SelectionPanel,
    MapMultigraphSelect,
    MultigraphDataEntries,
    TitleBar
  },
  data: () => ({
    startDelay: true,
    valuesSelected: [],
    defaultIcons: [
      {
        path: 0,
        scale: 10.0,
        fillColor: '#05FF00',
        fillOpacity: 0.8,
        strokeWeight: 0.4
      },
      {
        path: 0,
        scale: 10.0,
        fillColor: '#FF7F00',
        fillOpacity: 0.8,
        strokeWeight: 0.4
      }
    ],

    selectedVal: 'Temperature',
    valItems: ['Temperature', 'Humidity', 'Wind Speed', 'Visibility', 'Precipitation'],
    cols: 12,
    loading: false,
    height: 420,
    interval: 300000,
    title: 'Weather Data',
    intervalItems: [
      { text: '1 Hour', value: 3600000 },
      { text: '30 min', value: 1800000 },
      { text: '15 min', value: 900000 },
      { text: '5 min', value: 300000 }
    ],
    columnItems: [
      { text: '1 column', value: 12 },
      { text: '2 column', value: 6 },
      { text: '3 column', value: 4 }
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
    weather: {}
  }),

  computed: {
    markerIcons() {
      return this.icons ? this.icons : this.defaultIcons;
    },

    markers() {
      if (!this.startDelay) {
        return this.weatherStations;
      } else {
        return [];
      }
    },

    items() {
      return this.weatherStations.map(location => {
        return { id: location.id, name: location.name.trimRight(), data: null };
      });
    },

    multigraphModeSelect: {
      get() {
        return this.$store.state.traffic.multigraphModeSelect;
      },
      set(val) {
        this.$store.commit('traffic/SET_MULTIGRAPH_MODE_SELECT', val);
      }
    },

    ...mapState(['currentDate']),
    ...mapState('traffic', ['weatherStations', 'multigraphModes'])
  },

  mounted() {
    setTimeout(() => {
      this.startDelay = false;
    }, 100);
    if (this.weatherStations.length === 0) {
      this.fetchWeatherStations();
    }
  },

  watch: {
    cols() {
      this.$bus.$emit('CHART_RELOAD');
    },
    currentDate() {
      this.refreshData();
    }
  },

  created() {
    this.$store.commit('traffic/SHOW_PANEL', true);
  },

  methods: {
    clear() {
      this.valuesSelected = [];
      this.$bus.$emit('NAME_SELECTED', []);
    },

    removeItem(item) {
      this.valuesSelected = this.valuesSelected.filter(x => x.id && x.id != item.id);
      this.$bus.$emit('NAME_SELECTED', this.valuesSelected);
    },

    triggerFirstMarkerClick() {
      this.$refs.mapSelect.triggerFirstMarkerClick();
    },

    getSelectedMarker() {
      return this.$refs.mapSelect.getSelectedMarker();
    },

    valueSelectHandler(value) {
      if (value && value.length > 0 && value[value.length - 1]) {
        let marker = this.markers.find(m => m.id === value[value.length - 1].id);
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
          this.valuesSelected = this.valuesSelected.filter(x => x.name && x.name != marker.name);
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
            precip: this.formPrecipData(dataList, name)
          };
        }
        this.valuesSelected.forEach(val => {
          if (val.id == id) {
            val.data = data;
          }
        });
      } catch (error) {
        this.valuesSelected.forEach(val => {
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

    ...mapActions('traffic', ['fetchWeatherStations'])
  }
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

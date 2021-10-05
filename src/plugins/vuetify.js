import Vue from 'vue';
import Vuetify from 'vuetify/lib';
import * as VueGoogleMaps from 'vue2-google-maps';

// Highcharts
import HighchartsVue from 'highcharts-vue';
import Highcharts from 'highcharts';
import highchartsMore from 'highcharts/highcharts-more';
import heatmap from 'highcharts/modules/heatmap';
import exportingInit from 'highcharts/modules/exporting';
import offlineExporting from 'highcharts/modules/offline-exporting';

heatmap(Highcharts);
offlineExporting(Highcharts);
exportingInit(Highcharts);
highchartsMore(Highcharts);

// Global highcharts options
Highcharts.setOptions({
  // This is for all plots, change Date axis to local timezone
  global: {
    useUTC: false
  },
  plotOptions: {
    series: {
      animation: false
    }
  }
});
Vue.use(HighchartsVue);

Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyChboz4elIfBerLWDL4zwWLOAFnJgmnYrs',
    libraries: 'places' // This is required if you use the Autocomplete plugin
  },
  installComponents: true
});
Vue.use(Vuetify);

export default new Vuetify({});

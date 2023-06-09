import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import vuetify from './plugins/vuetify';
import axios from 'axios';
import VueNativeSock from 'vue-native-websocket';
import { trafficWebsocketUrl } from './utils/websocketUrl';
import { registerServiceWorker } from './utils/Notification';

import HighchartsVue from 'highcharts-vue';
/* import 'material-design-icons-iconfont/dist/material-design-icons.css' */
Vue.use(HighchartsVue);

axios.defaults.baseURL = process.env.VUE_APP_VISION_TRAFFIC_API_URL;

Vue.prototype.$http = axios;
Vue.config.productionTip = false;

// WebSocket
// https://stackoverflow.com/questions/57451148/receive-websockets-data-from-vuex-and-vue-native-websocket-plugin
Vue.use(VueNativeSock, trafficWebsocketUrl, {
  store: store,
  format: 'json',
  connectManually: true,
  reconnection: true, // (Boolean) whether to reconnect automatically (false)
  reconnectionAttempts: 5, // (Number) number of reconnection attempts before giving up (Infinity),
  reconnectionDelay: 3000 // (Number) how long to initially wait before attempting a new (1000)
});

// Add an event bus to the root
Vue.prototype.$bus = new Vue();

// Register service worker
registerServiceWorker((subscription, status) => {
  store.commit('SET_NOTIFICATION_SUB', subscription);
  store.commit('SET_NOTIFICATION_STATUS', status);
});

new Vue({
  router,
  store,
  vuetify,
  created: () => {
    store.dispatch('bluetooth/loadPreferences');
    store.dispatch('bluetooth/loadMapLayers');
    store.dispatch('auth/startRefreshTokenTimer');
    store.dispatch('traffic/loadMapLayers');
    store.dispatch('traffic/loadIncidentSettings');
    store.dispatch('loadSettings');
    store.dispatch('loadDarkMode');
  },
  render: h => h(App)
}).$mount('#app');

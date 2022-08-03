import Vue from 'vue';
import Utils from '@/utils/Utils';
import Settings from '@/utils/AppSettings';

const state = {
  socket: {
    isConnected: false,
    message: '',
    reconnectError: false,
    reconnectCount: 0
  },

  navigationWidth: '600px',
  settings: null,
  darkMode: null,
  showDrawer: false,
  snackbar: {},
  mapCenter: { lat: 39.14, lng: -75.5 },
  position: { lat: 39.084, lng: -77.1528 },
  currentDate: new Date()
};

const getters = {
  getSetting: state => (app, key) => {
    if (state.settings && app && key) {
      if (state.settings[app]) {
        let settingFilt = state.settings[app].settings.filter(s => s.name && s.name == key);
        if (settingFilt.length > 0) {
          return settingFilt[0].val;
        } else {
          return null;
        }
      }
    } else {
      return null;
    }
  }
};

const mutations = {
  SET_NAV_WIDTH(state, width) {
    state.navigationWidth = width;
  },
  SOCKET_ONOPEN(state, event) {
    Vue.prototype.$socket = event.currentTarget;
    state.socket.isConnected = true;
    console.log('--ws connected--');
  },
  SOCKET_ONCLOSE(state) {
    state.socket.isConnected = false;
    console.log('--ws closed--');
  },
  SOCKET_ONERROR(state) {
    console.error(state);
  },
  SOCKET_ONMESSAGE(state, message) {
    state.socket.message = message;
  },
  SOCKET_RECONNECT(state, count) {
    state.socket.reconnectCount = count;
  },
  SOCKET_RECONNECT_ERROR(state) {
    state.socket.reconnectError = true;
  },

  SET_SETTINGS: (state, settings) => {
    state.settings = settings;
  },

  SET_DARK_MODE: (state, darkMode) => {
    state.darkMode = darkMode;
  },

  SHOW_DRAWER: (state, show) => {
    state.showDrawer = show;
  },

  TOGGLE_SHOW_DRAWER: state => {
    state.showDrawer = !state.showDrawer;
  },

  SET_SNACKBAR(state, snackbar) {
    state.snackbar = snackbar;
  },

  SET_MAP_CENTER(state, center) {
    state.mapCenter = center;
  },

  SET_POSITION(state, pos) {
    state.position = pos;
  },

  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
  }
};

const actions = {
  WSConnect(context, url) {
    Vue.prototype.$connect(url);
  },

  WSDisconnect({ state }) {
    if (state.socket.isConnected) {
      Vue.prototype.$disconnect();
    }
  },

  WSSendMessage({ state }, message) {
    if (state.socket.isConnected) {
      Vue.prototype.$socket.send(message);
    }
  },

  loadSettings({ commit }) {
    const settings = localStorage.getItem('Settings');
    commit('SET_SETTINGS', settings ? JSON.parse(settings) : Settings.getDefault());
  },

  saveSettings({ state, commit }, settings) {
    commit('SET_SETTINGS', settings);
    localStorage.setItem('Settings', JSON.stringify(state.settings));
  },

  loadDarkMode({ commit }) {
    const darkMode = localStorage.getItem('ThemeDarkMode');
    commit('SET_DARK_MODE', darkMode ? JSON.parse(darkMode) : true);
  },

  saveDarkMode({ state, commit }, darkMode) {
    commit('SET_DARK_MODE', darkMode);
    localStorage.setItem('ThemeDarkMode', JSON.stringify(state.darkMode));
  },

  setSystemStatus({ commit }, status) {
    let snackbar = { showing: true, color: 'info', timeout: 4000, text: '' };
    Object.assign(snackbar, status);
    commit('SET_SNACKBAR', snackbar);
  },

  incCurrentDate({ state, commit }, days) {
    const currentDate = state.currentDate;
    if (Utils.isTodayAndBeyond(currentDate) && days > 0) {
      return;
    }

    const result = new Date(currentDate);
    result.setDate(result.getDate() + days);
    commit('SET_CURRENT_DATE', result);
  }
};

export { state, getters, mutations, actions };

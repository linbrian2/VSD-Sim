import TrafficApi from '@/utils/api/traffic';
import HRApi from '@/utils/api/hr';
// import StatusApi from '@/utils/api/status';

import Utils from '@/utils/Utils';

const state = {
  weatherStations: [],
  trafficIncidents: [],
  trafficDevices: [],
  hrData: [],
  hrSummary: [],
  detectors: [],
  segments: [],
  waze: [],

  pref: {
    theme: 'Dark Mode',
    dataUpdate: 5,
    dataUpdateEnabled: true,
    swap: 30,
    swapEnabled: true,
    resizableMap: false,
    layout: '1: Card, 2: Map, 3: Info',
    limitResults: true
  },
  showHighcharts: 1,
  darkMode: null,
  currentDate: new Date(),
  snackbar: {}
};

const getters = {};

const mutations = {
  SET_WEATHER_STATIONS(state, stations) {
    state.weatherStations = stations;
  },
  SET_TRAFFIC_INCIDENTS(state, data) {
    state.trafficIncidents = data;
  },
  SET_TRAFFIC_DEVICES(state, devices) {
    state.trafficDevices = devices;
  },
  SET_HR_DATA(state, data) {
    state.hrData = data;
  },
  SET_HR_SUMMARY(state, summary) {
    state.hrSummary = summary;
  },
  SET_DETECTORS(state, data) {
    state.detectors = data;
  },
  SET_SEGMENTS(state, data) {
    state.segments = data;
  },
  SET_WAZE(state, data) {
    state.waze = data;
  },

  SET_DARK_MODE: (state, darkMode) => {
    state.darkMode = darkMode;
  },
  SET_SNACKBAR(state, snackbar) {
    state.snackbar = snackbar;
  },
  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
  }
};

const actions = {
  async fetchWeatherStations({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchWeatherStations();
      commit('SET_WEATHER_STATIONS', response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  // Traffic Incidents
  async fetchTrafficIncidents({ commit, dispatch }) {
    const severity = 50;
    const duration = 30;
    try {
      const start = new Date().getTime();
      const response = await TrafficApi.fetchIncidentData(start, 1, severity, duration);
      commit('SET_TRAFFIC_INCIDENTS', response.data);
      // console.log(`Traffic Incidents: %o`, response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  // async fetchIncidentData(startTime) {
  //   const { severity, duration } = this.incidentSettings;
  //   try {
  //     const start = startTime.getTime();
  //     const response = await Api.fetchIncidentData(start, 1, severity, duration);
  //     const data = this.getResponseData(response);
  //     if (data) {
  //       this.incidents = data;
  //       this.incidentsByTime = this.composeIncidentHeatMapData(this.incidents);
  //       this.segments = this.createTotalSegments();
  //     } else {
  //       this.incidents = [];
  //       this.incidentsByTime = this.composeIncidentHeatMapData(this.incidents);
  //       this.segments = [];
  //     }
  //     this.incidentItem = null;
  //   } catch (error) {
  //     this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
  //   }
  // },
  // Traffic Detector Issues
  async fetchDevices({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchAnomalyDevices();
      let deviceLocations = response.data.map(obj => ({ ...obj, status: 0 }));
      commit('SET_TRAFFIC_DEVICES', deviceLocations);
      // console.log(`Traffic Devices: %o`, deviceLocations);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  // Signal Performance Issues
  async fetchSignalPerformanceIssues({ commit, dispatch }) {
    try {
      const response = await HRApi.fetchDevices();
      commit('SET_HR_DATA', response.data);
      // console.log(`HR: %o`, response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  async fetchStatusOfDevices({ commit, dispatch }) {
    // console.log('fetchStatusOfDevices');
    try {
      const response = await HRApi.fetchStatusOfDevices();
      commit('SET_HR_SUMMARY', response.data);
      // console.log(`HR Summary: %o`, response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  // Detectors (High Delay)
  async fetchDetectors({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchDevices();
      commit('SET_DETECTORS', response.data);
      // console.log(`Detectors: %o`, response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  // Routes (High Congestion)
  async fetchSegments({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchSegments();
      commit('SET_SEGMENTS', response.data);
      // console.log(`Segments: %o`, response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  // Reported Waze Alerts
  async fetchWaze({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchWazeData();
      commit('SET_WAZE', response.data);
      // console.log(`Waze: %o`, response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
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
    let snackbar = { showing: true, color: 'info', timeout: 2000, text: '' };
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

export default {
  namespaced: true,
  state: state,
  getters: getters,
  mutations: mutations,
  actions: actions
};

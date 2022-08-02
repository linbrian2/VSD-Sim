import TrafficApi from '@/utils/api/traffic';
import HRApi from '@/utils/api/hr';
import StatusApi from '@/utils/api/status';

import Utils from '@/utils/Utils';
import store from '@/store';

const state = {
  showTable: false,
  activeMarker: null,

  weatherStations: null,
  trafficIncidents: null,
  trafficDevices: null,
  signalPerformanceIssues: null,
  hrSummary: null,
  flowAnomData: null,
  detectors: null,
  segments: null,
  waze: null,

  selectedTrafficIncident: null,
  selectedtrafficDevice: null,
  selectedSignalPerformanceIssue: null,
  selectedDetector: null,
  selectedSegment: null,
  selectedWazeAlert: null,

  incidents: [],
  incidentSegmentLinks: null,
  incidentMarkers: null,

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
  SHOW_TABLE(state, show) {
    state.showTable = show;
  },
  SET_ACTIVE_MARKER(state, marker) {
    state.activeMarker = marker;
  },

  SET_WEATHER_STATIONS(state, stations) {
    state.weatherStations = stations;
  },
  SET_TRAFFIC_INCIDENTS(state, data) {
    state.trafficIncidents = data;
    state.incidents = data;
  },
  SET_TRAFFIC_DEVICES(state, devices) {
    state.trafficDevices = devices;
  },
  SET_HR_DATA(state, data) {
    state.signalPerformanceIssues = data;
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
  SET_FLOW_ANOM_DATA(state, data) {
    state.flowAnomData = data;
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
  //! Traffic Incidents
  async fetchTrafficIncidents({ commit, dispatch }) {
    const severity = 50;
    const duration = 30;
    try {
      // TODO: Remove outside of testing
      let useWholeDay = store.getters['getSetting']('dashboard', 'incidentsWholeDay');
      let usePrevDay = store.getters['getSetting']('dashboard', 'usePrevDay');
      let hours = useWholeDay ? 24 : 1;
      const start = usePrevDay ? new Date().getTime() - 24 * 60 * 60 * 1000 : new Date().getTime();
      const response = await TrafficApi.fetchIncidentData(start, 1, severity, duration);
      let sortedData = null;
      if (response.data.data) {
        sortedData = response.data.data
          .filter(x => {
            return new Date().getTime() - x.endTime < hours * 60 * 60 * 1000;
          })
          .sort((a, b) => (a.severity > b.severity ? -1 : b.severity > a.severity ? 1 : 0));
      }
      commit('SET_TRAFFIC_INCIDENTS', sortedData ? sortedData : []);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  //! Traffic Flow Issues
  async fetchTrafficDevices({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchAnomalyDevices();
      let deviceLocations = response.data.map(obj => ({ ...obj, status: 0 }));
      // TODO: Remove outside of testing
      if (store.getters['getSetting']('dashboard', 'addTrafficFlowIssue')) {
        deviceLocations[0].status = 1;
      }
      deviceLocations = deviceLocations.map(obj => ({ ...obj, state: obj.status === 0 ? 'Normal' : 'Anomaly' }));
      let sortedData = deviceLocations.sort((a, b) => (a.status > b.status ? -1 : b.status > a.status ? 1 : 0));
      commit('SET_TRAFFIC_DEVICES', sortedData);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  //! Signal Performance Issues
  async fetchSignalPerformanceIssues({ commit, dispatch }) {
    try {
      const response = await HRApi.fetchDevices();
      commit('SET_HR_DATA', response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  async fetchStatusOfDevices({ commit, dispatch }) {
    try {
      const response = await HRApi.fetchStatusOfDevices();
      let sortedData = response.data
        .map(x => ({ ...x, score: x.AoR[0] + x.AoR[1] }))
        .sort((a, b) => (a.score > b.score ? -1 : b.score > a.score ? 1 : 0));
      commit('SET_HR_SUMMARY', sortedData);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  //! Device Anomalies
  async fetchStatus({ commit, dispatch }) {
    let date = new Date();
    try {
      this.updatedTime = new Date();
      const response = await StatusApi.fetchErrors(date.getTime());
      if (response.data && response.data.data) {
        let data = response.data.data;
        let errCount5m = data.totalErrorCounts.filter(x => x != 0);
        let count = 0;
        for (let i = 0; i < 12; i++) {
          if (i < errCount5m.length) {
            count += errCount5m[errCount5m.length - 1 - i];
          }
        }
        data.sensorErrorCounts = data.sensorErrorCounts
          .map(x => {
            return { ...x, score: x.counts[0] + x.counts[1] * 100 };
          })
          .sort((a, b) => (a.score > b.score ? -1 : b.score > a.score ? 1 : 0));
        commit('SET_FLOW_ANOM_DATA', { ...data, count });
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' });
    }
  },
  //! Congested Routes
  async fetchSegments({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchBluetoothSegments();
      let sortedData = response.data.sort((a, b) =>
        a.travelTime.level > b.travelTime.level ? -1 : b.travelTime.level > a.travelTime.level ? 1 : 0
      );
      commit('SET_SEGMENTS', sortedData);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  //! Waze Alerts
  async fetchWaze({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchWazeData();
      let sortedData = response.data
        .sort((a, b) => (a.confidence > b.confidence ? -1 : b.confidence > a.confidence ? 1 : 0))
        .filter(
          (value, index, self) => index === self.findIndex(t => t.alertTimeTS === value.alertTimeTS && t.description)
        );
      commit('SET_WAZE', sortedData);
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

import Api from '@/utils/api/traffic';

const state = {
  currentBluetoothAnomaly: null,
  currentFlowAnomaly: null,
  currentWeatherCode: null,
  currentRestrictions: [],
  currentAnomalySegments: [],

  showFlowChart: false,
  showPanel: false,
  activeMarker: null,

  devices: [],
  anomalyDevices: [],
  weatherStations: [],

  bluetoothSegments: [],

  mapRegionSelection: -1,
  mapLayersSelection: null,
  currentFlowChartParams: {},

  incidentSettings: {
    severity: 50,
    duration: 30
  },

  notifications: []
};

const mutations = {
  SOCKET_FLOW(state, message) {
    state.currentFlowAnomaly = message.data;
  },
  SOCKET_WEATHER(state, message) {
    state.currentWeatherCode = message.data;
  },
  SOCKET_BLUETOOTH(state, message) {
    state.currentBluetoothAnomaly = message.data;
  },
  SOCKET_ANOMALY(state, message) {
    console.log('anomaly segment received.');
    const { severity, duration } = state.incidentSettings;
    state.currentAnomalySegments = message.data.filter(s => s.severity >= severity && s.duration >= duration);
  },
  SET_CURRENT_BLUETOOTH_ANOMALY_DATA(state, data) {
    state.currentBluetoothAnomaly = data;
  },
  SET_CURRENT_FLOW_ANOMALY_DATA(state, data) {
    state.currentFlowAnomaly = data;
  },
  SET_CURRENT_WEATHER_DATA(state, data) {
    state.currentWeatherCode = data;
  },
  SET_CURRENT_RESTRICTION_DATA(state, data) {
    state.currentRestrictions = data;
  },
  SET_CURRENT_ANOMALY_SEGMENTS(state, data) {
    state.currentAnomalySegments = data;
  },
  SHOW_FLOW_CHART(state, show) {
    state.showFlowChart = show;
  },
  SHOW_PANEL(state, show) {
    state.showPanel = show;
  },
  TOGGLE_SHOW_PANEL(state) {
    state.showPanel = !state.showPanel;
  },
  SET_ACTIVE_MARKER(state, marker) {
    state.activeMarker = marker;
  },
  SET_DEVICES(state, devices) {
    state.devices = devices;
  },
  SET_ANOMALY_DEVICES(state, devices) {
    state.anomalyDevices = devices;
  },
  SET_WEATHER_STATIONS(state, stations) {
    state.weatherStations = stations;
  },
  SET_BLUETOOTH_SEGMENTS(state, segments) {
    state.bluetoothSegments = segments;
  },
  SET_MAP_REGION(state, region) {
    state.mapRegionSelection = region;
  },
  SET_MAP_LAYER(state, layer) {
    state.mapLayersSelection = layer;
  },
  SET_CURRENT_FLOW_CHART_PARAMS(state, params) {
    state.currentFlowChartParams = params;
  },
  SET_INCIDENT_SETTINGS(state, settings) {
    state.incidentSettings = settings;
  },
  UPDATE_NOTIFICATION(state, payload) {
    state.notifications.push(payload);
  },
  CLEAR_NOTIFICATION(state) {
    state.notifications = [];
  },
  SET_VISION_RESULT(state, result) {
    state.visionResult = result;
  }
};

const getters = {
  getNotification(state) {
    return state.currentAnomalySegments.map(segment => ({
      id: segment.id,
      title: `<span ${segment.status === 0 ? 'style="color:green;"' : ''} >${segment.shortName}</span>`,
      status: segment.status,
      icon: 'mdi-alert-octagon',
      startTime: new Date(segment.startTime),
      evidenceCounts: segment.evidenceCounts,
      incidentScore: segment.severity,
      severityColor: segment.severityColor
    }));
  }
};

const actions = {
  async fetchDevices({ state, commit, dispatch }) {
    try {
      const response = await Api.fetchDevices();
      commit('SET_DEVICES', response.data);

      if (state.locations.length > 0) {
        commit('SET_ACTIVE_MARKER', state.locations[0]);
      }
    } catch (error) {
      console.log(error);
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  async fetchAnomalyDevices({ commit, dispatch }) {
    try {
      const response = await Api.fetchAnomalyDevices();
      commit('SET_ANOMALY_DEVICES', response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  async fetchWeatherStations({ state, commit, dispatch }) {
    try {
      const response = await Api.fetchWeatherStations();
      commit('SET_WEATHER_STATIONS', response.data);
      if (state.weatherStations.length > 0) {
        commit('SET_ACTIVE_MARKER', state.weatherStations[0]);
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  async fetchBluetoothSegments({ commit, dispatch }) {
    try {
      const response = await Api.fetchBluetoothSegments();
      commit('SET_BLUETOOTH_SEGMENTS', response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  async fetchInfo({ commit, dispatch }, { id, time }) {
    try {
      const response = await Api.fetchInfo(id, time);
      if (response.data.status === 'OK') {
        commit('SIGNAL_SET', response.data.data);
      } else {
        dispatch('setSystemStatus', { text: response.data.message, color: 'error' }, { root: true });
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  loadMapLayers({ commit }) {
    const layers = localStorage.getItem('mapLayer');
    const mapLayers = layers ? JSON.parse(layers) : [0, 1, 3, 4];
    commit('SET_MAP_LAYER', mapLayers);
  },

  saveMapLayers({ state, commit }, layers) {
    commit('SET_MAP_LAYER', layers);

    // Save to local storage
    localStorage.setItem('mapLayer', JSON.stringify(state.mapLayersSelection));
  },

  loadIncidentSettings({ commit }) {
    const settings = localStorage.getItem('incidentSettings');
    if (settings && settings !== 'undefined') {
      const value = JSON.parse(settings);
      if (value && value.severity && !isNaN(value.severity) && value.duration && !isNaN(value.duration)) {
        commit('SET_INCIDENT_SETTINGS', value);
      }
    }
  },

  saveIncidentSettings({ commit }, value) {
    if (value && value.severity && !isNaN(value.severity) && value.duration && !isNaN(value.duration)) {
      commit('SET_INCIDENT_SETTINGS', value);
      localStorage.setItem('incidentSettings', JSON.stringify(value));
    }
  },

  updateNotification({ commit }, text) {
    const note = {
      title: text,
      color: 'light-green',
      icon: 'mdi-account-circle',
      timeLabel: 'Just now'
    };
    commit('UPDATE_NOTIFICATION', note);
  },

  clearNotificaton({ commit }) {
    commit('CLEAR_NOTIFICATION');
  }
};

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  getters: getters,
  actions: actions
};

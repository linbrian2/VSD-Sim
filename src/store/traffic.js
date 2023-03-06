import Api from '@/utils/api/traffic';
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
import Message from '@/utils/Message.js';

const state = {
  currentTravelTimeAnomaly: null,
  currentFlowAnomaly: null,
  currentWeather: null,
  currentRestrictions: [],
  currentTrafficIncident: [],
  currentWaze: null,
  messages: [],

  showFlowChart: false,
  showPanel: false,
  activeMarker: null,

  devices: [],
  trafficCameras: [],
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

  multigraphModes: ['Traffic Flow Data', 'Travel Time Data', 'Weather Data'],
  multigraphModeSelect: 'Traffic Flow Data',
  singleSelect: 'Single Select',
  predictionModes: ['Freeway', 'Arterial'],
  predictionMode: 'Freeway',
  activeMultigraphMarkers: [],
  mitigation: null,
  simulation: null,
  loading: null,

  segments: null,
  waze: null,
  btDevices: null,
  btSensors: null
};

const mutations = {
  SOCKET_FLOW(state, message) {
    state.currentFlowAnomaly = message.data;

    const type = Constants.DATA_TRAFFIC_FLOW;
    if (Message.isSameKind(state.messages, type)) {
      const lastMessage = state.messages.pop();
      state.messages.push(Message.compose(type, message.data, lastMessage));
    } else {
      state.messages.push(Message.compose(type, message.data));
    }
  },
  SOCKET_WEATHER(state, message) {
    state.currentWeather = message.data;

    const type = Constants.DATA_WEATHER;
    if (Message.isSameKind(state.messages, type)) {
      const lastMessage = state.messages.pop();
      state.messages.push(Message.compose(type, message.data, lastMessage));
    } else {
      state.messages.push(Message.compose(type, message.data));
    }
  },
  SOCKET_BLUETOOTH(state, message) {
    state.currentTravelTimeAnomaly = message.data;

    const type = Constants.DATA_TRAVEL_TIME;
    if (Message.isSameKind(state.messages, type)) {
      const lastMessage = state.messages.pop();
      state.messages.push(Message.compose(type, message.data, lastMessage));
    } else {
      state.messages.push(Message.compose(type, message.data));
    }
  },
  SOCKET_ANOMALY(state, message) {
    state.currentTrafficIncident = message.data;

    const type = Constants.DATA_TRAFFIC_INCIDENT;
    if (Message.isSameKind(state.messages, type)) {
      const lastMessage = state.messages.pop();
      state.messages.push(Message.compose(type, message.data, lastMessage));
    } else {
      state.messages.push(Message.compose(type, message.data));
    }
  },
  SOCKET_RESTRICTION(state, message) {
    state.currentRestrictions = message.data;

    const type = Constants.DATA_RESTRICTION;
    if (Message.isSameKind(state.messages, type)) {
      const lastMessage = state.messages.pop();
      state.messages.push(Message.compose(type, message.data, lastMessage));
    } else {
      state.messages.push(Message.compose(type, message.data));
    }
  },
  SOCKET_WAZE(state, message) {
    state.currentWaze = message.data;

    const type = Constants.DATA_WAZE_ALERTS;
    if (Message.isSameKind(state.messages, type)) {
      const lastMessage = state.messages.pop();
      state.messages.push(Message.compose(type, message.data, lastMessage));
    } else {
      state.messages.push(Message.compose(type, message.data));
    }
  },
  SOCKET_MITIGATION(state, message) {
    state.mitigation = message.data;
    state.messages.push(Message.compose(Constants.DATA_MITIGATION, message.data));
  },
  SOCKET_SIMULATION(state, message) {
    state.simulation = message.data;
    state.messages.push(Message.compose(Constants.DATA_SIMULATION, message.data));
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
  ADD_ACTIVE_MULTIGRAPH_MARKER(state, marker) {
    state.activeMultigraphMarkers = state.activeMultigraphMarkers.filter(x => x != marker);
    state.activeMultigraphMarkers.push(marker);
  },
  REMOVE_ACTIVE_MULTIGRAPH_MARKER(state, marker) {
    state.activeMultigraphMarkers = state.activeMultigraphMarkers.filter(x => x != marker);
  },
  SET_DEVICES(state, devices) {
    state.devices = devices;
  },
  SET_ANOMALY_DEVICES(state, devices) {
    state.anomalyDevices = devices;
  },
  SET_TRAFFIC_CAMERAS(state, cameras) {
    state.trafficCameras = cameras;
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
  SET_VISION_RESULT(state, result) {
    state.visionResult = result;
  },
  SET_MULTIGRAPH_MODE_SELECT(state, mode) {
    state.multigraphModeSelect = mode;
  },
  SET_SINGLE_SELECT(state, val) {
    state.singleSelect = val;
  },
  SET_PREDICTION_MODE(state, mode) {
    state.predictionMode = mode;
  },
  SET_SEGMENTS(state, data) {
    state.segments = data;
  },
  SET_WAZE(state, data) {
    state.waze = data;
  },
  SET_BT_DEVICES(state, data) {
    state.btDevices = data;
  },
  SET_BT_SENSORS(state, data) {
    state.btSensors = data;
  },
  SET_LOADING(state, loading) {
    state.loading = loading;
  },
  CLEAR_MESSAGE(state) {
    state.messages = [];
  },
  ADD_MESSAGE_TO_LIST(state, message) {
    // Before adding, remove old messages
    state.messages = state.messages.filter(message => Utils.laterThan(message.time, 24 * 3600));

    // Add new message to the array
    state.messages.push(message);
  }
};

const getters = {
  getMessages(state) {
    // Get sort by time and return top 20
    const messages = state.messages;
    messages.sort((a, b) => b.time - a.time);
    return messages.slice(0, 100);
  }
};

const actions = {
  //! Congested Routes
  async fetchSegments({ commit, dispatch }, date) {
    try {
      const response = await Api.fetchSegmentsFull(date);
      // console.log('Segments: %o', response.data);
      commit('SET_SEGMENTS', response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  //! Waze Alerts
  async fetchWaze({ commit, dispatch }, date) {
    try {
      const response = await Api.fetchWazeFull(date);
      // console.log('Waze: %o', response.data);
      commit('SET_WAZE', response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  //! Bluetooth Devices
  async fetchBTDevices({ commit, dispatch }, date) {
    try {
      commit('SET_LOADING', true);
      const response = await Api.fetchDevicesFull(date);
      // console.log('Devices: %o', response.data);
      commit('SET_BT_DEVICES', response.data);
      commit('SET_LOADING', false);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
      commit('SET_LOADING', false);
    }
  },
  // ! Bluetooth Sensors
  async fetchBTSensors({ commit, dispatch }) {
    try {
      const response = await Api.fetchSensors();
      // console.log('Sensors: %o', response.data);
      commit('SET_BT_SENSORS', response.data);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

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

  async fetchTrafficCameras({ commit, dispatch }) {
    try {
      const response = await Api.fetchTrafficCameras();
      commit('SET_TRAFFIC_CAMERAS', response.data);
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
  },

  clearMessageQueue({ commit }) {
    commit('CLEAR_MESSAGE');
  }
};

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  getters: getters,
  actions: actions
};

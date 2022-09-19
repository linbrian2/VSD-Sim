const state = {
  playbackToggle: false,
  map: null,
  segmentPolylines: null,
  sensorMarkers: null,
  deviceMarkers: null,
  wazeMarkers: null,
  wazeClusters: null,
  dialog: -1,
  ttDialog: false,
  apiData: {
    segments: null,
    routes: null,
    waze: null,
    sensors: null,
    devices: null,
    segmentsFull: null,
    wazeFull: null,
    devicesFull: null
  },
  apiLoading: {
    segmentsFull: false,
    wazeFull: false,
    devicesFull: false
  },
  selectedRoutes: [],
  segGraph: [],
  modes: {
    addFromMap: false,
    historical: false
  },
  subtoggles: ['Hazard', 'Traffic Jam', 'Road Closed', 'Accident'],
  bpInfo: null,
  selectedDevice: null,
  mapLayerSelection: null,
  wazeLayerSelection: null,
  deviceLayerSelection: null,
  selectedSeg: {
    data: null,
    curr: null,
    histWInc: null,
    histWoInc: null
  },
  prefs: null,
  playState: 'stop',
  playbackSpeed: 10,
  currentProgress: 0,
  timePickerMenu: false,
  autoUpdate: true,
  timeSinceUpdate: 0,
  multigraphSegs: null,
  showFullDayAvail: true
};

const getters = {
  fetchDone: state => {
    return Object.values(state.apiData).filter(x => !x).length == 0;
  }
};

const mutations = {
  SET_API_DATA: (state, payload) => {
    if (payload.prop == 'allFull') {
      state.apiData.segmentsFull = payload.data;
      state.apiData.wazeFull = payload.data;
      state.apiData.devicesFull = payload.data;
    } else if (payload.prop == 'allCurr') {
      state.apiData.segments = payload.data;
      state.apiData.waze = payload.data;
      state.apiData.devices = payload.data;
    } else {
      state.apiData[payload.prop] = payload.data;
    }
  },
  SET_API_LOADING: (state, payload) => {
    if (payload.prop == 'all') {
      state.apiLoading.segmentsFull = payload.data;
      state.apiLoading.wazeFull = payload.data;
      state.apiLoading.devicesFull = payload.data;
    } else {
      state.apiLoading[payload.prop] = payload.data;
    }
  },
  SET_SEG_GRAPH: (state, val) => (state.segGraph = val),
  SET_MODE: (state, payload) => (state.modes[payload.key] = payload.val),
  SET_MAP: (state, map) => (state.map = map),
  SET_AUTO_UPDATE: (state, toggle) => (state.autoUpdate = toggle),
  SET_FULL_DAY_AVAIL: (state, toggle) => (state.showFullDayAvail = toggle),
  SET_SELECTED_SEG: (state, payload) => (state.selectedSeg[payload.prop] = payload.data),
  SET_TT_DIALOG: (state, dialog) => (state.ttDialog = dialog),
  SET_DIALOG: (state, dialog) => (state.dialog = dialog),
  SET_PLAYBACK_TOGGLE: (state, toggle) => (state.playbackToggle = toggle),
  SET_TIME_SINCE_UPDATE: (state, time) => (state.timeSinceUpdate = time),
  SET_TIMEPICKER_MENU: (state, menu) => (state.timePickerMenu = menu),
  SET_SELECTED_ROUTES: (state, routes) => (state.selectedRoutes = routes),
  SET_SELECTED_PAGE: (state, page) => (state.selectedPage = page),
  SET_SELECTED_DEVICE: (state, device) => (state.selectedDevice = device),
  SET_MAP_LAYER: (state, layer) => (state.mapLayerSelection = layer),
  SET_WAZE_LAYER: (state, layer) => (state.wazeLayerSelection = layer),
  SET_DEVICE_LAYER: (state, layer) => (state.deviceLayerSelection = layer),
  SET_PREFERENCES: (state, prefs) => (state.prefs = prefs),
  SET_PLAYBACK_SPEED: (state, speed) => (state.playbackSpeed = speed),
  SET_PLAY_STATE: (state, play) => (state.playState = play),
  SET_CURRENT_PROGRESS: (state, progress) => (state.currentProgress = progress)
};

const actions = {
  loadMapLayers({ commit }) {
    const layers = localStorage.getItem('mapLayer');
    const wazeLayers = localStorage.getItem('wazeLayer');
    const deviceLayers = localStorage.getItem('deviceLayer');
    const mapLayers = layers ? JSON.parse(layers) : [0, 1, 3];
    const mapWazeLayers = wazeLayers ? JSON.parse(wazeLayers) : [1, 2, 3];
    const mapDeviceLayers = deviceLayers ? JSON.parse(deviceLayers) : [1, 2];
    commit('SET_WAZE_LAYER', mapWazeLayers);
    commit('SET_DEVICE_LAYER', mapDeviceLayers);
    commit('SET_MAP_LAYER', mapLayers);
  },
  loadPreferences({ commit }) {
    const prefs = localStorage.getItem('preferences');
    const prefsJSON = prefs
      ? JSON.parse(prefs)
      : {
          darkMode: true,
          map: { center: { lat: 39.14, lng: -75.5 }, zoom: 9 },
          multiGraph: { graphHeight: 250, showButtons: true, colCountIdx: 0 }
        };
    commit('SET_PREFERENCES', prefsJSON);
  },
  saveMapLayers({ state, commit }, layers) {
    // console.log('Map:\n%o\nWaze:\n%o\nDevice:\n%o', layers.map, layers.waze, layers.device);
    commit('SET_MAP_LAYER', layers.map);
    localStorage.setItem('mapLayer', JSON.stringify(state.mapLayerSelection));
    commit('SET_WAZE_LAYER', layers.waze);
    localStorage.setItem('wazeLayer', JSON.stringify(state.wazeLayerSelection));
    commit('SET_DEVICE_LAYER', layers.device);
    localStorage.setItem('deviceLayer', JSON.stringify(state.deviceLayerSelection));
  }
};

export default {
  namespaced: true,
  state: state,
  getters: getters,
  mutations: mutations,
  actions: actions
};

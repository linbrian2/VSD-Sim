const state = {
  appTitle: 'Bluetooth and Waze Data',
  appMenuItems: [
    { title: 'Traffic Flow Data', url: '/flow' },
    { title: 'High Resolution Data', url: '/hr' },
    { title: 'Machine Vision on Traffic Cameras', url: '/vision' },
    { title: 'Bluetooth and Waze Data', url: '/bluetooth' },
    { title: 'Health Monitoring', url: '/status' }
  ],
  map: null,
  selectedDatetime: null,
  segmentPolylines: null,
  sensorMarkers: null,
  deviceMarkers: null,
  wazeMarkers: null,
  wazeClusters: null,
  dialog: {
    tt: false,
    bp: false,
    congestion: false,
    btSensors: false,
    devices: false,
    wazeAlerts: false
  },
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
  selectedRoutes: [],
  notification: {
    show: false,
    text: 'Default message.',
    timeout: 2000,
    color: 'info'
  },
  segGraph: [],
  selectedPage: 0,
  modes: {
    addFromMap: false,
    historical: false,
    trafficRouting: false
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
  currentProgress: 0
};

const mutations = {
  SET_SELECTED_PAGE: (state, page) => (state.selectedPage = page),
  SET_SELECTED_DEVICE: (state, device) => (state.selectedDevice = device),
  SET_NOTIFICATION: (state, notification) => (state.notification = notification),
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
    const mapLayers = layers ? JSON.parse(layers) : [0, 3];
    const mapWazeLayers = wazeLayers ? JSON.parse(wazeLayers) : [1, 2, 3];
    const mapDeviceLayers = deviceLayers ? JSON.parse(deviceLayers) : [0, 1, 2];
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
    console.log('Map:\n%o\nWaze:\n%o\nDevice:\n%o', layers.map, layers.waze, layers.device);
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
  mutations: mutations,
  actions: actions
};

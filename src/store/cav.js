const state = {
  currentGPS: null,
  currentEvent: null,
  currentCarPos: null,
  showPanel: false,
  locations: [],

  playState: 'stop',
  progressLoading: false,

  currentTrip: {},
  currentState: {},
  currentProgress: 0,
  playbackSpeed: 10,

  currentUpdates: [],
  liveCars: []
};

const mutations = {
  SOCKET_EVENT(state, message) {
    console.log('socket_event', message);
    state.currentEvent = message.data;
  },

  SOCKET_UPDATES(state, message) {
    console.log('socket_updates', message);
    state.currentUpdates = message.data;
  },

  SET_GPS_POSITION(state, pos) {
    state.currentGPS = pos;
  },
  SET_CAR_POSITION(state, pos) {
    state.currentCarPos = pos;
  },
  SET_EVENT(state, event) {
    state.currentEvent = event;
  },
  SHOW_PANEL(state, show) {
    state.showPanel = show;
  },
  TOGGLE_SHOW_PANEL(state) {
    state.showPanel = !state.showPanel;
  },
  SET_LOCATIONS(state, locations) {
    state.locations = locations;
  },
  SET_PROGRESS_LOADING(state, loading) {
    state.progressLoading = loading;
  },
  SET_PLAY_STATE(state, play) {
    state.playState = play;
  },
  SET_CURRENT_TRIP(state, trip) {
    state.currentTrip = trip;
  },
  SET_CURRENT_STATE(state, value) {
    state.currentState = value;
  },
  SET_CURRENT_PROGRESS(state, progress) {
    state.currentProgress = progress;
  },
  SET_PLAYBACK_SPEED(state, speed) {
    state.playbackSpeed = speed;
  },
  SET_CURRENT_UPDATES(state, updates) {
    state.currentUpdates = updates;
  },
  SET_LIVE_CARS(state, cars) {
    state.liveCars = cars;
  },
  UPDATE_LIVE_CAR(state, { vm, index, car }) {
    vm.$set(state.liveCars, index, car);
  }
};

const actions = {};

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  actions: actions
};

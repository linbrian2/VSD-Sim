const state = {
  socket: {
    isConnected: false,
    message: '',
    reconnectError: false
  },
  currentGPS: null,
  //
  snackbar: {},
  position: {
    lat: 39.084,
    lng: -77.1528
  },
  currentEvent: null,
  currentCarPos: null,
  showPanel: false,
  locations: [],
  currentAction: 'status',

  playState: 'stop',
  progressLoading: false,
  currentDate: new Date(),

  currentTrip: {},
  currentState: {},
  currentProgress: 0,
  playbackSpeed: 10,

  currentUpdates: [],
  liveCars: [],
}

const mutations = {
  SOCKET_ONOPEN(state, event) {
    if (!state.socket.isConnected) {
      Vue.prototype.$socket = event.currentTarget;
      state.socket.isConnected = true;
      console.log('WebSocket connected successfully.');
    }
  },
  SOCKET_ONCLOSE(state) {
    state.socket.isConnected = false;
    Vue.prototype.$socket = null;
    console.log('WebSocket closed successfully.');
  },
  SOCKET_ONERROR(state, event) {
    console.error(state, event);
  },
  // default handler called for all methods
  SOCKET_ONMESSAGE(state, message) {
    state.socket.message = message;
    try {
      if (message.type && message.data) {
        const json = message.data;
        switch (message.type) {
          case 0:
            state.currentEvent = json;
            break;
          case 1:
            state.currentUpdates = json;
            break;
        }
      }
      // eslint-disable-next-line no-empty
    } catch (e) { }
  },
  SOCKET_RECONNECT(state, count) {
    console.info(state, count);
  },
  SOCKET_RECONNECT_ERROR(state) {
    state.socket.reconnectError = true;
  },
  SET_GPS_POSITION(state, pos) {
    state.currentGPS = pos;
  },
  //
  SET_SNACK_BAR(state, snackbar) {
    state.snackbar = snackbar;
  },
  SET_POSITION(state, pos) {
    state.position = pos;
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
  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
  },
  SET_CURRENT_ACTION(state, action) {
    state.currentAction = action;
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
  },
}

const actions = {
  WSSendMessage({ state }, message) {
    if (state.socket.isConnected) {
      Vue.prototype.$socket.send(message);
    }
  },
  WSConnect(context, { vm, url }) {
    vm.$connect(url);
  },
  WSDisconnect(context, { vm }) {
    vm.$disconnect();
  },
  //
  setSystemStatus({ commit }, status) {
    let snackbar = { showing: true, color: 'info', timeout: 5000, text: '' };
    Object.assign(snackbar, status);
    commit('SET_SNACK_BAR', snackbar);
  },
}


export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  actions: actions,
}
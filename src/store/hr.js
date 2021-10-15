import Api from '@/utils/api/hr';

const state = {
  showPanel: true,
  showSelectionDlg: false,
  activeMarker: null,
  timing: null,
  phaseTiming: null,
  locations: [],
  currentSignal: null,
  multiSignals: [],
  mapProjection: null,
  currentSignalTime: null
};

const mutations = {
  SHOW_PANEL(state, show) {
    state.showPanel = show;
  },
  TOGGLE_SHOW_PANEL(state) {
    state.showPanel = !state.showPanel;
  },
  SHOW_SELECTION_DLG(state, show) {
    state.showSelectionDlg = show;
  },
  SET_ACTIVE_MARKER(state, marker) {
    state.activeMarker = marker;
  },
  SET_TIMING(state, timing) {
    state.timing = timing;
  },
  SET_PHASE_TIMING(state, phaseTiming) {
    state.phaseTiming = phaseTiming;
  },
  LOCATIONS_SET(state, locations) {
    state.locations = locations;
  },
  SIGNAL_SET(state, signal) {
    state.currentSignal = signal;
  },
  SET_MULTI_SIGNALS(state, signals) {
    state.multiSignals = signals;
  },
  SET_MAP_PROJECTION(state, proj) {
    state.mapProjection = proj;
  },
  SET_CURRENT_SIGNAL_TIME(state, time) {
    state.currentSignalTime = time;
  }
};

const actions = {
  async fetchLocations({ state, commit, dispatch }) {
    try {
      const response = await Api.fetchDevices();
      commit('LOCATIONS_SET', response.data);

      if (state.locations.length > 0) {
        commit('SET_ACTIVE_MARKER', state.locations[0]);
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  async fetchPlans({ commit, dispatch }, { id, time }) {
    try {
      const response = await Api.fetchPlans(id, time);
      if (response.data.status === 'OK') {
        commit('SIGNAL_SET', response.data.data);
      } else {
        dispatch('setSystemStatus', { text: response.data.message, color: 'error' }, { root: true });
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  async fetchTiming({ commit, dispatch }, { id, start, duration }) {
    try {
      // Now we await for both results, whose async processes have already been started
      const [timing, phaseTiming] = await Promise.all([
        Api.fetchTiming(id, start, duration),
        Api.fetchPhaseTiming(id, start, duration)
      ]);

      if (timing.data.status === 'OK') {
        commit('SET_TIMING', timing.data.data);
      } else {
        dispatch('setSystemStatus', { text: timing.data.message, color: 'error' }, { root: true });
      }

      if (phaseTiming.data.status === 'OK') {
        commit('SET_PHASE_TIMING', phaseTiming.data.data);
      } else {
        dispatch('setSystemStatus', { text: phaseTiming.data.message, color: 'error' }, { root: true });
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  }
};

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  actions: actions
};

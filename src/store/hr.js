import Utils from '@/utils/Utils';
import Api from '@/utils/api/hr';

const state = {
  position: {
    lat: 39.084,
    lng: -77.1528
  },
  showPanel: true,
  showSelectionDlg: false,
  activeMarker: null,
  timing: null,
  phaseTiming: null,
  locations: [],
  currentAction: 'dashboard',
  currentSignal: null,
  currentDate: Utils.yesterday(),
  multiSignals: [],
  mapProjection: null,
  currentSignalTime: null
};

const mutations = {
  POSITION_SET(state, pos) {
    state.position = pos;
  },
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
  SET_CURRENT_ACTION(state, action) {
    state.currentAction = action;
  },
  SIGNAL_SET(state, signal) {
    state.currentSignal = signal;
  },
  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
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
  incCurrentDate({ state, commit }, days) {
    const currentDate = state.currentDate;
    if (Utils.isTodayAndBeyond(currentDate) && days > 0) {
      return;
    }

    const result = new Date(currentDate);
    result.setDate(result.getDate() + days);
    commit('SET_CURRENT_DATE', result);
  },

  async fetchLocations({ state, commit, dispatch }) {
    try {
      const response = await Api.fetchDevices();
      commit('LOCATIONS_SET', response.data);

      if (state.locations.length > 0) {
        commit('SET_ACTIVE_MARKER', state.locations[0]);
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, timeout: 0, color: 'error' });
    }
  },

  async fetchPlans({ commit, dispatch }, { id, time }) {
    try {
      const response = await Api.fetchPlans(id, time);
      if (response.data.status === 'OK') {
        commit('SIGNAL_SET', response.data.data);
      } else {
        dispatch('setSystemStatus', { text: response.data.message, timeout: 0, color: 'error' });
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, timeout: 0, color: 'error' });
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
        this.setSystemStatus({ commit }, status);
        dispatch('setSystemStatus', { text: timing.data.message, color: 'error' });
      }

      if (phaseTiming.data.status === 'OK') {
        commit('SET_PHASE_TIMING', phaseTiming.data.data);
      } else {
        dispatch('setSystemStatus', { text: phaseTiming.data.message, color: 'error' });
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' });
    }
  }
};

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  actions: actions
};

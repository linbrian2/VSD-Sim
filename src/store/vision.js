import Api from '@/utils/api/vision';
import Utils from '@/utils/Utils';

const state = {
  snackbar: {},
  showPanel: true,
  position: {
    lat: 39.084,
    lng: -77.1528
  },
  activeMarker: null,
  locations: [],
  /* visionResult: [], */
  currentAction: 'dashboard',
  currentDate: new Date(),
  currentTime: '09:00',
  /* Vehicle Re-ID */
  imageAPI: 'http://10.51.4.9:34556/images',
  showReIDVideos: true,
  snippetHistory: [],
  snippetInfo: null,
  dialogMetadata: false,
}

const mutations = {
  SHOW_PANEL(state, show) {
    state.showPanel = show;
  },
  TOGGLE_SHOW_PANEL(state) {
    state.showPanel = !state.showPanel;
  },
  SET_SNACKBAR(state, snackbar) {
    state.snackbar = snackbar;
  },
  POSITION_SET(state, pos) {
    state.position = pos;
  },
  SET_ACTIVE_MARKER(state, marker) {
    state.activeMarker = marker;
  },
  SET_LOCATIONS(state, locations) {
    state.locations = locations;
  },
  /* SET_VISION_RESULT(state, result) {
    state.visionResult = result;
  }, */
  SET_CURRENT_ACTION(state, action) {
    state.currentAction = action;
  },
  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
  },
  SET_CURRENT_TIME(state, time) {
    state.currentTime = time;
  },
}

const actions = {
  setSystemStatus({ commit }, status) {
    let snackbar = { showing: true, color: 'info', timeout: 2000, text: '' };
    Object.assign(snackbar, status);
    commit('SET_SNACKBAR', snackbar);
  },
  async fetchLocations({ state, commit, dispatch }) {
    try {
      const response = await Api.fetchDevices();
      commit('SET_LOCATIONS', response.data);

      if (state.locations.length > 0) {
        commit('SET_ACTIVE_MARKER', state.locations[0]);
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, timeout: 0, color: 'error' });
    }
  },
  /* async fetchResult({ commit, dispatch }, { id, time }) {
    try {
      const response = await Api.fetchVisionResult(id, time);
      if (response.data.status === 'OK') {
        commit('SET_VISION_RESULT', response.data.data);
      } else {
        dispatch('setSystemStatus', { text: response.data.message, timeout: 4000, color: 'info' });
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, timeout: 4000, color: 'error' });
    }
  }, */
  /* async fetchInfo({ commit, dispatch }, { id, time }) {
    try {
      const response = await Api.fetchInfo(id, time);
      if (response.data.status === 'OK') {
        commit('SIGNAL_SET', response.data.data);
      } else {
        dispatch('vision/setSystemStatus', { text: response.data.message, timeout: 0, color: 'error' });
      }
    } catch (error) {
      dispatch('vision/setSystemStatus', { text: error, timeout: 0, color: 'error' });
    }
  }, */
  incCurrentDate({ state, commit }, days) {
    const currentDate = state.currentDate;
    if (Utils.isTodayAndBeyond(currentDate) && days > 0) {
      return;
    }
    const result = new Date(currentDate);
    result.setDate(result.getDate() + days);
    commit('SET_CURRENT_DATE', result);
  },
}

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  actions: actions,
}
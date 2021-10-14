import Api from '@/utils/api/vision';

const state = {
  showPanel: true,
  activeMarker: null,
  locations: [],
  currentTime: '09:00',
  imageAPI: 'http://10.51.4.9:34556/images',
  showReIDVideos: true,
  snippetHistory: [],
  snippetInfo: null,
  dialogMetadata: false
};

const mutations = {
  SHOW_PANEL(state, show) {
    state.showPanel = show;
  },
  TOGGLE_SHOW_PANEL(state) {
    state.showPanel = !state.showPanel;
  },
  SET_ACTIVE_MARKER(state, marker) {
    state.activeMarker = marker;
  },
  SET_LOCATIONS(state, locations) {
    state.locations = locations;
  }
};

const actions = {
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
  }
};

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  actions: actions
};

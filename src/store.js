import Vue from 'vue';
import Vuex from 'vuex';

import auth from '@/store/auth';
import cav from '@/store/cav';
import hr from '@/store/hr';
import bluetooth from '@/store/bluetooth';
import traffic from '@/store/traffic';
import vision from '@/store/vision';
import status from '@/store/status';

Vue.use(Vuex);

const modules = {
  auth,
  bluetooth,
  cav,
  hr,
  status,
  traffic,
  vision
};

const state = {
  darkMode: null,
  showDrawer: false,
  snackbar: {}
};

const mutations = {
  SET_DARK_MODE: (state, darkMode) => {
    state.darkMode = darkMode;
  },

  SHOW_DRAWER: (state, show) => {
    state.showDrawer = show;
  },

  TOGGLE_SHOW_DRAWER: state => {
    state.showDrawer = !state.showDrawer;
  },

  SET_SNACKBAR(state, snackbar) {
    state.snackbar = snackbar;
  }
};

const actions = {
  loadDarkMode({ commit }) {
    const darkMode = localStorage.getItem('darkMode');
    commit('SET_DARK_MODE', darkMode ? JSON.parse(darkMode) : true);
  },

  saveDarkMode({ state, commit }, darkMode) {
    commit('SET_DARK_MODE', darkMode);
    localStorage.setItem('darkMode', JSON.stringify(state.darkMode));
  },

  setSystemStatus({ commit }, status) {
    let snackbar = { showing: true, color: 'info', timeout: 2000, text: '' };
    Object.assign(snackbar, status);
    commit('SET_SNACKBAR', snackbar);
  }
};

export default new Vuex.Store({
  modules,
  state,
  mutations,
  actions
});

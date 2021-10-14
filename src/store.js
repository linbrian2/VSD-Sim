import Vue from 'vue';
import Vuex from 'vuex';

import Utils from '@/utils/Utils';
import auth from '@/store/auth';
import cav from '@/store/cav';
import hr from '@/store/hr';
import bluetooth from '@/store/bluetooth';
import traffic from '@/store/traffic';
import vision from '@/store/vision';

Vue.use(Vuex);

const modules = {
  auth,
  bluetooth,
  cav,
  hr,
  traffic,
  vision
};

const state = {
  darkMode: null,
  showDrawer: false,
  snackbar: {},
  position: { lat: 39.084, lng: -77.1528 },
  currentDate: new Date()
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
  },

  SET_POSITION(state, pos) {
    state.position = pos;
  },

  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
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
  },

  incCurrentDate({ state, commit }, days) {
    const currentDate = state.currentDate;
    if (Utils.isTodayAndBeyond(currentDate) && days > 0) {
      return;
    }

    const result = new Date(currentDate);
    result.setDate(result.getDate() + days);
    commit('SET_CURRENT_DATE', result);
  }
};

export default new Vuex.Store({
  modules,
  state,
  mutations,
  actions
});

import Vue from 'vue';
import Vuex from 'vuex';

import moduleBluetooth from '@/store/bluetooth'
import moduleTraffic from '@/store/traffic'
import moduleVision from '@/store/vision'
import moduleStatus from '@/store/status'
import moduleCav from '@/store/cav'
import moduleHR from '@/store/hr'

Vue.use(Vuex);

const modules = {
  bluetooth: moduleBluetooth,
  cav: moduleCav,
  hr: moduleHR,
  status: moduleStatus,
  traffic: moduleTraffic,
  vision: moduleVision,
}

const state = {
  darkMode: null
}

const mutations = {
  SET_DARK_MODE: (state, darkMode) => (state.darkMode = darkMode)
}

const actions = {
  loadDarkMode({ commit }) {
    const darkMode = localStorage.getItem('darkMode');
    commit('SET_DARK_MODE', darkMode ? JSON.parse(darkMode) : true);
  },
  saveDarkMode({ state, commit }, darkMode) {
    commit('SET_DARK_MODE', darkMode);
    localStorage.setItem('darkMode', JSON.stringify(state.darkMode));
  }
}


export default new Vuex.Store({
  modules: modules,
  state: state,
  mutations: mutations,
  actions: actions,
});
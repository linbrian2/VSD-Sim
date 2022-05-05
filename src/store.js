import Vue from 'vue';
import Vuex from 'vuex';

import { state, getters, mutations, actions } from '@/store/app';
import auth from '@/store/auth';
import dashboard from '@/store/dashboard';
import cav from '@/store/cav';
import hr from '@/store/hr';
import bluetooth from '@/store/bluetooth';
import traffic from '@/store/traffic';
import vision from '@/store/vision';

Vue.use(Vuex);

const modules = {
  auth,
  dashboard,
  bluetooth,
  cav,
  hr,
  traffic,
  vision
};

export default new Vuex.Store({
  modules,
  state,
  getters,
  mutations,
  actions
});

import Vue from 'vue';
import Vuex from 'vuex';

import { state, mutations, actions } from '@/store/app';
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

export default new Vuex.Store({
  modules,
  state,
  mutations,
  actions
});

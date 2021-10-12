import Utils from '@/utils/Utils';

const state = {
  currentDate: new Date(),
  currentAction: 'dashboard'
};

const mutations = {
  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
  },
  SET_CURRENT_ACTION(state, action) {
    state.currentAction = action;
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
  }
};

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  actions: actions
};


const state = {
  currentUser: {},
  currentDate: new Date(),
  currentAction: 'dashboard',
}

const mutations ={
  SET_CURRENT_USER(state, user) {
    state.currentUser = user;
  },
  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
  },
  SET_CURRENT_ACTION(state, action) {
    state.currentAction = action;
  },
}

const actions = {
  async fetchLoggedInInfo({ commit }) {
    try {
      const response = await Api.fetchLoggedInInfo();
      commit('SET_CURRENT_USER', response.data);
    } catch (error) {
      console.log(error);
    }
  },
}

export default {
  namespaced: true,
  state: state,
  mutations: mutations,
  actions: actions,
}
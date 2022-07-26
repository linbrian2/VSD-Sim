import router from '@/router';
import AuthService from '@/auth/services/auth.service';
import AuthToken from '@/auth/services/auth-token';
import AuthTimer from '@/auth/services/auth.refresh';

const user = AuthToken.getUser();
const state = {
  status: user ? { loggedIn: true } : {},
  user: user ? user : null,
  refreshTimerStarted: false
};

const getters = {
  isDevUser(state) {
    return state.user && (state.user.role === 'Admin' || state.user.role === 'Dev');
  },
  userRole(state) {
    return state.user ? state.user.role : '';
  }
};

const actions = {
  login({ commit }, { email, password, errorCallback }) {
    commit('loginRequest', { email });
    return AuthService.login(email, password).then(
      user => {
        if (user.jwtToken) {
          AuthTimer.startRefreshTokenTimer();
          commit('loginSuccess', user);
          router.push(sessionStorage.getItem('redirectPath') || '/', () => {});
        }
      },
      error => {
        commit('loginFailure');
        errorCallback(error.data.message);
      }
    );
  },

  logout({ commit }) {
    AuthService.logout();
    AuthTimer.stopRefreshTokenTimer();
    commit('logout');
    commit('refreshTimerStarted', false);
    router.push('/login', () => {});
  },

  startRefreshTokenTimer({ commit }) {
    if (AuthToken.isLoggedIn()) {
      AuthTimer.startRefreshTokenTimer();
      commit('refreshTimerStarted', true);
    }
  }
};

const mutations = {
  loginRequest(state, user) {
    state.status = { loggingIn: true };
    state.user = user;
  },

  loginSuccess(state, user) {
    state.status = { loggedIn: true };
    state.user = user;
  },

  loginFailure(state) {
    state.status = {};
    state.user = null;
  },

  logout(state) {
    state.status = {};
    state.user = null;
  },

  refreshTimerStarted(state, started) {
    state.refreshTimerStarted = started;
  }
};

export default {
  namespaced: true,
  state,
  actions,
  mutations,
  getters
};

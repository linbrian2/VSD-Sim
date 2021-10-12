import axios from 'axios';
import AuthToken from './auth-token';

const API_URL = `${process.env.VUE_APP_AUTH_API_URL}account`;

export default {
  login,
  logout,
  changePassword,
  forgotPassword,
  resetPassword,
  validateResetToken,
  refreshToken
};

function login(email, password) {
  return axios
    .post(API_URL + '/authenticate', { email, password }, { withCredentials: true })
    .then(handleResponse)
    .then(user => {
      // login successful if there's a jwt token in the response
      if (user.jwtToken) {
        // store user details and jwt token in local storage to keep user logged in between page refreshes
        AuthToken.saveUser(user);
      }
      return user;
    })
    .catch(handleError);
}

function logout() {
  // revoke token, stop refresh timer, remove user from local storage to log user out
  if (AuthToken.isLoggedIn()) {
    revokeToken();
  }
  AuthToken.removeUser();
}

function changePassword(oldPassword, newPassword) {
  return axios
    .post(API_URL + '/change-password', { oldPassword, newPassword }, { headers: AuthToken.authHeader() })
    .then(handleResponse)
    .catch(handleError);
}

function forgotPassword(email) {
  return axios
    .post(API_URL + '/forgot-password', { email })
    .then(handleResponse)
    .catch(handleError);
}

function resetPassword(token, password, confirmPassword) {
  return axios
    .post(API_URL + '/reset-password', { token, password, confirmPassword })
    .then(handleResponse)
    .catch(handleError);
}

function validateResetToken(token) {
  return axios
    .post(API_URL + '/validate-reset-token', { token })
    .then(handleResponse)
    .catch(handleError);
}

function refreshToken() {
  return axios
    .post(API_URL + '/refresh-token', {}, { headers: AuthToken.authHeader(), withCredentials: true })
    .then(handleResponse)
    .then(user => {
      // login successful if there's a jwt token in the response
      if (user.jwtToken) {
        // store user details and jwt token to keep user logged in between page refreshes
        AuthToken.saveUser(user);
      }
      return user;
    })
    .catch(handleError);
}

function revokeToken() {
  return axios
    .post(API_URL + '/revoke-token', {}, { headers: AuthToken.authHeader(), withCredentials: true })
    .then(handleResponse)
    .catch(handleError);
}

function handleResponse(response) {
  const data = response.data;
  if (response.status === 401) {
    // auto logout if 401 response returned from api
    logout();
    location.reload(true);
    return Promise.reject('401');
  }

  return data;
}

function handleError(error) {
  if (error.response) {
    // Request made and server responded
    //console.log(error.response.data);
    //console.log(error.response.status);
    //console.log(error.response.headers);
    return Promise.reject(error.response);
  } else if (error.request) {
    // The request was made but no response was received
    console.log(error.request);
    return Promise.reject(error.request);
  } else {
    // Something happened in setting up the request that triggered an Error
    console.log('Error', error.message);
  }
}

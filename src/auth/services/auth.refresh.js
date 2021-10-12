import AuthToken from './auth-token';
import AuthService from './auth.service';

let refreshTokenTimeout;

function refreshToken() {
  console.log('refresh_token', new Date());
  return AuthService.refreshToken().then(
    user => {
      if (user.jwtToken) {
        startRefreshTokenTimer();
      }
      return user;
    },
    error => {
      console.log(error.data.message);
    }
  );
}

function startRefreshTokenTimer() {
  // set a timeout to refresh the token a minute before it expires
  const expires = AuthToken.getTokenExpireDate();
  if (expires) {
    const timeout = expires.getTime() - Date.now() - 60 * 1000;
    //console.log('timeout=', timeout);
    refreshTokenTimeout = setTimeout(refreshToken, timeout);
  }
}

function stopRefreshTokenTimer() {
  clearTimeout(refreshTokenTimeout);
}

export default {
  startRefreshTokenTimer,
  stopRefreshTokenTimer
};

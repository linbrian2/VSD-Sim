function getUser() {
  return JSON.parse(localStorage.getItem('user'));
}

function saveUser(user) {
  localStorage.setItem('user', JSON.stringify(user));
}

function removeUser() {
  localStorage.removeItem('user');
}

function getToken() {
  let user = getUser();
  if (user && user.jwtToken) {
    return user.jwtToken;
  } else {
    return null;
  }
}

function getTokenExpireDate() {
  const jwtToken = getToken();
  return getJwtTokenExpireDate(jwtToken);
}

function getJwtTokenExpireDate(jwtToken) {
  if (jwtToken) {
    const [, payload] = jwtToken.split('.');
    const { exp: expires } = JSON.parse(window.atob(payload));
    if (typeof expires === 'number') {
      return new Date(expires * 1000);
    }
  }
  return null;
}

function isLoggedIn() {
  const jwtToken = getToken();
  if (!jwtToken) {
    return false;
  }

  const expires = getJwtTokenExpireDate(jwtToken);
  if (!expires) {
    return false;
  }

  return new Date() < expires;
}

function authHeader() {
  // return authorization header with jwt token
  let user = getUser();
  if (user && user.jwtToken) {
    return { Authorization: 'Bearer ' + user.jwtToken };
  } else {
    return {};
  }
}

export default {
  authHeader,
  saveUser,
  getToken,
  getUser,
  removeUser,
  getTokenExpireDate,
  isLoggedIn
};

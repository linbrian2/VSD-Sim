import axios from 'axios';
import AuthToken from '@/auth/services/auth-token';

const client = baseURL => {
  const defaultOptions = {
    baseURL
  };

  // Create instance
  const instance = axios.create(defaultOptions);

  // Set the AUTH token for any request
  instance.interceptors.request.use(function(config) {
    const token = AuthToken.getToken();
    config.headers.Authorization = token ? `Bearer ${token}` : '';
    return config;
  });

  // instance.interceptors.response.use(
  //   response => {
  //     if (response.status === 401) {

  //     }
  //     return response;
  //   },
  //   error => {
  //     if (error.response && error.response.data) {
  //       return Promise.reject(error.response.data);
  //     }
  //     return Promise.reject(error.message);
  //   }
  // );

  return instance;
};

export default client;

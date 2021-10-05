import axiosDefault from 'axios';

const axios = axiosDefault.create({
  baseURL: process.env.VUE_APP_STATUS_API_URL,
});

export default {
  fetchLoggedInInfo() {
    return axios.get('api/loggedInInfo');
  },

  fetchStatus() {
    return axios.get('api/status');
  },

  fetchErrors(start) {
    return axios.post('api/errors', { start });
  },

  fetchSensorErrors(id, start) {
    return axios.post('api/sensorErrors', { id, start });
  }
};

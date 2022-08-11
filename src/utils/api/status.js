import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_STATUS_API_URL);

export default {
  fetchStatus() {
    return AxiosClient.get('api/status');
  },

  fetchErrors(start) {
    return AxiosClient.post('api/errors', { start });
  },

  fetchSensorErrors(id, start) {
    return AxiosClient.post('api/sensorErrors', { id, start });
  },

  fetchSensorsErrorCounts(start) {
    return AxiosClient.post('api/sensorsErrorCounts', { start });
  }
};

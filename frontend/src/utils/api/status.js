import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_STATUS_API_URL);

export default {
  fetchStatus() {
    return AxiosClient.get('api/status');
  },

  fetchErrors(start) {
    return AxiosClient.post('api/errors', { start });
  },

  fetchErrorDevices(start, limit) {
    return AxiosClient.post('api/error-devices', { start, limit });
  },

  fetchSensorErrors(id, start) {
    return AxiosClient.post('api/sensorErrors', { id, start });
  },

  fetchSensorsErrorCounts(start) {
    return AxiosClient.post('api/sensorsErrorCounts', { start });
  },

  fetchSensorsErrorPercentCounts(id, start, days) {
    return AxiosClient.post('api/sensorErrorPercentCounts', { id, start, days });
  }
};

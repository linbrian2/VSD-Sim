import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_NTCIP_API_URL);

export default {
  getPatterns(ip, patterns) {
    return AxiosClient.post('api/get-pattern-parameters', { controllerIp: ip, patternNumbers: patterns });
  },

  getCurrentPatternNumber(ip) {
    return AxiosClient.post('api/get-current-pattern', { controllerIp: ip });
  },

  getCurrentPatternParameters(ip) {
    return AxiosClient.post('api/get-current-pattern-parameters', { controllerIp: ip });
  },

  changePattern(ip, params) {
    return AxiosClient.post('api/change-pattern-parameters', { ipAddress: ip, params });
  },

  createPattern(ip, params) {
    return AxiosClient.post('api/change-pattern-parameters', { ipAddress: ip, params });
  },

  executePattern(ip, patternNumber) {
    return AxiosClient.post('api/execute-pattern', { controllerIp: ip, patternNumber });
  },

  deletePattern(ip, patternNumber) {
    return AxiosClient.post('api/delete-pattern', { controllerIp: ip, patternNumber });
  },

  revertBack(ip) {
    return AxiosClient.post('api/revert-back', { controllerIp: ip });
  },

  addController(ip) {
    return AxiosClient.post('api/add-controller', { controllerIp: ip });
  },

  removeController(ip) {
    return AxiosClient.post('api/remove-controller', { controllerIp: ip });
  }
};

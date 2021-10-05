import axiosDefault from 'axios';

const axios = axiosDefault.create({
  baseURL: process.env.VUE_APP_HR_API_URL,
});

export default {
  fetchDevices() {
    return axios.get('api/devices');
  },

  fetchStatusOfDevices() {
    return axios.get('api/status');
  },

  fetchPlans(id, start) {
    return axios.post('api/signal', { id: id, start: start });
  },

  fetchPCD(id, phase, start) {
    return axios.post('api/pcd', { id: id, phases: [phase], start: start });
  },

  fetchPCDs(id, phases, start) {
    return axios.post('api/pcd', { id, phases, start });
  },

  fetchPCDSet(series, start) {
    return axios.post('api/pcd', { series, start });
  },

  fetchTiming(id, start, duration) {
    return axios.post('api/timing', {
      id: id,
      start: start,
      duration: duration
    });
  },

  fetchPhaseTiming(id, start, duration) {
    return axios.post('api/phaseTiming', {
      id: id,
      start: start,
      duration: duration
    });
  },

  fetchVolumeSet(series, start) {
    return axios.post('api/volume', { series, start });
  },

  fetchIntervals(id, phases, start) {
    return axios.post('api/phaseIntervals', { id, phases, start });
  },

  fetchDelays(id, phases, start) {
    return axios.post('api/phaseDelays', { id, phases, start });
  },

  fetchAORSet(series, start) {
    return axios.post('api/aor', { series, start });
  },

  fetchDetectorOccupancy(id, start, interval, type) {
    return axios.post('api/detectorOccupancy', { id, start, interval, type });
  },

  fetchDetectorOccupancySet(series, start, interval, type) {
    return axios.post('api/detectorOccupancy', { series, start, interval, type });
  },
};

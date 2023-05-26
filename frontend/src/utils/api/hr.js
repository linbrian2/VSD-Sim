import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_HR_API_URL);

export default {
  fetchDevices() {
    return AxiosClient.get('api/devices');
  },

  fetchStatusOfDevices() {
    return AxiosClient.get('api/status');
  },

  fetchPlans(id, start) {
    return AxiosClient.post('api/signal', { id: id, start: start });
  },

  fetchPCD(id, phase, start) {
    return AxiosClient.post('api/pcd', { id: id, phases: [phase], start: start });
  },

  fetchPCDs(id, phases, start) {
    return AxiosClient.post('api/pcd', { id, phases, start });
  },

  fetchPCDSet(series, start) {
    return AxiosClient.post('api/pcd', { series, start });
  },

  fetchTiming(id, start, duration) {
    return AxiosClient.post('api/timing', {
      id: id,
      start: start,
      duration: duration
    });
  },

  fetchPhaseTiming(id, start, duration) {
    return AxiosClient.post('api/phaseTiming', {
      id: id,
      start: start,
      duration: duration
    });
  },

  fetchTimeSpaceData(ids, start, duration, overlay) {
    const efficiency = 1;
    return AxiosClient.post('api/timeSpaceData', { ids, start, duration, overlay, efficiency });
  },

  fetchVolumeSet(series, start) {
    return AxiosClient.post('api/volume', { series, start });
  },

  fetchIntervals(id, phases, start) {
    return AxiosClient.post('api/phaseIntervals', { id, phases, start });
  },

  fetchDelays(id, phases, start) {
    return AxiosClient.post('api/phaseDelays', { id, phases, start });
  },

  fetchAORSet(series, start) {
    return AxiosClient.post('api/aor', { series, start });
  },

  fetchDetectorOccupancy(id, start, interval, type) {
    return AxiosClient.post('api/detectorOccupancy', { id, start, interval, type });
  },

  fetchDetectorOccupancySet(series, start, interval, type) {
    return AxiosClient.post('api/detectorOccupancy', { series, start, interval, type });
  }
};

import axios from 'axios';

export default {
  fetchDevices() {
    return axios.get('vision/devices');
  },
  fetchInfo(id) {
    return axios.get('vision/device', { params: { id } });
  },
  fetchCounts(id, start, interval) {
    return axios.post('vision/counts', { id, start, interval });
  },
  fetchVisionResult(id, start) {
    return axios.post('vision/visionResult', { id, start });
  },

  fetchVideoResult(id, start) {
    let output = 1;
    return axios.post('vision/visionResult', { id, start, output });
  },

  fetchVisionData(id, start) {
    return axios.post('vision/visionData', { id, start });
  },
  fetchFlowData(id, start) {
    return axios.post('vision/flowData', { id, start });
  },
  /* Vehicle Re-ID */
  fetchSnippets() {
    return axios.get('http://10.51.4.9:34556/getupdate').then(res => {
        return res.data;
    });
  },
};

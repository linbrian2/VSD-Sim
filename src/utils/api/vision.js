import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_VISION_API_URL);

export default {
  fetchDevices() {
    return AxiosClient.get('vision/devices');
  },
  fetchInfo(id) {
    return AxiosClient.get('vision/device', { params: { id } });
  },
  fetchCounts(id, start, interval) {
    return AxiosClient.post('vision/counts', { id, start, interval });
  },
  fetchVisionResult(id, start) {
    return AxiosClient.post('vision/visionResult', { id, start });
  },

  fetchVideoResult(id, start) {
    let output = 1;
    return AxiosClient.post('vision/visionResult', { id, start, output });
  },

  fetchVisionData(id, start) {
    return AxiosClient.post('vision/visionData', { id, start });
  },
  fetchFlowData(id, start) {
    return AxiosClient.post('vision/flowData', { id, start });
  },
  /* Vehicle Re-ID */
  fetchSnippets() {
    return AxiosClient.get('http://10.51.4.9:34556/getupdate').then(res => {
      return res.data;
    });
  }
};

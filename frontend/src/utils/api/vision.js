import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_VISION_API_URL);

export default {
  fetchDevices() {
    return AxiosClient.get('vision/devices');
  },

  fetchInfo(id, type) {
    return AxiosClient.get('vision/device', { params: { id, type } });
  },

  fetchCounts(id, type, start, interval) {
    return AxiosClient.post('vision/counts', { id, type, start, interval });
  },

  fetchVisionResult(id, type, start) {
    return AxiosClient.post('vision/visionResult', { id, type, start });
  },

  fetchVideoResult(id, type, start) {
    let output = 1;
    return AxiosClient.post('vision/visionResult', { id, type, start, output });
  },

  fetchVisionData(id, type, start) {
    return AxiosClient.post('vision/visionData', { id, type, start });
  },

  fetchFlowData(id, start) {
    return AxiosClient.post('vision/flowData', { id, start });
  },

  /* Vehicle Re-ID */
  fetchSnippets() {
    const url = `${process.env.VUE_APP_REID_API_URL}/getupdate`;
    return AxiosClient.get(url).then(res => {
      return res.data;
    });
  }
};

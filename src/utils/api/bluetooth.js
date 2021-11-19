import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_BLUETOOTH_API_URL);

export default {
  /* Init Today Full */
  initFullDay() {
    return AxiosClient.get(`bluetooth/init/full`)
      .then(res => {
        return res.data
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Segments */
  fetchSegments(endTS) {
    return AxiosClient.get(`bluetooth/segments/${endTS}`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchSegmentsFull(endTS) {
    let endpoint = endTS ? `/${endTS}` : ``;
    return AxiosClient.get(`bluetooth/segments/full${endpoint}`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Waze */
  fetchWazeData(min, endTS) {
    return AxiosClient.get(`bluetooth/waze/latest-${min}m/${endTS}`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchWazeDataFull(endTS) {
    let endpoint = endTS ? `/${endTS}` : ``;
    return AxiosClient.get(`bluetooth/waze/full${endpoint}`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Devices */
  fetchDevices(endTS) {
    return AxiosClient.get(`bluetooth/devices/${endTS}`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchDevicesFull(endTS) {
    let endpoint = endTS ? `/${endTS}` : ``;
    return AxiosClient.get(`bluetooth/devices/full${endpoint}`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Traffic Flow */
  fetchHistoricalTTWIncidentsByLinkID(linkId) {
    return AxiosClient.get(`bluetooth/historical/${linkId}/w-incidents`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchHistoricalTTWoIncidentsByLinkID(linkId) {
    return AxiosClient.get(`bluetooth/historical/${linkId}/wo-incidents`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchCurrTTByLinkId(linkId, endTS) {
    return AxiosClient.get(`bluetooth/current/${linkId}/${endTS}`)
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Sensors */
  fetchSensors() {
    return AxiosClient.get('bluetooth/sensors')
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Breakdown Probability */
  fetchDevicesBPData() {
    return AxiosClient.get('bluetooth/breakdown-prob/list')
      .then(res => {
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchBP(id, dir, endTS) {
    return AxiosClient.get(`bluetooth/breakdown-prob/${id}/${dir}/${endTS}`)
      .then(res => {
        console.log(res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  }
};

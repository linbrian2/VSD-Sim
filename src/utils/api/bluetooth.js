import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_BLUETOOTH_API_URL);

export default {
  /* Init Today Full */
  initFullDay() {
    return AxiosClient.get(`bluetooth/init/full`)
      .then(() => {
        //console.log('Success: %o', res.data);
        return;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Segments */
  fetchSegments(endTS) {
    return AxiosClient.get(`bluetooth/segments/${endTS}`)
      .then(res => {
        // console.log('Fetched Segment Data (Short): %o', res.data);
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
        //console.log('Fetched Segment Data (Full Day): %o', res.data);
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
        //console.log('Fetched Waze Data (Short): %o', res.data);
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
        //console.log('Fetched Waze Data (Full Day): %o', res.data);
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
        //console.log('Fetched Device Data (Short): %o', res.data);
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
        //console.log('Fetched Device Data (Full Day): %o', res.data);
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
        // console.log('Fetched Historical Data w/ Incidents: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchHistoricalTTWoIncidentsByLinkID(linkId) {
    return AxiosClient.get(`bluetooth/historical/${linkId}/wo-incidents`)
      .then(res => {
        // console.log('Fetched Historical Data w/o Incidents: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchCurrTTByLinkId(linkId, endTS) {
    return AxiosClient.get(`bluetooth/current/${linkId}/${endTS}`)
      .then(res => {
        //console.log('Fetched Current Data: %o', res.data);
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
        //console.log('Fetched Sensor Data: %o', res.data);
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
        //console.log('Fetched Devices Breakdown Probability Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchBP(apiReqName, start, end) {
    return AxiosClient.get(`bluetooth/breakdown-prob/${apiReqName}-${start}-${end}`)
      .then(res => {
        //console.log('Fetched Breakdown Probability Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  }
};

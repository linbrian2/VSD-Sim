import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_BLUETOOTH_API_URL);

export default {
  /* Segments */
  fetchSegments(printInfo = true) {
    return AxiosClient.get('bluetooth/segments')
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Segment Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchSegmentsFull(printInfo = true) {
    return AxiosClient.get('bluetooth/segments/full')
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Segments Data (Full Day Curr): %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchSegmentsHist(endTS, printInfo = true) {
    return AxiosClient.get(`bluetooth/segments/${endTS}`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Segment Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchSegmentsHistFull(endTS, printInfo = true) {
    return AxiosClient.get(`bluetooth/segments/full/${endTS}`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Segment Data (Full Day Hist): %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Waze */
  fetchWazeData(printInfo = true) {
    return AxiosClient.get('bluetooth/waze')
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Waze Data (Short Curr): %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchWazeDataFull(printInfo = true) {
    return AxiosClient.get('bluetooth/waze/full')
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Waze Data (Full Day Curr): %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchWazeDataHist(min, endTS, printInfo = true) {
    return AxiosClient.get(`bluetooth/waze/latest-${min}m/${endTS}`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Waze Data (Short Hist): %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchWazeDataHistFull(endTS, printInfo = true) {
    return AxiosClient.get(`bluetooth/waze/full/${endTS}`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Waze Data (Full Day Hist): %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Traffic Flow */
  fetchHistoricalTTWIncidentsByLinkID(linkId, printInfo = true) {
    return AxiosClient.get(`bluetooth/historical/${linkId}/w-incidents`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Historical Data w/ Incidents: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchHistoricalTTWoIncidentsByLinkID(linkId, printInfo = true) {
    return AxiosClient.get(`bluetooth/historical/${linkId}/wo-incidents`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Historical Data w/o Incidents: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchCurrTTByLinkId(linkId, endTS, printInfo = true) {
    return AxiosClient.get(`bluetooth/current/${linkId}/${endTS}`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Current Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Sensors */
  fetchSensors(printInfo = true) {
    return AxiosClient.get('bluetooth/sensors')
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Sensor Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Devices */
  fetchDevices(printInfo = true) {
    return AxiosClient.get('bluetooth/devices')
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Device Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchDevicesFull(printInfo = true) {
    return AxiosClient.get('bluetooth/devices/full')
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Device Data (Full Day Curr): %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchDevicesHist(endTS, printInfo = true) {
    return AxiosClient.get(`bluetooth/devices/${endTS}`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Device Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchDevicesHistFull(endTS, printInfo = true) {
    return AxiosClient.get(`bluetooth/devices/full/${endTS}`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Device Data (Full Day Hist): %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  /* Breakdown Probability */
  fetchDevicesBPData(printInfo = true) {
    return AxiosClient.get('bluetooth/breakdown-prob/list')
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Devices Breakdown Probability Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  },
  fetchBP(apiReqName, start, end, printInfo = true) {
    return AxiosClient.get(`bluetooth/breakdown-prob/${apiReqName}-${start}-${end}`)
      .then(res => {
        /* if (printInfo) */ console.log('Fetched Breakdown Probability Data: %o', res.data);
        return res.data;
      })
      .catch(err => {
        console.log(err.message);
      });
  }
};

import TrafficApi from '@/utils/api/traffic';
import HRApi from '@/utils/api/hr';
import StatusApi from '@/utils/api/status';
import Utils from '@/utils/Utils';
import store from '@/store';

const state = {
  showTable: false,
  activeMarker: null,

  trafficIncidents: [],
  trafficDevices: null,
  trafficRestrictions: [],
  signalIssues: null,
  deviceAnomalies: null,
  congestedSegments: null,
  wazeAlerts: null,

  selectedTrafficIncident: null,
  selectedTrafficDevice: null,
  selectedRestriction: null,
  selectedSignalIssue: null,
  selectedAnomalyDevice: null,
  selectedCongestedSegment: null,
  selectedWazeAlert: null,

  hrSummary: null,
  detectors: null,
  allDeviceStatus: null,

  incidents: [],
  incidentSegmentLinks: null,
  incidentMarkers: null,

  pref: {
    theme: 'Dark Mode',
    dataUpdate: 5,
    dataUpdateEnabled: true,
    swap: 30,
    swapEnabled: true,
    resizableMap: false,
    layout: '1: Card, 2: Map, 3: Info',
    limitResults: true
  },
  showHighcharts: 1,
  darkMode: null,
  currentDate: new Date()
};

const mutations = {
  SHOW_TABLE(state, show) {
    state.showTable = show;
  },
  SET_ACTIVE_MARKER(state, marker) {
    state.activeMarker = marker;
  },
  SET_TRAFFIC_INCIDENTS(state, data) {
    state.trafficIncidents = data;
    state.incidents = data;
  },
  SET_TRAFFIC_DEVICES(state, devices) {
    state.trafficDevices = devices;
  },
  SET_TRAFFIC_RESTRICTIONS(state, data) {
    state.trafficRestrictions = data;
  },
  SET_SIGNAL_ISSUES_DATA(state, data) {
    state.signalIssues = data;
  },
  SET_HR_SUMMARY(state, summary) {
    state.hrSummary = summary;
  },
  SET_DETECTORS(state, data) {
    state.detectors = data;
  },
  SET_CONGESTED_SEGMENTS(state, data) {
    state.congestedSegments = data;
  },
  SET_WAZE_ALERTS(state, data) {
    state.wazeAlerts = data;
  },
  SET_DEVICE_ANOMALIES_DATA(state, data) {
    state.deviceAnomalies = data;
  },
  SET_ALL_DEVICE_STATUS_DATA(state, data) {
    state.allDeviceStatus = data;
  },
  SET_DARK_MODE: (state, darkMode) => {
    state.darkMode = darkMode;
  },
  SET_CURRENT_DATE(state, date) {
    state.currentDate = date;
  },

  SET_SELECTED_TRAFFIC_INCIDENT(state, data) {
    state.selectedTrafficIncident = data;
  },

  SET_SELECTED_TRAFFIC_DEVICE(state, data) {
    state.selectedTrafficDevice = data;
  },

  SET_SELECTED_TRAFFIC_RESTRICTION(state, data) {
    state.selectedRestriction = data;
  },

  SET_SELECTED_SIGNAL_ISSUE(state, data) {
    state.selectedSignalIssue = data;
  },

  SET_SELECTED_ANOMALY_DEVICE(state, data) {
    state.selectedAnomalyDevice = data;
  },

  SET_SELECTED_CONGESTED_SEGMENT(state, data) {
    state.selectedCongestedSegment = data;
  },

  SET_SELECTED_WAZE_ALERT(state, data) {
    state.selectedWazeAlert = data;
  }
};

const getters = {
  getNotification(state) {
    return state.trafficIncidents.map(incident => ({
      id: incident.id,
      title: `<span ${incident.status === 0 ? 'style="color:green;"' : ''} >${incident.type} #${incident.id}</span>`,
      status: incident.status,
      icon: 'mdi-alert-octagon',
      startTime: new Date(incident.startTime),
      evidenceCounts: incident.evidenceCounts,
      incidentScore: incident.severity,
      severityColor: incident.severityColor
    }));
  }
};

const actions = {
  //! Traffic Incidents
  async fetchTrafficIncidents({ commit, dispatch }, date = null) {
    const severity = 50;
    const duration = 30;

    try {
      let start = date ? date.getTime() : new Date().getTime();
      const response = await TrafficApi.fetchIncidentData(start, 1, severity, duration);
      if (response.data && response.data.data) {
        const incidentList = response.data.data;

        // Add position
        const newIncidentList = incidentList.map(item => ({ ...item, position: item.location }));

        let sortedData = null;
        if (date) {
          sortedData = newIncidentList;
        } else {
          const duration = 2 * 60 * 60 * 1000;
          const now = new Date().getTime();
          sortedData = newIncidentList.filter(x => now - x.endTime < duration);
        }

        // Sort by severity
        sortedData.sort((a, b) => (a.severity > b.severity ? -1 : b.severity > a.severity ? 1 : 0));

        commit('SET_TRAFFIC_INCIDENTS', sortedData ? sortedData : []);
      } else {
        commit('SET_TRAFFIC_INCIDENTS', []);
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },
  //! Traffic Flow Issues
  async fetchTrafficDevices({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchAnomalyDevices();
      let deviceLocations = response.data.map(obj => ({ ...obj, status: 0 }));

      if (store.getters['getSetting']('dashboard', 'addTrafficFlowIssue')) {
        deviceLocations[0].status = 1;
      }
      deviceLocations = deviceLocations.map(obj => ({ ...obj, state: obj.status === 0 ? 'Normal' : 'Anomaly' }));
      let sortedData = deviceLocations.sort((a, b) => (a.status > b.status ? -1 : b.status > a.status ? 1 : 0));

      commit('SET_TRAFFIC_DEVICES', sortedData);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  //! Traffic rescrictions
  async fetchTrafficRestrictions({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchLatestRestrictionData(3600);
      if (response.data && response.data.data) {
        const result = response.data.data;
        let data = result;

        // Fill in nearby cameras if it has any
        if (result.length > 0) {
          const locations = result.map(res => ({ id: res.id, lat: res.position.lat, lng: res.position.lng }));
          const res = await TrafficApi.fetchNearbyCameras(locations);
          if (res.data && res.data.data) {
            let nearbyCameras = res.data.data;
            data = result.map(x => ({ ...x, cameras: nearbyCameras[x.id] }));
          }
        }

        // Set to the store value
        commit('SET_TRAFFIC_RESTRICTIONS', data);
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  //! Signal Performance Issues
  async fetchSignalIssues({ commit, dispatch }) {
    try {
      const res = await HRApi.fetchDevices();
      const detectors = res.data;

      const response = await HRApi.fetchStatusOfDevices();
      let sortedData = response.data
        .map(x => ({ ...x, score: x.AoR[0] + x.AoR[1] }))
        .sort((a, b) => (a.score > b.score ? -1 : b.score > a.score ? 1 : 0));

      const filteredDetectors = sortedData.filter(x => x.score > 60);

      // Combine detectors
      const result = filteredDetectors.map(t1 => ({ ...t1, ...detectors.find(t2 => t2.id === t1.id) }));

      // Fill in nearby cameras
      let signalIssues = result;
      if (result.length > 0) {
        const locations = result.map(signal => ({ id: signal.id, lat: signal.position.lat, lng: signal.position.lng }));
        const res = await TrafficApi.fetchNearbyCameras(locations);
        if (res.data && res.data.data) {
          let nearbyCameras = res.data.data;
          signalIssues = result.map(x => ({ ...x, cameras: nearbyCameras[x.id] }));
        }
      }

      commit('SET_SIGNAL_ISSUES_DATA', signalIssues);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  //! Device Anomalies
  async fetchAnomalyDevices({ commit, dispatch }, limit) {
    let date = new Date();
    try {
      this.updatedTime = new Date();
      const response = await StatusApi.fetchErrorDevices(date.getTime(), limit);

      if (response.data && response.data.data) {
        let data = response.data.data;
        commit('SET_DEVICE_ANOMALIES_DATA', data);
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' });
    }
  },

  //! Congested Routes
  async fetchCongestedSegments({ commit, dispatch }, level) {
    try {
      const response = await TrafficApi.fetchCongestedBluetoothSegments(level);
      let sortedData = response.data.sort((a, b) =>
        a.travelTime.level > b.travelTime.level ? -1 : b.travelTime.level > a.travelTime.level ? 1 : 0
      );
      const result = sortedData.filter(x => x.travelTime.level >= 5);
      commit('SET_CONGESTED_SEGMENTS', result);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  //! Waze Alerts
  async fetchWazeAlerts({ commit, dispatch }) {
    try {
      const response = await TrafficApi.fetchWazeData();
      let sortedData = response.data
        .sort((a, b) => (a.confidence > b.confidence ? -1 : b.confidence > a.confidence ? 1 : 0))
        .filter(
          (value, index, self) => index === self.findIndex(t => t.alertTimeTS === value.alertTimeTS && t.description)
        );
      const result = sortedData.filter(x => x.reliability >= 5);

      let wazeAlerts = [];

      // Fill in nearby cameras
      if (result.length > 0) {
        wazeAlerts = result;
        const locations = result.map(waze => ({ id: waze.id, lat: waze.position.lat, lng: waze.position.lng }));
        const res = await TrafficApi.fetchNearbyCameras(locations);
        if (res.data && res.data.data) {
          let nearbyCameras = res.data.data;
          wazeAlerts = result.map(x => ({ ...x, cameras: nearbyCameras[x.id] }));
        }
      }

      commit('SET_WAZE_ALERTS', wazeAlerts);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  async fetchStatusOfDevices({ commit, dispatch }) {
    try {
      const response = await HRApi.fetchStatusOfDevices();
      let sortedData = response.data
        .map(x => ({ ...x, score: x.AoR[0] + x.AoR[1] }))
        .sort((a, b) => (a.score > b.score ? -1 : b.score > a.score ? 1 : 0));
      commit('SET_HR_SUMMARY', sortedData);
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' }, { root: true });
    }
  },

  async fetchAllDeviceStatus({ commit, dispatch }) {
    let date = new Date();
    try {
      this.updatedTime = new Date();
      const response = await StatusApi.fetchErrors(date.getTime());

      if (response.data && response.data.data) {
        let data = response.data.data;
        let errCount5m = data.totalErrorCounts.filter(x => x != 0);
        let count = 0;
        for (let i = 0; i < 12; i++) {
          if (i < errCount5m.length) {
            count += errCount5m[errCount5m.length - 1 - i];
          }
        }
        data.sensorErrorCounts = data.sensorErrorCounts
          .map(x => {
            return { ...x, score: x.counts[0] + x.counts[1] * 100 };
          })
          .sort((a, b) => (a.score > b.score ? -1 : b.score > a.score ? 1 : 0));

        commit('SET_ALL_DEVICE_STATUS_DATA', { ...data, count });
      }
    } catch (error) {
      dispatch('setSystemStatus', { text: error, color: 'error' });
    }
  },

  loadDarkMode({ commit }) {
    const darkMode = localStorage.getItem('ThemeDarkMode');
    commit('SET_DARK_MODE', darkMode ? JSON.parse(darkMode) : true);
  },

  saveDarkMode({ state, commit }, darkMode) {
    commit('SET_DARK_MODE', darkMode);
    localStorage.setItem('ThemeDarkMode', JSON.stringify(state.darkMode));
  },

  incCurrentDate({ state, commit }, days) {
    const currentDate = state.currentDate;
    if (Utils.isTodayAndBeyond(currentDate) && days > 0) {
      return;
    }
    const result = new Date(currentDate);
    result.setDate(result.getDate() + days);
    commit('SET_CURRENT_DATE', result);
  }
};

export default {
  namespaced: true,
  state: state,
  getters: getters,
  mutations: mutations,
  actions: actions
};

import axios from 'axios';

export default {
  fetchLoggedInInfo() {
    return axios.get('api/loggedInInfo');
  },

  fetchDevices() {
    return axios.get('api/trafficDevices');
  },

  fetchLCMDevices() {
    return axios.get('api/lcmDevices');
  },

  fetchTrafficPerLaneDevices() {
    return axios.get('api/trafficPerLaneDevices');
  },

  fetchTrafficPerMinuteDevices() {
    return axios.get('api/trafficPerMinuteDevices');
  },

  fetchInfo(id) {
    return axios.get('api/trafficDevice', { params: { id } });
  },

  fetchSingleFlowData(id, direction, interval, startDate) {
    let params = { id, direction, interval, startDate };
    return axios.post('api/trafficFlow', params);
  },

  fetchTrafficFlowBaselineData(id, direction, interval, startDate, endDate) {
    let params = { id, direction, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return axios.post('api/trafficFlowBaseline', params);
  },

  fetchTrafficFlowData(id, direction, interval, startDate, endDate) {
    let params = { id, direction, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return axios.post('api/trafficFlow', params);
  },

  fetchTrafficFlowPerLaneData(id, uid, direction, interval, startDate, endDate) {
    let params = { id, uid, direction, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return axios.post('api/trafficFlowPerLane', params);
  },

  fetchTrafficFlowPerMinuteData(id, direction, interval, startDate, endDate) {
    let params = { id, direction, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return axios.post('api/trafficFlowPerMinute', params);
  },

  fetchTrafficFlowAndBaselineData(id, direction, interval, startDate) {
    let params = { id, direction, interval, startDate };
    return axios.post('api/trafficFlowAndBaseline', params);
  },

  fetchFlowData(deviceId, direction, start) {
    let id = `${deviceId}-${direction}`;
    return axios.post('vision/flowData', { id, start });
  },

  fetchPredictionSensors(baseURL) {
    const api = axios.create({ baseURL });
    return api.get('api/sensors');
  },

  fetchPredictions(baseURL, deviceId, deviceDir, startTime, endTime, method) {
    const api = axios.create({ baseURL });
    return api.post('api/predict', { deviceId, deviceDir, startTime, endTime, method });
  },

  fetchAnomalyDevices() {
    return axios.get('api/anomalyDevices');
  },

  fetchAnomalyResults(start, route, direction) {
    return axios.post('api/anomalyResult', { start, route, direction });
  },

  fetchAnomalyTimeList(deviceId, direction, start) {
    return axios.post('api/anomalyTimeList', { deviceId, direction, start });
  },

  fetchAnomalyHourlyResults(deviceId, direction, start, hour) {
    return axios.post('api/anomalyHourlyResult', { deviceId, direction, start, hour });
  },

  fetchCombinedAnomalyResults(deviceId, direction, start) {
    return axios.post('api/combinedAnomalyResult', { deviceId, direction, start });
  },

  fetchBluetoothAnomalyDevices() {
    return axios.get('travelTime/bluetoothAnomalyDevices');
  },

  fetchLatestDeviceInfo(id) {
    return axios.get('api/latestDeviceInfo', { params: { id } });
  },

  fetchTravelTimeData(linkId, interval, start) {
    return axios.post('travelTime/travelTimeData', { linkId, interval, start });
  },

  fetchLatestTravelTimeInfo(linkId) {
    return axios.get('travelTime/latestTravelTimeInfo', { params: { linkId } });
  },

  fetchWeatherStations() {
    return axios.get('weather/weatherStations');
  },

  fetchBluetoothSegments() {
    return axios.get('travelTime/bluetoothSegments');
  },

  fetchWeatherData(id, interval, startDate, endDate) {
    let params = { id, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return axios.post('weather/weatherData', params);
  },

  fetchLatestWeatherData(secondsAgo) {
    return axios.get('weather/latestWeatherData', { params: { secondsAgo } });
  },

  fetchWeatherDataOneDay(stationId, start) {
    return axios.post('weather/weatherDataOneDay', { stationId, start });
  },

  fetchLatestWeatherDataInfo(stationId, secondsAgo) {
    return axios.get('weather/latestWeatherDataInfo', { params: { stationId, secondsAgo } });
  },

  fetchLatestAnomalyData(secondsAgo) {
    return axios.get('api/latestAnomalyData', { params: { secondsAgo } });
  },

  fetchLatestBluetoothAnomalyData(secondsAgo) {
    return axios.get('travelTime/latestBluetoothAnomalyData', { params: { secondsAgo } });
  },

  fetchLatestRestrictionData(secondsAgo) {
    return axios.get('api/latestRestrictionData', { params: { secondsAgo } });
  },

  fetchLatestAnomalySegments(secondsAgo, severity, duration) {
    return axios.get('api/latestAnomalySegments', { params: { secondsAgo, severity, duration } });
  },

  fetchTravelTimeHeatMapData(route, direction, start, interval) {
    return axios.post('travelTime/travelTimeHeatMapData', { route, direction, start, interval });
  },

  fetchTravelTimeSegments(route, direction) {
    return axios.post('travelTime/travelTimeSegments', { route, direction });
  },

  fetchIncidentData(start, type, severity, duration) {
    const video = true;
    return axios.post('api/incidents', { start, type, video, severity, duration });
  },

  fetchMdistParameters(deviceId, direction, time) {
    return axios.post('api/mdistParameters', { deviceId, direction, time });
  },

  fetchTrafficFlowVolumeSpeed(id, direction, startDate) {
    let params = { id, direction, startDate };
    return axios.post('api/trafficFlowVolumeSpeed', params);
  },

  fetchRangedTrafficFlowVolumeSpeed(id, direction, startDate, range) {
    let params = { id, direction, startDate, range };
    return axios.post('api/trafficFlowVolumeSpeed', params);
  },

  fetchTrafficFlowAnomalyVolumeSpeed(id, direction, startDate) {
    let params = { id, direction, startDate };
    return axios.post('api/trafficFlowAnomalyVolumeSpeed', params);
  },

  fetchLCMCurve(id, direction, count = 200) {
    let params = { id, direction, count };
    return axios.post('api/fetchLCMCurve', params);
  },

  fetchTimingPlan(id) {
    return axios.get('api/timingPlan', { params: { id } });
  },

  fetchSimulationScenarios(type) {
    return axios.get('api/simulationScenarios', { params: { type } });
  }
};

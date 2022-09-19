import axios from 'axios';
import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_TRAFFIC_API_URL);

export default {
  // * Bluetooth
  fetchWazeFull(date) {
    let ts = date.getTime();
    return AxiosClient.get(`bluetooth/waze/full/${ts}`);
  },
  fetchSegmentsFull(date) {
    let ts = date.getTime();
    return AxiosClient.get(`bluetooth/segments/full/${ts}`);
  },
  fetchDevicesFull(date) {
    let ts = date.getTime();
    return AxiosClient.get(`bluetooth/devices/full/${ts}`);
  },
  fetchSensors() {
    return AxiosClient.get('bluetooth/sensors');
  },

  // * HR
  fetchHRDevices() {
    return AxiosClient.get('hr/devices');
  },

  fetchStatusOfDevices() {
    return AxiosClient.get('hr/status');
  },
  // * Status
  fetchErrors(start) {
    return AxiosClient.post('status/errors', { start });
  },

  // * Dashboard - Routes (High Congestion)
  fetchSegments() {
    let ts = new Date().getTime();
    return AxiosClient.get(`bluetooth/segments/${ts}`);
  },
  // * Dashboard - Reported Waze Alerts
  fetchWazeData() {
    let min = 60;
    let ts = new Date().getTime();
    return AxiosClient.get(`bluetooth/waze/latest-${min}m/${ts}`);
  },
  fetchDevices() {
    return AxiosClient.get('api/trafficDevices');
  },

  fetchLCMDevices() {
    return AxiosClient.get('api/lcmDevices');
  },

  fetchTrafficPerLaneDevices() {
    return AxiosClient.get('api/trafficPerLaneDevices');
  },

  fetchTrafficPerMinuteDevices() {
    return AxiosClient.get('api/trafficPerMinuteDevices');
  },

  fetchZoneList() {
    return AxiosClient.get('api/zoneList');
  },

  fetchInfo(id) {
    return AxiosClient.get('api/trafficDevice', { params: { id } });
  },

  fetchSingleFlowData(id, direction, interval, startDate) {
    let params = { id, direction, interval, startDate };
    return AxiosClient.post('api/trafficFlow', params);
  },

  fetchTrafficFlowBaselineData(id, direction, interval, startDate, endDate) {
    let params = { id, direction, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return AxiosClient.post('api/trafficFlowBaseline', params);
  },

  fetchTrafficFlowData(id, direction, interval, startDate, endDate) {
    let params = { id, direction, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return AxiosClient.post('api/trafficFlow', params);
  },

  fetchTrafficFlowPerLaneData(id, uid, direction, interval, startDate, endDate) {
    let params = { id, uid, direction, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return AxiosClient.post('api/trafficFlowPerLane', params);
  },

  fetchTrafficFlowPerMinuteData(id, direction, interval, startDate, endDate) {
    let params = { id, direction, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return AxiosClient.post('api/trafficFlowPerMinute', params);
  },

  fetchTrafficFlowAndBaselineData(id, direction, interval, startDate) {
    let params = { id, direction, interval, startDate };
    return AxiosClient.post('api/trafficFlowAndBaseline', params);
  },

  fetchTrafficVolumeAndBaselineData(id, direction, interval, startDate) {
    let params = { id, direction, interval, startDate };
    return AxiosClient.post('api/trafficVolumeAndBaseline', params);
  },

  fetchTrafficVolumeAndBaselineDataList(ids, interval, startDate) {
    let params = { ids, interval, startDate };
    return AxiosClient.post('api/trafficVolumeAndBaseline', params);
  },

  fetchFlowData(deviceId, direction, start) {
    let id = `${deviceId}-${direction}`;
    return AxiosClient.post('vision/flowData', { id, start });
  },

  fetchPredictionSensors(baseURL) {
    const api = axios.create({ baseURL });
    return api.get('api/sensors');
  },

  fetchPredictions(baseURL, deviceId, deviceDir, startTime, endTime, method) {
    const api = axios.create({ baseURL });
    return api.post('api/predict', { deviceId, deviceDir, startTime, endTime, method });
  },

  fetchGTSPredictions(baseURL, deviceId, deviceDir, startTime, endTime, interval) {
    const api = axios.create({ baseURL });
    return api.post('api/predict', { deviceId, deviceDir, startTime, endTime, interval });
  },

  startSimulation(baseURL, params) {
    const api = axios.create({ baseURL });
    return api.post('api/simulate', params);
  },

  fetchAnomalyDevices() {
    return AxiosClient.get('api/anomalyDevices');
  },

  fetchAnomalyResults(start, route, direction) {
    return AxiosClient.post('api/anomalyResult', { start, route, direction });
  },

  fetchAnomalyTimeList(deviceId, direction, start) {
    return AxiosClient.post('api/anomalyTimeList', { deviceId, direction, start });
  },

  fetchAnomalyTimeLists(devices, start) {
    return AxiosClient.post('api/anomalyTimeList', { devices, start });
  },

  fetchAnomalyHourlyResults(deviceId, direction, start, hour) {
    return AxiosClient.post('api/anomalyHourlyResult', { deviceId, direction, start, hour });
  },

  fetchCombinedAnomalyResults(deviceId, direction, start) {
    return AxiosClient.post('api/combinedAnomalyResult', { deviceId, direction, start });
  },

  fetchBluetoothAnomalyDevices() {
    return AxiosClient.get('travelTime/bluetoothAnomalyDevices');
  },

  fetchLatestDeviceInfo(id) {
    return AxiosClient.get('api/latestDeviceInfo', { params: { id } });
  },

  fetchTravelTimeData(linkId, interval, start) {
    return AxiosClient.post('travelTime/travelTimeData', { linkId, interval, start });
  },

  fetchLatestTravelTimeInfo(linkId) {
    return AxiosClient.get('travelTime/latestTravelTimeInfo', { params: { linkId } });
  },

  fetchWeatherStations() {
    return AxiosClient.get('weather/weatherStations');
  },

  fetchBluetoothSegments() {
    return AxiosClient.get('travelTime/bluetoothSegments');
  },

  fetchWeatherData(id, interval, startDate, endDate) {
    let params = { id, interval, startDate };
    if (endDate) {
      params = { ...params, endDate };
    }
    return AxiosClient.post('weather/weatherData', params);
  },

  fetchLatestWeatherData(secondsAgo) {
    return AxiosClient.get('weather/latestWeatherData', { params: { secondsAgo } });
  },

  fetchWeatherDataOneDay(stationId, start) {
    return AxiosClient.post('weather/weatherDataOneDay', { stationId, start });
  },

  fetchLatestWeatherDataInfo(stationId, secondsAgo) {
    return AxiosClient.get('weather/latestWeatherDataInfo', { params: { stationId, secondsAgo } });
  },

  fetchLatestAnomalyData(secondsAgo) {
    return AxiosClient.get('api/latestAnomalyData', { params: { secondsAgo } });
  },

  fetchLatestBluetoothAnomalyData(secondsAgo) {
    return AxiosClient.get('travelTime/latestBluetoothAnomalyData', { params: { secondsAgo } });
  },

  fetchLatestRestrictionData(secondsAgo) {
    return AxiosClient.get('api/latestRestrictionData', { params: { secondsAgo } });
  },

  fetchLatestAnomalySegments(secondsAgo, severity, duration) {
    return AxiosClient.get('api/latestAnomalySegments', { params: { secondsAgo, severity, duration } });
  },

  fetchTravelTimeHeatMapData(route, direction, start, interval) {
    return AxiosClient.post('travelTime/travelTimeHeatMapData', { route, direction, start, interval });
  },

  fetchTravelTimeSegments(route, direction) {
    return AxiosClient.post('travelTime/travelTimeSegments', { route, direction });
  },

  fetchIncidentData(start, type, severity, duration) {
    const video = true;
    return AxiosClient.post('api/incidents', { start, type, video, severity, duration });
  },

  searchIncidentData(queryInput) {
    return AxiosClient.post('api/searchIncidents', queryInput);
  },

  fetchMdistParameters(deviceId, direction, time) {
    return AxiosClient.post('api/mdistParameters', { deviceId, direction, time });
  },

  fetchTrafficFlowVolumeSpeed(id, direction, startDate) {
    let params = { id, direction, startDate };
    return AxiosClient.post('api/trafficFlowVolumeSpeed', params);
  },

  fetchRangedTrafficFlowVolumeSpeed(id, direction, startDate, range) {
    let params = { id, direction, startDate, range };
    return AxiosClient.post('api/trafficFlowVolumeSpeed', params);
  },

  fetchTrafficFlowAnomalyVolumeSpeed(id, direction, startDate) {
    let params = { id, direction, startDate };
    return AxiosClient.post('api/trafficFlowAnomalyVolumeSpeed', params);
  },

  fetchLCMCurve(id, direction, count = 200) {
    let params = { id, direction, count };
    return AxiosClient.post('api/fetchLCMCurve', params);
  },

  fetchTimingPlan(id) {
    return AxiosClient.get('api/timingPlan', { params: { id } });
  },

  fetchSimulationScenarios(type) {
    return AxiosClient.get('api/simulationScenarios', { params: { type } });
  },

  fetchMitigationSolution(incidentId) {
    const params = { incidentId };
    return AxiosClient.post('solution/solutionData', params);
  },

  fetchMitigationRampVolumes(incidentId) {
    const params = { incidentId };
    return AxiosClient.post('solution/rampVolumes', params);
  },

  fetchTrafficResponsiveData(zoneId, startDate) {
    return AxiosClient.post('api/trafficResponsiveData', { zoneId, startDate });
  }
};

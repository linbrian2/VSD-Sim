import axiosDefault from 'axios';

const axios = axiosDefault.create({
  baseURL: process.env.VUE_APP_CAV_API_URL,
});

export default {
  fetchTrips(time, limit) {
    return axios.get('api/trips', { params: { time, limit } });
  },

  fetchLatestTrips(limit) {
    return axios.get('api/latestTrips', { params: { limit } });
  },

  fetchOngoingTrips(limit) {
    return axios.get('api/ongoingTrips', { params: { limit } });
  },

  deleteTrip(tripId) {
    return axios.get('api/deleteTrip', { params: { id: tripId } });
  },

  fetchTripLocations(tripId) {
    return axios.get('api/locations', { params: { id: tripId } });
  },

  fetchTripInfo(tripId) {
    return axios.get('api/tripInfo', { params: { id: tripId } });
  },

  fetchTripData(tripId) {
    return axios.get('api/tripData', { params: { id: tripId } });
  },

  fetchTripGrid(tripId) {
    return axios.get('api/tripGrid', { params: { id: tripId } });
  },

  fetchTripPath(tripId) {
    return axios.get('api/tripPath', { params: { id: tripId } });
  },

  fetchTripSegment(tripId) {
    return axios.get('api/tripSegment', { params: { id: tripId } });
  },

  fetchTripStat(tripId) {
    return axios.get('api/tripStat', { params: { id: tripId } });
  }
};

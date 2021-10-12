import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_CAV_API_URL);

export default {
  fetchTrips(time, limit) {
    return AxiosClient.get('api/trips', { params: { time, limit } });
  },

  fetchLatestTrips(limit) {
    return AxiosClient.get('api/latestTrips', { params: { limit } });
  },

  fetchOngoingTrips(limit) {
    return AxiosClient.get('api/ongoingTrips', { params: { limit } });
  },

  deleteTrip(tripId) {
    return AxiosClient.get('api/deleteTrip', { params: { id: tripId } });
  },

  fetchTripLocations(tripId) {
    return AxiosClient.get('api/locations', { params: { id: tripId } });
  },

  fetchTripInfo(tripId) {
    return AxiosClient.get('api/tripInfo', { params: { id: tripId } });
  },

  fetchTripData(tripId) {
    return AxiosClient.get('api/tripData', { params: { id: tripId } });
  },

  fetchTripGrid(tripId) {
    return AxiosClient.get('api/tripGrid', { params: { id: tripId } });
  },

  fetchTripPath(tripId) {
    return AxiosClient.get('api/tripPath', { params: { id: tripId } });
  },

  fetchTripSegment(tripId) {
    return AxiosClient.get('api/tripSegment', { params: { id: tripId } });
  },

  fetchTripStat(tripId) {
    return AxiosClient.get('api/tripStat', { params: { id: tripId } });
  }
};

import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_AUTH_API_URL);

export default {
  addSubscription(subscription) {
    return AxiosClient.post('subscription/add-subscription', subscription);
  },

  removeSubscription(subscription) {
    return AxiosClient.post('subscription/remove-subscription', subscription);
  },

  testSubscription(subscription) {
    return AxiosClient.post('subscription/test-subscription', subscription);
  }
};

import client from '@/utils/AxiosClient';

const AxiosClient = client(process.env.VUE_APP_REID_API_URL);

export default {
  fetchSnippets() {
    return AxiosClient.get('getupdate').then(res => {
      return res.data;
    });
  }
};

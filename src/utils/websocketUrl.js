import Utils from '@/utils/Utils';
const websocketUrl = process.env.VUE_APP_CAV_WS_URL || Utils.getWebSocketUrl('cavws');
export { websocketUrl };

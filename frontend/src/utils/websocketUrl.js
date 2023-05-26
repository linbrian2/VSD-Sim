import Utils from '@/utils/Utils';
const trafficWebsocketUrl = process.env.VUE_APP_WS_URL || Utils.getWebSocketUrl('ws');
const cavWebsocketUrl = process.env.VUE_APP_CAV_WS_URL || Utils.getWebSocketUrl('cavws');
const hrWebsocketUrl = process.env.VUE_APP_HR_WS_URL || Utils.getWebSocketUrl('hrws');

export { trafficWebsocketUrl, cavWebsocketUrl, hrWebsocketUrl };

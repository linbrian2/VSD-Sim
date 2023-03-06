import Constants from '@/utils/constants/traffic';
import Utils from '@/utils/Utils';

export default {
  compose(type, data, lastMessage) {
    let message = null;

    switch (type) {
      case Constants.DATA_TRAFFIC_FLOW:
        {
          const anomalyDevices = data.filter(item => item.status > 0);
          const count = anomalyDevices.length + (lastMessage ? lastMessage.count : 0);
          message = {
            icon: Constants.DEVICE_TRAFFIC_ICON,
            color: 'red',
            title: 'Traffic flow anomaly detected',
            count,
            id: anomalyDevices.length > 0 ? anomalyDevices[0].deviceId : '',
            message: `${count} anomaly detectors`
          };
        }
        break;
      case Constants.DATA_TRAVEL_TIME:
        {
          const count = data.length + (lastMessage ? lastMessage.count : 0);
          message = {
            icon: Constants.DEVICE_BLUETOOTH_ICON,
            color: 'blue',
            title: 'Travel time anomaly detected',
            count,
            id: data.length > 0 ? data[0].linkId : '',
            message: `${count} anomaly segments`
          };
        }
        break;
      case Constants.DATA_WAZE_ALERTS:
        {
          const alerts = data.length > 0 ? JSON.parse(data[0].data) : [];
          const count = alerts.length + (lastMessage ? lastMessage.count : 0);
          message = {
            icon: Constants.DEVICE_WAZE_ICON,
            color: 'teal',
            title: 'Waze alerts received',
            count,
            id: alerts.length > 0 ? alerts[0].id : '',
            message: `${count} waze alerts`
          };
        }
        break;
      case Constants.DATA_RESTRICTION:
        {
          const count = data.length + (lastMessage ? lastMessage.count : 0);
          message = {
            icon: Constants.DEVICE_RESTRICTIONS_ICON,
            color: 'brown',
            title: 'Traffic restrictions received',
            count,
            id: data.length > 0 ? data[0].restrictionId : '',
            message: `${count} restrictions`
          };
        }
        break;
      case Constants.DATA_WEATHER:
        {
          const count = data.length + (lastMessage ? lastMessage.count : 0);
          message = {
            icon: Constants.DEVICE_WEATHER_ICON,
            color: 'green',
            title: 'Weather data received',
            count,
            id: data.length > 0 ? data[0].stationId : '',
            message: `${count} weather stations`
          };
        }
        break;
      case Constants.DATA_TRAFFIC_INCIDENT:
        {
          const count = data.length + (lastMessage ? lastMessage.count : 0);
          message = {
            icon: Constants.TRAFFIC_INCIDENT_ICON,
            color: 'purple',
            title: 'Traffic incident received',
            count,
            id: data.length > 0 ? data[0].id : '',
            message: `${count} incidents`
          };
        }
        break;
      case Constants.DATA_MITIGATION:
        {
          message = {
            icon: Constants.TRAFFIC_MITIGATION_ICON,
            color: 'lime',
            title: 'Incident mitigation result received',
            data
          };
        }
        break;
      case Constants.DATA_SIMULATION:
        {
          message = {
            icon: Constants.TRAFFIC_SIMULATION_ICON,
            color: 'blue-grey',
            title: 'Incident simulation progress received',
            data
          };
        }
        break;
    }

    return { type, time: new Date(), ...message };
  },

  isSameKind(messages, type) {
    let isSameKind = false;
    if (messages.length > 0) {
      const lastMessage = messages[messages.length - 1];
      if (lastMessage.type === type && Utils.getSecondsTillNow(lastMessage.time.getTime()) < 10) {
        isSameKind = true;
      }
    }
    return isSameKind;
  }
};

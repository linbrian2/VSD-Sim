export default Object.freeze({
  CHART_HEIGHT: 560,

  DEVICE_TRAFFIC: 'Traffic Detectors',
  DEVICE_BLUETOOTH: 'Bluetooth Sensors',
  DEVICE_WEATHER: 'Weather Stations',
  DEVICE_RESTRICTIONS: 'Travel Restrictions',
  DEVICE_SEGMENTS: 'Anomaly Segments',

  DEVICE_TRAFFIC_ICON: 'mdi-car-multiple',
  DEVICE_BLUETOOTH_ICON: 'mdi-bluetooth-connect',
  DEVICE_WEATHER_ICON: 'mdi-weather-cloudy',
  DEVICE_RESTRICTIONS_ICON: 'mdi-traffic-cone',
  DEVICE_WAZE_ICON: 'mdi-waze',
  DEVICE_ALERT_ICON: 'mdi-alert-circle-outline',
  DEVICE_SEGMENTS_ICON: 'mdi-vector-line',

  INFO_BAR_DEFAULT_COLOR: 'white',

  ADD_MAP_LAYER: 0,
  REMOVE_MAP_LAYER: 1,

  LAYER_DEVICE_TRAFFIC: 0,
  LAYER_DEVICE_BLUETOOTH: 1,
  LAYER_DEVICE_WEATHER: 2,
  LAYER_DEVICE_RESTRICTIONS: 3,
  LAYER_DEVICE_SEGMENTS: 4,
  LAYER_TOTAL_COUNT: 5,

  DATA_TRAFFIC_FLOW: 1,
  DATA_TRAVEL_TIME: 2,
  DATA_WAZE_ALERTS: 3,
  DATA_RESTRICTION: 4,
  DATA_WEATHER: 5,
  DATA_TRAFFIC_ALERT: 6,
  DATA_TRAFFIC_VIDEO: 7,

  TRAFFIC_DEVICE_CATEGORIES: [
    { title: 'All Detectors', value: -1 },
    { title: 'Urban Freeway Detectors', value: 1 },
    { title: 'Urban System Detectors', value: 2 },
    { title: 'CAV Area Freeway Detectors', value: 3 },
    { title: 'CAV Area System Detectors', value: 4 },
    { title: 'Beach Area System Detectors', value: 7 }
  ]
});

export default Object.freeze({
  DEVICE_TRAFFIC: 'Traffic flow Detectors',
  DEVICE_BLUETOOTH: 'Bluetooth Sensors',
  DEVICE_WEATHER: 'Weather Stations',
  DEVICE_RESTRICTIONS: 'Travel Restrictions',
  DEVICE_SEGMENTS: 'Anomaly Segments',
  DEVICE_SIGNALS: 'Traffic Signals',
  DEVICE_WAZE_ALERTS: 'Waze Alerts',
  DEVICE_CAMERAS: 'Traffic Cameras',
  DEVICE_INCIDENTS: 'Traffic Incidents',
  DEVICE_CONGESTED_SEGMENTS: 'Congested Segments',
  DEVICE_ANOMALY_DEVICES: 'Detectors with issues',

  DEVICE_TRAFFIC_ICON: 'mdi-car-multiple',
  DEVICE_BLUETOOTH_ICON: 'mdi-bluetooth-connect',
  DEVICE_WEATHER_ICON: 'mdi-weather-cloudy',
  DEVICE_RESTRICTIONS_ICON: 'mdi-traffic-cone',
  DEVICE_WAZE_ICON: 'mdi-waze',
  DEVICE_ALERT_ICON: 'mdi-alert-circle-outline',
  DEVICE_SEGMENTS_ICON: 'mdi-vector-line',
  DEVICE_TRAFFIC_CAMERA: 'mdi-cctv',
  TRAFFIC_INCIDENT_ICON: 'mdi-alert-octagram-outline',
  TRAFFIC_MITIGATION_ICON: 'mdi-traffic-light',
  TRAFFIC_SIMULATION_ICON: 'mdi-map-legend',

  INFO_BAR_DEFAULT_COLOR: 'white',

  LAYER_DEVICE_TRAFFIC: 0,
  LAYER_DEVICE_BLUETOOTH: 1,
  LAYER_DEVICE_WEATHER: 2,
  LAYER_DEVICE_RESTRICTIONS: 3,
  LAYER_DEVICE_SIGNALS: 5,
  LAYER_DEVICE_WAZE_ALERTS: 6,
  LAYER_DEVICE_CONGESTED_SEGMENTS: 7,
  LAYER_DEVICE_ANOMALY_DEVICES: 8,
  LAYER_DEVICE_INCIDENTS: 9,
  LAYER_DEVICE_CAMERAS: 10,

  DATA_TRAFFIC_FLOW: 1,
  DATA_TRAVEL_TIME: 2,
  DATA_WAZE_ALERTS: 3,
  DATA_RESTRICTION: 4,
  DATA_WEATHER: 5,
  DATA_TRAFFIC_ALERT: 6,
  DATA_TRAFFIC_VIDEO: 7,
  DATA_TRAFFIC_INCIDENT: 8,
  DATA_MITIGATION: 9,
  DATA_SIMULATION: 10,

  TRAFFIC_DEVICE_CATEGORIES: [
    { title: 'All Detectors', value: -1 },
    { title: 'Urban Freeway Detectors', value: 1 },
    { title: 'Urban System Detectors', value: 2 },
    { title: 'CAV Area Freeway Detectors', value: 3 },
    { title: 'CAV Area System Detectors', value: 4 },
    { title: 'Beach Area Detectors', value: 7 }
  ],

  TRAFFIC_DEVICE_REGIONS: [
    { title: 'All Regions', value: -1 },
    { title: 'Urban Area', value: 10 },
    { title: 'CAV Area', value: 20 },
    { title: 'Beach Area', value: 30 }
  ],

  TRAFFIC_DEVICE_TYPES: [
    { title: 'All Sensor Types', value: -1 },
    { title: 'System Detectors', value: 10 },
    { title: 'Fixed Wavetronix', value: 21 },
    { title: 'Portable Wavetronix', value: 22 }
  ],

  SIGNAL_DEVICE_CATEGORIES: [
    { title: 'All Signals', value: -1 },
    { divider: true },
    { title: 'New Castle County Signals', value: 1 },
    { title: 'Kent County Signals', value: 2 },
    { title: 'Sussex County Signals', value: 3 },
    { title: 'Wilmington City Signals', value: 4 },
    { divider: true },
    { title: 'TR Enabled Signals', value: 11 }
  ]
});

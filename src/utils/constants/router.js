export const RouterNames = {
  DASHBOARD: 'Dashboard',
  MAIN_DASHBOARD: 'Main',
  TRAFFIC_DASHBOARD: 'Traffic',
  BLUETOOTH_DASHBOARD: 'Bluetooth',

  TRAFFIC_FLOW: 'Traffic Flow Data',
  TRAFFIC_LCM: 'Traffic LCM',
  TRAFFIC_ROUTING: 'Traffic Routing',
  TRAFFIC_PREDICT: 'Traffic Flow Prediction',
  TRAFFIC_GTS_PREDICT: 'GTS Volume Prediction',
  TRAFFIC_ANOMALY: 'Anomaly Map',
  TRAFFIC_WEATHER: 'Weather Data',
  TRAFFIC_MULTIGRAPH: 'Multigraph',
  TRAVEL_TIME_DATA: 'Travel Time Data',
  TRAVEL_TIME_MAP: 'Travel Time Map',
  TRAFFIC_INCIDENT: 'Traffic Incidents',
  TRAFFIC_MITIGATION: 'Incident Mitigation',

  HR_DASHBOARD: "Today's Summary",
  HR_PCD: 'PCD Charts',
  HR_TIME_SPACE_DIAGRAM: 'Time Space Diagram',
  HR_SIGNAL: 'Signal Timing Playback',
  HR_SIGNAL_LIVE: 'Signal Timing Live',
  HR_PERFORMANCE_MEASURES: 'Performance Measures',
  HR_MULTI_PCD: 'Corridor PCD Charts',
  HR_MULTI_OCC: 'Corridor Occupancy Charts',

  VISION_DASHBOARD: 'Traffic Cameras',
  VISION_REID: 'Vehicle Re-ID',

  CAV_DASHBOARD: 'Trip Data',
  CAV_STATUS: 'Trip Status',

  STATUS_DASHBOARD: 'Status Check',
  STATUS_QUALITY_CHECK: 'Quality Check',

  AUTH_LOGIN: 'Login',
  AUTH_CHANGE_PASSWORD: 'ChangePassword',
  AUTH_RESET_PASSWORD: 'ResetPassword',
  AUTH_FORGOT_PASSWORD: 'ForgotPassword',
  AUTH_PREFERENCES: 'Preferences'
};

export const RouterPaths = {
  DASHBOARD_ROOT: '/dash',
  MAIN_DASHBOARD: '/dash/main',
  TRAFFIC_DASHBOARD: '/dash/traffic',
  BLUETOOTH_DASHBOARD: '/dash/bluetooth',

  TRAFFIC_ROOT: '/flow',
  TRAFFIC_FLOW: '/flow/data',
  TRAFFIC_LCM: '/flow/lcm',
  TRAFFIC_ROUTING: '/flow/routing',
  TRAFFIC_PREDICT: '/flow/predict',
  TRAFFIC_GTS_PREDICT: '/flow/gts-predict',
  TRAFFIC_ANOMALY: '/flow/anomaly',
  TRAFFIC_WEATHER: '/flow/weather',
  TRAFFIC_MULTIGRAPH: '/flow/multigraph',
  TRAVEL_TIME_DATA: '/flow/travel-time-data',
  TRAVEL_TIME_MAP: '/flow/travel-time-map',
  TRAFFIC_INCIDENT: '/flow/incidents',
  TRAFFIC_MITIGATION_PATH: '/flow/mitigation/:id',
  TRAFFIC_MITIGATION: '/flow/mitigation',

  HR_ROOT: '/hr',
  HR_DASHBOARD: '/hr/summary',
  HR_PCD: '/hr/pcd',
  HR_TIME_SPACE_DIAGRAM: '/hr/tsd',
  HR_SIGNAL: '/hr/signal-play',
  HR_SIGNAL_LIVE: '/hr/signal-live',
  HR_PERFORMANCE_MEASURES: '/hr/measures',
  HR_MULTI_PCD: '/hr/pcds',
  HR_MULTI_OCC: '/hr/occs',

  VISION_ROOT: '/vision',
  VISION_DASHBOARD: '/vision/camera',
  VISION_REID: '/vision/re-id',

  CAV_ROOT: '/cav',
  CAV_DASHBOARD: '/cav/trip',
  CAV_STATUS: '/cav/status',

  STATUS_ROOT: '/status',
  STATUS_DASHBOARD: '/status/check',
  STATUS_QUALITY_CHECK: '/status/quality',

  AUTH_LOGIN: '/login',
  AUTH_CHANGE_PASSWORD: '/change-password',
  AUTH_RESET_PASSWORD: '/account/reset-password',
  AUTH_FORGOT_PASSWORD: '/forgot-password',
  AUTH_PREFERENCES: '/pref'
};

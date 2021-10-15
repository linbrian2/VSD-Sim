export const RouterNames = {
  TRAFFIC_DASHBOARD: 'Dashboard',
  TRAFFIC_FLOW: 'Flow Charts',
  TRAFFIC_LCM: 'Traffic LCM',
  TRAFFIC_PREDICT: 'Flow Prediction',
  TRAFFIC_ANOMALY: 'Anomaly Map',
  TRAFFIC_WEATHER: 'Weather Data',
  TRAVEL_TIME_DATA: 'Travel Time Data',
  TRAVEL_TIME_MAP: 'Travel Time Map',
  TRAFFIC_INCIDENT: 'Traffic Incidents',

  HR_DASHBOARD: "Today's Summary",
  HR_PCD: 'PCD',
  HR_SIGNAL: 'Signal Playback',
  HR_PERFORMANCE_MEASURES: 'Performance Measures',
  HR_MULTI_PCD: 'Corridor PCD Charts',
  HR_MULTI_OCC: 'Corridor Occupancy Charts',

  VISION_DASHBOARD: 'Traffic Cameras',
  VISION_REID: 'Vehicle Re-ID',

  BLUETOOTH_DASHBOARD: 'Bluetooth',

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
  TRAFFIC_ROOT: '/flow',
  TRAFFIC_DASHBOARD: '/flow/dash',
  TRAFFIC_FLOW: '/flow/data',
  TRAFFIC_LCM: '/flow/lcm',
  TRAFFIC_PREDICT: '/flow/predict',
  TRAFFIC_ANOMALY: '/flow/anomaly',
  TRAFFIC_WEATHER: '/flow/weather',
  TRAVEL_TIME_DATA: '/flow/travel-time-data',
  TRAVEL_TIME_MAP: '/flow/travel-time-map',
  TRAFFIC_INCIDENT: '/flow/incidents',

  HR_ROOT: '/hr',
  HR_DASHBOARD: '/hr/summary',
  HR_PCD: '/hr/pcd',
  HR_SIGNAL: '/hr/signal',
  HR_PERFORMANCE_MEASURES: '/hr/measures',
  HR_MULTI_PCD: '/hr/pcds',
  HR_MULTI_OCC: '/hr/occs',

  VISION_ROOT: '/vision',
  VISION_DASHBOARD: '/vision/camera',
  VISION_REID: '/vision/re-id',

  BLUETOOTH_DASHBOARD: '/bluetooth',

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

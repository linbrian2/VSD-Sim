export const RouterNames = {
  DASHBOARD: 'Dashboard',
  TRAFFIC_DASHBOARD: 'Traffic',
  TRAFFIC_DATA_PLAYBACK: 'Traffic Data Playback',

  TRAFFIC_FLOW: 'Traffic Data',
  TRAFFIC_FLOW_DATA: 'Traffic Flow',
  TRAFFIC_LCM: 'Traffic LCM',
  TRAFFIC_ROUTING: 'Traffic Routing',
  TRAFFIC_PREDICT: 'Traffic Flow Prediction',

  TRAFFIC_ANOMALY: 'Traffic Anomaly Map',
  TRAFFIC_WEATHER: 'Weather Data',
  TRAFFIC_MULTIGRAPH: 'Traffic Data Viewer',
  TRAFFIC_MULTIGRAPH_SELECT: 'Traffic Data Viewer',
  TRAVEL_TIME_DATA: 'Travel Time Data',
  TRAVEL_TIME_MAP: 'Travel Time Map',
  TRAFFIC_INCIDENT_LIST: 'Traffic Incidents',
  TRAFFIC_INCIDENT_PATH: 'Traffic Incident',
  TRAFFIC_MITIGATION: 'Incident Mitigation',
  TRAFFIC_RESPONSIVE_DATA: 'Traffic Responsive Zone',
  TRAFFIC_SIGNALS: 'Traffic Signals',

  HR_DASHBOARD: "Today's Summary",
  HR_PCD: 'PCD Charts',
  HR_TIME_SPACE_DIAGRAM: 'Time Space Diagram',
  HR_SIGNAL_DISPLAY: 'Signal Timing',
  HR_PERFORMANCE_MEASURES: 'Performance Measures',
  HR_MULTI_PCD: 'Corridor PCD Charts',
  HR_MULTI_OCC: 'Corridor Occupancy Charts',

  VISION_DASHBOARD: 'Traffic Cameras',
  VISION_MULTIVIEW: 'Camera Multiview',
  VISION_REID: 'Vehicle Re-ID',

  CAV_DASHBOARD: 'Trip Data',
  CAV_STATUS: 'Trip Status',
  CAV_CSPI: 'Trip CSPI',

  STATUS_DASHBOARD: 'Status Check',
  STATUS_QUALITY_CHECK: 'Quality Check',
  STATUS_QUALITY_MAP: 'Detector Quality Map',

  AUTH_LOGIN: 'Login',
  AUTH_CHANGE_PASSWORD: 'ChangePassword',
  AUTH_RESET_PASSWORD: 'ResetPassword',
  AUTH_FORGOT_PASSWORD: 'ForgotPassword',
  AUTH_PREFERENCES: 'Preferences'
};

export const RouterPaths = {
  DASHBOARD_ROOT: '/dash',
  TRAFFIC_DASHBOARD: '/dashboard',

  TRAFFIC_ROOT: '/flow',
  TRAFFIC_FLOW: '/flow/data',
  TRAFFIC_LCM: '/flow/lcm',
  TRAFFIC_ROUTING: '/flow/routing',
  TRAFFIC_PREDICT: '/flow/predict',
  TRAFFIC_ANOMALY: '/flow/anomaly',
  TRAFFIC_WEATHER: '/flow/weather',
  TRAFFIC_MULTIGRAPH: '/flow/graph',
  TRAFFIC_FLOW_DATA: '/flow/traffic-flow/:id?',
  TRAVEL_TIME_DATA: '/flow/travel-time-data',
  TRAVEL_TIME_MAP: '/flow/travel-time-map',
  TRAFFIC_INCIDENT_LIST: '/flow/incidents',
  TRAFFIC_INCIDENT_LIST_ID: '/flow/incidents/:id',
  TRAFFIC_INCIDENT_PATH: '/in/:id',
  TRAFFIC_MITIGATION_PATH: '/flow/mitigation/:id',
  TRAFFIC_MITIGATION: '/flow/mitigation',
  TRAFFIC_RESPONSIVE_DATA: '/flow/tr',
  TRAFFIC_SIGNALS: '/flow/signals',
  TRAFFIC_DATA_PLAYBACK: '/flow/playback',
  STATUS_QUALITY_MAP: '/flow/quality-map',

  HR_ROOT: '/hr',
  HR_DASHBOARD: '/hr/summary',
  HR_PCD: '/hr/pcd',
  HR_TIME_SPACE_DIAGRAM: '/hr/tsd',
  HR_SIGNAL_DISPLAY: '/hr/signal',
  HR_PERFORMANCE_MEASURES: '/hr/measures',
  HR_MULTI_PCD: '/hr/pcds',
  HR_MULTI_OCC: '/hr/occs',

  VISION_ROOT: '/vision',
  VISION_DASHBOARD: '/vision/camera',
  VISION_MULTIVIEW: '/vision/multiview',
  VISION_REID: '/vision/re-id',

  CAV_ROOT: '/cav',
  CAV_DASHBOARD: '/cav/trip',
  CAV_STATUS: '/cav/status',
  CAV_CSPI: '/cav/cspi',

  STATUS_ROOT: '/status',
  STATUS_DASHBOARD: '/status/check',
  STATUS_QUALITY_CHECK: '/status/quality',

  AUTH_LOGIN: '/login',
  AUTH_CHANGE_PASSWORD: '/change-password',
  AUTH_RESET_PASSWORD: '/account/reset-password',
  AUTH_FORGOT_PASSWORD: '/forgot-password',
  AUTH_PREFERENCES: '/pref'
};

export const RouterIcons = {
  DASHBOARD_ROOT: 'mdi-view-dashboard',
  TRAFFIC_DASHBOARD: 'mdi-view-dashboard',
  TRAFFIC_LCM: 'mdi-chart-bell-curve',
  TRAFFIC_ROUTING: 'mdi-vector-radius',
  TRAFFIC_PREDICT: 'mdi-waveform',
  TRAFFIC_ANOMALY: 'mdi-view-comfy',
  TRAFFIC_WEATHER: 'mdi-chart-line',
  TRAFFIC_MULTIGRAPH: 'mdi-chart-line',
  TRAVEL_TIME_DATA: 'mdi-chart-line',
  TRAVEL_TIME_MAP: 'mdi-view-comfy',
  TRAFFIC_INCIDENT_LIST: 'mdi-format-list-bulleted',
  TRAFFIC_RESPONSIVE_DATA: 'mdi-traffic-light-outline',
  TRAFFIC_SIGNALS: 'mdi-traffic-light-outline',
  TRAFFIC_DATA_PLAYBACK: 'mdi-motion-play-outline',

  EXPERIMENTAL_ICON: 'mdi-lightning-bolt',

  HR_DASHBOARD: 'mdi-clipboard-list-outline',
  HR_PCD: 'mdi-chart-areaspline',
  HR_TIME_SPACE_DIAGRAM: 'mdi-chart-timeline',
  HR_SIGNAL_DISPLAY: 'mdi-map-clock-outline',
  HR_PERFORMANCE_MEASURES: 'mdi-chart-bar',

  VISION_DASHBOARD: 'mdi-cctv',
  VISION_MULTIVIEW: 'mdi-table',
  VISION_REID: 'mdi-car-2-plus',

  CAV_DASHBOARD: 'mdi-format-list-bulleted',
  CAV_STATUS: 'mdi-truck-check-outline',
  CAV_CSPI: 'mdi-counter',

  STATUS_DASHBOARD: 'mdi-list-status',
  STATUS_QUALITY_CHECK: 'mdi-check-decagram',
  STATUS_QUALITY_MAP: 'mdi-map-check-outline'
};

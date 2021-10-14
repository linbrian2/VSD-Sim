import { RouterNames, RouterPaths } from '@/utils/constants/router';

const protectedRoute = [
  {
    path: '/',
    meta: {
      title: 'home',
      icon: ''
    },
    children: [
      {
        path: '/',
        name: RouterNames.TRAFFIC_DASHBOARD,
        meta: {
          title: 'Dashboard',
          icon: 'mdi-view-dashboard'
        }
      },

      //Traffic Flow
      {
        path: RouterPaths.TRAFFIC_DASHBOARD,
        meta: {
          title: 'Traffic Flow Data',
          icon: 'flowIcon'
        },
        children: [
          {
            path: RouterPaths.TRAFFIC_DASHBOARD,
            name: RouterNames.TRAFFIC_DASHBOARD,
            meta: {
              title: 'Dashboard',
              icon: 'mdi-view-dashboard'
            }
          },
          {
            path: RouterPaths.TRAFFIC_FLOW,
            name: RouterNames.TRAFFIC_FLOW,
            meta: {
              title: 'Traffic Flow Charts',
              icon: 'mdi-chart-line'
            }
          },
          {
            path: RouterPaths.TRAVEL_TIME_DATA,
            name: RouterNames.TRAVEL_TIME_DATA,
            meta: {
              title: 'Travel Time Charts',
              icon: 'mdi-chart-line'
            }
          },
          {
            path: RouterPaths.TRAFFIC_WEATHER,
            name: RouterNames.TRAFFIC_WEATHER,
            meta: {
              title: 'Weather Data Charts',
              icon: 'mdi-chart-line'
            }
          },
          {
            path: RouterPaths.TRAFFIC_ANOMALY,
            name: RouterNames.TRAFFIC_ANOMALY,
            meta: {
              title: 'Traffic Anomaly Map',
              icon: 'mdi-view-comfy'
            }
          },
          {
            path: RouterPaths.TRAVEL_TIME_MAP,
            name: RouterNames.TRAVEL_TIME_MAP,
            meta: {
              title: 'Travel Time Map',
              icon: 'mdi-view-comfy'
            }
          },
          {
            path: RouterPaths.TRAFFIC_INCIDENT,
            name: RouterNames.TRAFFIC_INCIDENT,
            meta: {
              title: 'Traffic Incident List',
              icon: 'mdi-format-list-bulleted '
            }
          },
          {
            path: RouterPaths.TRAFFIC_PREDICT,
            name: RouterNames.TRAFFIC_PREDICT,
            meta: {
              title: 'Traffic Flow Prediction',
              icon: 'mdi-waveform'
            }
          },
          {
            path: RouterPaths.TRAFFIC_LCM,
            name: RouterNames.TRAFFIC_LCM,
            meta: {
              title: 'Traffic LCM',
              icon: 'mdi-chart-bell-curve '
            }
          }
        ]
      },
      //HR
      {
        path: RouterPaths.HR_DASHBOARD,
        meta: {
          title: 'High Resolution Data',
          icon: 'hrIcon'
        },
        children: [
          {
            path: RouterPaths.HR_DASHBOARD,
            name: RouterNames.HR_DASHBOARD,
            meta: {
              title: "Today's Summary",
              icon: 'mdi-clipboard-list-outline '
            }
          },
          {
            path: RouterPaths.HR_PCD,
            name: RouterNames.HR_PCD,
            meta: {
              title: 'Purdue Coordination Diagram',
              icon: 'mdi-chart-areaspline'
            }
          },
          {
            path: RouterPaths.HR_SIGNAL,
            name: RouterNames.HR_SIGNAL,
            meta: {
              title: 'Signal Timing',
              icon: 'mdi-clock'
            }
          },
          {
            path: RouterPaths.HR_PERFORMANCE_MEASURES,
            name: RouterNames.HR_PERFORMANCE_MEASURES,
            meta: {
              title: 'Performance Measures',
              icon: 'mdi-chart-bar'
            }
          },
          {
            path: RouterPaths.HR_MULTI_PCD,
            name: RouterNames.HR_MULTI_PCD,
            meta: {
              title: 'Corridor PCD Charts',
              icon: 'mdi-chart-gantt'
            }
          },
          {
            path: RouterPaths.HR_MULTI_OCC,
            name: RouterNames.HR_MULTI_OCC,
            meta: {
              title: 'Corridor Occupancy Charts',
              icon: 'mdi-chart-gantt'
            }
          }
        ]
      },
      //Machine Vision
      {
        path: RouterPaths.VISION_DASHBOARD,
        meta: {
          title: 'Machine Vision',
          icon: 'visionIcon'
        },
        children: [
          {
            path: RouterPaths.VISION_DASHBOARD,
            name: RouterNames.VISION_DASHBOARD,
            meta: {
              title: 'Traffic Cameras',
              icon: 'mdi-cctv'
            }
          },
          {
            path: RouterPaths.VISION_REID,
            name: RouterNames.VISION_REID,
            meta: {
              title: 'Vehicle Re-ID',
              icon: 'mdi-car-2-plus'
            }
          }
        ]
      },
      //Bluetooth
      {
        path: RouterPaths.BLUETOOTH_DASHBOARD,
        meta: {
          title: 'Bluetooth and Waze Data',
          icon: 'btIcon'
        }
      },
      //CAV
      {
        path: RouterPaths.CAV_DASHBOARD,
        meta: {
          title: 'CAV Data',
          icon: 'cavIcon'
        },
        children: [
          {
            path: RouterPaths.CAV_DASHBOARD,
            name: RouterNames.CAV_DASHBOARD,
            meta: {
              title: 'CAV Trip Data',
              icon: 'mdi-format-list-bulleted'
            }
          },
          {
            path: RouterPaths.CAV_STATUS,
            name: RouterNames.CAV_STATUS,
            meta: {
              title: 'Real Time Status',
              icon: 'mdi-truck-check-outline'
            }
          }
        ]
      },
      //Health
      {
        path: RouterPaths.STATUS_DASHBOARD,
        meta: {
          title: 'System Monitoring',
          icon: 'healthIcon'
        },
        children: [
          {
            path: RouterPaths.STATUS_DASHBOARD,
            name: RouterNames.STATUS_DASHBOARD,
            meta: {
              title: 'System Health Check',
              icon: 'mdi-list-status'
            }
          },
          {
            path: RouterPaths.STATUS_QUALITY_CHECK,
            name: RouterNames.STATUS_QUALITY_CHECK,
            meta: {
              title: 'Traffic Flow Quality Check',
              icon: 'mdi-check-decagram'
            }
          }
        ]
      }
    ]
  }
];

export default protectedRoute;

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
          title: RouterNames.TRAFFIC_DASHBOARD,
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
              title: RouterNames.TRAFFIC_DASHBOARD,
              icon: 'mdi-view-dashboard'
            }
          },
          {
            path: RouterPaths.TRAFFIC_BT_DASHBOARD,
            name: RouterNames.TRAFFIC_BT_DASHBOARD,
            meta: {
              title: RouterNames.TRAFFIC_BT_DASHBOARD,
              icon: 'mdi-view-dashboard'
            }
          },
          {
            path: RouterPaths.TRAFFIC_FLOW,
            name: RouterNames.TRAFFIC_FLOW,
            meta: {
              title: RouterNames.TRAFFIC_FLOW,
              icon: 'mdi-chart-line'
            }
          },
          {
            path: RouterPaths.TRAVEL_TIME_DATA,
            name: RouterNames.TRAVEL_TIME_DATA,
            meta: {
              title: RouterNames.TRAVEL_TIME_DATA,
              icon: 'mdi-chart-line'
            }
          },
          {
            path: RouterPaths.TRAFFIC_WEATHER,
            name: RouterNames.TRAFFIC_WEATHER,
            meta: {
              title: RouterNames.TRAFFIC_WEATHER,
              icon: 'mdi-chart-line'
            }
          },
          {
            path: RouterPaths.TRAFFIC_MULTIGRAPH,
            name: RouterNames.TRAFFIC_MULTIGRAPH,
            meta: {
              title: RouterNames.TRAFFIC_MULTIGRAPH,
              icon: 'mdi-chart-line'
            }
          },
          {
            path: RouterPaths.TRAFFIC_ANOMALY,
            name: RouterNames.TRAFFIC_ANOMALY,
            meta: {
              title: RouterNames.TRAFFIC_ANOMALY,
              icon: 'mdi-view-comfy'
            }
          },
          {
            path: RouterPaths.TRAVEL_TIME_MAP,
            name: RouterNames.TRAVEL_TIME_MAP,
            meta: {
              title: RouterNames.TRAVEL_TIME_MAP,
              icon: 'mdi-view-comfy'
            }
          },
          {
            path: RouterPaths.TRAFFIC_INCIDENT,
            name: RouterNames.TRAFFIC_INCIDENT,
            meta: {
              title: RouterNames.TRAFFIC_INCIDENT,
              icon: 'mdi-format-list-bulleted '
            }
          },
          {
            path: RouterPaths.TRAFFIC_PREDICT,
            name: RouterNames.TRAFFIC_PREDICT,
            meta: {
              title: RouterNames.TRAFFIC_PREDICT,
              icon: 'mdi-waveform'
            }
          },
          {
            path: RouterPaths.TRAFFIC_LCM,
            name: RouterNames.TRAFFIC_LCM,
            meta: {
              title: RouterNames.TRAFFIC_LCM,
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
              title: RouterNames.HR_DASHBOARD,
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
            path: RouterPaths.HR_TIME_SPACE_DIAGRAM,
            name: RouterNames.HR_TIME_SPACE_DIAGRAM,
            meta: {
              title: RouterNames.HR_TIME_SPACE_DIAGRAM,
              icon: 'mdi-chart-timeline'
            }
          },
          {
            path: RouterPaths.HR_SIGNAL,
            name: RouterNames.HR_SIGNAL,
            meta: {
              title: RouterNames.HR_SIGNAL,
              icon: 'mdi-clock'
            }
          },
          {
            path: RouterPaths.HR_PERFORMANCE_MEASURES,
            name: RouterNames.HR_PERFORMANCE_MEASURES,
            meta: {
              title: RouterNames.HR_PERFORMANCE_MEASURES,
              icon: 'mdi-chart-bar'
            }
          },
          {
            path: RouterPaths.HR_MULTI_PCD,
            name: RouterNames.HR_MULTI_PCD,
            meta: {
              title: RouterNames.HR_MULTI_PCD,
              icon: 'mdi-chart-gantt'
            }
          },
          {
            path: RouterPaths.HR_MULTI_OCC,
            name: RouterNames.HR_MULTI_OCC,
            meta: {
              title: RouterNames.HR_MULTI_OCC,
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
              title: RouterNames.VISION_DASHBOARD,
              icon: 'mdi-cctv'
            }
          },
          {
            path: RouterPaths.VISION_REID,
            name: RouterNames.VISION_REID,
            meta: {
              title: RouterNames.VISION_REID,
              icon: 'mdi-car-2-plus'
            }
          }
        ]
      },
      // Traffic Routing
      {
        path: RouterPaths.TRAFFIC_ROUTING_DASHBOARD,
        meta: {
          title: 'Traffic Routing',
          icon: 'mdi-vector-radius'
        }
      },
      //Bluetooth
      {
        path: RouterPaths.BLUETOOTH_DASHBOARD,
        meta: {
          title: 'Bluetooth and Waze Data',
          icon: 'btIcon'
        },
        children: [
          {
            path: RouterPaths.BLUETOOTH_DASHBOARD,
            name: RouterNames.BLUETOOTH_DASHBOARD,
            meta: {
              title: RouterNames.BLUETOOTH_DASHBOARD,
              icon: 'mdi-view-dashboard'
            }
          },
          {
            path: RouterPaths.BLUETOOTH_MULTIGRAPH,
            name: RouterNames.BLUETOOTH_MULTIGRAPH,
            meta: {
              title: RouterNames.BLUETOOTH_MULTIGRAPH,
              icon: 'mdi-chart-areaspline'
            }
          }
        ]
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
              title: RouterNames.CAV_DASHBOARD,
              icon: 'mdi-format-list-bulleted'
            }
          },
          {
            path: RouterPaths.CAV_STATUS,
            name: RouterNames.CAV_STATUS,
            meta: {
              title: RouterNames.CAV_STATUS,
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
              title: RouterNames.STATUS_DASHBOARD,
              icon: 'mdi-list-status'
            }
          },
          {
            path: RouterPaths.STATUS_QUALITY_CHECK,
            name: RouterNames.STATUS_QUALITY_CHECK,
            meta: {
              title: RouterNames.STATUS_QUALITY_CHECK,
              icon: 'mdi-check-decagram'
            }
          }
        ]
      }
    ]
  }
];

export default protectedRoute;

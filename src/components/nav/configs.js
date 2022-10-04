import { RouterNames, RouterPaths, RouterIcons } from '@/utils/constants/router';

const protectedRoute = [
  {
    path: '/',
    meta: {
      title: 'home',
      icon: ''
    },
    children: [
      {
        path: RouterPaths.TRAFFIC_DASHBOARD,
        meta: {
          title: 'Dashboard',
          icon: RouterIcons.TRAFFIC_DASHBOARD
        }
      },

      //Traffic Flow
      {
        path: RouterPaths.TRAFFIC_DASHBOARD,
        meta: {
          title: 'Traffic Data',
          icon: 'flowIcon'
        },
        children: [
          {
            path: RouterPaths.TRAFFIC_MULTIGRAPH,
            name: RouterNames.TRAFFIC_MULTIGRAPH,
            meta: {
              title: RouterNames.TRAFFIC_MULTIGRAPH,
              icon: RouterIcons.TRAFFIC_MULTIGRAPH
            }
          },
          {
            path: RouterPaths.TRAFFIC_SIGNALS,
            name: RouterNames.TRAFFIC_SIGNALS,
            meta: {
              title: RouterNames.TRAFFIC_SIGNALS,
              icon: RouterIcons.TRAFFIC_SIGNALS
            }
          },
          {
            path: RouterPaths.TRAFFIC_RESPONSIVE_DATA,
            name: RouterNames.TRAFFIC_RESPONSIVE_DATA,
            meta: {
              title: RouterNames.TRAFFIC_RESPONSIVE_DATA,
              icon: RouterIcons.TRAFFIC_RESPONSIVE_DATA
            }
          },
          {
            path: RouterPaths.TRAFFIC_ANOMALY,
            name: RouterNames.TRAFFIC_ANOMALY,
            meta: {
              title: RouterNames.TRAFFIC_ANOMALY,
              icon: RouterIcons.TRAFFIC_ANOMALY
            }
          },
          {
            path: RouterPaths.TRAVEL_TIME_MAP,
            name: RouterNames.TRAVEL_TIME_MAP,
            meta: {
              title: RouterNames.TRAVEL_TIME_MAP,
              icon: RouterIcons.TRAVEL_TIME_MAP
            }
          },
          {
            path: RouterPaths.TRAFFIC_INCIDENT_LIST,
            name: RouterNames.TRAFFIC_INCIDENT_LIST,
            meta: {
              title: RouterNames.TRAFFIC_INCIDENT_LIST,
              icon: RouterIcons.TRAFFIC_INCIDENT_LIST
            }
          },
          {
            path: RouterPaths.TRAFFIC_PREDICT,
            name: RouterNames.TRAFFIC_PREDICT,
            meta: {
              title: RouterNames.TRAFFIC_PREDICT,
              icon: RouterIcons.TRAFFIC_PREDICT
            }
          },
          {
            path: RouterPaths.TRAFFIC_DATA_PLAYBACK,
            name: RouterNames.TRAFFIC_DATA_PLAYBACK,
            meta: {
              title: RouterNames.TRAFFIC_DATA_PLAYBACK,
              icon: RouterIcons.TRAFFIC_DATA_PLAYBACK
            }
          },
          {
            path: RouterPaths.TRAFFIC_LCM,
            name: RouterNames.TRAFFIC_LCM,
            meta: {
              title: RouterNames.TRAFFIC_LCM,
              icon: RouterIcons.TRAFFIC_LCM,
              permissions: ['Admin', 'Dev']
            }
          },
          {
            path: RouterPaths.TRAFFIC_ROUTING,
            name: RouterNames.TRAFFIC_ROUTING,
            meta: {
              title: RouterNames.TRAFFIC_ROUTING,
              icon: RouterIcons.TRAFFIC_ROUTING,
              permissions: ['Admin', 'Dev']
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
              icon: RouterIcons.HR_DASHBOARD
            }
          },
          {
            path: RouterPaths.HR_PCD,
            name: RouterNames.HR_PCD,
            meta: {
              title: 'Purdue Coordination Diagram',
              icon: RouterIcons.HR_PCD
            }
          },
          {
            path: RouterPaths.HR_TIME_SPACE_DIAGRAM,
            name: RouterNames.HR_TIME_SPACE_DIAGRAM,
            meta: {
              title: RouterNames.HR_TIME_SPACE_DIAGRAM,
              icon: RouterIcons.HR_TIME_SPACE_DIAGRAM
            }
          },
          {
            path: RouterPaths.HR_SIGNAL_DISPLAY,
            name: RouterNames.HR_SIGNAL_DISPLAY,
            meta: {
              title: RouterNames.HR_SIGNAL_DISPLAY,
              icon: RouterIcons.HR_SIGNAL_DISPLAY
            }
          },
          {
            path: RouterPaths.HR_PERFORMANCE_MEASURES,
            name: RouterNames.HR_PERFORMANCE_MEASURES,
            meta: {
              title: RouterNames.HR_PERFORMANCE_MEASURES,
              icon: RouterIcons.HR_PERFORMANCE_MEASURES
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
              icon: RouterIcons.VISION_DASHBOARD
            }
          },
          {
            path: RouterPaths.VISION_REID,
            name: RouterNames.VISION_REID,
            meta: {
              title: RouterNames.VISION_REID,
              icon: RouterIcons.VISION_REID,
              permissions: ['Admin', 'Dev']
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
              icon: RouterIcons.CAV_DASHBOARD
            }
          },
          {
            path: RouterPaths.CAV_STATUS,
            name: RouterNames.CAV_STATUS,
            meta: {
              title: RouterNames.CAV_STATUS,
              icon: RouterIcons.CAV_STATUS
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
              icon: RouterIcons.STATUS_DASHBOARD,
              permissions: ['Admin', 'Dev']
            }
          },
          {
            path: RouterPaths.STATUS_QUALITY_CHECK,
            name: RouterNames.STATUS_QUALITY_CHECK,
            meta: {
              title: RouterNames.STATUS_QUALITY_CHECK,
              icon: RouterIcons.STATUS_QUALITY_CHECK
            }
          },
          {
            path: RouterPaths.STATUS_QUALITY_MAP,
            name: RouterNames.STATUS_QUALITY_MAP,
            meta: {
              title: RouterNames.STATUS_QUALITY_MAP,
              icon: RouterIcons.STATUS_QUALITY_MAP
            }
          }
        ]
      }
    ]
  }
];

export default protectedRoute;

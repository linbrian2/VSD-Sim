const protectedRoute = [
  {
    path: '/',
    meta: {
      title: 'home',
      icon: ''
    },
    children: [
      {
        path: '/dashboard',
        name: 'dashboard',
        meta: {
          title: 'Dashboard',
          icon: 'mdi-view-dashboard'
        }
      },

      //Traffic Flow
      {
        path: '/flow',
        meta: {
          title: 'Traffic Flow Data',
          icon: 'flowIcon'
        },
        children: [
          {
            path: '/flow',
            name: 'Dashboard',
            meta: {
              title: 'Dashboard',
              icon: 'mdi-view-dashboard'
            }
          },
          {
            path: '/flow/traffic-flow',
            name: 'Flow',
            meta: {
              title: 'Traffic Flow Charts',
              icon: 'mdi-chart-line'
            }
          },
          {
            path: '/flow/travel-time-data',
            name: 'bluetooth',
            meta: {
              title: 'Travel Time Charts',
              icon: 'mdi-chart-line'
            }
          },
          {
            path: '/flow/weather',
            name: 'weather',
            meta: {
              title: 'Weather Data Charts',
              icon: 'mdi-chart-line'
            }
          },
          {
            path: '/flow/anomaly',
            name: 'anomaly',
            meta: {
              title: 'Traffic Anomaly Map',
              icon: 'mdi-view-comfy'
            }
          },
          {
            path: '/flow/travel-time-map',
            name: 'ttmap',
            meta: {
              title: 'Travel Time Map',
              icon: 'mdi-view-comfy'
            }
          },
          {
            path: '/flow/incidents',
            name: 'incidents',
            meta: {
              title: 'Traffic Incident List',
              icon: 'mdi-format-list-bulleted '
            }
          },
          {
            path: '/flow/predict',
            name: 'predict',
            meta: {
              title: 'Traffic Flow Prediction',
              icon: 'mdi-waveform'
            }
          },
          {
            path: '/flow/lcm',
            name: 'lcm',
            meta: {
              title: 'Traffic LCM',
              icon: 'mdi-chart-bell-curve '
            }
          }
        ]
      },
      //HR
      {
        path: '/hr',
        meta: {
          title: 'High Resolution Data',
          icon: 'hrIcon'
        },
        children: [
          {
            path: '/hr',
            name: 'hr.dashboard',
            meta: {
              title: "Today's Summary",
              icon: 'mdi-clipboard-list-outline '
            }
          },
          {
            path: '/hr/pcd',
            name: 'hr.pcd',
            meta: {
              title: 'Purdue Coordination Diagram',
              icon: 'mdi-chart-areaspline'
            }
          },
          {
            path: '/hr/signal',
            name: 'hr.signal',
            meta: {
              title: 'Signal Timing',
              icon: 'mdi-clock'
            }
          },
          {
            path: '/hr/measures',
            name: 'hr.pref',
            meta: {
              title: 'Performance Measures',
              icon: 'mdi-chart-bar'
            }
          },
          {
            path: '/hr/pcds',
            name: 'hr.pcds',
            meta: {
              title: 'Corridor PCD Charts',
              icon: 'mdi-chart-gantt'
            }
          },
          {
            path: '/hr/occs',
            name: 'hr.occs',
            meta: {
              title: 'Corridor Occupancy Charts',
              icon: 'mdi-chart-gantt'
            }
          }
        ]
      },
      //Machine Vision
      {
        path: '/vision',
        meta: {
          title: 'Machine Vision',
          icon: 'visionIcon'
        },
        children: [
          {
            path: '/vision',
            name: 'vision.camera',
            meta: {
              title: 'Traffic Cameras',
              icon: 'mdi-cctv'
            }
          },
          {
            path: '/vision/re-id',
            name: 'vision.reid',
            meta: {
              title: 'Vehicle Re-ID',
              icon: 'mdi-car-2-plus'
            }
          }
        ]
      },
      //Bluetooth
      {
        path: '/bluetooth',
        meta: {
          title: 'Bluetooth and Waze Data',
          icon: 'btIcon'
        }
      },
      //CAV
      {
        path: '/cav',
        meta: {
          title: 'CA Vehicle Data',
          icon: 'cavIcon'
        },
        children: [
          {
            path: '/cav',
            name: 'cav.data',
            meta: {
              title: 'CAV Trip Data',
              icon: 'mdi-format-list-bulleted'
            }
          },
          {
            path: '/cav/status',
            name: 'cav.status',
            meta: {
              title: 'Real Time Status',
              icon: 'mdi-truck-check-outline'
            }
          }
        ]
      },
      //Health
      {
        path: '/status',
        meta: {
          title: 'System Monitoring',
          icon: 'healthIcon'
        },
        children: [
          {
            path: '/status',
            name: 'status.check',
            meta: {
              title: 'System Health Check',
              icon: 'mdi-list-status'
            }
          },
          {
            path: '/status/quality',
            name: 'status.quality',
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

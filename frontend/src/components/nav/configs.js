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
        path: '/',
        name: 'VMS Simulation',
        meta: {
          title: 'VMS Simulation',
          icon: RouterIcons.TRAFFIC_INCIDENT_LIST
        }
      }
    ]
  }
];

export default protectedRoute;

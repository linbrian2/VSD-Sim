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
        path: RouterPaths.TRAFFIC_INCIDENT_LIST,
        name: RouterNames.TRAFFIC_INCIDENT_LIST,
        meta: {
          title: RouterNames.TRAFFIC_INCIDENT_LIST,
          icon: RouterIcons.TRAFFIC_INCIDENT_LIST
        }
      }

    ]
  }
];

export default protectedRoute;

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
        path: RouterPaths.HR_TIME_SPACE_DIAGRAM,
        meta: {
          title: RouterNames.HR_TIME_SPACE_DIAGRAM,
          icon: RouterIcons.HR_TIME_SPACE_DIAGRAM
        }
      }
    ]
  }
];

export default protectedRoute;

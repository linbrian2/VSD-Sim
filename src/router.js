import Vue from 'vue';
import Router from 'vue-router';

import AuthToken from '@/auth/services/auth-token';
import LayoutAuth from '@/components/layouts/Auth';
import Traffic from '@/components/layouts/Traffic.vue';
import HR from '@/components/layouts/HR.vue';
import Vision from '@/components/layouts/Vision.vue';
import CAV from '@/components/layouts/CAV.vue';
import Status from '@/components/layouts/Status.vue';

import TrafficDashboard from '@/views/traffic/Dashboard.vue';
import TrafficFlow from '@/views/traffic/TrafficFlow.vue';
import TrafficLCM from '@/views/traffic/TrafficLCM.vue';
import TravelTimeData from '@/views/traffic/TravelTimeData.vue';
import WeatherData from '@/views/traffic/WeatherData.vue';
import TrafficPredict from '@/views/traffic/TrafficPredict.vue';
import AnomalyMap from '@/views/traffic/AnomalyMap.vue';
import TravelTimeMap from '@/views/traffic/TravelTimeMap.vue';
import IncidentList from '@/views/traffic/IncidentList.vue';
import CombinedAnomalyMap from '@/views/traffic/CombinedAnomalyMap.vue';

import HRDashboard from '@/views/hr/Dashboard.vue';
import PCD from '@/views/hr/PCD.vue';
import SignalTiming from '@/views/hr/SignalTiming.vue';
import PerformanceMeasures from '@/views/hr/PerformanceMeasures.vue';
import MultiPCD from '@/views/hr/MultiPCD.vue';
import MultiOcc from '@/views/hr/MultiOcc.vue';

import VisionDashboard from '@/views/vision/Dashboard.vue';
import VisionTrafficFlow from '@/views/vision/TrafficFlow.vue';
import ReID from '@/views/vision/ReID.vue';

import Bluetooth from '@/views/bluetooth/Bluetooth.vue';

import TripStatusView from '@/views/cav/TripStatusView.vue';
import TripDataView from '@/views/cav/TripDataView.vue';

import StatusDashboard from '@/views/status/Dashboard.vue';
import QualityCheck from '@/views/status/QualityCheck.vue';

import LoginPage from '@/auth/views/LoginPage';
import ChangePasswordPage from '@/auth/views/ChangePasswordPage';
import ResetPasswordPage from '@/auth/views/ResetPasswordPage';
import ForgotPasswordPage from '@/auth/views/ForgotPasswordPage';
import PreferencePage from '@/auth/views/PreferencePage';

Vue.use(Router);

const router = new Router({
  base: process.env.VUE_APP_BASE_URL,
  mode: 'history',
  routes: [
    {
      path: '/',
      alias: '/flow',
      component: Traffic,
      children: [
        {
          path: '',
          name: 'TrafficDashboard',
          component: TrafficDashboard
        },
        {
          path: 'traffic-flow',
          name: 'TrafficFlow',
          component: TrafficFlow
        },
        {
          path: 'lcm',
          name: 'LCM',
          component: TrafficLCM
        },
        {
          path: 'predict',
          name: 'Predict',
          component: TrafficPredict
        },
        {
          path: 'anomaly',
          name: 'Anaomaly',
          component: AnomalyMap
        },
        {
          path: 'weather',
          name: 'Weather',
          component: WeatherData
        },
        {
          path: 'travel-time-data',
          name: 'TravelTimeData',
          component: TravelTimeData
        },
        {
          path: 'travel-time-map',
          name: 'TravelTimeMap',
          component: TravelTimeMap
        },
        {
          path: 'incidents',
          name: 'Incidents',
          component: IncidentList
        },
        {
          path: 'combined-anomaly',
          name: 'CombinedAnomalyMap',
          component: CombinedAnomalyMap
        }
      ]
    },
    {
      path: '/hr',
      component: HR,
      children: [
        {
          path: '',
          name: 'HRDashboard',
          component: HRDashboard
        },
        {
          path: 'pcd',
          name: 'pcd',
          component: PCD
        },
        {
          path: 'signal',
          name: 'signal',
          component: SignalTiming
        },
        {
          path: 'measure',
          name: 'pref',
          component: PerformanceMeasures
        },
        {
          path: 'pcds',
          name: 'MultiPCD',
          component: MultiPCD
        },
        {
          path: 'occs',
          name: 'MultiOcc',
          component: MultiOcc
        }
      ]
    },
    {
      path: '/vision',
      component: Vision,
      children: [
        {
          path: '',
          name: 'VisionDashboard',
          component: VisionDashboard
        },
        {
          path: 'flow',
          name: 'VisionTrafficFlow',
          component: VisionTrafficFlow
        },
        {
          path: 're-id',
          name: 'Re-ID',
          component: ReID
        }
      ]
    },
    {
      path: '/bluetooth',
      name: 'Bluetooth',
      component: Bluetooth
    },
    {
      path: '/cav',
      component: CAV,
      children: [
        {
          path: '',
          name: 'TripData',
          component: TripDataView
        },
        {
          path: 'status',
          name: 'TripStatus',
          component: TripStatusView
        }
      ]
    },
    {
      path: '/status',
      component: Status,
      children: [
        {
          path: '',
          name: 'StatusDashboard',
          component: StatusDashboard
        },
        {
          path: 'quality',
          name: 'QualityCheck',
          component: QualityCheck
        }
      ]
    },
    {
      path: '',
      redirect: '/login',
      component: LayoutAuth,
      children: [
        {
          path: '/login',
          name: 'Login',
          component: LoginPage
        },
        {
          path: '/change-password',
          name: 'ChangePassword',
          component: ChangePasswordPage
        },
        {
          path: '/account/reset-password',
          name: 'ResetPassword',
          component: ResetPasswordPage
        },
        {
          path: '/forgot-password',
          name: 'FogotPassword',
          component: ForgotPasswordPage
        },
        {
          path: '/pref',
          name: 'preferences',
          component: PreferencePage
        }
      ]
    },

    // otherwise redirect to home
    { path: '*', redirect: '/' }
  ]
});

// redirect to login page if not logged in and trying to access a restricted page
router.beforeEach((to, from, next) => {
  const publicPages = ['/login', '/account/reset-password', '/forgot-password'];
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = AuthToken.isLoggedIn();

  if (authRequired && !loggedIn) {
    return next('/login', () => {});
  }

  next();
});

export default router;

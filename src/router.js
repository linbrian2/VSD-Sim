import Vue from 'vue';
import Router from 'vue-router';
import { RouterNames, RouterPaths } from '@/utils/constants/router';

import AuthToken from '@/auth/services/auth-token';
import LayoutAuth from '@/components/layouts/Auth';
import Traffic from '@/components/layouts/Traffic.vue';
import HR from '@/components/layouts/HR.vue';
import Vision from '@/components/layouts/Vision.vue';
import CAV from '@/components/layouts/CAV.vue';
import Status from '@/components/layouts/Status.vue';
import Bluetooth from '@/components/layouts/Bluetooth.vue';

import TrafficDashboard from '@/views/traffic/Dashboard.vue';
import TrafficFlow from '@/views/traffic/TrafficFlow.vue';
import TrafficLCM from '@/views/traffic/TrafficLCM.vue';
import TravelTimeData from '@/views/traffic/TravelTimeData.vue';
import WeatherData from '@/views/traffic/WeatherData.vue';
import TrafficPredict from '@/views/traffic/TrafficPredict.vue';
import AnomalyMap from '@/views/traffic/AnomalyMap.vue';
import TravelTimeMap from '@/views/traffic/TravelTimeMap.vue';
import IncidentList from '@/views/traffic/IncidentList.vue';

import HRDashboard from '@/views/hr/Dashboard.vue';
import PCD from '@/views/hr/PCD.vue';
import TimeSpaceDiagram from '@/views/hr/TimeSpaceDiagram.vue';
import SignalTiming from '@/views/hr/SignalTiming.vue';
import PerformanceMeasures from '@/views/hr/PerformanceMeasures.vue';
import MultiPCD from '@/views/hr/MultiPCD.vue';
import MultiOcc from '@/views/hr/MultiOcc.vue';

import VisionDashboard from '@/views/vision/Dashboard.vue';
import ReID from '@/views/vision/ReID.vue';

import BluetoothDashboard from '@/views/bluetooth/Dashboard.vue';
import BluetoothMultigraph from '@/views/bluetooth/MultiGraph.vue';
import BluetoothBreakdownProb from '@/views/bluetooth/BreakdownProbability.vue';

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
          name: RouterNames.TRAFFIC_DASHBOARD,
          component: TrafficDashboard
        },
        {
          path: RouterPaths.TRAFFIC_FLOW,
          name: RouterNames.TRAFFIC_FLOW,
          component: TrafficFlow
        },
        {
          path: RouterPaths.TRAFFIC_LCM,
          name: RouterNames.TRAFFIC_LCM,
          component: TrafficLCM
        },
        {
          path: RouterPaths.TRAFFIC_PREDICT,
          name: RouterNames.TRAFFIC_PREDICT,
          component: TrafficPredict
        },
        {
          path: RouterPaths.TRAFFIC_ANOMALY,
          name: RouterNames.TRAFFIC_ANOMALY,
          component: AnomalyMap
        },
        {
          path: RouterPaths.TRAFFIC_WEATHER,
          name: RouterNames.TRAFFIC_WEATHER,
          component: WeatherData
        },
        {
          path: RouterPaths.TRAVEL_TIME_DATA,
          name: RouterNames.TRAVEL_TIME_DATA,
          component: TravelTimeData
        },
        {
          path: RouterPaths.TRAVEL_TIME_MAP,
          name: RouterNames.TRAVEL_TIME_MAP,
          component: TravelTimeMap
        },
        {
          path: RouterPaths.TRAFFIC_INCIDENT,
          name: RouterNames.TRAFFIC_INCIDENT,
          component: IncidentList
        }
      ]
    },
    {
      path: RouterPaths.HR_DASHBOARD,
      component: HR,
      children: [
        {
          path: RouterPaths.HR_DASHBOARD,
          name: RouterNames.HR_DASHBOARD,
          component: HRDashboard
        },
        {
          path: RouterPaths.HR_PCD,
          name: RouterNames.HR_PCD,
          component: PCD
        },
        {
          path: RouterPaths.HR_TIME_SPACE_DIAGRAM,
          name: RouterNames.HR_TIME_SPACE_DIAGRAM,
          component: TimeSpaceDiagram
        },
        {
          path: RouterPaths.HR_SIGNAL,
          name: RouterNames.HR_SIGNAL,
          component: SignalTiming
        },
        {
          path: RouterPaths.HR_PERFORMANCE_MEASURES,
          name: RouterNames.HR_PERFORMANCE_MEASURES,
          component: PerformanceMeasures
        },
        {
          path: RouterPaths.HR_MULTI_PCD,
          name: RouterNames.HR_MULTI_PCD,
          component: MultiPCD
        },
        {
          path: RouterPaths.HR_MULTI_OCC,
          name: RouterNames.HR_MULTI_OCC,
          component: MultiOcc
        }
      ]
    },
    {
      path: RouterPaths.VISION_DASHBOARD,
      component: Vision,
      children: [
        {
          path: RouterPaths.VISION_DASHBOARD,
          name: RouterNames.VISION_DASHBOARD,
          component: VisionDashboard
        },
        {
          path: RouterPaths.VISION_REID,
          name: RouterNames.VISION_REID,
          component: ReID
        }
      ]
    },
    {
      path: RouterPaths.BLUETOOTH_DASHBOARD,
      component: Bluetooth,
      children: [
        {
          path: RouterPaths.BLUETOOTH_DASHBOARD,
          name: RouterNames.BLUETOOTH_DASHBOARD,
          component: BluetoothDashboard
        },
        {
          path: RouterPaths.BLUETOOTH_MULTIGRAPH,
          name: RouterNames.BLUETOOTH_MULTIGRAPH,
          component: BluetoothMultigraph
        },
        {
          path: RouterPaths.BLUETOOTH_BREAKDOWNPROB,
          name: RouterNames.BLUETOOTH_BREAKDOWNPROB,
          component: BluetoothBreakdownProb
        }
      ]
    },
    {
      path: RouterPaths.CAV_DASHBOARD,
      component: CAV,
      children: [
        {
          path: RouterPaths.CAV_DASHBOARD,
          name: RouterNames.CAV_DASHBOARD,
          component: TripDataView
        },
        {
          path: RouterPaths.CAV_STATUS,
          name: RouterNames.CAV_STATUS,
          component: TripStatusView
        }
      ]
    },
    {
      path: RouterPaths.STATUS_DASHBOARD,
      component: Status,
      children: [
        {
          path: RouterPaths.STATUS_DASHBOARD,
          name: RouterNames.STATUS_DASHBOARD,
          component: StatusDashboard
        },
        {
          path: RouterPaths.STATUS_QUALITY_CHECK,
          name: RouterNames.STATUS_QUALITY_CHECK,
          component: QualityCheck
        }
      ]
    },
    {
      path: '',
      redirect: RouterNames.AUTH_LOGIN,
      component: LayoutAuth,
      children: [
        {
          path: RouterPaths.AUTH_LOGIN,
          name: RouterNames.AUTH_LOGIN,
          component: LoginPage
        },
        {
          path: RouterPaths.AUTH_CHANGE_PASSWORD,
          name: RouterNames.AUTH_CHANGE_PASSWORD,
          component: ChangePasswordPage
        },
        {
          path: RouterPaths.AUTH_RESET_PASSWORD,
          name: RouterNames.AUTH_RESET_PASSWORD,
          component: ResetPasswordPage
        },
        {
          path: RouterPaths.AUTH_FORGOT_PASSWORD,
          name: RouterNames.AUTH_FORGOT_PASSWORD,
          component: ForgotPasswordPage
        },
        {
          path: RouterPaths.AUTH_PREFERENCES,
          name: RouterNames.AUTH_PREFERENCES,
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

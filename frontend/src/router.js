import Vue from 'vue';
import Router from 'vue-router';
import { RouterNames, RouterPaths } from '@/utils/constants/router';

// import AuthToken from '@/auth/services/auth-token';
import LayoutAuth from '@/components/layouts/Auth';
import Traffic from '@/components/layouts/Traffic.vue';
import HR from '@/components/layouts/HR.vue';
import Vision from '@/components/layouts/Vision.vue';
import CAV from '@/components/layouts/CAV.vue';
// import Dashboard from '@/components/layouts/Dashboard.vue';
import Fullscreen from '@/components/layouts/Fullscreen.vue';

import SimulationConfigs from '@/components/modules/traffic/incident/SimulationConfigs.vue';
// import TrafficDashboard from '@/views/traffic/Dashboard.vue';
import TrafficDataPlayback from '@/views/traffic/TrafficDataPlayback.vue';
import TrafficLCM from '@/views/traffic/TrafficLCM.vue';
import Multigraph from '@/views/traffic/Multigraph.vue';
import TrafficFlow from '@/components/modules/traffic/multigraph/TrafficFlow.vue';
import TrafficRoutingDashboard from '@/views/traffic/TrafficRoutingDashboard';
import TrafficPredict from '@/views/traffic/TrafficPredict.vue';
import AnomalyMap from '@/views/traffic/AnomalyMap.vue';
import TravelTimeMap from '@/views/traffic/TravelTimeMap.vue';
import IncidentList from '@/views/traffic/IncidentList.vue';
import TrafficIncident from '@/views/traffic/TrafficIncident.vue';
import IncidentMitigation from '@/views/traffic/IncidentMitigation.vue';
import TrafficResponsiveData from '@/views/traffic/TrafficResponsiveData.vue';
import TrafficSignals from '@/views/traffic/TrafficSignals.vue';

import HRDashboard from '@/views/hr/Dashboard.vue';
import PCD from '@/views/hr/PCD.vue';
import TimeSpaceDiagram from '@/views/hr/TimeSpaceDiagram.vue';
import SignalTimingDisplay from '@/views/hr/SignalTimingDisplay.vue';
import PerformanceMeasures from '@/views/hr/PerformanceMeasures.vue';
import MultiPCD from '@/views/hr/MultiPCD.vue';
import MultiOcc from '@/views/hr/MultiOcc.vue';

import VisionDashboard from '@/views/vision/Dashboard.vue';
import MultiView from '@/views/vision/MultiView.vue';
import ReID from '@/views/vision/ReID.vue';

import TripStatusView from '@/views/cav/TripStatusView.vue';
import TripDataView from '@/views/cav/TripDataView.vue';

import QualityMap from '@/views/status/QualityMap.vue';

import LoginPage from '@/auth/views/LoginPage';
import ChangePasswordPage from '@/auth/views/ChangePasswordPage';
import ResetPasswordPage from '@/auth/views/ResetPasswordPage';
import ForgotPasswordPage from '@/auth/views/ForgotPasswordPage';

Vue.use(Router);

const router = new Router({
  base: process.env.VUE_APP_BASE_URL,
  mode: 'history',
  routes: [
    {
      path: '/',
      alias: 'Simulation',
      component: Traffic,
      children: [
        {
          path: '',
          name: 'Simulation',
          component: SimulationConfigs
        }
      ]
    },
    {
      path: RouterPaths.TRAFFIC_MULTIGRAPH,
      component: Traffic,
      children: [
        {
          path: RouterPaths.TRAFFIC_LCM,
          name: RouterNames.TRAFFIC_LCM,
          component: TrafficLCM
        },
        {
          path: RouterPaths.TRAFFIC_ROUTING,
          name: RouterNames.TRAFFIC_ROUTING,
          component: TrafficRoutingDashboard
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
          path: RouterPaths.TRAFFIC_MULTIGRAPH,
          name: RouterNames.TRAFFIC_MULTIGRAPH,
          component: Multigraph
        },
        {
          path: RouterPaths.TRAFFIC_FLOW_DATA,
          name: RouterNames.TRAFFIC_FLOW_DATA,
          component: TrafficFlow
        },
        {
          path: RouterPaths.TRAVEL_TIME_MAP,
          name: RouterNames.TRAVEL_TIME_MAP,
          component: TravelTimeMap
        },
        {
          path: RouterPaths.TRAFFIC_INCIDENT_LIST,
          name: RouterNames.TRAFFIC_INCIDENT_LIST,
          component: IncidentList
        },
        {
          path: RouterPaths.TRAFFIC_DATA_PLAYBACK,
          name: RouterNames.TRAFFIC_DATA_PLAYBACK,
          component: TrafficDataPlayback
        },
        {
          path: RouterPaths.TRAFFIC_MITIGATION_PATH,
          name: RouterNames.TRAFFIC_MITIGATION,
          component: IncidentMitigation
        },
        {
          path: RouterPaths.TRAFFIC_RESPONSIVE_DATA,
          name: RouterNames.TRAFFIC_RESPONSIVE_DATA,
          component: TrafficResponsiveData
        },
        {
          path: RouterPaths.TRAFFIC_SIGNALS,
          name: RouterNames.TRAFFIC_SIGNALS,
          component: TrafficSignals
        },
        {
          path: RouterPaths.STATUS_QUALITY_MAP,
          name: RouterNames.STATUS_QUALITY_MAP,
          component: QualityMap
        }
      ]
    },
    {
      path: RouterPaths.TRAFFIC_INCIDENT_PATH,
      component: Fullscreen,
      children: [
        {
          path: RouterPaths.TRAFFIC_INCIDENT_PATH,
          name: RouterNames.TRAFFIC_INCIDENT_PATH,
          component: TrafficIncident
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
          path: RouterPaths.HR_SIGNAL_DISPLAY,
          name: RouterNames.HR_SIGNAL_DISPLAY,
          component: SignalTimingDisplay
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
          path: RouterPaths.VISION_MULTIVIEW,
          name: RouterNames.VISION_MULTIVIEW,
          component: MultiView
        },
        {
          path: RouterPaths.VISION_REID,
          name: RouterNames.VISION_REID,
          component: ReID
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
        }
      ]
    },

    // otherwise redirect to home
    { path: '*', redirect: '/' }
  ]
});

// redirect to login page if not logged in and trying to access a restricted page
// router.beforeEach((to, from, next) => {
//   const publicPages = ['/login', '/account/reset-password', '/forgot-password', '/images'];
//   const authRequired = !publicPages.includes(to.path);
//   const loggedIn = AuthToken.isLoggedIn();

//   if (authRequired && !loggedIn) {
//     sessionStorage.setItem('redirectPath', to.path);
//     return next('/login', () => {});
//   }

//   next();
// });

export default router;

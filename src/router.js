import Vue from 'vue';
import Router from 'vue-router';

import Traffic from '@/views/traffic/Traffic.vue'
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

import HR from '@/views/hr/HR.vue'
import HRDashboard from '@/views/hr/Dashboard.vue';
import PCD from '@/views/hr/PCD.vue';
import SignalTiming from '@/views/hr/SignalTiming.vue';
import PerformanceMeasures from '@/views/hr/PerformanceMeasures.vue';
import MultiPCD from '@/views/hr/MultiPCD.vue';
import MultiOcc from '@/views/hr/MultiOcc.vue';

import Vision from '@/views/vision/Vision.vue';
import VisionDashboard from '@/views/vision/Dashboard.vue';
import VisionTrafficFlow from '@/views/vision/TrafficFlow.vue';
import ReID from '@/views/vision/ReID.vue';

import Bluetooth from '@/views/bluetooth/Bluetooth.vue'

import CAV from '@/views/cav/CAV.vue'
import TripStatusView from '@/views/cav/TripStatusView.vue';
import TripDataView from '@/views/cav/TripDataView.vue';

import Status from '@/views/status/Status.vue'
import StatusDashboard from '@/views/status/Dashboard.vue';
import QualityCheck from '@/views/status/QualityCheck.vue';

Vue.use(Router);

export default new Router({
  mode: "history",
  routes: [
    {
      path: '/',
      alias: '/flow',
      name: 'Traffic',
      component: Traffic,
      children: [
        {
          path: '',
          name: 'TrafficDashboard',
          component: TrafficDashboard
        },
        {
          path: 'trafficFlow',
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
          path: 'travelTimeData',
          name: 'TravelTimeData',
          component: TravelTimeData
        },
        {
          path: 'travelTimeMap',
          name: 'TravelTimeMap',
          component: TravelTimeMap
        },
        {
          path: 'incidents',
          name: 'Incidents',
          component: IncidentList
        },
        {
          path: 'combinedAnomaly',
          name: 'CombinedAnomalyMap',
          component: CombinedAnomalyMap
        }
      ]
    },
    {
      path: '/hr',
      name: 'HR',
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
          path: 'pref',
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
      name: 'Vision',
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
      component: Bluetooth,
    },
    {
      path: '/status',
      name: 'Status',
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
      path: '/cav',
      name: 'CAV',
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
  ]
})
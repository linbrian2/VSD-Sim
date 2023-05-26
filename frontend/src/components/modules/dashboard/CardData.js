import Constants from '@/utils/constants/dashboard.js';
import { RouterPaths } from '@/utils/constants/router';

export default [
  {
    id: Constants.CARD_DATA_INCIDENTS_ID,
    title: Constants.TRAFFIC_INCIDENTS,
    icon: Constants.TRAFFIC_INCIDENTS_ICON,
    link: RouterPaths.TRAFFIC_INCIDENT_LIST,
    val: '-',
    displayOrder: 0,
    thresholds: [
      { val: 0, color: '#2ad728' },
      { val: 1, color: '#339900' },
      { val: 3, color: '#D7DF01' },
      { val: 5, color: '#FFCC55' },
      { val: 7, color: '#FF6600' },
      { val: 9, color: '#FF0000' }
    ]
  },
  {
    id: Constants.CARD_DATA_RESTRICTIONS_ID,
    title: Constants.TRAFFIC_RESTRICTIONS,
    icon: Constants.DEVICE_TRAFFIC_ICON,
    link: '',
    val: '-',
    displayOrder: 2,
    thresholds: [
      { val: 0, color: '#2ad728' },
      { val: 3, color: '#339900' },
      { val: 10, color: '#D7DF01' },
      { val: 20, color: '#FFCC55' },
      { val: 30, color: '#FF6600' },
      { val: 40, color: '#FF0000' }
    ]
  },
  {
    id: Constants.CARD_DATA_SIGNAL_ISSUES_ID,
    title: Constants.SIGNAL_PERFORMANCE_ISSUES,
    icon: Constants.SIGNAL_PERFORMANCE_ISSUES_ICON,
    link: RouterPaths.HR_DASHBOARD,
    val: '-',
    displayOrder: 3,
    thresholds: [
      { val: 0, color: '#2ad728' },
      { val: 1, color: '#339900' },
      { val: 4, color: '#D7DF01' },
      { val: 6, color: '#FFCC55' },
      { val: 8, color: '#FF6600' },
      { val: 10, color: '#FF0000' }
    ]
  },
  {
    id: Constants.CARD_DATA_DEDVICE_ANOMALIES_ID,
    title: Constants.DEVICE_ANOMALIES,
    icon: Constants.DEVICE_CABINET_ICON,
    link: RouterPaths.STATUS_QUALITY_MAP,
    val: '-',
    displayOrder: 5,
    thresholds: [
      { val: 0, color: '#2ad728' },
      { val: 10, color: '#339900' },
      { val: 30, color: '#D7DF01' },
      { val: 50, color: '#FFCC55' },
      { val: 80, color: '#FF6600' },
      { val: 100, color: '#FF0000' }
    ]
  },
  {
    id: Constants.CARD_DATA_CONGESTED_ROUTES_ID,
    title: Constants.HIGH_CONGESTION_ROUTES,
    icon: Constants.HIGH_CONGESTION_ROUTES_ICON,
    link: RouterPaths.TRAFFIC_DATA_PLAYBACK,
    val: '-',
    displayOrder: 1,
    thresholds: [
      { val: 0, color: '#2ad728' },
      { val: 5, color: '#339900' },
      { val: 10, color: '#D7DF01' },
      { val: 20, color: '#FFCC55' },
      { val: 30, color: '#FF6600' },
      { val: 40, color: '#FF0000' }
    ]
  },
  {
    id: Constants.CARD_DATA_WAZE_ALERTS_ID,
    title: Constants.REPORTED_WAZE_ALERTS,
    icon: Constants.REPORTED_WAZE_ALERTS_ICON,
    link: RouterPaths.TRAFFIC_DATA_PLAYBACK,
    val: '-',
    displayOrder: 4,
    thresholds: [
      { val: 0, color: '#2ad728' },
      { val: 1, color: '#339900' },
      { val: 20, color: '#D7DF01' },
      { val: 35, color: '#FFCC55' },
      { val: 50, color: '#FF6600' },
      { val: 65, color: '#FF0000' }
    ]
  }
];

export default Object.freeze({
  DIALOG_NONE: -1,
  DIALOG_CONGESTION: 0,
  DIALOG_WAZE: 1,
  DIALOG_DEVICES: 2,
  DIALOG_BLUETOOTH_SENSORS: 3,
  DIALOG_TRAVEL_TIME: 4,

  LAYER_CONGESTION: 0,
  LAYER_WAZE: 1,
  LAYER_GROUPED_WAZE: 2,
  LAYER_DEVICES: 3,
  LAYER_BLUETOOTH_SENSORS: 4,
  LAYER_LABELS: 5,
  LAYER_TRAFFIC_INCIDENTS: 6,

  WAZE_HAZARD: 0,
  WAZE_TRAFFIC_JAM: 1,
  WAZE_ROAD_CLOSED: 2,
  WAZE_ACCIDENT: 3,

  DEVICES_LOW_TRAFFIC: 0,
  DEVICES_MEDIUM_TRAFFIC: 1,
  DEVICES_HIGH_TRAFFIC: 2,

  MAP_DISABLED_LABELS: [
    {
      featureType: 'administrative.neighborhood',
      elementType: 'labels',
      stylers: [{ visibility: 'off' }]
    },
    {
      featureType: 'administrative.land_parcel',
      elementType: 'labels',
      stylers: [{ visibility: 'off' }]
    },
    {
      featureType: 'administrative.locality',
      elementType: 'labels',
      stylers: [{ visibility: 'off' }]
    }
  ],
  MAP_ENABLED_LABELS: [
    {
      featureType: 'administrative.neighborhood',
      elementType: 'labels',
      stylers: [{ visibility: 'on' }]
    },
    {
      featureType: 'administrative.land_parcel',
      elementType: 'labels',
      stylers: [{ visibility: 'on' }]
    },
    {
      featureType: 'administrative.locality',
      elementType: 'labels',
      stylers: [{ visibility: 'on' }]
    }
  ]
});

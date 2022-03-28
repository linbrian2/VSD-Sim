export const mapIcons = {
  data() {
    return {
      homeIcon: require('@/assets/home-24.png'),
      msgIcon: require('@/assets/alert-24.png'),

      startIcon: {
        url: require('@/assets/start.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' }
      },

      endIcon: {
        url: require('@/assets/end.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' }
      },

      normalMarker: {
        url: require('@/assets/green-icon-48.png'),
        size: { width: 30, height: 48, f: 'px', b: 'px' }
      },

      activeMarker: {
        url: require('@/assets/orange-icon-48.png'),
        size: { width: 30, height: 48, f: 'px', b: 'px' }
      },

      weatherIcon: {
        url: require('@/assets/icon30.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' }
      },

      weatherIconActive: {
        url: require('@/assets/icon30-select.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' }
      },

      normalBluetoothIcon: {
        url: require('@/assets/icon54.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' },
        anchor: { x: 16, y: 16 }
      },

      redBluetoothIcon: {
        url: require('@/assets/icon7.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' },
        anchor: { x: 16, y: 16 }
      },

      redBluetoothIconActive: {
        url: require('@/assets/icon7-select.png'),
        size: { width: 36, height: 36, f: 'px', b: 'px' },
        anchor: { x: 18, y: 18 }
      },

      redDotIcon: {
        url: require('@/assets/red-dot.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' }
      },

      trafficLightIcon: {
        url: require('@/assets/traffic_light_small.png'),
        size: { width: 15, height: 36, f: 'px', b: 'px' },
        anchor: { x: 8, y: 18 }
      },

      trafficLightIconActive: {
        url: require('@/assets/traffic_light_active.png'),
        size: { width: 15, height: 36, f: 'px', b: 'px' },
        anchor: { x: 8, y: 18 }
      },

      redDotIconActive: {
        url: require('@/assets/red-dot-select.png'),
        size: { width: 36, height: 36, f: 'px', b: 'px' }
      },

      restrictionIcon: {
        url: require('@/assets/icon41.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' },
        anchor: { x: 16, y: 16 }
      },

      restrictionIconActive: {
        url: require('@/assets/icon41-select.png'),
        size: { width: 36, height: 36, f: 'px', b: 'px' },
        anchor: { x: 18, y: 18 }
      },

      wazeIcon: {
        url: require('@/assets/waze.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' },
        anchor: { x: 16, y: 16 }
      },

      wazeIconActive: {
        url: require('@/assets/waze-select.png'),
        size: { width: 36, height: 36, f: 'px', b: 'px' },
        anchor: { x: 18, y: 18 }
      },

      alertIcon: {
        url: require('@/assets/alert.png'),
        size: { width: 64, height: 64, f: 'px', b: 'px' },
        anchor: { x: 32, y: 32 }
      },

      alertIconActive: {
        url: require('@/assets/alert.png'),
        size: { width: 60, height: 60, f: 'px', b: 'px' },
        anchor: { x: 30, y: 30 }
      },

      cautionIcon: {
        url: require('@/assets/caution.png'),
        size: { width: 40, height: 40, f: 'px', b: 'px' },
        anchor: { x: 20, y: 20 }
      },

      cautionIconActive: {
        url: require('@/assets/caution-select.png'),
        size: { width: 44, height: 44, f: 'px', b: 'px' },
        anchor: { x: 22, y: 22 }
      },

      cameraIcon: {
        url: require('@/assets/icon49.png'),
        size: { width: 32, height: 32, f: 'px', b: 'px' },
        anchor: { x: 16, y: 16 }
      },

      cameraIconActive: {
        url: require('@/assets/icon49-select.png'),
        size: { width: 36, height: 36, f: 'px', b: 'px' },
        anchor: { x: 18, y: 18 }
      },

      redIcon: {
        path: 0,
        scale: 10.0,
        fillColor: '#FF0000',
        fillOpacity: 0.8,
        strokeWeight: 1.2,
        strokeColor: '#FFFFFF',
        strokeOpacity: 0.8
      },

      smallRedIcon: {
        path: 0,
        scale: 6.0,
        fillColor: '#FF0000',
        fillOpacity: 0.8,
        strokeWeight: 1.2,
        strokeColor: '#FFFFFF',
        strokeOpacity: 0.8
      },

      smallGreenIcon: {
        path: 0,
        scale: 6.0,
        fillColor: '#00FF00',
        fillOpacity: 0.8,
        strokeWeight: 1.2,
        strokeColor: '#FFFFFF',
        strokeOpacity: 0.8
      },

      icons: [
        {
          path: 0,
          scale: 10.0,
          fillColor: '#05FF00',
          fillOpacity: 0.8,
          strokeWeight: 0.4
        },
        {
          path: 0,
          scale: 10.0,
          fillColor: '#FF0000',
          fillOpacity: 0.8,
          strokeWeight: 0.4
        }
      ]
    };
  },

  methods: {
    getWeatherMarkerIcon(marker) {
      if (marker) {
        return this.getWeatherIcon(marker.code);
      }
    },

    getSegmentMarkerIcon(marker) {
      if (marker) {
        return marker.status == 7 ? this.redBluetoothIcon : this.normalBluetoothIcon;
      }
    },

    getMarkerIcon(marker) {
      if (marker) {
        return marker.status > 0 ? this.icons[1] : this.icons[0];
      }
      return this.selectedMarkerId === marker.id ? this.icons[1] : this.icons[0];
    },

    getIconByName(name) {
      return this[name];
    }
  }
};

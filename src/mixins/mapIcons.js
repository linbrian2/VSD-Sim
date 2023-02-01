/* global google */
export const mapIcons = {
  data() {
    return {
      homeIcon: require('@/assets/home-24.png'),
      zoomIcon: require('@/assets/zoom-24.png'),
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

      greenDotSelect: {
        url: require('@/assets/green-select.png'),
        size: { width: 30, height: 30, f: 'px', b: 'px' },
        anchor: { x: 15, y: 15 }
      },

      redDotSelect: {
        url: require('@/assets/red-select.png'),
        size: { width: 30, height: 30, f: 'px', b: 'px' },
        anchor: { x: 15, y: 15 }
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

      normalBluetoothIconActive: {
        url: require('@/assets/icon54-select.png'),
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
        url: require('@/assets/traffic_light_small_select.png'),
        size: { width: 19, height: 40, f: 'px', b: 'px' },
        anchor: { x: 9, y: 20 }
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
        size: { width: 48, height: 48, f: 'px', b: 'px' },
        anchor: { x: 24, y: 24 }
      },

      alertIconActive: {
        url: require('@/assets/alert-select.png'),
        size: { width: 48, height: 48, f: 'px', b: 'px' },
        anchor: { x: 24, y: 24 }
      },

      alertAnimatedIcon: {
        url: require('@/assets/alert-animated.gif'),
        size: { width: 64, height: 64, f: 'px', b: 'px' },
        anchor: { x: 32, y: 32 }
      },

      alertAnimatedIconActive: {
        url: require('@/assets/alert-animated-select.gif'),
        size: { width: 64, height: 64, f: 'px', b: 'px' },
        anchor: { x: 32, y: 32 }
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

      cabinetIcon: {
        url: require('@/assets/file-cabinet.png'),
        size: { width: 28, height: 28, f: 'px', b: 'px' },
        anchor: { x: 14, y: 14 }
      },

      cabinetIconActive: {
        url: require('@/assets/file-cabinet-select.png'),
        size: { width: 28, height: 28, f: 'px', b: 'px' },
        anchor: { x: 14, y: 14 }
      },

      yellowMaskIcon: {
        path: 'M18 4H6c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2z',
        scale: 2.0,
        fillColor: '#FFFFFF',
        fillOpacity: 0.0,
        strokeWeight: 2.8,
        strokeColor: '#FFFF55',
        strokeOpacity: 1.0,
        anchor: { x: 12, y: 12 }
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
      ],

      icons2: [
        {
          path: 0,
          scale: 10.0,
          fillColor: '#fa00ff',
          fillOpacity: 0.8,
          strokeWeight: 0.4
        },
        {
          path: 0,
          scale: 10.0,
          fillColor: '#00ffff',
          fillOpacity: 0.8,
          strokeWeight: 0.4
        }
      ],

      wazeIcons: [
        {
          url: require('@/assets/waze.png'),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        },
        {
          url: require('@/assets/waze-select.png'),
          size: { width: 36, height: 36, f: 'px', b: 'px' },
          anchor: { x: 18, y: 18 }
        },
        {
          url: require('@/assets/waze-accident.png'),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        },
        {
          url: require('@/assets/waze-accident-select.png'),
          size: { width: 36, height: 36, f: 'px', b: 'px' },
          anchor: { x: 18, y: 18 }
        },
        {
          url: require('@/assets/waze-construction.png'),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        },
        {
          url: require('@/assets/waze-construction-select.png'),
          size: { width: 36, height: 36, f: 'px', b: 'px' },
          anchor: { x: 18, y: 18 }
        },
        {
          url: require('@/assets/waze-hazard.png'),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        },
        {
          url: require('@/assets/waze-hazard-select.png'),
          size: { width: 36, height: 36, f: 'px', b: 'px' },
          anchor: { x: 18, y: 18 }
        },
        {
          url: require('@/assets/waze-road-closed.png'),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        },
        {
          url: require('@/assets/waze-road-closed-select.png'),
          size: { width: 36, height: 36, f: 'px', b: 'px' },
          anchor: { x: 18, y: 18 }
        },
        {
          url: require('@/assets/waze-traffic-jam.png'),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        },
        {
          url: require('@/assets/waze-traffic-jam-select.png'),
          size: { width: 36, height: 36, f: 'px', b: 'px' },
          anchor: { x: 18, y: 18 }
        }
      ],

      signalIcons: [
        {
          url: require('@/assets/traffic_light_green.png'),
          scaledSize: new google.maps.Size(15, 38),
          anchor: { x: 6, y: 15 }
        },
        {
          url: require('@/assets/traffic_light_teal.png'),
          scaledSize: new google.maps.Size(15, 38),
          anchor: { x: 6, y: 15 }
        },
        {
          url: require('@/assets/traffic_light_yellow.png'),
          scaledSize: new google.maps.Size(15, 38),
          anchor: { x: 6, y: 15 }
        },
        {
          url: require('@/assets/traffic_light_lightorange.png'),
          scaledSize: new google.maps.Size(15, 38),
          anchor: { x: 6, y: 15 }
        },
        {
          url: require('@/assets/traffic_light_purple.png'),
          scaledSize: new google.maps.Size(15, 38),
          anchor: { x: 6, y: 15 }
        },
        {
          url: require('@/assets/traffic_light_red.png'),
          scaledSize: new google.maps.Size(15, 38),
          anchor: { x: 6, y: 15 }
        },
        {
          url: require('@/assets/traffic_light_blue.png'),
          scaledSize: new google.maps.Size(15, 38),
          anchor: { x: 6, y: 15 }
        }
      ],

      deviceIcons: [
        {
          path: 0,
          scale: 9.0,
          fillColor: '#339900',
          fillOpacity: 1.0,
          strokeWeight: 1.2,
          strokeOpacity: 0.8
        },
        {
          path: 0,
          scale: 9.0,
          fillColor: '#00FF33',
          fillOpacity: 1.0,
          strokeWeight: 1.2,
          strokeOpacity: 0.8
        },
        {
          path: 0,
          scale: 9.0,
          fillColor: '#FFFF00',
          fillOpacity: 1.0,
          strokeWeight: 0.6,
          strokeOpacity: 0.8
        },
        {
          path: 0,
          scale: 9.0,
          fillColor: '#FFC031',
          fillOpacity: 1.0,
          strokeWeight: 0.6,
          strokeOpacity: 0.8
        },
        {
          path: 0,
          scale: 9.0,
          fillColor: '#FA7A2A',
          fillOpacity: 1.0,
          strokeWeight: 0.6,
          strokeOpacity: 0.8
        },
        {
          path: 0,
          scale: 9.0,
          fillColor: '#FF0000',
          fillOpacity: 1.0,
          strokeWeight: 0.6,
          strokeOpacity: 0.8
        },
        {
          path: 0,
          scale: 9.0,
          fillColor: '#C00000',
          fillOpacity: 1.0,
          strokeWeight: 0.6,
          strokeOpacity: 0.8
        }
      ]
    };
  },

  methods: {
    getHRIcons(marker, selected = false) {
      if (marker) {
        return marker.status > 0 || selected ? this.trafficLightIconActive : this.trafficLightIcon;
      }
      return this.selectedMarkerId === marker.id ? this.trafficLightIconActive : this.trafficLightIcon;
    },

    getWeatherMarkerIcon(marker) {
      if (marker) {
        return this.getWeatherIcon(marker.code);
      }
    },

    getNormalWeatherMarkerIcon(marker, selected = false) {
      return selected ? this.weatherIconActive : this.getWeatherIcon(marker.code);
    },

    getSegmentMarkerIcon(marker) {
      if (marker) {
        return marker.status == 7 ? this.redBluetoothIcon : this.normalBluetoothIcon;
      }
    },

    getBluetoothMarkerIcon(selected = false) {
      return selected ? this.normalBluetoothIconActive : this.normalBluetoothIcon;
    },

    getCameraMarkerIcon(selected = false) {
      return selected ? this.cameraIconActive : this.cameraIcon;
    },

    getDotMarkerIcon(selected = false) {
      return selected ? this.greenDotSelect : this.icons[0];
    },

    getDotMarker(normal = true, selected = false) {
      if (normal) {
        return selected ? this.greenDotSelect : this.icons[0];
      } else {
        return selected ? this.redDotSelect : this.icons[1];
      }
    },

    getDeviceMarkerIcon(selected = false) {
      return selected ? this.cabinetIconActive : this.cabinetIcon;
    },

    getRestrictionIcon(selected = false) {
      return selected ? this.restrictionIconActive : this.restrictionIcon;
    },

    getDefaultMarkerIcon(marker, selected = false) {
      if (marker) {
        return marker.status > 0 || selected ? this.icons[1] : this.icons[0];
      }
      return this.selectedMarkerId === marker.id ? this.icons[1] : this.icons[0];
    },

    getMarker2Icon(marker, selected = false) {
      if (marker) {
        return marker.status > 0 || !selected ? this.icons2[1] : this.icons2[0];
      }
      return this.selectedMarkerId === marker.id ? this.icons2[1] : this.icons2[0];
    },

    getMarkerIcon(marker, selected = false) {
      if (marker) {
        return marker.status > 0 || selected ? this.icons[1] : this.icons[0];
      }
      return this.selectedMarkerId === marker.id ? this.icons[1] : this.icons[0];
    },

    getIconByName(name) {
      return this[name];
    },

    getWazeIcon(marker, active = false) {
      if (marker.subType == undefined) {
        return active ? this.wazeIcons[7] : this.wazeIcons[6];
      }
      let id = marker.subType.id;
      if (id >= 10 && id <= 13)
        // Accident
        return active ? this.wazeIcons[3] : this.wazeIcons[2];
      else if (id >= 20 && id <= 25)
        // Jam
        return active ? this.wazeIcons[11] : this.wazeIcons[10];
      else if (id >= 90 && id <= 91)
        // Construction
        return active ? this.wazeIcons[5] : this.wazeIcons[4];
      else if (id >= 100 && id <= 104)
        // Closure
        return active ? this.wazeIcons[9] : this.wazeIcons[8];
      // Hazard
      else return active ? this.wazeIcons[7] : this.wazeIcons[6];
    },

    getDeviceIcon(level, active = false) {
      if (level >= 1)
        return active
          ? { ...this.deviceIcons[level], strokeColor: '#FFFFFF', strokeWeight: 5.0 }
          : this.deviceIcons[level];
      else return this.deviceIcons[0];
    },

    getSignalIcon(level) {
      return this.signalIcons[level];
    }
  }
};

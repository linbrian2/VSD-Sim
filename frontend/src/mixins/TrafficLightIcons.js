export const TrafficLightIcons = {
  data() {
    return {
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

      trafficLightROffIcon: {
        path: 0,
        scale: 12.0,
        fillColor: '#FF0000',
        fillOpacity: 0.2,
        strokeWeight: 3.0,
        strokeColor: '#222222',
        strokeOpacity: 0.8
      },

      trafficLightYOffIcon: {
        path: 0,
        scale: 12.0,
        fillColor: '#FFFF00',
        fillOpacity: 0.2,
        strokeWeight: 3.0,
        strokeColor: '#222222',
        strokeOpacity: 0.8
      },

      trafficLightGOffIcon: {
        path: 0,
        scale: 12.0,
        fillColor: '#00FF00',
        fillOpacity: 0.8,
        strokeWeight: 3.0,
        strokeColor: '#222222',
        strokeOpacity: 0.8
      },

      trafficLightRIcon: {
        path: 0,
        scale: 12.0,
        fillColor: '#FF0000',
        fillOpacity: 0.8,
        strokeWeight: 3.0,
        strokeColor: '#222222',
        strokeOpacity: 0.8
      },

      trafficLightYIcon: {
        path: 0,
        scale: 12.0,
        fillColor: '#FFFF00',
        fillOpacity: 0.8,
        strokeWeight: 3.0,
        strokeColor: '#222222',
        strokeOpacity: 0.8
      },

      trafficLightGIcon: {
        path: 0,
        scale: 12.0,
        fillColor: '#00FF00',
        fillOpacity: 0.8,
        strokeWeight: 3.0,
        strokeColor: '#222222',
        strokeOpacity: 0.8
      }
    };
  },

  methods: {
    getIconByName(name) {
      return this[name];
    }
  }
};

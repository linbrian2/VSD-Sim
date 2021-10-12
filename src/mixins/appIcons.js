export const appIcons = {
  data() {
    return {
      btIcon: require('@/assets/icons/bt.png'),
      cavIcon: require('@/assets/icons/cav.png'),
      flowIcon: require('@/assets/icons/flow.png'),
      healthIcon: require('@/assets/icons/health.png'),
      hrIcon: require('@/assets/icons/hr.png'),
      simIcon: require('@/assets/icons/sim.png'),
      visionIcon: require('@/assets/icons/vision.png'),
      btIconLight: require('@/assets/icons/bt_d.png'),
      cavIconLight: require('@/assets/icons/cav_d.png'),
      flowIconLight: require('@/assets/icons/flow_d.png'),
      healthIconLight: require('@/assets/icons/health_d.png'),
      hrIconLight: require('@/assets/icons/hr_d.png'),
      simIconLight: require('@/assets/icons/sim_d.png'),
      visionIconLight: require('@/assets/icons/vision_d.png')
    };
  },

  methods: {
    getAppIconByName(name, darkMode) {
      return darkMode ? this[name] : this[name + 'Light'];
    }
  }
};

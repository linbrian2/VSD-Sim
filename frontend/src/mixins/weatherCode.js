export const weatherCode = {
  data() {
    return {
      weatherCodeMapping: [
        40,
        40,
        40,
        40,
        40,
        35,
        32,
        35,
        32,
        32,
        32,
        40,
        40,
        35,
        35,
        43,
        43,
        43,
        35,
        34,
        34,
        34,
        34,
        34,
        34,
        34,
        42,
        34,
        34,
        34,
        34,
        41,
        41,
        33,
        33,
        40,
        30,
        36,
        36,
        40,
        40,
        43,
        43,
        43,
        34,
        35,
        35,
        35
      ]
    };
  },

  methods: {
    getWeatherIcon(conditionCode) {
      let icon = null;
      if (conditionCode >= 0 && conditionCode <= 47) {
        icon = this.weatherCodeMapping[conditionCode];
      }

      if (icon) {
        return {
          url: require(`@/assets/weather/icon${icon}.png`),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        };
      } else {
        return {
          url: require(`@/assets/weather/icon41.png`),
          size: { width: 32, height: 32, f: 'px', b: 'px' },
          anchor: { x: 16, y: 16 }
        };
      }
    }
  }
};

export default {
  // Get a setting: $store.getters.getSettings(app, setting)
  // Example: $store.getters.getSetting('dashboard', 'autoDataUpdate')
  // Computed property: ...mapGetters(['getSetting']) => getSetting('dashboard', 'autoDataUpdate')
  // ! Use restore to default button on settings page after making changes/adding new settings
  getDefault() {
    return {
      general: {
        name: 'General',
        icon: 'mdi-cog',
        settings: [
          { divider: true, title: 'Weather Settings' },
          { id: 0, name: 'showWeatherBar', label: 'Show Weather Bar', type: 'boolean', val: true },
          { id: 1, name: 'showTemp', label: 'Show Temperature', type: 'boolean', val: true },
          { id: 2, name: 'showWind', label: 'Show Wind Speed', type: 'boolean', val: false },
          { id: 3, name: 'showVisibility', label: 'Show Visibility', type: 'boolean', val: false },
          { id: 4, name: 'showPrecip', label: 'Show Percipitation', type: 'boolean', val: false }
          // { divider: true, title: 'Side Bar' },
          // { id: 5, name: 'sideBarWidth', label: 'Side Bar Width', type: 'number', val: 700, min: 0, max: 1500 }
        ]
      },
      dashboard: {
        name: 'Main Dashboard',
        icon: 'mdi-view-dashboard',
        settings: [
          { divider: true, title: 'Updates' },
          { id: 1000, name: 'autoDataUpdate', label: 'Automatic Data Update', type: 'boolean', val: true },
          {
            id: 1001,
            name: 'dataUpdateInterval',
            label: 'Data Update Interval (min)',
            type: 'number',
            val: 5,
            min: 1,
            max: 10
          },
          { id: 1002, name: 'autoPageSwaps', label: 'Automatic Page Swaps', type: 'boolean', val: true },
          { id: 1003, name: 'swapInterval', label: 'Swap Interval (sec)', type: 'number', val: 45, min: 10, max: 60 },
          { divider: true, title: 'Miscellaneous' },
          { id: 1100, name: 'infoColumnCount', label: 'Info Column Count', type: 'number', val: 1, min: 1, max: 5 },
          { id: 1101, name: 'limitListings', label: 'Limit Listings', type: 'number', val: 0, min: 0, max: 5 },
          { id: 1102, name: 'hideIconsRightPanel', label: 'Hide Icons on Right Panel', type: 'boolean', val: false },
          { id: 1103, name: 'showAllOverlayCards', label: 'Show All Overlay Cards', type: 'boolean', val: false },
          { divider: true, title: 'Debug' },
          {
            id: 1200,
            name: 'incidentsWholeDay',
            label: 'Use Whole Day of Traffic Incidents',
            type: 'boolean',
            val: false
          },
          {
            id: 1201,
            name: 'usePrevDay',
            label: 'Use Previous Day',
            type: 'boolean',
            val: false
          }
        ]
      },
      traffic: {
        name: 'Traffic Flow Data',
        icon: 'flowIcon',
        settings: [{ divider: true, title: 'Flow Settings' }]
      },
      hr: {
        name: 'High Resolution Data',
        icon: 'hrIcon',
        settings: [{ divider: true, title: 'Hr Settings' }]
      },
      vision: {
        name: 'Machine Vision',
        icon: 'visionIcon',
        settings: [{ divider: true, title: 'Vision Settings' }]
      },
      cav: {
        name: 'CAV Data',
        icon: 'cavIcon',
        settings: [{ divider: true, title: 'Cav Settings' }]
      },
      status: {
        name: 'System Monitoring',
        icon: 'healthIcon',
        settings: [{ divider: true, title: 'Health Settings' }]
      }
    };
  }
};

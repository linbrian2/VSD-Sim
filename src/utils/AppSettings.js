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
          { divider: true, title: 'Notifications' },
          { id: 0, name: 'allowEmail', label: 'Enable email notifications', type: 'boolean', val: false },
          { id: 1, name: 'allowSMS', label: 'Enable SMS notifications', type: 'boolean', val: false },
          { divider: true, title: 'Weather Settings' },
          { id: 3, name: 'showWeatherBar', label: 'Show Weather Bar', type: 'boolean', val: true },
          { id: 4, name: 'showTemp', label: 'Show Temperature', type: 'boolean', val: true },
          { id: 5, name: 'showWind', label: 'Show Wind Speed', type: 'boolean', val: false },
          { id: 6, name: 'showVisibility', label: 'Show Visibility', type: 'boolean', val: false },
          { id: 7, name: 'showPrecip', label: 'Show Percipitation', type: 'boolean', val: false }
        ]
      },
      dashboard: {
        name: 'Dashboard',
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
          { id: 1100, name: 'infoColumnCount', label: 'Info Column Count', type: 'number', val: 1, min: 1, max: 4 },
          { id: 1101, name: 'limitListings', label: 'Limit Listings', type: 'number', val: 0, min: 0, max: 5 },
          { id: 1102, name: 'hideIconsRightPanel', label: 'Hide Icons on Right Panel', type: 'boolean', val: false },
          { id: 1103, name: 'showAllOverlayCards', label: 'Show All Overlay Cards', type: 'boolean', val: false }
        ]
      }
    };
  }
};

export default {
  // Get a setting: $store.getters.getSettings(app, setting)
  // Example: $store.getters.getSetting('mainDashboard', 'autoDataUpdate')
  // Computed property: ...mapGetters(['getSetting']) => getSetting('mainDashboard', 'autoDataUpdate')
  // ! Use restore to default button on settings page after making changes/adding new settings
  getDefault() {
    return {
      general: {
        icon: 'mdi-cog',
        params: {}
      },
      mainDashboard: {
        icon: 'mdi-view-dashboard',
        params: {
          autoDataUpdate: { label: 'Enable Automatic Data Update', val: true },
          dataUpdateInterval: { label: 'Data update interval (min)', val: 5, min: 1, max: 10 },
          autoPageSwaps: { label: 'Enable Automatic Page Swaps', val: true },
          swapInterval: { label: 'Swap Interval (sec)', val: 45, min: 10, max: 60 },
          resizableMap: { label: 'Enable Resizable Map (Overrides layout)', val: false },
          pageLayout: {
            label: 'Page layout',
            val: '1: Card, 2: Map, 3: Info',
            items: ['1: Card, 2: Info, 3: Map', '1: Card, 2: Map, 3: Info']
          },
          limitListings: { label: 'Limit Listings', val: 1, min: 1, max: 5 }
        }
      },
      trafficFlowData: {
        icon: 'flowIcon',
        params: {}
      },
      highResolutionData: {
        icon: 'hrIcon',
        params: {}
      },
      machineVision: {
        icon: 'visionIcon',
        params: {}
      },
      cavData: {
        icon: 'cavIcon',
        params: {}
      },
      systemMonitoring: {
        icon: 'healthIcon',
        params: {}
      }
    };
  }
};

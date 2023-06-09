<template>
  <div>
    <div v-if="$vuetify.breakpoint.mobile" class="mt-5" />
    <TitleBar title="Time Space Diagram" :loading="loading" :refresh="refreshData">
      <div class="d-flex justify-center">
        <div style="width:160px">
          <ScrollableMenuTimePicker
            ref="timePicker"
            :inc="duration"
            @set-time="timeChanged"
            @prev="timeChanged"
            @next="timeChanged"
          />
        </div>

        <div class="d-flex justify-center mt-n2" style="`width:110px; margin-right: 30px; margin-left:100px;`">
          <v-select
            dark
            style="max-width: 120px;font-size: 14px"
            v-model="overlay"
            :items="overlayItems"
            item-text="text"
            item-value="value"
            hide-details
            single-line
            @input="overlaySelected"
            prepend-icon="mdi-content-copy"
          />
        </div>
      </div>
    </TitleBar>
    <v-container fluid style="max-width: 95%">
      <div v-if="showChartInGroup">
        <div v-for="(data, group, index) in timeSpaceGroups" :key="index">
          <v-card class="mt-4">
            <TimeSpaceChart :data="data" :speed="speed" :title="`Group ${group}`" />
          </v-card>
        </div>
      </div>
      <div v-else>
        <v-card class="mt-4">
          <TimeSpaceChart :data="data" />
        </v-card>
      </div>

      <v-card tile class="mt-4">
        <div class="d-flex align-center">
          <h4 class="mx-4 overline">PASSER II Efficiency Assessment:</h4>
          <v-chip-group active-class="primary--text" column class="ml-6">
            <v-chip
              small
              v-for="{ color, name, range } in efficiencyClasses"
              :key="name"
              :color="color"
              class="black--text mr-3"
            >
              {{ range }}: {{ name }}
            </v-chip>
          </v-chip-group>
        </div>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/hr';
import Utils from '@/utils/Utils';
import { mapState, mapActions } from 'vuex';
import TimeSpaceChart from '@/components/modules/hr/TimeSpaceChart';
import TitleBar from '@/components/modules/hr/TitleBar';
import ScrollableMenuTimePicker from '@/components/modules/hr/ScrollableMenuTimePicker';

export default {
  components: {
    TitleBar,
    ScrollableMenuTimePicker,
    TimeSpaceChart
  },

  data: () => ({
    loading: false,
    height: 1000,
    duration: 900,
    startTime: null,
    timeSpaceGroups: {},
    data: {},
    showChartInGroup: false,
    overlay: 0,
    overlayItems: [
      { text: 'None', value: 0 },
      { text: 'Transit', value: 1 },
      { text: 'Cav', value: 2 }
    ],
    efficiencyClasses: [
      { range: '0.00 - 0.12', color: '#ED561B', name: 'Poor Progression' },
      { range: '0.13 - 0.24', color: '#DDDF00', name: 'Fair Progression' },
      { range: '0.25 - 0.36', color: '#058DC7', name: 'Good Progression' },
      { range: '0.37 - 1.00', color: '#50B432', name: 'Great Progression' }
    ]
  }),

  computed: {
    ...mapState(['currentDate'])
  },

  watch: {
    currentDate() {
      this.refreshData();
    }
  },

  mounted() {
    setTimeout(() => {
      this.initPanelState = this.$store.state.hr.showPanel;
      this.$store.commit('hr/SHOW_PANEL', false);
    }, 100);
    this.refreshData();

    this.setInitialStartTime();
  },

  beforeDestroy() {
    if (this.initPanelState) {
      this.$store.commit('hr/SHOW_PANEL', true);
    }
  },

  methods: {
    refreshData() {
      this.fetchData();
    },

    overlaySelected() {
      this.fetchData();
    },

    timeChanged(time) {
      // Convert time string to date object
      let startTime = Utils.dateFromDateAndTimeString(this.currentDate, time);
      this.setStartTime(startTime, false);
    },

    setInitialStartTime() {
      let time = Utils.getSameTimeAsToday(this.currentDate);
      if (Utils.isToday(time)) {
        time = Utils.addSeconds(time, -1800);
      }
      this.setStartTime(time, true);
    },

    setStartTime(time, updateUI) {
      const roundedTime = Utils.roundToMinutes(time, 5);
      this.startTime = roundedTime.getTime();

      // Update Timer Picker
      if (updateUI) {
        const minutes = Utils.formatTimeToMinutes(roundedTime);
        if (this.$refs.timePicker) {
          this.$refs.timePicker.setTime(minutes);
        }
      }

      // Fetch the data
      this.fetchData();
    },

    fetchData() {
      const locations = this.$store.state.hr.locations;
      if (locations.length == 0) {
        this.fetchLocations().then(() => {
          this.showTimeSpaceDiagram();
        });
      } else {
        this.showTimeSpaceDiagram();
      }
    },

    showTimeSpaceDiagram() {
      const locations = this.$store.state.hr.locations;
      const ids = locations
        .filter(loc => loc.order)
        .sort((a, b) => a.order - b.order)
        .map(loc => loc.id);
      this.fetchTimeSpaceData(ids, this.startTime, this.duration, this.overlay);
    },

    fetchTimeSpaceData(ids, start, duration, overlay) {
      // console.log(`ids: ${ids}, start: ${start}, duration: ${duration}, overlay: ${overlay}`);
      try {
        this.loading = true;
        Api.fetchTimeSpaceData(ids, start, duration, overlay).then(response => {
          if (response.data.status === 'OK') {
            const data = response.data.data;
            if (this.showChartInGroup) {
              // Group the data by group
              const groups = data.reduce((r, item) => {
                r[item.group] = r[item.group] || [];
                r[item.group].push(item);
                return r;
              }, {});
              //console.log(groups);
              this.timeSpaceGroups = groups;
            } else {
              this.data = data;
            }
          } else {
            this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'error' });
          }
          this.loading = false;
        });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    ...mapActions('hr', ['fetchLocations'])
  }
};
</script>

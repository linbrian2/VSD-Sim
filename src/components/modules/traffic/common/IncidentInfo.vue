<template>
  <v-sheet outlined :color="borderColor">
    <v-card dense tile :color="statusColor" dark @click="handleClick">
      <v-card-title class="d-flex justify-space-between">
        <div>Incident {{ incident.id }}</div>
        <div class="caption grey--text">{{ incident.region }} {{ incident.route }} {{ incident.direction }}</div>
        <div class="caption grey--text"><v-icon small>mdi-clock</v-icon> {{ incident.duration }}min</div>
        <div>
          <v-chip small :color="incident.severityColor">
            <strong class="black--text">{{ incident.severity }}</strong>
          </v-chip>
        </div>
      </v-card-title>

      <v-card-actions class="mt-n5">
        <v-list-item class="grow">
          <v-list-item-content class="ml-n2">
            <v-list-item-title class="d-flex justify-start">
              <div class="caption grey--text">{{ formatDate(incident) }}</div>
            </v-list-item-title>
          </v-list-item-content>

          <v-row align="center" justify="end">
            <v-badge
              class="mr-1"
              v-for="(count, name, index) in incident.evidenceCounts"
              :color="getEvidenceColor(name)"
              :key="index"
              :content="count"
              inline
            >
              <v-icon small v-text="getEvidenceIcon(name)"></v-icon>
            </v-badge>
            <div v-if="incident.mitigation">
              <v-tooltip bottom>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon color="green" v-bind="attrs" v-on="on" @click="showMitigationSolutions(incident)"
                    >mdi-traffic-light</v-icon
                  >
                </template>
                <span>Mitigation Solutions</span>
              </v-tooltip>
            </div>
          </v-row>
        </v-list-item>
      </v-card-actions>
    </v-card>
  </v-sheet>
</template>

<script>
import Utils from '@/utils/Utils';
import Constants from '@/utils/constants/traffic';
export default {
  props: {
    incident: Object
  },

  computed: {
    borderColor() {
      return this.incident.selected ? 'yellow' : '';
    },
    statusColor() {
      return this.incident.status === 0 ? '#004D40' : '';
    }
  },

  filters: {
    time(time) {
      const d = new Date(time);
      const dd = Utils.formatDateWOYear(d);
      const tt = Utils.formatTimeAsMinute(d);
      return `${dd} ${tt}`;
    }
  },

  methods: {
    handleClick() {
      this.incident.selected = true;
      this.$emit('click', this.incident);
    },

    getEvidenceIcon(name) {
      const ICONS = {
        flow: Constants.DEVICE_TRAFFIC_ICON,
        bluetooth: Constants.DEVICE_BLUETOOTH_ICON,
        waze: Constants.DEVICE_WAZE_ICON,
        restriction: Constants.DEVICE_RESTRICTIONS_ICON,
        weather: Constants.DEVICE_WEATHER_ICON,
        alert: Constants.DEVICE_ALERT_ICON
      };
      return ICONS[name];
    },

    getEvidenceColor(name) {
      const ICONS = {
        flow: '#3F51B5',
        bluetooth: 'blue darken-4',
        waze: 'cyan accent-4',
        restriction: 'orange',
        weather: 'grey',
        alert: 'red'
      };
      return ICONS[name];
    },

    formatDate(incident) {
      const d1 = new Date(incident.startTime);
      const d2 = new Date(incident.endTime);

      const mm1 = Utils.formatDateWOYear(d1);
      const tt1 = Utils.formatTimeAsMinute(d1);
      const mm2 = Utils.formatDateWOYear(d2);
      const tt2 = Utils.formatTimeAsMinute(d2);

      let endTime = '';
      if (incident.status === 1) {
        endTime = mm1 === mm2 ? tt2 : `${mm2} ${tt2}`;
      }

      return `${mm1} ${tt1} - ${endTime}`;
    },

    showMitigationSolutions(item) {
      const id = item.id;
      this.$emit('mitigation', id);
    }
  }
};
</script>

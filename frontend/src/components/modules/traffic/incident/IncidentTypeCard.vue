<template>
  <v-card tile min-width="100%" min-height="200px">
    <v-card-title class="my-n2 pa-1 teal darken-2" v-if="title">
      <h5 class="text-center ml-5 overline">{{ title }}</h5>
    </v-card-title>

    <v-card-text class="mt-2 pb-0">
      <v-simple-table>
        <template v-slot:default>
          <tbody>
            <tr>
              <td><strong>Type:</strong></td>
              <td>
                <span>{{ item.type }}</span>
                <v-tooltip bottom color="blue-grey" max-width="400" v-if="item.reason">
                  <template v-slot:activator="{ on }">
                    <v-btn class="mt-n1 ml-3" icon v-on="on">
                      <v-icon color="orange">mdi-lightbulb-on-outline</v-icon>
                    </v-btn>
                  </template>
                  <span>{{ item.reason }}</span>
                </v-tooltip>
              </td>
            </tr>
            <tr>
              <td><strong>Proof:</strong></td>
              <td>
                <v-badge
                  class="mr-7"
                  v-for="(count, name, index) in item.evidenceCounts"
                  color="orange"
                  :key="index"
                  :content="count"
                  offset-x="5"
                  offset-y="22"
                  bordered
                >
                  <v-icon small v-text="getIcon(name)"></v-icon>
                </v-badge>
              </td>
            </tr>
            <tr>
              <td><strong>Severity:</strong></td>
              <td>
                <v-chip :color="item.severityColor" outlined label small
                  ><strong>{{ item.severity }}</strong>
                </v-chip>
              </td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      <slot></slot>
    </v-card-text>
  </v-card>
</template>

<script>
import Constants from '@/utils/constants/traffic';
export default {
  props: {
    title: String,
    item: Object
  },

  methods: {
    getIcon(name) {
      const ICONS = {
        flow: Constants.DEVICE_TRAFFIC_ICON,
        bluetooth: Constants.DEVICE_BLUETOOTH_ICON,
        waze: Constants.DEVICE_WAZE_ICON,
        restriction: Constants.DEVICE_RESTRICTIONS_ICON,
        weather: Constants.DEVICE_WEATHER_ICON,
        alert: Constants.DEVICE_ALERT_ICON
      };
      return ICONS[name];
    }
  }
};
</script>

<style lang="scss" scoped>
.text-center {
  font-weight: bold;
  text-align: center;
  color: white;
}
</style>

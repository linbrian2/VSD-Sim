<template>
  <div class="detectors">
    <v-data-table
      :height="height"
      fixed-header
      :headers="headers"
      :items="items"
      :items-per-page="itemsPerPage"
      hide-default-footer
      :item-class="itemRowBackground"
      @click:row="handleRowClick"
      class="elevation-1"
    >
      <template v-slot:[`item.status`]="{ item }">
        <v-chip small :color="'red'"
          ><strong class="black--text">{{ item.status }}</strong></v-chip
        >
      </template>
    </v-data-table>
    <!-- Data Display -->
    <div class="mx-0 pt-2">
      <div class="v-tabs theme--dark">
        <div
          role="tablist"
          class="v-item-group theme--dark v-slide-group v-tabs-bar teal--text text--accent-4"
          data-booted="true"
        >
          <div class="v-slide-group__prev v-slide-group__prev--disabled"><!----></div>
          <div class="v-slide-group__wrapper">
            <div class="v-slide-group__content v-tabs-bar__content">
              <div class="v-tabs-slider-wrapper" style="width: 90px; height: 2px; left: 0px;">
                <div class="v-tabs-slider"></div>
              </div>
              <div tabindex="0" aria-selected="true" role="tab" class="v-tab v-tab--active">
                <span class="v-chip v-chip--no-color theme--dark v-size--small"
                  ><span class="v-chip__content">SB</span></span
                >
              </div>
            </div>
          </div>
          <div class="v-slide-group__next v-slide-group__next--disabled"><!----></div>
        </div>
        <div class="v-window v-item-group theme--dark v-tabs-items">
          <div class="v-window__container" style="">
            <div class="v-window-item v-window-item--active">
              <div class="row">
                <div class="col col-6">
                  <div
                    data-v-a4ae34cc=""
                    class="mt-1 v-card v-sheet theme--dark rounded-0"
                    extra="SB"
                    style="min-height: 120px; min-width: 100%;"
                  >
                    <div data-v-a4ae34cc="" class="v-card__title d-flex justify-space-between my-n2 pa-1 teal darken-2">
                      <h5 data-v-a4ae34cc="" class="text-center ml-5">Current Flow</h5>
                      <!----><!---->
                    </div>
                    <div data-v-a4ae34cc="" class="v-card__text mt-1 pb-0 mb-n2">
                      <div data-v-a4ae34cc="" class="container">
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">speed</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">49</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">volume</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">32</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">occupancy</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">4</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col col-6">
                  <div
                    data-v-a4ae34cc=""
                    class="mt-1 v-card v-sheet theme--dark rounded-0"
                    extra="SB"
                    style="min-height: 120px; min-width: 100%;"
                  >
                    <div data-v-a4ae34cc="" class="v-card__title d-flex justify-space-between my-n2 pa-1 teal darken-2">
                      <h5 data-v-a4ae34cc="" class="text-center ml-5">Flow Status</h5>
                      <!----><!---->
                    </div>
                    <div data-v-a4ae34cc="" class="v-card__text mt-1 pb-0 mb-n2">
                      <div data-v-a4ae34cc="" class="container">
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">Status 0</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">No Delay</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">vol + occ</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">25%</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">Flow Level</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">0</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col col-6">
                  <div
                    data-v-a4ae34cc=""
                    class="mt-1 v-card v-sheet theme--dark rounded-0"
                    extra="SB"
                    style="min-height: 120px; min-width: 100%;"
                  >
                    <div data-v-a4ae34cc="" class="v-card__title d-flex justify-space-between my-n2 pa-1 teal darken-2">
                      <h5 data-v-a4ae34cc="" class="text-center ml-5">Anomaly Detection</h5>
                      <!----><!---->
                    </div>
                    <div data-v-a4ae34cc="" class="v-card__text mt-1 pb-0 mb-n2">
                      <div data-v-a4ae34cc="" class="container">
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">mdist</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate"></strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">severity</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate"></strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">Status</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">Normal</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col col-6">
                  <div
                    data-v-a4ae34cc=""
                    class="mt-1 v-card v-sheet theme--dark rounded-0"
                    extra="SB"
                    style="min-height: 120px; min-width: 100%;"
                  >
                    <div data-v-a4ae34cc="" class="v-card__title d-flex justify-space-between my-n2 pa-1 teal darken-2">
                      <h5 data-v-a4ae34cc="" class="text-center ml-5">Average Flow</h5>
                      <!----><!---->
                    </div>
                    <div data-v-a4ae34cc="" class="v-card__text mt-1 pb-0 mb-n2">
                      <div data-v-a4ae34cc="" class="container">
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">avg Speed</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">49</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">avg Volume</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">27</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">avg Occupancy</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">3</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col col-6">
                  <div
                    data-v-a4ae34cc=""
                    class="mt-1 v-card v-sheet theme--dark rounded-0"
                    extra="SB"
                    style="min-height: 120px; min-width: 100%;"
                  >
                    <div data-v-a4ae34cc="" class="v-card__title d-flex justify-space-between my-n2 pa-1 teal darken-2">
                      <h5 data-v-a4ae34cc="" class="text-center ml-5">Throughput</h5>
                      <!----><!---->
                    </div>
                    <div data-v-a4ae34cc="" class="v-card__text mt-1 pb-0 mb-n2">
                      <div data-v-a4ae34cc="" class="container">
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">Percentage</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">48%</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">5 Min</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">66</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">Per Hour</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">792</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col col-6">
                  <div
                    data-v-a4ae34cc=""
                    class="mt-1 v-card v-sheet theme--dark rounded-0"
                    extra="SB"
                    style="min-height: 120px; min-width: 100%;"
                  >
                    <div data-v-a4ae34cc="" class="v-card__title d-flex justify-space-between my-n2 pa-1 teal darken-2">
                      <h5 data-v-a4ae34cc="" class="text-center ml-5">Max Volume</h5>
                      <!----><!---->
                    </div>
                    <div data-v-a4ae34cc="" class="v-card__text mt-1 pb-0 mb-n2">
                      <div data-v-a4ae34cc="" class="container">
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">5 Min</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">150</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">1 Hr</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">1800</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                        <div data-v-a4ae34cc="">
                          <div data-v-a4ae34cc="" class="d-flex justify-space-between my-2">
                            <h4 data-v-a4ae34cc="">1 Hr Projected</h4>
                            <div data-v-a4ae34cc="">
                              <h3 data-v-a4ae34cc="" class="text-success">
                                <strong data-v-a4ae34cc="" class="text-truncate">384</strong>
                              </h3>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    height: { type: Number, default: 200 },
    data: Object
  },

  data: () => ({
    itemsPerPage: 3,
    headers: [
      { text: 'Detector', value: 'device' },
      { text: 'Status', value: 'status' }
    ],
    items: [
      { device: '19900 - ND0058 - Ramp G1 (Christiana Mall) @ St', status: 'Anomaly' },
      { device: '19901 - ND0036 - SR 1 @ SR 273', status: 'Anomaly' },
      { device: '19907 - ND0021 - I-95 @ Chapman Rd - NB', status: 'Anomaly' }
    ],
    selectedDevice: null
  }),

  filters: {
    time(time) {
      const d = new Date(time);
      const dd = Utils.formatDate(d);
      const tt = Utils.formatTimeAsMinute(d);
      return `${dd} ${tt}`;
    }
  },

  mounted() {
    this.handleRowClick(this.items[0]);
  },

  methods: {
    itemRowBackground(item) {
      return item.device == this.selectedDevice ? 'table_tr_selected' : 'table_tr_normal';
    },

    handleRowClick(item) {
      this.selectedDevice = item.device;
      this.$emit('click', item);
    }

    /* getEvidenceIcon(name) {
      const ICONS = {
        flow: Constants.DEVICE_TRAFFIC_ICON,
        bluetooth: Constants.DEVICE_BLUETOOTH_ICON,
        waze: Constants.DEVICE_WAZE_ICON,
        restriction: Constants.DEVICE_RESTRICTIONS_ICON,
        weather: Constants.DEVICE_WEATHER_ICON,
        alert: Constants.DEVICE_ALERT_ICON
      };
      return ICONS[name];
    }, */

    /* getEvidenceColor(name) {
      const ICONS = {
        flow: 'purple',
        bluetooth: 'blue darken-4',
        waze: 'cyan accent-4',
        restriction: 'orange',
        weather: 'grey',
        alert: 'red'
      };
      return ICONS[name];
    } */
  }
};
</script>

<style lang="scss">
/* .table_tr_selected {
  background-color: #eee;
}
.table_tr_normal {
  background-color: transparent !important;
}

.v-data-table > .v-data-table__wrapper > table > tbody > tr > th,
.v-data-table > .v-data-table__wrapper > table > thead > tr > th,
.v-data-table > .v-data-table__wrapper > table > tfoot > tr > th {
  font-size: 14px !important;
}

.v-expansion-panel-content__wrap {
  padding: 0 !important;
}

.table_tr_selected {
  background-color: #a9a9a9;
} */
</style>

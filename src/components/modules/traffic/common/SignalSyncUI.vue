<template>
  <div>
    <v-sheet outlined color="green" rounded v-if="show">
      <v-card>
        <v-system-bar window dark class="mb-2">
          <v-icon>mdi-traffic-light</v-icon>
          <span class="overline">Signal Controller</span>
          <v-spacer></v-spacer>
          <v-btn icon small @click="show = false">
            <v-icon small>mdi-close</v-icon>
          </v-btn>
        </v-system-bar>
        <v-container>
          <div class="d-flex justify-space-between align-center">
            <div class="d-flex align-center">
              <MenuButton
                :items="sync_menu_items"
                tooltip="Sync With ..."
                icon="mdi-sync"
                color="teal darken-3"
                :normal="true"
                @menuItemclick="syncMenuItemClicked"
              />
              <div class="ml-5" v-if="lastSyncTime">
                <span class="overline grey--text">Last sync: {{ formatTime(lastSyncTime) }}</span>
              </div>
            </div>

            <div>
              <v-btn small color="primary" @click="onFetchAll" :loading="fetchLoading">
                <v-icon small left>mdi-tray-arrow-down</v-icon>
                Fetch all patterns
              </v-btn>

              <v-btn small color="purple darken-5" class="ml-8 mr-10" @click="onRevertBack" :loading="revertLoading">
                <v-icon small left>mdi-arrow-u-left-top</v-icon>
                Revert to Tactics
              </v-btn>

              <v-tooltip top>
                <template v-slot:activator="{ on }">
                  <v-btn small fab class="ml-10" color="primary darken-2" v-on="on" @click="onCreateNewPattern">
                    <v-icon>mdi-plus</v-icon>
                  </v-btn>
                </template>
                <span>Create New Pattern</span>
              </v-tooltip>
            </div>
          </div>
        </v-container>
      </v-card>
    </v-sheet>
    <ConfirmDialog ref="confirm" />
    <SignalPatternDialog ref="pattern" />
  </div>
</template>

<script>
import Utils from '@/utils/Utils';
import Api from '@/utils/api/ntcip';
import MenuButton from '@/components/common/MenuButton';
import ConfirmDialog from '@/components/modules/traffic/common/ConfirmDialog';
import SignalPatternDialog from '@/components/modules/traffic/common/SignalPatternDialog';

export default {
  props: {
    value: Boolean,
    controller: Object
  },

  components: {
    MenuButton,
    ConfirmDialog,
    SignalPatternDialog
  },

  data: () => ({
    syncLoading: false,
    fetchLoading: false,
    revertLoading: false,
    lastSyncTime: null,
    currentPattern: null,
    sync_menu_items: [
      { title: 'Sync with controller', action: 'controller' },
      { title: 'Sync with Tactics', action: 'tactics' },
      { title: 'Sync with database', action: 'database' }
    ]
  }),

  computed: {
    show: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      }
    }
  },

  mounted() {
    this.$bus.$on('APPLY_PATTERN', ({ action, pattern }) => {
      this.actionSignalPattern(action, pattern);
    });

    this.$bus.$on('APPLY_PATTERNS', patterns => {
      this.onExecutePatterns(patterns);
    });
  },

  methods: {
    async onSync() {
      if (await this.$refs.confirm.open('Confirm', 'Are you sure you want to sync with the controller?')) {
        this.getCurrentPattern(this.controller.ip);
      }
    },

    async onSyncTactics() {
      if (await this.$refs.confirm.open('Confirm', 'Are you sure you want to sync with the Tactics?')) {
        //this.getCurrentPattern(this.controller.ip);
      }
    },

    async onSyncDatabase() {
      if (await this.$refs.confirm.open('Confirm', 'Are you sure you want to sync with the database?')) {
        //this.getCurrentPattern(this.controller.ip);
      }
    },

    async onFetchAll() {
      if (
        await this.$refs.confirm.open('Confirm', 'Are you sure you want to fetch all patterns from the controller?')
      ) {
        this.fetchAllPatterns(this.controller.ip, this.controller.patterns);
      }
    },

    async onRevertBack() {
      if (await this.$refs.confirm.open('Confirm', 'Are you sure you want to revert back to Tactics?')) {
        this.revertBack(this.controller.ip);
      }
    },

    async onCreateNewPattern() {
      // Initialize pattern id by adding 1 to the last pattern id
      const len = this.controller.patterns.length;
      const pattern = len > 0 ? parseInt(this.controller.patterns[len - 1]) + 1 : '111';
      if (await this.$refs.pattern.open('Create New Pattern', { pattern }, { constant: false })) {
        this.createPattern(this.controller.ip, this.$refs.pattern.get());
      }
    },

    syncMenuItemClicked(action) {
      switch (action) {
        case 'controller':
          this.onSync();
          break;
        case 'tactics':
          this.onSyncTactics();
          break;
        case 'database':
          this.onSyncDatabase();
          break;
      }
    },

    async onExecutePatterns(patterns) {
      const message = `Are you sure you want to apply the pattern onto all the controllers listed below?`;
      if (this.$refs.confirm && (await this.$refs.confirm.open('Confirm', message))) {
        this.executePatterns(patterns);
      }
    },

    async actionSignalPattern(action, signalItem) {
      const ip = this.controller.ip;
      if (ip) {
        if (action === 0) {
          const message = `Are you sure you want to apply the pattern <strong>${signalItem.pattern}</strong> onto the controller?`;
          if (this.$refs.confirm && (await this.$refs.confirm.open('Confirm', message))) {
            const patternNumber = parseInt(signalItem.pattern);
            this.executePattern(ip, patternNumber);
          }
        } else if (action === 1) {
          const message = `Are you sure you want to modify the pattern <strong>${signalItem.pattern}</strong> and apply onto the controller?`;
          if (this.$refs.confirm && (await this.$refs.confirm.open('Confirm', message))) {
            if (await this.$refs.pattern.open('Modify Pattern', signalItem, { constant: true })) {
              this.changePattern(ip, this.$refs.pattern.get());
            }
          }
        } else if (action === 2) {
          const message = `Are you sure you want to delete the pattern <strong>${signalItem.pattern}</strong> from the controller?`;
          if (this.$refs.confirm && (await this.$refs.confirm.open('Confirm', message))) {
            this.deletePattern(ip, signalItem);
          }
        }
      }
    },

    async changePattern(ip, params) {
      try {
        const res = await Api.changePattern(ip, params);
        let data = this.parseResponseData(res);
        this.$store.dispatch('setSystemStatus', { text: data.Message, color: 'info' });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async createPattern(ip, params) {
      try {
        const res = await Api.createPattern(ip, params);
        let data = this.parseResponseData(res);
        if (data) {
          const signal = {
            pattern: params.patternNumber,
            cycle: params.cycleLength,
            offset: params.offset,
            P1: params.phaseTime[0],
            P2: params.phaseTime[1],
            P3: params.phaseTime[2],
            P4: params.phaseTime[3],
            P5: params.phaseTime[4],
            P6: params.phaseTime[5],
            P7: params.phaseTime[6],
            P8: params.phaseTime[7]
          };
          this.$bus.$emit('UPDATE_SIGNAL_TABLE', { action: 1, signal });
        }
        this.$store.dispatch('setSystemStatus', { text: data.Message, color: 'info' });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async executePatterns(patterns) {
      try {
        const res = await Api.executePatterns({ data: patterns });
        let data = this.parseResponseData(res);
        this.$store.dispatch('setSystemStatus', { text: data.Message, color: 'info' });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async executePattern(ip, patternNumber) {
      try {
        const res = await Api.executePattern(ip, patternNumber);
        let data = this.parseResponseData(res);
        this.$store.dispatch('setSystemStatus', { text: data.Message, color: 'info' });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async deletePattern(ip, signalItem) {
      try {
        const patternNumber = parseInt(signalItem.pattern);
        const res = await Api.deletePattern(ip, patternNumber);
        let data = this.parseResponseData(res);
        this.$bus.$emit('UPDATE_SIGNAL_TABLE', { action: 2, signal: signalItem });
        this.$store.dispatch('setSystemStatus', { text: data.Message, color: 'info' });
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
    },

    async getCurrentPattern(ip) {
      this.syncLoading = true;
      try {
        const res = await Api.getCurrentPatternNumber(ip);
        const data = this.parseResponseData(res);
        if (data) {
          if (data.pattern && data.pattern.Code != -1) {
            this.currentPattern = data.pattern.Message;
            this.lastSyncTime = new Date();
            this.$bus.$emit('UPDATE_CURRENT_PATTERN', { pattern: this.currentPattern });
          } else {
            this.$store.dispatch('setSystemStatus', { text: data.pattern.Message, color: 'info' });
          }
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.syncLoading = false;
    },

    async fetchAllPatterns(ip, patterns) {
      this.fetchLoading = true;
      try {
        this.$bus.$emit('UPDATE_SIGNAL_TABLE', { action: 0, signal: [] });
        const res = await Api.getPatterns(ip, patterns);
        let data = this.parseResponseData(res);
        console.log(data);
        if (data && Object.keys(data.patterns).length > 0) {
          const signals = this.composeSignals(data.patterns);
          this.$bus.$emit('UPDATE_SIGNAL_TABLE', { action: 0, signal: signals });
        }
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.fetchLoading = false;
    },

    async revertBack(ip) {
      this.revertLoading = true;
      try {
        const res = await Api.revertBack(ip);
        this.parseResponseData(res);
      } catch (error) {
        this.$store.dispatch('setSystemStatus', { text: error, color: 'error' });
      }
      this.revertLoading = false;
    },

    parseResponseData(response) {
      let result = null;
      if (response.status === 200) {
        if (response.data) {
          //console.log(response.data);
          if (response.data.status === 'OK') {
            if (response.data.data !== undefined) {
              result = response.data.data;
            }
          } else {
            if (response.data.message) {
              this.$store.dispatch('setSystemStatus', { text: response.data.message, color: 'warning' });
            }
          }
        } else {
          this.$store.dispatch('setSystemStatus', { text: 'No response data', color: 'warning' });
        }
      } else {
        this.$store.dispatch('setSystemStatus', { text: 'Response failure!', color: 'error' });
      }
      return result;
    },

    composeSignals(patterns) {
      const signals = [];
      Object.keys(patterns).forEach(patternId => {
        const result = patterns[patternId];
        const params = {
          pattern: patternId,
          cycle: result.cycleLength,
          offset: result.offset,
          action: 'action',
          controller: true
        };

        for (let i = 0; i < 8; i++) {
          const phaseId = i + 1;
          params['P' + phaseId] = parseInt(result.phases[i]);
          params['M' + phaseId] = 0;
        }
        signals.push(params);
      });
      return signals;
    },

    formatTime(d) {
      return Utils.formatAMPMTime(d);
    }
  }
};
</script>

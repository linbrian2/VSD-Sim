<template>
  <v-navigation-drawer app clipped right ref="drawer" v-model="showPanel" :width="navigationWidth">
    {{ navigationWidth }}<br />
    {{ width }}
    <v-toolbar dense flat fixed overflow @click="changeTable">
      <v-toolbar-title class="action-title">
        <v-icon dark class="mt-n1 mr-2" v-if="icon">{{ icon }}</v-icon>
        {{ title }}
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <v-tooltip bottom v-if="tableButton">
          <template v-slot:activator="{ on, attrs }">
            <v-icon color="primary" class="mr-10" :loading="loading" v-bind="attrs" v-on="on">
              {{ showTable ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
            </v-icon>
          </template>
          <span>Expand List</span>
        </v-tooltip>

        <slot name="buttons"></slot>
        <v-btn icon small @click="showPanel = false" :loading="loading"><v-icon>mdi-close</v-icon></v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <slot></slot>
  </v-navigation-drawer>
</template>

<script>
import Utils from '@/utils/Utils';
import { mapGetters } from 'vuex';
export default {
  props: {
    tableButton: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
    width: {
      type: Number,
      default: 600
    },
    name: {
      type: String,
      default: ''
    },
    title: String,
    icon: {
      type: String,
      default: ''
    }
  },
  data: () => ({
    navigation: {
      shown: false,
      borderSize: 5
    }
  }),

  computed: {
    showPanel: {
      get() {
        return this.$store.state.traffic.showPanel;
      },
      set(show) {
        this.$store.commit('traffic/SHOW_PANEL', show);
      }
    },
    navigationWidth: {
      get() {
        return this.$store.state.navigationWidth;
      },
      set(width) {
        this.$store.commit('SET_NAV_WIDTH', width);
      }
    },
    showTable: {
      get() {
        return this.$store.state.dashboard.showTable;
      },
      set(show) {
        this.$store.commit('dashboard/SHOW_TABLE', show);
      }
    },
    ...mapGetters(['getSetting'])
  },

  mounted() {
    this.navigationWidth = this.width;

    this.setBorderWidth();
    this.setEvents();
  },

  created() {
    window.addEventListener('keypress', this.resizePanel);
    this.loadConfig();
  },

  destroyed() {
    window.removeEventListener('keypress', this.resizePanel);
    this.saveConfig();
  },

  watch: {
    navigationWidth(value) {
      this.$emit('navigation-width-changed', value);
    },
    width(width) {
      this.navigationWidth = width;
    }
  },

  methods: {
    changeTable() {
      setTimeout(() => {
        if (this.showPanel) {
          this.showTable = !this.showTable;
        }
      }, 10);
    },
    loadConfig() {
      if (this.name) {
        let value = Utils.loadFromLocalStorage(this.name);
        if (value) {
          value = value + '';
          const str = value.replace('px', '');
          const width = parseInt(str);
          if (Utils.isNumber(width)) {
            this.navigationWidth = width;
          }
        }
      }
    },

    saveConfig() {
      if (this.name) {
        Utils.saveToLocalStorage(this.name, this.navigationWidth);
      }
    },

    showPanelIfNot() {
      if (!this.showPanel) {
        this.$store.commit('traffic/SHOW_PANEL', true);
      }
    },

    resizePanel(e) {
      const key = String.fromCharCode(e.keyCode).toLowerCase();
      if (key === 'l') {
        this.resizeIt(-20);
      } else if (key === 'r') {
        this.resizeIt(20);
      }
    },

    resizeIt(length) {
      this.navigationWidth = this.navigationWidth + length;
      if (this.navigationWidth < 200) {
        this.navigationWidth = 200;
      }
      if (this.navigationWidth > window.innerWidth - 200) {
        this.navigationWidth = window.innerWidth - 200;
      }
    },

    setBorderWidth() {
      let i = this.$refs.drawer.$el.querySelector('.v-navigation-drawer__border');
      i.style.width = this.navigation.borderSize + 'px';
      i.style.cursor = 'ew-resize';
      // i.style.backgroundColor = 'red';
    },

    setEvents() {
      // https://codepen.io/oze4/pen/mojrZM?editors=1010
      const minSize = this.navigation.borderSize;
      const el = this.$refs.drawer.$el;
      const drawerBorder = el.querySelector('.v-navigation-drawer__border');
      const direction = el.classList.contains('v-navigation-drawer--right') ? 'right' : 'left';

      function resize(e) {
        e.preventDefault();
        document.body.style.cursor = 'ew-resize';
        let f = direction === 'right' ? document.body.scrollWidth - e.clientX : e.clientX;
        el.style.width = f + 'px';
      }

      drawerBorder.addEventListener(
        'mousedown',
        e => {
          if (e.stopPropagation) e.stopPropagation();
          if (e.preventDefault) e.preventDefault();
          if (e.offsetX < minSize) {
            el.style.transition = 'initial';
            document.addEventListener('mousemove', resize, false);
          }
        },
        false
      );

      document.addEventListener(
        'mouseup',
        e => {
          e.preventDefault();
          el.style.transition = '';
          this.navigationWidth = el.style.width;
          document.body.style.cursor = '';
          document.removeEventListener('mousemove', resize, false);
        },
        false
      );
    }
  }
};
</script>

<style lang="scss" scoped>
.action-title {
  font-weight: bold;
  font-size: 16px;
  text-transform: uppercase;
}
</style>

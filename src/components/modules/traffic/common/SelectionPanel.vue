<template>
  <v-navigation-drawer app clipped ref="drawer" v-model="showPanel" :width="navigationWidth">
    <slot></slot>
  </v-navigation-drawer>
</template>

<script>
import Utils from '@/utils/Utils';
export default {
  props: {
    width: {
      type: Number,
      default: 600
    },
    name: {
      type: String,
      default: ''
    }
  },
  data: () => ({
    navigationWidth: 600,
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
    }
  },

  mounted() {
    if (!this.name) {
      this.navigationWidth = this.width;
    }

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
      this.$bus.$emit('CHART_RELOAD');
    }
  },

  methods: {
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

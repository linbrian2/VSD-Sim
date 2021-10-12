<template>
  <div>
    <v-icon v-if="isMdi" v-text="icon" />
    <v-img v-else :src="getIcon(icon)" width="24" height="24" />
  </div>
</template>

<script>
import { appIcons } from '@/mixins/appIcons';
export default {
  mixins: [appIcons],

  props: {
    icon: String
  },

  computed: {
    isMdi() {
      return this.icon.startsWith('mdi-');
    }
  },

  methods: {
    getIcon(name) {
      return this.getAppIconByName(name, this.$store.state.darkMode);
    }
  }
};
</script>

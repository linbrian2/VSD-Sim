<template>
  <v-list-item :to="item.path">
    <v-list-item-icon v-if="item.icon">
      <v-tooltip bottom>
        <template #activator="{ on, attrs }">
          <NavIcon v-bind="attrs" v-on="on" :icon="item.icon" />
        </template>
        <span v-text="item.title" />
      </v-tooltip>
    </v-list-item-icon>
    <v-list-item-content>
      <v-list-item-title v-text="item.title" />
    </v-list-item-content>
    <v-list-item-action v-if="item.isNew">
      <v-icon color="green">mdi-new-box </v-icon>
    </v-list-item-action>
  </v-list-item>
</template>

<script>
import NavIcon from '@/components/nav//NavIcon';
export default {
  props: {
    item: Object
  },
  components: {
    NavIcon
  },
  computed: {}
};
</script>

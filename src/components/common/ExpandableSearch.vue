<template>
  <v-autocomplete
    @focus="searchClosed = false"
    @blur="searchClosed = true"
    light
    dense
    flat
    solo
    clearable
    class="test_cursor expanding-search"
    :class="{ closed: searchClosed }"
    v-model="select"
    single-line
    hide-details
    prepend-inner-icon="mdi-magnify"
    append-icon=""
    placeholder="Search"
    hide-no-data
    :search-input.sync="search"
    return-object
    :items="searchItems"
    item-text="desc"
    item-value="id"
    @change="onSearchChange"
  />
</template>

<script>
export default {
  props: {
    entities: Array
  },

  data: () => ({
    loading: false,
    select: null,
    search: null,
    searchItems: [],
    searchClosed: true
  }),

  computed: {},

  watch: {
    select() {
      this.$nextTick(() => {
        this.select = null;
      });
    },

    search(val) {
      if (val) {
        if (!this.isLoading) {
          this.isLoading = true;
          const key = val.toLowerCase();
          this.searchItems = this.entities.filter(item => item.desc.toLowerCase().includes(key));
          this.loading = false;
        }
      }
    }
  },

  methods: {
    onSearchChange(item) {
      if (item) {
        this.$emit('search', item);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.test_cursor input {
  cursor: pointer;
}

.v-input.expanding-search {
  transition: max-width 0.3s;
}

.v-input.expanding-search.closed {
  max-width: 30px;
}
</style>

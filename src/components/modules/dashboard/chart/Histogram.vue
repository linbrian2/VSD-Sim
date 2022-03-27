<template>
  <div class="histogram">
    <graph-area-timerange
      theme="dark"
      shape="step"
      :width="350"
      :height="300"
      :axis-min="0"
      :axis-max="200"
      :axis-reverse="false"
      :axis-format="'HH:mm'"
      :axis-interval="1000 * 60 * 60 * 2"
      :labels="labels"
      :values="values"
    >
      <guideline :tooltip-x="true" :tooltip-y="true"></guideline>
    </graph-area-timerange>
  </div>
</template>

<script>
export default {
  name: 'Histogram',
  props: ['labels', 'values']
};
</script>

<style lang="scss" scoped>
.histogram {
  padding-left: 5px;
  padding-bottom: 10px;
}
</style>

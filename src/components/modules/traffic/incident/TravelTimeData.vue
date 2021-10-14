<template>
  <div v-if="filteredSegments.length > 0">
    <SlideButtons
      class="mb-4"
      :items="filteredSegments"
      :title="evidenceText"
      :show="isEvidenceVisible"
      :callback="segmentClicked"
      :showEvidenceText="showEvidenceText"
      @expand="isEvidenceVisible = !isEvidenceVisible"
      ref="slideButtons"
    />

    <ExpansionItems
      :name="selectSegmentName"
      :infoList="travelTimeListBySegment"
      :show="isEvidenceVisible"
      @close="hideEvidences"
    />

    <div class="mt-4" v-if="selectedSegment">
      <TravelTimeCharts
        ref="travelTimeCharts"
        :height="height"
        :linkId="selectedSegment.linkId"
        :name="selectSegmentName"
        :interval="interval"
        :time="time"
        :timeSlots="timeSlots"
      />
    </div>
  </div>
</template>

<script>
import ExpansionItems from '@/components/modules/traffic/incident/ExpansionItems';
import SlideButtons from '@/components/modules/traffic/incident/SlideButtons';
import TravelTimeCharts from '@/components/modules/traffic/incident/TravelTimeCharts';

export default {
  props: {
    infoList: Array,
    segments: Array,
    selectedSegmentId: { type: String, default: null },
    time: Date,
    timeSlots: Array,
    interval: Number,
    showEvidenceText: { type: Boolean, default: true },
    height: { type: Number, default: 380 }
  },

  components: {
    ExpansionItems,
    SlideButtons,
    TravelTimeCharts
  },

  data: () => ({
    isEvidenceVisible: false,
    selectedSegment: null
  }),

  computed: {
    selectSegmentName() {
      return this.selectedSegment ? this.selectedSegment.name : null;
    },

    travelTimeListBySegment() {
      if (this.selectedSegment) {
        return this.infoList.filter(item => item.linkId === this.selectedSegment.linkId);
      } else {
        return null;
      }
    },

    evidenceText() {
      const infoList = this.travelTimeListBySegment;
      return infoList ? `${infoList.length} Evidence${infoList.length > 1 ? 's' : ''}` : '';
    },

    filteredSegments() {
      if (this.selectedSegmentId === null) {
        return this.segments;
      } else {
        return this.segments.filter(item => item.linkId === this.selectedSegmentId);
      }
    }
  },

  watch: {
    filteredSegments(segments) {
      this.selectFirst(segments);
    }
  },

  mounted() {
    this.selectFirst(this.filteredSegments);
  },

  methods: {
    selectFirst(segments) {
      const firstItem = segments.length > 0 ? segments[0] : null;
      if (firstItem) {
        this.segmentClicked(firstItem);
      }
    },

    selectItem(itemName) {
      const item = this.filteredSegments.find(item => item.name === itemName);
      if (item) {
        this.segmentClicked(item);
        this.$refs.slideButtons.selectItem(itemName);
      }
    },

    segmentClicked(item) {
      this.selectedSegment = item;
      this.$bus.$emit('INCIDENT_ITEM_SELECTED', item);
      this.updateTravelTimeCharts();
    },

    hideEvidences() {
      this.isEvidenceVisible = false;
    },

    updateTravelTimeCharts() {
      if (this.selectedSegment) {
        const linkId = this.selectedSegment.linkId;
        if (this.$refs.travelTimeCharts) {
          this.$refs.travelTimeCharts.init(linkId, this.time, this.timeSlots);
        }
      }
    }
  }
};
</script>

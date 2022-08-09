<template>
  <div>
    <TitleBar title="Service Health Check" :loading="loading" :refresh="refreshData">
      <div class="update" v-if="updatedTime">{{ updatedTime | date }}</div>
    </TitleBar>
    <v-container fluid style="max-width: 85%">
      <v-card class="rounded-0 elevation-5 mt-4 mb-10">
        <v-card-title :class="['py-1 font-weight-regular', dataCollectionLabelColor]"
          >Data Collection Services</v-card-title
        >
        <v-card-text>
          <v-container fluid>
            <SummaryTable :loading="loading" :summary="dataCollectionServices" />
          </v-container>
        </v-card-text>
      </v-card>

      <v-card class="rounded-0 elevation-5">
        <v-card-title :class="['py-1 font-weight-regular', dataProcessingLabelColor]"
          >Data Processing Services</v-card-title
        >
        <v-card-text class="mb-10">
          <v-container fluid>
            <SummaryTable :loading="loading" :summary="dataProcessingServices" />
          </v-container>
        </v-card-text>
      </v-card>

      <v-card class="rounded-0 elevation-5">
        <v-card-title :class="['py-1 font-weight-regular', infrastructureLabelColor]"
          >Infrastructure Services</v-card-title
        >
        <v-card-text class="mb-10">
          <v-container fluid>
            <ViewTable :loading="loading" :summary="infrastructureServices" />
          </v-container>
        </v-card-text>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import Api from '@/utils/api/status';
import Utils from '@/utils/Utils';
import TitleBar from '@/components/modules/status/TitleBar';
import SummaryTable from '@/components/modules/status/SummaryTable';
import ViewTable from '@/components/modules/status/ViewTable';
export default {
  components: {
    TitleBar,
    SummaryTable,
    ViewTable
  },
  data: () => ({
    loading: false,
    updatedTime: null,
    timeId: null,
    interval: 300000,
    summary: []
  }),
  computed: {
    dataCollectionServices() {
      return this.summary.filter(service => service.group === 0);
    },

    dataProcessingServices() {
      return this.summary.filter(service => service.group === 1);
    },

    infrastructureServices() {
      return this.summary.filter(service => service.group === 2);
    },

    dataCollectionLabelColor() {
      const notAllLive = this.dataCollectionServices.some(service => !service.isLive);
      return notAllLive ? 'red lighten-1' : 'green lighten-1';
    },

    dataProcessingLabelColor() {
      const notAllLive = this.dataProcessingServices.some(service => !service.isLive);
      return notAllLive ? 'red lighten-1' : 'green lighten-1';
    },

    infrastructureLabelColor() {
      const notAllLive = this.dataProcessingServices.some(service => !service.isLive);
      return notAllLive ? 'red lighten-1' : 'green lighten-1';
    }
  },
  filters: {
    date: function(date) {
      return 'Updated time: ' + Utils.formatDateAndTime(date);
    }
  },
  async mounted() {
    this.refreshData();
    this.timeId = setInterval(() => {
      this.refreshData();
    }, this.interval);
  },
  beforeDestroy() {
    if (this.timeId != null) {
      clearInterval(this.timeId);
    }
  },
  methods: {
    refreshData() {
      this.fetchStatus();
    },
    async fetchStatus() {
      this.loading = true;
      try {
        const response = await Api.fetchStatus();
        console.log(response.data);
        this.summary = response.data;
        this.updatedTime = new Date();
      } catch (error) {
        console.log(error);
      }
      this.loading = false;
    }
  }
};
</script>

<style lang="scss" scoped>
.update {
  font-size: 13px !important;
  position: absolute;
  right: 80px;
  margin-top: 12px;
}
</style>

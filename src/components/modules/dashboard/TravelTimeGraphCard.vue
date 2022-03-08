<template>
  <v-card class="pb-3 px-1" v-if="apiInfo">
    <div class="card-container">
      <v-btn icon @click="detailF" class="info-button"> <v-icon class="pt-2">mdi-information-outline</v-icon></v-btn>
    </div>
    <div class="card-container">
      <v-btn icon @click="exportF" class="export-button"> <v-icon class="pt-2">mdi-export-variant</v-icon></v-btn>
    </div>
    <div class="graph grid-center">
      <template v-if="showHighcharts && lineData">
        <BasicChart :data="selectIdx < 4 ? lineData : lineData2" :height="400" :width="560" :showTitle="true" />
      </template>
      <template v-else>
        <Histogram
          v-if="apiInfo != null"
          :labels="getLabels(apiInfo.data[selectVal].labels)"
          :values="getValues(apiInfo.data[selectVal].values)"
        />
      </template>
    </div>
  </v-card>
</template>

<script>
import BasicChart from '@/components/modules/dashboard/graphs/BasicChart';
import Histogram from '@/components/modules/dashboard/graphs/Histogram';

import { mapState } from 'vuex';
import Utils from '@/utils/Utils';
import lineData from '@/utils/LineChartSampleData2';

export default {
  props: ['apiInfo', 'selection'],
  components: {
    BasicChart,
    Histogram
  },
  data() {
    return {
      selectedCorr: null,
      corridors: [
        'I-95 NORTHBOUND',
        'I-95 SOUTHBOUND',
        'I-295 NORTHBOUND',
        'I-295 SOUTHBOUND',
        'I-495 NORTHBOUND',
        'I-495 SOUTHBOUND',
        'I-895 NORTHBOUND',
        'I-895 SOUTHBOUND'
      ],
      lineData: lineData
    };
  },
  mounted() {
    this.selectedCorr = this.corridors[this.selectIdx];
  },
  computed: {
    selectIdx() {
      return this.selection[1];
    },
    selectVal() {
      return this.selection[0];
    },
    ...mapState('dashboard', ['showHighcharts'])
  },
  methods: {
    /* getLineDataByName(lineData, name) {
      console.log(lineData);
      console.log(lineData.data);
      let data = null;
      if (name == 'i95n') {
        data = lineData.data.filter(x => x.name == 'I-95');
      }
      if (name == 'i495s') {
        data = lineData.data.filter(x => x.name == 'I-495');
      }
      return {
        data,
        // title: 'Travel Time',
        xAxis: lineData.xAxis,
        xmin: lineData.xmin,
        yAxis: 'Travel Time (s)'
      };
    }, */
    getLabels(data) {
      console.log(data);
      return [new Date(data.startDate), new Date(data.endDate)];
    },
    getValues(data) {
      var newData = [];
      console.log(data);
      data.forEach(point => {
        newData.push([new Date(point.time), point.value]);
      });
      console.log(newData);
      return newData;
    },
    detailF() {
      this.$emit('showDetail');
    },
    exportF() {
      let item = 'I-95';
      if (this.selectVal == 'i95n') {
        item = 'I-95';
      }
      if (this.selectVal == 'i495s') {
        item = 'I-495';
      }
      let name = `Travel Time - ${item}`;
      let data = this.getLineDataByName(this.lineData, this.selectVal);
      Utils.downloadJSON(name, data);
    }
  }
};
</script>

<style>
.card-container {
  position: relative;
}
.info-button {
  position: absolute;
  top: 8px;
  right: 40px;
  z-index: 99;
}
.export-button {
  position: absolute;
  top: 5px;
  right: 10px;
  z-index: 99;
}
</style>

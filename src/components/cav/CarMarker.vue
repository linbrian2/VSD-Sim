<template>
  <div>
    <!-- Car -->
    <GmapMarker
      v-if="visible"
      :position="position"
      :clickable="true"
      :draggable="false"
      :icon="carIcon"
      @click="markerClicked"
    />

    <!-- Infowindow -->
    <GmapInfoWindow :options="infoOptions" :position="position" :opened="infoWinOpen" @closeclick="infoWinOpen = false">
      <div v-html="infoContent"></div>
    </GmapInfoWindow>
  </div>
</template>

<script>
/* global google */
import Format from '@/utils/Format';
import { LatLng, computeDistanceBetween, computeHeading } from 'spherical-geometry-js';
export default {
  props: {
    visible: Boolean,
    info: Object,
    position: Object,
    color: {
      type: String,
      default: 'red'
    },
    minMoveDist: {
      type: Number,
      default: 5
    }
  },

  data: function() {
    return {
      // Car Icon
      carIcon: {
        path:
          'M17.402,0H5.643C2.526,0,0,3.467,0,6.584v34.804c0,3.116,2.526,5.644,5.643,5.644h11.759c3.116,0,5.644-2.527,5.644-5.644 V6.584C23.044,3.467,20.518,0,17.402,0z M22.057,14.188v11.665l-2.729,0.351v-4.806L22.057,14.188z M20.625,10.773 c-1.016,3.9-2.219,8.51-2.219,8.51H4.638l-2.222-8.51C2.417,10.773,11.3,7.755,20.625,10.773z M3.748,21.713v4.492l-2.73-0.349 V14.502L3.748,21.713z M1.018,37.938V27.579l2.73,0.343v8.196L1.018,37.938z M2.575,40.882l2.218-3.336h13.771l2.219,3.336H2.575z M19.328,35.805v-7.872l2.729-0.355v10.048L19.328,35.805z',
        scale: 0.7,
        strokeColor: 'black',
        strokeWeight: 0.6,
        fillOpacity: 1,
        fillColor: this.color,
        offset: '50%'
      },
      lastPos: null,
      infoContent: 'Hello',
      infoWinOpen: false,
      infoWinTimer: null,
      //optional: offset infowindow so it visually sits nicely on top of our marker
      infoOptions: {
        pixelOffset: {
          width: 8,
          height: -3
        },
        disableAutoPan: true
      }
    };
  },

  watch: {
    position(pos) {
      if (this.update(this.lastPos, pos)) {
        this.lastPos = pos;
      }
    }
  },

  methods: {
    update(lastPos, pos) {
      if (!lastPos) {
        return true;
      }

      // Check distance
      let lastPosN = new LatLng(lastPos.lat, lastPos.lng);
      let curPosN = new LatLng(pos.lat, pos.lng);
      if (computeDistanceBetween(lastPosN, curPosN) < this.minMoveDist) {
        // No need to update the car display
        return false;
      }

      // Calculate the car heading direction
      let heading = computeHeading(lastPosN, curPosN);
      let icon = Object.assign({}, this.carIcon);
      icon.rotation = heading;
      icon.anchor = new google.maps.Point(10, 25); // orig 10,50 back of car, 10,0 front of car, 10,25 center of car
      this.carIcon = icon;

      return true;
    },

    markerClicked() {
      if (this.info) {
        const item = this.formatInfo(this.info);
        const content = this.buildInfoContent(item);
        this.showInfoWindow(content);
      }
    },

    showInfoWindow(content) {
      this.infoWinOpen = true;
      this.infoWinPos = this.position;
      this.infoContent = content;

      // Set a timer to close this info window after certain period of time
      if (this.infoWinTimer) {
        clearTimeout(this.infoWinTimer);
      }
      this.infoWinTimer = setTimeout(() => {
        this.infoWinOpen = false;
      }, 6000);
    },

    buildInfoContent(item) {
      const html = `
      <div class="content"><h3>Device ${item.deviceId}</h3>
      <hr class="new">
      <table class="blueTable">
        <tbody>
          <tr><td><h4>Speed:</h4></td><td>${item.speed}</td></tr>
          <tr><td><h4>Duration:</h4></td><td>${item.duration}</td></tr>
          <tr><td><h4>Distance:</h4></td><td>${item.distance}</td></tr>
          <tr><td><h4>LastUpdated:</h4></td><td>${item.lastUpdated}</td></tr>
        </tbody>
      </table>
      </div>
      `;
      return html;
    },

    formatInfo(info) {
      return {
        tripId: info.id,
        deviceId: info.deviceId,
        lastUpdated: Format.fromatTimestamp(new Date(info.lastUpdated)),
        startTime: Format.fromatTimestamp(new Date(info.startTime)),
        endTime: Format.fromatTimestamp(new Date(info.endTime)),
        distance: info.distance ? Format.formatDistance(info.distance) : undefined,
        duration: info.duration ? Format.formatDuration(info.duration, false) : undefined,
        speed: info.speed !== undefined ? info.speed + ' mph' : undefined
      };
    }
  }
};
</script>

<style lang="scss">
.content {
  margin: 5px 10px;
}
hr.new {
  border-top: 1px dashed grey;
  margin: 5px 0px;
}
table.blueTable {
  width: 100%;
  text-align: left;
  border-collapse: collapse;
}
table.blueTable td,
table.blueTable th {
  padding: 5px 10px 5px 0px;
}
table.blueTable tbody td {
  font-size: 13px;
}
</style>

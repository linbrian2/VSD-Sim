<template>
  <!-- Infowindow -->
  <GmapInfoWindow :options="infoOptions" :position="position" :opened="infoWinOpen" @closeclick="infoWinOpen = false">
    <div v-html="infoContent"></div>
  </GmapInfoWindow>
</template>

<script>
export default {
  props: {
    position: Object
  },

  data: function() {
    return {
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

  methods: {
    show(info) {
      if (info) {
        const content = this.buildInfoContent(info);
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
      }, 3000);
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

    buildInfoContent1(info) {
      const html = `
       <div>
        <div class="Infobox no-title no-close">
          <a class="infobox-close" href="javascript:void(0)">
            <img
              class="infobox-close-img"
              src="data:image/svg+xml;base64,PHN2ZyBoZWlnaHQ9IjE0cHgiIHdpZHRoPSIxNHB4IiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB2ZXJzaW9uPSIxLjEiPjxwYXRoIGQ9Ik03LDBDMy4xMzQsMCwwLDMuMTM0LDAsN2MwLDMuODY3LDMuMTM0LDcsNyw3YzMuODY3LDAsNy0zLjEzMyw3LTdDMTQsMy4xMzQsMTAuODY3LDAsNywweiBNMTAuNSw5LjVsLTEsMUw3LDhsLTIuNSwyLjVsLTEtMUw2LDdMMy41LDQuNWwxLTFMNyw2bDIuNS0yLjVsMSwxTDgsN0wxMC41LDkuNXoiLz48L3N2Zz4="
              alt="close infobox"
            />
          </a>
          <div class="infobox-body">
            <div class="infobox-title">Detectors</div>
            <div class="infobox-info">Traffic Flow</div>
            <div class="infobox-actions">
              <ul class="infobox-actions-list">
                <div></div>
              </ul>
            </div>
          </div>
          <div class="infobox-stalk" style=""></div>
        </div>
        <div class="InfoboxCustom" style="">
          <div class="infobox-body">
            <div>
              <div class="tlp-incident">
                <div class="incidentSeverity mild" data-tag="severity">
                  <div class="tlp-incidentTitle" data-tag="incidentTitle">
                    <div class="tlp-iconContainer">
                      <svg width="13px" height="13px" viewBox="0 0 60 54" version="1.1" xmlns="http://www.w3.org/2000/svg">
                        <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                          <g id="Traffic-(4)-2" fill-rule="nonzero">
                            <g id="Traffic-(4)">
                              <path
                                d="M26.7294,0.82323 C29.8462,-0.85703 33.7371,0.11931 35.6491,2.9829 L35.8973,3.385 L59.1553,44.2548 C59.7092,45.2283 59.9999,46.3228 59.9999,47.4355 C59.9999,50.9099 57.2266,53.7539 53.7171,53.9849 L53.2553,54 L6.7458,54 C5.6028,54 4.4785,53.7173 3.47857,53.1784 C0.36158,51.4985 -0.85256,47.7704 0.62691,44.6736 L0.84542,44.2555 L24.097,3.3857 C24.71,2.3081 25.6223,1.4201 26.7294,0.82323 Z"
                                id="alertPath"
                              ></path>
                              <path
                                d="M29.9999,39.3921 C31.6532,39.3921 32.9935,40.6966 32.9935,42.3058 C32.9935,43.915 31.6532,45.2194 29.9999,45.2194 C28.3466,45.2194 27.0064,43.915 27.0064,42.3058 C27.0064,40.6966 28.3466,39.3921 29.9999,39.3921 Z"
                                id="Path"
                                fill="#FFFFFF"
                              ></path>
                              <path
                                d="M29.9873,17.5039993 C31.1255,17.5031 32.0668,18.3256 32.2166,19.3935 L32.2373,19.6904 L32.2481007,32.824 C32.2491,34.0325 31.2434,35.013 30.0017,35.0139007 C28.8636,35.0148 27.9223,34.1924 27.7725,33.1244 L27.7518,32.8275 L27.7409993,19.6939 C27.74,18.4854 28.7457,17.505 29.9873,17.5039993 Z"
                                id="Path"
                                fill="#FFFFFF"
                              ></path>
                            </g>
                          </g>
                        </g>
                      </svg>
                    </div>
                    Miscellaneous
                  </div>
                  <div class="tlp-incidentDescription">
                    <div
                      class="tlp-incidentDescriptionText"
                      title="COVID-19 Shared Streets. Traffic regulations changed from Olympic St to Urbana Dr / Georgia Ave."
                    >
                      COVID-19 Shared Streets. Traffic regulations changed from Olympic St to Urbana Dr / Georgia Ave.
                    </div>
                  </div>
                  <div class="tlp-incidentTime" style="">
                    <span>Start time:</span>
                    <span>6/9/2021 4:11 AM</span>
                  </div>
                  <div class="tlp-incidentTime" style="">
                    <span>Est. end time:</span>
                    <span>Unknown</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="infobox-stalk" style="display: none; top: 169.8px; left: 135px;"></div>
        </div>
      </div>
      `;
      console.log(info, html);
      return html;
    }
  }
};
</script>

<style lang="scss" scoped>
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

.Infobox {
  position: relative;
  border: 1px solid #888;
  border-radius: 8px;
  left: 0;
  top: 0;
  pointer-events: auto;
  background-color: #fff;

  .infobox-body {
    padding-bottom: 4px;
  }

  .infobox-title {
    font-family: Verdana;
    font-size: 10pt;
    line-height: 1.1;
    margin-right: 20px;
    font-weight: bold;
    padding: 10px;
    color: #333;

    a {
      text-decoration: none;
      &:hover {
        text-decoration: underline;
      }
    }
  }

  .infobox-info {
    font-family: verdana;
    font-size: 10pt;
    color: #888;
    padding: 10px;
    overflow: auto;
  }

  .infobox-actions {
    border-top: 1px solid #888;
    margin: 0 0 -4px;
    padding: 4px 0;
    font-size: 0.9em;
    clear: both;
    padding: 4px 8px 8px;
    color: #676767;
    height: 15px;
    overflow: auto;
  }

  .infobox-actions-list {
    line-height: 1.3;
    margin: 0;
    overflow: hidden;
    width: 100%;
    list-style: none outside none;
    margin: 0 0 -8px;
    padding: 0;

    li {
      float: left;
      margin: 0 0 4px -4px;

      &:last-child {
        a {
          border: 0 none;
        }
      }

      a {
        border-right: 1px solid #888;
        display: block;
        padding: 0 8px;
        text-decoration: none;
        &:hover {
          text-decoration: underline;
        }
      }

      a.icon {
        width: 13px;
        height: 18px;
        margin: 2px 8px 2px 8px;
        padding: 0;
        position: relative;
        img {
          border: none;
        }
      }
    }
  }

  .infobox-stalk {
    width: 14px;
    height: 16px;
    background: no-repeat;
    position: absolute;
    bottom: 1px;
  }
}

.infobox-body {
  pointer-events: auto;
}

.InfoboxCustom {
  pointer-events: auto;
  .infobox-stalk {
    width: 14px;
    height: 16px;
    background: no-repeat;
    position: absolute;
    bottom: 1px;
  }
}

.infobox-close {
  color: #777;
  height: 14px;
  outline: medium none;
  position: absolute;
  right: 5px;
  text-align: center;
  text-decoration: none;
  top: 5px;
  width: 14px;
  pointer-events: auto;
}

.Infobox.no-close {
  .infobox-title {
    margin-right: auto;
  }
}

.Infobox.no-title {
  .infobox-title {
    display: none;
  }

  .infobox-info {
    margin-right: 15px;
  }
}
.Infobox.no-close.no-title {
  .infobox-info {
    margin-right: auto;
  }
}

.infobox-actions-list {
  li {
    a.icon {
      &:hover {
        .iconUnderline {
          border-bottom: 1px solid #36b;
        }
      }
    }
  }
}
</style>

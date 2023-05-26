export function getSegCW(data) {
  return {
    content:
      '<div id="content">' +
      '<u><h2 id="firstHeading" class="firstHeading">' +
      data.info.name +
      "</h2></u>" +
      '<div id="bodyContent">' +
      "<p />" +
      "<ul>" +
      "<li><b>Description:</b> " +
      data.info.description +
      "</li>" +
      "<li><b>Last updated:</b> " +
      (data.travelTime && data.travelTime.calculationTimestamp ? data.travelTime.calculationTimestamp.text : 'N/A') +
      "</li>" +
      "<li><b>Total Distance:</b> " +
      data.info.distance + " mi" +
      "</li>" +
      "<li><b>Severity Level:</b> " +
      (data.travelTime ? data.travelTime.level : -3) +
      "<li><b>Segment ID:</b> " +
      data.info.linkId +
      "</li>" +
      "</ul>" +
      "</div>" +
      '<div class="pt-2 text-center"><a><b>' +
      `<button onclick="getAdditionalInfo('${data.info.name}')">Additional Info</button>` +
      '</b></a></div>' +
      "</div>",
    position: null,
  };
}

export function getSenCW(data) {
  return {
    content:
      '<div id="content">' +
      '<u><h2 id="firstHeading" class="firstHeading">' +
      data.node +
      "</h2></u>" +
      '<div id="bodyContent">' +
      "<p />" +
      "<ul>" +
      "<li><b>Latitude:</b> " +
      data.lat +
      "</li>" +
      "<li><b>Longitude:</b> " +
      data.lng +
      "</li>" +
      "</ul>" +
      "</div>" +
      "</div>",
    position: null,
  }
}

export function getDevCW(data) {
  return {
    content:
      '<div id="content">' +
      '<u><h2 id="firstHeading" class="firstHeading">' +
      data.title +
      "</h2></u>" +
      '<div id="bodyContent">' +
      "<p />" +
      "<ul>" +
      "<li><b>Device ID:</b> " +
      data.deviceId +
      "</li>" +
      "<li><b>Station Count:</b> " +
      data.stationCount +
      "</li>" +
      "<li><b>Type:</b> " +
      data.type +
      "</li>" +
      "<li><b>UID:</b> " +
      data.uid +
      "</li>" +
      "<li><b>Level:</b> " +
      data.status.level +
      "</li>" +
      "</ul>" +
      "</div>" +
      "</div>",
    position: null,
  }
}

export function getWazeCW(data) {
  return {
    content:
      '<div id="content">' +
      '<u><h2 id="firstHeading" class="firstHeading">' +
      (data.description != null ? data.description : data.alertType.name) +
      "</h2></u>" +
      '<div id="bodyContent">' +
      "<p />" +
      "<ul>" +
      "<li><b>Time of Alert:</b> " +
      data.alertTime +
      "</li>" +
      "<li><b>Road Type:</b> " +
      data.roadType.name +
      "</li>" +
      "<li><b>Latitude:</b> " +
      data.lat +
      "</li>" +
      "<li><b>Longitude:</b> " +
      data.lng +
      "</li>" +
      "</ul>" +
      "</div>" +
      "</div>",
    position: null,
  }
}
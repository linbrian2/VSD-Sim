/* global google */
// 1 degree is equals to 111,111 meters
const MAGIC_NUMBER = 111111;

export default {
  rectangle: function(center, sizex, sizey, angle, projection) {
    let ratioXY = this.getRatioXY(center);
    const DELTAX = (0.3048 * sizex * 6) / MAGIC_NUMBER;
    const DELTAY = (0.3048 * sizey * ratioXY * 6) / MAGIC_NUMBER;

    // Polygon
    let lat0 = center.lat + DELTAY;
    let lng0 = center.lng - DELTAX;
    let lat1 = center.lat - DELTAY;
    let lng1 = center.lng - DELTAX;
    let lat2 = center.lat - DELTAY;
    let lng2 = center.lng + DELTAX;
    let lat3 = center.lat + DELTAY;
    let lng3 = center.lng + DELTAX;

    let polygon = [
      { lat: lat0, lng: lng0 },
      { lat: lat1, lng: lng1 },
      { lat: lat2, lng: lng2 },
      { lat: lat3, lng: lng3 }
    ];

    return this.rotatePolygon(projection, polygon, center, angle);
  },

  arrow: function(center, size, angle, projection) {
    let ratioXY = this.getRatioXY(center);
    const RATIOX = (0.3048 * size) / MAGIC_NUMBER;
    const RATIOY = RATIOX * ratioXY;

    let xc = center.lng;
    let yc = center.lat;
    let x0 = xc;
    let y0 = yc + RATIOY * 4.75;
    let x1 = xc - RATIOX * 1.667;
    let y1 = yc - RATIOY * 0.25;
    let x2 = xc - RATIOX * 0.5;
    let y2 = yc + RATIOY * 0.75;
    let x3 = x2;
    let y3 = yc - RATIOY * 4.75;
    let x4 = xc + RATIOX * 0.5;
    let y4 = y3;
    let x5 = x4;
    let y5 = y2;
    let x6 = xc + RATIOX * 1.67;
    let y6 = y1;

    let polygon = [
      { lat: y0, lng: x0 },
      { lat: y1, lng: x1 },
      { lat: y2, lng: x2 },
      { lat: y3, lng: x3 },
      { lat: y4, lng: x4 },
      { lat: y5, lng: x5 },
      { lat: y6, lng: x6 }
    ];

    return this.rotatePolygon(projection, polygon, center, angle);
  },

  leftArrow: function(center, size, angle, projection) {
    let ratioXY = this.getRatioXY(center);
    const RATIOX = (0.3048 * size) / MAGIC_NUMBER;
    const RATIOY = RATIOX * ratioXY;

    let r0 = 2.29691667;
    let r1 = 3.28125;

    let xc = center.lng;
    let yc = center.lat;

    let x0 = xc - RATIOX * 3.0;
    let y0 = yc + RATIOY * 2.5;
    let x6 = xc + RATIOX * 3.0;
    let y6 = yc - RATIOY * 2.5;
    let x5 = x6 - RATIOX * 1.0;
    let y5 = y6;
    let x4 = x5;
    let y4 = y5 + RATIOY * 2.0;
    let x3 = x4 - RATIOX * r0;
    let y3 = y4 + RATIOY * r0;
    let x2 = x3 - RATIOX * 0.6667;
    let y2 = y3;
    let x1 = x4 - RATIOX * 2.63;
    let y1 = y4;
    let x7 = x6;
    let y7 = y6 + RATIOY * 2.66667;
    let x8 = x7 - RATIOX * r1;
    let y8 = y7 + RATIOY * r1;
    let x9 = x2;
    let y9 = y8;
    let x10 = x6 - RATIOX * r1;
    let y10 = y6 + RATIOY * 8.0;

    let polygon = [
      { lat: y0, lng: x0 },
      { lat: y1, lng: x1 },
      { lat: y2, lng: x2 },
      { lat: y3, lng: x3 },
      { lat: y4, lng: x4 },
      { lat: y5, lng: x5 },
      { lat: y6, lng: x6 },
      { lat: y7, lng: x7 }
    ];

    let polygon1 = [
      { lat: y8, lng: x8 },
      { lat: y9, lng: x9 },
      { lat: y10, lng: x10 }
    ];

    // Create circle points
    let center0 = { lat: y4, lng: x4 - RATIOX * r0 };
    let center1 = { lat: y7, lng: x7 - RATIOX * r1 };
    let points0 = this.circle(center0, r0, size, ratioXY, 0, 90, 10).reverse();
    let points1 = this.circle(center1, r1, size, ratioXY, 0, 90, 10);

    // Insert circle points
    polygon.splice(4, 0, ...points0);
    polygon.push(...points1);

    // Append the rest
    polygon.push(...polygon1);

    return this.rotatePolygon(projection, polygon, center, angle);
  },

  rightArrow: function(center, size, angle, projection) {
    // Create a left arrow
    let polygon = this.leftArrow(center, size, 0);

    // Mirror on the center line
    let mirroredPolygon = polygon.map(pt => {
      let newLng = 2 * center.lng - pt.lng;
      return { lat: pt.lat, lng: newLng };
    });

    // Perform rotation
    return this.rotatePolygon(projection, mirroredPolygon, center, angle);
  },

  rotatePolygon: function(projection, polygon, center, angle) {
    if (Math.abs(angle) < 1e-5) {
      return polygon;
    }

    let origin = projection.fromLatLngToPoint(new google.maps.LatLng(center));

    return polygon.map(point => {
      let pixel = projection.fromLatLngToPoint(new google.maps.LatLng(point));
      let rotatedPixel = this.rotatePoint(pixel, origin, angle);
      let rotatedLatLng = projection.fromPointToLatLng(rotatedPixel);
      return { lat: rotatedLatLng.lat(), lng: rotatedLatLng.lng() };
    });
  },

  rotatePoint: function(point, origin, angle) {
    const angleRad = this.rad(angle);
    const x = Math.cos(angleRad) * (point.x - origin.x) - Math.sin(angleRad) * (point.y - origin.y) + origin.x;
    const y = Math.sin(angleRad) * (point.x - origin.x) + Math.cos(angleRad) * (point.y - origin.y) + origin.y;
    return { x, y };
  },

  circle: function(center, radius, size, ratioXY, startAngle, endAngle, num) {
    const RATIOX = (0.3048 * size) / MAGIC_NUMBER;
    const RATIOY = RATIOX * ratioXY;
    let dAngle = (endAngle - startAngle) / (num + 1);

    let points = [];
    for (let i = 1; i < num; i++) {
      let theta = this.rad(startAngle + i * dAngle);
      let dx = radius * Math.cos(theta);
      let dy = radius * Math.sin(theta);
      let lat = center.lat + RATIOY * dy;
      let lng = center.lng + RATIOX * dx;
      points.push({ lat: lat, lng: lng });
    }

    return points;
  },

  getRatioXY: function(center) {
    let xScale = this.distance(center.lat, center.lng, center.lat, center.lng + 0.1);
    let yScale = this.distance(center.lat, center.lng, center.lat + 0.1, center.lng);
    let ratioXY = xScale / yScale;
    return ratioXY;
  },

  rotate: function(polygon, center, angle) {
    if (Math.abs(angle) < 1e-5) {
      return polygon;
    }

    let cx = center.lng;
    let cy = center.lat;

    let cos = Math.cos(this.rad(angle));
    let sin = Math.sin(this.rad(angle));

    return polygon.map(pt => {
      let dx = pt.lng - cx;
      let dy = pt.lat - cy;

      let x = pt.lng + dx * cos - dy * sin;
      let y = pt.lat + dx * sin + dy * cos;

      return { lat: y, lng: x };
    });
  },

  distance: function(lat1, lon1, lat2, lon2) {
    // Earth mean radius in meter
    let R = 6378137;
    let dLat = this.rad(lat2 - lat1);
    let dLong = this.rad(lon2 - lon1);

    let a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(this.rad(lat1)) * Math.cos(this.rad(lat2)) * Math.sin(dLong / 2) * Math.sin(dLong / 2);

    let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    let d = R * c;

    // returns the distance in meter
    return d;
  },

  rad: function(x) {
    return (x * Math.PI) / 180;
  }
};

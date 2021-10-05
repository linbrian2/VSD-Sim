// MAD outlier removal algorithm implementation
// https://www.influxdata.com/blog/anomaly-detection-with-median-absolute-deviation/
export default class OutlierRemoval {
  constructor(threshold) {
    this.threshold = threshold;
  }

  remove(points) {
    if (points.length < 5) {
      return;
    }

    // Compute the centroid of the data
    const center = this.centroid(points);

    // Convert data to distance list
    const distances = points.map((point, index) => ({ id: index, distance: this.distance(point, center) }));

    // Find the median of the distance list
    const median = this.medianValue(distances.map(item => item.distance));

    // Get the absolute difference between the median from each series
    const differences = distances.map(item => ({ id: item.id, value: Math.abs(item.distance - median) }));

    // Calculate the median absolute deviation (MAD)
    const MAD = 1.4826 * this.medianValue(differences.map(item => item.value));

    // Normalization
    const finals = differences.map(item => ({ id: item.id, value: item.value / MAD }));

    // Perform Median Absolute Deviation(MAD) outlier removal
    const outliers = finals.filter(item => item.value > this.threshold).map(item => item.id);
    if (outliers.length === 0) {
      return points;
    }

    // Convert array to Set for fast lookup
    const set = new Set(outliers);

    // Actually remove outlier points from list
    return points.filter((point, index) => !set.has(index));
  }

  centroid(points) {
    const count = points.length;
    const latTotal = points.reduce((acc, point) => acc + point.lat, 0);
    const lngTotal = points.reduce((acc, point) => acc + point.lng, 0);
    return { lat: latTotal / count, lng: lngTotal / count };
  }

  medianValue(data) {
    // Sort the data
    data.sort();

    // The middle index
    const middle = Math.floor(data.length / 2);

    return data.length % 2 ? data[middle] : (data[middle - 1] + data[middle]) / 2;
  }

  distance(pt0, pt1) {
    const EarthRadiusMeters = 6378137.0; // meters
    const lat1 = pt0.lat;
    const lon1 = pt0.lng;
    const lat2 = pt1.lat;
    const lon2 = pt1.lng;
    const dLat = this.radians(lat2 - lat1);
    const dLon = this.radians(lon2 - lon1);
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(this.radians(lat1)) * Math.cos(this.radians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const d = EarthRadiusMeters * c;
    return d;
  }

  radians(deg) {
    return (deg * Math.PI) / 180;
  }
}

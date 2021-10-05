export default function useMapGeometry(path) {
  const radians = deg => {
    return (deg * Math.PI) / 180;
  };

  const distance = (pt0, pt1) => {
    const EarthRadiusMeters = 6378137.0; // meters
    const lat1 = pt0.lat;
    const lon1 = pt0.lng;
    const lat2 = pt1.lat;
    const lon2 = pt1.lng;
    const dLat = radians(lat2 - lat1);
    const dLon = radians(lon2 - lon1);
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(radians(lat1)) * Math.cos(radians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const d = EarthRadiusMeters * c;
    return d;
  };

  const totalDistance = () => {
    let dist = 0;
    for (let i = 1; i < path.length; i++) {
      dist += distance(path[i - 1], path[i]);
    }
    return dist;
  };

  // === A method which returns a GLatLng of a point a given distance along the path ===
  // === Returns null if the path is shorter than the specified distance ===
  const getPointAtDistance = meters => {
    // some awkward special cases
    if (meters == 0) return path[0];
    if (meters < 0) return null;
    if (path.length < 2) return null;

    let dist = 0;
    let lastDist = 0;
    let curIdx = 0;
    for (let i = 1; i < path.length && dist < meters; i++) {
      lastDist = dist;
      dist += distance(path[i - 1], path[i]);
      curIdx = i;
    }

    if (dist < meters) {
      return null;
    }

    const p1 = path[curIdx - 1];
    const p2 = path[curIdx];
    const m = (meters - lastDist) / (dist - lastDist);

    // Do interpolation
    const lat = p1.lat + (p2.lat - p1.lat) * m;
    const lng = p1.lng + (p2.lng - p1.lng) * m;

    const position = curIdx - 1 + m;
    const point = { lat, lng };

    return { position, point };
  };

  const getMetersAtPercentage = percentage => {
    const total = totalDistance();
    const meters = (total * percentage) / 100;
    return meters;
  };

  return { getPointAtDistance, getMetersAtPercentage, totalDistance };
}

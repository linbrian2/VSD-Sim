package com.iai.traffic;

public class LatLonRect {
	/**
	 * upper right corner
	 */
	private LatLonPoint upperRight;

	/**
	 * lower left corner
	 */
	private LatLonPoint lowerLeft;

	/**
	 * flag for dateline cross
	 */
	private boolean crossDateline = false;

	/**
	 * All longitudes are included
	 */
	private boolean allLongitude = false;

	/**
	 * width and initial longitude
	 */
	private double width, lon0;

	/**
	 * Construct a lat/lon bounding box from a point, and a delta lat, lon. This
	 * disambiguates which way the box wraps around the globe.
	 *
	 * @param p1
	 *            one corner of the box
	 * @param deltaLat
	 *            delta lat from p1. (may be positive or negetive)
	 * @param deltaLon
	 *            delta lon from p1. (may be positive or negetive)
	 */
	public LatLonRect(LatLonPoint p1, double deltaLat, double deltaLon) {
		init(p1, deltaLat, deltaLon);
	}

	private void init(LatLonPoint p1, double deltaLat, double deltaLon) {
		double lonmin, lonmax;
		double latmin = Math.min(p1.getLatitude(), p1.getLatitude() + deltaLat);
		double latmax = Math.max(p1.getLatitude(), p1.getLatitude() + deltaLat);

		double lonpt = p1.getLongitude();
		if (deltaLon > 0) {
			lonmin = lonpt;
			lonmax = lonpt + deltaLon;
			crossDateline = (lonmax > 180.0);
		} else {
			lonmax = lonpt;
			lonmin = lonpt + deltaLon;
			crossDateline = (lonmin < -180.0);
		}

		this.lowerLeft  = new LatLonPoint(latmin, lonmin);
		this.upperRight = new LatLonPoint(latmax, lonmax);

		// these are an alternative way to view the longitude range
		this.width = Math.abs(deltaLon);
		this.lon0 = LatLonPoint.lonNormal(p1.getLongitude() + deltaLon / 2);
		this.allLongitude = (this.width >= 360.0);
	}

	/**
	 * Create a LatLonRect that covers the whole world.
	 */
	public LatLonRect() {
		this(new LatLonPoint(-90, -180), 180, 360);
	}
	
	/**
	 * Create a LatLonRect that covers the whole world.
	 */
	public LatLonRect(double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon) {
		this.lowerLeft  = new LatLonPoint(lowerLeftLat,  lowerLeftLon);
		this.upperRight = new LatLonPoint(upperRightLat, upperRightLon);
	}

	/**
	 * Get the upper right corner of the bounding box.
	 *
	 * @return upper right corner of the bounding box
	 */
	public LatLonPoint getUpperRightPoint() {
		return upperRight;
	}

	/**
	 * Get the lower left corner of the bounding box.
	 *
	 * @return lower left corner of the bounding box
	 */
	public LatLonPoint getLowerLeftPoint() {
		return lowerLeft;
	}

	/**
	 * Get the upper left corner of the bounding box.
	 *
	 * @return upper left corner of the bounding box
	 */
	public LatLonPoint getUpperLeftPoint() {
		return new LatLonPoint(upperRight.getLatitude(),
				lowerLeft.getLongitude());
	}

	/**
	 * Get the lower left corner of the bounding box.
	 *
	 * @return lower left corner of the bounding box
	 */
	public LatLonPoint getLowerRightPoint() {
		return new LatLonPoint(lowerLeft.getLatitude(),
				upperRight.getLongitude());
	}

	/**
	 * return width of bounding box, always between 0 and 360 degrees.
	 *
	 * @return width of bounding box in degrees longitude
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * return height of bounding box, always between 0 and 180 degrees.
	 *
	 * @return height of bounding box in degrees latitude
	 */
	public double getHeight() {
		return getLatMax() - getLatMin();
	}

	/**
	 * return center Longitude, always in the range +/-180
	 *
	 * @return center Longitude
	 */
	public double getCenterLon() {
		return lon0;
	}

	/**
	 * Get minimum longitude, aka "west" edge
	 *
	 * @return minimum longitude
	 */
	public double getLonMin() {
		return lowerLeft.getLongitude();
	}

	/**
	 * Get maximum longitude, aka "east" edge
	 *
	 * @return maximum longitude
	 */
	public double getLonMax() {
		return upperRight.getLongitude();
	}

	/**
	 * Get minimum latitude, aka "south" edge
	 *
	 * @return minimum latitude
	 */
	public double getLatMin() {
		return lowerLeft.getLatitude();
	}

	/**
	 * Get maximum latitude, aka "north" edge
	 *
	 * @return maximum latitude
	 */
	public double getLatMax() {
		return upperRight.getLatitude();
	}

	/**
	 * Determine if a specified LatLonPoint is contained in this bounding box.
	 *
	 * @param p
	 *            the specified point to be tested
	 * @return true if point is contained in this bounding box
	 */
	public boolean contains(LatLonPoint p) {
		return contains(p.getLatitude(), p.getLongitude());
	}

	/**
	 * Determine if the given lat/lon point is contains inside this rectangle.
	 *
	 * @param lat    lat of point
	 * @param lon    lon of point
	 * @return true if the given lat/lon point is contained inside this rectangle
	 */
	public boolean contains(double lat, double lon) {
		// check lat first
		if ((lat + eps < lowerLeft.getLatitude())
				|| (lat - eps > upperRight.getLatitude())) {
			return false;
		}

		if (allLongitude)
			return true;

		if (crossDateline) {
			// bounding box crosses the +/- 180 seam
			return ((lon >= lowerLeft.getLongitude()) || (lon <= upperRight.getLongitude()));
		} else {
			// check "normal" lon case
			return ((lon >= lowerLeft.getLongitude()) && (lon <= upperRight.getLongitude()));
		}
	}

	private double eps = 1.0e-9;

	/**
	 * Determine if this bounding box is contained in another LatLonRect.
	 *
	 * @param b
	 *            the other box to see if it contains this one
	 * @return true if b contained in this bounding box
	 */
	public boolean containedIn(LatLonRect b) {
		return (b.getWidth() >= width) && b.contains(upperRight)
				&& b.contains(lowerLeft);
	}

	/*
	 * Determine if a specified LatLonRect intersects this
	 * 
	 * @param b the specified box to be tested
	 * 
	 * @param p
	 * 
	 * @return true if b intersects this bounding box
	 * 
	 * public boolean intersects(LatLonRect b) { if
	 * (b.getUpperRightPoint().getLatitude() < lowerLeft.getLatitude()) return
	 * false; if (b.getLowerLeftPoint().getLatitude() >
	 * upperRight.getLatitude()) return false;
	 * 
	 * double blon0 = b.getCenterLon(); double normal = (blon0 + lon0) / 2; if
	 * (Math.abs(blon0-lon0) > 180.0) normal += 180; blon0 =
	 * LatLonPoint.lonNormal(blon0, normal); double mylon0 =
	 * LatLonPoint.lonNormal(lon0, normal);
	 * 
	 * if (blon0 + b.getWidth() < mylon0 - width) return false; if (blon0 -
	 * b.getWidth() > mylon0 + width) return false;
	 * 
	 * return true; }
	 */

	/**
	 * Extend the bounding box to contain this point
	 *
	 * @param p
	 *            point to include
	 */
	public void extend(LatLonPoint p) {
		if (contains(p))
			return;

		double lat = p.getLatitude();
		double lon = p.getLongitude();

		// lat is easy to deal with
		if (lat > upperRight.getLatitude()) {
			upperRight.setLatitude(lat);
		}
		if (lat < lowerLeft.getLatitude()) {
			lowerLeft.setLatitude(lat);
		}

		// lon is uglier
		if (allLongitude) {
			// do nothing
		} else if (crossDateline) {
			// bounding box crosses the +/- 180 seam
			double d1 = lon - upperRight.getLongitude();
			double d2 = lowerLeft.getLongitude() - lon;
			if ((d1 > 0.0) && (d2 > 0.0)) { // needed ?
				if (d1 > d2) {
					lowerLeft.setLongitude(lon);
				} else {
					upperRight.setLongitude(lon);
				}
			}
		} else {

			// normal case
			if (lon > upperRight.getLongitude()) {
				if (lon - upperRight.getLongitude() > lowerLeft.getLongitude()
						- lon + 360) {
					crossDateline = true;
					lowerLeft.setLongitude(lon);
				} else {
					upperRight.setLongitude(lon);
				}
			} else if (lon < lowerLeft.getLongitude()) {
				if (lowerLeft.getLongitude() - lon > lon + 360.0
						- upperRight.getLongitude()) {
					crossDateline = true;
					upperRight.setLongitude(lon);
				} else {
					lowerLeft.setLongitude(lon);
				}
			}
		}

		// recalc delta, center
		width = upperRight.getLongitude() - lowerLeft.getLongitude();
		lon0 = (upperRight.getLongitude() + lowerLeft.getLongitude()) / 2;
		if (crossDateline) {
			width += 360;
			lon0 -= 180;
		}

		this.allLongitude = this.allLongitude || (this.width >= 360.0);
	}

	/**
	 * Create the instersection of this LatLon with the given one
	 *
	 * @param clip
	 *            intersect with this
	 * @return intersection, or null if there is no intersection
	 */
	public LatLonRect intersect(LatLonRect clip) {
		double latMin = Math.max(getLatMin(), clip.getLatMin());
		double latMax = Math.min(getLatMax(), clip.getLatMax());
		double deltaLat = latMax - latMin;
		if (deltaLat < 0)
			return null;

		// lon as always is a pain : if not intersection, try +/- 360
		double lon1min = getLonMin();
		double lon1max = getLonMax();
		double lon2min = clip.getLonMin();
		double lon2max = clip.getLonMax();
		if (!intersect(lon1min, lon1max, lon2min, lon2max)) {
			lon2min = clip.getLonMin() + 360;
			lon2max = clip.getLonMax() + 360;
			if (!intersect(lon1min, lon1max, lon2min, lon2max)) {
				lon2min = clip.getLonMin() - 360;
				lon2max = clip.getLonMax() - 360;
			}
		}

		// we did our best to find an intersection
		double lonMin = Math.max(lon1min, lon2min);
		double lonMax = Math.min(lon1max, lon2max);
		double deltaLon = lonMax - lonMin;
		if (deltaLon < 0)
			return null;

		return new LatLonRect(new LatLonPoint(latMin, lonMin), deltaLat,
				deltaLon);
	}

	private boolean intersect(double min1, double max1, double min2, double max2) {
		double min = Math.max(min1, min2);
		double max = Math.min(max1, max2);
		return min < max;
	}

	/**
	 * Return a String representation of this object.
	 * 
	 * <pre>
	 * eg: ll: 90.0S .0E+ ur: 90.0N .0E
	 * </pre>
	 *
	 * @return a String representation of this object.
	 */
	public String toString() {
		return " ll: " + lowerLeft + "+ ur: " + upperRight;
	}
}

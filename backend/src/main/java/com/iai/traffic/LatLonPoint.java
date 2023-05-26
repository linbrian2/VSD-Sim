package com.iai.traffic;

import java.text.DecimalFormat;

public class LatLonPoint {

	/**
	 * East latitude in degrees, always +/- 90
	 */
	private double lat;

	/**
	 * North longitude in degrees, always +/- 180
	 */
	private double lon;

	/**
	 * Default constructor with values 0,0.
	 */
	public LatLonPoint() {
		this(0.0, 0.0);
	}

	/**
	 * Copy Constructor.
	 *
	 * @param pt          point to copy
	 */
	public LatLonPoint(LatLonPoint pt) {
		this(pt.getLatitude(), pt.getLongitude());
	}

	/**
	 * Creates a LatLonPoint from component latitude and longitude values. The
	 * longitude is adjusted to be in the range [-180.,180.].
	 *
	 * @param lat          north latitude in degrees
	 * @param lon          east longitude in degrees
	 */
	public LatLonPoint(double lat, double lon) {
		setLatitude(lat);
		setLongitude(lon);
	}

	/**
	 * Set the longitude, in degrees. It is normalized to +/-180.
	 *
	 * @param lon           east longitude in degrees
	 */
	public void setLongitude(double lon) {
		this.lon = lonNormal(lon);
	}

	/**
	 * Set the latitude, in degrees. Must lie beween +/-90
	 *
	 * @param lat           north latitude in degrees
	 */
	public void setLatitude(double lat) {
		this.lat = latNormal(lat);
	}

	/**
	 * Returns the longitude, in degrees.
	 *
	 * @return the longitude, in degrees
	 */
	public double getLongitude() {
		return lon;
	}

	/**
	 * Returns the latitude, in degrees.
	 *
	 * @return the latitude, in degrees
	 */
	public double getLatitude() {
		return lat;
	}

	/**
	 * Normalize the longitude to lie between +/-180
	 *
	 * @param lon         east latitude in degrees
	 * @return normalized lon
	 */
	static public double lonNormal(double lon) {
		if ((lon < -180.) || (lon > 180.)) {
			return Math.IEEEremainder(lon, 360.0);
		} else {
			return lon;
		}
	}

	/**
	 * Normalize the latitude to lie between +/-90
	 *
	 * @param lat         north latitude in degrees
	 * @return normalized lat
	 */
	static public double latNormal(double lat) {
		if (lat < -90.) {
			return -90.;
		} else if (lat > 90.) {
			return 90.;
		} else {
			return lat;
		}
	}

	/**
	 * Make a nicely formatted representation of a latitude, eg 40.34N or 12.9S.
	 *
	 * @param lat          the latitude.
	 * @param sigDigits    number of significant digits to display.
	 * @return String representation.
	 */
	static public String latToString(double lat, int sigDigits) {
		boolean is_north = (lat >= 0.0);
		if (!is_north) {
			lat = -lat;
		}

		StringBuilder latBuff = new StringBuilder(20);
		latBuff.setLength(0);
		latBuff.append(new DecimalFormat("#.##").format(lat));
		latBuff.append(is_north ? "N" : "S");

		return latBuff.toString();
	}

	/**
	 * Make a nicely formatted representation of a longitude, eg 120.3W or
	 * 99.99E.
	 *
	 * @param lon      the longitude.
	 * @param sigDigits     number of significant digits to display.
	 * @return String representation.
	 */
	static public String lonToString(double lon, int sigDigits) {
		double wlon = lonNormal(lon);
		boolean is_east = (wlon >= 0.0);
		if (!is_east) {
			wlon = -wlon;
		}

		StringBuilder lonBuff = new StringBuilder(20);
		lonBuff.setLength(0);
		lonBuff.append(new DecimalFormat("#.##").format(wlon));
		lonBuff.append(is_east ? "E" : "W");

		return lonBuff.toString();
	}

	/**
	 * Default string representation
	 *
	 * @return string representing this point
	 */
	public String toString() {
		return toString(4);
	}

	/**
	 * String representation in the form, eg 40.23N 105.1W
	 *
	 * @param sigDigits     significant digits
	 * @return String representation
	 */
	public String toString(int sigDigits) {
		StringBuilder sbuff = new StringBuilder(40);
		sbuff.setLength(0);
		sbuff.append(latToString(lat, sigDigits));
		sbuff.append(" ");
		sbuff.append(lonToString(lon, sigDigits));
		return sbuff.toString();
	}
}

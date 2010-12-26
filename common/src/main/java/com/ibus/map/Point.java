package com.ibus.map;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static com.ibus.map.utils.MapUtils.roundLatitude;
import static com.ibus.map.utils.MapUtils.roundLongitude;;

/**
 * Represents a point on a lane
 * 
 * @author Home
 * 
 */
public class Point implements Comparable<Point>{
	
	
	/**
	 * The earth radius
	 */
	private static double RADIO = 6378.16;

	/**
	 * The longitude in degrees
	 */
	protected double longitude;
	/**
	 * The latitude in degrees
	 */
	protected double latitude;

	public Point(){}
	
	public Point(Point p){
		latitude = p.latitude;
		longitude = p.longitude;
	}
	public Point(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	/**
	 * calculates distance between two points in km
	 * @param p
	 * @return
	 */
	public double distnaceFrom(Point p) {
		if (p == this)
			return 0;// the same point

		double dlon = toRadians(p.longitude - longitude);
		double dlat = toRadians(p.latitude - latitude);

		double a = (sin(dlat / 2) * sin(dlat / 2))
				+ cos(toRadians(p.latitude)) * cos(toRadians(latitude))
				* (sin(dlon / 2) * sin(dlon / 2));
		double angle = 2 * atan2(sqrt(a), sqrt(1 - a));
		return angle * RADIO;
	}

	
	/**
	 * rounds the point to +-10m
	 * @return
	 */
	public Point round(){
		Point rnd = new Point(roundLongitude(longitude, (int)(latitude/10)), roundLatitude(latitude));
		return rnd;
	}
	
	@Override
	public int compareTo(Point p) {
		//distance in meters 
		return (int)(this.distnaceFrom(p)*1000);
	}


	@Override
	public String toString() {
		return latitude+"_"+longitude;
	}

	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		return true;
	}
}

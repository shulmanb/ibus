package com.ibus.map.utils;

import java.math.BigDecimal;

import com.ibus.map.Point;

public class MapUtils {
	
	//EQUATOR
	//latitude longitude pairs according to latitude 0,10,20,30,40,50,60,70,80,90
	static long[][] LAT_LONG_SEC_SIZES = new long[][]{
		{110570,111320}, //EQUATOR
		{110610,109640}, //LAT10
		{110700,104650}, //LAT20
		{110850,96490},  //LAT30
		{111030,85390},  //LAT40
		{111230,71700},  //LAT50
		{111410,55800},  //LAT60
		{111560,38190},  //LAT70
		{111660,19390},  //LAT80
		{111690,0}};     //LAT90
	

	/**
	 * rouds the latitude
	 * @param lat
	 * @return
	 */
	public static double roundLatitude(double lat){
		//35.105624
		//always round by the 2rd ~ 1.11 m for each .00001 (11m rounding)
		int negative = 1;
		if(lat < 0){
			negative = -1;
			lat = (-1)*lat;
		}
		BigDecimal toTrim = BigDecimal.valueOf(lat).movePointRight(4);
		toTrim = toTrim.subtract(new BigDecimal(toTrim.toBigInteger())).movePointLeft(4);
		return BigDecimal.valueOf(lat).subtract(toTrim).multiply(BigDecimal.valueOf(negative)).doubleValue();
	}
	
	/**
	 * rounds the longitude
	 * @param lon
	 * @param latIndex
	 * @return
	 */
	public static double roundLongitude(double lon, int latIndex){
		//32.664444
		int negative = 1;
		
		if(latIndex == 9){
			return lon;
		}

		if(lon < 0){
			negative = -1;
			lon = (-1)*lon;
		}
		BigDecimal toTrim;
		if(latIndex > 5 ){
			toTrim = BigDecimal.valueOf(lon).movePointRight(4); //~.5 for each .00001 rounding (5m rounding)
		}else{
			toTrim = BigDecimal.valueOf(lon).movePointRight(4); //~1 for each .00001 rounding (10m rounding)
		}
		toTrim = toTrim.subtract(new BigDecimal(toTrim.toBigInteger())).movePointLeft(4);
		return BigDecimal.valueOf(lon).subtract(toTrim).multiply(BigDecimal.valueOf(negative)).doubleValue();
	}

	public static Point calculateRightCorner(Point center, int latoffset,
			int lonoffset) {
		long[] lat_lon = LAT_LONG_SEC_SIZES[(int) (center.getLatitude()/10)];
		float latdiff = ((float)latoffset)/lat_lon[0];
		float londiff = ((float)lonoffset)/lat_lon[1];
		Point p = new Point();
		p.setLongitude(center.getLongitude()+londiff);
		p.setLatitude(center.getLatitude()-latdiff);
		return p;
	}

	public static Point calculateLeftCorner(Point center, int latoffset,
			int lonoffset) {
		long[] lat_lon = LAT_LONG_SEC_SIZES[(int) (center.getLatitude()/10)];
		double latdiff = ((double)latoffset)/lat_lon[0];
		double londiff = ((double)lonoffset)/lat_lon[1];
		Point p = new Point();
		p.setLongitude(center.getLongitude()-londiff);
		p.setLatitude(center.getLatitude()+latdiff);
		return p;
	}
	
}

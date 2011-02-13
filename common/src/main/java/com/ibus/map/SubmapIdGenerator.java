package com.ibus.map;

import com.ibus.map.utils.MapUtils;

public class SubmapIdGenerator {
	private Point southeast;
	private Point northwest;
	private int length;
	/**
	 * 
	 */
	private int submapsInLon;
	/**
	 * number of submaps between latitudes
	 */
	private int submapsInLat;
	/**
	 * The region id, usually the country sign, IL for example
	 */
	private String regionId;

	/**
	 * @param nortwest upper left corner of the region
	 * @param southeast lower right corner of the region
	 * @param submaplength length of the submap (width and hight)
	 */
	public SubmapIdGenerator(Point nortwest,Point southeast, int submaplength, String regionId){
		this.northwest = nortwest;
		this.southeast = southeast;
		this.length = submaplength;
		this.regionId = regionId;
		//calculate the width
		submapsInLat = MapUtils.calculateLatitudeDistance(nortwest.getLatitude(), 
														  southeast.getLatitude())/submaplength;
		submapsInLon = MapUtils.calculateLongitudeDistance(nortwest.getLongitude(), 
														   southeast.getLongitude(), 
														   (nortwest.getLatitude()+southeast.getLatitude())/2)/submaplength;
	}
	
	public String getSubmapIdForPoint(Point p){
		//TODO: check if point in the region
		
		
		//the region is divided into squares, the squares are numbered starting from 1: 
		//from west to east, from north to south
		//we should find the square where the point fits
		
		int latOffsetInSubmaps = MapUtils.calculateLatitudeDistance(northwest.getLatitude(), 
				  p.getLatitude())/length;
		
		int lonOffsetInSubmaps = MapUtils.calculateLongitudeDistance(northwest.getLongitude(), 
				   p.getLongitude(), 
				   (p.getLatitude()))/length;
		
		//calculate number of squares in the latitude diff and subtract the not needed squares from the last horizontal series  
		int latIndx = (latOffsetInSubmaps < submapsInLat)?latOffsetInSubmaps+1:latOffsetInSubmaps;
		int lonIndx = (lonOffsetInSubmaps < submapsInLon)?lonOffsetInSubmaps+1:lonOffsetInSubmaps;
		int id = (latIndx)*submapsInLat - (submapsInLon - lonIndx);
		return regionId+"_"+id;
	}
}

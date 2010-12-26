package com.ibus.navigation.map;

import com.ibus.map.AreaDetails;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopDetails;

public interface IMapQuery {
	
	public StopDetails[] getStopsInArea(String submap,Point left, Point right);
	public StopDetails[] getStopsInArea(String submap,Point center, int latoffset, int lonoffset);
	public Point[] getLineDetails(String lineId, int level);
	public Stop[] getLineStations(String lineId);
	public String[] getLinesForStation(String stationid);
	public StopDetails getStation(String stationId);
	public Point[] getLineDetailsInArea(String lineId,Point left, Point right,int level);
	public Point[] getLineDetailsInArea(String lineId,Point center, int latoffset, int lonoffset, int level);
	public Stop[] getLineStationsInArea(String lineId,Point left, Point right);
	public Stop[] getLineStationsInArea(String lineId,Point center, int latoffset, int lonoffset);
	public AreaDetails getAreaDetails(Point left, Point right);
	public AreaDetails getAreaDetails(Point center, int latoffset, int lonoffset);
	
}

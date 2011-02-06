package com.ibus.navigation.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.inject.Inject;
import com.ibus.map.AreaDetails;
import com.ibus.map.Line;
import com.ibus.map.LineDetails;
import com.ibus.map.Lines;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopDetails;
import com.ibus.map.utils.MapUtils;
import com.ibus.navigation.map.db.IMapQueryDB;
import com.ibus.navigation.map.db.SimpleDBMapQuery;

public class MapQuery implements IMapQuery {

	private IMapQueryDB db;
	public MapQuery(String awskey, String awssecret){
		this.db = new SimpleDBMapQuery(awskey, awssecret);
	}
	
	@Inject
	public MapQuery(){}
	
	@Inject
	public void setDb(IMapQueryDB db) {
		this.db = db;
	}

	@Override
	public StopDetails[] getStopsInArea(String submap,Point left, Point right) {
		StopDetails stops[] = db.getStationsInArea(submap,left,right);
		return stops;
	}

	@Override
	public StopDetails[] getStopsInArea(String submap,Point center, int latoffset, int lonoffset) {
		Point left = MapUtils.calculateLeftCorner(center, latoffset, lonoffset);
		Point right = MapUtils.calculateRightCorner(center, latoffset, lonoffset);
		StopDetails[] stops = db.getStationsInArea(submap,left,right);
		return stops;
	}

	@Override
	public Point[] getLineDetails(String lineId, int level) {
		return db.getLinePoints(lineId);
	}

	@Override
	public Stop[] getLineStations(String lineId) {
		return db.getStationsForLine(lineId);
	}

	@Override
	public String[] getLinesForStation(String stationid) {
		return db.getStopDetails(stationid).getLines();
	}

	@Override
	public StopDetails getStation(String stationId) {
		return db.getStopDetails(stationId);
	}

	@Override
	public Point[] getLineDetailsInArea(String lineId, Point left, Point right,
			int level) {
		Point[] res =  db.getLinePoints(lineId);
		ArrayList<Point> lst = new ArrayList<Point>();
		for(Point p:res){
			if(p.getLatitude() <= left.getLatitude()
				&& p.getLatitude() >= right.getLatitude() 
				&& p.getLongitude() >= left.getLongitude()
				&& p.getLongitude() <= right.getLongitude()){
					lst.add(p);
				}
		}
		return lst.toArray(new Point[0]);
	}

	@Override
	public Point[] getLineDetailsInArea(String lineId, Point center,
			int latoffset, int lonoffset, int level) {
		return db.getLinePoints(lineId);
	}

	@Override
	public Stop[] getLineStationsInArea(String lineId, Point left,
			Point right) {
		return db.getStationsForLine(lineId);
	}

	@Override
	public Stop[] getLineStationsInArea(String lineId, Point center,
			int latoffset, int lonoffset) {
		return db.getStationsForLine(lineId);
	}
	
	@Override
	public AreaDetails getAreaDetails(Point left, Point right) {
		//TODO: currently the search is done for the whole stations table
		//add later on the search for the area first and search for stations within the area only
		AreaDetails ret = new AreaDetails();
		StopDetails[] stops = db.getStationsInArea(left,right);
		ret.setStops(stops);
		Set<String> linesIds = new TreeSet<String>();
		Map<String, String> lineIdToName = new HashMap<String, String>(); 
		for(StopDetails sd:stops){
			linesIds.addAll(Arrays.asList(sd.getLines()));
			int i = 0;
			for(String id:sd.getLines()){
				lineIdToName.put(id, sd.getLinesNames()[i]);
				i++;
			}
		}
		List<LineDetails> ldList = new LinkedList<LineDetails>();
		for(String id:linesIds){
			LineDetails ld = new LineDetails();
			ld.setId(id);
			ld.setName(lineIdToName.get(id));
			ld.setPoints(getLineDetailsInArea(id, left, right, 10));
			ldList.add(ld);
		}
		ret.setLines(ldList.toArray(new LineDetails[0]));
		return ret;
	}

	@Override
	public AreaDetails getAreaDetails(Point center,	int latoffset, int lonoffset) {
		Point left = MapUtils.calculateLeftCorner(center, latoffset, lonoffset);
		Point right = MapUtils.calculateRightCorner(center, latoffset, lonoffset);
		return getAreaDetails(left, right);
	}
	
	@Override
	public void deleteLine(String lineId){
		db.deleteLineById(lineId);
	}
	
	@Override
	public Lines getLinesInSubmap(String submap){
		Lines ret = new Lines();
		ret.setLines(db.getLinesInSubmap(submap));
		return null;
	}
}
